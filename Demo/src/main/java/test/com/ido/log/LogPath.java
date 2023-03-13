package test.com.ido.log;

import java.util.List;

public interface LogPath {

    /**
     * 文本文件后缀名
     */
    String TXT_SUFFIX = ".txt";

    /**
     * 重启日志文件名
     */
    String RESTART_LOG_NAME = "restartLog.zip";

    /**
     * 异常日志
     */
    String ERROR_LOG_NAME = "errorLog.zip";

    /**
     * 获取跟路径
     */
    String getRootPath();

    /**
     * 获取文件存储路径
     */
    String getInfoPath();

    /**
     * 获取普通日志存储路径
     */
    String getLogPath();

    /**
     * 获取日志分享后的zip存储路径
     */
    String getLogZipNamePath();

    /**
     * 获取图片存储路径
     */
    String getPicPath();

    /**
     * 获取地狱岩翻译文件存储路径
     */
    String getLanguagePath();

    /**
     * 获取APP崩溃日志存储路径
     */
    String getCrashLogPath();

    /**
     * 获取缓存文件路径
     */
    String getSportfile();

    /**
     * 获取普通文件存储路径
     */
    String getNormalFilePath();

    /**
     * 获取定位信息日志存储路径
     */
    String getLocationInfoPath();

    /**
     * 获取APK存储文件路径
     */
    String getApkPath();

    /**
     * bug日志存储文件路径
     */
    String getBugLogPath();

    /**
     * 获取相机拍照图片存储路径
     */
    String getCameraPicPath();

    /**
     * 获取服务器日志文件保存路径
     */
    String getServerLogPath();

    /**
     * 蓝牙SDK日志存放路径
     */
    String getBleSdkLogPath();

    /**
     * OTA文件路径
     */
    String getOtaFilePath();

    /**
     * H5文件路径
     */
    String getHtmlFilePath();

    /**
     * 表盘文件路径
     */
    String getDialFilePath();

    /**
     * 自定义表盘文件路径
     */
    String getDialDefinedFilePath();

    /**
     * 壁纸表盘存储路径
     */
    String getWallpaperDialFilePath();

    /**
     * 天气日志存储路径
     */
    String getWeatherLogPath();

    /**
     * 提示日志存储路径
     */
    String getNotificationLogPath();

    /**
     * Strava日志存储路径
     */
    String getStravaLogPath();

    /**
     * GoogleFit日志存储路径
     */
    String getGoogleFitLogPath();

    /**
     * GoogleMap日志存储路径
     */
    String getGoogleMapLogPath();

    /**
     * 获取语言文件路径
     *
     * @return
     */
    String getLanguageFilePath();

    /**
     * 获取运动的log
     *
     * @return
     */
    String getSportLogPath();

    /**
     * 获取登录注册的log
     *
     * @return
     */
    String getLoginRegisterLogPath();

    /**
     * OTA日志
     */
    String getOtaLogPath();

    /**
     * 保活日志
     *
     * @return
     */
    String getKeepLiveLogPath();

    /**
     * 睡眠数据日志
     *
     * @return
     */
    String getSleepDataLogPath();

    /**
     * 表盘日志
     *
     * @return
     */
    String getDialLogPath();

    /**
     * 表盘图片路径
     *
     * @return
     */
    String getDialPicPath();

    /**
     * 云端语言日志
     *
     * @return
     */
    String getRemoteLanguageLogPath();

    /**
     * 设备绑定日志
     *
     * @return
     */
    String getBindLogPath();

    /**
     * so日志
     *
     * @return
     */
    String getSoPath();

    /**
     * flash日志
     */
    String getFlashPath();

    /**
     * flash日志
     */
    String getBtFlashPath();

    /**
     * Agps文件路径
     *
     * @return
     */
    String getAgpsFilePath();

    /**
     * Agps日志路径
     *
     * @return
     */
    String getAgpsLogPath();

    /**
     * 数据库升级日志路径
     */
    String getDataBaseUpgradePath();

    /**
     * 设备日志路径
     *
     * @return
     */
    String getDeviceLogPath();

    /**
     * 设备重启日志路径（用于上传给服务器）
     *
     * @return
     */
    String getDeviceRestartLogPath();

    /**
     * 设备异常日志路径(用于上传给服务器)
     * @return
     */
    String getDeviceErrorLogPath();

    /**
     * 设备重启缓存日志路径（APP本地保存设备重启日志，用于本地导出数据分析）
     *
     * @return
     */
    String getDeviceRestartCacheLogPath();
    /**
     * 设备重启缓存日志路径（APP本地保存设备重启日志，用于本地导出数据分析）
     *
     * @return
     */
    String getDeviceRestartSaveLogPath();

    /**
     * 设备重启日志压缩包路径
     *
     * @return
     */
    String getDeviceRestartLogZipPath();

    /**
     * 设备异常日志压缩包路径
     * @return
     */
    String getDeviceErrorLogZipPath();


    /**
     * alexa日志
     *
     * @return
     */
    String getAlexaLogPath();

    /**
     * 连接日志
     *
     * @return
     */
    String getConnectLogPath();

    /**
     * gps日志
     *
     * @return
     */
    String getGpsLogPath();

    /**
     * 主页快捷设置日志
     *
     * @return
     */
    String getMainQuicklySettingLogPath();

    /**
     * 家庭账号日志
     *
     * @return
     */
    String getFamilyAccountLogPath();

    /**
     * 表盘与外框拼接日志
     *
     * @return
     */
    String getDialPicLogPath();

    /**
     * 音乐的日志
     * @return
     */
    String getMusicLogPath();

    /**
     * 获取所有日志路径
     */
    List<String> getAllLogPath();
}
