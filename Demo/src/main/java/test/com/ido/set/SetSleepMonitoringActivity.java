package test.com.ido.set;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.ido.ble.BLEManager;
import com.ido.ble.callback.SettingCallBack;
import com.ido.ble.protocol.model.NotDisturbPara;
import com.ido.ble.protocol.model.SleepMonitoringPara;

import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;

public class SetSleepMonitoringActivity extends BaseAutoConnectActivity {

    private Switch switchSleep;
    private EditText edStartHour,edStartMin,edEndHour,edEndMin;

    private SettingCallBack.ICallBack iCallBack = new SettingCallBack.ICallBack() {
        @Override
        public void onSuccess(SettingCallBack.SettingType type, Object returnData) {
            Toast.makeText(SetSleepMonitoringActivity.this, R.string.set_para_tip_msg_ok, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailed(SettingCallBack.SettingType type) {
            Toast.makeText(SetSleepMonitoringActivity.this, R.string.set_para_tip_msg_failed, Toast.LENGTH_SHORT).show();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_sleep_monitoring);
        BLEManager.registerSettingCallBack(iCallBack);

        edEndHour=(EditText)findViewById(R.id.set_para_sleep_end_hour);
        edEndMin=(EditText)findViewById(R.id.set_para_sleep_end_min);
        edStartHour=(EditText)findViewById(R.id.set_para_sleep_start_hour);
        edStartMin=(EditText)findViewById(R.id.set_para_sleep_start_min);
        switchSleep =(Switch)findViewById(R.id.set_para_sleep_switch);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BLEManager.unregisterSettingCallBack(iCallBack);
    }

    public void toSetSleepPara(View v){



    }
}
