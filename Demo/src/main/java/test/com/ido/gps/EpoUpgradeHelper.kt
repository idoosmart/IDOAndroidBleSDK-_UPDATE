package test.com.ido.gps

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.ido.ble.BLEManager
import com.ido.ble.LocalDataManager
import com.ido.ble.callback.DeviceParaChangedCallBack
import com.ido.ble.callback.OtherProtocolCallBack
import com.ido.ble.custom.MakeGpsFileConfig
import com.ido.ble.file.transfer.FileTransferConfig
import com.ido.ble.file.transfer.IFileTransferListener
import com.ido.ble.gps.callback.GpsCallBack
import com.ido.ble.gps.model.GPSInfo
import com.ido.ble.gps.model.GpsHotStartParam
import com.ido.ble.gps.model.GpsStatus
import com.ido.ble.protocol.model.BasicInfo
import com.ido.ble.protocol.model.DeviceChangedPara
import test.com.ido.APP
import test.com.ido.utils.DataUtils
import test.com.ido.utils.NetworkUtil
import test.com.ido.utils.download.DownloadManager
import java.io.File

/**
 * @author jiangdz
 * @date 2022/10/20
 * @time 14:08
 * 用途:EPO升级
 */
const val NETWORK_IS_UNAVAILABLE = 0;
const val DEVICE_NOT_CONNECTED = 1;
const val FILE_DONWLOAD_FAILED = 2;
const val UPGREADE_GPS_FAILED = 3;
const val DEVICE_IS_BUSY = 4;
const val EPO_FILE_FAILED = 5;
const val ICOE_EPO_FILE_TRANSFER_FAILED = 6;
const val EPO_FILE_TRANSFER_FAILED = 6;
const val DEVICE_SIDE_EPO_UPGREADE_FAILED = 7;
class EpoUpgradeHelper : GpsCallBack.IGetGpsInfoCallBack,
    DeviceParaChangedCallBack.ICallBack {
    private val TAG = "EpoUpgradeHelper"




    var isGpsStatus = false;


    //是否正在执行升级流程
    private var mUpgrading = false

    //是否正在获取GPS状态
    private var mGetGpsStatus = false

    //EPO升级连续失败最大多少次，今天就不再执行EPO升级检测流程
    private val mEpoFailedMaxCount = 3

    //EPO检测升级流程，连续失败多少次
    private var mEpoFailedCount = 0
    private var mTotalSize = 0
    private var mCurrentIndex = 0
    private val mHandler = Handler(Looper.getMainLooper())

    //获取GPS状态超时时间
    private val mGetGpsTimeOutDuration = 15 * 1000

    private var mEpoUpgradeListener: EpoUpgradeListener? = null

    //获取GPS状态超时事件
    private val mGetGpsTimeOutAction = Runnable {

    }

    private val EPO_DIR =
        APP.getAppContext().externalCacheDir?.absolutePath?.plus(File.separator)
            ?.plus("epo/")
    private val ICOE_EPO_DIR =
        APP.getAppContext().externalCacheDir?.absolutePath?.plus(File.separator)
            ?.plus("eph/")

    //三合一后的EPO文件名称
    private val AIROHA_AGPS_OFFLINE_FILENAME = "EPO.DAT"

    private val mEpoList by lazy {
        getEpoBeanList()
    }

    companion object {
        private val mInstance: EpoUpgradeHelper by lazy {
            EpoUpgradeHelper()
        }

        @JvmStatic
        fun getInstance(): EpoUpgradeHelper {
            return mInstance
        }
    }

    fun setEpoUpgradeListener(listener: EpoUpgradeListener?) {
        mEpoUpgradeListener = listener
    }

    fun startUpgradeEpo() {
//        if (mUpgrading) {
//            printAndSaveLog("已经在执行EPO检测升级流程，不用重新执行")
//            return
//        }
        if (!NetworkUtil.isNetworkAvailable(APP.getAppContext())) {
            printAndSaveLog("准备执行EPO检测升级流程，但是此时没有网络")
            mEpoUpgradeListener?.onFailed("Network is unavailable!", NETWORK_IS_UNAVAILABLE)
            return
        }
        //TODO The release version should be upgraded every 24 hours.
        //TODO The release version should be upgraded every 24 hours.
        //TODO The release version should be upgraded every 24 hours.
//        val lastUpgradeDate = DataUtils.getInstance().epoLastUpgradeTime
//        if (lastUpgradeDate > 0 && System.currentTimeMillis() - lastUpgradeDate < 24 * 60 * 60 * 1000) {
//            printAndSaveLog("今天已经检测过了EPO检查升级流程，请明天再来")
//            mEpoUpgradeListener?.onFailed("It's not time to upgrade",0)
//            return
//        }
        mUpgrading = true
//        if (isSupportGPSUpgrade()) {
//            printAndSaveLog("先获取gps信息...")
//            BLEManager.unregisterGetGpsInfoCallBack(this)
//            BLEManager.registerGetGpsInfoCallBack(this)
//            BLEManager.getGpsInfo()
//        } else {
//            printAndSaveLog("start...")
            startDownloadFile()
//        }
    }

    private fun isSupportGPSUpgrade(): Boolean {
        val functionInfo = LocalDataManager.getSupportFunctionInfo()
        return functionInfo != null && functionInfo.support_update_gps
    }

    private fun isSupportEPO(): Boolean {
        val functionInfo = LocalDataManager.getSupportFunctionInfo()
//        return functionInfo != null && functionInfo.Airoha_gps_chip
        return true
    }


    private fun startDownloadFile() {
        printAndSaveLog("开始去下载EPO资源文件")
        unregisterListener()
        val file = File(EPO_DIR)
        if (!file.exists()) {
            file.mkdirs()
        }
        mTotalSize = mEpoList.size
        mCurrentIndex = 0
        startDownloadFile(mEpoList[mCurrentIndex])
    }

    private fun startDownloadFile(epoBean: EpoBean) {
        val file = File(epoBean.filePath)
        if (file.exists() && file.isFile) file.delete()
        val deviceId = System.currentTimeMillis()
        DownloadManager.download(
            if (epoBean.fileName.contains(".pgl")) {
                epoBean.url.plus(epoBean.fileName)
            } else {
                epoBean.url.plus(deviceId)
            },
            epoBean.filePath,
            object : DownloadManager.DownloadListener {
                /**
                 * 开始下载
                 */
                override fun onDownloadStart() {
                    printAndSaveLog("开始下载EPO文件 epoBean=$epoBean")
                    mHandler.post {
                        mEpoUpgradeListener?.onDownloadStart()
                    }
                }

                /**
                 * 下载进度
                 *
                 * @param progress
                 */
                override fun onDownloadProgress(progress: Int) {
                    printAndSaveLog("EPO文件下载进度 fileName=${epoBean.fileName},progress=$progress")
                    mHandler.post {
                        mEpoUpgradeListener?.onDownloadProgress(
                            mCurrentIndex,
                            mTotalSize,
                            progress
                        )
                    }
                }

                /**
                 * 下载完成
                 *
                 * @param path
                 */
                override fun onDownloadFinish(path: String?) {
                    mHandler.post {
                        printAndSaveLog("EPO文件下载成功 epoBean=$epoBean")
                        mCurrentIndex++
                        if (mCurrentIndex < mTotalSize) {
                            startDownloadFile(mEpoList[mCurrentIndex])
                        } else {
                            mEpoUpgradeListener?.onDownloadSuccess()
                            if (BLEManager.isConnected() && isSupportEPO()) {
                                mHandler.removeCallbacks(mGetGpsTimeOutAction)
                                mHandler.postDelayed(
                                    mGetGpsTimeOutAction,
                                    mGetGpsTimeOutDuration.toLong()
                                )
                                BLEManager.unregisterGetGpsInfoCallBack(this@EpoUpgradeHelper)
                                BLEManager.registerGetGpsInfoCallBack(this@EpoUpgradeHelper)
                                mGetGpsStatus = true
                                BLEManager.getGpsStatus()
                            } else {
                                mEpoUpgradeListener?.onFailed(
                                    "Device not connected or not supported",
                                    DEVICE_NOT_CONNECTED
                                )
                                upgradeFailed()
                                printAndSaveLog("EPO文件下载完成，但是此时用户连接的设备不支持EPO升级")
                            }
                        }
                    }

                }

                /**
                 * 下载失败
                 *
                 * @param errCode
                 * @param errInfo
                 */
                override fun onDownloadFailed(errCode: Int, errInfo: String?) {
                    printAndSaveLog("EPO文件下载失败 epoBean=$epoBean,errCode=$errCode,errInfo=$errInfo")
                    mHandler.post {
                        mEpoUpgradeListener?.onFailed("Download failed!", FILE_DONWLOAD_FAILED)
                        upgradeFailed()
                    }
                }

            })
    }

    private fun getEpoBeanList(): List<EpoBean> {
        return mutableListOf(
            EpoBean(
                url = "https://elpo.airoha.com/ELPO_GR3_1.DAT?vendor=IDOO&project=J3zLAKQJ4vy_81un3vc89qcvBcfjY6GiuiZZs4gn_LM&device_id=",
                fileName = "EPO_GR_3_1.DAT",
                filePath = EPO_DIR.plus("EPO_GR_3_1.DAT")
            ),
            EpoBean(
                url = "https://elpo.airoha.com/ELPO_GAL_3.DAT?vendor=IDOO&project=J3zLAKQJ4vy_81un3vc89qcvBcfjY6GiuiZZs4gn_LM&device_id=",
                fileName = "EPO_GAL_3.DAT",
                filePath = EPO_DIR.plus("EPO_GAL_3.DAT")
            ),

            EpoBean(
                url = "https://elpo.airoha.com/ELPO_BDS_3.DAT?vendor=IDOO&project=J3zLAKQJ4vy_81un3vc89qcvBcfjY6GiuiZZs4gn_LM&device_id=",
                fileName = "EPO_BDS_3.DAT",
                filePath = EPO_DIR.plus("EPO_BDS_3.DAT")
            ),
            EpoBean(
                url = "http://starcourse.location.io/IC2MItGo7K/",
                fileName = "f1e1G3.pgl",
                filePath = ICOE_EPO_DIR.plus("f1e1G3.pgl")
            ),
            EpoBean(
                url = "http://starcourse.location.io/IC2MItGo7K/",
                fileName = "f1e1C3.pgl",
                filePath = ICOE_EPO_DIR.plus("f1e1C3.pgl")
            ),
            EpoBean(
                url = "http://starcourse.location.io/IC2MItGo7K/",
                fileName = "f1e1E3.pgl",
                filePath = ICOE_EPO_DIR.plus("f1e1E3.pgl")
            ),
            EpoBean(
                url = "http://starcourse.location.io/IC2MItGo7K/",
                fileName = "f1e1J3.pgl",
                filePath = ICOE_EPO_DIR.plus("f1e1J3.pgl")
            )
        )
    }

    /**
     *EPO升级成功
     */
    private fun upgradeSuccess() {
        mEpoFailedCount = 0
        mUpgrading = false
        mGetGpsStatus = false
        unregisterListener()
        DataUtils.getInstance().saveEPOUpgradeTime()
    }

    /**
     *EPO升级失败
     */
    private fun upgradeFailed() {
        mEpoFailedCount++
        mUpgrading = false
        mGetGpsStatus = false
        unregisterListener()
        if (mEpoFailedCount >= mEpoFailedMaxCount) {
            DataUtils.getInstance().saveEPOUpgradeTime()
        }
    }

    data class EpoBean(
        val url: String,
        val fileName: String,
        val filePath: String
    )

    private fun printAndSaveLog(message: String?) {
        if (message.isNullOrEmpty()) return
        Log.d(TAG, message)
    }

    override fun onGetGpsInfo(gpsInfo: GPSInfo?) {
        printAndSaveLog("Gps信息获取成功，$gpsInfo")
        BLEManager.unregisterGetGpsInfoCallBack(this)
        if (gpsInfo?.errCode == 8) {
            printAndSaveLog("Gps异常，不能升级epo")
            mEpoUpgradeListener?.onFailed(
                "The gps is abnormal. Upgrade the gps firmware first",
                UPGREADE_GPS_FAILED
            )
            upgradeFailed()
        } else {
            startDownloadFile()
        }
    }

    override fun onGetHotStartGpsPara(p0: GpsHotStartParam?) {
    }

    override fun onGetGpsStatus(gpsStatus: GpsStatus?) {

        printAndSaveLog("Gps状态获取成功，开始传输GPS文件,gpsStatus=$gpsStatus,mUpgrading=$mUpgrading,mGetGpsStatus=$mGetGpsStatus")
        if (mGetGpsStatus && mUpgrading) {
            if (gpsStatus != null && gpsStatus.gps_run_status == GpsStatus.STATUS_IDLE) {
                isGpsStatus = true;

            } else {
                mEpoUpgradeListener?.onFailed("Device is busy", DEVICE_IS_BUSY)
                upgradeFailed()
            }
        }
    }


    //三个文件合并成一个
    private fun makeAirohaGpsFile(filePath: String, fileName: String, outFilename: String) {
        printAndSaveLog("开始将三个文件合并成一个文件")
        val config = MakeGpsFileConfig()
        val file = File(filePath)
        if (!file.exists()) {
            file.mkdirs()
        }
        val dirPath = filePath + fileName
        val dir = File(dirPath)
        if (dir.exists()) {
            dir.delete()
        }

        config.blockSize = 1024
        config.filePath = filePath
        config.outFileName = outFilename
        BLEManager.registerOtherProtocolCallBack(object :
            OtherProtocolCallBack.ICallBack {
            override fun onSuccess(type: OtherProtocolCallBack.SettingType) {
                if (type == OtherProtocolCallBack.SettingType.GPSMAKEFILE) {
                    printAndSaveLog("EPO文件三合一成功")
                    BLEManager.unregisterOtherProtocolCallBack(this)
                }
            }

            override fun onFailed(type: OtherProtocolCallBack.SettingType) {
                if (type == OtherProtocolCallBack.SettingType.GPSMAKEFILE) {
                    printAndSaveLog("EPO文件制作失败")
                    BLEManager.unregisterOtherProtocolCallBack(this)
                    mEpoUpgradeListener?.onFailed("EPO file creation failed", EPO_FILE_FAILED)
                    upgradeFailed()
                }
            }
        })
        BLEManager.makGpsFile(config)
    }

    /**
     *开始传输ICOE EPO文件
     */
    fun startTransferIcoeEpoFile() {
        if (isGpsStatus) {
            ICOE_EPO_DIR?.let {
                makeAirohaGpsFile(
                    it,
                    "EPO.DAT",
                    AIROHA_AGPS_OFFLINE_FILENAME
                )
            }
        } else {
            mEpoUpgradeListener?.onFailed("Gps状态获取失败", 1)
        }
        BLEManager.unregisterDeviceParaChangedCallBack(this)
        BLEManager.registerDeviceParaChangedCallBack(this)
        val fileTransferConfig =
            FileTransferConfig.getDefaultUbloxAGpsFileConfig(
                ICOE_EPO_DIR.plus(
                    AIROHA_AGPS_OFFLINE_FILENAME
                ),
                object : IFileTransferListener {
                    override fun onStart() {
                        printAndSaveLog("IcoeEpo文件开始传输")
                    }

                    override fun onProgress(progress: Int) {
                        printAndSaveLog("IcoeEpo文件传输进度 progress=$progress")
                        mEpoUpgradeListener?.onTransferProgress(progress)
                    }

                    override fun onSuccess() {
                        printAndSaveLog("IcoeEpo文件传输成功")
                        mEpoUpgradeListener?.onTransferSuccess()
                    }

                    override fun onFailed(error: String?) {
                        printAndSaveLog("IcoeEpo文件传输失败 error=$error")
                        mEpoUpgradeListener?.onFailed(
                            "IcoeEpo file transfer failed",
                            ICOE_EPO_FILE_TRANSFER_FAILED
                        )
                        upgradeFailed()
                    }

                })
        fileTransferConfig.firmwareSpecName = "EPO.DAT"
        mEpoUpgradeListener?.onTransferStart()
        BLEManager.startTranCommonFile(fileTransferConfig)
    }

    /**
     *开始传输EPO文件
     */
    fun startTransferEpoFile() {
        if (isGpsStatus) {
            EPO_DIR?.let { makeAirohaGpsFile(it, "EPO.DAT", AIROHA_AGPS_OFFLINE_FILENAME) }
        } else {
            mEpoUpgradeListener?.onFailed("Gps状态获取失败", 1)
        }
        BLEManager.unregisterDeviceParaChangedCallBack(this)
        BLEManager.registerDeviceParaChangedCallBack(this)
        val fileTransferConfig =
            FileTransferConfig.getDefaultUbloxAGpsFileConfig(
                EPO_DIR.plus(
                    AIROHA_AGPS_OFFLINE_FILENAME
                ),
                object : IFileTransferListener {
                    override fun onStart() {
                    }

                    override fun onProgress(progress: Int) {
                        printAndSaveLog("EPO文件传输进度 progress=$progress")
                        mEpoUpgradeListener?.onTransferProgress(progress)

                    }

                    override fun onSuccess() {
                        printAndSaveLog("EPO文件传输成功")
                        mEpoUpgradeListener?.onTransferSuccess()
                    }

                    override fun onFailed(error: String?) {
                        printAndSaveLog("EPO文件传输失败 error=$error")
                        mEpoUpgradeListener?.onFailed(
                            "EPO file transfer failed",
                            EPO_FILE_TRANSFER_FAILED
                        )
                        upgradeFailed()
                    }

                })
        fileTransferConfig.firmwareSpecName = "EPO.DAT"
        mEpoUpgradeListener?.onTransferStart()
        BLEManager.startTranCommonFile(fileTransferConfig)
    }

    override fun onChanged(deviceChangedPara: DeviceChangedPara?) {
        if (deviceChangedPara == null) return
        when (deviceChangedPara.dataType) {
            //EPO升级失败
            43 -> {
                printAndSaveLog("EPO文件升级失败 deviceChangedPara=$deviceChangedPara")
                mEpoUpgradeListener?.onFailed(
                    "Device side EPO upgrade failed",
                    DEVICE_SIDE_EPO_UPGREADE_FAILED
                )
                upgradeFailed()
            }
            //EPO升级成功
            44 -> {
                printAndSaveLog("EPO文件升级成功 deviceChangedPara=$deviceChangedPara")
                mEpoUpgradeListener?.onSuccess()
                upgradeSuccess()
            }
        }
    }

    fun unregisterListener() {
        BLEManager.unregisterDeviceParaChangedCallBack(this)
        BLEManager.unregisterGetGpsInfoCallBack(this)
        mHandler.removeCallbacks(mGetGpsTimeOutAction)
    }

    /**
     *是否正在执行EPO升级流程
     */
    fun isUpgrading() = mUpgrading
}

interface EpoUpgradeListener {
    fun onDownloadStart()
    fun onDownloadProgress(index: Int, totalCount: Int, progress: Int)
    fun onDownloadSuccess()
    fun onPackaging()
    fun onTransferStart()
    fun onTransferProgress(progress: Int)
    fun onTransferSuccess()
    fun onFailed(errorMsg: String, code: Int)
    fun onSuccess()
}