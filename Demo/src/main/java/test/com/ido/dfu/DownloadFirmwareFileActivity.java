package test.com.ido.dfu;

import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ido.ble.BLEManager;
import com.ido.ble.LocalDataManager;
import com.ido.ble.bluetooth.device.BLEDevice;
import com.ido.ble.dfu.nodic.firmware.CheckNewVersionPara;
import com.ido.ble.dfu.nodic.firmware.CheckNewVersionResponse;
import com.ido.ble.dfu.nodic.firmware.FirmwareListener;
import com.ido.ble.protocol.model.BasicInfo;

import java.io.File;
import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;

public class DownloadFirmwareFileActivity extends BaseAutoConnectActivity {

    private EditText etAge, etSex, etFirmwareId, etFirmVersion, etMacAddress, etEnvironment;
    private TextView tvLatestInfo;
    private String mDownloadUrl = "";
    private ProgressBar pgbDownloadProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_firmware_file);

        initView();
        loadData();
    }

    private void loadData() {
        etAge.setText("25");
        etSex.setText("1");

        BasicInfo basicInfo = LocalDataManager.getBasicInfo();
        if (basicInfo != null){
            etFirmwareId.setText(basicInfo.deivceId + "");
            etFirmVersion.setText(basicInfo.firmwareVersion + "");
        }

        BLEDevice bleDevice = LocalDataManager.getLastConnectedDeviceInfo();
        if (bleDevice != null){
            etMacAddress.setText(bleDevice.mDeviceAddress);
        }

        etEnvironment.setText("1");
    }

    private void initView() {
        etAge = findViewById(R.id.et_check_dfu_age);
        etSex = findViewById(R.id.et_check_dfu_sex);
        etFirmwareId = findViewById(R.id.et_check_dfu_firmware_id);
        etFirmVersion = findViewById(R.id.et_check_dfu_firmware_version);
        etMacAddress = findViewById(R.id.et_check_dfu_mac_address);
        etEnvironment = findViewById(R.id.et_check_dfu_environment);

        tvLatestInfo = findViewById(R.id.tv_check_dfu_latest_info);
        pgbDownloadProgress = findViewById(R.id.pgb_download_progress);
    }

    public void getLatestVersionInfo(View view){
        showProgressDialog("get ...");
        mDownloadUrl = "";
        new Thread(new Runnable() {
            @Override
            public void run() {
                get();
            }
        }).start();
    }

    public void downloadFirmwareFile(View view){
        if (TextUtils.isEmpty(mDownloadUrl)){
            Toast.makeText(this, "url is null", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder builder = new StringBuilder();
        final String dirPath = builder.append(Environment.getExternalStorageDirectory().getAbsolutePath()).
                append(File.separator).
                append("IDO_SDK_DEMO").
                append(File.separator).
                append("dfu").toString();


        final String fileName = "firmware.zip";

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                BLEManager.downloadDeviceNewFirmwarePackage(mDownloadUrl, dirPath, fileNameList, new FirmwareListener.IDownloadListener() {
//                    @Override
//                    public void onStart() {
//
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                pgbDownloadProgress.setProgress(0);
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onProgress(final int progress) {
//
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                pgbDownloadProgress.setProgress(progress);
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onSuccess() {
//
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(DownloadFirmwareFileActivity.this, "download success", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onFailed() {
//
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(DownloadFirmwareFileActivity.this, "download failed", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//                });
//            }
//        }).start();


    }



    private void get(){

        CheckNewVersionPara para = new CheckNewVersionPara();
        para.deviceId = Integer.parseInt(etFirmwareId.getText().toString());
        para.firmwareVersion = Integer.parseInt(etFirmVersion.getText().toString());

        CheckNewVersionPara.FilterPara filterPara = new CheckNewVersionPara.FilterPara();
        filterPara.age = Integer.parseInt(etAge.getText().toString());
        filterPara.gender = Integer.parseInt(etSex.getText().toString());
        filterPara.macAddress = etMacAddress.getText().toString();
        filterPara.environment = Integer.parseInt(etEnvironment.getText().toString());
        para.filterPara = filterPara;

//        BLEManager.checkDeviceNewVersion(para, new FirmwareListener.ICheckNewVersionListener() {
//            @Override
//            public void onNoNewVersion() {
//                android.util.Log.e("check", "onNoNewVersion");
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        closeProgressDialog();
//                        tvLatestInfo.setText("no new version");
//                    }
//                });
//            }
//
//            @Override
//            public void onHasNewVersion(final CheckNewVersionResponse.NewVersionInfo newVersionInfo) {
//                android.util.Log.e("check", newVersionInfo.toString());
//                mDownloadUrl = newVersionInfo.url;
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        closeProgressDialog();
//                        tvLatestInfo.setText(newVersionInfo.toString());
//                    }
//                });
//            }
//
//            @Override
//            public void onCheckFailed() {
//                android.util.Log.e("check", "onCheckFailed");
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        closeProgressDialog();
//                        tvLatestInfo.setText("get failed");
//                    }
//                });
//            }
//        });
    }

}
