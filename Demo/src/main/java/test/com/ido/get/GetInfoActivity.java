package test.com.ido.get;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import com.ido.ble.BLEManager;
//import com.ido.ble.Config;
//import com.ido.ble.callback.BaseGetDeviceInfoCallBack;
import com.ido.ble.callback.GetDeviceInfoCallBack;
import com.ido.ble.callback.GetDeviceParaCallBack;
import com.ido.ble.protocol.model.ActivityDataCount;
import com.ido.ble.protocol.model.ActivitySwitch;
import com.ido.ble.protocol.model.AlarmV3;
import com.ido.ble.protocol.model.AllHealthMonitorSwitch;
import com.ido.ble.protocol.model.BasicInfo;
import com.ido.ble.protocol.model.BatteryInfo;
import com.ido.ble.protocol.model.CalorieAndDistanceGoal;
import com.ido.ble.protocol.model.CanDownLangInfo;
import com.ido.ble.protocol.model.CanDownLangInfoV3;
import com.ido.ble.protocol.model.DeviceSummarySoftVersionInfo;
import com.ido.ble.protocol.model.DeviceUpgradeState;
import com.ido.ble.protocol.model.FirmwareAndBt3Version;
import com.ido.ble.protocol.model.FlashBinInfo;
import com.ido.ble.protocol.model.HIDInfo;
import com.ido.ble.protocol.model.BtA2dpHfpStatus;
import com.ido.ble.protocol.model.LiveData;
import com.ido.ble.protocol.model.MacAddressInfo;
import com.ido.ble.protocol.model.MenuList;
import com.ido.ble.protocol.model.NotDisturbPara;
import com.ido.ble.protocol.model.NoticeReminderSwitchStatus;
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

import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;
import test.com.ido.utils.GsonUtil;
import test.com.ido.utils.JsonUtils;

public class GetInfoActivity extends BaseAutoConnectActivity {

    private TextView tvResult;

    private GetDeviceInfoCallBack.ICallBack iCallBack = new GetDeviceInfoCallBack.ICallBack()

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
        public void onGetTime(SystemTime time) {
            if (time != null){
                tvResult.setText(time.toString());
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
        public void onGetBatteryInfo(BatteryInfo batteryInfo) {
            if (batteryInfo != null){
                tvResult.setText(batteryInfo.toString());
            }else {
                tvResult.setText("get failed!!!");
            }
        }

        @Override
        public void onGetSNInfo(SNInfo snInfo) {
            if (snInfo != null){
                tvResult.setText(snInfo.toString());
            }else {
                tvResult.setText("get failed!!!");
            }
        }

        @Override
        public void onGetNoticeCenterSwitchStatus(NoticeSwitchInfo info) {
            if (info != null){
                tvResult.setText(info.toString());
            }else {
                tvResult.setText("get failed!!!");
            }
        }

        @Override
        public void onGetLiveData(LiveData liveData) {
            if (liveData != null){
                tvResult.setText(liveData.toString());
            }else {
                tvResult.setText("get failed!!!");
            }
        }

        @Override
        public void onGetHIDInfo(HIDInfo hidInfo) {
            if (hidInfo != null){
                tvResult.setText(hidInfo.toString());
            }else {
                tvResult.setText("get failed!!!");
            }
        }

        @Override
        public void onGetActivityCount(ActivityDataCount activityDataCount) {
            if (activityDataCount != null){
                tvResult.setText("" + activityDataCount.toString());
            }else {
                tvResult.setText("get failed!!!");
            }
        }

        @Override
        public void onGetDeviceSummarySoftVersionInfo(DeviceSummarySoftVersionInfo info) {
            if (info != null){
                tvResult.setText("" + info.toString());
            }else {
                tvResult.setText("get failed!!!");
            }
        }

        @Override
        public void onGetCanDownloadLangInfo(CanDownLangInfo canDownLangInfo) {
            if (canDownLangInfo != null){
                tvResult.setText("" + canDownLangInfo.toString());
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

        @Override
        public void onGetCanDownloadLangInfoV3(CanDownLangInfoV3 canDownLangInfoV3) {
            if (canDownLangInfoV3 != null){
                tvResult.setText("" + canDownLangInfoV3.toString());
            }else {
                tvResult.setText("get failed!!!");
            }
        }

        @Override
        public void onGetNoticeReminderSwitchStatus(NoticeReminderSwitchStatus noticeReminderSwitchStatus) {
            if (noticeReminderSwitchStatus != null){
                tvResult.setText("" + noticeReminderSwitchStatus.toString());
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
        public void onGetMenuList(MenuList.DeviceReturnInfo deviceReturnInfo) {

        }

        @Override
        public void onGetWalkReminder(WalkReminder walkReminder) {
            if (walkReminder != null){
                tvResult.setText("" + walkReminder.toString());
            }else {
                tvResult.setText("get failed!!!");
            }
        }

        @Override
        public void onGetSportThreeCircleGoal(CalorieAndDistanceGoal calorieAndDistanceGoal,String macAddress) {
            if (calorieAndDistanceGoal != null){
                tvResult.setText("" + calorieAndDistanceGoal.toString());
            }else {
                tvResult.setText("get failed!!!");
            }
        }

        @Override
        public void onGetActivitySwitch(ActivitySwitch activitySwitch) {

        }

        @Override
        public void onGetAllHealthMonitorSwitch(AllHealthMonitorSwitch allHealthMonitorSwitch) {

        }

        @Override
        public void onGetFirmwareAndBt3Version(FirmwareAndBt3Version firmwareAndBt3Version) {

        }

        @Override
        public void onGetPressCalibrationValue(PressCalibrationValue pressCalibrationValue) {

        }

        @Override
        public void onGetBtA2dpHfpStatus(BtA2dpHfpStatus btA2dpHfpStatus) {

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

//    public static String getRootPath(){
//            String root = Environment.getExternalStorageDirectory().getAbsolutePath();
//            if (Build.VERSION.SDK_INT >= 29){
//                File file = Config.getApplication().getExternalFilesDir("");
//                if (file != null){
//                    root = file.getAbsolutePath();
//                }
//            }
//        root = root
//                    + File.separator + "IDO_BLE_SDK" + File.separator + Config.getApplication().getPackageName() + File.separator + "Flash.txt";
//        return root;
//    }
    public void getTime(View v){
        tvResult.setText("getTime...");

//        BLEManager.getTime();
    }

    public void getMacAddress(View v){
//        tvResult.setText("getMacAddress...");
        BLEManager.getMacAddress();
//        BLEManager.collectDeviceFlashLog(getRootPath(), 20, new ICollectFlashLogListener() {
//            @Override
//            public void onStart() {
//
//            }
//
//            @Override
//            public void onFinish() {
//
//            }
//        });
    }

    public void getBatteryInfo(View v){
        tvResult.setText("getBatteryInfo...");
        BLEManager.getBatteryInfo();
    }

    public void getSN(View v){
        tvResult.setText("getSN...");
//        BLEManager.getSNInfo();

    }

    public void getNoticeSwitchStatus(View v){
        tvResult.setText("getNoticeSwitchStatus...");
//        BLEManager.getNoticeSwitchState();
    }

    public void getLiveData(View v){
        tvResult.setText("getLiveData...");
        BLEManager.getLiveData();
    }

    public void getHidInfo(View v){
        tvResult.setText("getHidInfo...");
//        BLEManager.getHIDInfo();
        BLEManager.getDeviceHeatLog();
    }

    public void getActivityCount(View v){
        tvResult.setText("getActivityCount...");
        BLEManager.getActivityCount();
    }
    public void getNotDisturbPara(View view){
        tvResult.setText("getNotDisturbPara...");
        BLEManager.getDoNotDisturbPara();
    }
    public void getAlarmV3(View view){
        tvResult.setText("getAlarm...");
        BLEManager.getAlarmV3();
    }
    public void getSupportSportInfoV3(View view){
        tvResult.setText("getSportSortV3...");
//        BLEManager.getSupportSportInfoV3();
        BLEManager.getScreenBrightness();
    }

    public void getWalkReminder(View view){
        tvResult.setText("getWalkReminder...");
        BLEManager.getWalkReminder();
    }

    public void getSportThreeCircleGoal(View view){
        tvResult.setText("getSportThreeCircleGoal...");
        BLEManager.getSportThreeCircleGoal();
    }

}
