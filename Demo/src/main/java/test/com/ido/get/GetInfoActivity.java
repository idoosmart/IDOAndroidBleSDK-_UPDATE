package test.com.ido.get;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import com.ido.ble.BLEManager;
import com.ido.ble.LocalDataManager;
import com.ido.ble.callback.DeviceParaChangedCallBack;
import com.ido.ble.callback.GetDeviceInfoCallBack;
import com.ido.ble.callback.GetDeviceParaCallBack;
import com.ido.ble.callback.UserHabitCallBack;
import com.ido.ble.gps.callback.GpsCallBack;
import com.ido.ble.gps.model.GPSInfo;
import com.ido.ble.gps.model.GpsHotStartParam;
import com.ido.ble.gps.model.GpsStatus;
import com.ido.ble.protocol.model.ActivityDataCount;
import com.ido.ble.protocol.model.ActivitySwitch;
import com.ido.ble.protocol.model.AlarmV3;
import com.ido.ble.protocol.model.AllHealthMonitorSwitch;
import com.ido.ble.protocol.model.BasicInfo;
import com.ido.ble.protocol.model.BatteryInfo;
import com.ido.ble.protocol.model.CalorieAndDistanceGoal;
import com.ido.ble.protocol.model.CanDownLangInfo;
import com.ido.ble.protocol.model.DeviceChangedPara;
import com.ido.ble.protocol.model.DeviceSummarySoftVersionInfo;
import com.ido.ble.protocol.model.DeviceUpgradeState;
import com.ido.ble.protocol.model.FirmwareAndBt3Version;
import com.ido.ble.protocol.model.FlashBinInfo;
import com.ido.ble.protocol.model.HIDInfo;
import com.ido.ble.protocol.model.BtA2dpHfpStatus;
import com.ido.ble.protocol.model.HabitInfo;
import com.ido.ble.protocol.model.LiveData;
import com.ido.ble.protocol.model.MacAddressInfo;
import com.ido.ble.protocol.model.MenuList;
import com.ido.ble.protocol.model.NotDisturbPara;
import com.ido.ble.protocol.model.NoticeSwitchInfo;
import com.ido.ble.protocol.model.PressCalibrationValue;
import com.ido.ble.protocol.model.SNInfo;
import com.ido.ble.protocol.model.ScheduleReminderV3;
import com.ido.ble.protocol.model.ScreenBrightness;
import com.ido.ble.protocol.model.SupportFunctionInfo;
import com.ido.ble.protocol.model.SupportSportInfoV3;
import com.ido.ble.protocol.model.SystemTime;
import com.ido.ble.protocol.model.UpHandGesture;
import com.ido.ble.protocol.model.WalkReminder;

import java.io.File;
import java.util.List;

import test.com.ido.CallBack.BaseGetDeviceInfoCallBack;
import test.com.ido.R;
import test.com.ido.app2device.AppControlDeviceActivity;
import test.com.ido.connect.BaseAutoConnectActivity;
import test.com.ido.device2app.DeviceControlAppActivity;
import test.com.ido.log.LogPathImpl;
import test.com.ido.utils.GsonUtil;
import test.com.ido.utils.JsonUtils;

public class GetInfoActivity extends BaseAutoConnectActivity {

    private TextView tvResult;


    private GetDeviceInfoCallBack.ICallBack iCallBack = new BaseGetDeviceInfoCallBack()

    {
        @Override
        public void onGetBasicInfo(BasicInfo basicInfo) {
            if (basicInfo != null){
                tvResult.setText(basicInfo.toString());
            }else {
                tvResult.setText("get failed!!!");
            }
        }

        @Override
        public void onGetFunctionTable(SupportFunctionInfo supportFunctionInfo) {
            if (supportFunctionInfo != null){
                tvResult.setText(JsonUtils.format(supportFunctionInfo.toString()));
            }else {
                tvResult.setText("get failed!!!");
            }
        }


        @Override
        public void onGetMacAddress(MacAddressInfo macAddressInfo) {
            if (macAddressInfo != null){
                tvResult.setText(macAddressInfo.toString());
            }else {
                tvResult.setText("get failed!!!");
            }
        }

        @Override
        public void onGetFlashBinInfo(FlashBinInfo flashBinInfo) {
            if (flashBinInfo != null){
                tvResult.setText("" + flashBinInfo.toString());
            }else {
                tvResult.setText("get failed!!!");
            }
        }
    };

    private GetDeviceParaCallBack.ICallBack getParaCallBack = new GetDeviceParaCallBack.ICallBack() {
        @Override
        public void onGetDoNotDisturbPara(NotDisturbPara para) {
            if (para != null){
                tvResult.setText("" + para.toString());
            }else {
                tvResult.setText("get failed!!!");
            }
        }

        @Override
        public void onGetAlarmV3(List<AlarmV3> alarmV3List) {
            if (alarmV3List != null){
                tvResult.setText("" + GsonUtil.toJson(alarmV3List));
            }else {
                tvResult.setText("get failed!!!");
            }
        }

        @Override
        public void onGetScheduleReminderV3(List<ScheduleReminderV3> scheduleReminderV3List) {

        }

        @Override
        public void onGetSupportSportInfoV3(SupportSportInfoV3 supportSportInfoV3) {
            if (supportSportInfoV3 != null){
                tvResult.setText("" + supportSportInfoV3.toString());
            }else {
                tvResult.setText("get failed!!!");
            }
        }

        @Override
        public void onGetScreenBrightness(ScreenBrightness screenBrightness) {
            if (screenBrightness != null){
                tvResult.setText("" + GsonUtil.toJson(screenBrightness));
            }else {
                tvResult.setText("get failed!!!");
            }
        }

        @Override
        public void onGetUpHandGesture(UpHandGesture upHandGesture) {

        }

        @Override
        public void onGetDeviceUpgradeState(DeviceUpgradeState upgradeState) {

        }

        @Override
        public void onGetMenuList(MenuList.DeviceReturnInfo menulist) {
            if(menulist!=null){
                tvResult.setText("" + GsonUtil.toJson(menulist));
            }else {
                tvResult.setText("get menulist  fail ");
            }

        }

        @Override
        public void onGetWalkReminder(WalkReminder walkReminder) {

        }

        @Override
        public void onGetSportThreeCircleGoal(CalorieAndDistanceGoal calorieAndDistanceGoal,String macAddress) {
        }

        @Override
        public void onGetActivitySwitch(ActivitySwitch activitySwitch) {

        }

        @Override
        public void onGetAllHealthMonitorSwitch(AllHealthMonitorSwitch allHealthMonitorSwitch) {

        }

        @Override
        public void onGetFirmwareAndBt3Version(FirmwareAndBt3Version firmwareAndBt3Version) {
            if(firmwareAndBt3Version!=null){
                tvResult.setText(GsonUtil.toJson(firmwareAndBt3Version));
            }else{
                tvResult.setText("onGetFirmwareAndBt3Version fail ");
            }
        }

        @Override
        public void onGetPressCalibrationValue(PressCalibrationValue pressCalibrationValue) {

        }

        @Override
        public void onGetBtA2dpHfpStatus(BtA2dpHfpStatus btA2dpHfpStatus) {

        }

        @Override
        public void onGetContactReceiveTime(boolean needToSendContact) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_info);
        tvResult = (TextView) findViewById(R.id.get_info_result);
        BLEManager.registerGetDeviceInfoCallBack(iCallBack);
        BLEManager.registerGetDeviceParaCallBack(getParaCallBack);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BLEManager.unregisterGetDeviceInfoCallBack(iCallBack);
        BLEManager.unregisterGetDeviceParaCallBack(getParaCallBack);
    }

    public void getBasicInfo(View v){
        tvResult.setText("getBasicInfo...");
        BLEManager.getBasicInfo();
    }

    public void getFunction(View v){
        tvResult.setText("getSupportFunctionInfo...");
        BLEManager.getFunctionTables();
    }

    public void getMacAddress(View v){
       tvResult.setText("getMacAddress...");
        BLEManager.getMacAddress();

    }

    public void getMenulist(View v){
        tvResult.setText("getMenulist...");
        BLEManager.getMenuList();
    }


    public void getGpsState(View v){
        tvResult.setText("getGpsState...");
        BLEManager.getGpsStatus();
        BLEManager.registerGetGpsInfoCallBack(new GpsCallBack.IGetGpsInfoCallBack() {
            @Override
            public void onGetGpsInfo(GPSInfo gpsInfo) {

            }

            @Override
            public void onGetHotStartGpsPara(GpsHotStartParam gpsHotStartParam) {

            }

            @Override
            public void onGetGpsStatus(GpsStatus gpsStatus) {
                if(gpsStatus != null){
                    tvResult.setText("" + GsonUtil.toJson(gpsStatus));
                }else {
                    tvResult.setText("get gpsStatus fail");
                }

            }
        });
    }

    public void getSupportSportInfoV3(View view){
        tvResult.setText("getSportSortV3...");
        BLEManager.getSupportSportInfoV3();
    }

    public void getFireWareVersion(View view){
        tvResult.setText("getFireWareVersion...");
        SupportFunctionInfo functionInfo = LocalDataManager.getSupportFunctionInfo();
        if(functionInfo.V3_support_set_v3_notify_add_app_name){
            BLEManager.getFirmwareAndBt3Version();

        }else {
            tvResult.setText("nou support get Bt version");
        }

    }

    public void NotifyApp(View view){
        tvResult.setText("NotifyApp...");
        DeviceParaChangedCallBack.ICallBack callBack = new DeviceParaChangedCallBack.ICallBack() {
            @Override
            public void onChanged(DeviceChangedPara deviceChangedPara) {

            }
        };
        BLEManager.registerDeviceParaChangedCallBack(callBack);
    }

    public void deviceControlApp(View view){
        startActivity(new Intent(this, DeviceControlAppActivity.class));
    }
    public void getScreenBrightness(View view){
        tvResult.setText("getScreenBrightness...");
        BLEManager.getScreenBrightness();
    }

}
