package test.com.ido.notice

import java.io.Serializable

/**
 * @author xyb
 * @date 2021-07-20 10:32
 * @description: 手机常用联系人的对象
 */
data class PhoneDto(
    val name: String?, val telePhone: String?,
    var isCheck: Boolean, var isSelected: Boolean
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PhoneDto

        if (telePhone != other.telePhone) return false

        return true
    }

    override fun hashCode(): Int {
        return telePhone.hashCode()
    }
}
