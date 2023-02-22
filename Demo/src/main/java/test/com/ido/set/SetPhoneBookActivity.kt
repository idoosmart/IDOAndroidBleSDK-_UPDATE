package test.com.ido.set

import android.content.ContentResolver
import android.database.ContentObserver
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.ido.ble.BLEManager
import com.ido.ble.LocalDataManager.getSupportFunctionInfo
import com.ido.ble.callback.AppSendAllPhoneContactsCallBack
import com.ido.ble.file.transfer.FileTransferConfig
import com.ido.ble.file.transfer.IFileTransferListener
import com.ido.ble.protocol.model.AllPhoneContacts
import com.ido.ble.protocol.model.IncomingCallInfo
import kotlinx.android.synthetic.main.activity_set_phone_book.*
import kotlinx.coroutines.Job
import test.com.ido.APP
import test.com.ido.R
import test.com.ido.connect.BaseAutoConnectActivity
import test.com.ido.model.ContactBean
import test.com.ido.utils.*
import java.util.*

class SetPhoneBookActivity : BaseAutoConnectActivity() {

    private var contacts: MutableList<ContactBean>? = null
    private var mJob: Job? = null

    private var mOnContactsChangedListener: OnContactsChangedListener? = null

    private var mContentResolver: ContentResolver? = null
    private var mObserver: Observer? = null
    private var mRegistered = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_phone_book)
        addDebounce(1000L)
        registerListener()
    }


    fun set(view: View) {
        if (PermissionUtil.checkSelfPermission(
                this, *PermissionUtil.getOnlyContactPermission()
            )
        ) {
            sendAllContacts2Device()
        } else {
            logP("requestPermissions")
            PermissionUtil.requestPermissions(
                this,
                PermissionUtil.CODE_REQUEST_PHONE,
                *PermissionUtil.getOnlyContactPermission()
            )
        }
    }


    fun dial(view: android.view.View) {
//        val phoneNumber = et_phone_number.text.toString().trim()
//        if (TextUtils.isEmpty(phoneNumber)) {
//            Toast.makeText(this, "input phone number", Toast.LENGTH_LONG).show()
//        } else {
//            val incomingInfo = IncomingCallInfo()
//            incomingInfo.phoneNumber = phoneNumber
//            BLEManager.setIncomingCallInfo(incomingInfo)
//        }
    }

    private fun logP(msg: String) {
        Log.d("SetPhoneBookActivity", msg)
    }

    private fun registerListener() {
        logP("registerListener, mRegistered = $mRegistered")
        if (!mRegistered && PermissionUtil.checkSelfPermission(
                this, *PermissionUtil.getOnlyContactPermission()
            )
        ) {
            mObserver = Observer(Handler(Looper.getMainLooper()))
            mContentResolver = APP.getAppContext().contentResolver
            val uri = ContactsContract.Contacts.CONTENT_URI
            mContentResolver?.registerContentObserver(uri, true, mObserver!!)
            mRegistered = true
        } else {
            logP("未授权，不注册监听")
        }
    }

    private fun addDebounce(timeInMill: Long) {
        CoroutinesUtils.debounce<Boolean>(timeInMill, onOffer = {
            mOnContactsChangedListener = object : OnContactsChangedListener {
                override fun onContactsChanged(changed: Boolean) {
                    logP("onContactsChanged changed = $changed")
                    it.offer(changed)
                }
            }
        }, onClose = {
            mOnContactsChangedListener = null
        }, onCatch = {
            logP("onContactsChanged error：$it")
        }) {
            logP("notify contact changed：$it")
            sendAllContacts2Device()
        }
    }

    /**
     * TODO 目前设备最多支持3000条联系人下发，超过的会被截取
     * TODO 目前设备最多支持3000条联系人下发，超过的会被截取
     * TODO 目前设备最多支持3000条联系人下发，超过的会被截取
     */
    private fun sendAllContacts2Device() {
        logP("sendAllContacts2Device")
        val isSupport =
            getSupportFunctionInfo()?.v2_support_get_all_contact ?: false
        val isConnected = BLEManager.isConnected()
        val isBind = BLEManager.isBind()
        if (isConnected && isSupport && isBind) {
            try {
                contacts = PhoneUtil.getPhoneSorted().filter {
                    val isEmpty = TextUtils.isEmpty(it.telePhone)
                    if (isEmpty) {
                        logP("${it.name} phone num is empty")
                    }
                    !isEmpty
                }.map {
                    val phone = try {
                        it.telePhone.trim().replace(" ", "")
                    } catch (e: Exception) {
                        it.telePhone
                    }
                    ContactBean(it.name, phone)
                }.toMutableList()
                logP("sendAllContacts2Device: $contacts")
                contacts = removeDuplicatePhone()

                if (contacts != null && contacts!!.isNotEmpty()) {
                    if (isContactsNotChanged()) {
                        logP("sendAllContacts2Device, contacts not changed!")
                        return
                    }
                    logP("sendAllContacts2Device, contacts size = ${contacts!!.size}")
                    startSendContacts2Device(contacts!!)
                } else {
                    logP("get no contact, notify MainActivity to check if the app has [android.permission.READ_CONTACTS] permission!")
                }
            } catch (e: Exception) {
                logP("sendAllContacts2Device failed: $e")
            }
        } else {
            logP("sendAllContacts2Device failed!")
        }
    }

    private fun removeDuplicatePhone(): MutableList<ContactBean>? {
        try {
            val result = mutableListOf<ContactBean>()
            val hashSet = hashSetOf<String>()
            if (contacts != null && contacts!!.isNotEmpty()) {
                contacts?.forEach {
                    if (!hashSet.contains(it.telePhone)) {
                        result.add(it)
                        hashSet.add(it.telePhone)
                    }
                }
            }
            return result
        } catch (e: Exception) {
        }
        return contacts
    }

    private fun isContactsNotChanged(): Boolean {
        val cacheList = DataUtils.getInstance().contactsForDevice
        logP("isContactsNotChanged：${cacheList?.size}")
        if (ListUtils.isNotEmpty(cacheList) && ListUtils.isNotEmpty(contacts)) {
            return ListUtils.equals(cacheList, contacts!!)
        }
        return false
    }

    private fun startSendContacts2Device(contacts: List<ContactBean>) {
        val mAllPhoneContacts = AllPhoneContacts()
        mAllPhoneContacts.items = contacts.map {
            val item = AllPhoneContacts.PhoneContactItem()
            item.name = it.name
            item.phone = it.telePhone
            item
        }
        mAllPhoneContacts.contact_item_num = mAllPhoneContacts.items.size
        val now = Calendar.getInstance(Locale.CHINA)
        mAllPhoneContacts.year = now.get(Calendar.YEAR)
        mAllPhoneContacts.month = now.get(Calendar.MONTH)
        mAllPhoneContacts.day = now.get(Calendar.DAY_OF_MONTH)
        mAllPhoneContacts.hour = now.get(Calendar.HOUR_OF_DAY)
        mAllPhoneContacts.minute = now.get(Calendar.MINUTE)
        mAllPhoneContacts.second = now.get(Calendar.SECOND)
        logP("startSendContacts2Device")
        BLEManager.unregisterAppSendAllPhoneContactsCallBack(
            mAppSendAllPhoneContactsCallBack
        )
        BLEManager.registerAppSendAllPhoneContactsCallBack(
            mAppSendAllPhoneContactsCallBack
        )
        BLEManager.setAllPhoneContacts(mAllPhoneContacts)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PermissionUtil.CODE_REQUEST_PHONE && PermissionUtil.checkSelfPermission(
                this, *PermissionUtil.getOnlyContactPermission()
            )
        ) {
            registerListener()
            mOnContactsChangedListener?.onContactsChanged(true)
        }
    }

    /**
     * TODO 解绑的时候可以清除
     * TODO 解绑的时候可以清除
     * TODO 解绑的时候可以清除
     */
    fun onUnBind() {
        try {
            logP("removeContactsForDevice")
            DataUtils.getInstance().removeContactsForDevice()
        } catch (e: Exception) {
            logP("removeContactsForDevice error: $e")
        }
    }

    private val mAppSendAllPhoneContactsCallBack =
        AppSendAllPhoneContactsCallBack.ICallBack { filePath ->
            logP("联系人文件：$filePath")
            if (!TextUtils.isEmpty(filePath)) {
                val config = FileTransferConfig()
                config.filePath = filePath
                config.iFileTransferListener = mTransferListener
                config.firmwareSpecName = FileUtil.getFileNameFromPath(filePath)
                BLEManager.startTranCommonFile(config)
                logP("开始传输联系人文件！")
            }
        }

    private val mTransferListener = object : IFileTransferListener {
        override fun onStart() {
            logP("onStart")
        }

        override fun onProgress(p0: Int) {
            logP("onProgress：$p0")
        }

        override fun onSuccess() {
            logP("onSuccess：${contacts?.size}")
            if (ListUtils.isNotEmpty(contacts)) {
                DataUtils.getInstance().saveContactsForDevice(contacts)
            }
            runOnUiThread {
                Toast.makeText(
                    this@SetPhoneBookActivity,
                    R.string.set_tip_success,
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        override fun onFailed(p0: String?) {
            logP("onFailed: $p0")
        }

    }


    interface OnContactsChangedListener {
        fun onContactsChanged(changed: Boolean)
    }

    inner class Observer(handler: Handler?) : ContentObserver(handler) {
        override fun onChange(selfChange: Boolean) {
            super.onChange(selfChange)
            logP("监听联系人发生变化: $selfChange")
            mOnContactsChangedListener?.onContactsChanged(true)
        }
    }

}