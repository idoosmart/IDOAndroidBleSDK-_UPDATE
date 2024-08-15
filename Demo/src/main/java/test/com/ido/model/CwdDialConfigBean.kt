package test.com.ido.model

/**
 * @author WYong
 * @date 2023/11/15
 * @description:
 */
data class CwdDialConfigBean(
        val project: Int,
        val version: Int,
        val name: String,
        val id: Int,
        val dial_path : String?,
        val postfix : String?,
        val dial_data: MutableList<DialDataBean>
)

data class DialDataBean(val user: Int, val wtype: Int, val xdata: Int, val dtype: Int, var x: Int, var y: Int, val file: String, val font_size: Int, var r: Int,
                        var g: Int, var b: Int)

