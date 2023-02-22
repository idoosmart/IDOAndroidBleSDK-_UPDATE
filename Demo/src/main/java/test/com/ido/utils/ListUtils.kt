package test.com.ido.utils

object ListUtils {
    fun isNullOrEmpty(list: List<*>?): Boolean {
        return list == null || list.isEmpty()
    }

    fun isNotEmpty(list: List<*>?): Boolean {
        return list != null && list.isNotEmpty()
    }

    /**
     * 比较两个列表元素是否一样
     * @param list1
     * @param list2
     * the two lists should overwrite equals and hashcode
     */
    fun equals(list1: List<*>, list2: List<*>): Boolean {
        if (list1.size != list2.size) {
            return false
        }
        for (item in list1) {
            if (!list2.contains(item)) {
                return false
            }
        }
        return true
    }
}