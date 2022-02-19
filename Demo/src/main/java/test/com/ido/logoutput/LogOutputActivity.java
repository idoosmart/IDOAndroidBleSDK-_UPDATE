package test.com.ido.logoutput;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import test.com.ido.R;

public class LogOutputActivity extends Activity {

    private TextView tvLog;
    private ScrollView scrollView;

    private String lastActivityClassName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_output);

        lastActivityClassName = getIntent().getStringExtra("className");
        tvLog = (TextView) findViewById(R.id.log_tv);
        scrollView = (ScrollView) findViewById(R.id.result_scroll);


        LogOutput.setLocalLogOutputListener(new LogOutput.LogListener() {
            @Override
            public void onLog(final String log) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (tvLog.getText().toString().getBytes().length > 100000){
                            tvLog.setText("");
                        }
                        tvLog.append(log);
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }
        });

        setFinishOnTouchOutside(false);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        lastActivityClassName = intent.getStringExtra("className");
    }

    public void clearLog(View v){
        tvLog.setText("");
    }

    public void hideLogWindow(View v){
        hide();
    }

    @Override
    public void onBackPressed() {
        hide();
    }

    private void hide(){
        Intent intent = new Intent();
        intent.setClassName(getPackageName(), lastActivityClassName);
        startActivity(intent);
    }
}
