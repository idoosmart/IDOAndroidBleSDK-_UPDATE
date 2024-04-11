package com.ido.jielidial;

import android.text.TextUtils;
import android.util.Log;

import com.ido.jielidial.model.DialConfig;
import com.ido.jielidial.model.JieliDialConfig;
import com.ido.jielidial.utils.CustomDialUtils;

import java.io.File;

/**
 * @author tianwei
 * @date 2024/3/15
 * @time 17:50
 * 用途: jieli照片表盘设置，负责组装表盘bin文件，将照片、预览图（背景图和时间组件上颜色后叠加而成）数据写入到bin文件
 */
public class JieliDialMaker extends DialMaker {

    @Override
    protected boolean make(DialConfig config) {
        return CustomDialUtils.toDialFile(config.getTargetFilePath(), config.getBgPath(), config.getPreviewPath(), config.getTimeColor(), ((JieliDialConfig) config).getBaseBinPath());
    }

    @Override
    protected boolean checkConfig(DialConfig config) {
        if (config == null) {
            Log.e(TAG, "config cannot be null!");
            return false;
        }

        if (!(config instanceof JieliDialConfig)) {
            Log.e(TAG, "config type mismatch, pls use JeliDialConfig");
            return false;
        }

        if (TextUtils.isEmpty(config.getBgPath())) {
            Log.e(TAG, "bgPath in config cannot be null or empty!");
            return false;
        }

        File bgFile = new File(config.getBgPath());
        if (!bgFile.exists()) {
            Log.e(TAG, "bg image not exists!");
            return false;
        }

        if (TextUtils.isEmpty(config.getPreviewPath())) {
            Log.e(TAG, "previewPath in config cannot be null or empty!");
            return false;
        }

        File previewFile = new File(config.getBgPath());
        if (!previewFile.exists()) {
            Log.e(TAG, "preview image not exists!");
            return false;
        }

        if (TextUtils.isEmpty(config.getTargetFilePath())) {
            Log.w(TAG, "invalid targetPath!");
            return false;
        }
        return true;
    }
}
