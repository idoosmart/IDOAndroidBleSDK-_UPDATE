package test.com.ido.runplan.utils;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Author:   shensl
 * Function: 封装业务
 * DATE:     2018/3/2
 */

public class OkHttpUtil extends OkHttpBaseRequest {
    /**
     * 常量
     */
    private static final MediaType MediaType_JSON = MediaType.parse("application/json; charset=utf-8");

    private final Gson gson = new Gson();

    private static OkHttpUtil instance;

    /**
     * 单例获取实例
     *
     * @return
     */
    public static final OkHttpUtil getInstance() {
        if (instance == null) {
            instance = new OkHttpUtil();
        }
        return instance;
    }

    /**
     * IP地址接口
     */
    public interface IPCallBack {
        /**
         * IP请求
         *
         * @param api
         */
        void baseIPRequest(String api);
    }



    /**
     * 获取Token值
     *
     * @param callBack 二次封装回调
     */
    public void getAccessToken(final IOkHttpCallBack<String> callBack) {
        String url = "https://cn-user.idoocloud.com/userapi/public/account/login";
        RequestBody requestBody = new FormBody.Builder()
                .add("username", "18370995392@163.com")
                .add("password", "123456")
                .build();
        postRequestMethod(url, requestBody, new IOkHttpCallBack<String>() {
            @Override
            public void success(String s) {
                callBack.success(s);
            }

            @Override
            public void fail(AGException e) {
                if (callBack != null) {
                    callBack.fail(e);
                }
            }
        });
    }

}
