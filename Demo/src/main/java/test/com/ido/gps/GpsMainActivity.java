package test.com.ido.gps;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ido.ble.BLEManager;
import com.ido.ble.gps.callback.GpsCallBack;
import com.ido.ble.gps.database.HealthGps;
import com.ido.ble.gps.database.HealthGpsItem;

import java.util.List;

import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;

public class GpsMainActivity extends BaseAutoConnectActivity {

    private TextView tvSyncGpsDataStatus;

    private GpsCallBack.ISyncGpsDataCallBack syncGpsDataCallBack = new GpsCallBack.ISyncGpsDataCallBack() {
        @Override
        public void onStart() {
            tvSyncGpsDataStatus.setText("onStart");
        }

        @Override
        public void onProgress(int progress) {
            tvSyncGpsDataStatus.setText("progress=" + progress);
        }

        @Override
        public void onGetGpsData(HealthGps healthGps, List<HealthGpsItem> healthGpsItems, boolean isSectionItemData) {
            tvSyncGpsDataStatus.setText("onGetGpsData");
        }

        @Override
        public void onFinish() {
            tvSyncGpsDataStatus.setText("onFinish");
        }

        @Override
        public void onFailed() {
            tvSyncGpsDataStatus.setText("onFailed");
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps_main);

        tvSyncGpsDataStatus = findViewById(R.id.sync_gps_status_tv);

        BLEManager.registerSyncGpsDataCallBack(syncGpsDataCallBack);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BLEManager.unregisterSyncGpsDataCallBack(syncGpsDataCallBack);
    }

    public void synGpsData(View view){
        BLEManager.startSyncGpsData();
    }

    public void tranAgpsFile(View view){
        startActivity(new Intent(this, AGpsSectionTranslateActivity.class));
    }

    public void autoTranAgpsFile(View view){
        startActivity(new Intent(this, AGpsAutoTranslateActivity.class));
    }

    public void createTestGpsData(View view){
        new Thread(new TestGpsDataUtil()).start();
    }

    public void showDataOnMap(View view){
        startActivity(new Intent(this, BaiDuMapActivity.class));
    }
}
