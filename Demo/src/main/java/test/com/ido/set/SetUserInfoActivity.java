package test.com.ido.set;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ido.ble.BLEManager;
import com.ido.ble.LocalDataManager;
import com.ido.ble.callback.SettingCallBack;
import com.ido.ble.callback.SyncCallBack;
import com.ido.ble.protocol.model.Goal;
import com.ido.ble.protocol.model.UserInfo;

import test.com.ido.HomeActivity;
import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;
import test.com.ido.utils.DataUtils;

public class SetUserInfoActivity extends BaseAutoConnectActivity implements View.OnClickListener {

    private Button btnSetUserInfo, btnNan,btnNv;
    private EditText edYear,edMonth,edDay, edHeight,edWeight;
    private TextView tvSex;
    private int sex= UserInfo.MALE;
    private boolean isFromScanPage = false;


    private SettingCallBack.ICallBack iCallBack = new SettingCallBack.ICallBack() {
        @Override
        public void onSuccess(SettingCallBack.SettingType type, Object returnData) {
            Toast.makeText(SetUserInfoActivity.this, R.string.set_para_tip_msg_ok, Toast.LENGTH_SHORT).show();
            closeProgressDialog();
        }

        @Override
        public void onFailed(SettingCallBack.SettingType type) {
            Toast.makeText(SetUserInfoActivity.this, R.string.set_para_tip_msg_failed, Toast.LENGTH_SHORT).show();
        }
    };

//    private SyncCallBack.IConfigCallBack iConfigCallBack = new SyncCallBack.IConfigCallBack() {
//        @Override
//        public void onStart() {
//
//        }
//
//        @Override
//        public void onStop() {
//            closeProgressDialog();
//            Toast.makeText(SetUserInfoActivity.this, "sync config info stop", Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onSuccess() {
//            closeProgressDialog();
//            DataUtils.getInstance().setIsFirst(false);
//            startActivity(new Intent(SetUserInfoActivity.this, HomeActivity.class));
//            finish();
//        }
//
//        @Override
//        public void onSyncFailed() {
//            closeProgressDialog();
//            Toast.makeText(SetUserInfoActivity.this, "sync config info failed", Toast.LENGTH_SHORT).show();
//        }
//    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_user_info);

        isFromScanPage = getIntent().getBooleanExtra("isFromScanPage", false);
        BLEManager.getFunctionTables();
        initView();
        initData();
        addListener();



//        BLEManager.registerSyncConfigCallBack(iConfigCallBack);
        BLEManager.registerSettingCallBack(iCallBack);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        BLEManager.unregisterSyncConfigCallBack(iConfigCallBack);
        BLEManager.unregisterSettingCallBack(iCallBack);
    }

    public void initView(){
        btnNan=(Button)findViewById(R.id.btn_nan);
        btnNv=(Button)findViewById(R.id.btn_nv);
        btnSetUserInfo =(Button)findViewById(R.id.btn_set_use_info);
        edDay=(EditText)findViewById(R.id.ed_day);
        edHeight =(EditText)findViewById(R.id.ed_height);
        edMonth=(EditText)findViewById(R.id.ed_month);
        edWeight=(EditText)findViewById(R.id.ed_weight);
        edYear=(EditText)findViewById(R.id.ed_year);
        tvSex=(TextView)findViewById(R.id.tv_sex);

    }

    public void initData(){

        UserInfo userInfo = LocalDataManager.getUserInfo();
        if (userInfo != null){
            edYear.setText("" + userInfo.year);
            edMonth.setText("" + userInfo.month);
            edDay.setText("" + userInfo.day);

            if (userInfo.gender == UserInfo.FEMALE){
                tvSex.setText(getString(R.string.set_para_user_info_person_sex_girl));
            }else {
                tvSex.setText(getString(R.string.set_para_user_info_person_sex_boy));
            }

            edWeight.setText("" + userInfo.weight);
            edHeight.setText("" + userInfo.height);

        }

    }

    public void addListener(){
        btnNan.setOnClickListener(this);
        btnNv.setOnClickListener(this);
        btnSetUserInfo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_nv:
                sex=UserInfo.FEMALE;
                tvSex.setText(getString(R.string.set_para_user_info_person_sex_girl));
                break;
            case R.id.btn_nan:
                sex=UserInfo.MALE;
                tvSex.setText(getString(R.string.set_para_user_info_person_sex_boy));
                break;
            case R.id.btn_set_use_info:

                int year=0;
                if(edYear.getText().length()!=0){
                    year=Integer.parseInt(edYear.getText().toString());
                }
                int month=0;
                if(edMonth.getText().length()!=0){
                    month=Integer.parseInt(edMonth.getText().toString());
                }
                int day=0;
                if(edDay.getText().length()!=0){
                    day=Integer.parseInt(edDay.getText().toString());
                }
                int weight=0;
                if(edWeight.getText().length()!=0){
                    weight=Integer.parseInt(edWeight.getText().toString());
                }
                int height=0;
                if(edHeight.getText().length()!=0){
                    height=Integer.parseInt(edHeight.getText().toString());
                }

                UserInfo userInfo = new UserInfo();
                userInfo.year = year;
                userInfo.month = month;
                userInfo.day = day;
                userInfo.weight = weight;
                userInfo.height = height;
                userInfo.gender = sex;

                if (isFromScanPage){
                    BLEManager.setUserInfoPending(userInfo);

                    Goal goal = new Goal();
                    goal.sport_step = 8000;

                    BLEManager.setGoalPending(goal);
                    startActivity(new Intent(SetUserInfoActivity.this, HomeActivity.class));
                    finish();
                }else {
                    showProgressDialog("set user info ...");
                    BLEManager.setUserInfo(userInfo);
                }
                break;

            default:
                break;
        }
    }

}
