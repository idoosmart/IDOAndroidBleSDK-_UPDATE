package test.com.ido.sync;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ido.ble.BLEManager;
import com.ido.ble.LocalDataManager;
import com.ido.ble.business.sync.ISyncDataListener;
import com.ido.ble.business.sync.ISyncProgressListener;
import com.ido.ble.business.sync.SyncPara;
import com.ido.ble.data.manage.database.HealthActivity;
import com.ido.ble.data.manage.database.HealthActivityV3;
import com.ido.ble.data.manage.database.HealthBloodPressed;
import com.ido.ble.data.manage.database.HealthBloodPressedItem;
import com.ido.ble.data.manage.database.HealthBloodPressureV3;
import com.ido.ble.data.manage.database.HealthBodyComposition;
import com.ido.ble.data.manage.database.HealthBodyPower;
import com.ido.ble.data.manage.database.HealthGpsV3;
import com.ido.ble.data.manage.database.HealthHRVdata;
import com.ido.ble.data.manage.database.HealthHeartRate;
import com.ido.ble.data.manage.database.HealthHeartRateItem;
import com.ido.ble.data.manage.database.HealthHeartRateSecond;
import com.ido.ble.data.manage.database.HealthNoise;
import com.ido.ble.data.manage.database.HealthPressure;
import com.ido.ble.data.manage.database.HealthPressureItem;
import com.ido.ble.data.manage.database.HealthRespiratoryRate;
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
import com.ido.ble.data.manage.database.HealthV3Ecg;
import com.ido.ble.gps.database.HealthGps;
import com.ido.ble.gps.database.HealthGpsItem;
import com.ido.ble.protocol.model.DrinkPlanData;
import com.ido.ble.protocol.model.SupportFunctionInfo;

import java.util.List;

import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;

public class SyncDataActivity extends BaseAutoConnectActivity {

    private TextView tvSyncResult;
    private String TAG = "SyncDataActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_data);
        tvSyncResult = (TextView) findViewById(R.id.sync_result);

        SupportFunctionInfo functionInfo = LocalDataManager.getSupportFunctionInfo();
        if (functionInfo != null){
            tvSyncResult.setText("functionInfo is ok");
        }else {
            BLEManager.getFunctionTables();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    public void syncAllData(View view){
        SyncPara syncPara = new SyncPara();
        syncPara.iSyncDataListener = new ISyncDataListener(){

            @Override
            public void onGetSportData(HealthSport healthSport, List<HealthSportItem> items, boolean isSectionItemData) {
                Log.d(TAG, "onGetSportData: is ok");
                tvSyncResult.setText("onGetSportData is ok");
            }

            @Override
            public void onGetSleepData(HealthSleep healthSleep, List<HealthSleepItem> items) {
                Log.d(TAG, "onGetSleepData: is ok");
                tvSyncResult.setText("onGetSleepData is ok");
            }

            @Override
            public void onGetHeartRateData(HealthHeartRate healthHeartRate, List<HealthHeartRateItem> items, boolean isSectionItemData) {
                Log.d(TAG, "onGetHeartRateData: is ok");
                tvSyncResult.setText("functionInfo is ok");
            }

            @Override
            public void onGetBloodPressureData(HealthBloodPressed healthBloodPressed, List<HealthBloodPressedItem> items, boolean isSectionItemData) {
                Log.d(TAG, "onGetBloodPressureData: is ok");
                tvSyncResult.setText("onGetHeartRateData is ok");
            }

            @Override
            public void onGetActivityData(HealthActivity healthActivity) {
                Log.d(TAG, "onGetActivityData: is ok");
                tvSyncResult.setText("onGetActivityData is ok");
            }

            @Override
            public void onGetGpsData(HealthGps healthGps, List<HealthGpsItem> healthGpsItems, boolean isSectionItemData) {
                Log.d(TAG, "onGetGpsData: is ok");
                tvSyncResult.setText("onGetGpsData is ok");
            }

            @Override
            public void onGetHealthSpO2Data(HealthSpO2 healthSpO2, List<HealthSpO2Item> itemList, boolean isSectionItemData) {
                Log.d(TAG, "onGetHealthSpO2Data: is ok");
                tvSyncResult.setText("onGetHealthSpO2Data is ok");
            }

            @Override
            public void onGetHealthPressureData(HealthPressure healthPressure, List<HealthPressureItem> itemList, boolean isSectionItemData) {
                Log.d(TAG, "onGetHealthPressureData: is ok");
                tvSyncResult.setText("onGetHealthPressureData is ok");
            }

            @Override
            public void onGetHealthHeartRateSecondData(HealthHeartRateSecond healthHeartRateSecond, boolean isSectionItemData) {
                Log.d(TAG, "onGetHealthHeartRateSecondData: is ok");
                tvSyncResult.setText("onGetHealthHeartRateSecondData is ok");
            }

            @Override
            public void onGetHealthSwimmingData(HealthSwimming healthSwimming) {
                Log.d(TAG, "onGetHealthSwimmingData: is ok");
                tvSyncResult.setText("onGetHealthSwimmingData is ok");
            }

            @Override
            public void onGetHealthActivityV3Data(HealthActivityV3 healthActivityV3) {
                Log.d(TAG, "onGetHealthActivityV3Data: is ok");
                tvSyncResult.setText("onGetHealthActivityV3Data is ok");
            }

            @Override
            public void onGetHealthSportV3Data(HealthSportV3 healthSportV3) {
                Log.d(TAG, "onGetHealthSportV3Data: is ok"+healthSportV3.toString());
                tvSyncResult.setText("onGetHealthSportV3Data is ok");
            }

            @Override
            public void onGetHealthSleepV3Data(HealthSleepV3 healthSleepV3) {
                Log.d(TAG, "onGetHealthSleepV3Data: is ok");
                tvSyncResult.setText("onGetHealthSleepV3Data is ok");
            }

            @Override
            public void onGetHealthGpsV3Data(HealthGpsV3 healthGpsV3) {
                Log.d(TAG, "onGetHealthGpsV3Data: is ok");
                tvSyncResult.setText("onGetHealthGpsV3Data is ok");
            }

            @Override
            public void onGetHealthNoiseData(HealthNoise healthNoise) {
                Log.d(TAG, "onGetHealthNoiseData: is ok");
                tvSyncResult.setText("onGetHealthNoiseData is ok");
            }

            @Override
            public void onGetHealthTemperature(HealthTemperature healthTemperature) {
                Log.d(TAG, "onGetHealthTemperature: is ok");
                tvSyncResult.setText("onGetHealthTemperature is ok");
            }

            @Override
            public void onGetHealthBloodPressure(HealthBloodPressureV3 healthBloodPressureV3) {
                Log.d(TAG, "onGetHealthBloodPressure: is ok");
                tvSyncResult.setText("onGetHealthBloodPressure is ok");
            }

            @Override
            public void onGetHealthRespiratoryRate(HealthRespiratoryRate healthRespiratoryRate) {
                Log.d(TAG, "onGetHealthRespiratoryRate: ");

            }

            @Override
            public void onGetHealthBodyPower(HealthBodyPower healthBodyPower) {

            }

            @Override
            public void onGetHealthHRV(HealthHRVdata healthHRVdata) {

            }

            @Override
            public void onGetDrinkPlan(DrinkPlanData drinkPlanData) {

            }

            @Override
            public void onGetHealthBodyCompositionData(HealthBodyComposition healthBodyComposition) {

            }

            @Override
            public void onGetHealthV3EcgData(HealthV3Ecg healthV3Ecg) {

            }
        };
        syncPara.isNeedSyncConfigData = false;
        syncPara.iSyncProgressListener = new ISyncProgressListener() {
            @Override
            public void onStart() {

            }


            @Override
            public void onProgress(int i) {
                tvSyncResult.setText("progress:"+i);
            }

            @Override
            public void onSuccess() {
                tvSyncResult.setText("Success");
            }

            @Override
            public void onFailed() {

            }
        };
        BLEManager.syncAllData(syncPara);
    }

    public void stopSyncAllData(View view){
        BLEManager.stopSyncAllData();
    }
}
