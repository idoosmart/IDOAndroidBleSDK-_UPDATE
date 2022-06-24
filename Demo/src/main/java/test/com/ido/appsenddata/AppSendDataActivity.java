package test.com.ido.appsenddata;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ido.ble.BLEManager;
import com.ido.ble.callback.AppSendDataCallBack;
import com.ido.ble.callback.SettingCallBack;
import com.ido.ble.protocol.model.Menstrual;
import com.ido.ble.protocol.model.MenstrualRemind;
import com.ido.ble.protocol.model.MenstruationHistoricalData;
import com.ido.ble.protocol.model.MenuList;
import com.ido.ble.protocol.model.MusicControlInfo;
import com.ido.ble.protocol.model.SportModeSortV3;
import com.ido.ble.protocol.model.SportType;
import com.ido.ble.protocol.model.WeatherInfo;
import com.ido.ble.protocol.model.WeatherInfoV3;

import java.util.ArrayList;

import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;
import test.com.ido.utils.JsonUtils;

public class AppSendDataActivity extends BaseAutoConnectActivity {

    private EditText etJsonData, etDataType;
    private TextView tvTip;
    private AppSendDataCallBack.ICallBack iCallBack = new AppSendDataCallBack.ICallBack() {
        @Override
        public void onSuccess(AppSendDataCallBack.DataType type) {
            Toast.makeText(AppSendDataActivity.this, R.string.app_send_data_tip_ok, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onFailed(AppSendDataCallBack.DataType type) {
            Toast.makeText(AppSendDataActivity.this, R.string.app_send_data_tip_failed, Toast.LENGTH_LONG).show();
        }
    };

    private SettingCallBack.ICallBack setCallback = new SettingCallBack.ICallBack() {
        @Override
        public void onSuccess(SettingCallBack.SettingType type, Object data) {
            Toast.makeText(AppSendDataActivity.this, R.string.app_send_data_tip_ok, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onFailed(SettingCallBack.SettingType type) {
            Toast.makeText(AppSendDataActivity.this, R.string.app_send_data_tip_failed, Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_send_data);

        initView();
        BLEManager.registerAppSendDataCallBack(iCallBack);
        BLEManager.registerSettingCallBack(setCallback);

        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                etDataType.setText(TYPE_WEATHER);
            }
        }, 500);

    }

    private void initView() {
        tvTip = findViewById(R.id.data_type_tip_tv);
        etJsonData = (EditText) findViewById(R.id.weather_para_et);
        etDataType = findViewById(R.id.data_type_tv);

        etDataType.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tvTip.setVisibility(View.GONE);
                dataType = s.toString();
                if (dataType.equals(TYPE_WEATHER)){
                    createWeatherJsonData();
                }else if(dataType.equals(TYPE_MENSTRUAL)){
                    createMenstrualJsonData();
                }else if (dataType.equals(TYPE_MENSTRUAL_REMIND)){
                    createMenstrualRemindJsonData();
                }else if (dataType.equals(TYPE_MENU_LIST)){
                    createMenuList();
                    tvTip.setVisibility(View.VISIBLE);
                }else if (dataType.equals(TYPE_MUSIC_CONTROL_INFO)){
                    createMusicControlInfo();
                }else if (dataType.equals(TYPE_SPORT_SORT_V3)){
                    createSportSortV3JsonData();
                }else if (dataType.equals(TYPE_WEATHER_V3)){
                    createWeatherV3JsonData();
                }else if (dataType.equals(TYPE_WOMAN_HEALTH_HISTORY)){
                    createWomanHealthHistoryJsonData();
                }else {
                    etJsonData.setText("");
                }
            }
        });
    }

    private String dataType;
    private static final String TYPE_WEATHER = "1";
    private static final String TYPE_MENSTRUAL = "2";
    private static final String TYPE_MENSTRUAL_REMIND = "3";
    private static final String TYPE_MENU_LIST = "4";
    private static final String TYPE_MUSIC_CONTROL_INFO = "5";
    private static final String TYPE_SPORT_SORT_V3 = "6";
    private static final String TYPE_WEATHER_V3 = "7";
    private static final String TYPE_WOMAN_HEALTH_HISTORY = "8";

    private void createMusicControlInfo() {
        MusicControlInfo musicControlInfo = new MusicControlInfo();
        musicControlInfo.status = 1;
        musicControlInfo.curTimeSecond = 30;
        musicControlInfo.totalTimeSecond = 100;
        musicControlInfo.musicName = "hello android";
        etJsonData.setText(JsonUtils.format(new Gson().toJson(musicControlInfo)));
    }

    private void createMenstrualJsonData(){
        Menstrual menstrual = new Menstrual();
        menstrual.on_off = Menstrual.STATUS_ON;
        etJsonData.setText(JsonUtils.format(new Gson().toJson(menstrual)));
    }

    private void createWomanHealthHistoryJsonData(){
        MenstruationHistoricalData.MenstruationItem item = new MenstruationHistoricalData.MenstruationItem();
        item.year = 2021;
        item.mon = 7;
        item.day = 24;
        item.menstrual_day = 5;
        item.cycle_day = 29;

        MenstruationHistoricalData.MenstruationItem item2 = new MenstruationHistoricalData.MenstruationItem();
        item2.year = 2021;
        item2.mon = 8;
        item2.day = 24;
        item2.menstrual_day = 6;
        item2.cycle_day = 28;

        MenstruationHistoricalData.MenstruationItem item3 = new MenstruationHistoricalData.MenstruationItem();
        item3.year = 2021;
        item3.mon = 9;
        item3.day = 24;
        item3.menstrual_day = 8;
        item3.cycle_day = 27;

        MenstruationHistoricalData.MenstruationItem item4 = new MenstruationHistoricalData.MenstruationItem();
        item4.year = 2021;
        item4.mon = 10;
        item4.day = 24;
        item4.menstrual_day = 7;
        item4.cycle_day = 29;

        MenstruationHistoricalData.MenstruationItem item5 = new MenstruationHistoricalData.MenstruationItem();
        item5.year = 2021;
        item5.mon = 11;
        item5.day = 24;
        item5.menstrual_day = 6;
        item5.cycle_day = 30;

        ArrayList<MenstruationHistoricalData.MenstruationItem> list = new ArrayList();
        list.add(item);
        list.add(item2);
        list.add(item3);
        list.add(item4);
        list.add(item5);
        MenstruationHistoricalData data = new MenstruationHistoricalData();
        data.avg_menstrual_day = 7;
        data.avg_cycle_day = 30;
        data.items_len = 5;
        data.items = list;

        etJsonData.setText(JsonUtils.format(new Gson().toJson(data)));
    }

    private void createMenuList(){
        MenuList menuList = new MenuList();
        menuList.items.add(MenuList.MENU_FIND_PHONE);
        etJsonData.setText(JsonUtils.format(new Gson().toJson(menuList)));
    }

    private void createMenstrualRemindJsonData() {
        MenstrualRemind menstrualRemind = new MenstrualRemind();
        etJsonData.setText(JsonUtils.format(new Gson().toJson(menstrualRemind)));
    }
    private void createWeatherJsonData(){
        WeatherInfo weatherInfo = new WeatherInfo();
        weatherInfo.type = WeatherInfo.WEATHER_TYPE_SNOW;
        weatherInfo.temp = 9;
        weatherInfo.max_temp = 33;
        weatherInfo.min_temp =  2;
        weatherInfo.humidity = 32;
        weatherInfo.uv_intensity = 23;
        weatherInfo.aqi = 3;

        WeatherInfo.WeatherFutureInfo futureInfo = new WeatherInfo.WeatherFutureInfo();
        futureInfo.type = WeatherInfo.WEATHER_TYPE_CLOUDY;
        futureInfo.max_temp = 55;
        futureInfo.min_temp = 44;
        weatherInfo.future[0] = futureInfo;

        WeatherInfo.WeatherFutureInfo futureInfo1 = new WeatherInfo.WeatherFutureInfo();
        futureInfo1.type = WeatherInfo.WEATHER_TYPE_CLOUDY;
        futureInfo1.max_temp = 66;
        futureInfo1.min_temp = 77;
        weatherInfo.future[1] = futureInfo1;

        WeatherInfo.WeatherFutureInfo futureInfo2 = new WeatherInfo.WeatherFutureInfo();
        futureInfo2.type = WeatherInfo.WEATHER_TYPE_CLOUDY;
        futureInfo2.max_temp = 88;
        futureInfo2.min_temp = 99;
        weatherInfo.future[2] = futureInfo2;
        etJsonData.setText(JsonUtils.format(new Gson().toJson(weatherInfo)));
    }

    private void createSportSortV3JsonData(){
        SportModeSortV3 sportModeSortV3 = new SportModeSortV3();
        sportModeSortV3.num = 6;
        sportModeSortV3.item = new ArrayList<>();

        SportModeSortV3.SportModeSortItemV3 itemV3 = new SportModeSortV3.SportModeSortItemV3();
        itemV3.index = 1;
        itemV3.type = 1;
        sportModeSortV3.item.add(itemV3);

        itemV3 = new SportModeSortV3.SportModeSortItemV3();
        itemV3.index = 2;
        itemV3.type = 3;
        sportModeSortV3.item.add(itemV3);

        itemV3 = new SportModeSortV3.SportModeSortItemV3();
        itemV3.index = 3;
        itemV3.type = 3;
        sportModeSortV3.item.add(itemV3);

        itemV3 = new SportModeSortV3.SportModeSortItemV3();
        itemV3.index = 4;
        itemV3.type = 4;
        sportModeSortV3.item.add(itemV3);

        itemV3 = new SportModeSortV3.SportModeSortItemV3();
        itemV3.index = 5;
        itemV3.type = 5;
        sportModeSortV3.item.add(itemV3);

        itemV3 = new SportModeSortV3.SportModeSortItemV3();
        itemV3.index = 6;
        itemV3.type = 6;
        sportModeSortV3.item.add(itemV3);
        etJsonData.setText(JsonUtils.format(new Gson().toJson(sportModeSortV3)));
    }

    private void createWeatherV3JsonData(){
        WeatherInfoV3 weatherInfoV3 = new WeatherInfoV3();
        weatherInfoV3.future_items = new ArrayList<>();
        weatherInfoV3.sunrise_item = new ArrayList<>();
        weatherInfoV3.weather_type = WeatherInfo.WEATHER_TYPE_SNOW;
        weatherInfoV3.today_tmp = 9;
        weatherInfoV3.today_max_temp = 33;
        weatherInfoV3.today_min_temp =  2;
        weatherInfoV3.city_name =  "shenzhen";

        weatherInfoV3.air_quality = 6;
        weatherInfoV3.precipitation_probability = 39;
        weatherInfoV3.humidity = 32;
        weatherInfoV3.today_uv_intensity = 11;
        weatherInfoV3.wind_speed = 5;

        weatherInfoV3.month = 9;
        weatherInfoV3.day = 27;

        weatherInfoV3.hour = 11;
        weatherInfoV3.min = 27;
        weatherInfoV3.sec = 9;
        weatherInfoV3.week = 2;

        weatherInfoV3.sunrise_hour = 5;
        weatherInfoV3.sunrise_min = 37;
        weatherInfoV3.sunset_hour = 18;
        weatherInfoV3.sunset_min = 39;

        WeatherInfoV3.Future futureInfoV3 = new WeatherInfoV3.Future();
        futureInfoV3.weather_type = WeatherInfoV3.WEATHER_TYPE_CLOUDY;
        futureInfoV3.max_temp = 55;
        futureInfoV3.min_temp = 44;
        weatherInfoV3.future_items.add(futureInfoV3);

        WeatherInfoV3.Future futureInfoV3_2 = new WeatherInfoV3.Future();
        futureInfoV3_2.weather_type = WeatherInfoV3.WEATHER_TYPE_CLOUDY;
        futureInfoV3_2.max_temp = 66;
        futureInfoV3_2.min_temp = 77;
        weatherInfoV3.future_items.add(futureInfoV3_2);

        WeatherInfoV3.Future futureInfoV3_3 = new WeatherInfoV3.Future();
        futureInfoV3_3.weather_type = WeatherInfoV3.WEATHER_TYPE_CLOUDY;
        futureInfoV3_3.max_temp = 88;
        futureInfoV3_3.min_temp = 99;
        weatherInfoV3.future_items.add(futureInfoV3_3);

        WeatherInfoV3.SunRiseSet SunRiseSet_1 = new WeatherInfoV3.SunRiseSet();
        SunRiseSet_1.sunrise_hour = 5;
        SunRiseSet_1.sunrise_min = 10;
        SunRiseSet_1.sunset_hour = 18;
        SunRiseSet_1.sunset_min = 20;
        weatherInfoV3.sunrise_item.add(SunRiseSet_1);

        WeatherInfoV3.SunRiseSet SunRiseSet_2 = new WeatherInfoV3.SunRiseSet();
        SunRiseSet_2.sunrise_hour = 6;
        SunRiseSet_2.sunrise_min = 20;
        SunRiseSet_2.sunset_hour = 19;
        SunRiseSet_2.sunset_min = 5;
        weatherInfoV3.sunrise_item.add(SunRiseSet_2);

        WeatherInfoV3.SunRiseSet SunRiseSet_3 = new WeatherInfoV3.SunRiseSet();
        SunRiseSet_3.sunrise_hour = 7;
        SunRiseSet_3.sunrise_min = 15;
        SunRiseSet_3.sunset_hour = 20;
        SunRiseSet_3.sunset_min = 8;
        weatherInfoV3.sunrise_item.add(SunRiseSet_3);

        weatherInfoV3.sunrise_item_num = 3;

        etJsonData.setText(JsonUtils.format(new Gson().toJson(weatherInfoV3)));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BLEManager.unregisterAppSendDataCallBack(iCallBack);
        BLEManager.unregisterSettingCallBack(setCallback);
    }

    public void sendJsonData(View v){
        if (dataType.equals(TYPE_WEATHER)) {
            WeatherInfo weatherInfo = new Gson().fromJson(etJsonData.getText().toString(), WeatherInfo.class);
            BLEManager.setWeatherData(weatherInfo);
        }else if (dataType.equals(TYPE_MENSTRUAL)){
            Menstrual menstrual = new Gson().fromJson(etJsonData.getText().toString(), Menstrual.class);
            BLEManager.setMenstrual(menstrual);
        }else if (dataType.equals(TYPE_MENSTRUAL_REMIND)){
            MenstrualRemind menstrualRemind = new Gson().fromJson(etJsonData.getText().toString(), MenstrualRemind.class);
            BLEManager.setMenstrualRemind(menstrualRemind);
        }else if (dataType.equals(TYPE_MENU_LIST)){
            MenuList menuList = new Gson().fromJson(etJsonData.getText().toString(), MenuList.class);
            BLEManager.setMenuList(menuList);
        }else if (dataType.equals(TYPE_MUSIC_CONTROL_INFO)){
            MusicControlInfo musicControlInfo = new Gson().fromJson(etJsonData.getText().toString(), MusicControlInfo.class);
            BLEManager.setMusicControlInfo(musicControlInfo);
        }else if (dataType.equals(TYPE_SPORT_SORT_V3)){
            SportModeSortV3 sportModeSortV3 = new Gson().fromJson(etJsonData.getText().toString(), SportModeSortV3.class);
            BLEManager.setSportModeSortInfoV3(sportModeSortV3);
        }else if (dataType.equals(TYPE_WEATHER_V3)){
            WeatherInfoV3 weatherInfoV3 = new Gson().fromJson(etJsonData.getText().toString(), WeatherInfoV3.class);
            BLEManager.setWeatherDataV3(weatherInfoV3);
        }else if (dataType.equals(TYPE_WOMAN_HEALTH_HISTORY)){
            MenstruationHistoricalData menstruationHistoricalData = new Gson().fromJson(etJsonData.getText().toString(), MenstruationHistoricalData.class);
            BLEManager.setMenstruationHistoricalData(menstruationHistoricalData);
        }
    }



}
