package test.com.ido.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * @author tianwei
 * @date 2021/7/28
 * @time 18:54
 * 用途:
 */
public class ExecutorDispatcher {

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    public static ExecutorDispatcher getInstance() {
        return SingleInstanceHolder.instance;
    }

    private ExecutorDispatcher() {
    }

    private static class SingleInstanceHolder {
        private final static ExecutorDispatcher instance = new ExecutorDispatcher();
    }

    public void dispatchOnMainThread(Runnable runnable) {
        if (runnable != null) {
            mHandler.post(runnable);
        }
    }

    public Future dispatch(Runnable runnable) {
        if (runnable != null) {
            return executor.submit(runnable);
        }
        return null;
    }
}
