package test.com.ido.set;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.ido.ble.BLEManager;
import com.ido.ble.LocalDataManager;
import com.ido.ble.callback.SettingCallBack;
import com.ido.ble.protocol.model.Units;

import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;

public class SetUnitActivity extends BaseAutoConnectActivity {

    private SettingCallBack.ICallBack iCallBack = new SettingCallBack.ICallBack() {
        @Override
        public void onSuccess(SettingCallBack.SettingType type, Object returnData) {
            Toast.makeText(SetUnitActivity.this, R.string.set_para_tip_msg_ok, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailed(SettingCallBack.SettingType type) {
            Toast.makeText(SetUnitActivity.this, R.string.set_para_tip_msg_failed, Toast.LENGTH_SHORT).show();
        }
    };

    private RadioGroup distUnitRG, weightUnitRG, tempUnitRG, langRG, timeModeRG, gpsSwitchRG;
    private EditText wakeET, runET;
    private Units mUnits;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_unit);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        BLEManager.registerSettingCallBack(iCallBack);

        initView();
        intData();
    }

    private void intData() {
        Units units = LocalDataManager.getUnits();
        if (units != null){
            mUnits = units;

            if (units.dist == Units.DIST_UNIT_KM){
                distUnitRG.check(R.id.set_unit_dist_unit_km);
            }else if (units.dist == Units.DIST_UNIT_MI){
                distUnitRG.check(R.id.set_unit_dist_unit_mi);
            }else if (units.dist == Units.UNIT_DEFAULT){
                distUnitRG.check(R.id.set_unit_dist_unit_default);
            }

            if (units.weight == Units.UNIT_DEFAULT){
                weightUnitRG.check(R.id.set_unit_weight_unit_default);
            }else if (units.weight == Units.WEIGHT_UNIT_KG){
                weightUnitRG.check(R.id.set_unit_weight_kg);
            }else if (units.weight == Units.WEIGHT_UNIT_LB){
                weightUnitRG.check(R.id.set_unit_weight_lb);
            }else if (units.weight == Units.WEIGHT_UNIT_ST){
                weightUnitRG.check(R.id.set_unit_weight_st);
            }

            if (units.temp == Units.TEMP_UNIT_C){
                tempUnitRG.check(R.id.set_unit_temp_unit_c);
            }else if (units.temp == Units.TEMP_UNIT_F){
                tempUnitRG.check(R.id.set_unit_temp_unit_f);
            }else if (units.temp == Units.UNIT_DEFAULT){
                tempUnitRG.check(R.id.set_unit_temp_unit_default);
            }


            wakeET.setText("" + units.stride);
            langRG.check(getLangCheckedId(units.language));

            if (units.timeMode == Units.TIME_MODE_24){
                timeModeRG.check(R.id.set_unit_time_mode_24);
            }else if (units.timeMode == Units.TIME_MODE_12){
                timeModeRG.check(R.id.set_unit_time_mode_12);
            }else if (units.timeMode == Units.UNIT_DEFAULT){
                timeModeRG.check(R.id.set_unit_time_mode_default);
            }

            runET.setText("" + units.strideRun);

            if (units.strideGPSCal == Units.STRIDE_GPS_CAL_SWITCH_ON){
                gpsSwitchRG.check(R.id.set_unit_dist_gps_on);
            }else if (units.strideGPSCal == Units.STRIDE_GPS_CAL_SWITCH_OFF){
                gpsSwitchRG.check(R.id.set_unit_dist_gps_off);
            }else if (units.strideGPSCal == Units.UNIT_DEFAULT){
                gpsSwitchRG.check(R.id.set_unit_dist_gps_default);
            }


        }else {
            mUnits = new Units();
            mUnits.dist = Units.UNIT_DEFAULT;
            mUnits.weight = Units.UNIT_DEFAULT;
            mUnits.temp = Units.UNIT_DEFAULT;
            mUnits.stride = 0;
            mUnits.language = Units.UNIT_DEFAULT;
            mUnits.timeMode = Units.UNIT_DEFAULT;
            mUnits.strideRun = 0;
            mUnits.strideGPSCal = Units.UNIT_DEFAULT;
        }
    }

    private int getLangCheckedId(int lang){
        int checkId = R.id.set_unit_lang_default;
        switch (lang){
            case Units.UNIT_DEFAULT:
                checkId = R.id.set_unit_lang_default;
                break;
            case Units.LANG_ZH:
                checkId = R.id.set_unit_lang_zh;
                break;
            case Units.LANG_EN:
                checkId = R.id.set_unit_lang_en;
                break;
            case Units.LANG_FR:
                checkId = R.id.set_unit_lang_fr;
                break;
            case Units.LANG_DE:
                checkId = R.id.set_unit_lang_de;
                break;
            case Units.LANG_IT:
                checkId = R.id.set_unit_lang_it;
                break;
            case R.id.set_unit_lang_es:
                checkId = Units.LANG_ES;
                break;
            case Units.LANG_JA:
                checkId = R.id.set_unit_lang_ja;
                break;
            case Units.LANG_PO:
                checkId = R.id.set_unit_lang_po;
                break;
            case Units.LANG_CZ:
                checkId = R.id.set_unit_lang_cz;
                break;

        }

        return checkId;
    }
    private void initView() {
        wakeET = (EditText) findViewById(R.id.set_unit_stride_wake_et);
        runET = (EditText) findViewById(R.id.set_unit_stride_run_et);

        distUnitRG = (RadioGroup) findViewById(R.id.set_unit_dist_unit_radio_group);
        distUnitRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.set_unit_dist_unit_default:
                        mUnits.dist = Units.UNIT_DEFAULT;
                        break;
                    case R.id.set_unit_dist_unit_km:
                        mUnits.dist = Units.DIST_UNIT_KM;
                        break;
                    case R.id.set_unit_dist_unit_mi:
                        mUnits.dist = Units.DIST_UNIT_MI;
                        break;
                }
            }
        });

        weightUnitRG = (RadioGroup) findViewById(R.id.set_unit_weight_unit_radio_group);
        weightUnitRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.set_unit_weight_unit_default:
                        mUnits.weight = Units.UNIT_DEFAULT;
                        break;
                    case R.id.set_unit_weight_kg:
                        mUnits.weight = Units.WEIGHT_UNIT_KG;
                        break;
                    case R.id.set_unit_weight_lb:
                        mUnits.weight = Units.WEIGHT_UNIT_LB;
                        break;
                    case R.id.set_unit_weight_st:
                        mUnits.weight = Units.WEIGHT_UNIT_ST;
                        break;
                }
            }
        });

        tempUnitRG = (RadioGroup) findViewById(R.id.set_unit_temp_unit_radio_group);
        tempUnitRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.set_unit_temp_unit_default:
                        mUnits.temp = Units.UNIT_DEFAULT;
                        break;
                    case R.id.set_unit_temp_unit_c:
                        mUnits.temp = Units.TEMP_UNIT_C;
                        break;
                    case R.id.set_unit_temp_unit_f:
                        mUnits.temp = Units.TEMP_UNIT_F;
                        break;
                }
            }
        });

        langRG = (RadioGroup) findViewById(R.id.set_unit_lang_radio_group);
        langRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.set_unit_lang_default:
                        mUnits.language = Units.UNIT_DEFAULT;
                        break;
                    case R.id.set_unit_lang_zh:
                        mUnits.language = Units.LANG_ZH;
                        break;
                    case R.id.set_unit_lang_en:
                        mUnits.language = Units.LANG_EN;
                        break;
                    case R.id.set_unit_lang_fr:
                        mUnits.language = Units.LANG_FR;
                        break;
                    case R.id.set_unit_lang_de:
                        mUnits.language = Units.LANG_DE;
                        break;
                    case R.id.set_unit_lang_it:
                        mUnits.language = Units.LANG_IT;
                        break;
                    case R.id.set_unit_lang_es:
                        mUnits.language = Units.LANG_ES;
                        break;
                    case R.id.set_unit_lang_ja:
                        mUnits.language = Units.LANG_JA;
                        break;
                    case R.id.set_unit_lang_po:
                        mUnits.language = Units.LANG_PO;
                        break;
                    case R.id.set_unit_lang_cz:
                        mUnits.language = Units.LANG_CZ;
                        break;

                }
            }
        });

        timeModeRG = (RadioGroup) findViewById(R.id.set_unit_time_mode_radio_group);
        timeModeRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.set_unit_time_mode_default:
                        mUnits.timeMode = Units.UNIT_DEFAULT;
                        break;
                    case R.id.set_unit_time_mode_12:
                        mUnits.timeMode = Units.TIME_MODE_12;
                        break;
                    case R.id.set_unit_time_mode_24:
                        mUnits.timeMode = Units.TIME_MODE_24;
                        break;
                }
            }
        });

        gpsSwitchRG = (RadioGroup) findViewById(R.id.set_unit_gps_switch_radio_group);
        gpsSwitchRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.set_unit_dist_gps_default:
                        mUnits.strideGPSCal = Units.UNIT_DEFAULT;
                        break;
                    case R.id.set_unit_dist_gps_off:
                        mUnits.strideGPSCal = Units.STRIDE_GPS_CAL_SWITCH_OFF;
                        break;
                    case R.id.set_unit_dist_gps_on:
                        mUnits.strideGPSCal = Units.STRIDE_GPS_CAL_SWITCH_ON;
                        break;
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BLEManager.unregisterSettingCallBack(iCallBack);
    }

    public void setUnit(View v){

        mUnits.stride = Integer.parseInt(wakeET.getText().toString());
        mUnits.strideRun = Integer.parseInt(runET.getText().toString());
        BLEManager.setUnit(mUnits);
    }
}
