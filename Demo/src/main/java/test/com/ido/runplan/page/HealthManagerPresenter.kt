package test.com.ido.runplan.page

import android.app.Service
import android.media.MediaPlayer
import android.os.Handler
import android.os.Vibrator
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.ido.ble.BLEManager
import com.ido.ble.LocalDataManager
import com.ido.ble.protocol.model.*
import test.com.ido.APP
import test.com.ido.exgdata.demo.BleSdkWrapper
import test.com.ido.runplan.*
import test.com.ido.runplan.HealthManagerPresenter
import test.com.ido.runplan.data.LatLngBean
import test.com.ido.runplan.data.RunPlanCountDownTimeBean
import test.com.ido.runplan.sync.NoticeSportActionToggleCallBackWrapper
import test.com.ido.runplan.utils.RunTimeUtil
import test.com.ido.runplan.utils.SportLogHelper
import test.com.ido.utils.DateUtil
import test.com.ido.utils.TimeUtil
import java.util.*

/**
 * @author pm
 * @date 2022/1/11
 * @time 16:28
 * 用途:睡眠管理和健康管理
 */
class HealthManagerPresenter(val view: IHealthManagerView) :BaseCmdPresenter() {
    var mUserId = RunTimeUtil.getInstance().userId
    private var TAG = "HealthManagerActivity"
    private var mediaPlayer: MediaPlayer? = null
    private var mVib: Vibrator? = null
    private var avgRate = 0 //心率平均值
    private var avgRateFrequency = 0 //心率平均值
    private var isAccumulateRate = false //是否累积心率值
    var mCurrRunPlanBean: SetRunPlanH5Info? = null
    public var  mOrignalWeight=0f
    public var  mOrignalWeightTime=0L
    var mUserInfo : UserInfo? = null
    /**
     * 运动是否完成
     */
    protected var mIsCompleteRun = false
    /**
     * 运动管理类
     */
    var mSportRunManager: SportRunManager? = null

    // 播放提示音
    private var mMediaPlayer: MediaPlayer? = null
    /**
     * 是否是强制结束
     */
    private var isEnd = false

    /**
     * 是否是户外运动
     */
    private var mIsOut: Boolean = false

    /**
     * 是否移除过网络定位点
     */
    private var mIsAlreadyLoadGps: Boolean = false

    private var isConnectedDevice: Boolean = false

    private val handler = Handler()
    /**
     * app和手环交互数据的数据结构
     */
    private val mV3SwitchDataAppIng = V3AppExchangeDataIngPara()
    /**
     * gps信号值
     */
    private var mGpsSignValue = Constants.GPS_INVALID

    /**
     * 交互数据时发送给手环的距离
     */
    private var mSendDistance = 0.0

    private var changeIndex: Long = 0
    /**
     * 是否支持实时配速
     */
    private var mIsSupportRealPace: Boolean = false
    /**
     * v3协议
     */
    private var mIsV3Exchange = false
    /**
     *.4.app通知h5   sendNotificationToWeb
     * | 'runningPlanStartTraining'           | 表示开始训练，通知h5                                      |
    | ------------------------------------ | --------------------------------------------------------- |
    | 'runningPlanSuspendTraining'         | 表示暂停训练，通知h5暂停训练                              |
    | 'runningPlanRenewTraining'           | 表示恢复训练，通知h5恢复训练                              |
    | 'runningPlanStopTraining'            | 表示停止训练，通知h5停止训练                              |
    | 'runningPlanSyncDeviceData'          | 表示同步设备数据到app，通知h5弹窗提示用户，并禁止页面操作 |
    | 'runningPlanIsSetPlanSuccese'        | 表示设置计划成功，通知h5弹窗提示用户                      |
    | 'runningPlanIsSetPlanFail'           | 表示设置计划失败，通知h5弹窗提示用户                      |
    | 'runningPlanConnectingDeviceSuccese' | 表示连接设备成功，通知h5弹窗提示用户                      |
    | 'runningPlanConnectingDeviceFail'    | 表示连接设备失败，通知h5弹窗提示用户                      |
     */
    var lastClickTime = 0L
    var lastSetClickTime = 0L
    fun sendNotificationToWeb(name: String) {
        val json = JsonObject()
        //300ms以内只会发送一次通知给H5  防止多次发送

        var curClickTime = System.currentTimeMillis()
        if ((curClickTime - lastClickTime) >= 300) {
            json.addProperty("data", name)
            sendNotificationToWeb(json, Constants.SEND_NOTIFICATION_TOWEB)
            lastClickTime = System.currentTimeMillis()
        }
    }

    /**
     *.4.app通知h5   sendNotificationToWeb
     * | 'runningPlanStartTraining'           | 表示开始训练，通知h5                                      |
    | ------------------------------------ | --------------------------------------------------------- |
    | 'runningPlanSuspendTraining'         | 表示暂停训练，通知h5暂停训练                              |
    | 'runningPlanRenewTraining'           | 表示恢复训练，通知h5恢复训练                              |
    | 'runningPlanStopTraining'            | 表示停止训练，通知h5停止训练                              |
    | 'runningPlanSyncDeviceData'          | 表示同步设备数据到app，通知h5弹窗提示用户，并禁止页面操作 |
    | 'runningPlanIsSetPlanSuccese'        | 表示设置计划成功，通知h5弹窗提示用户                      |
    | 'runningPlanIsSetPlanFail'           | 表示设置计划失败，通知h5弹窗提示用户                      |
    | 'runningPlanConnectingDeviceSuccese' | 表示连接设备成功，通知h5弹窗提示用户                      |
    | 'runningPlanConnectingDeviceFail'    | 表示连接设备失败，通知h5弹窗提示用户                      |
     */
    fun sendNotificationToWeb(name: String, data: String) {
        val json = JsonObject()
        json.addProperty(name, data)
        sendNotificationToWeb(json, Constants.SEND_NOTIFICATION_TOWEB)
    }



    private fun sendNotificationToWeb(json: JsonObject, name: String) {
        if (view != null) {
            view.runPlanAppSendH5(json, name)
        }
    }

    fun registerCallBack() {
        getFunction()
        mediaPlayer = MediaPlayer()
        BLEManager.unregisterSportPlanCallBack(sportPlanCallBack)
        BLEManager.unregisterNoticeSportActionToggleCallBack(noticeSportActionToggleCallBack)
        BLEManager.registerSportPlanCallBack(sportPlanCallBack)
        BLEManager.registerNoticeSportActionToggleCallBack(noticeSportActionToggleCallBack)
        mSportRunManager?.onRestoreInstanceState()
        mSportRunManager = SportRunManager.getInstance()
        mSportRunManager?.userId = mUserId
        mIsAlreadyLoadGps = false
        isConnectedDevice = BLEManager.isConnected()
    }

    fun registerRunBack() {
        setOnStartListener()
        setSportRunListener()
    }


    /**
     * 1.获取用户信息  runningPlanGetUserInfoAPP
     * | language                 | 语言|
    | token                    | token|
    | appKey                   | appKey|
    | userId                   | 用户ID|
    | deviceVersion            | 设备版本号 |
    | bluetoothStatus          | 'YES':蓝牙已连接,'NO':蓝牙未连接|
    | equipmentBindingStatus   | 'YES':已绑定设备,'NO':未绑定设备|
    | isSupportRunningPlan     | 'YES':当前设备支持跑步计划,'NO':当前设备不支持跑步计划|
    | isSupportHeartRate       | 'YES':当前设备支持心率功能,'NO':当前设备不支持心率功能|
    | synchronizationStatus    | 设置：SyncPending同步中，SyncSuccess同步成功，SyncFail同步失败都要h5开启弹窗|
     */
    fun sendInitDataToWeb() {
        val json = JsonObject()
        val appLanguage = getAppLanguage()
        val connected = BLEManager.isConnected()
        val bind = BLEManager.isBind()


        val functionInfo = supportFunctionInfo
        json.addProperty("language", appLanguage)
        json.addProperty("serverSite", getServerSite())
        json.addProperty("token", RunTimeUtil.getInstance().appToken)
        json.addProperty("timestamp", getLocalTimeUnits())
        json.addProperty("appKey", Constants.APP_KEY)
        var  calorieUnit=" KCAL"

        //体重
        var  weightUnit="1"
        var  heightUnit="1"
        //身高
        heightUnit =  "1"
        json.addProperty("caloriseUnit", calorieUnit)
        //1男  2女
        json.addProperty("gender", "1")
        json.addProperty("weightUnit", weightUnit)
        json.addProperty("heightUnit", heightUnit)
        json.addProperty("userId", mUserId)
        json.addProperty("weekStart", "MONDAY")
        if (connected) {
            val device = LocalDataManager.getCurrentDeviceInfo()
            json.addProperty("deviceName", device?.mDeviceName)
        }
        json.addProperty("deviceVersion", "001")
        if (true) json.addProperty("bluetoothStatus", "YES")
        else json.addProperty("bluetoothStatus", "NO")

        if (connected) json.addProperty("boundNoConnectDeviceStatus", "YES")
        else json.addProperty("boundNoConnectDeviceStatus", "NO")

        if (bind) json.addProperty("equipmentBindingStatus", "YES")
        else json.addProperty("equipmentBindingStatus", "NO")

        json.addProperty("isSyncHealthInfo", "NO")
        if (functionInfo.v3_support_sports_plan) json.addProperty("isSupportRunningPlan", "YES")
        else json.addProperty("isSupportRunningPlan", "NO")
        if (functionInfo.heartRate || functionInfo.ex_main4_v3_hr_data) json.addProperty("isSupportHeartRate", "YES")
        else json.addProperty("isSupportHeartRate", "NO")
        json.addProperty("appName", "mentech wear")
        sendNotificationToWeb(json, H5ToAppConstant.GET_USER_INFO_APP)
    }
    fun sendMusicToWeb(musicId: Int, state: Int) {
        val json = JsonObject()
        json.addProperty("id", musicId)
        json.addProperty("status", state)
        sendNotificationToWeb(json, H5ToAppConstant.PLAY_MUSIC_APP)
    }
    fun sendInitMedalToWeb() {

    }
    /**
    2.下发计划到设备 1 开始计划
     */
    fun sendSetPlanToDevice(bean: SetRunPlanH5Info, type: Int) {
        val sportPlan = SportPlan()
        sportPlan.type = bean.runningPlanObj.type
        sportPlan.version = bean.runningPlanObj.version
        sportPlan.day = bean.runningPlanObj.day
        sportPlan.year = bean.runningPlanObj.year
        sportPlan.month = bean.runningPlanObj.month
        sportPlan.hour = bean.runningPlanObj.hour
        sportPlan.min = bean.runningPlanObj.minute
        sportPlan.sec = bean.runningPlanObj.second
        sportPlan.day_num = bean.runningPlanObj.dayNum
        sportPlan.operate = type//计划下发
        sportPlan.items = mutableListOf<SportPlan.PlanContent>()
        if (type == 2) {
            for (i in 0 until bean.runningPlanObj.dayPlanContent.size) {
                val planContent = SportPlan.PlanContent()
                planContent.item = mutableListOf<SportPlan.ActionContent>()
                planContent.num = bean.runningPlanObj.dayPlanContent[i].num
                planContent.type = bean.runningPlanObj.dayPlanContent[i].type
                if (bean.runningPlanObj.dayPlanContent[i] == null || bean.runningPlanObj.dayPlanContent[i].actionContent == null) {
                    sportPlan.items.add(planContent)
                } else {
                    for (x in 0 until bean.runningPlanObj.dayPlanContent[i].actionContent.size) {
                        val actionContent = SportPlan.ActionContent()
                        actionContent.low_heart = bean.runningPlanObj.dayPlanContent[i].actionContent[x].lowHeart
                        actionContent.type = bean.runningPlanObj.dayPlanContent[i].actionContent[x].type
                        actionContent.height_heart = bean.runningPlanObj.dayPlanContent[i].actionContent[x].heightHeart
                        actionContent.time = bean.runningPlanObj.dayPlanContent[i].actionContent[x].time
                        planContent.item.add(actionContent)
                    }
                    sportPlan.items.add(planContent)
                }

            }
            BLEManager.setSportPlanDataSend(sportPlan)
        } else {
            BLEManager.setStartSportPlan(sportPlan)
        }
            sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_ISSET_PLAN_SUCCESS)
    }
    private var mStartTimeBean : HealthManagerPresenter.StartTimeBean? = null
    /**
     * 3. app 通知固件运动过程切换
     *  operate; //0x01:开始运动 ，0x02：暂停运动 , 0x03:恢复运动 ，0x04：结束运动 0x05：切换动作
     * @param mRunPlanBean
     */
    fun sendOperationPlanToDevice(mRunPlanBean: RunPlanToggleH5Info) {
        val mNoticeSportActionToggle = NoticeSportActionToggle()
        mNoticeSportActionToggle.day = mRunPlanBean.message.day.toInt()
        mNoticeSportActionToggle.type = mRunPlanBean.message.type.toInt()
        mNoticeSportActionToggle.month = mRunPlanBean.message.month.toInt()
        mNoticeSportActionToggle.training_offset = mRunPlanBean.message.training_offset
        mNoticeSportActionToggle.year = mRunPlanBean.message.year.toInt()
        //时分秒使用当前时间的
        mNoticeSportActionToggle.hour = TimeUtil.getHour()
        mNoticeSportActionToggle.minute = TimeUtil.getMinute()
        mNoticeSportActionToggle.second = TimeUtil.getSecond()
        mStartTimeBean = HealthManagerPresenter.StartTimeBean(mNoticeSportActionToggle.hour, mNoticeSportActionToggle.minute, mNoticeSportActionToggle.second)
        if (mRunPlanBean.message.operate == 1) {
            registerRunBack()
        }
        if (mRunPlanBean.message.operate == 5) {
            //强制结束
            isEnd = true
            mNoticeSportActionToggle.operate = 4
        } else {
            isEnd = false
            mNoticeSportActionToggle.operate = mRunPlanBean.message.operate
        }
        BLEManager.setNoticeSportActionToggle(mNoticeSportActionToggle)

    }

    /**
    训练中(推送动作切换等信息)  runningPlanSendActionToggleDataToWeb
     */
    fun sendActionToggleDataToWeb(noticeSportActionToggle: NoticeSportActionToggle) {
        val json = JsonObject()
        json.addProperty("operate", noticeSportActionToggle.operate)
        json.addProperty("type", noticeSportActionToggle.type)
        json.addProperty("action_type", noticeSportActionToggle.action_type)
        json.addProperty("time", noticeSportActionToggle.time)
        json.addProperty("nextType", noticeSportActionToggle.type)
        json.addProperty("nextTime", noticeSportActionToggle.time)
        json.addProperty("low_heart", noticeSportActionToggle.low_heart)
        json.addProperty("height_heart", noticeSportActionToggle.height_heart)
        sendNotificationToWeb(json, H5ToAppConstant.RUNNING_PLAN_SEND_ACTION_TOGGLE_DATA_TO_WEB)
    }

    var speed: String? = null
    /**
    9.训练中（推送训练数据）  runningPlanSendTrainingDataToWeb
     */
    fun sendTrainingDataToWeb(data: SportHealth, heartRate: Int, bean: RunPlanCountDownTimeBean?) {

        val connected = BLEManager.isConnected()
        val json = JsonObject()
        json.addProperty("count_hour", bean?.count_hour)
        json.addProperty("count_minute", bean?.count_minute)
        json.addProperty("count_second", bean?.count_second)
        json.addProperty("progress", data.totalSeconds)
        json.addProperty("distance", data.distance)
        json.addProperty("bluetoothStatus", connected)
        json.addProperty("duration", data.totalSeconds)
        json.addProperty("real_time_speed", bean?.km_speed)
        json.addProperty("calories", data.numCalories)
        json.addProperty("steps", data.numSteps)
        json.addProperty("heart_rate", heartRate)
        sendNotificationToWeb(json, H5ToAppConstant.RUNNING_PLAN_SEND_TRAINING_DATA_TO_WEB)
    }

    /**
    9.训练中（推送训练数据）  RUNNING_PLAN_SEND_AVERAGE_HEART_RATE_TO_WEB
     */
    fun sendTrainingAvgRateDataToWebTwo(avgRate: Int) {
        val json = JsonObject()
        json.addProperty("averageHeartRate", avgRate)
        json.addProperty("status", 1)
        sendNotificationToWeb(json, H5ToAppConstant.RUNNING_PLAN_SEND_AVERAGE_HEART_RATE_TO_WEB)
    }

    /**
    10.获取可连接蓝牙设备列表  getBluetoothDeviceListToWeb
     */
    fun getBluetoothDeviceListToWeb() {
        var basicInfo = LocalDataManager.getCurrentDeviceInfo()
        val array = JsonArray()
        if(basicInfo != null){
            val json = JsonObject()
            json.addProperty("deviceName", basicInfo.mDeviceName)
            json.addProperty("deviceVersion", basicInfo.version)
            json.addProperty("mac", basicInfo.mDeviceAddress)
            array.add(json)
        }
        val json = JsonObject()
        json.add("bluetoothDeviceList", array)
        sendNotificationToWeb(json, H5ToAppConstant.GET_BLUETOOTH_DEVICE_LIST_TO_WEB)
    }

    /**
    12.实时传输给h5的设备连接状态 (所有，若切换其中一个状态则更新四个数据)
     */
    fun sendConnectDeviceAllStatusToWeb() {
        val json = JsonObject()
        val jsonNew = JsonObject()
        if (true) json.addProperty("bluetoothStatus", "YES")
        else json.addProperty("bluetoothStatus", "NO")

        if (BLEManager.isConnected()) json.addProperty("boundNoConnectDeviceStatus", "YES")
        else json.addProperty("boundNoConnectDeviceStatus", "NO")

        if (BLEManager.isBind()) json.addProperty("equipmentBindingStatus", "YES")
        else json.addProperty("equipmentBindingStatus", "NO")

        val functionInfo = supportFunctionInfo

        if (functionInfo != null && functionInfo.v3_support_sports_plan) json.addProperty("isSupportRunningPlan", "YES")
        else json.addProperty("isSupportRunningPlan", "NO")

        if (functionInfo != null && functionInfo.heartRate || functionInfo.ex_main4_v3_hr_data) json.addProperty("isSupportHeartRate", "YES")
        else json.addProperty("isSupportHeartRate", "NO")

        if (BLEManager.isSyncAllDataIng()) json.addProperty("synchronizationStatus", "YES")
        else json.addProperty("synchronizationStatus", "NO")

        jsonNew.addProperty("runningPlanAllDeviceStatus", json.toString())
        sendNotificationToWeb(jsonNew, Constants.SEND_NOTIFICATION_TOWEB)
    }

    /**
    12.运动结束报告数据
     */
    fun sendSportEndDataToWeb(sportHealth: SportHealth) {
        val json = JsonObject()
        json.addProperty("completionRate", sportHealth.completionRate)
        json.addProperty("hrCompletionRate", sportHealth.hrCompletionRate)
        json.addProperty("inClassCalories", sportHealth.inClassCalories)
        json.addProperty("runningPullUp", sportHealth.runningPullUp)
        json.addProperty("totalSeconds", sportHealth.totalSeconds)
        json.addProperty("numCalories", sportHealth.numCalories)
        json.addProperty("numSteps", sportHealth.numSteps)
        json.addProperty("distance", sportHealth.distance)
        sendNotificationToWeb(json, H5ToAppConstant.RUNNING_PLAN_CURRENT_TRAINING_RECORD_TO_WEB)
    }

    /**
     * v3的数据交换
     */
    private fun changeV3Cmd() {
        //CommonLogUtil.d(TAG, "changeV3Cmd: ");
        SportLogHelper.saveSportLog(TAG,
                "changeV3Cmd: $mGpsSignValue,$mSendDistance")
        mV3SwitchDataAppIng.signalFlag = mGpsSignValue
        mV3SwitchDataAppIng.distance = mSendDistance.toInt()
        BleSdkWrapper.v3AppSwitchDataIng(mV3SwitchDataAppIng)
    }


    /**
     *运动计划回调监听
     */
    private val noticeSportActionToggleCallBack = object : NoticeSportActionToggleCallBackWrapper() {
        /**
         * App设置结果，根据operate; //0x01:开始运动 ，0x02：暂停运动 , 0x03:恢复运动 ，0x04：结束运动 0x05：切换动作 做类型区分
         */
        override fun onSettintResult(i: Int, b: Boolean, noticeSportActionToggle: NoticeSportActionToggle?) {
            super.onSettintResult(i, b, noticeSportActionToggle)
            SportLogHelper.saveSportLog(TAG,
                    "onSettintResult $b" + noticeSportActionToggle.toString())
            when (i) {
                0x01 -> {
                    if (b) {
                        mIsCompleteRun = true
                        mSportRunManager?.startRunPlan(mStartTimeBean)
                        //   sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_START_TRAINING)
                    } else {
                        mIsCompleteRun = false
                        mSportRunManager?.stopRunPlan(true)
                        sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_START_TRAINING_FAIL)
                    }
                }
                0x02 -> {
                    if (b) {
                        sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_SUSPEND_TRAINING)
                    } else {
                        sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_SUSPEND_TRAINING_FAIL)
                    }
                }
                0x03 -> {
                    if (b) {
                        sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_RENEW_TRAINING)
                    } else {
                        sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_RENEW_TRAINING_FAIL)
                    }
                }
                0x04 -> {
                    mIsCompleteRun = false
                    if (b) {
                        mSportRunManager?.mRunPlanData = true
                        mSportRunManager?.mIsEnd = true
                        if (isEnd) {
                            mSportRunManager?.stopRunPlan(false)
                        } else {
                            mSportRunManager?.stopRunPlan(true)
                        }
                        sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_STOP_TRAINING)
                    } else {
                        sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_STOP_TRAINING_FAIL)
                    }
                }
                0x05 -> {
                    //   noticeSportActionToggle?.let { sendActionToggleDataToWeb(it) }
                }
            }
        }

        /**
         * 设备通知App切换，根据operate; //0x01:开始运动 ，0x02：暂停运动 , 0x03:恢复运动 ，0x04：结束运动 0x05：切换动作 做类型区分
         */
        override fun onDeviceNotify(i: Int, b: Boolean, noticeSportActionToggle: NoticeSportActionToggle?) {
            super.onDeviceNotify(i, b, noticeSportActionToggle)
            SportLogHelper.saveSportLog(TAG,
                    "onDeviceNotify $b" + noticeSportActionToggle.toString())
            when (i) {
                0x01 -> {
                    sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_START_TRAINING)
                }
                0x02 -> {
                    sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_SUSPEND_TRAINING)
                }
                0x03 -> {
                    sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_RENEW_TRAINING)
                }
                0x04 -> {
                    mSportRunManager?.mRunPlanData = true
                    mIsCompleteRun = false
                    mSportRunManager?.mIsEnd = true
                    sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_STOP_TRAINING)
                }
                0x05 -> {
                    noticeSportActionToggle?.let { sendActionToggleDataToWeb(it) }
                }
            }
        }
    }
    /**
     *运动计划设置监听
     */
    private val sportPlanCallBack = object : SportPlanCallBackWrapper() {
        override fun onStartPlan(b: Boolean) {
            super.onStartPlan(b)
            SportLogHelper.saveSportLog(TAG,
                    "开始计划监听onStartPlan $b")

            if (b) sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_ISSET_PLAN_SUCCESS) else
                sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_ISSET_PLAN_FAIL)

        }

        override fun onSportDataSend(b: Boolean) {
            super.onSportDataSend(b)
            var curClickTime = System.currentTimeMillis()
            if (curClickTime - lastSetClickTime > 1000) {
                if (!b) {
                    SportLogHelper.saveSportLog(TAG,
                            "下发计划结果onSportDataSend $b")
                    //下发计划失败
                    sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_ISSET_PLAN_FAIL)
                } else {
                    SportLogHelper.saveSportLog(TAG,
                            "下发计划结果onSportDataSend ${mCurrRunPlanBean.toString()}")
                    mCurrRunPlanBean?.let { sendSetPlanToDevice(it, SportPlan.OPERATE_START) }
                }
            }

        }

        override fun onPlanEnd(b: Boolean) {
            super.onPlanEnd(b)
            if (b) sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_IS_END_PLAN_SUCCESS) else
                sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_IS_END_PLAN_FAIL)
            SportLogHelper.saveSportLog(TAG,
                    "运动计划结束结果onPlanEnd $b")
        }

        override fun onQueryResult(b: Boolean, sportPlan: SportPlan?) {
            super.onQueryResult(b, sportPlan)
            SportLogHelper.saveSportLog(TAG,
                    "运动计划查询结果onQueryResult $b" + sportPlan.toString())

            if (b && sportPlan != null) {
                if(mCurrRunPlanBean == null){
                    sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_CONNECTING_DEVICE_STATUS, "3")
                } else{
                    //查询成功
                    if (mCurrRunPlanBean?.runningPlanObj?.year == sportPlan.year
                            && mCurrRunPlanBean?.runningPlanObj?.month == sportPlan.month
                            && mCurrRunPlanBean?.runningPlanObj?.day == sportPlan.day
                            && mCurrRunPlanBean?.runningPlanObj?.hour == sportPlan.hour
                            && mCurrRunPlanBean?.runningPlanObj?.minute == sportPlan.min
                            && mCurrRunPlanBean?.runningPlanObj?.second == sportPlan.sec) {
                        sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_CONNECTING_DEVICE_STATUS, "1")
                        //    sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_CONNECTING_DEVICE_SUCCESS_AND_SAME)
                    } else if (sportPlan.year == 0) {
                        //  sendConnectDeviceAllStatusToWeb()
                        sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_CONNECTING_DEVICE_STATUS, "2")
                    } else {
                        sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_CONNECTING_DEVICE_STATUS, "3")
                    }
                }

            } else {

                //    sendSetPlanToDevice(mCurrRunPlanBean!!, SportPlan.OPERATE_SEND)
                sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_CONNECTING_DEVICE_STATUS, "2")
            }

        }
    }

    /**
     *获取各个服务器
     */
    fun getServerSite(): String {
        val serverSite: String
        serverSite = "cn"
        return serverSite
    }

    /**
     * 获取功能表走v3协议还是v2协议
     */
    private fun getFunction() {
        val supportFunctionInfo = LocalDataManager.getSupportFunctionInfo()
        if (supportFunctionInfo != null && BleSdkWrapper.isConnected()) {
            mIsV3Exchange = supportFunctionInfo.ex_table_main9_v3_activity_exchange_data
            mIsSupportRealPace = supportFunctionInfo.V3_support_v3_exchange_data_reply_add_real_time_speed_pace
        }
    }

    private fun getAppLanguage(): String? {

        var language ="en"

        return language
    }


    /**
     * 开启户外运动
     *
     * @param sportType
     * @param isApp
     */
    fun mRunDetail(mRunPlanBean: RunPlanToggleH5Info) {

    }

    fun setOnStartListener() {
        mSportRunManager!!.setSportStartCallback(object : SportRunManager.ISportStartCallBack {
            override fun sportStartSuccess() {
                if (view == null) {
                    return
                }
                mIsCompleteRun = true
                view.showSportStartSuccess()
                //开启提示声
                //    playMusic(com.ido.life.R.raw.target_tips_music)
                SportLogHelper.saveSportLog(TAG, "sportStartSuccess: ")
            }

            override fun sportStartFailedByLowPower() {
                if (view == null) {
                    return
                }
                view.showSportStartFailedLowPower()
                SportLogHelper.saveSportLog(TAG, "sportStartFaildByLowPower: ")
            }

            override fun sportStartFailed() {
                if (view == null) {
                    return
                }
                view.showSportStartFail()
                SportLogHelper.saveSportLog(TAG, "sportStartFaild: ")
            }

            override fun sportChargePower() {
                if (view == null) {
                    return
                }
                view.showSportStartFailedChargePower()
                SportLogHelper.saveSportLog(TAG, "sportChagerPower: ")
            }

            override fun sportRunInAlexa() {
                if (view == null) return
                SportLogHelper.saveSportLog(TAG, "sportRunInAlexa: ")
            }

            override fun sportStartInCalling() {
                if (view == null) return

                view.showSportStartFailedInCalling()
                SportLogHelper.saveSportLog(TAG, "sportStartInCalling: ")
            }
        })
    }

    fun setSportRunListener() {
        mSportRunManager?.setSportRunCallback(object : SportRunManager.ISportRunCallBack {

            override fun sportPause(isSuccess: Boolean) {
                SportLogHelper.saveSportLog(TAG, "sportPause: $isSuccess")
                if (isSuccess) {
                    if (view == null) {
                        return
                    }
                    view.setSportStatus(false)
                }
            }

            override fun sportResume(isSuccess: Boolean) {
                SportLogHelper.saveSportLog(TAG, "sportResume: $isSuccess")
                if (isSuccess) {
                    if (view == null) {
                        return
                    }
                    view.setSportStatus(true)
                }

            }

            override fun sportStop(isSuccess: Boolean,
                                   sportHealth: SportHealth, latLngBeanList: List<LatLngBean>) {
                SportLogHelper.saveSportLog(TAG, "sportStop: " + isSuccess + "" +
                        "," + sportHealth.toString())
                //根据不同类型旋转不同的详情
                if (isSuccess) {
                    mSportRunManager?.close()
                    //运动停止成功
                    sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_STOP_TRAINING)
                    sendSportEndDataToWeb(sportHealth)
                } else {
                    //停止失败.不需要保存数据
                    mSportRunManager?.close()
                    sendSportEndDataToWeb(sportHealth)
                    sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_STOP_TRAINING)
                }

            }


            override fun sportRunning(sportRunningBean: SportHealth?, heartRate: Int, latLngDomain: LatLngBean?, gpsSignValue: Int, isRemoveFistPoint: Boolean, bean: RunPlanCountDownTimeBean?) {
                if (!isAccumulateRate && sportRunningBean?.totalSeconds!! % 120 == 0 || (sportRunningBean?.totalSeconds!! - 1)
                        !! % 120 == 0 || (sportRunningBean.totalSeconds!! + 1)!! % 120 == 0) {
                    //开始累积心率值
                    avgRate = 0
                    avgRateFrequency = 0
                    isAccumulateRate = true
                }
                if (isAccumulateRate) {
                    avgRate += heartRate
                    avgRateFrequency++
                }
                if (sportRunningBean?.totalSeconds != 30 && sportRunningBean?.totalSeconds != 31 && sportRunningBean?.totalSeconds != 29 && ((sportRunningBean.totalSeconds - 30) % 120 == 0 || (sportRunningBean.totalSeconds - 31) % 120 == 0
                                || (sportRunningBean.totalSeconds - 29) % 120 == 0) && avgRateFrequency != 0) {
                    isAccumulateRate = false
                    sendTrainingAvgRateDataToWebTwo(avgRate / avgRateFrequency)
                    avgRate = 0
                    avgRateFrequency = 0
                    //     vibrate()
                }
                sendTrainingDataToWeb(sportRunningBean, heartRate, bean)

            }

            override fun sportFivePeaceAndSpeed(realTimePeace: String, realTimeSpeed: String) {
                var mRealTimePeace = realTimePeace
                //骑行是速度 其余的都是配速 固件没有返回平均配速现在都是自己算（之后会改为实时配速和速度）
                //   SportLogHelper.saveSportLog(TAG, "sportFivePeaceAndSpeed: $realTimePeace,$realTimeSpeed")
                //时间（分钟）/距离（公里） 固件直接给值 0～99'99''
                if (realTimePeace.contains("'")) {
                    try {
                        val paces = realTimePeace.split("'".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        val paceInt = Integer.parseInt(paces[0])
                        if (paceInt > 99) {
                            mRealTimePeace = DateUtil.computeTimePace("99.99")
                        }
                    } catch (e: Exception) {
                        SportLogHelper.saveSportLog(TAG, "getAvgPace: $e")
                    }

                }
                speed = mRealTimePeace
            }

        })

        mSportRunManager?.setConnectCallBack(object : SportRunManager.ISportConnectCallBack {
            override fun connectTimeOut() {
                SportLogHelper.saveSportLog(TAG, "connectTimeOut: ")
                if (view == null) {
                    return
                }
                mSportRunManager?.stopRunPlan(false)

            }

            override fun bleDisconnect() {
                SportLogHelper.saveSportLog(TAG, "bleDisconnect: ")
                if (view == null) {
                    return
                }
                sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_CONNECTING_BLUETOOTH_FAIL)

                mSportRunManager?.pauseRun()
            }

            override fun bleConnect() {
                if (view == null) {
                    return
                }
                SportLogHelper.saveSportLog(TAG, "bleConnect: ")
                view.hideLoading()
                mSportRunManager?.resumeRun()

            }

            override fun startConnect() {

            }
        })
    }

    fun back(): Boolean {
        if (mIsCompleteRun) {

            return false
        }
        return true
    }

    /**
     * 是否开启定位
     */
    fun getLocation() {
        mSportRunManager?.startLocation()
    }

    fun stopLocation() {
        SportLogHelper.saveSportLog(TAG, "stopLocation: stopLocation")
        mSportRunManager?.stopLocation()
    }



    fun setCurrRunPlan(mRunPlanBean: SetRunPlanH5Info) {
        mCurrRunPlanBean = mRunPlanBean
    }

    //开启振动
    fun vibrate() {
        try {
            if (mVib == null) {
                mVib = APP.getAppContext().getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
            }
            if (mVib != null && mVib!!.hasVibrator()) {
                mVib!!.vibrate(200)
                SportLogHelper.saveSportLog(TAG, "跑步计划震动提示")
            } else {
                SportLogHelper.saveSportLog(TAG, "当前手机不支持震动")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            SportLogHelper.saveSportLog(TAG, "跑步计划开始震动，出现异常exception=" + e.message)
        }

        return
    }
    fun sportPlanuUnregisterSportPlanCallBack() {
        BLEManager.unregisterSportPlanCallBack(sportPlanCallBack)
    }

    /**
     * 获取本地保存的单位，如果不存在，则按默认值设置
     *
     * @return
     */
    fun getLocalTimeUnits(): String  {
        return "HOUR_CLOCK_24"
    }


    /**
     * 查询用户健康管理数据
     */
    fun queryUserWeightPlanRemind() {

    }
}
