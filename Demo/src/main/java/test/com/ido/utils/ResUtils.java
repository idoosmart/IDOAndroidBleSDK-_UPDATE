package test.com.ido.utils;

import android.content.Context;

import test.com.ido.APP;

/**
 * @author tianwei
 * @date 2022/11/3
 * @time 11:51
 * 用途:
 */
public class ResUtils {
    public static int getStringResId(String name) {
        return APP.getAppContext().getResources().getIdentifier(name, "string",
                APP.getAppContext().getPackageName());
    }

    public static int getResIdByName(String name, String type) {
        return APP.getAppContext().getResources().getIdentifier(name, type, APP.getAppContext().getPackageName());
    }

    public static int getMipmapResId(String name) {
        return getResIdByName(name, "mipmap");
    }

    public static String getString(String name) {
        return APP.getAppContext().getString(getStringResId(name));
    }

    public static String getString(int resId) {
        return APP.getAppContext().getString(resId);
    }
}
