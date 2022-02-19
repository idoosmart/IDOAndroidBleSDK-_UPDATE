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
import com.ido.ble.callback.SettingCallBack;
import com.ido.ble.protocol.model.AntiLostMode;
import com.ido.ble.protocol.model.BloodPressureAdjustDeviceReplyInfo;
import com.ido.ble.protocol.model.BloodPressureAdjustPara;
import com.ido.ble.protocol.model.BloodPressureQueryAdjustResultPara;
import com.ido.ble.protocol.model.DialPlate;
import com.ido.ble.protocol.model.DisplayMode;
import com.ido.ble.protocol.model.Goal;
import com.ido.ble.protocol.model.HandWearMode;
import com.ido.ble.protocol.model.ShortCut;
import com.ido.ble.protocol.model.SportModeSort;
import com.ido.ble.protocol.model.SportType;
import com.ido.ble.protocol.model.WeatherInfoV3;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;
import test.com.ido.utils.DateUtil;

public class MainSetActivity extends BaseAutoConnectActivity {

    private EditText etGoal, etDialPlate, etSystolic , etDiastolic, etScreenBrigthness, etWeatherCityName,
       etWeatherProbability,etWeatherUVintensity,etWeatherWindSpeed;
    private TextView tvQueryBloodAdjustResult;
    private RadioGroup rgbShortCut, rgbAntiLostMode, rgbHandWearMode, rgbDisplayMode;
    private int mShortCutMode = ShortCut.MODE_CAMERA;
    private int mAntiLostMode = AntiLostMode.MODE_NOT_ANTI_LOST;
    private int mHandWearMode = HandWearMode.HAND_MODE_LEFT;
    private int mDisplayMode = DisplayMode.MODE_DEFAULT;
    private Switch switchFindPhone, switchMusic, switchSOS, switchWeather;
    private boolean mIsSetFindPhoneOpen = false;

    private QueryStatusCallBack.ICallBack queryCallBack = new QueryStatusCallBack.ICallBack() {
        @Override
        public void onQueryBloodAdjustResult(BloodPressureAdjustDeviceReplyInfo.BloodAdjustResult result) {
            tvQueryBloodAdjustResult.setText("" + result.toString());
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

        initView();
        initData();

    }

    private void initData() {
        Goal goal = LocalDataManager.getGoal();
        if (goal != null){
            etGoal.setText(goal.sport_step + "");
        }

        DialPlate dialPlate = LocalDataManager.getDialPlate();
        if (dialPlate != null){
            etDialPlate.setText(dialPlate.dial_id+ "");
        }

        BloodPressureAdjustPara pressureAdjustPara = LocalDataManager.getBloodPressureAdjustPara();
        if (pressureAdjustPara != null){
            etDiastolic.setText(pressureAdjustPara.diastolic+ "");
            etSystolic.setText(pressureAdjustPara.systolic+ "");
        }

        AntiLostMode antiLostMode = LocalDataManager.getAntiLostMode();
        if (antiLostMode != null){
            if (AntiLostMode.MODE_NOT_ANTI_LOST == antiLostMode.mode){
                rgbAntiLostMode.check(R.id.set_para_anti_lost_not);
            }else if (AntiLostMode.MODE_SHORT_DISTANCE == antiLostMode.mode){
                rgbAntiLostMode.check(R.id.set_para_anti_lost_short);
            }else if (AntiLostMode.MODE_MID_DISTANCE == antiLostMode.mode){
                rgbAntiLostMode.check(R.id.set_para_anti_lost_mid);
            }else if (AntiLostMode.MODE_LONG_DISTANCE == antiLostMode.mode){
                rgbAntiLostMode.check(R.id.set_para_anti_lost_long);
            }
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

        DisplayMode displayMode = LocalDataManager.getDisplayMode();
        if (displayMode != null){
            if (DisplayMode.MODE_DEFAULT == displayMode.mode){
                rgbDisplayMode.check(R.id.set_para_display_mode_default);
            }else if (DisplayMode.MODE_HORIZONTAL == displayMode.mode){
                rgbDisplayMode.check(R.id.set_para_display_mode_horizontal);
            }else if (DisplayMode.MODE_VERTICAL == displayMode.mode){
                rgbDisplayMode.check(R.id.set_para_display_mode_vertical);
            }else if (DisplayMode.MODE_OVER_180 == displayMode.mode){
                rgbDisplayMode.check(R.id.set_para_display_mode_over_180);
            }
        }

        switchSOS.setChecked(LocalDataManager.getOneKeySOSSwitch());
        switchWeather.setChecked(LocalDataManager.getWeatherSwitch());

    }

    private void initView(){
        etGoal = (EditText) findViewById(R.id.set_para_goal_et);
        etDialPlate = (EditText) findViewById(R.id.set_para_dial_plate_et);
        etSystolic = (EditText) findViewById(R.id.set_para_blood_sys);
        etDiastolic = (EditText) findViewById(R.id.set_para_blood_dia);

        rgbShortCut = (RadioGroup) findViewById(R.id.set_para_short_cut_group);
        rgbShortCut.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.set_para_short_cut_camera:
                        mShortCutMode = ShortCut.MODE_CAMERA;
                        break;
                    case R.id.set_para_short_cut_sport_mode:
                        mShortCutMode = ShortCut.MODE_SPORT;
                        break;
                    case R.id.set_para_short_cut_not_disturb:
                        mShortCutMode = ShortCut.MODE_NOT_DISTURB;
                        break;
                }
            }
        });

        rgbAntiLostMode = (RadioGroup) findViewById(R.id.set_para_anti_lost_group);
        rgbAntiLostMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.set_para_anti_lost_not:
                        mAntiLostMode = AntiLostMode.MODE_NOT_ANTI_LOST;
                        break;
                    case R.id.set_para_anti_lost_short:
                        mAntiLostMode = AntiLostMode.MODE_SHORT_DISTANCE;
                        break;
                    case R.id.set_para_anti_lost_mid:
                        mAntiLostMode = AntiLostMode.MODE_MID_DISTANCE;
                        break;
                    case R.id.set_para_anti_lost_long:
                        mAntiLostMode = AntiLostMode.MODE_LONG_DISTANCE;
                        break;
                }
            }
        });

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

        rgbDisplayMode = (RadioGroup) findViewById(R.id.set_para_display_mode_group);
        rgbDisplayMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.set_para_display_mode_default:
                        mDisplayMode = DisplayMode.MODE_DEFAULT;
                        break;
                    case R.id.set_para_display_mode_horizontal:
                        mDisplayMode = DisplayMode.MODE_HORIZONTAL;
                        break;
                    case R.id.set_para_display_mode_vertical:
                        mDisplayMode = DisplayMode.MODE_VERTICAL;
                        break;
                    case R.id.set_para_display_mode_over_180:
                        mDisplayMode = DisplayMode.MODE_OVER_180;
                        break;
                }
            }
        });

        etScreenBrigthness = findViewById(R.id.set_screen_brightness);
        etWeatherCityName = findViewById(R.id.set_weather_city_name);
        etWeatherProbability = findViewById(R.id.set_precipitation_probability);
        etWeatherUVintensity = findViewById(R.id.set_today_uv_intensity);
        etWeatherWindSpeed = findViewById(R.id.set_wind_speed);
        switchSOS = (Switch) findViewById(R.id.set_para_sos_switch);
        switchWeather = (Switch) findViewById(R.id.set_para_weather_switch);
        tvQueryBloodAdjustResult = findViewById(R.id.set_para_query_blood_adjust_result_tv);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BLEManager.registerSettingCallBack(iCallBack);
        BLEManager.registerOperateCallBack(operateICallBack);
        BLEManager.registerQueryStatusCallBack(queryCallBack);
        BLEManager.registerOtherProtocolCallBack(otherICallBack);
    }

    @Override
    protected void onStop() {
        super.onStop();
        BLEManager.unregisterSettingCallBack(iCallBack);
        BLEManager.unregisterOperateCallBack(operateICallBack);
        BLEManager.unregisterQueryStatusCallBack(queryCallBack);
        BLEManager.unregisterOtherProtocolCallBack(otherICallBack);
    }

    public void setTime(View v){
        startActivity(new Intent(this, SetTimeActivity.class));

    }

    public void setAlarm(View v){
        startActivity(new Intent(this, ShowAlarmsActivity.class));
    }

    public void setAlarmV3(View v){
        startActivity(new Intent(this, ShowAlarmsV3Activity.class));
    }

    public void setGoal(View v){
        Goal goal = new Goal();
        goal.sport_step = Integer.parseInt(etGoal.getText().toString());
        BLEManager.setGoal(goal);
    }

    public void setUserInfo(View v){
        startActivity(new Intent(this, SetUserInfoActivity.class));
    }

    public void setUnit(View v){
        startActivity(new Intent(this, SetUnitActivity.class));
    }

    public void setDialPlate(View v){
        DialPlate dialPlate = new DialPlate();
        dialPlate.dial_id = Integer.parseInt(etDialPlate.getText().toString());
        BLEManager.setDialPlate(dialPlate);
    }

    public void setShortCut(View v){
        ShortCut shortCut = new ShortCut();
        shortCut.func1 = mShortCutMode;
        BLEManager.setShortCut(shortCut);
    }

    public void setBloodMeasurePara(View v){
        BloodPressureAdjustPara pressureAdjustPara = new BloodPressureAdjustPara();
        pressureAdjustPara.diastolic = Integer.parseInt(etDiastolic.getText().toString());
        pressureAdjustPara.systolic = Integer.parseInt(etSystolic.getText().toString());
        BLEManager.setBloodPressureAdjustPara(pressureAdjustPara);
    }

    public void queryBloodAdjustResult(View view){
        BLEManager.queryBloodPressureAdjustResult(new BloodPressureQueryAdjustResultPara());
    }

    public void setLongSit(View v){
        startActivity(new Intent(this, SetLongSitActivity.class));
    }

    public void setAntiLostMode(View v){
        AntiLostMode mode = new AntiLostMode();
        mode.mode = mAntiLostMode;
        BLEManager.setAntiLostMode(mode);
    }

    public void setAntiLostPara(View v){
//        AntiLostPara antiLostPara = new AntiLostPara();
//        BLEManager.setAntiLostPara(antiLostPara);
    }

    public void setHandWearMode(View v){
        HandWearMode handWearMode = new HandWearMode();
        handWearMode.hand = mHandWearMode;
        BLEManager.setHandWearMode(handWearMode);

    }

    public void setPhoneSystemInfo(View v){

    }

    public void setHeartRateInterval(View v){
        startActivity(new Intent(this, SetHeartRateIntervalActivity.class));
    }

    public void setHeartRateMode(View v){
        startActivity(new Intent(this, SetHeartRateModeActivity.class));
    }

    public void setFindPhoneSwitch(View v){
        BLEManager.setFindPhoneSwitch(mIsSetFindPhoneOpen);
    }

    public void setOneKeyReset(View v){
        BLEManager.setOneKeyReset();
    }

    public void setUpHandGesture(View v){
        startActivity(new Intent(this, SetUpHandGestureActivity.class));
    }

    public void setNotDisturb(View v){
        startActivity(new Intent(this, SetNotDisturbActivity.class));
    }

    public void setMusicSwitch(View v){
        BLEManager.setMusicSwitch(switchMusic.isChecked());
    }

    public void setDisplayMode(View v){
        DisplayMode displayMode = new DisplayMode();
        displayMode.mode = mDisplayMode;
        BLEManager.setDisplayMode(displayMode);
    }

    public void setOneKeySOS(View v){
        BLEManager.setOneKeySOSSwitch(switchSOS.isChecked());
    }

    public void setWeatherSwitch(View v){
        BLEManager.setWeatherSwitch(switchWeather.isChecked());
    }

    public void setScreenBrightness(View view){
        BLEManager.setScreenBrightness(Integer.parseInt(etScreenBrigthness.getText().toString()));
    }
    public void setQuickSportMode(View v){
        startActivity(new Intent(this, SetQuickSportModeActivity.class));
    }

    public void setNoticeSwitch(View v){

    }

    public void setSleepMonitoringPara(View v){
        startActivity(new Intent(this, SetSleepMonitoringActivity.class));
    }

    public void setWeatherCityName(View view){
        BLEManager.setWeatherCityName(etWeatherCityName.getText().toString());
    }
    //设置降水率
    public void setWeatherPrecipitation(View view){
        WeatherInfoV3 weatherInfoV3  = getWeatherInfoV3();
        weatherInfoV3.precipitation_probability = Integer.parseInt(etWeatherProbability.getText().toString());
        weatherInfoV3.today_uv_intensity = Integer.parseInt(etWeatherUVintensity.getText().toString());
        weatherInfoV3.wind_speed = Integer.parseInt(etWeatherWindSpeed.getText().toString());
        BLEManager.setWeatherDataV3(weatherInfoV3);
    }
    //设置紫外线
    public void setWeatherUVintensity(View view){
        WeatherInfoV3 weatherInfoV3  = getWeatherInfoV3();
        weatherInfoV3.precipitation_probability = Integer.parseInt(etWeatherProbability.getText().toString());
        weatherInfoV3.today_uv_intensity = Integer.parseInt(etWeatherUVintensity.getText().toString());
        weatherInfoV3.wind_speed = Integer.parseInt(etWeatherWindSpeed.getText().toString());
        BLEManager.setWeatherDataV3(weatherInfoV3);
    }
    //设置风速
    public void setWeatherWindSpeed(View view){
        WeatherInfoV3 weatherInfoV3  = getWeatherInfoV3();
        weatherInfoV3.precipitation_probability = Integer.parseInt(etWeatherProbability.getText().toString());
        weatherInfoV3.today_uv_intensity = Integer.parseInt(etWeatherUVintensity.getText().toString());
        weatherInfoV3.wind_speed = Integer.parseInt(etWeatherWindSpeed.getText().toString());
        BLEManager.setWeatherDataV3(weatherInfoV3);
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
        BLEManager.querySportSubItemParaSort(48);
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
}
