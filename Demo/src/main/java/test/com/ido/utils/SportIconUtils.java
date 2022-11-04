package test.com.ido.utils;

import android.content.Context;
import android.util.Log;

import com.ido.ble.LocalDataManager;
import com.ido.ble.protocol.model.Sport100TypeItem;
import com.ido.ble.protocol.model.SupportFunctionInfo;

import java.io.File;
import java.util.List;

/**
 * @author tianwei
 * @date 2022/11/3
 * @time 15:19
 * 用途:
 */
public class SportIconUtils {
    //0 全部未传 0000
    static final int PIC_NOT_DOWNLOAD = Sport100TypeItem.PIC_NOT_DOWNLOAD;

    //2 大图已传 0010
    static final int PIC_BIG_DOWNLOAD_SUCCESS = Sport100TypeItem.PIC_BIG_DOWNLOAD_SUCCESS;

    //1 小图已传 0001
    static final int PIC_SMALL_DOWNLOAD_SUCCESS = Sport100TypeItem.PIC_SMALL_DOWNLOAD_SUCCESS;

    //4 中图已传 0100
    static final int PIC_MIDDLE_DOWNLOAD_SUCCESS = Sport100TypeItem.PIC_MIDDLE_DOWNLOAD_SUCCESS;

    //8 x图已传 1000
    static final int PIC_X_DOWNLOAD_SUCCESS = 8;

    //5 小图+中图已传 0101
    static final int PIC_SMALL_MIDDLE_DOWNLOAD_SUCCESS = Sport100TypeItem.PIC_SMALL_MIDDLE_DOWNLOAD_SUCCESS;

    //3 小图+大图已传 0011
    static final int PIC_SMALL_BIG_DOWNLOAD_SUCCESS = Sport100TypeItem.PIC_ALL_DOWNLOAD_SUCCESS;

    //6 中图+大图已传 0110
    static final int PIC_MIDDLE_BIG_DOWNLOAD_SUCCESS = Sport100TypeItem.PIC_MIDDLE_BIG_DOWNLOAD_SUCCESS;

    //7 小中大全部已传 0111
    static final int PIC_NEW_ALL_DOWNLOAD_SUCCESS = Sport100TypeItem.PIC_NEW_ALL_DOWNLOAD_SUCCESS;

    //x小中大全部已传 1111
    static final int PIC_X_ALL_DOWNLOAD_SUCCESS = 15;

    //x小大全部已传1011
    static final int PIC_ONLY_X_ALL_DOWNLOAD_SUCCESS = 11;

    static String TAG = "SportIconUtils";
    //资源名称不带后缀
    public static final String RESOURCE_NAME_NO_SUFFIX = "motion_types";

    //资源名称
    public static final String RESOURCE_NAME = RESOURCE_NAME_NO_SUFFIX + ".zip";

    //资源存储的路径
    public static final String RESOURCE_DIR = "motion_types";

    //资源版本号，与本地缓存比较，不同表示有更新，需要重新解压覆盖
    public static final int RESOURCE_VERSION = 2;

    static final int BASE_MAX_ICON_COUNT_EACH_MOTION = 2;

    static final int SMALL_ICON_FLAG = 1;
    static final int BIG_ICON_FLAG = 2;
    static final int MIDDLE_ICON_FLAG = 4;
    static final int X_ICON_FLAG = 8;

    //是否需要小尺寸图
    public static boolean requireSmall(int iconFlag) {
        return (iconFlag & SMALL_ICON_FLAG) == 0;
    }

    //是否需要大尺寸图
    public static boolean requireBig(int iconFlag) {
        return (iconFlag & BIG_ICON_FLAG) == 0;
    }

    //是否需要中尺寸图
    public static boolean requireMiddle(int iconFlag) {
        return (iconFlag & MIDDLE_ICON_FLAG) == 0;
    }

    //是否需x尺寸图
    public static boolean requireX(int iconFlag) {
        return (iconFlag & X_ICON_FLAG) == 0;
    }

    /**
     * 每个运动最多需要的图标数量
     *
     * @return
     */
    public static int getMaxIconCountForEachSport() {
        if (isSupportXIcon() && isSupportMiddleIcon()) {
            return BASE_MAX_ICON_COUNT_EACH_MOTION + 2;
        } else if (isSupportXIcon() || isSupportMiddleIcon()) {
            return BASE_MAX_ICON_COUNT_EACH_MOTION + 1;
        }
        return BASE_MAX_ICON_COUNT_EACH_MOTION;
    }

    /**
     * 是否支持中尺寸图传输
     */
    public static boolean isSupportMiddleIcon() {
        SupportFunctionInfo func = LocalDataManager.getSupportFunctionInfo();
        return func != null && func.v3_surport_sport_medium_icon;
    }

    /**
     * 是否支持传输
     */
    public static boolean isSupportXIcon() {
        SupportFunctionInfo func = LocalDataManager.getSupportFunctionInfo();
        return func != null && func.v3_suppor_sport_icon_min_small;
    }

    /**
     * 是否支持设置图标传输开始和结束
     */
    public static boolean isSupportSetTransferInfo() {
        SupportFunctionInfo func = LocalDataManager.getSupportFunctionInfo();
        return func != null && func.v2_support_notice_icon_information;
    }

    /**
     * 比较当前版本的图片资源与用户存储的资源是否一致
     */
    public static boolean checkMotionResourceVersionChanged() {
        int savedVersion = DataUtils.getInstance().getMotionTypeVersion();
        Log.d(TAG, "check the motion resource version, oldVersion = " + savedVersion + ", newVersion = " + RESOURCE_VERSION);
        return savedVersion != RESOURCE_VERSION;
    }

    /**
     * 判断图标是否存在
     */
    public static boolean isMotionTypeIconsExist(Context context) {
        try {
            File dir = getResourceDir(context);
            boolean exist = dir.exists();
            File[] files = dir.listFiles();
            return exist && files != null && files.length > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 资源存放路径
     * 文件夹结构
     * /data/data/com.watch.life/app_motion_types/xx
     */
    public static File getResourceDir(Context context) {
        return context.getDir(RESOURCE_DIR, Context.MODE_PRIVATE);
    }

    /**
     * 获取运动图尺寸转换后存放目录
     */
    public static String getSportIconTempDir(Context context) {
        return getResourceDir(context).getAbsolutePath() + File.separator + "temp";
    }


}
