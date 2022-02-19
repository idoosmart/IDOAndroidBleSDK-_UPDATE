package test.com.ido.set;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.ido.ble.BLEManager;
import com.ido.ble.LocalDataManager;
import com.ido.ble.callback.SettingCallBack;
import com.ido.ble.protocol.model.Alarm;

import java.util.ArrayList;
import java.util.List;

import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;
import test.com.ido.utils.DataUtils;

/**
 *
 * @author Administrator demo只提供演示，只设置一组闹钟，闹钟最大支持十个，开发者可以自行设置，为方便演示，demo只设置一个
 *         每次增删改闹钟都必须重新提交一次所有闹钟，不然会设置不上
 */
public class SetAlarmActivity extends BaseAutoConnectActivity implements android.widget.CompoundButton.OnCheckedChangeListener {
    private Switch switchAlarm, switch1, switch2, switch3, switch4, switch5, switch6, switch7;
    private RadioGroup rgAlarms;
    private RadioButton rbShuijiao, rbQichuang, rbDuanlian, rbChiyao, rbYuehui, rbJuhui, rbHuiyi, rbZidingyi;
    private Button btnCommit;
    private EditText edMin, edHour;
    private boolean onOff;// 闹钟开关

    private int alarmType;// 闹钟类型
    private ArrayList<Switch> switchs = new ArrayList<Switch>();
//    private ArrayList<Alarm> alarms = new ArrayList<Alarm>();
    private boolean[] weeks = new boolean[7];
    private Alarm mAlarm;

    private SettingCallBack.ICallBack iCallBack = new SettingCallBack.ICallBack() {
        @Override
        public void onSuccess(SettingCallBack.SettingType type, Object returnData) {
            closeProgressDialog();
            Toast.makeText(SetAlarmActivity.this, R.string.set_para_tip_msg_ok, Toast.LENGTH_SHORT).show();
            List<Alarm> alarms = DataUtils.getInstance().getAlarm();
            if (alarms == null){
                alarms = new ArrayList<>();
            }
            alarms.add(mAlarm);
            DataUtils.getInstance().saveAlarm(alarms);
        }

        @Override
        public void onFailed(SettingCallBack.SettingType type) {
            closeProgressDialog();
            Toast.makeText(SetAlarmActivity.this, R.string.set_para_tip_msg_failed, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);
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
        rgAlarms = (RadioGroup) findViewById(R.id.rd_type);
        rbChiyao = (RadioButton) findViewById(R.id.chiyao);
        rbShuijiao = (RadioButton) findViewById(R.id.shuijiao);
        rbDuanlian = (RadioButton) findViewById(R.id.duanlian);
        rbHuiyi = (RadioButton) findViewById(R.id.huiyi);
        rbJuhui = (RadioButton) findViewById(R.id.juhui);
        rbQichuang = (RadioButton) findViewById(R.id.qichuang);
        rbYuehui = (RadioButton) findViewById(R.id.yuehui);
        rbZidingyi = (RadioButton) findViewById(R.id.zidingy);
        switchs.add(switch1);
        switchs.add(switch2);
        switchs.add(switch3);
        switchs.add(switch4);
        switchs.add(switch5);
        switchs.add(switch6);
        switchs.add(switch7);
        switchAlarm = (Switch) findViewById(R.id.switch_alarm);
        btnCommit = (Button) findViewById(R.id.btn_alarm_commit);
        edHour = (EditText) findViewById(R.id.ed_alarm_hour);
        edMin = (EditText) findViewById(R.id.ed_alarm_min);
    }

    public void initData() {
        List<Alarm> alarmList = LocalDataManager.getAlarm();
        if (alarmList == null || alarmList.size() == 0){
            return;
        }

        //测试，只取一个
        Alarm alarm = alarmList.get(0);

        onOff = alarm.getOn_off();
        switchAlarm.setChecked(alarm.getOn_off());

        alarmType = alarm.getAlarmType();
        rgAlarms.check(getCheckedId(alarm.getAlarmType()));

        edHour.setText(alarm.getAlarmHour() + "");
        edMin.setText(alarm.getAlarmMinute() +"");

        for (int i =0 ; i < switchs.size() ; i++){
            switchs.get(i).setChecked(alarm.getWeekRepeat()[i]);
            weeks[i] = alarm.getWeekRepeat()[i];
        }


    }

    private int getCheckedId(int type) {
        int id = R.id.qichuang;
        switch (type) {
            case Alarm.TYPE_GETUP:

                 id = R.id.qichuang;
                break;
            case Alarm.TYPE_SLEEP:
                id = R.id.shuijiao;
                break;
            case Alarm.TYPE_EXERCISE:
                id = R.id.duanlian;
                break;
            case Alarm.TYPE_MEDICINE:
                id = R.id.chiyao;
                break;
            case Alarm.TYPE_ENGAGEMENT:
                id = R.id.yuehui;
                break;
            case Alarm.TYPE_GATHERING:
                id = R.id.juhui;
                break;
            case Alarm.TYPE_MEETING:
                id = R.id.huiyi;
                break;
            case Alarm.TYPE_CUSTOMIZE:
                id = R.id.zidingy;
                break;

        }

        return id;
    }

    public void addListener() {
        switch1.setOnCheckedChangeListener(this);
        switch2.setOnCheckedChangeListener(this);
        switch3.setOnCheckedChangeListener(this);
        switch4.setOnCheckedChangeListener(this);
        switch5.setOnCheckedChangeListener(this);
        switch6.setOnCheckedChangeListener(this);
        switch7.setOnCheckedChangeListener(this);
        switchAlarm.setOnCheckedChangeListener(this);
        rgAlarms.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                switch (arg1) {
                    case R.id.qichuang:
                        alarmType = Alarm.TYPE_GETUP;
                        break;
                    case R.id.shuijiao:
                        alarmType = Alarm.TYPE_SLEEP;
                        break;
                    case R.id.duanlian:
                        alarmType = Alarm.TYPE_EXERCISE;
                        break;
                    case R.id.chiyao:
                        alarmType = Alarm.TYPE_MEDICINE;
                        break;
                    case R.id.yuehui:
                        alarmType = Alarm.TYPE_ENGAGEMENT;
                        break;
                    case R.id.juhui:
                        alarmType = Alarm.TYPE_GATHERING;
                        break;
                    case R.id.huiyi:
                        alarmType = Alarm.TYPE_MEETING;
                        break;
                    case R.id.zidingy:
                        alarmType = Alarm.TYPE_CUSTOMIZE;
                        break;

                    default:
                        break;
                }
            }
        });

        btnCommit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                showProgressDialog("Add...");
                // 设置闹钟
                mAlarm = new Alarm();
                int hour = Integer.parseInt(edHour.getText().toString());
                int min = Integer.parseInt(edMin.getText().toString());
                mAlarm.setAlarmHour(hour);
                mAlarm.setAlarmMinute(min);
                mAlarm.setAlarmType(alarmType);
                mAlarm.setOn_off(onOff);
                mAlarm.setWeekRepeat(weeks);


                List<Alarm> alarms = DataUtils.getInstance().getAlarm();
                if (alarms == null){
                    alarms = new ArrayList<>();
                }else {
                    mAlarm.setAlarmId(alarms.size());
                }
                mAlarm.setAlarmId(alarms.size() + 1);
                alarms.add(mAlarm);

                BLEManager.setAlarm(alarms);

            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
        // TODO Auto-generated method stub
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
            case R.id.switch_alarm:
                onOff = arg1;
                break;
            default:
                break;
        }

    }
}

