package test.com.ido.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Gson解析工具
 */
public class GsonUtil {

    private static Gson gson = new Gson();

    /**
     * 把json转成List集合
     *
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> analysisJsonArrayToList(String json, Class<T[]> clazz) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        T[] array = gson.fromJson(json, clazz);
        return Arrays.asList(array);
    }

    /**
     * 把json转成List集合
     *
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> analysisJsonObjectToList(String json, Class<T> clazz) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        Type type = new TypeToken<ArrayList<JsonObject>>() {
        }.getType();
        ArrayList<JsonObject> jsonObjects = gson.fromJson(json, type);
        ArrayList<T> arrayList = new ArrayList<T>();
        for (JsonObject jsonObject : jsonObjects) {
            arrayList.add(gson.fromJson(jsonObject, clazz));
        }
        return arrayList;
    }

    /**
     * 把对象转成json
     *
     * @param obj
     * @return
     */
    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    /**
     * 解析json获取对象
     *
     * @param json
     * @return
     */
    public static <T> T analysisJsonToObject(String json, Class<T> clazz) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        return gson.fromJson(json, clazz);
    }
    /**
     * @param gsonStr json字符串
     * @param clazz   返回的类型
     * @param <T>
     * @return 解析错误返回null
     */
    public static <T> T fromJson(String gsonStr, Class<T> clazz) {
        Gson gson = new Gson();
        try {
            return gson.fromJson(gsonStr, clazz);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
