package test.com.ido.utils;

import android.os.Environment;

/**
 * @author: zhouzj
 * @date: 2017/11/16 17:14
 */

public class Constant {
    public static final String APP_ROOT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/IDO_SDK_DEMO";
    public static final String CRASH_PATH = APP_ROOT_PATH + "/crash/";
    public static final String LOG_OUTPUT_TOOL_PATH = APP_ROOT_PATH + "/tool/";
    public static final String TOOL_APK_NAME = "Logoutput-debug.apk";
}
