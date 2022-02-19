package test.com.ido.set;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.ido.ble.BLEManager;
import com.ido.ble.LocalDataManager;
import com.ido.ble.callback.SettingCallBack;
import com.ido.ble.protocol.model.HeartRateMeasureMode;
import com.ido.ble.protocol.model.HeartRateMeasureModeV3;

import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;

public class SetHeartRateModeActivity extends BaseAutoConnectActivity {

    private Switch switchHasTimeRange;
    private RadioGroup rgbMode;

    private EditText edStartHour, edStartMin, edEndHour, edEndMin, etInterval;
    private int hasTimeRange, measureMode = HeartRateMeasureMode.MODE_AUTO;

    private SettingCallBack.ICallBack iCallBack = new SettingCallBack.ICallBack() {
        @Override
        public void onSuccess(SettingCallBack.SettingType type, Object returnData) {
            Toast.makeText(SetHeartRateModeActivity.this, R.string.set_para_tip_msg_ok, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailed(SettingCallBack.SettingType type) {
            Toast.makeText(SetHeartRateModeActivity.this, R.string.set_para_tip_msg_failed, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_heart_rate_mode);

        initView();
        initData();
        BLEManager.registerSettingCallBack(iCallBack);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BLEManager.unregisterSettingCallBack(iCallBack);
    }

    private void initData() {
        HeartRateMeasureMode heartRateMeasureMode = LocalDataManager.getHeartRateMode();
        if (heartRateMeasureMode == null){
            return;
        }

        if (heartRateMeasureMode.mode == HeartRateMeasureMode.MODE_MANUAL){
            rgbMode.check(R.id.set_para_heart_rate_mode_manual);
        }else if (heartRateMeasureMode.mode == HeartRateMeasureMode.MODE_OFF){
            rgbMode.check(R.id.set_para_heart_rate_mode_off);
        }else if (heartRateMeasureMode.mode == HeartRateMeasureMode.MODE_AUTO){
            rgbMode.check( measureMode = HeartRateMeasureMode.MODE_AUTO);
        }

        switchHasTimeRange.setChecked(heartRateMeasureMode.hasTimeRange == HeartRateMeasureMode.TIME_RANGE_ON);

        edStartHour.setText(heartRateMeasureMode.startHour + "");
        edStartMin.setText(heartRateMeasureMode.startMinute + "");
        edEndHour.setText(heartRateMeasureMode.endHour + "");
        edEndMin.setText(heartRateMeasureMode.endMinute + "");
    }

    private void initView() {
        switchHasTimeRange = (Switch) findViewById(R.id.set_para_heart_rate_mode_switch_has_time_range);
        switchHasTimeRange.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    hasTimeRange = HeartRateMeasureMode.TIME_RANGE_ON;
                }else {
                    hasTimeRange = HeartRateMeasureMode.TIME_RANGE_OFF;
                }
            }
        });

        rgbMode = (RadioGroup) findViewById(R.id.set_para_heart_rate_mode_group);
        rgbMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.set_para_heart_rate_mode_manual:
                        measureMode = HeartRateMeasureMode.MODE_MANUAL;
                        break;
                    case R.id.set_para_heart_rate_mode_off:
                        measureMode = HeartRateMeasureMode.MODE_OFF;
                        break;
                    case R.id.set_para_heart_rate_mode_atuo:
                        measureMode = HeartRateMeasureMode.MODE_AUTO;
                        break;

                }
            }
        });


        edEndHour = (EditText) findViewById(R.id.set_para_heart_rate_mode_endhour);
        edEndMin = (EditText) findViewById(R.id.set_para_heart_rate_mode_endmin);
        edStartHour = (EditText) findViewById(R.id.set_para_heart_rate_mode_starthour);
        edStartMin = (EditText) findViewById(R.id.set_para_heart_rate_mode_startmin);
        etInterval = findViewById(R.id.set_para_heart_rate_mode_interval);
    }

    public void setHeartRateMeasureMode(View view){

        HeartRateMeasureMode heartRateMode = new HeartRateMeasureMode();
        heartRateMode.hasTimeRange = hasTimeRange;
        heartRateMode.measurementInterval = Integer.parseInt(etInterval.getText().toString());
        if (hasTimeRange == HeartRateMeasureMode.TIME_RANGE_ON) {
            heartRateMode.startHour = Integer.parseInt(edStartHour.getText().toString());
            heartRateMode.startMinute = Integer.parseInt(edStartMin.getText().toString());
            heartRateMode.endHour = Integer.parseInt(edEndHour.getText().toString());
            heartRateMode.endMinute = Integer.parseInt(edEndMin.getText().toString());
        }
        heartRateMode.mode = measureMode;

        BLEManager.setHeartRateMeasureMode(heartRateMode);

    }

    public void setHeartRateMeasureModeV3(View view){

        HeartRateMeasureModeV3 heartRateMode = new HeartRateMeasureModeV3();
        heartRateMode.hasTimeRange = hasTimeRange;
        heartRateMode.measurementInterval = Integer.parseInt(etInterval.getText().toString());
        if (hasTimeRange == HeartRateMeasureMode.TIME_RANGE_ON) {
            heartRateMode.startHour = Integer.parseInt(edStartHour.getText().toString());
            heartRateMode.startMinute = Integer.parseInt(edStartMin.getText().toString());
            heartRateMode.endHour = Integer.parseInt(edEndHour.getText().toString());
            heartRateMode.endMinute = Integer.parseInt(edEndMin.getText().toString());
        }
        heartRateMode.mode = measureMode;

        BLEManager.setHeartRateMeasureModeV3(heartRateMode);

    }
}
