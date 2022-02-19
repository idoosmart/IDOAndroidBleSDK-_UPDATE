package test.com.ido.device2app;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.ido.ble.BLEManager;
import com.ido.ble.LocalDataManager;
import com.ido.ble.bluetooth.device.BLEDevice;
import com.ido.ble.callback.DeviceControlAppCallBack;
import com.ido.ble.protocol.model.SupportFunctionInfo;

import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;

public class DeviceControlAppActivity extends BaseAutoConnectActivity {


    private TextView tvEvent, tvFindPhone, tvSOS, tvAntiLost;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (device == null || TextUtils.isEmpty(device.getAddress())){
                return;
            }
            Log.d("aaa", "device name: " + device.getAddress());

            SupportFunctionInfo supportFunctionInfo = LocalDataManager.getSupportFunctionInfo(device.getAddress());
            if (supportFunctionInfo == null || !supportFunctionInfo.support_ble_control_photograph){
                return;
            }
            int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1);
            switch (state) {
                case BluetoothDevice.BOND_NONE:
                    Log.d("aaa", "BOND_NONE 删除配对");
                    break;
                case BluetoothDevice.BOND_BONDING:
                    Log.d("aaa", "BOND_BONDING 正在配对");
                    break;
                case BluetoothDevice.BOND_BONDED:
                    Log.d("aaa", "BOND_BONDED 配对成功");
                    BLEDevice bleDevice = LocalDataManager.getLastConnectedDeviceInfo();
                    if (bleDevice != null && device.getAddress().equals(bleDevice.mDeviceAddress)) {
                        BLEManager.autoConnect();
                    }
                    break;
            }
        }
    };

    private DeviceControlAppCallBack.ICallBack iCallBack = new DeviceControlAppCallBack.ICallBack() {
        @Override
        public void onControlEvent(DeviceControlAppCallBack.DeviceControlEventType eventType,int value) {
            handleControlEvent(eventType);
        }

        @Override
        public void onFindPhone(boolean isStart, long timeOut) {
            if (isStart){
                tvFindPhone.setText(R.string.device_control_start);
            }else {
                tvFindPhone.setText(R.string.device_control_end);
            }
        }

        @Override
        public void onOneKeySOS(boolean isStart, long timeOut) {
            if (isStart){
                tvSOS.setText(R.string.device_control_start);
            }else {
                tvSOS.setText(R.string.device_control_end);
            }
        }

        @Override
        public void onAntiLostNotice(boolean isStart, long timeOut) {
            if (isStart){
                tvAntiLost.setText(R.string.device_control_start);
            }else {
                tvAntiLost.setText(R.string.device_control_end);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_control_app);
        tvEvent = (TextView) findViewById(R.id.device_control_app_event);
        tvFindPhone = (TextView) findViewById(R.id.device_control_app_find_phone);
        tvSOS = (TextView) findViewById(R.id.device_control_app_sos);
        tvAntiLost = (TextView) findViewById(R.id.device_control_app_anti_lost);


        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(receiver, intentFilter);
        BLEManager.registerDeviceControlAppCallBack(iCallBack);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        BLEManager.unregisterDeviceControlAppCallBack(iCallBack);
    }

    /**
     * @param eventType
     */
    private void handleControlEvent(DeviceControlAppCallBack.DeviceControlEventType eventType){
        if (eventType == DeviceControlAppCallBack.DeviceControlEventType.START){
            tvEvent.setText(R.string.device_control_app_event_start);
        }else if (eventType == DeviceControlAppCallBack.DeviceControlEventType.PAUSE){
            tvEvent.setText(R.string.device_control_app_event_pause);
        }else if (eventType == DeviceControlAppCallBack.DeviceControlEventType.STOP){
            tvEvent.setText(R.string.device_control_app_event_stop);
        }else if (eventType == DeviceControlAppCallBack.DeviceControlEventType.PREVIOUS){
            tvEvent.setText(R.string.device_control_app_event_pre);
        }else if (eventType == DeviceControlAppCallBack.DeviceControlEventType.NEXT){
            tvEvent.setText(R.string.device_control_app_event_next);
        }else if (eventType == DeviceControlAppCallBack.DeviceControlEventType.TAKE_ONE_PHOTO){
            tvEvent.setText(R.string.device_control_app_event_single_photo);
        }else if (eventType == DeviceControlAppCallBack.DeviceControlEventType.TAKE_MULTI_PHOTO){
            tvEvent.setText(R.string.device_control_app_event_mutil_photo);
        }else if (eventType == DeviceControlAppCallBack.DeviceControlEventType.VOLUME_UP){
            tvEvent.setText(R.string.device_control_app_event_vol_up);
        }else if (eventType == DeviceControlAppCallBack.DeviceControlEventType.VOLUME_DOWN){
            tvEvent.setText(R.string.device_control_app_event_vol_down);
        }else if (eventType == DeviceControlAppCallBack.DeviceControlEventType.OPEN_CAMERA){
            tvEvent.setText(R.string.device_control_app_event_open_camera);
        }else if (eventType == DeviceControlAppCallBack.DeviceControlEventType.CLOSE_CAMERA){
            tvEvent.setText(R.string.device_control_app_event_close_camera);
        }else if (eventType == DeviceControlAppCallBack.DeviceControlEventType.ANSWER_PHONE){
            tvEvent.setText(R.string.device_control_app_event_answer_phone);
        }else if (eventType == DeviceControlAppCallBack.DeviceControlEventType.REJECT_PHONE){
            tvEvent.setText(R.string.device_control_app_event_hand_up_phone);
        }else {
            tvEvent.setText(""+eventType);
        }

        if (eventType == DeviceControlAppCallBack.DeviceControlEventType.REQUEST_PAIRED){
            BLEManager.disConnect();
        }

    }



}
