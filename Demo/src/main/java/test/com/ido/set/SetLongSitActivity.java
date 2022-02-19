package test.com.ido.set;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.ido.ble.BLEManager;
import com.ido.ble.LocalDataManager;
import com.ido.ble.callback.SettingCallBack;
import com.ido.ble.protocol.model.LongSit;

import java.util.ArrayList;

import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;

public class SetLongSitActivity extends BaseAutoConnectActivity implements CompoundButton.OnCheckedChangeListener {

    private Switch switchLongSit, switch1, switch2, switch3, switch4, switch5, switch6, switch7;
    private Button btnCommit;
    private EditText edLenth, edStartHour, edStartMin, edEndHour, edEndMin;
    private int startHour, startMinute, endHour, endMinute, lenth;
    private boolean onOff;
    private ArrayList<Switch> switchs = new ArrayList<Switch>();
    private boolean[] weeks = new boolean[7];

    private SettingCallBack.ICallBack iCallBack = new SettingCallBack.ICallBack() {
        @Override
        public void onSuccess(SettingCallBack.SettingType type, Object returnData) {
            Toast.makeText(SetLongSitActivity.this, R.string.set_para_tip_msg_ok, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailed(SettingCallBack.SettingType type) {
            Toast.makeText(SetLongSitActivity.this, R.string.set_para_tip_msg_failed, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_long_sit);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
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
        switch1 = (Switch) findViewById(R.id.switch_1);
        switch2 = (Switch) findViewById(R.id.switch_2);
        switch3 = (Switch) findViewById(R.id.switch_3);
        switch4 = (Switch) findViewById(R.id.switch_4);
        switch5 = (Switch) findViewById(R.id.switch_5);
        switch6 = (Switch) findViewById(R.id.switch_6);
        switch7 = (Switch) findViewById(R.id.switch_7);
        switchs.add(switch1);
        switchs.add(switch2);
        switchs.add(switch3);
        switchs.add(switch4);
        switchs.add(switch5);
        switchs.add(switch6);
        switchs.add(switch7);
        switchLongSit = (Switch) findViewById(R.id.switch_longsit);
        btnCommit = (Button) findViewById(R.id.btn_longsit_commit);
        edEndHour = (EditText) findViewById(R.id.ed_longsit_endhour);
        edEndMin = (EditText) findViewById(R.id.ed_longsit_endmin);
        edStartHour = (EditText) findViewById(R.id.ed_longsit_starthour);
        edStartMin = (EditText) findViewById(R.id.ed_longsit_startmin);
        edLenth = (EditText) findViewById(R.id.ed_longsit_min);
    }

    public void initData() {
        LongSit longSit = LocalDataManager.getLongSit();
        if (longSit != null){
            onOff = longSit.isOnOff();
            switchLongSit.setChecked(longSit.isOnOff());
            edLenth.setText("" + longSit.getInterval());
            edStartHour.setText("" + longSit.getStartHour());
            edStartMin.setText("" + longSit.getStartMinute());
            edEndHour.setText("" + longSit.getEndHour());
            edEndMin.setText("" + longSit.getEndMinute());

            for (int i = 0; i < switchs.size() ; i ++){
                switchs.get(i).setChecked(longSit.getWeeks()[i]);
                weeks[i] = longSit.getWeeks()[i];
            }
        }
    }

    public void addListener() {
        switch1.setOnCheckedChangeListener(this);
        switch2.setOnCheckedChangeListener(this);
        switch3.setOnCheckedChangeListener(this);
        switch4.setOnCheckedChangeListener(this);
        switch5.setOnCheckedChangeListener(this);
        switch6.setOnCheckedChangeListener(this);
        switch7.setOnCheckedChangeListener(this);
        switchLongSit.setOnCheckedChangeListener(this);
        btnCommit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                startHour = Integer.parseInt(edStartHour.getText().toString());
                startMinute = Integer.parseInt(edStartMin.getText().toString());
                endHour = Integer.parseInt(edEndHour.getText().toString());
                endMinute = Integer.parseInt(edEndMin.getText().toString());
                lenth = Integer.parseInt(edLenth.getText().toString());


                LongSit longSit = new LongSit();
                longSit.setStartHour(startHour);
                longSit.setStartMinute(startMinute);
                longSit.setEndHour(endHour);
                longSit.setEndMinute(endMinute);
                longSit.setInterval(lenth);
                longSit.setOnOff(onOff);
                longSit.setWeeks(weeks);
                BLEManager.setLongSit(longSit);
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
        switch (arg0.getId()) {
            case R.id.switch_1:
                weeks[0] = arg1;
                break;
            case R.id.switch_2:
                weeks[1] = arg1;
                break;
            case R.id.switch_3:
                weeks[2] = arg1;
                break;
            case R.id.switch_4:
                weeks[3] = arg1;
                break;
            case R.id.switch_5:
                weeks[4] = arg1;
                break;
            case R.id.switch_6:
                weeks[5] = arg1;
                break;
            case R.id.switch_7:
                weeks[6] = arg1;
                break;
            case R.id.switch_longsit:
                onOff = arg1;
                break;

            default:
                break;
        }
    }

}
