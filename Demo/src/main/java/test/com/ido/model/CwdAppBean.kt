package test.com.ido.model

import java.io.Serializable

/**
 * @author tianwei
 * @date 2021/12/29
 * @time 17:46
 * 用途:
 */
data class CwdAppBean(
    val app: App,
    val locations: List<Location>?,
    val select: Select,
    val version: Int,
    val zipName: Int,
    val function_support: Int,
    val function_support_new: Int, //新版本标志位，为了兼容
    val time_location_support: Int,
    val time_widget_list: List<TimeWidgetItem>?,
    val function_list: List<Function>?,
    val screen_corner: Float,
    val previewRadius: Int,
    val colors: List<String>?
) : Serializable {
    data class Location(
        val type: Int, val time: List<Int>, val week: List<Int>, val day: List<Int>, val function_coordinate: List<FunctionCoordinate>?,val time_widget: List<CwdIwfBean.Item>?
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

        data class FunctionCoordinate(
            val item: List<Item>, val function: Int
        ) : Serializable {
            data class Item(
                val coordinate: List<Int>, val type: String, val widget: String
            )

            fun findItem(type: String, widget: String): Item? {
                return item.find { it.type == type && it.widget == widget }
            }

            fun findItem(function: Int, type: String, widget: String): Item? {
                if (function != this.function) return null
                return item.find { it.type == type && it.widget == widget }
            }
        }
    }

    fun getLocationValues(): List<Int>? {
        return locations?.map { it.type }
    }

    fun findLocation(location: Int): Location? {
        return locations?.find { it.type == location }
    }

    fun findFunction(function: Int): Function? {
        return function_list?.find { it.function == function }
    }

    fun getFunctionValues(): List<Int>? {
        return function_list?.map { it.function }
    }

    data class App(
        val bpp: Int, val format: String, val name: String
    ) : Serializable

    data class Select(
        var funcColorIndex: Int, var function: List<Int>?, var timeColorIndex: Int, var timeFuncLocation: Int
    ) : Serializable {
        fun getSelectFunction(): Int {
            if (function.isNullOrEmpty()) {
                return 0;
            }
            return function!![0]
        }
    }
    data class TimeWidgetItem(val type: String, val widget: String) : Serializable

    fun findTimeWidgetItem(type: String, widget: String): TimeWidgetItem? {
        return time_widget_list?.find { it.type == type && it.widget == widget }
    }

    data class Function(
        val item: List<Item>, val name: String, val function: Int
    ) {
        data class Item(
            val type: String,
            val widget: String,
            val font: String,
            val fontnum: Int,
            val support_color_set: Boolean,
            val bg: String?,
            val align: String?
        )

        fun findItem(type: String, widget: String): Item? {
            return item.find { it.type == type && it.widget == widget }
        }
    }

}