package test.com.ido.interanltest;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import com.ido.ble.BLEManager;
import com.ido.ble.event.stat.one.IEventStatCallBack;

import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;

public class UserFeedbackActivity extends BaseAutoConnectActivity {

    private String feedbackType;
    private EditText extraInfoEt;
    private Spinner typeSpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feedback);

        extraInfoEt = (EditText) findViewById(R.id.feedback_extra_et);
        typeSpinner = (Spinner) findViewById(R.id.feedback_type_spinner);



        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] feedbackTypes = getResources().getStringArray(R.array.feedback_type);
                feedbackType = feedbackTypes[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void submit(View view){
        showProgressDialog("提交中");
        BLEManager.userFeedback(feedbackType, extraInfoEt.getText().toString(), new IEventStatCallBack() {
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(UserFeedbackActivity.this, "反馈成功", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onFailed() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(UserFeedbackActivity.this, "反馈失败，请重试", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
