package test.com.ido.runplan.sync;

import com.ido.ble.bluetooth.connect.ConnectFailedReason;
import com.ido.ble.bluetooth.device.BLEDevice;
import com.ido.ble.callback.ConnectCallBack;

/**
 * @package: com.ido.life.ble
 * @description: {@link ConnectCallBack.ICallBack} 接口实现类
 */
public class BaseConnCallback implements ConnectCallBack.ICallBack {
    @Override
    public void onConnectStart(String macAddress) {
    }

    @Override
    public void onConnecting(String macAddress) {
    }

    @Override
    public void onRetry(int i,String macAddress) {

    }

    @Override
    public void onConnectSuccess(String macAddress) {

    }

    @Override
    public void onConnectFailed(ConnectFailedReason connectFailedReason, String s) {

    }


    @Override
    public void onConnectBreak(String macAddress) {

    }

    @Override
    public void onInDfuMode(BLEDevice bleDevice) {
    }

    @Override
    public void onDeviceInNotBindStatus(String macAddress) {
    }

    @Override
    public void onInitCompleted(String macAddress) {
    }
}
