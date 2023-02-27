package test.com.ido.model

import java.io.Serializable

data class SportTypeBean(
    val type: Int,//运动类型唯一标识
    val name: String,//运动类型名称
    val iconResId: Int,//运动类型icon资源id
    var iconFlag: Int,//是否有图标
    var available: Boolean = false//是否可用，运动数据视图种使用
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SportTypeBean

        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        return type
    }
}
