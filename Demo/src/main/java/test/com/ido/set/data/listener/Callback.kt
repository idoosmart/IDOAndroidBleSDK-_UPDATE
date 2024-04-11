package test.com.ido.set.data.listener

import androidx.annotation.NonNull

/**
 * @author tianwei
 * @date 2021/7/28
 * @time 18:47
 * 用途:异步回调
 */
interface Callback<T> {
    fun onSuccess(data: T)
    fun onFailed(@NonNull errMsg: String)
}
