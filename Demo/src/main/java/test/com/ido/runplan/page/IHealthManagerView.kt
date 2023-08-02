package test.com.ido.runplan.page

import com.google.gson.JsonObject
import test.com.ido.runplan.SportHealth
import test.com.ido.runplan.data.LatLngBean

/**
 * @author pm
 * @date 2022/1/11
 * @time 16:28
 * 用途:睡眠管理和健康管理
 */
interface IHealthManagerView : IBindView{

    fun onDataLoadSuccess(groupDataList:MutableList<MutableMap<String, String>>,childDataList: MutableList<MutableList<MutableMap<String, String>>>)

    fun expandGroup(groupPosition:Int)
    /**
     *跑步计划app通知H5信息
     */
    fun runPlanAppSendH5(json: JsonObject,name :String)
    /**
     *跑步计划app通知H5信息
     */
    fun AppSendH5Medal(json: JsonObject,name :String)

    /**
     * 显示运动成功
     */
    fun showSportStartSuccess()


    /**
     * 设置设备的连接状态
     * @param deviceStatus
     */
    fun setDeviceStatus(deviceStatus: String)


    /**
     * 显示低电量
     */
    fun showSportStartFailedLowPower()

    /**
     * 手环已经在运动模式了
     */
    fun showSportStartFail()
    /**
     * 来电中
     */
    fun showSportStartFailedInCalling()

    /**
     * 正在充电
     */
    fun showSportStartFailedChargePower()

    /**
     * 开始运动出错
     */
    fun showSportStartError(msg: String)

    /**
     * 设置gps的信号强度
     * @param gpsSingleStrength
     */
    fun setGPSSingleStrength(gpsSingleStrength: Int)

    /**
     * 设置运动的距离
     * @param sportDistance
     */
    fun setSportDistance(sportDistance: String)

    /**
     * 设置运动距离的单位
     * @param sportDistanceUnit
     */
    fun setSportDistanceUnit(sportDistanceUnit: String)

    /**
     * 运动速度
     * @param sportSpeed
     */
    fun setSportSpeed(sportSpeed: String)


    /**
     * 运动时间
     * @param sportTime
     */
    fun setSportTime(sportTime: String)

    /**
     * 运动的卡路里
     * @param sportCalorie
     */
    fun setSportCalorie(sportCalorie: String)



    /**
     * 设置设备的连接状态
     * @param deviceStatusMap
     */
    fun setDeviceStatusMap(deviceStatusMap: String)

    /**
     * 设置gps的信号强度
     * @param gpsSingleStrengthMap
     */
    fun setGPSSingleStrengthMap(gpsSingleStrengthMap: Int)

    /**
     * 设置运动的距离
     * @param sportDistanceMap
     */
    fun setSportDistanceMap(sportDistanceMap: String)

    /**
     * 设置运动距离的单位
     * @param sportDistanceUnitMap
     */
    fun setSportDistanceUnitMap(sportDistanceUnitMap: String)

    /**
     * 运动速度
     * @param sportSpeedMap
     */
    fun setSportSpeedMap(sportSpeedMap: String)

    /**
     * 设置地图上的标题
     * @param sportSpeedMapTitle
     */
    fun setSportSpeedMapTitle(sportSpeedMapTitle: String)

    /**
     * 运动时间
     * @param sportTimeMap
     */
    fun setSportTimeMap(sportTimeMap: String)

    /**
     * 运动过短显示提示信息
     */
    fun showEndConfirmDialog()

    /**
     * 跳转到运动小结界面
     */
    fun toSportHistory(type: Int, sportHealth: SportHealth)

    /**
     * 绘制轨迹点
     * @param latLngBean
     * @param isEndPoint
     */
    fun addPolylineAndMove(latLngBean: LatLngBean, isEndPoint: Boolean)

    /**
     * 加载调试
     * @param latLngBean
     */
    fun loadMap(latLngBean: LatLngBean)

    /**
     * 添加当前位置的点
     * @param latLngBean
     */
    fun addCurrentMarker(latLngBean: LatLngBean)

    /**
     * 添加无效轨迹当前点
     * @param latLngBean
     */
    fun addFirstCurrentMarker(latLngBean: LatLngBean)

    /**
     * 显示提示信息
     * @param message
     */
    fun showMessage(message: String)

    /**
     * 正在连接的loading
     * @param message
     */
    fun showLoading(message: String)

    /**
     * 连接成功的loading
     */
    fun hideLoading()

    /**
     * 显示断开设备的弹框
     */
    fun showDisconnectDialog();



    /**
     * 设置运动状态
     * @param isRunning
     */
    fun setSportStatus(isRunning: Boolean)



    /**
     * 设置gps的状态描述
     * @param gpsStatusDesc
     */
    fun setGpsStatusDesc(gpsStatusDesc: String)

    /**
     * 显示还是隐藏gps的状态描述
     * @param visible
     */
    fun showGpsStatusDesc(visible: Boolean)

    /**
     * 显示gps信号
     * @param visible
     */
    fun showGpsSingle(visible: Boolean)







    /**
     * 暂停
     */
    fun pause()




    /**
     * 继续运动
     */
    fun reStart()


    /**
     * 结束运动
     */
    fun end()

            ;

    /**
     * 是否是户外运动
     */
    fun isSportOutDoor(isOutDoor: Boolean)

    /**
     * 停止定位
     */
    abstract fun stopLocation()

    /**
     * 监听运动状态
     */
    fun setSportRunListener()

    /**
     * 取消监听
     */
    fun clearListener()

    /**
     * 结束运动
     * @param isSave
     */
    fun stopRun(isSave: Boolean)


    /**
     * 通过运动类型获取运动名称
     * @param sportType
     */
    fun getSportNameByType(sportType: Int)

    /**
     * 初始化用户目标
     */
    fun initUserTarget()

    /**
     * 打开或关闭声音开关
     */
    fun toSoundOffOrOn()

    /**
     * 是否初始化
     */
    fun getInit(): Boolean

    /**
     * 重新开始运动
     */
    fun onRestoreInstanceState()
    /**
     *图片生成成功
     */
    fun screenShotSuccess()
    /**
     *睡眠管理app通知H5信息
     */
    fun sleepAppSendH5(json: JsonObject)
}
