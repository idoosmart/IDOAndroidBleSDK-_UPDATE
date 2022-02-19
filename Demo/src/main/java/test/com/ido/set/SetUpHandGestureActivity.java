package test.com.ido.set;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.ido.ble.BLEManager;
import com.ido.ble.LocalDataManager;
import com.ido.ble.callback.SettingCallBack;
import com.ido.ble.protocol.model.UpHandGesture;

import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;

public class SetUpHandGestureActivity extends BaseAutoConnectActivity {

    private Switch switchOnOff, switchHasTimeRange;

    private EditText etStartHour, etStartMin, etEndHour, etEndMin, etShowTime;

    private SettingCallBack.ICallBack iCallBack = new SettingCallBack.ICallBack() {
        @Override
        public void onSuccess(SettingCallBack.SettingType type, Object returnData) {
            Toast.makeText(SetUpHandGestureActivity.this, R.string.set_para_tip_msg_ok, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailed(SettingCallBack.SettingType type) {
            Toast.makeText(SetUpHandGestureActivity.this, R.string.set_para_tip_msg_failed, Toast.LENGTH_SHORT).show();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_hand_gesture);

        initView();
        initData();
        BLEManager.registerSettingCallBack(iCallBack);
    }

    private void initData() {
        UpHandGesture upHandGesture = LocalDataManager.getUpHandGesture();
        if (upHandGesture == null){
            return;
        }

        switchOnOff.setChecked(upHandGesture.onOff == UpHandGesture.STATE_ON);
        etShowTime.setText("" + upHandGesture.showSecond);
        switchHasTimeRange.setChecked(upHandGesture.hasTimeRange == UpHandGesture.TIME_RANGE_ON);
        etStartHour.setText("" + upHandGesture.startHour);
        etStartMin.setText(upHandGesture.startMinute + "");
        etEndHour.setText(upHandGesture.endHour +  "");
        etEndMin.setText(upHandGesture.endMinute + "");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BLEManager.unregisterSettingCallBack(iCallBack);
    }

    private void initView() {
        switchOnOff = (Switch) findViewById(R.id.set_para_up_hand_switch);
        switchHasTimeRange = (Switch) findViewById(R.id.set_para_up_hand_has_time_range);

        etShowTime = (EditText) findViewById(R.id.set_para_up_hand_time_et);
        etStartHour = (EditText) findViewById(R.id.set_para_up_hand_starthour);
        etStartMin = (EditText) findViewById(R.id.set_para_up_hand_startmin);
        etEndHour = (EditText) findViewById(R.id.set_para_up_hand_mode_endhour);
        etEndMin = (EditText) findViewById(R.id.set_para_up_hand_endmin);


    }

    public void toSetUpHandGesture(View v){
        UpHandGesture upHandGesture = new UpHandGesture();
        upHandGesture.onOff = switchOnOff.isChecked() ? UpHandGesture.STATE_ON : UpHandGesture.STATE_OFF;
        upHandGesture.hasTimeRange = switchHasTimeRange.isChecked()? UpHandGesture.TIME_RANGE_ON : UpHandGesture.TIME_RANGE_OFF;
        upHandGesture.showSecond = Integer.parseInt(etShowTime.getText().toString());
        if (switchHasTimeRange.isChecked()) {
            upHandGesture.startHour = Integer.parseInt(etStartHour.getText().toString());
            upHandGesture.startMinute = Integer.parseInt(etStartMin.getText().toString());
            upHandGesture.endHour = Integer.parseInt(etEndHour.getText().toString());
            upHandGesture.endMinute = Integer.parseInt(etEndMin.getText().toString());

        }
        BLEManager.setUpHandGesture(upHandGesture);
    }
}
