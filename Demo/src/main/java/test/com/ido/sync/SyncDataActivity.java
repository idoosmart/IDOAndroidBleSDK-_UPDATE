package test.com.ido.sync;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ido.ble.BLEManager;
import com.ido.ble.LocalDataManager;
import com.ido.ble.business.sync.ISyncDataListener;
import com.ido.ble.business.sync.ISyncProgressListener;
import com.ido.ble.business.sync.SyncPara;
import com.ido.ble.callback.SyncCallBack;
import com.ido.ble.callback.SyncV3CallBack;
import com.ido.ble.data.manage.database.HealthActivity;
import com.ido.ble.data.manage.database.HealthActivityV3;
import com.ido.ble.data.manage.database.HealthBloodPressed;
import com.ido.ble.data.manage.database.HealthBloodPressedItem;
import com.ido.ble.data.manage.database.HealthBloodPressureV3;
import com.ido.ble.data.manage.database.HealthBodyPower;
import com.ido.ble.data.manage.database.HealthGpsV3;
import com.ido.ble.data.manage.database.HealthHRVdata;
import com.ido.ble.data.manage.database.HealthHeartRate;
import com.ido.ble.data.manage.database.HealthHeartRateItem;
import com.ido.ble.data.manage.database.HealthHeartRateSecond;
import com.ido.ble.data.manage.database.HealthHeartRateSecondItem;
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
import com.ido.ble.gps.database.HealthGps;
import com.ido.ble.gps.database.HealthGpsItem;
import com.ido.ble.protocol.model.SupportFunctionInfo;

import java.util.List;

import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;

public class SyncDataActivity extends BaseAutoConnectActivity {

    private TextView tvSyncResult;


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

            @Override
            public void onGetHealthRespiratoryRate(HealthRespiratoryRate healthRespiratoryRate) {

            }

            @Override
            public void onGetHealthBodyPower(HealthBodyPower healthBodyPower) {

            }

            @Override
            public void onGetHealthHRV(HealthHRVdata healthHRVdata) {

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
