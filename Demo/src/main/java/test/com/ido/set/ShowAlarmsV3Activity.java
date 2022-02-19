package test.com.ido.set;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ido.ble.BLEManager;
import com.ido.ble.callback.GetDeviceParaCallBack;
import com.ido.ble.callback.SettingCallBack;
import com.ido.ble.protocol.model.ActivitySwitch;
import com.ido.ble.protocol.model.AlarmV3;
import com.ido.ble.protocol.model.AllHealthMonitorSwitch;
import com.ido.ble.protocol.model.CalorieAndDistanceGoal;
import com.ido.ble.protocol.model.DeviceUpgradeState;
import com.ido.ble.protocol.model.FirmwareAndBt3Version;
import com.ido.ble.protocol.model.BtA2dpHfpStatus;
import com.ido.ble.protocol.model.MenuList;
import com.ido.ble.protocol.model.NotDisturbPara;
import com.ido.ble.protocol.model.PressCalibrationValue;
import com.ido.ble.protocol.model.ScheduleReminderV3;
import com.ido.ble.protocol.model.ScreenBrightness;
import com.ido.ble.protocol.model.SupportSportInfoV3;
import com.ido.ble.protocol.model.UpHandGesture;
import com.ido.ble.protocol.model.WalkReminder;

import java.util.ArrayList;
import java.util.List;

import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;

public class ShowAlarmsV3Activity extends BaseAutoConnectActivity {
    private ListView lvAlarm;
    private List<AlarmV3> mAlarmList;
    private List<AlarmV3> tempList = new ArrayList<>();
    private SettingCallBack.ICallBack iCallBack = new SettingCallBack.ICallBack() {
        @Override
        public void onSuccess(SettingCallBack.SettingType type, Object returnData) {
            closeProgressDialog();
            Toast.makeText(ShowAlarmsV3Activity.this, "delete ok", Toast.LENGTH_SHORT).show();
            getFromDevice();
        }

        @Override
        public void onFailed(SettingCallBack.SettingType type) {
            closeProgressDialog();
            Toast.makeText(ShowAlarmsV3Activity.this, "delete failed", Toast.LENGTH_SHORT).show();
        }
    };

    private GetDeviceParaCallBack.ICallBack getParaCallBack = new GetDeviceParaCallBack.ICallBack() {
        @Override
        public void onGetDoNotDisturbPara(NotDisturbPara para) {

        }

        @Override
        public void onGetAlarmV3(List<AlarmV3> alarmV3List) {
            mAlarmList = new ArrayList<>();
            if (alarmV3List != null){
                mAlarmList.addAll(alarmV3List);
                lvAlarm.setAdapter(new ShowAlarmsV3Activity.AlarmAdapter(mAlarmList));
            }else {
                lvAlarm.setAdapter(new ShowAlarmsV3Activity.AlarmAdapter(mAlarmList));
            }
        }

        @Override
        public void onGetScheduleReminderV3(List<ScheduleReminderV3> scheduleReminderV3List) {

        }

        @Override
        public void onGetSupportSportInfoV3(SupportSportInfoV3 supportSportInfoV3) {

        }

        @Override
        public void onGetScreenBrightness(ScreenBrightness screenBrightness) {

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
        setContentView(R.layout.activity_show_alarms_v3_acitivity);
        lvAlarm = findViewById(R.id.show_alarm_list);
        lvAlarm.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showProgressDialog("deleting...");
                tempList.clear();
                tempList.addAll(mAlarmList);
                tempList.remove(position);
                BLEManager.setAlarmV3(tempList);
                return true;
            }
        });


    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        BLEManager.unregisterSettingCallBack(iCallBack);
        BLEManager.unregisterGetDeviceParaCallBack(getParaCallBack);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BLEManager.registerSettingCallBack(iCallBack);
        BLEManager.registerGetDeviceParaCallBack(getParaCallBack);
        getFromDevice();

    }

    private void getFromDevice(){
        BLEManager.getAlarmV3();

    }

    public void addAlarm(View view){
        startActivity(new Intent(this, SetAlarmV3Activity.class));
    }

    class AlarmAdapter extends BaseAdapter {
        List<AlarmV3> alarmList;
        AlarmAdapter(List<AlarmV3> alarmList){
            this.alarmList = alarmList;
        }

        @Override
        public int getCount() {
            return alarmList.size();
        }

        @Override
        public Object getItem(int position) {
            return alarmList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = new TextView(getBaseContext());
            AlarmV3 alarm = alarmList.get(position);
            alarm.getWeekRepeat();
            textView.setText(alarm.toString());
            return textView;
        }
    }
}
