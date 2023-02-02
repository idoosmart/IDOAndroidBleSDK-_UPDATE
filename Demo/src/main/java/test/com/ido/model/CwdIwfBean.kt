package test.com.ido.model

import androidx.annotation.StringDef
import java.io.Serializable

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
    val version: Int
) : Serializable {

    data class Item(
        var bg: String,
        var fgcolor: String,
        var font: Any,
        var fontnum: Int,
        var h: Int,
        val type: String,
        var w: Int,
        val widget: String,
        var x: Int,
        var y: Int
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
