package test.com.ido.app2device;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ido.ble.BLEManager;
import com.ido.ble.callback.BloodPressureMeasureCallBack;
import com.ido.ble.protocol.model.BloodPressureMeasureDeviceReplyData;

import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;

public class BloodPressureMeasureActivity extends BaseAutoConnectActivity {

    private TextView tvResult;
    private BloodPressureMeasureCallBack.ICallBack iCallBack = new BloodPressureMeasureCallBack.ICallBack() {

        @Override
        public void onReply(BloodPressureMeasureDeviceReplyData reply) {
            tvResult.setText(reply.toString());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_pressure_measure);
        initView();
        BLEManager.registerBloodPressureMeasureCallBack(iCallBack);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BLEManager.unregisterBloodPressureMeasureCallBack(iCallBack);
    }

    private void initView() {
        tvResult = (TextView) findViewById(R.id.blood_pressure_result);
    }

    public void startMeasure(View view){
        tvResult.setText("start....");
        BLEManager.startMeasureBloodPressure();
    }

    public void stopMeasure(View view){
        tvResult.setText("stop....");
        BLEManager.stopMeasureBloodPressure();
    }

    public void getBloodData(View view){
        tvResult.setText("get data....");
        BLEManager.getBloodPressureData();
    }
}
