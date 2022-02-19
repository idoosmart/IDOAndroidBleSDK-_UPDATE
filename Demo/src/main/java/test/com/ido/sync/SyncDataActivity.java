package test.com.ido.sync;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ido.ble.BLEManager;
import com.ido.ble.LocalDataManager;
import com.ido.ble.business.sync.ISyncDataListener;
import com.ido.ble.business.sync.SyncPara;
import com.ido.ble.callback.SyncCallBack;
import com.ido.ble.callback.SyncV3CallBack;
import com.ido.ble.data.manage.database.HealthActivity;
import com.ido.ble.data.manage.database.HealthActivityV3;
import com.ido.ble.data.manage.database.HealthBloodPressed;
import com.ido.ble.data.manage.database.HealthBloodPressedItem;
import com.ido.ble.data.manage.database.HealthBloodPressureV3;
import com.ido.ble.data.manage.database.HealthGpsV3;
import com.ido.ble.data.manage.database.HealthHeartRate;
import com.ido.ble.data.manage.database.HealthHeartRateItem;
import com.ido.ble.data.manage.database.HealthHeartRateSecond;
import com.ido.ble.data.manage.database.HealthHeartRateSecondItem;
import com.ido.ble.data.manage.database.HealthNoise;
import com.ido.ble.data.manage.database.HealthPressure;
import com.ido.ble.data.manage.database.HealthPressureItem;
import com.ido.ble.data.manage.database.HealthSleep;
import com.ido.ble.data.manage.database.HealthSleepItem;
import com.ido.ble.data.manage.database.HealthSleepV3;
import com.ido.ble.data.manage.database.HealthSpO2;
import com.ido.ble.data.manage.database.HealthSpO2Item;
import com.ido.ble.data.manage.database.HealthSport;
import com.ido.ble.data.manage.database.HealthSportItem;
import com.ido.ble.data.manage.database.HealthSportV3;
import com.ido.ble.data.manage.database.HealthSwimming;
import com.ido.ble.data.manage.database.HealthTemperature;
import com.ido.ble.gps.database.HealthGps;
import com.ido.ble.gps.database.HealthGpsItem;
import com.ido.ble.protocol.model.SupportFunctionInfo;

import java.util.List;

import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;

public class SyncDataActivity extends BaseAutoConnectActivity {

    private TextView tvSyncResult;
    private SyncCallBack.IActivityCallBack iActivityCallBack = new SyncCallBack.IActivityCallBack() {
        @Override
        public void onStart() {
            tvSyncResult.setText("start sync activity data");
        }

        @Override
        public void onStop() {
            tvSyncResult.setText("sync activity data stop!");
        }

        @Override
        public void onSuccess() {
            tvSyncResult.setText("sync activity data success!");
        }

        @Override
        public void onFailed() {
            tvSyncResult.setText("sync activity failed");
        }

        @Override
        public void onGetActivityData(HealthActivity healthActivity) {

        }
    };

    private SyncCallBack.IConfigCallBack iConfigCallBack = new SyncCallBack.IConfigCallBack() {
        @Override
        public void onStart() {
            tvSyncResult.setText("start sync config");
        }

        @Override
        public void onStop() {
            tvSyncResult.setText("sync config stop!");
        }

        @Override
        public void onSuccess() {
            tvSyncResult.setText("sync config data success!");
        }

        @Override
        public void onFailed() {
            tvSyncResult.setText("sync config failed");
        }
    };

    private SyncCallBack.IHealthCallBack iHealthCallBack = new SyncCallBack.IHealthCallBack() {
        @Override
        public void onStart() {
            tvSyncResult.setText("start sync health data");
        }

        @Override
        public void onProgress(int progress) {
            tvSyncResult.setText("sync health data progress = " + progress + "%");
        }

        @Override
        public void onStop() {
            tvSyncResult.setText("sync health data stop!");
        }

        @Override
        public void onSuccess() {
            tvSyncResult.setText("sync health data success!");
        }

        @Override
        public void onFailed() {
            tvSyncResult.setText("sync health data failed");
        }

        @Override
        public void onGetSportData(HealthSport healthSport, List<HealthSportItem> items, boolean isSectionItemData) {

        }

        @Override
        public void onGetSleepData(HealthSleep healthSleep, List<HealthSleepItem> items) {

        }

        @Override
        public void onGetHeartRateData(HealthHeartRate healthHeartRate, List<HealthHeartRateItem> items, boolean isSectionItemData) {

        }

        @Override
        public void onGetBloodPressureData(HealthBloodPressed healthBloodPressed, List<HealthBloodPressedItem> items, boolean isSectionItemData) {

        }
    };

    private SyncV3CallBack.ICallBack iSyncV3CallBack = new SyncV3CallBack.ICallBack() {
        @Override
        public void onStart() {
            tvSyncResult.setText("start sync v3 data");
        }

        @Override
        public void onProgress(int progress) {
            tvSyncResult.setText("sync v3 data progress = " + progress + "%");
        }

        @Override
        public void onStop() {
            tvSyncResult.setText("sync v3 data stop!");
        }

        @Override
        public void onSuccess() {
            tvSyncResult.setText("sync v3 data success!");
        }

        @Override
        public void onFailed() {
            tvSyncResult.setText("sync v3 data failed");
        }

        @Override
        public void onGetHealthSpO2Data(HealthSpO2 healthSpO2, List<HealthSpO2Item> itemList, boolean isSectionItemData) {

        }

        @Override
        public void onGetHealthPressureData(HealthPressure healthPressure, List<HealthPressureItem> itemList, boolean isSectionItemData) {

        }

        @Override
        public void onGetHealthHeartRateSecondData(HealthHeartRateSecond healthHeartRateSecond, boolean isSectionItemData) {

        }

        @Override
        public void onGetHealthSwimmingData(HealthSwimming healthSwimming) {

        }

        @Override
        public void onGetHealthActivityV3Data(HealthActivityV3 healthActivity) {

        }

        @Override
        public void onGetHealthSportV3Data(HealthSportV3 healthSportV3) {

        }

        @Override
        public void onGetHealthSleepV3Data(HealthSleepV3 healthSleepV3) {

        }

        @Override
        public void onGetHealthGpsV3Data(HealthGpsV3 healthGpsV3) {

        }

        @Override
        public void onGetHealthNoiseData(HealthNoise healthNoise) {

        }

        @Override
        public void onGetHealthTemperature(HealthTemperature healthTemperature) {

        }

        @Override
        public void onGetHealthBloodPressure(HealthBloodPressureV3 healthBloodPressureV3) {

        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_data);
        tvSyncResult = (TextView) findViewById(R.id.sync_result);
        BLEManager.registerSyncConfigCallBack(iConfigCallBack);
        BLEManager.registerSyncHealthCallBack(iHealthCallBack);
        BLEManager.registerSyncActivityCallBack(iActivityCallBack);
        BLEManager.registerSyncV3CallBack(iSyncV3CallBack);

        SupportFunctionInfo functionInfo = LocalDataManager.getSupportFunctionInfo();
        if (functionInfo != null){
            tvSyncResult.setText("functionInfo is ok");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BLEManager.unregisterSyncConfigCallBack(iConfigCallBack);
        BLEManager.unregisterSyncHealthCallBack(iHealthCallBack);
        BLEManager.unregisterSyncActivityCallBack(iActivityCallBack);
        BLEManager.unregisterSyncV3CallBack(iSyncV3CallBack);
    }

    public void syncConfigData(View view){
        BLEManager.startSyncConfigInfo();
    }
    public void stopSyncConfigData(View view){
        BLEManager.stopSyncConfigInfo();
    }



    public void syncHealthData(View view){
        BLEManager.startSyncHealthData();
    }
    public void stopSyncHealthData(View view){
        BLEManager.stopSyncHealthData();
    }


    public void syncActivityData(View view){
        BLEManager.startSyncActivityData();
    }
    public void stopSyncActivityData(View view){
        BLEManager.stopSyncActivityData();
    }

    public void syncV3Data(View view){
        BLEManager.startSyncV3Health();
    }

    public void stopSyncV3Data(View view){
        BLEManager.stopSyncV3Health();
    }

    public void syncAllData(View view){
        SyncPara syncPara = new SyncPara();
        syncPara.iSyncDataListener = new ISyncDataListener(){

            @Override
            public void onGetSportData(HealthSport healthSport, List<HealthSportItem> items, boolean isSectionItemData) {
                tvSyncResult.setText("onGetSportData is ok");
            }

            @Override
            public void onGetSleepData(HealthSleep healthSleep, List<HealthSleepItem> items) {
                tvSyncResult.setText("onGetSleepData is ok");
            }

            @Override
            public void onGetHeartRateData(HealthHeartRate healthHeartRate, List<HealthHeartRateItem> items, boolean isSectionItemData) {
                tvSyncResult.setText("functionInfo is ok");
            }

            @Override
            public void onGetBloodPressureData(HealthBloodPressed healthBloodPressed, List<HealthBloodPressedItem> items, boolean isSectionItemData) {
                tvSyncResult.setText("onGetHeartRateData is ok");
            }

            @Override
            public void onGetActivityData(HealthActivity healthActivity) {
                tvSyncResult.setText("onGetActivityData is ok");
            }

            @Override
            public void onGetGpsData(HealthGps healthGps, List<HealthGpsItem> healthGpsItems, boolean isSectionItemData) {
                tvSyncResult.setText("onGetGpsData is ok");
            }

            @Override
            public void onGetHealthSpO2Data(HealthSpO2 healthSpO2, List<HealthSpO2Item> itemList, boolean isSectionItemData) {
                tvSyncResult.setText("onGetHealthSpO2Data is ok");
            }

            @Override
            public void onGetHealthPressureData(HealthPressure healthPressure, List<HealthPressureItem> itemList, boolean isSectionItemData) {
                tvSyncResult.setText("onGetHealthPressureData is ok");
            }

            @Override
            public void onGetHealthHeartRateSecondData(HealthHeartRateSecond healthHeartRateSecond, boolean isSectionItemData) {
                tvSyncResult.setText("onGetHealthHeartRateSecondData is ok");
            }

            @Override
            public void onGetHealthSwimmingData(HealthSwimming healthSwimming) {
                tvSyncResult.setText("onGetHealthSwimmingData is ok");
            }

            @Override
            public void onGetHealthActivityV3Data(HealthActivityV3 healthActivityV3) {
                tvSyncResult.setText("onGetHealthActivityV3Data is ok");
            }

            @Override
            public void onGetHealthSportV3Data(HealthSportV3 healthSportV3) {
                tvSyncResult.setText("onGetHealthSportV3Data is ok");
            }

            @Override
            public void onGetHealthSleepV3Data(HealthSleepV3 healthSleepV3) {
                tvSyncResult.setText("onGetHealthSleepV3Data is ok");
            }

            @Override
            public void onGetHealthGpsV3Data(HealthGpsV3 healthGpsV3) {
                tvSyncResult.setText("onGetHealthGpsV3Data is ok");
            }

            @Override
            public void onGetHealthNoiseData(HealthNoise healthNoise) {
                tvSyncResult.setText("onGetHealthNoiseData is ok");
            }

            @Override
            public void onGetHealthTemperature(HealthTemperature healthTemperature) {
                tvSyncResult.setText("onGetHealthTemperature is ok");
            }

            @Override
            public void onGetHealthBloodPressure(HealthBloodPressureV3 healthBloodPressureV3) {
                tvSyncResult.setText("onGetHealthBloodPressure is ok");
            }
        };
        syncPara.isNeedSyncConfigData = false;
        BLEManager.syncAllData(syncPara);
    }

    public void stopSyncAllData(View view){
        BLEManager.stopSyncAllData();
    }
}
