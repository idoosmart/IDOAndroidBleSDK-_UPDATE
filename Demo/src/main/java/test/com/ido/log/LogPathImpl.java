package test.com.ido.log;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import test.com.ido.APP;
import test.com.ido.utils.FileUtil;

public class LogPathImpl implements LogPath {
    private static LogPathImpl mInstance = new LogPathImpl();
    private static String sRootPath;

    public static LogPath getInstance() {
        return mInstance;
    }

    /**
     * 初始化日志路径
     *
     * @param context
     */
    public static void initLogPath(Context context) {
        if (context == null) {
            return;
        }
        sRootPath = context.getFilesDir().getAbsolutePath() + "/BLEdemo/";
    }

    @Override
    public String getRootPath() {
        if (TextUtils.isEmpty(sRootPath)) {
            try {
                if (APP.getAppContext() != null) {
                    sRootPath = APP.getAppContext().getFilesDir().getAbsolutePath() + "/BLEdemo/";
                }
            } catch (Exception e) {
                sRootPath = "/BLEdemo/";
            }
        }
        return sRootPath;
    }

    @Override
    public String getInfoPath() {
        return getRootPath().concat("info/");
    }

    @Override
    public String getLogPath() {
        return getRootPath().concat("log/");
    }

    @Override
    public String getLogZipNamePath() {
        return FileUtil.getSdcard().concat("/BLEdemo/blelog.zip");
    }

    @Override
    public String getPicPath() {
        return getRootPath().concat("pic/");
    }

    @Override
    public String getLanguagePath() {
        return getRootPath().concat("multi_language/");
    }

    @Override
    public String getCrashLogPath() {
        return getLogPath().concat("crash/");
    }

    @Override
    public String getSportfile() {
        return getRootPath().concat("sport/");
    }

    @Override
    public String getNormalFilePath() {
        return getRootPath().concat("file/");
    }

    @Override
    public String getLocationInfoPath() {
        return getRootPath().concat("location/");
    }

    @Override
    public String getApkPath() {
        return getRootPath().concat("apk/");
    }

    @Override
    public String getBugLogPath() {
        return getLogPath().concat("bug/");
    }

    @Override
    public String getCameraPicPath() {
        return getRootPath().concat("DCIM/photo/");
    }

    @Override
    public String getServerLogPath() {
        return getLogPath().concat("server/");
    }

    @Override
    public String getBleSdkLogPath() {
        return getLogPath().concat("ble_sdk/");
    }

    @Override
    public String getOtaFilePath() {
        return getRootPath().concat("ota/");
    }

    @Override
    public String getHtmlFilePath() {
        return getRootPath().concat("h5/");
    }

    @Override
    public String getDialFilePath() {
        return getRootPath().concat("dial/");
    }

    @Override
    public String getDialDefinedFilePath() {
        return getRootPath().concat("dialdefined/");
    }

    @Override
    public String getWallpaperDialFilePath() {
        return getRootPath().concat("wallpaper/");
    }

    @Override
    public String getWeatherLogPath() {
        return getLogPath().concat("weather/");
    }

    @Override
    public String getNotificationLogPath() {
        return getLogPath().concat("notification/");
    }

    @Override
    public String getStravaLogPath() {
        return getLogPath().concat("strava/");
    }

    @Override
    public String getGoogleFitLogPath() {
        return getLogPath().concat("googlefit/");
    }

    @Override
    public String getGoogleMapLogPath() {
        return getLogPath().concat("googlemap/");
    }

    @Override
    public String getLanguageFilePath() {
        return getRootPath().concat("language/");
    }

    @Override
    public String getSportLogPath() {
        return getLogPath().concat("sport/");
    }

    @Override
    public String getLoginRegisterLogPath() {
        return getLogPath().concat("login_register/");
    }

    @Override
    public String getOtaLogPath() {
        return getLogPath().concat("ota/");
    }

    @Override
    public String getKeepLiveLogPath() {
        return getLogPath().concat("keep_live/");
    }

    @Override
    public String getSleepDataLogPath() {
        return getLogPath().concat("sleep/");
    }

    @Override
    public String getDialLogPath() {
        return getLogPath().concat("dial/");
    }

    @Override
    public String getDialPicPath() {
        return getRootPath().concat("dialpic/");
    }

    @Override
    public String getRemoteLanguageLogPath() {
        return getLogPath().concat("language/");
    }

    @Override
    public String getBindLogPath() {
        return getLogPath().concat("bind/");
    }

    @Override
    public String getSoPath() {
        return getLogPath().concat("so/");
    }

    /**
     * flash日志
     */
    @Override
    public String getFlashPath() {
        return getLogPath().concat("flash/");
    }

    @Override
    public String getBtFlashPath() {
        return getLogPath().concat("btFlash/");
    }

    @Override
    public String getAgpsFilePath() {
        return getRootPath().concat("agps/");
    }

    @Override
    public String getAgpsLogPath() {
        return getLogPath().concat("agps/");
    }

    /**
     * 数据库升级日志路径
     */
    @Override
    public String getDataBaseUpgradePath() {
        return getLogPath().concat("databaseUpgrade/");
    }

    @Override
    public String getDeviceLogPath() {
        return getLogPath().concat("deviceLog/");
    }

    @Override
    public String getDeviceRestartLogPath() {
        return getLogPath().concat("restartLog/");
    }

    @Override
    public String getDeviceErrorLogPath() {
        return getLogPath().concat("errorLog/");
    }

    @Override
    public String getDeviceRestartCacheLogPath() {
        return getDeviceLogPath().concat("restartLog/");
    }

    @Override
    public String getDeviceRestartSaveLogPath() {
        return getDeviceLogPath().concat("restartSaveLog/");
    }


    @Override
    public String getDeviceRestartLogZipPath() {
        return getLogPath().concat(RESTART_LOG_NAME);
    }

    @Override
    public String getDeviceErrorLogZipPath() {
        return getLogPath().concat(ERROR_LOG_NAME);
    }

    /**
     * alexa日志
     *
     * @return
     */
    @Override
    public String getAlexaLogPath() {
        return getLogPath().concat("alexa/");
    }

    @Override
    public String getConnectLogPath() {
        return getLogPath().concat("connect/");
    }

    @Override
    public String getGpsLogPath() {
        return getLogPath().concat("gps/");
    }

    @Override
    public String getMainQuicklySettingLogPath() {
        return getLogPath().concat("quicklySetting/");
    }

    @Override
    public String getFamilyAccountLogPath() {
        return getLogPath().concat("familyAccount/");
    }

    @Override
    public String getDialPicLogPath() {
        return getLogPath().concat("dialpiclog/");
    }

    @Override
    public String getMusicLogPath() {
        return getLogPath().concat("music/");
    }

    /**
     * 获取所有日志路径
     */
    @Override
    public List<String> getAllLogPath() {
        return new ArrayList<String>() {
            {
                //自动删除该目录下所有过期的日志（会遍历目录下面的目录，所以这里只需传入日志的根目录）
                add(getLogPath());
            }
        };
    }
}
