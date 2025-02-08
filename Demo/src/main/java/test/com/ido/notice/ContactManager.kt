package test.com.ido.notice

import android.content.ContentResolver
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import android.util.Log
import com.ido.ble.BLEManager
import com.ido.ble.LocalDataManager
import com.ido.ble.callback.AppSendAllPhoneContactsCallBack
import com.ido.ble.callback.UnbindCallBack
import com.ido.ble.file.transfer.FileTransferConfig
import com.ido.ble.file.transfer.IFileTransferListener
import com.ido.ble.protocol.model.AllPhoneContacts
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import test.com.ido.APP
import test.com.ido.CallBack.BaseConnCallback
import test.com.ido.CallBack.BaseGetDeviceParaCallBack
import test.com.ido.utils.PermissionUtil
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author tianwei
 * @date 2022/3/8
 * @time 16:25
 * 用途: 联系人管理类
 */
class ContactManager : BaseConnCallback(), IFileTransferListener {
    private var mContentResolver: ContentResolver? = null
    private var mObserver: Observer? = null
    private var mRegistered = false
    var callback: ICallback? = null

    interface ICallback {
        fun onStart()

        fun onProgress(var1: Int)

        fun onSuccess(count:Int)

        fun onFailed(var1: String?)
    }

    companion object {
        const val TAG = "ContactManager"

        @JvmStatic
        private val mInstance = ContactManager()

        @JvmStatic
        fun getInstance() = mInstance
    }

    private val mContactList = mutableListOf<ContactBean>()

    /**
     *是否正在同步联系人
     */
    private var mSyncing = AtomicBoolean(false)
    private var mSyncConTime = 0L

    private val unBindCallback = object : UnbindCallBack.ICallBack {
        override fun onSuccess() {
            //unbind success
            stopSync()
        }

        override fun onFailed() {
            //unbind failed
        }

    }

    fun init() {
        registerListener()
        BLEManager.registerConnectCallBack(this)
        BLEManager.registerUnbindCallBack(unBindCallback)
    }

    private fun registerListener() {
        if (!mRegistered && PermissionUtil.checkSelfPermission(
                APP.getAppContext(),
                *PermissionUtil.getOnlyContactPermission()
            )
        ) {
            mObserver = Observer(Handler(Looper.getMainLooper()))
            mContentResolver = APP.getAppContext().contentResolver
            val uri = ContactsContract.Contacts.CONTENT_URI
            mContentResolver?.registerContentObserver(uri, true, mObserver!!)
            mRegistered = true
        } else {
            //未授权，不注册监听
        }
    }

    /**
     *开始同步联系人到固件
     */
    fun startSync() {
        if (((System.currentTimeMillis() - mSyncConTime) < 60000) && mSyncing.get() && mSyncConTime != 0L) {
            Log.d(TAG, "上次的同步联系人操作还在处理中，请稍后重试。")
            return
        }
        mSyncConTime = System.currentTimeMillis()
        Log.d(TAG, "开始同步联系人到固件")
        mSyncing.set(true)
        GlobalScope.launch {
            queryContactsStatus()
        }
    }

    /**
     *停止同步联系人
     */
    fun stopSync() {
        Log.d(TAG, "停止同步联系人到固件")
        BLEManager.unregisterGetDeviceParaCallBack(mGetDeviceParaCallBack)
        BLEManager.unregisterAppSendAllPhoneContactsCallBack(mAppSendAllPhoneContactsCallBack)
        BLEManager.stopTranCommonFile()
    }

    private fun sendAllContacts2Device(update: Boolean) {
        Log.d(TAG, "从固件端获取上次同步联系人时间成功，update=$update")
        if (BLEManager.isConnected()) {
            mContactList.clear()
            try {
                val localContactList = PhoneUtil.getPhoneSorted().filter {
                    val isEmpty = it.telePhone!!.replace(" ", "").isEmpty()
                    if (isEmpty) {
                        Log.d(TAG, "${it.name} phone num is empty")
                    }
                    !isEmpty
                }
                    .map { it.name?.let { it1 -> ContactBean(name = it1, telePhone = it.telePhone!!.replace(" ", "")) } }
                    .toMutableList()
                if (localContactList.isNotEmpty()) {
                    for (PhoneDto in localContactList) {
                        PhoneDto!!.telePhone = PhoneDto!!.telePhone + ")"
                        if (PhoneDto!!.telePhone.contains("(")) {
                            PhoneDto!!.telePhone = PhoneDto.telePhone.replace("(", "")
                        }
                        if (PhoneDto!!.telePhone.contains(")")) {
                            PhoneDto!!.telePhone = PhoneDto.telePhone.replace(")", "")
                        }
                        if (PhoneDto!!.telePhone.contains("-")) {
                            PhoneDto!!.telePhone = PhoneDto.telePhone.replace("-", "")
                        }
                        if (PhoneDto!!.name
                                .replace(" ", "").replace("(", "").replace("-", "")
                                .replace(")", "") == PhoneDto.telePhone.replace(" ", "")
                        ) {
                            PhoneDto!!.name = ""
                        }
                        mContactList.add(PhoneDto)
                    }
                }
                removeDuplicateContact()
                if (mContactList.isNotEmpty()) {
                    if (!update && isContactsNotChanged()) {
                        mSyncing.set(false)
                        mContactList.clear()
                        Log.d(TAG, "C库返回不需要更新联系人并且本地联系人列表没有发生变化，不用同步联系人列表到固件")
                        return
                    }
                    startSendContacts2Device(mContactList)
                } else {
                    Log.d(TAG, "没有发现有效的联系人")
                    mSyncing.set(false)
                }
            } catch (e: Exception) {
                Log.d(TAG, "查询本地联系人出现异常 error=$e")
                mSyncing.set(false)
            }
        } else {
            Log.d(TAG, "sendAllContacts2Device，设备未连接")
            mSyncing.set(false)
        }
    }

    private fun isContactsNotChanged(): Boolean {
        val cacheContactList = getContactsForDevice()
        val cacheContactListSize =
            if (cacheContactList.isNullOrEmpty()) 0 else cacheContactList.size
        val newContactListSize = mContactList.size
        if (cacheContactListSize != newContactListSize)
            return false
        var noChange = true
        for (index in 0 until newContactListSize) {
            val newContactItem = mContactList[index]
            val oldContactItem = cacheContactList[index]
            if (newContactItem != oldContactItem) {
                noChange = false
                break
            }
        }
        return noChange
    }

    private fun getContactsForDevice(): MutableList<ContactBean> {
        //cached device phone book list
        return mutableListOf()
    }

    private fun saveContactsForDevice(list: MutableList<ContactBean>) {
        //cache device phone
    }

    /**
     *去掉重复手机号码的联系人
     */
    private fun removeDuplicateContact() {
        if (mContactList.isEmpty()) return
        val phoneNumberMap = TreeSet<String>()
        val newContactList = mContactList.filter {
            if (!phoneNumberMap.contains(it.telePhone)) {
                phoneNumberMap.add(it.telePhone)
                true
            } else {
                false
            }
        }
        mContactList.clear()
        if (newContactList.isNotEmpty()) mContactList.addAll(newContactList)
    }

    private fun startSendContacts2Device(contacts: List<ContactBean>) {
        val allPhoneContacts = AllPhoneContacts()
        allPhoneContacts.items = contacts.map {
            val item = AllPhoneContacts.PhoneContactItem()
            item.name = it.name
            item.phone = it.telePhone.replace(" ", "")
            item
        }
        allPhoneContacts.contact_item_num = allPhoneContacts.items.size
        val now = Calendar.getInstance(Locale.CHINA)
        allPhoneContacts.year = now.get(Calendar.YEAR)
        allPhoneContacts.month = now.get(Calendar.MONTH)
        allPhoneContacts.day = now.get(Calendar.DAY_OF_MONTH)
        allPhoneContacts.hour = now.get(Calendar.HOUR_OF_DAY)
        allPhoneContacts.minute = now.get(Calendar.MINUTE)
        allPhoneContacts.second = now.get(Calendar.SECOND)
        Log.d(TAG, "开始下发手机联系人到固件端")
        BLEManager.unregisterAppSendAllPhoneContactsCallBack(mAppSendAllPhoneContactsCallBack)
        BLEManager.registerAppSendAllPhoneContactsCallBack(mAppSendAllPhoneContactsCallBack)
        BLEManager.setAllPhoneContacts(allPhoneContacts)
    }

    override fun onConnectBreak(macAddress: String?) {
        super.onConnectBreak(macAddress)
        stopSync()
    }

    private fun queryContactsStatus() {
        val isSupport = deviceSupportSyncContact()
        val isConnected = BLEManager.isConnected()
        val isBind = BLEManager.isBind()
        Log.d(TAG, "queryContactsStatus, isSupport = $isSupport, isBind = $isBind , isConnected = $isConnected")
        if (isConnected && isBind && isSupport) {
            BLEManager.unregisterGetDeviceParaCallBack(mGetDeviceParaCallBack)
            BLEManager.registerGetDeviceParaCallBack(mGetDeviceParaCallBack)
            BLEManager.getContactReceiveTime()
        } else {
            mSyncing.set(false)
            Log.d(TAG, "queryContactsStatusDelay, device not connect or bind or not support")
        }
    }

    private val mAppSendAllPhoneContactsCallBack =
        AppSendAllPhoneContactsCallBack.ICallBack { filePath ->
            Log.d(TAG, "C库返回的联系人列表存储文件路径是：$filePath")
            if (!filePath.isNullOrEmpty()) {
                Log.d(TAG, "开始将C库写入的联系人列表文件传输到固件端")
                if (BLEManager.isConnected()) {
                    val config = FileTransferConfig()
                    config.filePath = filePath
                    config.iFileTransferListener = this@ContactManager
                    config.firmwareSpecName = ".ml"
                    Log.d(TAG, "onTransferStart")
                    BLEManager.startTranCommonFile(config)
                } else {
                    Log.d(TAG, "startTransfer device is disconnected")
                }
            } else {
                mContactList.clear()
                mSyncing.set(false)
            }
        }

    private val mGetDeviceParaCallBack = object : BaseGetDeviceParaCallBack() {
        override fun onGetContactReceiveTime(b: Boolean) {
            super.onGetContactReceiveTime(b)
            sendAllContacts2Device(b)
        }
    }

    inner class Observer(handler: Handler?) : ContentObserver(handler) {
        override fun onChange(selfChange: Boolean) {
            super.onChange(selfChange)
            Log.d(TAG, "监听联系人发生变化 : $selfChange")
            if (BLEManager.isConnected() && BLEManager.isBind() && deviceSupportSyncContact()) {
                startSync()
            }
        }
    }

    /**
     *判断设备是否支持获取最近一次同步联系人的时间获取是否支持同步联系人到固件
     */
    fun deviceSupportSyncContact(): Boolean {
        if (!BLEManager.isConnected()) return false
        val functionInfo = LocalDataManager.getSupportFunctionInfo()
        return functionInfo != null && functionInfo.v2_support_get_all_contact
    }

    override fun onStart() {
        //phone book file trans start
        callback?.onStart()
    }

    override fun onProgress(p0: Int) {
        //phone book file trans progress
        Log.d(TAG, "手机联系人传输进度 progress=$p0")
        callback?.onProgress(p0)
    }

    override fun onFailed(p0: String?) {
        //phone book file trans failed
        Log.d(TAG, "联系人传输到固件端失败")
        mSyncing.set(false)
        mContactList.clear()
        callback?.onFailed(p0)
    }

    override fun onSuccess() {
        //phone book file trans success
        Log.d(TAG, "${mContactList.size}条手机联系人成功传输到固件端")
        callback?.onSuccess(mContactList.size)
        if (mContactList.isNotEmpty()) {
            saveContactsForDevice(mContactList)
            mSyncing.set(false)
            mContactList.clear()
        }
    }


}
