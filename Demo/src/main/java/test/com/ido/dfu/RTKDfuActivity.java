package test.com.ido.dfu;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ido.ble.BLEManager;
import com.ido.ble.dfu.BleDFUConfig;

import java.util.Timer;
import java.util.TimerTask;

import test.com.ido.R;
import test.com.ido.logoutput.LogOutput;
import test.com.ido.utils.DataUtils;

public class RTKDfuActivity extends Activity {

    private String macAddress, deviceId;
    private String filePath;
    private TextView tvError, tvFilePath, tvLog, tvLostTime, tvLiveLostTime, tvRetryTimes;
    private Button btnStartUpgrade;
    private ProgressBar progressBar, ingProgressBar;
    private ScrollView scrollView;
    private CheckBox cbxAutoTestMode;

    private Timer timer;

    private long dfuStartTime = 0;
    private int mRetryTimes = 0;

    private final String TEST_MAC = "F7:19:3E:91:D8:15";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rtk_dfu);

        macAddress = getIntent().getStringExtra("mac_address");
        deviceId = getIntent().getStringExtra("device_id");
        filePath = DataUtils.getInstance().getDfuFilePath();
        tvError = findViewById(R.id.dfu_error_tv);
        tvFilePath = findViewById(R.id.dfu_file_path_tv);
        tvLostTime = findViewById(R.id.dfu_lost_time_tv);
        tvRetryTimes = findViewById(R.id.dfu_retry_times_tv);
        tvLiveLostTime = findViewById(R.id.live_lost_time_tv);
        btnStartUpgrade = findViewById(R.id.btn_start_upgrade);
        tvLog = findViewById(R.id.dfu_log_tv);
        progressBar = findViewById(R.id.dfu_progressBar);
        ingProgressBar = findViewById(R.id.dfu_ing_progressBar);
        scrollView = findViewById(R.id.duf_result_scroll);
        cbxAutoTestMode = findViewById(R.id.dfu_is_auto_test_cbx);

        if (TextUtils.isEmpty(filePath)){
            tvFilePath.setText("请先选择固件包(.zip)");
        }else {
            tvFilePath.setText(filePath);
        }

        tvFilePath.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                scrollView.setVisibility(View.VISIBLE);
                return true;
            }
        });

        btnStartUpgrade.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });


        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (dfuStartTime != 0) {
                            tvLiveLostTime.setText("耗时:" + (System.currentTimeMillis() - dfuStartTime) /1000);
                        }
                    }
                });
            }
        }, 0, 1000);

        LogOutput.enableSelf();
        LogOutput.setLocalLogOutputListener(new LogOutput.LogListener() {
            @Override
            public void onLog(final String log) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        tvLog.append(log);
                        if (tvLog.getText().length() > 10000){
                            tvLog.setText(log);
                        }
                        btnStartUpgrade.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                            }
                        }, 50);

                    }
                });
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
        timer = null;

    }

    public void startUpgrade(View view){
        if (BLEManager.isConnected()){
            BLEManager.disConnect();
        }

        BLEManager.startDFU(new BleDFUConfig().
                setDeviceId("123").
                setMacAddress(TEST_MAC).
                setFilePath(tvFilePath.getText().toString()));

    }




    public void cancel(View view){
//        BLEManager.disConnect();

    }

    public void disConnect(View view){
    }

    public void selectFile(View view){
        openFileChooser();
    }



    private static final int SELECT_FILE_REQ = 1;
    private void openFileChooser() {
        final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/zip");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            // file browser has been found on the device
            startActivityForResult(intent, SELECT_FILE_REQ);
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {
            case SELECT_FILE_REQ: {
                // and read new one
                final Uri uri = data.getData();
                /*
                 * The URI returned from application may be in 'file' or 'content' schema. 'File' schema allows us to create a File object and read details from if
                 * directly. Data from 'Content' schema must be read by Content Provider. To do that we are using a Loader.
                 */
                if (uri.getScheme().equals("file") || uri.getScheme().equals("content")) {
                    // the direct path to the file has been returned
                    final String path = uri.getPath();
                    filePath = path;
                    tvFilePath.setText(path);
                    DataUtils.getInstance().saveDfuFilePath(path);
                }
            }

        }
    }
}
