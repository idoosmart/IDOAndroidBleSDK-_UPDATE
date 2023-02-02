package test.com.ido.utils;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.ArrayRes;
import androidx.annotation.ColorRes;
import androidx.annotation.IntegerRes;
import androidx.annotation.RawRes;
import androidx.annotation.StringRes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import test.com.ido.APP;

/**
 * 获取系统资源的工具类
 */
public class ResourceUtil {

    /**
     * 根据资源ID返回字符串
     *
     * @param resources 资源对象
     * @param resId     资源id
     * @return 字符串
     */
    public static String getString(Resources resources, @StringRes int resId) {
        return resources.getString(resId);
    }

    /**
     * 根据资源ID和传入的参数进行格式化返回字符串
     *
     * @param resources  资源对象
     * @param resId      资源id
     * @param formatArgs 格式化参数数组
     * @return 字符串
     */
    public static String getString(Resources resources, @StringRes int resId, Object... formatArgs) {
        return resources.getString(resId, formatArgs);
    }

    /**
     * 获取Resource
     *
     * @return resource
     */
    public static Resources getResources() {
        return APP.getAppContext().getResources();
    }


    /**
     * 获取颜色 资源对象
     *
     * @param resources
     * @param resId
     * @param theme
     * @return
     */
    public static int getColor(Resources resources, @ColorRes int resId, Resources.Theme theme) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return resources.getColor(resId, theme);
        } else {
            //noinspection deprecation
            return resources.getColor(resId);
        }
    }

    /**
     * 根据资源ID返回字符串
     *
     * @param resid
     * @return 该方法可能会出现异常，请使用{@link #getString(Resources, int)}
     */
//    @Deprecated
    public static String getString(int resid) {
        String result = "";
        try {
            result = getResources().getString(resid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 根据资源ID名称获取文本内容
     */
    public static String getString(String identify) {
        if (TextUtils.isEmpty(identify)) return "";
        int resId = getStringResId(identify);
        return getResources().getString(resId);
    }

    /**
     * 根据资源ID查询资源名称
     */
    public static String getEntryName(int strId) {
        String result = "";
        try {
            result = getResources().getResourceEntryName(strId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 根据资源ID和传入的参数进行格式化返回字符串
     *
     * @param resid      资源id
     * @param formatArgs 格式化参数数组
     * @return 该方法有风险，被标记过时! 请使用 {@link #getString(Resources, int, Object...)}
     */
//    @Deprecated
    public static String getString(int resid, Object... formatArgs) {
        return getResources().getString(resid, formatArgs);
    }

    /**
     * 获取颜色
     */
//    @Deprecated
    public static int getColor(int resid) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getResources().getColor(resid, APP.getAppContext().getTheme());
        } else {
            return getResources().getColor(resid);
        }
    }

    /**
     * 获取颜色状态
     *
     * @param resId
     * @return
     */
    public static ColorStateList getColorStateList(int resId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Resources.Theme theme = APP.getAppContext().getTheme();
            return getResources().getColorStateList(resId, theme);
        } else {
            return getResources().getColorStateList(resId);
        }
    }

    /**
     * 获取图片
     */
    public static Drawable getDrawable(int resid) {
        return getResources().getDrawable(resid);
    }

    /**
     * 获取dimems大小（单位为像素pix）
     *
     * @param resid
     * @return
     */
    public static int getDimens(int resid) {
        return (int) getResources().getDimension(resid);
    }

    /**
     * 根据资源id返回字符串数组
     *
     * @param resid
     * @return
     */
    public static String[] getStringArray(int resid) {
        return getResources().getStringArray(resid);
    }

    /**
     * 获取资源中的整数数据
     *
     * @param resId
     * @return
     */
    public static int getInteger(@IntegerRes int resId) {
        return getResources().getInteger(resId);
    }

    /**
     * 获取资源中的整形数组
     *
     * @param resId
     * @return
     */
    public static int[] getIntegerArray(@ArrayRes int resId) {
        return getResources().getIntArray(resId);
    }

    /**
     * 根据资源名获取资源id
     *
     * @param name 资源名
     * @return
     */
    public static int getDrawableResId(String name) {
        return getResources().getIdentifier(name, "drawable",
                AppInfoUtil.getPackageName(APP.getAppContext()));
    }

    /**
     * 根据资源名获取资源id
     *
     * @param name 资源名
     * @return
     */
    public static int getStringResId(String name) {
        return getResources().getIdentifier(name, "string",
                AppInfoUtil.getPackageName(APP.getAppContext()));
    }

    /**
     * 打开 res/raw 中的资源
     *
     * @param resId raw id
     * @return 输入流
     */
    public static InputStream openRaw(@RawRes int resId) {
        return getResources().openRawResource(resId);
    }

    /**
     * 打开 res/raw 中的文本资源
     *
     * @param resId rawId
     * @return 文本内容
     */
    public static String openRawForText(@RawRes int resId) {
        InputStream is = openRaw(resId);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            int index = 0;
            byte[] buf = new byte[512];
            while ((index = is.read(buf)) != -1) {
                os.write(buf, 0, index);
            }
            return os.toString("utf-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            IOUtil.close(os);
            IOUtil.close(is);
        }
    }

    public static Uri resId2Uri(int resId) {
        String resPath = "android.resource://"
                + AppInfoUtil.getPackageName(APP.getAppContext()) + "/" + resId;
        return Uri.parse(resPath);
    }

    /**
     * 该方法已移动到CameraUtil中
     * 根据当前的视频大小即选中的视频质量,选择合适的码率
     * @param resolution 视频分辨率
     * @param bitrate 当前选中的视频质量,依次为 '优化','均衡','品质'及'极佳'
     * @return 返回文字质量对应下的实际码率数值
     */
//    public static int calculateSuitableBitrate(CameraResolutionAdapter.ResolutionBean resolution, String bitrate) {
//        Map<CameraResolutionAdapter.ResolutionBean, Map<String, Integer>> VIDEO_BITRATE_MAP = new HashMap<>(4);
//        final int BITRATE_M = 1000000;
//
//        final CameraResolutionAdapter.ResolutionBean low = new CameraResolutionAdapter.ResolutionBean(1280, 720, 30);
//        Map<String, Integer> lowBitrate = new HashMap<>(4);
//        lowBitrate.put("optimize", 3 * BITRATE_M);
//        lowBitrate.put("equilibrium", 5 * BITRATE_M);
//        lowBitrate.put("quality", 7 * BITRATE_M);
//        lowBitrate.put("excellent", 8 * BITRATE_M);
//        VIDEO_BITRATE_MAP.put(low, lowBitrate);
//
//        final CameraResolutionAdapter.ResolutionBean mid = new CameraResolutionAdapter.ResolutionBean(1920, 1080, 30);
//        Map<String, Integer> midBitrate = new HashMap<>(4);
//        midBitrate.put("optimize", 10 * BITRATE_M);
//        midBitrate.put("equilibrium", 12 * BITRATE_M);
//        midBitrate.put("quality", 15 * BITRATE_M);
//        midBitrate.put("excellent", 18 * BITRATE_M);
//        VIDEO_BITRATE_MAP.put(mid, midBitrate);
//
//        final CameraResolutionAdapter.ResolutionBean midSuperior = new CameraResolutionAdapter.ResolutionBean(1920, 1080, 60);
//        Map<String, Integer> midSuperiorBitrate = new HashMap<>(4);
//        midSuperiorBitrate.put("optimize", 12 * BITRATE_M);
//        midSuperiorBitrate.put("equilibrium", 18 * BITRATE_M);
//        midSuperiorBitrate.put("quality", 22 * BITRATE_M);
//        midSuperiorBitrate.put("excellent", 27 * BITRATE_M);
//        VIDEO_BITRATE_MAP.put(midSuperior, midSuperiorBitrate);
//
//        final CameraResolutionAdapter.ResolutionBean high = new CameraResolutionAdapter.ResolutionBean(3840, 2160, 30);
//        Map<String, Integer> highBitrate = new HashMap<>(4);
//        highBitrate.put("optimize", 23 * BITRATE_M);
//        highBitrate.put("equilibrium", 33 * BITRATE_M);
//        highBitrate.put("quality", 45 * BITRATE_M);
//        highBitrate.put("excellent", 80 * BITRATE_M);
//        VIDEO_BITRATE_MAP.put(high, highBitrate);
//
//        try {
//            return VIDEO_BITRATE_MAP.get(resolution).get(bitrate);
//        } catch (Exception e) {
//            return 8 * BITRATE_M;
//        }
//    }
}
