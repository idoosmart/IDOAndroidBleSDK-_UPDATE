package test.com.ido.utils;

import android.content.Context;

/**
 * @author tianwei
 * @date 2022/11/3
 * @time 11:51
 * 用途:
 */
public class ResUtils {
    public static int getStringResId(Context context, String name) {
        return context.getResources().getIdentifier(name, "string",
                context.getPackageName());
    }

    public static int getResIdByName(Context context, String name, String type) {
        return context.getResources().getIdentifier(name, type, context.getPackageName());
    }

    public static int getMipmapResId(Context context, String name) {
        return getResIdByName(context, name, "mipmap");
    }
}
