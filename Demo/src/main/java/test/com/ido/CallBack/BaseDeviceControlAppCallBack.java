package test.com.ido.CallBack;

import com.ido.ble.callback.DeviceControlAppCallBack;

public class BaseDeviceControlAppCallBack implements DeviceControlAppCallBack.ICallBack {
    @Override
    public void onControlEvent(DeviceControlAppCallBack.DeviceControlEventType deviceControlEventType, int var2) {

    }

    @Override
    public void onFindPhone(boolean b, long l) {

    }

    @Override
    public void onOneKeySOS(boolean b, long l) {

    }

    @Override
    public void onAntiLostNotice(boolean b, long l) {

    }
}