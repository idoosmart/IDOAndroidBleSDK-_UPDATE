package test.com.ido.app2device;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.ido.ble.BLEManager;
import com.ido.ble.callback.AppControlDeviceCallBack;

import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;
import test.com.ido.device2app.DeviceControlAppActivity;

public class AppControlDeviceActivity extends BaseAutoConnectActivity {

    private Switch switchPlayMusic, switchCamera, switchFindPhone;
    private EditText etCustomCmdData;
    private AppControlDeviceCallBack.ICallBack iCallBack = new AppControlDeviceCallBack.ICallBack() {
        @Override
        public void onSuccess(AppControlDeviceCallBack.AppControlType type) {
            if (AppControlDeviceCallBack.AppControlType.MUSIC_PLAY_ENTER == type) {
                startActivity(new Intent(AppControlDeviceActivity.this, DeviceControlAppActivity.class));
            }
            else if (AppControlDeviceCallBack.AppControlType.CAMERA_ENTER == type){
                startActivity(new Intent(AppControlDeviceActivity.this, DeviceControlAppActivity.class));

            }else if (AppControlDeviceCallBack.AppControlType.ONCE_SPORT_ENTER == type){


            }else if (AppControlDeviceCallBack.AppControlType.FIND_DEVICE_ENTER == type){

            }else if (AppControlDeviceCallBack.AppControlType.CONTROL_HARDWARE == type){

            }else if (AppControlDeviceCallBack.AppControlType.OPEN_ANCS == type){

            }else if (AppControlDeviceCallBack.AppControlType.CLOSE_ANCS == type){

            }

            Toast.makeText(AppControlDeviceActivity.this, R.string.app_control_device_success, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onFailed(AppControlDeviceCallBack.AppControlType type) {
            Toast.makeText(AppControlDeviceActivity.this, R.string.app_control_device_failed, Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_control_device);
        BLEManager.registerAppControlDeviceCallBack(iCallBack);

        initView();
    }

    private void initView() {
        switchPlayMusic = (Switch) findViewById(R.id.app_control_device_play_music_switch);
        switchCamera = (Switch) findViewById(R.id.app_control_device_camera_switch);
        switchFindPhone = (Switch) findViewById(R.id.app_control_device_find_device_switch);
        etCustomCmdData = findViewById(R.id.custom_cmd_data_et);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BLEManager.unregisterAppControlDeviceCallBack(iCallBack);
    }

    public void entryMusicMode(View v){
        if (switchPlayMusic.isChecked()){
            BLEManager.enterMusicMode();
        }else {
            BLEManager.exitMusicMode();
        }
    }

    public void entryCameraMode(View v){
        if (switchCamera.isChecked()){
            BLEManager.enterCameraMode();
        }else {
            BLEManager.exitCameraMode();
        }
    }

    public void entryOnceSportMode(View v){

    }

    public void startFindDevice(View v){
        if (switchFindPhone.isChecked()){
            BLEManager.startFindDevice();
        }else {
            BLEManager.stopFindDevice();
        }
    }

    public void setDeviceVibrateAndDisplayTime(View v){

    }

    public void measureBlood(View v){
        startActivity(new Intent(this, BloodPressureMeasureActivity.class));
    }

    public void openANCS(View v){
//        BLEManager.openANCS();
    }

    public void closeANCS(View v){
//        BLEManager.closeANCS();
    }

    public void sendCustomCmds(View view){
        String source = etCustomCmdData.getText().toString();
        String bytesStrings[] = source.split("-");
        byte[] cmd = new byte[bytesStrings.length];
        for (int i = 0; i < bytesStrings.length; i ++){
            System.out.print(bytesStrings[i] + " ");
            int parseInt = Integer.parseInt(bytesStrings[i], 16);
//            System.out.print(parseInt + " ");
            byte b = (byte) parseInt;
//            System.out.println(b);
            cmd[i] = b;
        }
//        DeviceManager.writeForce(cmd);
    }
}
