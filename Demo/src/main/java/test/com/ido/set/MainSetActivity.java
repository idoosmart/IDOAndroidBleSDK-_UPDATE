package test.com.ido.set;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.ido.ble.BLEManager;
import com.ido.ble.LocalDataManager;
import com.ido.ble.callback.OperateCallBack;
import com.ido.ble.callback.OtherProtocolCallBack;
import com.ido.ble.callback.QueryStatusCallBack;
import com.ido.ble.callback.RebootCallback;
import com.ido.ble.callback.SettingCallBack;
import com.ido.ble.dfu.BleDFUConfig;
import com.ido.ble.protocol.model.ActivitySwitch;
import com.ido.ble.protocol.model.AntiLostMode;
import com.ido.ble.protocol.model.BloodPressureAdjustDeviceReplyInfo;
import com.ido.ble.protocol.model.BloodPressureAdjustPara;
import com.ido.ble.protocol.model.BloodPressureQueryAdjustResultPara;
import com.ido.ble.protocol.model.CalorieAndDistanceGoal;
import com.ido.ble.protocol.model.DialPlate;
import com.ido.ble.protocol.model.DisplayMode;
import com.ido.ble.protocol.model.DrinkWaterReminder;
import com.ido.ble.protocol.model.FrequentContactsV3;
import com.ido.ble.protocol.model.Goal;
import com.ido.ble.protocol.model.HandWearMode;
import com.ido.ble.protocol.model.Menstrual;
import com.ido.ble.protocol.model.MenstrualRemind;
import com.ido.ble.protocol.model.MenuList;
import com.ido.ble.protocol.model.MusicControlInfo;
import com.ido.ble.protocol.model.NightTemperatureMonitoringPara;
import com.ido.ble.protocol.model.NoisePara;
import com.ido.ble.protocol.model.NoticeDevicePermmsion;
import com.ido.ble.protocol.model.NoticeReminderSwitchStatus;
import com.ido.ble.protocol.model.PhoneVoice;
import com.ido.ble.protocol.model.PressureParam;
import com.ido.ble.protocol.model.SPO2Param;
import com.ido.ble.protocol.model.ScheduleReminderV3;
import com.ido.ble.protocol.model.ScreenBrightness;
import com.ido.ble.protocol.model.ShortCut;
import com.ido.ble.protocol.model.SosSwitch;
import com.ido.ble.protocol.model.SportModeSort;
import com.ido.ble.protocol.model.SportType;
import com.ido.ble.protocol.model.SupportFunctionInfo;
import com.ido.ble.protocol.model.WalkReminder;
import com.ido.ble.protocol.model.WeatherInfo;
import com.ido.ble.protocol.model.WeatherInfoV3;
import com.ido.ble.protocol.model.WorldTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import test.com.ido.HomeActivity;
import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;
import test.com.ido.utils.DateUtil;

public class MainSetActivity extends BaseAutoConnectActivity {

    private EditText etGoal, etSystolic , etDiastolic, etScreenBrigthness, etWeatherCityName;
    private TextView tvQueryBloodAdjustResult;
    private RadioGroup rgbHandWearMode;
    private int mHandWearMode = HandWearMode.HAND_MODE_LEFT;
    private Switch switchFindPhone, switchMusic, switchSOS, switchWeather;
    private boolean mIsSetFindPhoneOpen = false;

    private QueryStatusCallBack.ICallBack queryCallBack = new QueryStatusCallBack.ICallBack() {
        @Override
        public void onQueryBloodAdjustResult(BloodPressureAdjustDeviceReplyInfo.BloodAdjustResult result) {
            tvQueryBloodAdjustResult.setText("" + result.toString());
        }
    };

    private RebootCallback.ICallBack rebootCallBack = new RebootCallback.ICallBack() {
        @Override
        public void onSuccess() {
            Toast.makeText(MainSetActivity.this, R.string.reboot_device_tip_ok, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailed() {
            Toast.makeText(MainSetActivity.this, R.string.reboot_device_tip_failed, Toast.LENGTH_SHORT).show();
        }
    };

    private SettingCallBack.ICallBack iCallBack = new SettingCallBack.ICallBack() {
        @Override
        public void onSuccess(SettingCallBack.SettingType type, Object returnData) {
            Toast.makeText(MainSetActivity.this, R.string.set_para_tip_msg_ok, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailed(SettingCallBack.SettingType type) {
            Toast.makeText(MainSetActivity.this, R.string.set_para_tip_msg_failed, Toast.LENGTH_SHORT).show();
        }
    };

    private OperateCallBack.ICallBack operateICallBack = new OperateCallBack.ICallBack(){

        @Override
        public void onSetResult(OperateCallBack.OperateType type, boolean isSuccess) {
            Toast.makeText(MainSetActivity.this,"onSetResult " + isSuccess, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAddResult(OperateCallBack.OperateType type, boolean isSuccess) {

        }

        @Override
        public void onDeleteResult(OperateCallBack.OperateType type, boolean isSuccess) {

        }

        @Override
        public void onModifyResult(OperateCallBack.OperateType type, boolean isSuccess) {

        }

        @Override
        public void onQueryResult(OperateCallBack.OperateType type, Object returnData) {
            Toast.makeText(MainSetActivity.this,"onQueryResult", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        BLEManager.getFunctionTables();
        initView();
        initData();

    }

    private void initData() {
        Goal goal = LocalDataManager.getGoal();
        if (goal != null){
            etGoal.setText(goal.sport_step + "");
        }

        BloodPressureAdjustPara pressureAdjustPara = LocalDataManager.getBloodPressureAdjustPara();
        if (pressureAdjustPara != null){
            etDiastolic.setText(pressureAdjustPara.diastolic+ "");
            etSystolic.setText(pressureAdjustPara.systolic+ "");
        }

        HandWearMode handWearMode = LocalDataManager.getHandWearMode();
        if (handWearMode != null){
            if (handWearMode.hand == HandWearMode.HAND_MODE_LEFT){
                rgbHandWearMode.check(R.id.set_para_hand_wear_mode_left);
            }else if (handWearMode.hand == HandWearMode.HAND_MODE_RIGHT){
                rgbHandWearMode.check(R.id.set_para_hand_wear_mode_right);
            }
        }

        switchFindPhone.setChecked(LocalDataManager.getFindPhoneSwitch());
        switchMusic.setChecked(LocalDataManager.getMusicSwitch());

        switchSOS.setChecked(LocalDataManager.getOneKeySOSSwitch());
        switchWeather.setChecked(LocalDataManager.getWeatherSwitch());

    }

    private void initView(){
        etGoal = (EditText) findViewById(R.id.set_para_goal_et);
        etSystolic = (EditText) findViewById(R.id.set_para_blood_sys);
        etDiastolic = (EditText) findViewById(R.id.set_para_blood_dia);
        rgbHandWearMode = (RadioGroup) findViewById(R.id.set_para_hand_wear_mode_group);
        rgbHandWearMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.set_para_hand_wear_mode_left:
                        mHandWearMode = HandWearMode.HAND_MODE_LEFT;
                        break;
                    case R.id.set_para_hand_wear_mode_right:
                        mHandWearMode = HandWearMode.HAND_MODE_RIGHT;
                        break;

                }
            }
        });

        switchFindPhone = (Switch) findViewById(R.id.set_para_find_phone_switch);
        switchFindPhone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mIsSetFindPhoneOpen = isChecked;
            }
        });

        switchMusic = (Switch) findViewById(R.id.set_para_music_switch);

        etScreenBrigthness = findViewById(R.id.set_screen_brightness);
        etWeatherCityName = findViewById(R.id.set_weather_city_name);
        switchSOS = (Switch) findViewById(R.id.set_para_sos_switch);
        switchWeather = (Switch) findViewById(R.id.set_para_weather_switch);
        tvQueryBloodAdjustResult = findViewById(R.id.set_para_query_blood_adjust_result_tv);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BLEManager.registerOperateCallBack(operateICallBack);
        BLEManager.registerQueryStatusCallBack(queryCallBack);
        BLEManager.registerOtherProtocolCallBack(otherICallBack);
    }

    @Override
    protected void onStop() {
        super.onStop();
        BLEManager.unregisterOperateCallBack(operateICallBack);
        BLEManager.unregisterQueryStatusCallBack(queryCallBack);
        BLEManager.unregisterOtherProtocolCallBack(otherICallBack);
    }

    /**
     * 1 setTime
     * @param v
     */
    public void setTime(View v){
        startActivity(new Intent(this, SetTimeActivity.class));
    }

    /**
     * 2 setfindPhone
     * @param v
     */
    public void setFindPhoneSwitch(View v){
        BLEManager.setFindPhoneSwitch(mIsSetFindPhoneOpen);
    }

    /**
     * 3 set alarm
     * @param v
     */
    public void setAlarm(View v){
        startActivity(new Intent(this, ShowAlarmsActivity.class));
    }

    /**
     * 3 set alarm v3
     * @param v
     */
    public void setAlarmV3(View v){
        startActivity(new Intent(this, ShowAlarmsV3Activity.class));
    }


    /**
     * 4. set step goal
     * @param v
     */
    public void setGoal(View v){
        Goal goal = new Goal();
        goal.sport_step = Integer.parseInt(etGoal.getText().toString());
        BLEManager.setGoal(goal);
    }

    /**
     * 5 reboot
     * @param v
     */
    public void setReboot(View v){
        BLEManager.registerRebootCallBack(rebootCallBack);
        BLEManager.reBoot();
    }

    /**
     * 6 restoreFactory
     * @param v
     */
    public void setRestoreFactory(View v){

        BLEManager.setRestoreFactory();
    }

    /**
     * 7  setUphandGesture
     * @param v
     */
    public void setUpHandGesture(View v){
        startActivity(new Intent(this, SetUpHandGestureActivity.class));
    }

    /**
     * 8 setMusicSwitch
     * @param v
     */
    public void setMusicSwitch(View v){
        BLEManager.setMusicSwitch(switchMusic.isChecked());
    }


    /**
     * 9 setMusicControlInfo
     * @param v
     */
    public void setMusicControlInfo(View v){
        MusicControlInfo musicInfo = new MusicControlInfo();
        musicInfo.status = MusicControlInfo.STATUS_PLAY;
        musicInfo.musicName = "boy";
        //musicInfo.singerName = "";//V3_music_control_02_add_singer_name = true 才支
        BLEManager.setMusicControlInfo(musicInfo);
    }

    /**
     * 10 setPhoneVoice
     * @param v
     */
    public void setPhoneVoice(View v){
        PhoneVoice phoneVoice = new PhoneVoice();
        phoneVoice.total_voice = 100;//最大音量
        phoneVoice.now_voice = 50;//当前音量
        BLEManager.setPhoneVoice(phoneVoice);
    }

    /**
     * 11 setNotDisturb
     * @param v
     */
    public void setNotDisturb(View v){
        startActivity(new Intent(this, SetNotDisturbActivity.class));
    }

    /**
     * 12 setUserInfo
     * @param v
     */
    public void setUserInfo(View v){
        startActivity(new Intent(this, SetUserInfoActivity.class));
    }

    /**
     * 13 setUnit
     * @param v
     */
    public void setUnit(View v){
        startActivity(new Intent(this, SetUnitActivity.class));
    }

    /**
     * 14 setScreenBrightness
     * @param view
     */
    public void setScreenBrightness(View view){
        BLEManager.setScreenBrightness(Integer.parseInt(etScreenBrigthness.getText().toString()));
    }

    /**
     * 15 setMenstrual
     * @param v
     */
    public void setMenstrual(View v){
        Menstrual menstrual = new Menstrual();
        menstrual .menstrual_length = 7;
        menstrual .menstrual_cycle = 30;
        menstrual .last_menstrual_year = 2021;
        menstrual .last_menstrual_month = 9;
        menstrual .last_menstrual_day = 3;
        BLEManager.setMenstrual(menstrual);
    }
    /**
     * 16 setLongSit
     * @param v
     */
    public void setLongSit(View v){
        startActivity(new Intent(this, SetLongSitActivity.class));
    }

    /**
     * 17 setDrinkWaterReminder
     * @param v
     */
    public void setDrinkWaterReminder(View v){
        DrinkWaterReminder waterReminder = new DrinkWaterReminder();
        waterReminder.setStartHour(9);
        waterReminder.setStartMinute(0);
        waterReminder.setEndHour(18);
        waterReminder.setEndMinute(0);
        waterReminder.setInterval(30);
        waterReminder.setWeeks(new boolean[]{true, true, true, true, true, false, false});
        //调用接口
        BLEManager.setDrinkWaterReminder(waterReminder);
    }


    /**
     * 18 setWeatherSwitch
     * @param v
     */
    public void setWeatherSwitch(View v){
        BLEManager.setWeatherSwitch(switchWeather.isChecked());
    }

    /**
     * 18 setWearther
     * @param view
     */
    public void setWearther(View view){
        SupportFunctionInfo supportFunctionInfo =  LocalDataManager.getSupportFunctionInfo();
        if(supportFunctionInfo!=null && supportFunctionInfo.V3_support_set_v3_weather){
            WeatherInfoV3 weatherInfoV3  = getWeatherInfoV3New();
            BLEManager.setWeatherDataV3(weatherInfoV3);
        }else {
            WeatherInfo weatherInfo = new WeatherInfo();
            weatherInfo.type = WeatherInfo.WEATHER_TYPE_CLOUDY;
            weatherInfo.max_temp = 25;
            weatherInfo.max_temp = 16;
            BLEManager.setWeatherData(weatherInfo);
        }

    }
    /**
     * 19 sos
     * @param v
     */
    public void setOneKeySOS(View v){
        BLEManager.setOneKeySOSSwitch(switchSOS.isChecked());
    }

    /**
     * 20 setWalkReminder
     * @param v
     */
    public void setWalkReminder(View v){
        WalkReminder walkReminder =  getWalkReminderState();//上面获取到的状态，下面是用户修改参数设置
        walkReminder.setStartHour(9);
        walkReminder.setStartMinute(0);
        walkReminder.setEndHour(18);
        walkReminder.setEndMinute(30);
        walkReminder.setGoalStep(500);
        BLEManager.setWalkReminder(walkReminder );
    }

    /**
     * 21 setMenstrualRemind
     * @param v
     */
    public void setMenstrualRemind(View v){
        MenstrualRemind menstrualRemind = new MenstrualRemind();
        menstrualRemind.start_day = 3;
        menstrualRemind.ovulation_day = 3;
        menstrualRemind.hour = 9;
        menstrualRemind.minute = 30;
        menstrualRemind.pregnancy_day_before_remind = 5;
        menstrualRemind.pregnancy_day_end_remind = 2;
        menstrualRemind.menstrual_day_end_remind = 1;
        BLEManager.setMenstrualRemind(menstrualRemind);
    }

    /**
     * 22 setMotionintelligent
     * @param v
     */
    public void setMotionintelligent(View v){
        ActivitySwitch mMotionRecognitionState =  getMotionRecognitionState();
        mMotionRecognitionState.autoIdentifySportRun = ActivitySwitch.SWITCH_ON;
        BLEManager.setActivitySwitch(mMotionRecognitionState );

    }

    /**
     * 23 setHeartRateMode
     * @param v
     */
    public void setHeartRateMode(View v){
        startActivity(new Intent(this, SetHeartRateModeActivity.class));
    }

    /**
     * 24 setPressureParam
     * @param v
     */
    public void setPressureParam(View v){
        PressureParam pressureParam = new PressureParam();
        pressureParam.onOff = PressureParam.STATE_ON;
        pressureParam.startHour  = 9;
        pressureParam.startMinute = 0;
        BLEManager.setPressureParam(pressureParam);
    }

    /**
     * 25 setMenuList
     * @param v
     */
    public void setMenuList(View v){
        MenuList menuList = new MenuList();
        menuList.items.add(MenuList.MENU_ALARM);
        menuList.items.add(MenuList.MENU_PRESSURE);//调用接口
        BLEManager.setMenuList(menuList);
    }

    /**
     * 26 setFrequentContactsV3
     * @param v
     */
    public void setFrequentContactsV3(View v){
        List<FrequentContactsV3> list = new ArrayList<>();
        FrequentContactsV3 frequentContactsV3 = new FrequentContactsV3();
        frequentContactsV3.name = "lisan";
        frequentContactsV3.phone = "133356894526";
        list.add(frequentContactsV3);
        BLEManager.setFrequentContactsV3(list);
    }

    /**
     * 27 CallReminderSwitch
     * @param v
     */
    public void CallReminderSwitch(View v){
        SupportFunctionInfo supportFunctionInfo =  LocalDataManager.getSupportFunctionInfo();
        if(supportFunctionInfo!=null && supportFunctionInfo.V3_support_sync_contact){
            NoticeReminderSwitchStatus status = new NoticeReminderSwitchStatus();
            status.notify_switch = NoticeReminderSwitchStatus.NOTIFY_88;
            status.call_switch = NoticeReminderSwitchStatus.SWITCH_ON;
            BLEManager.setNoticeReminderSwitchStatus(status);
        }
    }

    /**
     * 28  ScheduleReminder
     * @param v
     */
    public void ScheduleReminder(View v){
        List<ScheduleReminderV3> list = new ArrayList<>();
        ScheduleReminderV3 scheduleReminderV3 = new ScheduleReminderV3();
        scheduleReminderV3.setTitle("test");
        scheduleReminderV3.setNote("please ....;.");
        scheduleReminderV3.setYear(2022);
        scheduleReminderV3.setMon(8);
        scheduleReminderV3.setDay(5);
        scheduleReminderV3.setHour(12);
        scheduleReminderV3.setMin(30);
        scheduleReminderV3.setRemind_on_off(1);// 1 on   0 off
        scheduleReminderV3.setState(2);// REMIND_STATE_ENABLE = 2;  REMIND_STATE_DELETE = 1;REMIND_STATE_INVALID = 0;
        list.add(scheduleReminderV3);
        BLEManager.addScheduleReminderV3(list);
    }

    /**
     * 29  setWorldTime
     * @param v
     */
    public void setWorldTime(View v){
        List<WorldTime.Item> list = new ArrayList<>();
        WorldTime.Item item = new WorldTime.Item();
        item.city_name = "Beijing";
        item.id = 31;
        item.min_offset = 480;
        item.sunset_hour = 4;
        item.sunrise_min = 45;
        item.sunset_hour = 19;
        item.sunrise_min = 44;
        item.latitude_flag = 1;
        item.latitude = 3390;// 33.90 *100
        item.longitude_flag = 1;
        item.longitude = 11641; // 116.41*100
        list.add(item);
        BLEManager.setWorldTime(list);
    }

    /**
     * 30  setSCreenBrightnessConfig
     * @param v
     */
    public void setSCreenBrightnessConfig(View v){
        ScreenBrightness brights = new ScreenBrightness();
        brights.level = 60;
        brights.mode = 2;
        brights.autoAdjustNight = 3;
        brights.startHour = 19;
        brights.startMinute = 0;
        brights.endHour = 6;
        brights.endMinute = 0;
        brights.nightLevel = 0;
        brights.showInterval = 5;
        BLEManager.setSCreenBrightnessConfig(brights);
    }
    /**
     * 31  setcameraPermission, support only tit03
     * @param v
     */
    public void setcameraPermission(View v){
        SupportFunctionInfo supportFunctionInfo =  LocalDataManager.getSupportFunctionInfo();
        if(supportFunctionInfo!=null && supportFunctionInfo.v2_support_disable_func){
            NoticeDevicePermmsion permmsion = new NoticeDevicePermmsion();
            permmsion.type = NoticeDevicePermmsion.CAMERA_PERMMSION;
            permmsion.enable =  NoticeDevicePermmsion.STATUS_ON ;
            BLEManager.setNoticeDeviceDiableFunc(permmsion);
        }

    }

    /**
     * 32  setNoisePara
     * @param v
     */
    public void setNoisePara(View v){
        SupportFunctionInfo supportFunctionInfo =  LocalDataManager.getSupportFunctionInfo();
        if(supportFunctionInfo!=null && supportFunctionInfo.V3_health_sync_noise){
            NoisePara para = new NoisePara();
            para.mode = NoisePara.MODE_OFF;
            BLEManager.setNoisePara(para);
        }
    }
    /**
     * 33  setNightTemperatureMonitoringPara
     * @param v
     */
    public void setNightTemperatureMonitoringPara(View v){
        SupportFunctionInfo supportFunctionInfo =  LocalDataManager.getSupportFunctionInfo();
        if(supportFunctionInfo!=null && supportFunctionInfo.V3_support_set_temperature_switch){
            NightTemperatureMonitoringPara param = new NightTemperatureMonitoringPara();
            param.mode = NightTemperatureMonitoringPara.SWITCH_OFF;
            param.unit= NightTemperatureMonitoringPara.UNIT_F; BLEManager.setNightTemperatureMonitoringPara(param);
        }
    }

    /**
     * 34  setSPO2Param
     * @param v
     */
    public void setSPO2Param(View v){
        SupportFunctionInfo supportFunctionInfo =  LocalDataManager.getSupportFunctionInfo();
        if(supportFunctionInfo!=null && supportFunctionInfo.V3_support_set_spo2_all_day_on_off){
            SPO2Param spO2Param =  spO2Param = new SPO2Param();
            spO2Param.startHour = 0;
            spO2Param.startMinute = 0;
            spO2Param.endHour = 23;
            spO2Param.endMinute = 59;
            spO2Param.lowSpo2OnOff = SPO2Param.STATE_OFF;
            spO2Param.onOff = SPO2Param.STATE_OFF;
            spO2Param.lowSpo2OnValue = 85;
            BLEManager.setSPO2Param(spO2Param );
        }
    }

    /**
     * 35  setCalorieAndDistanceGoal
     * @param v
     */
    public void setCalorieAndDistanceGoal(View v){
        CalorieAndDistanceGoal calorieAndDistanceGoal = new CalorieAndDistanceGoal();
        calorieAndDistanceGoal .calorie  = 100;
        calorieAndDistanceGoal .distance = 100;
        BLEManager.setCalorieAndDistanceGoal(calorieAndDistanceGoal);
    }

    /**
     * 36 setHandWearMode
     * @param v
     */
    public void setHandWearMode(View v){
        HandWearMode handWearMode = new HandWearMode();
        handWearMode.hand = mHandWearMode;
        BLEManager.setHandWearMode(handWearMode);

    }


    public void setQuickSportMode(View v){
        startActivity(new Intent(this, SetQuickSportModeActivity.class));
    }


    public void setSleepMonitoringPara(View v){
        startActivity(new Intent(this, SetSleepMonitoringActivity.class));
    }

    public void setWeatherCityName(View view){
        BLEManager.setWeatherCityName(etWeatherCityName.getText().toString());
    }


    private OtherProtocolCallBack.ICallBack otherICallBack = new OtherProtocolCallBack.ICallBack(){

        @Override
        public void onSuccess(OtherProtocolCallBack.SettingType type) {
            Toast.makeText(MainSetActivity.this,"设置成功" + type, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailed(OtherProtocolCallBack.SettingType type) {
            Toast.makeText(MainSetActivity.this,"设置失败" + type, Toast.LENGTH_SHORT).show();
        }
    };

    public void setPhoneSystemInfo(View v){

    }


    public void setSportModeSort(View view){
        SportModeSort sportModeSort = new SportModeSort();
        SportModeSort.SportModeSortItem item1 = new  SportModeSort.SportModeSortItem();
        item1.index = 1;
        item1.type = SportType.SPORT_TYPE_WALK;

        SportModeSort.SportModeSortItem item2 = new  SportModeSort.SportModeSortItem();
        item2.index = 2;
        item2.type = SportType.SPORT_TYPE_CYCLING;

        SportModeSort.SportModeSortItem item3 = new  SportModeSort.SportModeSortItem();
        item3.index = 3;
        item3.type = SportType.SPORT_TYPE_SWIM;

        sportModeSort.items = new SportModeSort.SportModeSortItem[]{item1,item2,item3};
        BLEManager.setSportModeSortInfo(sportModeSort);
    }

    public void querySportSubItemParaSort(View view){
//        BLEManager.querySportSubItemParaSort(48);
        PhoneVoice phoneVoice = new PhoneVoice();
        phoneVoice.now_voice = 55;
        phoneVoice.total_voice = 100;
        BLEManager.setPhoneVoice(phoneVoice);
    }


    private WeatherInfoV3 getWeatherInfoV3(){
        WeatherInfoV3 weatherInfoV3 = new WeatherInfoV3();
        Date date = new Date();
        weatherInfoV3.version = 1;
        weatherInfoV3.month = DateUtil.getCurrentMonth();
        weatherInfoV3.day = DateUtil.getCurrentDay();
        weatherInfoV3.hour = DateUtil.getCurrentHour();
        weatherInfoV3.min = DateUtil.getCurrentMin();
        weatherInfoV3.sec = DateUtil.getCurrentSecond();
        weatherInfoV3.week = DateUtil.getDataWeekDay(date);

        weatherInfoV3.sunrise_hour = 10;
        weatherInfoV3.sunrise_min = 10;
        weatherInfoV3.sunset_hour = 2;
        weatherInfoV3.sunset_min = 2;
        weatherInfoV3.wind_speed = 10;
        weatherInfoV3.today_uv_intensity = 10;
        weatherInfoV3.precipitation_probability =10;
        return weatherInfoV3;
    }

    private WeatherInfoV3 getWeatherInfoV3New(){
        WeatherInfoV3 weatherInfoV3 = new WeatherInfoV3();
        Date date = new Date();
        weatherInfoV3.version = 3;
        weatherInfoV3.month = DateUtil.getCurrentMonth();
        weatherInfoV3.day = DateUtil.getCurrentDay();
        weatherInfoV3.hour = DateUtil.getCurrentHour();
        weatherInfoV3.min = DateUtil.getCurrentMin();
        weatherInfoV3.sec = DateUtil.getCurrentSecond();
        weatherInfoV3.week = DateUtil.getDataWeekDay(date);

        weatherInfoV3.weather_type  = 2;
        weatherInfoV3.today_tmp = 126;
        weatherInfoV3.today_max_temp = 128;
        weatherInfoV3.today_min_temp = 122;
        weatherInfoV3.city_name = "shenzhen";
        weatherInfoV3.sunrise_hour = 10;
        weatherInfoV3.sunrise_min = 10;
        weatherInfoV3.sunset_hour = 2;
        weatherInfoV3.sunset_min = 2;
        ArrayList<WeatherInfoV3.Future> list = new ArrayList<>();
         for (int i = 0 ;i<7;i++){
             WeatherInfoV3.Future future = new WeatherInfoV3.Future();
             future.max_temp = 125-i;
             future.min_temp = 110;
             future.weather_type = 3;
             list.add(future);
         }
         weatherInfoV3.future_items = list;
        return weatherInfoV3;
    }


    public void setBloodMeasurePara(View v){
        BloodPressureAdjustPara pressureAdjustPara = new BloodPressureAdjustPara();
        pressureAdjustPara.diastolic = Integer.parseInt(etDiastolic.getText().toString());
        pressureAdjustPara.systolic = Integer.parseInt(etSystolic.getText().toString());
        BLEManager.setBloodPressureAdjustPara(pressureAdjustPara);
    }

    public void setOneKeyReset(View v){
        BLEManager.setOneKeyReset();
    }


    /**
     * getWalkReminderState获取走动提醒状态
     *
     * @return
     */
    public WalkReminder getWalkReminderState() {
        WalkReminder walkReminder = LocalDataManager.getWalkReminder();
        if (walkReminder == null) {
            walkReminder = new WalkReminder();
            walkReminder.setOnOff(WalkReminder.OFF);
            walkReminder.setWeeks(new boolean[]{true, true, true, true, true, true, true});
            walkReminder.setStartHour(9);
            walkReminder.setStartMinute(0);
            walkReminder.setEndHour(21);
            walkReminder.setEndMinute(0);
            walkReminder.setGoalStep(200);//默认的小时目标，用户自己定义
        }
        return walkReminder;
    }

    public ActivitySwitch getMotionRecognitionState() {
        ActivitySwitch activitySwitch = LocalDataManager.getActivitySwitch();
        return activitySwitch == null ? new ActivitySwitch() : activitySwitch;
    }
}
