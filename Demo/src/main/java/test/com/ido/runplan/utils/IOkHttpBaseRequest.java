package test.com.ido.runplan.utils;

import okhttp3.RequestBody;

/**
 * Author:   shensl
 * Function: 基础请求接口
 * DATE:     2018/3/2
 */

public interface IOkHttpBaseRequest<T> {

    /**
     * 带请求头的post请求
     *
     * @param header      请求头
     * @param url         请求地址
     * @param requestBody 请求体
     * @param callBack    二次封装的请求回调
     */
    void postRequestMethod(String header, String url, RequestBody requestBody, IOkHttpCallBack<T> callBack);

    /**
     * 普通的post请求
     *
     * @param url         请求地址
     * @param requestBody 请求体
     * @param callBack    二次封装的请求回调
     */
    void postRequestMethod(String url, RequestBody requestBody, IOkHttpCallBack<T> callBack);

    /**
     * 带请求头的get请求
     *
     * @param header   请求头
     * @param url      请求地址
     * @param callBack 二次封装的请求回调
     */
    void getRequestMethod(String header, String url, IOkHttpCallBack<T> callBack);

    /**
     * 带请求头的get请求
     *
     * @param url      请求地址
     * @param callBack 二次封装的请求回调
     */
    void getRequestMethod(String url, IOkHttpCallBack<T> callBack);


}
