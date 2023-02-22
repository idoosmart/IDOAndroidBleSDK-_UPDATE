package test.com.ido.model

import java.io.Serializable

data class ContactBean(val name: String, val telePhone: String) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ContactBean

        if (name != other.name) return false
        if (telePhone != other.telePhone) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + telePhone.hashCode()
        return result
    }

    override fun toString(): String {
        return "ContactBean(name='$name', telePhone='$telePhone')"
    }


}
