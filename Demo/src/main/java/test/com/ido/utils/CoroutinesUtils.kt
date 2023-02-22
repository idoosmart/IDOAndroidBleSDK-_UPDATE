package test.com.ido.utils

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.*

/**
 * @author tianwei
 * @date 2021/8/28
 * @time 12:16
 * 用途:协程工具类
 */
object CoroutinesUtils {
    fun delay(timeInMill: Long, func: () -> Unit): Job {
        return GlobalScope.launch(Dispatchers.IO) {
            delay(timeInMill)
            func()
        }
    }

    fun <R> debounce(
        timeInMill: Long,
        onOffer: (producerScope: ProducerScope<R>) -> Unit,
        onClose: (() -> Unit)? = null,
        onCatch: ((Throwable) -> Unit)? = null,
        todo: (R) -> Unit
    ): Job {
        return GlobalScope.launch(Dispatchers.IO) {
            callbackFlow<R> {
                onOffer(this)
                awaitClose { onClose?.let { it() } }
            }.debounce(timeInMill)
                .catch { msg -> onCatch?.let { it(msg) } }
                .flowOn(Dispatchers.IO)
                .collect {
                    todo(it)
                }
        }
    }

    @JvmStatic
    fun <R> debounce(timeInMill: Long, onOffer: (producerScope: ProducerScope<R>) -> Unit, todo: (R) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            callbackFlow<R> {
                onOffer(this)
                awaitClose {}
            }.debounce(timeInMill)
                .catch { }
                .flowOn(Dispatchers.Main)
                .collect {
                    todo(it)
                }
        }
    }
}