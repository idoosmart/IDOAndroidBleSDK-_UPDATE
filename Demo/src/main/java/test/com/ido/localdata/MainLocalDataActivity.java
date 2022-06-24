package test.com.ido.localdata;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.ido.ble.LocalDataManager;
import com.ido.ble.data.manage.database.HealthHeartRateSecond;
import com.ido.ble.data.manage.database.HealthHeartRateSecondItem;

import java.util.ArrayList;
import java.util.List;

import test.com.ido.R;
import test.com.ido.chart.SimpleMpChart;
import test.com.ido.connect.BaseAutoConnectActivity;


public class MainLocalDataActivity extends BaseAutoConnectActivity {

    private int dataType = DataQueryType.DATA_TYPE_SPORT;
    private RadioGroup rgbType;
    private EditText etDay, etMonth, etYear, etStartDay, etStartMonth, etStartYear, etEndDay, etEndMonth, etEndYear, etWeek;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_local_data);

        initView();
    }

    private void initView() {
        rgbType = (RadioGroup) findViewById(R.id.main_local_data_data_type_rg);

        etDay = (EditText) findViewById(R.id.one_day_data_day_et);
        etMonth = (EditText) findViewById(R.id.one_day_data_month_et);
        etYear = (EditText) findViewById(R.id.one_day_data_year_et);

        etStartDay = (EditText) findViewById(R.id.one_period_data_start_day_et);
        etStartMonth = (EditText) findViewById(R.id.one_period_data_start_month_et);
        etStartYear = (EditText) findViewById(R.id.one_period_data_start_year_et);
        etEndDay = (EditText) findViewById(R.id.one_period_data_end_day_et);
        etEndMonth = (EditText) findViewById(R.id.one_period_data_end_month_et);
        etEndYear = (EditText) findViewById(R.id.one_period_data_end_year_et);

        etWeek = (EditText) findViewById(R.id.last_n_week_data_et);
        rgbType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.main_local_data_type_activity_rb){
                    dataType = DataQueryType.DATA_TYPE_ACTIVITY;
                }else if (checkedId == R.id.main_local_data_type_health_rb){
                    dataType = DataQueryType.DATA_TYPE_SPORT;
                }else if (checkedId == R.id.main_local_data_type_heart_rate_rb){
                    dataType = DataQueryType.DATA_TYPE_HEART_RATE;
                }else if (checkedId == R.id.main_local_data_type_sleep_rb){
                    dataType = DataQueryType.DATA_TYPE_SLEEP;
                }else if (checkedId == R.id.main_local_data_type_blood_rb){
                    dataType = DataQueryType.DATA_TYPE_BLOOD;
                }

            }
        });
    }

    public void getOneTimeData(View v){
        DataQueryType dataQueryType = new DataQueryType();
        dataQueryType.dataType = dataType;
        dataQueryType.queryType = DataQueryType.QUERY_TYPE_ONE_DAY;
        dataQueryType.year = Integer.parseInt(etYear.getText().toString());
        dataQueryType.month = Integer.parseInt(etMonth.getText().toString());
        dataQueryType.day = Integer.parseInt(etDay.getText().toString());

        query(dataQueryType);

    }

    public void getOnePeriodData(View v){

        DataQueryType dataQueryType = new DataQueryType();
        dataQueryType.dataType = dataType;
        dataQueryType.queryType = DataQueryType.QUERY_TYPE_PERIOD;
        dataQueryType.year = Integer.parseInt(etStartYear.getText().toString());
        dataQueryType.month = Integer.parseInt(etStartMonth.getText().toString());
        dataQueryType.day = Integer.parseInt(etStartDay.getText().toString());

        dataQueryType.endYear = Integer.parseInt(etEndYear.getText().toString());
        dataQueryType.endMonth = Integer.parseInt(etEndMonth.getText().toString());
        dataQueryType.endDay = Integer.parseInt(etEndDay.getText().toString());

        query(dataQueryType);
    }

    public void getLastNWeekData(View v){
        DataQueryType dataQueryType = new DataQueryType();
        dataQueryType.dataType = dataType;
        dataQueryType.queryType = DataQueryType.QUERY_TYPE_WEEK;
        dataQueryType.week = Integer.parseInt(etWeek.getText().toString());

        HealthHeartRateSecond second = LocalDataManager.getHealthHeartRateSecondByDay(2019, 8, 16);

        query(dataQueryType);
    }

    private void query(DataQueryType dataQueryType){
        Intent intent = new Intent();
        if (dataQueryType.dataType == DataQueryType.DATA_TYPE_SPORT){
            intent.setClass(this, GetSportDataActivity.class);
        }else if (dataQueryType.dataType == DataQueryType.DATA_TYPE_ACTIVITY){
            intent.setClass(this, GetActivityDataActivity.class);
        }else if (dataQueryType.dataType == DataQueryType.DATA_TYPE_HEART_RATE){
            intent.setClass(this, GetHeartRateActivity.class);
        }else if (dataQueryType.dataType == DataQueryType.DATA_TYPE_SLEEP){
            intent.setClass(this, GetSleepDataActivity.class);
        }else if (dataQueryType.dataType == DataQueryType.DATA_TYPE_BLOOD){
            intent.setClass(this, GetBloodDataActivity.class);
        }

        intent.putExtra(DataQueryType.INTENT_EXTRA_FLAG, dataQueryType);
        startActivity(intent);
    }

}
