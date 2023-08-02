package test.com.ido.runplan.utils;

import test.com.ido.runplan.Constants;

public class RunTimeUtil {
    public final static int USERID_DEFAULT = -1;
    private final static RunTimeUtil mInstance = new RunTimeUtil();
    private long mUserId = 450962308303294464L;
    private RunTimeUtil() {
    }

    public static RunTimeUtil getInstance() {
        return mInstance;
    }
    /**
     * 获取UserId
     */
    public long getUserId() {
        return mUserId;
    }
    public void setUserId(long userId) {
        mUserId = userId;
    }

    public String getAppToken(){
        String token = (String) SPUtils.get(Constants.TOKEN,"");
        return token;
    }
}
