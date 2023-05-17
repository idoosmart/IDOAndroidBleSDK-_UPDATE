package test.com.ido.model

import android.text.TextUtils
import androidx.annotation.StringDef
import com.ido.life.util.BackgroundType
import java.io.Serializable
import java.util.Locale

/**
 * @author tianwei
 * @date 2021/12/28
 * @time 15:00
 * 用途:云壁纸表盘的iwf.json
 */
data class CwdIwfBean(
    val author: String,
    val battery: Boolean,
    val bluetooth: Boolean,
    val description: String,
    val disturb: Boolean,
    val item: List<Item>,
    val name: String,
    val preview: String,
    val compress: String,
    val version: Int
) : Serializable {

    data class Item(
        val widget: String,
        val type: String,
        var x: Int,
        var y: Int,
        var w: Int,
        var h: Int,
        var bg: String?,
        var align:String?,
        var bgcolor: String?,
        var fgcolor: String?,
        var fgrender: String?,
        var font: String?,
        var fontnum: Int?
    ) : Serializable {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Item

            if (type != other.type) return false

            return true
        }

        override fun hashCode(): Int {
            return type.hashCode()
        }
    }

    fun getBgItem(): Item? {
        return item.find { it.type == "icon" }
    }

    fun useBmpBg(): Boolean {
        val bg: String = getBgItem()?.bg ?: ""
        if (!TextUtils.isEmpty(bg)) {
            return bg.lowercase(Locale.getDefault()).endsWith(BackgroundType.BMP)
        }
        return false
    }
}

@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD, AnnotationTarget.FUNCTION)
@StringDef(IwfItemType.TIME, IwfItemType.WEEK, IwfItemType.DAY, IwfItemType.ICON)
@Retention(AnnotationRetention.SOURCE)
annotation class IwfItemType {
    companion object {
        const val TIME = "time"
        const val WEEK = "week"
        const val DAY = "day"
        const val ICON = "icon"
    }
}
