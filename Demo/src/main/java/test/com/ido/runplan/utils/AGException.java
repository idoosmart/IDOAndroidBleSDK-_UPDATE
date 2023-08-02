package test.com.ido.runplan.utils;

/**
 * Author:   shensl
 * Function: 自定义异常类
 * DATE:     2018/3/1
 */

public class AGException extends Exception {

    // 访问服务器错误
    public static final int ACCESS_SERVER_ERROR = -1;
    // 请求失败
    public static final int REQUEST_ERROR = 1;
    // 下载失败
    public static final int DOWNLOAD_ERROR = 2;
    // 解析出错
    public static final int ANALYSIS_ERROR = 3;
    //其他错误
    public static final int OTHER_ERROR = 4;

    protected int errorCode;//错误码
    protected String description;//错误信息提示

    public AGException(int errorCode, String detailMessage) {
        super(detailMessage);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public String getDescription() {
        return this.description;
    }

    public String toString() {
        return this.description == null ? "AGException{errorCode=" + this.errorCode + ", message=\'" + this.getMessage() + '\'' + '}' : "AGException{errorCode=" + this.errorCode + ", message=\'" + this.getMessage() + '\'' + ", description=\'" + this.description + '\'' + '}';
    }

}
