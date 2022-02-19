package test.com.ido.dfu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ido.ble.BLEManager;
import com.ido.ble.callback.EnterDfuModeCallback;

import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;

public class MainUpgradeActivity extends Activity {

    private EditText etMinEnergyValue;
    private EnterDfuModeCallback.ICallBack iCallBack = new EnterDfuModeCallback.ICallBack() {
        @Override
        public void onSuccess() {
            Toast.makeText(MainUpgradeActivity.this, "success", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onError(EnterDfuModeCallback.DfuError error) {
            Toast.makeText(MainUpgradeActivity.this, "" + error, Toast.LENGTH_LONG).show();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_upgrade);
        etMinEnergyValue = findViewById(R.id.min_energy_value_et);
        findViewById(R.id.to_dfu_nodic_page_bt).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                etMinEnergyValue.setEnabled(true);
                return false;
            }
        });
        BLEManager.registerEnterDfuModeCallBack(iCallBack);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BLEManager.unregisterEnterDfuModeCallBack(iCallBack);
    }

    public void downloadFile(View view){
        startActivity(new Intent(this, DownloadFirmwareFileActivity.class));
    }

    public void to_dfu_mode(View view){
        BLEManager.enterDfuMode();
    }
    public void to_dfu(View view){

//        int minEnergyValue = Integer.parseInt(etMinEnergyValue.getText().toString());
//        BasicInfo basicInfo = LocalDataManager.getBasicInfo();
//        if (basicInfo != null && basicInfo.energe < minEnergyValue){
//            Toast.makeText(this, "手环电量(" + basicInfo.energe +"%)过低，请先充电！", Toast.LENGTH_LONG).show();
//            return;
//        }
        startActivity(new Intent(this, MainDfuActivity.class));
    }

    public void dfu_rtk(View view){
        startActivity(new Intent(this, RTKDfuActivity.class));
    }
}
