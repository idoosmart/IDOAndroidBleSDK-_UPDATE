package test.com.ido.runplan.utils;

/**
 * Author:   shensl
 * Function: 功能描述
 * DATE:     2018/3/1
 */

public interface IOkHttpCallBack<T> {

    /**
     * 请求成功的回调
     *
     * @param t 请求成功返回的信息
     */
    void success(T t);

    /**
     * 请求失败的回调
     *
     * @param e 请求失败的信息
     */
    void fail(AGException e);

}
