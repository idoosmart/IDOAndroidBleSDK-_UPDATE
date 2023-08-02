package test.com.ido.runplan.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import test.com.ido.runplan.Constants;

/**
 * Author:   shensl
 * Function: 封装OkHttp的基础请求
 * DATE:     2018/3/2
 */

public class OkHttpBaseRequest implements IOkHttpBaseRequest<String> {

    // 全局的实例
    private final OkHttpClient mOkHttpClient = new OkHttpClient().newBuilder()
            .connectTimeout(10 * 60 * 1000, TimeUnit.SECONDS)
            .readTimeout(10 * 60 * 1000, TimeUnit.SECONDS)
            .build();

    public OkHttpClient getmOkHttpClient() {
        return mOkHttpClient;
    }

    /**
     * 带请求头的post请求
     *
     * @param header      请求头
     * @param url         请求地址
     * @param requestBody 请求体
     * @param callBack    二次封装的请求回调
     */
    @Override
    public void postRequestMethod(String header, String url, RequestBody requestBody, IOkHttpCallBack<String> callBack) {
        Request request = new Request.Builder().addHeader("token", header).url(url).post(requestBody).build();
        baseRequestMethod(request, callBack);
    }

    /**
     * 普通的post请求
     *
     * @param url         请求地址
     * @param requestBody 请求体
     * @param callBack    二次封装的请求回调
     */
    @Override
    public void postRequestMethod(String url, RequestBody requestBody, IOkHttpCallBack<String> callBack) {
        Request request = new Request.Builder().url(url).post(requestBody).header("appkey", Constants.APP_KEY).build();
        baseRequestMethod(request, callBack);
    }

    /**
     * 带请求头的get请求
     *
     * @param header   请求头
     * @param url      请求地址
     * @param callBack 二次封装的请求回调
     */
    @Override
    public void getRequestMethod(String header, String url, IOkHttpCallBack<String> callBack) {
        Request request = new Request.Builder().addHeader("token", header).url(url).build();
        baseRequestMethod(request, callBack);
    }

    /**
     * 带请求头的get请求
     *
     * @param url      请求地址
     * @param callBack 二次封装的请求回调
     */
    @Override
    public void getRequestMethod(String url, IOkHttpCallBack<String> callBack) {
        Request request = new Request.Builder().url(url).build();
        baseRequestMethod(request, callBack);
    }



    /**
     * 基本请求方法
     *
     * @param request  请求
     * @param callBack 二次封装的请求回调
     */
    private void baseRequestMethod(Request request, final IOkHttpCallBack<String> callBack) {

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 访问服务器失败
                if (callBack != null) {
                    callBack.fail(new AGException(AGException.ACCESS_SERVER_ERROR, e.getMessage()));
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (!response.isSuccessful()) {
                        // 请求失败
                        if (callBack != null) {
                            callBack.fail(new AGException(AGException.REQUEST_ERROR, response.message()));
                        }
                    } else {
                        // 请求成功
                        String json = response.body().string();
                        if (callBack != null) {
                            callBack.success(json);
                        }
                    }
                } finally {
                    if (response != null) {
                        response.close();
                    }
                }
            }
        });
    }

}
