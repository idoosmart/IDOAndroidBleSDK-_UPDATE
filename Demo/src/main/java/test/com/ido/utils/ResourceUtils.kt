package test.com.ido.utils

import test.com.ido.APP
import java.util.*

/**
 * @author tianwei
 * @date 2021/8/5
 * @time 11:33
 * 用途:
 */
object ResourceUtils {
    fun getResIdByName(name: String, type: String): Int {
        val context = APP.getAppContext()
        return context.resources.getIdentifier(name, type, context.packageName)
    }

    /**
     * 获取mipmap图片资源id
     * @param name 图片名称
     */
    fun getMipmapResId(name: String): Int {
        return getResIdByName(name, "mipmap")
    }
}