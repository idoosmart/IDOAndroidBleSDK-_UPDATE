package com.ido.jielidial;

import androidx.annotation.WorkerThread;

import com.ido.jielidial.model.DialConfig;

/**
 * @author tianwei
 * @date 2024/3/18
 * @time 11:37
 * 用途:
 */
public abstract class DialMaker {
    protected String TAG = getClass().getSimpleName();

    /**
     * 制作照片表盘bin，应该在子线程执行
     * @param config
     * @return
     */
    @WorkerThread
    public boolean makePhotoDial(DialConfig config) {
        if (checkConfig(config)) {
            return make(config);
        }
        return false;
    }

    protected boolean checkConfig(DialConfig config) {
        return true;
    }

    protected abstract boolean make(DialConfig config);
}
