package test.com.ido.runplan.page

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.View
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView

import com.github.lzyzsd.jsbridge.DefaultHandler
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.ido.ble.BLEManager
import com.ido.ble.bluetooth.device.BLEDevice
import com.ido.ble.protocol.model.SportPlan
import com.ido.ble.protocol.model.Units

import kotlinx.android.synthetic.main.activity_health_manager_layout.*
import org.json.JSONObject
import test.com.ido.R
import test.com.ido.runplan.*
import test.com.ido.runplan.data.LatLngBean
import test.com.ido.runplan.utils.SportLogHelper
import test.com.ido.utils.DialogUtil.showToast
import java.io.File

/**
 * @author pm
 * @date 2022/1/11
 * @time 16:28
 * 用途:睡眠管理和健康管理
 */ class  HealthManagerActivity : Activity(), IHealthManagerView,
        PageLoadView {

    private val mCustomWebChromClient: CustomWebChromClient = CustomWebChromClient()
    private var TAG = "HealthManagerActivity"
    private var url = ""
    private var form = ""
    private var isSound = true
    var mRunPlanBean: SetRunPlanH5Info? = null

    /**
     * 到音乐停止时间 停止播放
     */
    private val callHandler = Handler(Looper.getMainLooper())

    /**
     * 布局资源文件
     *
     * @return 布局资源文件
     */
    private var isStartMusicService = 0
    private var mediaPlayer: MediaPlayer? = null
    private var mStringList = mutableListOf<String>()

    private var mPresenter : HealthManagerPresenter? = null

    fun initPresenter() {
        if(mPresenter == null){
            mPresenter = HealthManagerPresenter(this)
        }
    }

    fun getLayoutResId() = R.layout.activity_health_manager_layout

    fun initViews() {
        mPresenter?.queryUserWeightPlanRemind()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResId())
        initPresenter()
        initViews()
        initData()
    }

    fun initData() {
        url = intent.getStringExtra(Constants.INTENT_URL)!!
        form = intent.getStringExtra(Constants.INTENT_FROM_MANAGER)!!
        getStringList()
        // 传递参数调用
        val webSettings = wv_h5.settings
        webSettings.domStorageEnabled = true
        /*     webSettings.setAppCacheMaxSize(1024*1024*8)
             var appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
             webSettings.setAppCachePath(appCachePath)
             webSettings.setAllowFileAccess(true)*/
        wv_h5.visibility = View.VISIBLE

        webSettings.javaScriptEnabled = true
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE // 不用cache
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webSettings.setSupportZoom(false)
        webSettings.builtInZoomControls = false
        webSettings.domStorageEnabled = true
        webSettings.databaseEnabled = true
        webSettings.blockNetworkLoads = false
        webSettings.blockNetworkImage = false
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        webSettings.setAppCacheEnabled(true)

        webSettings.setSupportMultipleWindows(true)
        webSettings.domStorageEnabled = true
        webSettings.pluginState = WebSettings.PluginState.ON
//        webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        wv_h5!!.setDefaultHandler(DefaultHandler())
        wv_h5.loadUrl(url)
        wv_h5.webChromeClient = mCustomWebChromClient
        //监听H5发送给APP得数据
        H5SendAppDataListener()
        //监听H5发送给APP需要奖章
        H5SendAppDataListenerMedal()
        //周报分享
        H5RunPlanDataRegisterHandler()
        mPresenter?.sendInitDataToWeb()
        //打开健康管理  发送主题色 语言 token appkey到H5
        mediaPlayer = MediaPlayer()
        mPresenter?.sportPlanuUnregisterSportPlanCallBack()
        mPresenter?.registerCallBack()


        wv_h5.webViewClient = WebViewClient(wv_h5, applicationContext, this)

    }

    private fun H5SendAppDataListenerMedal() {
        wv_h5.registerHandler(
            H5ToAppConstant.SET_USER_INFO_MEDAL
        ) { data, _ ->
            mPresenter?.sendInitMedalToWeb()
        }
    }
    private fun H5SendAppDataListener() {
        wv_h5.registerHandler(H5ToAppConstant.GET_USER_INFO_APP){
            data,_ ->

            SportLogHelper.saveSportLog(H5ToAppConstant.GET_USER_INFO_APP,"get user info,send connect Device status")
            mPresenter?.sendConnectDeviceAllStatusToWeb()
        }
        wv_h5.registerHandler(
            H5ToAppConstant.SEND_NOTIFICATION_TO_APP
        ) { data, _ ->
            SportLogHelper.saveSportLog(
                TAG, data.toString()
            )
            //  showToast(data.toString())
            val gson = GsonBuilder().create()
            val homeNavigatorBean: HeathManagerH5Info =
                gson.fromJson(data.toString(), HeathManagerH5Info::class.java)
            //message 传'stopSleepPlan'表示用户点击终止计划，通知app停止计划；
            //        传'updateSleepTime'表示用户更改了睡眠计划的起床入睡时间
            //        传'backToApp'表示通知app关闭当前h5；
            //        传'backToLogin'表示token失效，通知app关闭当前h5并返回登录页重新登录；
            //        传''表示不做任何操作
            when (homeNavigatorBean.message) {

                H5ToAppConstant.SET_USER_INFO_MEDAL -> {
                    mPresenter?.sendInitMedalToWeb()
                }
                Constants.BACK_TO_APP -> {
                    finish()
                }
                Constants.BACK_TO_LOGIN -> {
                    //Token时效
//                    gotoPreLoginPage(Constants.LOGIN_GET_USERINFO_FAIL_THREE)
                }

                Constants.OPEN_SYNC_HEALTHINFO -> {
                    //表示开启计划，通知用户打开更新数据信息；
                    //      gotoPreLoginPage(Constants.LOGIN_GET_USERINFO_FAIL_THREE)
//                    var safeSetting =
//                        GreenDaoUtil.queryPrivateSafeSetting(RunTimeUtil.getInstance().userId)
//                    if (safeSetting == null) {
//                        safeSetting = PrivateSafeSetting()
//                        safeSetting.userId = RunTimeUtil.getInstance().userId
//                        safeSetting.savePrivateData = true
//                        safeSetting.saveSportData = true
//                        safeSetting.saveHealthData = true
//                        GreenDaoUtil.addPrivateSafeSetting(safeSetting)
//                    } else {
//                        safeSetting.savePrivateData = true
//                        safeSetting.saveSportData = true
//                        safeSetting.saveHealthData = true
//                        try {
//                            safeSetting.update()
//                        } catch (e: Exception) {
//                            GreenDaoUtil.addPrivateSafeSetting(safeSetting)
//                        }
//                    }
                    val json = JsonObject()
                    json.addProperty("data", "updateSleepData")
                    SportLogHelper.saveSportLog(TAG, json.toString())
                    backH5(Constants.SEND_NOTIFICATION_TOWEB, json.toString())
                }

                H5ToAppConstant.RUNNING_PLAN_LOCK_SCREEN -> {
                    //表示锁屏

                }

                H5ToAppConstant.GOTOEXERCISE -> {
                    //去运动
                    //表示告知app端鏈接設備
                }
                H5ToAppConstant.ADDEDFOOD -> {
                    //用户记录了饮食  需要计算当天奖章
                }

                H5ToAppConstant.RUNNING_PLAN_OPEN_VIBRATION -> {
                    //表示开始震动+提示音
                    if (isSound) {
//                        mPresenter?.playMusic(R.raw.run_plan_dingdong)
                    }
                    mPresenter?.vibrate()
                }
                H5ToAppConstant.RUNNING_PLAN_OPEN_VIBRATION_TWO -> {
                    //表示开始震动+提示音2
                    if (isSound) {
//                        mPresenter?.playMusic(R.raw.run_plan_2)
                    }
                    mPresenter?.vibrate()
                }
                H5ToAppConstant.BLUETOOTH_DEVICE_LIST -> {
                    //表示告知app端返回蓝牙设备列表
                    mPresenter?.getBluetoothDeviceListToWeb()
                }
                H5ToAppConstant.CONNECT_DEVICE_WEIGHT_PLAN -> {
                    //表示告知app端鏈接設備
                }
                H5ToAppConstant.OPEN_SOUND -> {
                    //打开声音
                    isSound = true

                }
                H5ToAppConstant.CLOSE_SOUND -> {
                    //关闭声音
                    isSound = false
                }
                H5ToAppConstant.BACK_TO_BOUND_DEVICE -> {
                    //表示告知app端去绑定设备，需要返回结果
                }
                H5ToAppConstant.RUNNING_PLAN_ALL_DEVICE_STATUS -> {
                    //表示告知app端需要返回设备和蓝牙状态给H5   需要查询设备跑步计划
                    //当做开始训练调试用
                    if (mPresenter != null && BLEManager.isConnected() && mPresenter!!.supportFunctionInfo.v3_support_sports_plan) {
                        val getSportPlanBean = SportPlan()
                        getSportPlanBean.operate = 4
                        BLEManager.getSportPlan(getSportPlanBean)
                    }
                    if (mPresenter != null && !BLEManager.isConnected()) {
                        mPresenter?.sendConnectDeviceAllStatusToWeb()
                    }
                    //      mPresenter.sendConnectDeviceAllStatusToWeb()
                }

            }

        }
    }

    private val sportPlanCallBack = object : SportPlanCallBackWrapper(){
        override fun onQueryResult(b: Boolean, sportPlan: SportPlan?) {
            super.onQueryResult(b, sportPlan)
            if (b && sportPlan != null && sportPlan.year != 0){

            } else{
                if(mRunPlanBean != null)
                    mPresenter?.sendSetPlanToDevice(mRunPlanBean!!, SportPlan.OPERATE_SEND)
            }
            BLEManager.unregisterSportPlanCallBack(this)
        }
    }



    /**
     * 设置计划
     */
    private fun H5SendAppSetPlan() {
        wv_h5.registerHandler(
            H5ToAppConstant.RUNNING_PLAN_SET_PLAN
        ) { data, _ ->
            SportLogHelper.saveSportLog(
                    TAG,
                data.toString()
            )
            //  showToast(data.toString())
            val gson = GsonBuilder().create()
            mRunPlanBean =
                gson.fromJson(data.toString(), SetRunPlanH5Info::class.java)
            if (mRunPlanBean != null && mRunPlanBean?.runningPlanObj != null) {
                mPresenter?.setCurrRunPlan(mRunPlanBean!!)
                if (mRunPlanBean?.runningPlanObj?.operate == 1) {
                    mPresenter?.sendSetPlanToDevice(mRunPlanBean!!, SportPlan.OPERATE_SEND)
                    //制定计划
                } else if (mRunPlanBean?.runningPlanObj?.operate == 2) {

                } else {
                    //结束计划
                    val endSportPlan = SportPlan()
                    endSportPlan.operate = SportPlan.OPERATE_END
                    endSportPlan.type = mRunPlanBean?.runningPlanObj?.type!!.toInt()
                    endSportPlan.year = mRunPlanBean?.runningPlanObj?.year!!
                    endSportPlan.month = mRunPlanBean?.runningPlanObj?.month!!
                    endSportPlan.day = mRunPlanBean?.runningPlanObj?.day!!
                    endSportPlan.hour = mRunPlanBean?.runningPlanObj?.hour!!.toInt()
                    endSportPlan.min = mRunPlanBean?.runningPlanObj?.minute!!.toInt()
                    endSportPlan.sec = mRunPlanBean?.runningPlanObj?.second!!.toInt()
                    BLEManager.setSportPlanEnd(endSportPlan)

                }
            } else {
                //下发计划失败
                mPresenter?.sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_ISSET_PLAN_FAIL)
            }
        }
    }


    /**
     * 3.开始，暂停，恢复，结束运动   runningPlanToggleTraining
     */
    private fun H5SendAppSport() {
        wv_h5.registerHandler(
            H5ToAppConstant.RUNNING_PLAN_TOGGLE_TRAINING
        ) { data, _ ->
            SportLogHelper.saveSportLog(TAG,
                H5ToAppConstant.RUNNING_PLAN_TOGGLE_TRAINING + data.toString()
            )
            val gson = GsonBuilder().create()
            val mRunPlanToggleH5Info: RunPlanToggleH5Info =
                gson.fromJson(data.toString(), RunPlanToggleH5Info::class.java)
            if (mPresenter != null && mPresenter?.supportFunctionInfo != null && mPresenter!!.supportFunctionInfo.v3_support_sports_plan) {
                //支持跑步计划
                //查询设备的跑步计划是否与当前用户计划
                mPresenter?.sendOperationPlanToDevice(mRunPlanToggleH5Info)
            } else {
                mPresenter?.mRunDetail(mRunPlanToggleH5Info)
            }
        }
        //连接蓝牙
        wv_h5.registerHandler(
            H5ToAppConstant.CONNECT_BLUETOOTH_DEVICE_TO_APP
        ) { data, _ ->
            SportLogHelper.saveSportLog(TAG, data.toString())
            val gson = GsonBuilder().create()
            val mConnectBluetoothDevice =
                gson.fromJson(data.toString(), ConnectBluetoothDeviceH5Bean::class.java)
//            var deviceList = SPHelper.getDeviceList()
//            for (device in deviceList) {
//                if (mConnectBluetoothDevice.bluetoothDevice != null && !TextUtils.isEmpty(
//                        mConnectBluetoothDevice.bluetoothDevice.mac
//                    ) && mConnectBluetoothDevice.bluetoothDevice.mac == device.getMac()
//                ) {
//                    mPresenter?.connectDevice(device)
//                }
//            }
        }
    }

    /**
     * H5各个接口数据监听
     */
    private fun H5RunPlanDataRegisterHandler() {
        H5SendAppSport()
        H5SendAppSetPlan()
    }


    override fun runPlanAppSendH5(json: JsonObject, name: String) {
        SportLogHelper.saveSportLog(TAG, name + json.toString())
        wv_h5.callHandler(
            name,
            json.toString()
        ) { data ->
            SportLogHelper.saveSportLog(TAG, data.toString())
        }
    }
    override fun AppSendH5Medal(json: JsonObject, name: String) {
        SportLogHelper.saveSportLog(TAG, name + json.toString())
        wv_h5.callHandler(
            name,
            json.toString()
        ) { data ->
            SportLogHelper.saveSportLog(TAG, data.toString())
        }
    }



    companion object {
        @JvmStatic
        fun startActivity(activity: Activity, url: String, form: String) {
            val intent = Intent(activity, HealthManagerActivity::class.java)
            intent.putExtra(Constants.INTENT_URL, url)
            intent.putExtra(Constants.INTENT_FROM_MANAGER, form)
            activity.startActivity(intent)
        }
    }

    /**
     * 统一回复H5yes
     */
    private fun backH5(name: String, data: String) {
        wv_h5.callHandler(
            name,
            data
        ) { data ->
            SportLogHelper.saveSportLog(
                    TAG, data.toString()
            )
        }
    }

    override fun onDataLoadSuccess(
        groupDataList: MutableList<MutableMap<String, String>>,
        childDataList: MutableList<MutableList<MutableMap<String, String>>>
    ) {

    }


    /**
     * 注册动态广播
     */
    private fun unRegisterMusicReceiver() {


    }

    override fun onNeedOpenBle() {
        println("onNeedOpenBle")
    }

    override fun onConnectStart(device: BLEDevice?) {

    }

    override fun onConnectSuccess(needBind: Boolean) {
        //连接成功  查询当前连接设备得计划
        mPresenter?.sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_CONNECTING_DEVICE_SUCCESS)


    }

    override fun onConnectFailed(errorCode: Int) {
        mPresenter?.sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_CONNECTING_DEVICE_FAIL)
    }

    override fun onInDfuMode(device: BLEDevice?) {

    }

    override fun onBindWrongDevice(device: BLEDevice?) {

    }

    override fun onBindOrifitDevice() {

    }

    override fun onGetDeviceInfoSuccess() {

    }

    override fun onNeedAuthCode(len: Int) {

    }

    override fun onNeedConfirm(shape: Int, mac: String?) {

    }

    override fun onBindSuccess() {
        mPresenter?.sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_CONNECTING_DEVICE_SUCCESS)
    }

    override fun onBindTimeOut(errorCode: Int) {
        mPresenter?.sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_CONNECTING_DEVICE_FAIL)
    }

    override fun onBindFailed(errorCode: Int, isReject: Boolean) {
        mPresenter?.sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_CONNECTING_DEVICE_FAIL)
    }

    override fun showSportStartSuccess() {
        mPresenter?.sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_START_TRAINING)
    }

    override fun setDeviceStatus(deviceStatus: String) {

    }

    override fun showSportStartFailedLowPower() {
        //启动失败  电量过低
        mPresenter?.sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_START_TRAINING_FAIL)
    }

    override fun showSportStartFail() {
        mPresenter?.sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_START_TRAINING_FAIL)
    }

    override fun showSportStartFailedInCalling() {
        mPresenter?.sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_START_TRAINING_FAIL)
    }

    override fun showSportStartFailedChargePower() {
        mPresenter?.sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_START_TRAINING_FAIL)
    }

    override fun showSportStartError(msg: String) {
        mPresenter?.sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_START_TRAINING_FAIL)
    }

    override fun setGPSSingleStrength(gpsSingleStrength: Int) {

    }

    override fun setSportDistance(sportDistance: String) {

    }

    override fun setSportDistanceUnit(sportDistanceUnit: String) {

    }

    override fun setSportSpeed(sportSpeed: String) {

    }

    override fun setSportTime(sportTime: String) {

    }

    override fun setSportCalorie(sportCalorie: String) {

    }


    override fun setDeviceStatusMap(deviceStatusMap: String) {

    }

    override fun setGPSSingleStrengthMap(gpsSingleStrengthMap: Int) {

    }

    override fun setSportDistanceMap(sportDistanceMap: String) {

    }

    override fun setSportDistanceUnitMap(sportDistanceUnitMap: String) {

    }

    override fun setSportSpeedMap(sportSpeedMap: String) {

    }

    override fun setSportSpeedMapTitle(sportSpeedMapTitle: String) {

    }

    override fun setSportTimeMap(sportTimeMap: String) {

    }

    override fun showEndConfirmDialog() {

    }

    override fun toSportHistory(type: Int, sportHealth: SportHealth) {

    }

    override fun addPolylineAndMove(latLngBean: LatLngBean, isEndPoint: Boolean) {

    }

    override fun loadMap(latLngBean: LatLngBean) {

    }

    override fun addCurrentMarker(latLngBean: LatLngBean) {

    }

    override fun addFirstCurrentMarker(latLngBean: LatLngBean) {

    }

    override fun showMessage(message: String) {
        showToast(this,message)
    }

    override fun showLoading(message: String) {

    }

    override fun hideLoading() {

    }

    override fun showDisconnectDialog() {

    }


    override fun setSportStatus(isRunning: Boolean) {
        if (isRunning) {
            mPresenter?.sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_RENEW_TRAINING)
        } else {
            mPresenter?.sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_SUSPEND_TRAINING)
        }
    }


    override fun setGpsStatusDesc(gpsStatusDesc: String) {

    }

    override fun showGpsStatusDesc(visible: Boolean) {

    }

    override fun showGpsSingle(visible: Boolean) {

    }

    override fun pause() {
        mPresenter?.sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_RENEW_TRAINING)
    }

    override fun reStart() {
        mPresenter?.sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_RENEW_TRAINING)
    }

    override fun onBackPressed() {
        if(wv_h5.url?.contains("running/#/runningAbility/trainingNewDevice") == true){
            //运动界面不能返回
            return
        }
        super.onBackPressed()
//        if(wv_h5.url?.contains("running/#/introduce") == true){
//            super.onBackPressed()
//        } else {
//            if(wv_h5.canGoBack()){
//                wv_h5.goBack()
//            } else{
//                super.onBackPressed()
//            }
//        }
    }

    override fun end() {

    }

    override fun isSportOutDoor(isOutDoor: Boolean) {

    }

    override fun stopLocation() {

    }

    override fun setSportRunListener() {

    }

    override fun clearListener() {

    }

    override fun stopRun(isSave: Boolean) {

    }

    override fun getSportNameByType(sportType: Int) {

    }

    override fun initUserTarget() {

    }

    override fun toSoundOffOrOn() {

    }

    override fun getInit(): Boolean {
        return false
    }

    override fun onRestoreInstanceState() {

    }

    override fun expandGroup(groupPosition: Int) {

    }


    /**
     * 用户信息失效,退出到登录注册页面
     */
    private fun gotoPreLoginPage(resultKey: String) {

    }



    /**
     *图片生成成功
     */
    override fun screenShotSuccess() {

    }

    override fun sleepAppSendH5(json: JsonObject) {
        //message 传'updateSleepData'表示用户通过设备更新了数据，通知h5更新睡眠数据；
        //        传'stopPlan'表示用户多次睡眠不达标，通知h5弹窗提示用户可以停止计划；
        //        传''表示不做任何操作
        /* wv_h5.send(json.toString(), CallBackFunction { data ->

             Toast.makeText(applicationContext, data.toString(), Toast.LENGTH_LONG).show()
             ConnectLogHelper.saveLog(TAG, data.toString())
         });*/
        wv_h5.callHandler(
            "sendNotificationToWeb",
            json.toString()
        ) { data ->
            SportLogHelper.saveSportLog(TAG, data.toString())
        }
    }

    /**
     * 9国隐私政策和用户协议
     */
    private fun getStringList() {
        mStringList.add("en")
        mStringList.add("de")
        mStringList.add("es") //西班牙
        mStringList.add("fr")
        mStringList.add("it")
        mStringList.add("ja")
        mStringList.add("ko") //韩语
        mStringList.add("pt") //葡萄牙
        mStringList.add("ru") //俄语
        mStringList.add("cn")
        mStringList.add("cs") //捷克语
        mStringList.add("tr") //土耳其
        mStringList.add("nl") //荷兰
        mStringList.add("el") //希腊语
    }

    override fun onDestroy() {
        super.onDestroy()
        clearRegister()
        BLEManager.unregisterSportPlanCallBack(sportPlanCallBack)
        callHandler.removeCallbacksAndMessages(null)
        stopMediaPlayer()
        unRegisterMusicReceiver()
    }

    private fun clearRegister() {
        try {
            val messageHandler = wv_h5::class.java.getDeclaredField("messageHandlers")
            messageHandler.isAccessible = true
            val map = messageHandler.get(wv_h5) as? MutableMap<*,*>
            map?.clear()
        } catch (e: Exception) {

        }
    }

    /**
     * 重新加载数据
     */
    open fun requestPullData() {
//        val userId = RunTimeUtil.getInstance().userId
//        val allTaskPropertyList = getHistoryTaskRunPlanAndSleepPlanPropertyList()
//        for (itemTaskPropertyPair in allTaskPropertyList) {
//            val constructor =
//                itemTaskPropertyPair.value.getConstructor(Long::class.java, Boolean::class.java)
//            val listener = constructor.newInstance(userId, false) as BaseHistoryTaskListener
//            listener.startTask(
//                null, null
//            )
//
//        }
//        calculateCategoryMedal(userId, MedalCategory.HealthManager)
    }

    override fun onStop() {
        super.onStop()
        //    requestPullData()
        mPresenter?.sportPlanuUnregisterSportPlanCallBack()
    }

    private fun stopMediaPlayer() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer!!.stop()
                mediaPlayer!!.release()
                mediaPlayer = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun LoadPageFinish() {

    }




    inner class CustomWebChromClient : WebChromeClient() {
        var uploadMessages: ValueCallback<Array<Uri>>? = null
        private val FILECHOOSER_RESULTCODE = 0x01
        /**
         * 兼容5.0及以上
         */
        override fun onShowFileChooser(
                webView: WebView,
                valueCallback: ValueCallback<Array<Uri>>,
                fileChooserParams: FileChooserParams
        ): Boolean {
            uploadMessages = valueCallback
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = generateType(fileChooserParams.acceptTypes)
            startActivityForResult(
                Intent.createChooser(intent, "File Chooser"), FILECHOOSER_RESULTCODE
            )
            return true
        }

        private fun generateType(types: Array<String>?): String {
            val stringBuilder = StringBuilder()
            if (types != null && types.size > 0) {
                for (i in types.indices) {
                    if (i > 0) {
                        stringBuilder.append(",")
                    }
                    stringBuilder.append(types[i])
                }
            }
            val type = stringBuilder.toString()
            return if (TextUtils.isEmpty(type)) {
                "*/*" //所有类型的文件
            } else {
                type
            }
        }

        fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
            if (requestCode == FILECHOOSER_RESULTCODE) {
                val result =
                    if (intent == null || resultCode != Activity.RESULT_OK) null else intent.data
                if (uploadMessages != null) {
                    if (result == null) {
                        uploadMessages!!.onReceiveValue(null)
                    } else {
                        uploadMessages!!.onReceiveValue(arrayOf(result))
                    }
                    uploadMessages = null
                }
            }
        }

        override fun onReceivedTitle(view: WebView, title: String) {
            super.onReceivedTitle(view, title)

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mCustomWebChromClient.onActivityResult(requestCode, resultCode, data)
    }
}
