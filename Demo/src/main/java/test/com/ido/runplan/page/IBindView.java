package test.com.ido.runplan.page;

import com.ido.ble.bluetooth.device.BLEDevice;

/**
 * @Author: ym
 * @ClassName: IBindView
 * @Description:
 * @Package: com.ido.life.module.bind
 * @CreateDate: 2020-03-17 17:45
 */

public interface IBindView {

    /**
     * 需要开启蓝牙
     */
    void onNeedOpenBle();

    /**
     * 连接开始
     */
    void onConnectStart(BLEDevice device);

    /**
     * 连接成功
     */
    void onConnectSuccess(boolean needBind);

    /**
     * 连接失败
     */
    void onConnectFailed(int errorCode);

    /**
     * dfu模式
     *
     * @param device
     */
    void onInDfuMode(BLEDevice device);

    /**
     * 绑定了错误的设备
     *
     * @param device
     */
    void onBindWrongDevice(BLEDevice device);

    /**
     * 绑定驰腾7200设备
     */
    void onBindOrifitDevice();

    /**
     * 获取设备信息成功
     */
    void onGetDeviceInfoSuccess();

    /**
     * 需要验证码
     *
     * @param len 验证码长度
     */
    void onNeedAuthCode(int len);

    /**
     * 需要绑定确认
     *
     * @param shape 形状
     * @param mac   mac地址
     */
    void onNeedConfirm(int shape, String mac);

    /**
     * 绑定成功
     */
    void onBindSuccess();

    /**
     * 绑定超时
     */
    void onBindTimeOut(int errorCode);

    /**
     * 绑定失败
     */
    void onBindFailed(int errorCode, boolean isReject);
}
