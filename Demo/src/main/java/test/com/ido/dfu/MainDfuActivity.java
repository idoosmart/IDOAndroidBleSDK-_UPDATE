package test.com.ido.dfu;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
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
import android.widget.Toast;

import com.ido.ble.BLEManager;
import com.ido.ble.LocalDataManager;
import com.ido.ble.bluetooth.device.BLEDevice;
import com.ido.ble.dfu.BleDFUConfig;
import com.ido.ble.dfu.BleDFUState;


import java.util.Timer;
import java.util.TimerTask;

import test.com.ido.R;
import test.com.ido.connect.ScanDeviceActivity;
import test.com.ido.logoutput.LogOutput;
import test.com.ido.utils.DataUtils;

public class MainDfuActivity extends Activity {

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

    private void setLostTimeTv(boolean isFull){
        if (isFull) {
            tvLostTime.append("[" + (System.currentTimeMillis() - dfuStartTime) / 1000 + "," + mRetryTimes + "],");
        }else {
            tvLostTime.append("[" + (System.currentTimeMillis() - dfuStartTime) / 1000 + "," + mRetryTimes + ", N],");
        }
        dfuStartTime = 0;
        mRetryTimes = 0;
    }

    private BleDFUState.IListener iListener = new BleDFUState.IListener() {
        @Override
        public void onPrepare() {
            setTitle("prepare...");
            ingProgressBar.setVisibility(View.VISIBLE);
            tvError.setText("");
            progressBar.setProgress(0);
            dfuStartTime = System.currentTimeMillis();
            tvRetryTimes.setText("--");
        }

        @Override
        public void onDeviceInDFUMode() {

        }

        @Override
        public void onProgress(int progress) {
            progressBar.setProgress(progress);
            setTitle(progress + "%");

            if (isAutoTesting && progress >= 22){
                BluetoothAdapter.getDefaultAdapter().disable();
            }
        }

        @Override
        public void onSuccess() {
            setTitle("success");
            setLostTimeTv(true);
            BLEManager.removeDFUStateListener(iListener);

            if (BLEManager.isBind()) {
                if (!isAutoTesting && !cbxAutoTestMode.isChecked()) {
                    BLEManager.autoConnect();
                }
            }else {
                Toast.makeText(MainDfuActivity.this, "dfu success", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainDfuActivity.this, ScanDeviceActivity.class));
            }

            oneTimeFinished();

            ingProgressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onSuccessAndNeedToPromptUser() {
            setLostTimeTv(false);
            setTitle("success");
            BLEManager.removeDFUStateListener(iListener);
            tvError.setText("如果设备未重启，请尝试重启蓝牙，然后重启App，再次尝试升级");

            if (BLEManager.isBind()) {
                if (!isAutoTesting && !cbxAutoTestMode.isChecked()) {
                    BLEManager.autoConnect();
                }
            }else {
                Toast.makeText(MainDfuActivity.this, "dfu success", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainDfuActivity.this, ScanDeviceActivity.class));
            }

            oneTimeFinished();

            ingProgressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onFailed(BleDFUState.FailReason failReason) {
            setTitle("failed");
            tvError.setText(failReason + "");
            BLEManager.removeDFUStateListener(iListener);

            autoTestFailedTimes ++;
            oneTimeFinished();

            ingProgressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onCanceled() {
            setTitle("canceled");
            tvError.setText("canceled");
            BLEManager.removeDFUStateListener(iListener);

            oneTimeFinished();
            ingProgressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onRetry(int count) {
            mRetryTimes = count;
            tvRetryTimes.setText("重试次数:" + count);
        }
    };

    private int autoTestTimes = 0, autoTestFailedTimes = 0;
    private boolean isAutoTesting = false;
    private static final int MAX_TEST_FAILED_TIMES = 200;
    private void startAutoTest(){
        if (!isAutoTesting){
            autoTestTimes = 0;
            autoTestFailedTimes = 0;
            autoTestTimes ++;
            btnStartUpgrade.setText("自动测试中[" + autoTestTimes + "," + autoTestFailedTimes + "]...");
            isAutoTesting = true;

            upgrade();
        }else {
            autoTestTimes = 0;
            BLEManager.removeDFUStateListener(iListener);
            BLEManager.cancelDFU();
            btnStartUpgrade.setText("取消自动测试中...");
            btnStartUpgrade.postDelayed(new Runnable() {
                @Override
                public void run() {
                    btnStartUpgrade.setText("开始升级");
                    isAutoTesting = false;
                }
            }, 5000);

        }

    }
    private void oneTimeFinished(){
        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()){
//            tvError.setText("switch is closed, exit!");
//            timer.cancel();
//            return;
            BluetoothAdapter.getDefaultAdapter().enable();
        }
        if (isAutoTesting && autoTestFailedTimes < MAX_TEST_FAILED_TIMES){
            btnStartUpgrade.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tvLog.setText("");
                    autoTestTimes ++;
                    btnStartUpgrade.setText("自动测试中[" + autoTestTimes + "," + autoTestFailedTimes + "]...");
                    upgrade();
                }
            }, 15000);

        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main_dfu);
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
                startAutoTest();
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
        BLEManager.removeDFUStateListener(iListener);
        BLEManager.cancelDFU();
    }

    public void startUpgrade(View view){
        if (isAutoTesting){
            return;
        }
        upgrade();
    }


    private void upgrade(){

        String address = LocalDataManager.getBindMacAddress();
        if (TextUtils.isEmpty(address)){
            address = macAddress;
        }
        String dId = "";
        BLEDevice bleDevice = LocalDataManager.getLastConnectedDeviceInfo();
        if (bleDevice != null && bleDevice.mDeviceId != 0){
            dId = bleDevice.mDeviceId + "";
            address = bleDevice.mDeviceAddress;
        }else {
            dId = deviceId;
        }

        if (TextUtils.isEmpty(dId)){
            dId = "1234560";
        }

        BLEManager.addDFUStateListener(iListener);
        BLEManager.startDFU(new BleDFUConfig().setFilePath(filePath)
                .setMacAddress(address).setDeviceId(dId));
    }

    public void cancel(View view){
        BLEManager.cancelDFU();
    }

    public void disConnect(View view){
        BLEManager.disConnect();
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
