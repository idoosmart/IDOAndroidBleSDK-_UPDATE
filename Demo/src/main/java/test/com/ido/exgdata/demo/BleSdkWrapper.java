package test.com.ido.exgdata.demo;

import com.ido.ble.BLEManager;
import com.ido.ble.LocalDataManager;
import com.ido.ble.bluetooth.device.BLEDevice;
import com.ido.ble.business.sync.SyncPara;
import com.ido.ble.callback.AppExchangeDataCallBack;

import com.ido.ble.callback.V3AppExchangeDataCallBack;
import com.ido.ble.protocol.model.AppExchangeDataIngPara;
import com.ido.ble.protocol.model.AppExchangeDataPausePara;
import com.ido.ble.protocol.model.AppExchangeDataResumePara;
import com.ido.ble.protocol.model.AppExchangeDataStartPara;
import com.ido.ble.protocol.model.AppExchangeDataStopPara;
import com.ido.ble.protocol.model.DeviceExchangeDataIngAppReplyPara;
import com.ido.ble.protocol.model.DeviceExchangeDataPauseAppReplyData;
import com.ido.ble.protocol.model.DeviceExchangeDataResumeAppReplyData;
import com.ido.ble.protocol.model.DeviceExchangeDataStartAppReplyData;
import com.ido.ble.protocol.model.DeviceExchangeDataStopAppReplyData;
import com.ido.ble.protocol.model.DeviceNoticeAppExchangeDataPauseAppReplyData;
import com.ido.ble.protocol.model.DeviceNoticeAppExchangeDataResumeAppReplyData;
import com.ido.ble.protocol.model.DeviceNoticeAppExchangeDataStopAppReplyData;
import com.ido.ble.protocol.model.V3AppExchangeDataIngPara;


import java.util.Locale;

import test.com.ido.runplan.sync.BaseConnCallback;

/**
 * Created by lyw.
 *
 * @author: lyw
 * @package: com.ido.veryfitpro.common.ble
 * @description: {@link BLEManager}类的封装
 * @date: 2018/8/8 0008
 */
public class BleSdkWrapper {

    public static boolean isConnected() {
        return BLEManager.isConnected() && BLEManager.isBind();
    }

    public static String getBindMac() {
        BLEDevice bleDevice = LocalDataManager.getLastConnectedDeviceInfo();
        if (bleDevice != null) {
            return bleDevice.mDeviceAddress;
        }
        return "";
    }

    public static void unregisterAppExchangeDataCallBack(AppExchangeDataCallBack.ICallBack callBack) {
        BLEManager.unregisterAppExchangeDataCallBack(callBack);
    }

    public static void registerAppExchangeDataCallBack(AppExchangeDataCallBack.ICallBack callBack) {
        unregisterAppExchangeDataCallBack(callBack);
        BLEManager.registerAppExchangeDataCallBack(callBack);
    }

    public static void replyDeviceNoticeAppExchangeDataStop(DeviceNoticeAppExchangeDataStopAppReplyData data) {
        BLEManager.replyDeviceNoticeAppExchangeDataStop(data);
    }

    public static void replyDeviceNoticeAppExchangeDataPause(DeviceNoticeAppExchangeDataPauseAppReplyData data) {
        BLEManager.replyDeviceNoticeAppExchangeDataPause(data);
    }

    public static void replyDeviceNoticeAppExchangeDataResume(DeviceNoticeAppExchangeDataResumeAppReplyData data) {
        BLEManager.replyDeviceNoticeAppExchangeDataResume(data);
    }

    public static void replyDeviceExchangeDataStart(DeviceExchangeDataStartAppReplyData para) {
        BLEManager.replyDeviceExchangeDataStart(para);
    }

    public static void replyDeviceExchangeDataIng(DeviceExchangeDataIngAppReplyPara para) {
        BLEManager.replyDeviceExchangeDataIng(para);
    }

    public static void replyDeviceExchangeDataStop(DeviceExchangeDataStopAppReplyData para) {
        BLEManager.replyDeviceExchangeDataStop(para);
    }

    public static void replyDeviceExchangeDataPause(DeviceExchangeDataPauseAppReplyData para) {
        BLEManager.replyDeviceExchangeDataPause(para);
    }

    public static void replyDeviceExchangeDataResume(DeviceExchangeDataResumeAppReplyData para) {
        BLEManager.replyDeviceExchangeDataResume(para);
    }

    public static void appSwitchPause(int day, int hour, int minute, int second) {
        AppExchangeDataPausePara data = new AppExchangeDataPausePara();
        data.day = day;
        data.hour = hour;
        data.minute = minute;
        data.second = second;
        BLEManager.appExchangeDataPause(data);
    }

    public static void appSwitchRestore(int day, int hour, int minute, int second) {

        AppExchangeDataResumePara data = new AppExchangeDataResumePara();

        data.day = day;
        data.hour = hour;
        data.minute = minute;
        data.second = second;
        BLEManager.appExchangeDataResume(data);
    }

    public static void appSwitchDataEnd(int day, int hour, int minute, int second, int type, int durations, int calories, int distance, int is_save) {
        AppExchangeDataStopPara data = new AppExchangeDataStopPara();
        data.day = day;
        data.hour = hour;
        data.minute = minute;
        data.second = second;
        data.sport_type = type;
        data.durations = durations;
        data.calories = calories;
        data.distance = distance;
        data.is_save = is_save;
        BLEManager.appExchangeDataStop(data);
    }

    public static void appSwitchDataIng(AppExchangeDataIngPara data) {

        BLEManager.appExchangeDataIng(data);

    }

    public static void appSwitchDataStart(AppExchangeDataStartPara switchDataAppStart) {
        BLEManager.appExchangeDataStart(switchDataAppStart);
    }
    public static void unregisterConnectCallBack(BaseConnCallback callBack) {
        BLEManager.unregisterConnectCallBack(callBack);
    }
    /**
     * 取消v3数据的数据交换
     * @param callBack
     */
    public static void unregisterV3AppExchangeDataCallBack(V3AppExchangeDataCallBack.ICallBack callBack) {
        BLEManager.unregisterV3AppExchangeDataCallBack(callBack);
    }
    /**
     * 注册v3的数据交换
     * @param callBack
     */
    public static void registerV3AppExchangeDataCallBack(V3AppExchangeDataCallBack.ICallBack callBack) {
        unregisterV3AppExchangeDataCallBack(callBack);
        BLEManager.registerV3AppExchangeDataCallBack(callBack);
    }
    public static void registerConnectCallBack(BaseConnCallback callBack) {
        unregisterConnectCallBack(callBack);
        BLEManager.registerConnectCallBack(callBack);
    }
    /**
     * 运动结束
     */
    public static void v3AppSwitchDataEnd() {
        BLEManager.v3getEndActivityData();
    }


    /**
     * 交换v3数据
     * @param data
     */
    public static void v3AppSwitchDataIng(V3AppExchangeDataIngPara data) {
        BLEManager.v3AppExchangeDataIng(data);

    }

    /**
     * v3获取心率交换数据
     */
    public static void getExChangeV3DataHeartRateInterval() {
        BLEManager.v3AppExchangeDataGetHeartRate();
    }
}
