package test.com.ido.runplan;

import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

interface IHealthManagerView {
    /**
     *跑步计划app通知H5信息
     */
    void runPlanAppSendH5(JsonObject json, String name);
    /**
     *跑步计划app通知H5信息
     */
    void  AppSendH5Medal( JsonObject json ,String name );

    /**
     * 显示运动成功
     */
    void showSportStartSuccess();



    /**
     * 显示低电量
     */
    void showSportStartFailedLowPower();

    /**
     * 手环已经在运动模式了
     */
    void showSportStartFail();
    /**
     * 来电中
     */
    void showSportStartFailedInCalling();

    /**
     * 正在充电
     */
    void showSportStartFailedChargePower();

    /**
     * 开始运动出错
     */
    void showSportStartError(String msg);


    void showMessage(String message);
    /**
     * 正在连接的loading
     * @param message
     */
    void showLoading(String message);

    /**
     * 连接成功的loading
     */
    void hideLoading();

    /**
     * 显示断开设备的弹框
     */
    void showDisconnectDialog();



    /**
     * 设置运动状态
     * @param isRunning
     */
    void setSportStatus(Boolean isRunning);

}
