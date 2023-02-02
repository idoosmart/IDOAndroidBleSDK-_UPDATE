package test.com.ido.model

import com.ido.life.constants.WallpaperDialConstants
import java.io.Serializable

/**
 * @author tianwei
 * @date 2021/12/29
 * @time 17:46
 * 用途:
 */
data class CwdAppBean(
    val app: App,
    val locations: List<Location>,
    val select: Select = Select(
        0,
        listOf(WallpaperDialConstants.WidgetFunction.STEP),
        0,
        WallpaperDialConstants.WidgetLocation.RIGHT_TOP
    ),
    val version: Int,
    val zipName: Int,
    val function_support: Int,
    val time_location_support: Int,
    val screen_corner: Float
) : Serializable {
    data class Location(
        val type: Int,
        val time: List<Int>,
        val week: List<Int>,
        val day: List<Int>,
        val func: List<Int>
    ) : Serializable {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Location

            if (type != other.type) return false

            return true
        }

        override fun hashCode(): Int {
            return type
        }
    }

    data class App(
        val bpp: Int,
        val format: String,
        val name: String
    ) : Serializable

    data class Select(
        var funcColorIndex: Int,
        var function: List<Int>,
        var timeColorIndex: Int,
        var timeFuncLocation: Int
    ) : Serializable
}