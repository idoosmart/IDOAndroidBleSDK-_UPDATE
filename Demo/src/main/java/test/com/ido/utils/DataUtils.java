package test.com.ido.utils;

import android.content.Context;
import android.text.TextUtils;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ido.ble.protocol.model.Alarm;
import com.ido.ble.protocol.model.AlarmV3;

import java.util.ArrayList;
import java.util.List;

import test.com.ido.APP;

/**
 * @author: zhouzj
 * @date: 2017/11/6 14:50
 */

public class DataUtils extends CommonPreferences {
    private static final String SP_NAME = "demo_sp";
    private static final String KEY_FIRST = "isFirst";
    private static final String KEY_DFU_FILE_PATH = "dfu_file_path";
    private static final String KEY_ALARM = "alarms";
    private static final String KEY_ALARM_V3 = "alarmsV3";
    private static final String KEY_FONT_UPGRADE_FONT_FILE_PATH = "KEY_FONT_UPGRADE_FONT_FILE_PATH";
    private static final String KEY_FONT_UPGRADE_BIN_FILE_PATH = "KEY_FONT_UPGRADE_BIN_FILE_PATH";
    private static final String KEY_FONT_UPGRADE_OTA_FILE_PATH = "KEY_FONT_UPGRADE_OTA_FILE_PATH";
    private static final String KEY_FONT_UPGRADE_NRF_PRN = "KEY_FONT_UPGRADE_NRF_PRN";
    private static final String KEY_FONT_UPGRADE_BIN_PRN = "KEY_FONT_UPGRADE_BIN_PRN";
    private static final String KEY_SAVE_FILE_PATH = "KEY_SAVE_FILE_PATH";


    public void init(Context context) {
        super.init(context, SP_NAME);
    }

    private static DataUtils instance;

    public static final DataUtils getInstance() {
        if (instance == null) {
            instance = new DataUtils();
            instance.init(APP.getAppContext());
        }
        return instance;
    }

    public boolean isFirst(){
        return getValue(KEY_FIRST, true);
    }

    public void setIsFirst(boolean isFirst){
        setValue(KEY_FIRST, isFirst);
    }

    public void saveDfuFilePath(String path){
        setValue(KEY_DFU_FILE_PATH, path);
    }

    public String getDfuFilePath(){
        return getValue(KEY_DFU_FILE_PATH, "");
    }

    public void saveAlarm(List<Alarm> alarmList){
        String jsonData = new Gson().toJson(alarmList);
        setValue(KEY_ALARM, jsonData);
    }
    public List<Alarm> getAlarm(){
        String jsonData = getValue(KEY_ALARM, "");
        if (!TextUtils.isEmpty(jsonData)){
            return new Gson().fromJson(jsonData, new TypeToken<List<Alarm>>() {}.getType());
        }
        return new ArrayList<>();
    }

    public void saveAlarmV3(List<AlarmV3> alarmList){
        String jsonData = new Gson().toJson(alarmList);
        setValue(KEY_ALARM_V3, jsonData);
    }
    public List<AlarmV3> getAlarmV3(){
        String jsonData = getValue(KEY_ALARM_V3, "");
        if (!TextUtils.isEmpty(jsonData)){
            return new Gson().fromJson(jsonData, new TypeToken<List<Alarm>>() {}.getType());
        }
        return new ArrayList<>();
    }

    public void saveFontUpgradeFontFilePath(String path){
        setValue(KEY_FONT_UPGRADE_FONT_FILE_PATH, path);
    }
    public String getFontUpgradeFontFilePath(){
        return getValue(KEY_FONT_UPGRADE_FONT_FILE_PATH, "");
    }

    public void saveFontUpgradeBinFilePath(String path){
        setValue(KEY_FONT_UPGRADE_BIN_FILE_PATH, path);
    }
    public String getFontUpgradeBinFilePath(){
        return getValue(KEY_FONT_UPGRADE_BIN_FILE_PATH,"");
    }

    public void saveFontUpgradeOtaFilePath(String path){
        setValue(KEY_FONT_UPGRADE_OTA_FILE_PATH, path);
    }
    public String getFontUpgradeOtaFilePath(){
        return getValue(KEY_FONT_UPGRADE_OTA_FILE_PATH, "");
    }

    public void saveFontUpgradeNRFPRN(int prn){
        setValue(KEY_FONT_UPGRADE_NRF_PRN, prn);
    }
    public int getFontUpgradeNRFPRN(){
        return getValue(KEY_FONT_UPGRADE_NRF_PRN, 0);
    }
    public void saveFontUpgradeBinPRN(int prn){
        setValue(KEY_FONT_UPGRADE_BIN_PRN, prn);
    }
    public int getFontUpgradeBinPRN(){
        return getValue(KEY_FONT_UPGRADE_BIN_PRN, 0);
    }

    public String getFilePath(){
        return getValue(KEY_SAVE_FILE_PATH,"");
    }

    public void saveFilePath(String path){
        setValue(KEY_SAVE_FILE_PATH, path);
    }
}
