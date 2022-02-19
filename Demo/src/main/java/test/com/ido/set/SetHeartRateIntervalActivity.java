package test.com.ido.set;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.ido.ble.BLEManager;
import com.ido.ble.LocalDataManager;
import com.ido.ble.callback.SettingCallBack;
import com.ido.ble.protocol.model.HeartRateInterval;

import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;

public class SetHeartRateIntervalActivity extends BaseAutoConnectActivity {

    private EditText edBurn, edAerobic, edLimit, edMinHrValue, edMaxHrValue;
    private Switch shMaxRemind, shMinRemind;
    private Button btnCommit;

    private EditText edStartHour, edStartMin, edEndHour, edEndMin;

    private SettingCallBack.ICallBack iCallBack = new SettingCallBack.ICallBack() {
        @Override
        public void onSuccess(SettingCallBack.SettingType type, Object returnData) {
            Toast.makeText(SetHeartRateIntervalActivity.this, R.string.set_para_tip_msg_ok, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailed(SettingCallBack.SettingType type) {
            Toast.makeText(SetHeartRateIntervalActivity.this, R.string.set_para_tip_msg_failed, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_heart_rate_interval);
        initView();
        initData();
        addListener();

        BLEManager.registerSettingCallBack(iCallBack);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BLEManager.unregisterSettingCallBack(iCallBack);
    }

    public void initView() {
        edBurn = (EditText) findViewById(R.id.ed_burn_fat_threshold);// 脂肪燃烧
        edAerobic = (EditText) findViewById(R.id.ed_aerobic_threshold);// 有氧锻炼
        edLimit = (EditText) findViewById(R.id.ed_limit_threshold);// 极限锻炼
        btnCommit = (Button) findViewById(R.id.btn_heartrateinterval_commit);
        edMinHrValue = findViewById(R.id.et_min_value);
        edMaxHrValue = findViewById(R.id.et_max_value);

        shMaxRemind = findViewById(R.id.sh_max_remind);
        shMinRemind = findViewById(R.id.sh_min_remind);

        edEndHour = (EditText) findViewById(R.id.ed_longsit_endhour);
        edEndMin = (EditText) findViewById(R.id.ed_longsit_endmin);
        edStartHour = (EditText) findViewById(R.id.ed_longsit_starthour);
        edStartMin = (EditText) findViewById(R.id.ed_longsit_startmin);
    }

    public void initData() {
        HeartRateInterval interval = LocalDataManager.getHeartRateInterval();
        if (interval != null){
            edBurn.setText("" + interval.getBurnFatThreshold());
            edAerobic.setText("" + interval.getAerobicThreshold());
            edLimit.setText("" + interval.getLimintThreshold());
        }
    }

    public void addListener() {
        btnCommit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                int burn = Integer.parseInt(edBurn.getText().toString());// 脂肪燃烧
                int aerobic = Integer.parseInt(edAerobic.getText().toString());// 有氧锻炼
                int limit = Integer.parseInt(edLimit.getText().toString());// 极限锻炼
                int minHrValue = Integer.parseInt(edMinHrValue.getText().toString());//
                int maxHrValue = Integer.parseInt(edMaxHrValue.getText().toString());//

                int startHour = Integer.parseInt(edStartHour.getText().toString());//
                int startMin = Integer.parseInt(edStartMin.getText().toString());//
                int endHour = Integer.parseInt(edEndHour.getText().toString());//
                int endMin = Integer.parseInt(edEndMin.getText().toString());//

                HeartRateInterval heartRateInterval = new HeartRateInterval();
                heartRateInterval.setBurnFatThreshold(burn);
                heartRateInterval.setAerobicThreshold(aerobic);
                heartRateInterval.setLimintThreshold(limit);
                heartRateInterval.setAnaerobicThreshold(200);
                heartRateInterval.setWarmUpThreshold(201);
                heartRateInterval.setUserMaxHR(maxHrValue);
                heartRateInterval.setMinHr(minHrValue);
                heartRateInterval.setMaxHrRemind(shMaxRemind.isChecked() ? HeartRateInterval.REMIND_ON:HeartRateInterval.REMIND_OFF);
                heartRateInterval.setMinHrRemind(shMinRemind.isChecked() ? HeartRateInterval.REMIND_ON:HeartRateInterval.REMIND_OFF);
                heartRateInterval.setRemindStartHour(startHour);
                heartRateInterval.setRemindStartMinute(startMin);
                heartRateInterval.setRemindStopHour(endHour);
                heartRateInterval.setRemindStopMinute(endMin);

                BLEManager.setHeartRateInterval(heartRateInterval);

            }
        });
    }

}
