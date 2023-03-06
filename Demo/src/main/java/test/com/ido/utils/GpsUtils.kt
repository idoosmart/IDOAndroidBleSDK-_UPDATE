package test.com.ido.utils

/**
 * @author tianwei
 * @date 2023/3/6
 * @time 14:48
 * 用途:
 */
object GpsUtils {
    /**
     *将从固件端获取到的GPS版本号转换成三级版本号
     */
    @JvmStatic
    fun getGpsVersion(gpsVersion: Int): String {
        var version = ""
        if (gpsVersion > 0) {
            //低8位
            val firstCode = gpsVersion and 255
            val secondCode = gpsVersion shr 8 and 255
            val thirdCode = gpsVersion shr 16 and 255
            if (thirdCode > 0 || secondCode > 0) {
                version =
                    "%d.%02d.%02d".format(thirdCode, secondCode, firstCode)
            } else if (firstCode > 0) {
                version = "${firstCode}.00.00"
            }
        }
        return version
    }
}