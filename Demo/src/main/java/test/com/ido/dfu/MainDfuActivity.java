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
import test.com.ido.utils.GetFilePathFromUri;

public class MainDfuActivity extends Activity {

    private String macAddress, deviceId;
    private String filePath;
    private TextView tvError, tvFilePath,  tvRetryTimes;
    private ProgressBar progressBar;

    private long dfuStartTime = 0;
    private int mRetryTimes = 0;
    private boolean isNoric = false;// false :rtk   true： nordic

    private BleDFUState.IListener iListener = new BleDFUState.IListener() {
        @Override
        public void onPrepare() {
            setTitle("prepare...");
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
        }

        @Override
        public void onSuccess() {
            setTitle("success");
        }

        @Override
        public void onSuccessAndNeedToPromptUser() {

        }

        @Override
        public void onFailed(BleDFUState.FailReason failReason) {
            setTitle("failed");
            tvError.setText(failReason + "");
            BLEManager.removeDFUStateListener(iListener);
        }

        @Override
        public void onCanceled() {
            setTitle("canceled");
            tvError.setText("canceled");
        }

        @Override
        public void onRetry(int count) {
            mRetryTimes = count;
            tvRetryTimes.setText("重试次数:" + count);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        isNoric = getIntent().getBooleanExtra("nordic", false);
        setContentView(R.layout.activity_main_dfu);
        macAddress = getIntent().getStringExtra("mac_address");
        deviceId = getIntent().getStringExtra("device_id");
        filePath = DataUtils.getInstance().getDfuFilePath();
        tvError = findViewById(R.id.dfu_error_tv);
        tvFilePath = findViewById(R.id.dfu_file_path_tv);
        tvRetryTimes = findViewById(R.id.dfu_retry_times_tv);
        progressBar = findViewById(R.id.dfu_progressBar);
        if (TextUtils.isEmpty(filePath)){
            tvFilePath.setText("请先选择固件包(.zip)");
        }else {
            tvFilePath.setText(filePath);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BLEManager.removeDFUStateListener(iListener);
        BLEManager.cancelDFU();
    }

    public void startUpgrade(View view){
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
        BLEManager.addDFUStateListener(iListener);
        BleDFUConfig config =  new BleDFUConfig().setFilePath(filePath)
                .setMacAddress(address).setDeviceId(dId);
        if(isNoric){  //nordic  和 rtk 的区别
            config.setPlatform(BleDFUConfig.PLATFORM_NORDIC);
        }
        BLEManager.startDFU(config);
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

                String path = GetFilePathFromUri.getFileAbsolutePath(MainDfuActivity.this,uri);
                filePath = path;
                tvFilePath.setText(path);
                DataUtils.getInstance().saveDfuFilePath(path);
            }

        }
    }


}
