package test.com.ido.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ido.ble.protocol.model.Alarm;
import com.ido.ble.protocol.model.AlarmV3;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import test.com.ido.APP;
import test.com.ido.model.ContactBean;

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
    private static final String KEY_SAVE_DIAL_FILE_PATH = "KEY_SAVE_DIAL_FILE_PATH";
    private static final String KEY_SAVE_GPS_FILE_PATH = "KEY_SAVE_GPS_FILE_PATH";
    private static final String KEY_SAVE_DIAL_NAME = "KEY_SAVE_DIAL_NAME";
    private static final String KEY_SAVE_MUSIC_NAME = "KEY_SAVE_MUSIC_NAME";
    private static final String KEY_SAVE_MUSIC_PATH = "KEY_SAVE_MUSIC_PATH";
    private static final String KEY_MOTION_TYPE_VERSION = "KEY_MOTION_TYPE_VERSION";
    private static final String KEY_SAVE_MUSIC_NAME_SWITCH = "KEY_SAVE_MUSIC_NAME_SWITCH";

    private static final String KEY_WORLD_TIME_LIST = "KEY_WORLD_TIME_LIST";

    private static final String KEY_CONTACTS_IN_DEVICE = "KEY_CONTACTS_IN_DEVICE";

    private static final String KEY_EPO_LAST_UPGRADE_TIME = "KEY_EPO_LAST_UPGRADE_TIME";
    private static final String KEY_EPO_MODE = "KEY_EPO_MODE";

    private static final String KEY_APP_UNIQUE_FLAG = "KEY_APP_UNIQUE_FLAG";



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

    public String getDialPackagePath(){
        return getValue(KEY_SAVE_DIAL_FILE_PATH,"");
    }

    public void saveDialPackagePath(String path){
        setValue(KEY_SAVE_DIAL_FILE_PATH, path);
    }

    public String getGPSPath() {
        return getValue(KEY_SAVE_GPS_FILE_PATH, "");
    }

    public void saveGPSPath(String path) {
        setValue(KEY_SAVE_GPS_FILE_PATH, path);
    }

    public String getDialName() {
        return getValue(KEY_SAVE_DIAL_NAME, "");
    }

    public void saveDialName(String path){
        setValue(KEY_SAVE_DIAL_NAME, path);
    }

    public String getMusicPath(){
        return getValue(KEY_SAVE_MUSIC_PATH,"");
    }

    public void saveMusicPath(String path) {
        setValue(KEY_SAVE_MUSIC_PATH, path);
    }

    public String getMusicName() {
        return getValue(KEY_SAVE_MUSIC_NAME, "");
    }

    public void saveMusicName(String path) {
        setValue(KEY_SAVE_MUSIC_NAME, path);
    }

    public int getMotionTypeVersion() {
        return getValue(KEY_MOTION_TYPE_VERSION, 0);
    }

    public void saveMotionTypeVersion(int version) {
        setValue(KEY_MOTION_TYPE_VERSION, version);
    }

    public long getEPOLastUpgradeTime() {
        return getValue(KEY_EPO_LAST_UPGRADE_TIME, 0L);
    }

    public void saveEPOUpgradeTime() {
        setValue(KEY_EPO_LAST_UPGRADE_TIME, System.currentTimeMillis());
    }

    public boolean isAutoUpgradeEPO() {
        return getValue(KEY_EPO_MODE, true);
    }

    public void saveEPOUpgradeMode(boolean auto) {
        setValue(KEY_EPO_MODE, auto);
    }

    public boolean getMusicNameSwitch() {
        return getValue(KEY_SAVE_MUSIC_NAME_SWITCH, false);
    }

    public void saveMusicNameSwitch(boolean status) {
        setValue(KEY_SAVE_MUSIC_NAME_SWITCH, status);
    }

    public List<Integer> getWorldTimeList() {
        String json = getValue(KEY_WORLD_TIME_LIST, "[]");
        if (TextUtils.isEmpty(json)) {
            return new ArrayList<>();
        }
        return GsonUtil.analysisJsonArrayToList(json, Integer[].class);
    }

    public void saveWorldTimeList(List<Integer> ids) {
        setValue(KEY_WORLD_TIME_LIST, GsonUtil.toJson(ids));
    }


    public void saveContactsForDevice(List<ContactBean> list) {
//        putJsonWithMac(KEY_CONTACTS_IN_DEVICE,list);
        setValue(KEY_CONTACTS_IN_DEVICE, GsonUtil.toJson(list));
    }

    public List<ContactBean> getContactsForDevice() {
        String json = getValue(KEY_CONTACTS_IN_DEVICE, "[]");
        List<ContactBean> list = GsonUtil.analysisJsonObjectToList(json, ContactBean.class);
        return list == null ? new ArrayList<>() : new ArrayList<>(list);
    }

    public void removeContactsForDevice() {
        remove(KEY_CONTACTS_IN_DEVICE);
    }

    public int createAppUniqueFlag(String packageName) {
        try {
            Log.d("NotificationIconTransfer", "createAppUniqueFlag: " + packageName);
            Map<String, Integer> flags = getAppFlag();
            Log.d("NotificationIconTransfer", "createAppUniqueFlag: " + flags);
            Integer value = 0;
            if (flags == null || flags.isEmpty()) {
                if (flags == null) {
                    flags = new HashMap<>();
                }
                value = 1;
            }

            if (value == 0) {
                value = flags.get(packageName);
            }

            if (value == null || value == 0) {
                int maxValue = 0;
                for (Map.Entry<String, Integer> entry : flags.entrySet()) {
                    if (entry.getValue() > maxValue) {
                        maxValue = entry.getValue();
                    }
                }
                value = maxValue + 1;
            }
            flags.put(packageName,value);
            saveAppFlag(flags);
            return value;
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    public void saveAppFlag(Map<String, Integer> flags) {
        setValue(KEY_APP_UNIQUE_FLAG, GsonUtil.toJson(flags));
    }

    public Map<String, Integer> getAppFlag() {
        String json = getValue(KEY_APP_UNIQUE_FLAG, "");
        if (TextUtils.isEmpty(json)) {
            return new HashMap<>();
        }
        try {
            Type type = new TypeToken<Map<String, Integer>>() {
            }.getType();
            Gson gson = new Gson();
            return gson.fromJson(json, type);
        } catch (Exception ignored) {

        }
        return new HashMap<>();
    }
}
