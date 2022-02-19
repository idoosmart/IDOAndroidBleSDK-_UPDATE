package test.com.ido.set;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.ido.ble.BLEManager;
import com.ido.ble.LocalDataManager;
import com.ido.ble.callback.SettingCallBack;
import com.ido.ble.protocol.model.NotDisturbPara;

import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;

public class SetNotDisturbActivity extends BaseAutoConnectActivity {

    private Switch switchDoNotDisturb;
    private EditText edStartHour,edStartMin,edEndHour,edEndMin;

    private SettingCallBack.ICallBack iCallBack = new SettingCallBack.ICallBack() {
        @Override
        public void onSuccess(SettingCallBack.SettingType type, Object returnData) {
            Toast.makeText(SetNotDisturbActivity.this, R.string.set_para_tip_msg_ok, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailed(SettingCallBack.SettingType type) {
            Toast.makeText(SetNotDisturbActivity.this, R.string.set_para_tip_msg_failed, Toast.LENGTH_SHORT).show();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_not_disturb);

        edEndHour=(EditText)findViewById(R.id.set_para_not_disturb_end_hour);
        edEndMin=(EditText)findViewById(R.id.set_para_not_disturb_end_min);
        edStartHour=(EditText)findViewById(R.id.set_para_not_disturb_start_hour);
        edStartMin=(EditText)findViewById(R.id.set_para_not_disturb_start_min);
        switchDoNotDisturb=(Switch)findViewById(R.id.set_para_not_disturb_switch);

        BLEManager.registerSettingCallBack(iCallBack);

        initData();

    }

    private void initData() {
        NotDisturbPara notDisturbPara = LocalDataManager.getNotDisturbPara();
        if (notDisturbPara == null){
            return;
        }

        switchDoNotDisturb.setChecked(notDisturbPara.onOFf == NotDisturbPara.STATE_ON);
        edStartHour.setText(notDisturbPara.startHour + "");
        edStartMin.setText(notDisturbPara.startMinute + "");
        edEndHour.setText(notDisturbPara.endHour + "");
        edEndMin.setText(notDisturbPara.endMinute + "");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BLEManager.unregisterSettingCallBack(iCallBack);
    }

    public void toSetNotDisturb(View view){

        int startHour=Integer.parseInt(edStartHour.getText().toString());
        int startMin=Integer.parseInt(edStartMin.getText().toString());
        int endHour=Integer.parseInt(edEndHour.getText().toString());
        int endMin=Integer.parseInt(edEndMin.getText().toString());

        NotDisturbPara notDisturbPara = new NotDisturbPara();
        notDisturbPara.onOFf = switchDoNotDisturb.isChecked() ? NotDisturbPara.STATE_ON : NotDisturbPara.STATE_OFF;
        notDisturbPara.startHour = startHour;
        notDisturbPara.startMinute = startMin;
        notDisturbPara.endHour = endHour;
        notDisturbPara.endMinute = endMin;

        BLEManager.setNotDisturbPara(notDisturbPara);
    }
}
