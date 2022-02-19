package test.com.ido.set;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ido.ble.BLEManager;
import com.ido.ble.callback.SettingCallBack;
import com.ido.ble.protocol.model.SystemTime;

import java.util.Calendar;

import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;

public class SetTimeActivity extends BaseAutoConnectActivity {

    private TextView tvResult;
    private EditText etYear, etMonth, etDay, etHour, etMin, etSecond, etWeek, etZone;

    private SettingCallBack.ICallBack iCallBack = new SettingCallBack.ICallBack() {
        @Override
        public void onSuccess(SettingCallBack.SettingType type, Object returnData) {
            tvResult.setText("set time ok");
        }

        @Override
        public void onFailed(SettingCallBack.SettingType type) {
            tvResult.setText("set time failed");
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_time);
        etYear = (EditText) findViewById(R.id.set_time_year);
        etMonth = (EditText) findViewById(R.id.set_time_month);
        etDay = (EditText) findViewById(R.id.set_time_day);
        etHour = (EditText) findViewById(R.id.set_time_hour);
        etMin = (EditText) findViewById(R.id.set_time_min);
        etSecond = (EditText) findViewById(R.id.set_time_second);
        etWeek = (EditText) findViewById(R.id.set_time_weak);
        etZone = findViewById(R.id.set_time_zone);

        tvResult = (TextView) findViewById(R.id.set_time_tv_result);

        etYear.setText(""+Calendar.getInstance().get(Calendar.YEAR));
        etMonth.setText(""+Calendar.getInstance().get(Calendar.MONTH));
        etDay.setText(""+Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        etHour.setText(""+Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        etMin.setText(""+Calendar.getInstance().get(Calendar.MINUTE));
        etSecond.setText(""+Calendar.getInstance().get(Calendar.SECOND));
        etWeek.setText(""+(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)-1));

        BLEManager.registerSettingCallBack(iCallBack);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        BLEManager.unregisterSettingCallBack(iCallBack);
        Log.e("eee", "onDestroy");
    }


    public void setTime(View v){
        tvResult.setText("start set time ...");

        SystemTime systemTime = new SystemTime();
        systemTime.year = Integer.parseInt(etYear.getText().toString());
        systemTime.monuth = Integer.parseInt(etMonth.getText().toString());
        systemTime.day = Integer.parseInt(etDay.getText().toString());
        systemTime.hour = Integer.parseInt(etHour.getText().toString());
        systemTime.minute = Integer.parseInt(etMin.getText().toString());
        systemTime.second = Integer.parseInt(etSecond.getText().toString());
        systemTime.week = Integer.parseInt(etWeek.getText().toString()) - 1;
        systemTime.time_zone = Integer.parseInt(etZone.getText().toString());
        BLEManager.setTime(systemTime);
    }

    public void setZone(View view){
        int zone = Integer.parseInt(etZone.getText().toString());
    }
}
