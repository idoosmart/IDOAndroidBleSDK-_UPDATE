package com.ido.life.util

import android.graphics.BitmapFactory
import android.text.TextUtils
import android.util.Log
import android.util.Pair
import android.view.Gravity
import android.widget.RelativeLayout
import androidx.annotation.IntDef
import androidx.annotation.StringDef
import androidx.annotation.WorkerThread
import com.ido.ble.BLEManager
import com.ido.ble.LocalDataManager
import com.ido.ble.bluetooth.device.BLEDevice
import com.ido.ble.callback.DeviceParaChangedCallBack
import com.ido.ble.protocol.model.BasicInfo
import com.ido.ble.protocol.model.DeviceChangedPara
import com.ido.life.constants.WallpaperDialConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import test.com.ido.APP
import test.com.ido.utils.FileUtil
import test.com.ido.utils.FileUtilLib.sizeOfDirectory
import test.com.ido.utils.ResourceUtil
import test.com.ido.utils.ResourceUtils
import test.com.ido.utils.ZipUtils
import java.io.File
import java.util.*

/**
 * @author tianwei
 * @date 2021/10/18
 * @time 9:49
 * 用途:壁纸表盘管理类
 */
object WallpaperDialManager : DeviceParaChangedCallBack.ICallBack {
    private const val WALLPAPER_DIAL_PREFIX = "wallpaper_dial_function_"
    private const val WALLPAPER_DIAL_ICON_PREFIX =
        "icon_wallpaper_dial_function_"

    /////////////////////////云壁纸表盘
    private const val BMP_FORMAT_16 = 5
    private const val BMP_FORMAT_32 = 8

    private const val CWD_PACK_SUFFIX = ".zip"

    private const val ROOT_DIR = "/cubitt/wallpaper_defined/"

    private const val FILE_DIR_NAME_FUNCTION = "watchFileFunction"

    //固件设备的json文件名称
    private const val CWD_DEVICE_JSON = "iwf.json"

    //配置文件app.json
    private const val CWD_APP_JSON = "app.json"

    //背景图
    private const val CWD_DEVICE_BACKGROUND_IMAGE = "bg.bmp"

    //预览图
    private const val CWD_DEVICE_PREVIEW_IMAGE = "preview.png"

    //保存zip路径
    private const val ZIP_DIR = "zip/"

    //临时目录，存放临时文件
    private const val TEMP_DIR = "temp/"

    //用来预览的图片位置
    private const val PREVIEW_IMAGE_DIR = "images/"

    //临时bg.png
    const val TEMP_BG_PNG = "bg.png"
    const val TEMP_BACKGROUND_BIN = "background.bin"
    //临时bg.bmp
    const val TEMP_BG_BMP = "bg.bmp"

    //临时preview.png
    const val TEMP_PREVIEW_IMAGE = "preview.png"

    //缩放后的临时preview.png，用于生产bmp
    const val TEMP_ZOOMED_PREVIEW_IMAGE = "zoom_preview.png"

    //临时preview.bmp
    const val TEMP_PREVIEW_IMAGE_BMP = "preview.bmp"

    const val TEMP_PREVIEW_IMAGE_BIN= "preview.bin"

    //临时zip包
    private const val TEMP_NAME = "temp"

    //是否使用bmp做背景图
    private const val IS_SUPPORT_BMP = true

    //蓝牙正在通话
    var isTelephone = false

    const val TAG = "WallpaperDialManager"

    /**
     * 当前设备信息
     */
    private var mCurrentDevice: BLEDevice? = null

    /**
     * 当前正在操作的照片云表盘
     */
    private var mOperateDialName: String? = null

    @JvmStatic
    fun getDeviceId() = mCurrentDevice?.mDeviceId ?: 0

    @JvmStatic
    fun getDeviceMac() = mCurrentDevice?.mDeviceAddress ?: ""


    fun init() {
        BLEManager.registerDeviceParaChangedCallBack(this)
    }

    /**
     * 是否可以改变电池颜色
     */
    @JvmStatic
    fun ifChangeBatteryColor() = false


    /**
     * 获取功能名称
     */
    @JvmStatic
    fun getFunctionName(function: Int): String {
        val nameRes =
            ResourceUtil.getStringResId("${WALLPAPER_DIAL_PREFIX}$function")
        return if (nameRes > 0) ResourceUtil.getString(nameRes) else ""
    }

    /**
     * 获取功能图标
     */
    @JvmStatic
    fun getFunctionIcon(function: Int): Int {
        var function = function
        if (function <= 0) {
            function = WallpaperDialConstants.WidgetFunction.WEEK_DATE;
        }
        return ResourceUtils.getMipmapResId("${WALLPAPER_DIAL_ICON_PREFIX}${function}")
    }

    /**
     * 获取布局规则
     */
    @JvmStatic
    fun getLayoutRulesByLocation(@WallpaperDialConstants.WidgetLocation location: Int): IntArray {
        var rules =
            intArrayOf(RelativeLayout.ALIGN_END, RelativeLayout.ALIGN_TOP)
        when (location) {
            WallpaperDialConstants.WidgetLocation.CENTER_TOP -> rules =
                intArrayOf(
                    RelativeLayout.ALIGN_PARENT_TOP,
                    RelativeLayout.CENTER_HORIZONTAL
                )

            WallpaperDialConstants.WidgetLocation.CENTER -> rules = intArrayOf(
                RelativeLayout.CENTER_IN_PARENT
            )

            WallpaperDialConstants.WidgetLocation.CENTER_BOTTOM -> rules =
                intArrayOf(
                    RelativeLayout.ALIGN_PARENT_BOTTOM,
                    RelativeLayout.CENTER_HORIZONTAL
                )

            WallpaperDialConstants.WidgetLocation.LEFT_TOP -> rules =
                intArrayOf(
                    RelativeLayout.ALIGN_PARENT_LEFT,
                    RelativeLayout.ALIGN_PARENT_TOP
                )

            WallpaperDialConstants.WidgetLocation.LEFT_BOTTOM -> rules =
                intArrayOf(
                    RelativeLayout.ALIGN_PARENT_LEFT,
                    RelativeLayout.ALIGN_PARENT_BOTTOM
                )

            WallpaperDialConstants.WidgetLocation.RIGHT_TOP -> rules =
                intArrayOf(
                    RelativeLayout.ALIGN_PARENT_RIGHT,
                    RelativeLayout.ALIGN_PARENT_TOP
                )

            WallpaperDialConstants.WidgetLocation.RIGHT_BOTTOM -> rules =
                intArrayOf(
                    RelativeLayout.ALIGN_PARENT_RIGHT,
                    RelativeLayout.ALIGN_PARENT_BOTTOM
                )
        }
        return rules
    }

    /**
     * 获取布局规则
     */
    @JvmStatic
    fun getLayoutGravityByLocation(@WallpaperDialConstants.WidgetLocation location: Int): Int {
        return when (location) {
            WallpaperDialConstants.WidgetLocation.CENTER_TOP -> Gravity.CENTER_HORIZONTAL or Gravity.TOP
            WallpaperDialConstants.WidgetLocation.CENTER -> Gravity.CENTER
            WallpaperDialConstants.WidgetLocation.CENTER_BOTTOM -> Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
            WallpaperDialConstants.WidgetLocation.LEFT_TOP -> Gravity.LEFT or Gravity.TOP
            WallpaperDialConstants.WidgetLocation.LEFT_BOTTOM -> Gravity.LEFT or Gravity.BOTTOM
            WallpaperDialConstants.WidgetLocation.RIGHT_TOP -> Gravity.RIGHT or Gravity.TOP
            WallpaperDialConstants.WidgetLocation.RIGHT_BOTTOM -> Gravity.RIGHT or Gravity.BOTTOM
            else -> Gravity.RIGHT or Gravity.TOP
        }
    }

    @JvmStatic
    fun getGravityByLocation(@WallpaperDialConstants.WidgetLocation location: Int): Int {
        return when (location) {
            WallpaperDialConstants.WidgetLocation.CENTER_TOP -> Gravity.TOP or Gravity.CENTER_HORIZONTAL
            WallpaperDialConstants.WidgetLocation.CENTER -> Gravity.CENTER
            WallpaperDialConstants.WidgetLocation.CENTER_BOTTOM -> Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            WallpaperDialConstants.WidgetLocation.LEFT_TOP -> Gravity.LEFT or Gravity.TOP
            WallpaperDialConstants.WidgetLocation.RIGHT_TOP -> Gravity.RIGHT or Gravity.TOP
            WallpaperDialConstants.WidgetLocation.RIGHT_BOTTOM -> Gravity.RIGHT or Gravity.BOTTOM
            WallpaperDialConstants.WidgetLocation.LEFT_BOTTOM -> Gravity.LEFT or Gravity.BOTTOM
            else -> Gravity.RIGHT or Gravity.TOP
        }
    }

    /**
     * 壁纸表盘颜色集合
     */
    @JvmStatic
    fun getWallpaperColorList(): List<String> {
        val mColorList = ArrayList<String>()
        if (mColorList.isEmpty()) {
            val values = WallpaperDialConstants.WidgetColor.values()
            for (value in values) {
                mColorList.add(value.color)
            }
        }
        return mColorList
    }

    /**
     * 获取当前设备的mac
     */
    private val deviceUniqueCode: String
        get() = mCurrentDevice?.mDeviceAddress ?: ""

    /**
     * 是否支持bmp
     */
    @JvmStatic
    fun isUseBmpBg() = IS_SUPPORT_BMP

    /**
     * 背景图名称
     */
    @JvmStatic
    fun getCwdBgImageName() = if (isUseBmpBg()) TEMP_BG_BMP else TEMP_BG_PNG

    /**
     * 背景图名称
     */
    @JvmStatic
    fun getCwdBgImageName(name: String) =
        if (name.endsWith(
                BackgroundType.BMP,
                true
            )
        ) TEMP_BG_BMP else TEMP_BG_PNG

    /**
     * 根目录
     * @example /Veryfit/wallpaper_defined
     */
    @JvmStatic
    fun getCwdBaseDir() =
        APP.getAppContext().filesDir.path + ROOT_DIR

    /**
     * 临时目录
     */
    @JvmStatic
    fun getTempCwdBaseDir() = getCwdBaseDir() + TEMP_DIR

    /**
     * 临时表盘目录
     */
    @JvmStatic
    fun getTempCwdDir() = getTempCwdBaseDir() + TEMP_NAME + File.separator

    /**
     * 获取临时zip包
     */
    @JvmStatic
    fun getTempZipPath() =
        getTempCwdBaseDir() + ZIP_DIR + TEMP_NAME + CWD_PACK_SUFFIX

    /**
     * 临时bg.png
     */
    @JvmStatic
    fun getTempBgImagePath() = getTempCwdBaseDir() + TEMP_BG_PNG
    /**
     * 临时preview.bin
     */
    @JvmStatic
    fun getTempCwdPreviewImagePathByBin() = getTempCwdDir() + TEMP_PREVIEW_IMAGE_BIN


    /**
     * 临时bg.bmp
     */
    @JvmStatic
    fun getTempCwdBgImagePath(fileName: String) =
        getTempCwdDir() + fileName

    /**
     * 临时preview.png
     */
    @JvmStatic
    fun getTempCwdPreviewImagePath() = getTempCwdBaseDir() + TEMP_PREVIEW_IMAGE_BMP
    /**
     * 临时preview.png
     */
    @JvmStatic
    fun getTempCwdZoomedPreviewImagePath() =
        getTempCwdBaseDir() + TEMP_ZOOMED_PREVIEW_IMAGE

    /**
     * 临时表盘包预览图
     */
    @JvmStatic
    fun getTempCwdPreviewImagePath(name: String) = getTempCwdDir() + name


    @JvmStatic
    fun isTempCwdExist(): Boolean {
        return File(getTempCwdDir()).exists()
    }

    /**
     * 临时表盘大小
     */
    @JvmStatic
    fun getTempCwdSize(): Long {
        val cwdDir = File(getTempCwdDir())
        if (cwdDir.exists()) {//临时表盘文件存在
            return sizeOfDirectory(cwdDir)
        }
        return 0L
    }

    /**
     * 删除临时表盘包
     */
    @JvmStatic
    fun deleteTempCwdPack(otaFaceName: String) {
        try {
            val cwdDir = File(getTempCwdDir())
            if (cwdDir.exists() && cwdDir.isDirectory) {
                FileUtil.deleteDirectory(cwdDir)
            }
        } catch (e: Exception) {
        }
    }

    /**
     * 准备临时表盘资源包
     */
    @JvmStatic
    fun prepareDeviceTempCwdResource(mDialOtaFaceName: String) =
        copyDir(getDeviceCwdPackPath(mDialOtaFaceName), getTempCwdDir())

    /**
     * 设备的云壁纸表盘的json文件路径
     */
    @JvmStatic
    fun getDeviceTempJsonPath() =
        getTempCwdDir() + File.separator + CWD_DEVICE_JSON

    /**
     * 压缩临时云壁纸表盘包到临时目录
     */
    @JvmStatic
    fun packTempCwdPackage(otaFaceName: String): String {
        try {
            val zipPath = getTempZipPath()
            ZipUtils.zip(
                getTempCwdDir(),
                getTempCwdBaseDir() + ZIP_DIR,
                TEMP_NAME + CWD_PACK_SUFFIX
            )
            return zipPath
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    /**
     * 解压缩临时表盘包
     */
    @JvmStatic
    fun unpackTempCwdPackage(otaFaceName: String): Boolean {
        try {
            var result = ZipUtils.unpackCopyZip(
                getTempCwdDir(),
                getDeviceCwdPackPath(otaFaceName)
            )
            if (result) {
                val file = File(getTempCwdBaseDir() + otaFaceName)
                if (file.exists()) {
                    val newFile = File(file.parent + File.separator + TEMP_NAME)
                    Log.d(TAG, "重命名：${file.name} -> ${newFile.name}")
                    result = file.renameTo(newFile)
                }
            }
            return result
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * 解压缩临时表盘包
     */
    @JvmStatic
    fun unpackCwdPackage(otaFaceName: String): Boolean {
        try {
            val path = getDeviceCwdBaseDir()
            var result =
                ZipUtils.unpackCopyZip(path, getCwdPackFilePath(otaFaceName))
//            if (result) {
//                val file = File(path + otaFaceName)
//                if (file.exists()) {
//                    val newFile = File(file.parent + File.separator + TEMP_NAME)
//                    CommonLogUtil.printAndSave("重命名：${file.name} -> ${newFile.name}")
//                    result = file.renameTo(newFile)
//                }
//            }
            return result
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * 获取云壁纸表盘压缩包根路径
     */
    @JvmStatic
    fun getCwdPackDir() = getCwdBaseDir() + ZIP_DIR

    /**
     * 获取云壁纸表盘压缩包路径
     */
    @JvmStatic
    fun getCwdPackFilePath(otaFaceName: String) =
        getCwdBaseDir() + ZIP_DIR + otaFaceName + CWD_PACK_SUFFIX

    /**
     * 获取云壁纸表盘压缩包文件对象
     */
    @JvmStatic
    fun getCwdPackFile(otaFaceName: String) =
        File(getCwdPackFilePath(otaFaceName))

    /**
     * 判断云壁纸表盘压缩包是否存在
     */
    @JvmStatic
    fun isCwdPackFileExist(otaFaceName: String) = try {
        File(getCwdPackFilePath(otaFaceName)).exists()
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }

    /**
     * 删除云壁纸表盘压缩包
     */
    @JvmStatic
    fun deleteCwdPackFile(otaFaceName: String) = try {
        val packFile = File(getCwdPackFilePath(otaFaceName))
        if (packFile.exists()) {
            packFile.delete()
        }
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }

    /**
     * 设备云壁纸表盘解压后存放的根目录
     * @example /Veryfit/wallpaper_defined/336/watchFileFunction/
     */
    @JvmStatic
    fun getDeviceCwdBaseDir() =
        getCwdBaseDir() + deviceUniqueCode + File.separator + FILE_DIR_NAME_FUNCTION + File.separator

    /**
     * 获取设备的云壁纸表盘包的外层目录
     * @example  /Veryfit/wallpaper_defined/336/watchFileFunction/watchzh1/xx
     */
    @JvmStatic
    fun getDeviceCwdParentDir(otaFaceName: String) =
        getDeviceCwdBaseDir() + otaFaceName + File.separator

    /**
     * 获取设备的云壁纸表盘包
     * @example  /Veryfit/wallpaper_defined/336/watchFileFunction/watchzh1/watchzh1.zip
     */
    @JvmStatic
    fun getDeviceCwdPackPath(otaFaceName: String) =
        getDeviceCwdParentDir(otaFaceName) + otaFaceName + CWD_PACK_SUFFIX

    /**
     * 删除设备的云壁纸表盘文件
     */
    @JvmStatic
    fun deleteDeviceCwdFile(otaFaceName: String) = try {
        val cwdDir = File(getDeviceCwdParentDir(otaFaceName))
        if (cwdDir.exists() && cwdDir.isDirectory) {
            FileUtil.deleteDirectory(cwdDir)
        }
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }

    /**
     * 清理表盘包
     */
    @WorkerThread
    @JvmStatic
    fun clearCwd(otaFaceName: String): Boolean {
        Log.d(
            TAG,
            "clearCwd：deviceId = $deviceUniqueCode, otaFaceName = $otaFaceName"
        )
        val deleteDeviceCwdFileResult = deleteDeviceCwdFile(otaFaceName)
        Log.d(TAG, "deleteDeviceCwdFile：$deleteDeviceCwdFileResult")
        val deleteCwdPackFileResult = deleteCwdPackFile(otaFaceName)
        Log.d(TAG, "deleteCwdPackFile：$deleteCwdPackFileResult")
        return deleteDeviceCwdFileResult && deleteCwdPackFileResult
    }

    /**
     * 判断设备的云表盘是否存在
     */
    @JvmStatic
    fun isDeviceCwdDirExist(otaFaceName: String) = try {
        File(getDeviceCwdParentDir(otaFaceName)).exists()
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }

    /**
     * 判断设备的云壁纸表盘包是否存在
     */
    @JvmStatic
    fun isDeviceCwdPackExist(otaFaceName: String) = try {
        File(getDeviceCwdPackPath(otaFaceName)).exists()
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }

    /**
     * 设备的表盘配置app.json
     */
    @JvmStatic
    fun getAppJsonPath(otaFaceName: String) =
        getDeviceCwdParentDir(otaFaceName) + File.separator + CWD_APP_JSON

    /**
     * 设备的云壁纸表盘的背景图路径
     * @example  /Veryfit/wallpaper_defined/336/watchFileFunction/watchzh1/images/bg.png
     */
    @JvmStatic
    fun getDeviceBackgroundImagePath(otaFaceName: String): String {
        val path =
            getDeviceCwdParentDir(otaFaceName) + PREVIEW_IMAGE_DIR + TEMP_BG_PNG
        return if (File(path).exists()) {
            path
        } else {
            ""
        }
    }

    /**
     * 设备的云壁纸表盘的预览图路径
     */
    @JvmStatic
    fun getDevicePreviewImagePath(otaFaceName: String): String {
        val path =
            getDeviceCwdParentDir(otaFaceName) + PREVIEW_IMAGE_DIR + CWD_DEVICE_PREVIEW_IMAGE
        return if (File(path).exists()) {
            path
        } else {
            ""
        }
    }

    /**
     * 获取云壁纸表盘文件解压后的大小
     */
    @JvmStatic
    fun getDeviceDialSize(otaFaceName: String): Long {
        if (isDeviceCwdDirExist(otaFaceName)) {//表盘文件存在
            return sizeOfDirectory(File(getDeviceCwdPackPath(otaFaceName)))
        }
        return 0
    }

    /**
     * 拷贝目录
     */
    @JvmStatic
    fun copyDir(srcDir: String, desDir: String) =
        FileUtil.copyFolder(srcDir, desDir)

    /**
     * 拷贝文件
     */
    @JvmStatic
    fun copyFile(src: String, des: String) = FileUtil.copyFile(src, des)

    /**
     * 删除文件
     */
    @JvmStatic
    fun deleteFile(path: String) {
        FileUtil.deleteFile(path)
    }

    /**
     * 转换bmp
     */
    @JvmStatic
    fun replaceCwrBgImageWithTemp(
        src: String,
        des: String,
        isUseBmpBg: Boolean
    ): Boolean {
        try {
            if (isUseBmpBg) {
                return convertImage2Bmp(src, des)
            }
            val file = File(src)
            if (file.exists()) {
                copyFile(src, des)
                return true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * 转换bmp
     */
    @JvmStatic
    fun replaceCwrPreviewImageWithTemp(
        src: String,
        des: String,
        isUseBmpBg: Boolean
    ): Boolean {
        try {
            if (isUseBmpBg) {
                return convertImage2Bmp(src, des)
            }
            val file = File(src)
            if (file.exists()) {
                copyFile(src, des)
                return true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * 转换bmp
     */
    @JvmStatic
    fun convertImage2Bmp(src: String, des: String): Boolean {
        try {
            val file = File(src)
            if (file.exists()) {
//                val bitmap = BitmapFactory.decodeFile(src)
//                AndroidBmpUtil.save(bitmap, des)
//                return true
                val desFile = File(des)
                if (!desFile.exists()) {
                    desFile?.parentFile?.mkdirs()
                }
                return BLEManager.png2Bmp(src, des, BMP_FORMAT_16) == 0
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * 获取图片大小
     */
    @JvmStatic
    fun getImageSize(imagePath: String): Pair<Int, Int>? {
        try {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(imagePath, options)
            options.inSampleSize = 1
            options.inJustDecodeBounds = false;
            return Pair(options.outWidth, options.outHeight)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 16进制颜色字符串转换成rgb
     * @param hexStr
     * @return rgb
     */
    @JvmStatic
    fun hex2RGB(hexStr: String?): IntArray? {
        if (hexStr != null && "" != hexStr && hexStr.length == 7) {
            val rgb = IntArray(3)
            rgb[0] = Integer.valueOf(hexStr.substring(1, 3), 16)
            rgb[1] = Integer.valueOf(hexStr.substring(3, 5), 16)
            rgb[2] = Integer.valueOf(hexStr.substring(5, 7), 16)
            return rgb
        }
        return null
    }

    /**
     * 颜色值转16进制
     */
    @JvmStatic
    fun colorTo16(color: String): String {
        //带透明度
        val hasAlpha = color.replace("#", "").toLong(16) > "FFFFFF".toLong(16)
        return if (hasAlpha) {
            color.replace("#", "0x")
        } else {
            color.replace("#", "0xFF")
        }
    }

    /**
     * 颜色值转16进制
     */
    @JvmStatic
    fun colorTo16Long(color: String): Long {
        //带透明度
        return color.replace("#", "").toLong(16)
    }

    override fun onChanged(deviceChangedPara: DeviceChangedPara?) {
        if (deviceChangedPara != null) {
        }
    }

}

@Target(
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.FIELD,
    AnnotationTarget.FUNCTION
)
@StringDef(
    CustomFaceType.CUSTOM_PHOTO,
    CustomFaceType.CUSTOM_FIXED_PHOTO
)
@Retention(AnnotationRetention.SOURCE)
annotation class CustomFaceType {
    companion object {
        //自定义壁纸表盘
        const val CUSTOM_PHOTO = "CUSTOM_PHOTO"

        //照片固定云表盘
        const val CUSTOM_FIXED_PHOTO = "CUSTOM_FIXED_PHOTO"
    }
}

@Target(
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.FIELD,
    AnnotationTarget.FUNCTION
)
@StringDef(
    BackgroundType.PNG,
    BackgroundType.BMP
)
@Retention(AnnotationRetention.SOURCE)
annotation class BackgroundType {
    companion object {
        const val PNG = ".png"
        const val BMP = ".bmp"
    }
}


@Target(
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.FIELD,
    AnnotationTarget.FUNCTION
)
@IntDef(
    FunctionGroupType.HEALTH,
    FunctionGroupType.SPORTS,
    FunctionGroupType.TOOLS
)
@Retention(AnnotationRetention.SOURCE)
annotation class FunctionGroupType {
    companion object {
        const val HEALTH = 1//健康
        const val SPORTS = 2//运动
        const val TOOLS = 5//工具
    }
}