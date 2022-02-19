package test.com.ido.set;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ido.ble.BLEManager;
import com.ido.ble.callback.SettingCallBack;
import com.ido.ble.protocol.model.Alarm;

import java.util.ArrayList;
import java.util.List;

import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;
import test.com.ido.utils.DataUtils;

public class ShowAlarmsActivity extends BaseAutoConnectActivity {

    private ListView lvAlarm;
    private List<Alarm> mAlarmList;
    private List<Alarm> tempList = new ArrayList<>();
    private SettingCallBack.ICallBack iCallBack = new SettingCallBack.ICallBack() {
        @Override
        public void onSuccess(SettingCallBack.SettingType type, Object returnData) {
            closeProgressDialog();
            Toast.makeText(ShowAlarmsActivity.this, "delete ok", Toast.LENGTH_SHORT).show();
            DataUtils.getInstance().saveAlarm(tempList);
            reset();
        }

        @Override
        public void onFailed(SettingCallBack.SettingType type) {
            closeProgressDialog();
            Toast.makeText(ShowAlarmsActivity.this, "delete failed", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_alarms);
        lvAlarm = findViewById(R.id.show_alarm_list);
        lvAlarm.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showProgressDialog("deleting...");
                tempList.clear();
                tempList.addAll(mAlarmList);
                tempList.remove(position);
                BLEManager.setAlarm(tempList);
                return true;
            }
        });


    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        BLEManager.unregisterSettingCallBack(iCallBack);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BLEManager.registerSettingCallBack(iCallBack);
        reset();

    }

    private void reset(){
        mAlarmList = DataUtils.getInstance().getAlarm();

        lvAlarm.setAdapter(new AlarmAdapter(mAlarmList));
    }

    public void addAlarm(View view){
        startActivity(new Intent(this, SetAlarmActivity.class));
    }

    class AlarmAdapter extends BaseAdapter{
        List<Alarm> alarmList;
        AlarmAdapter(List<Alarm> alarmList){
            this.alarmList = alarmList;
        }

        @Override
        public int getCount() {
            return alarmList.size();
        }

        @Override
        public Object getItem(int position) {
            return alarmList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = new TextView(getBaseContext());
            Alarm alarm = alarmList.get(position);
            textView.setText(alarm.toString());
            return textView;
        }
    }
}
