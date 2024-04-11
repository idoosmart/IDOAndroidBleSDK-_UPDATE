package test.com.ido.gps;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.ido.ble.BLEManager;
import com.ido.ble.LocalDataManager;
import com.ido.ble.file.transfer.FileTransferConfig;
import com.ido.ble.file.transfer.IFileTransferListener;
import com.ido.ble.gps.callback.GpsCallBack;
import com.ido.ble.gps.gps.GpsFileTransferConfig;
import com.ido.ble.gps.gps.GpsFileTransferListener;
import com.ido.ble.gps.model.ConnParamReply;
import com.ido.ble.gps.model.ControlGpsReply;
import com.ido.ble.gps.model.GPSInfo;
import com.ido.ble.gps.model.GpsHotStartParam;
import com.ido.ble.gps.model.GpsStatus;
import com.ido.ble.protocol.model.SupportFunctionInfo;

import java.io.File;

import test.com.ido.APP;
import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;
import test.com.ido.utils.DataUtils;
import test.com.ido.utils.FileUtil;
import test.com.ido.utils.GetFilePathFromUri;
import test.com.ido.utils.GpsUtils;
import test.com.ido.utils.SPUtils;

public class GpsMainActivity extends BaseAutoConnectActivity implements GpsCallBack.IGetGpsInfoCallBack, EpoUpgradeListener {

    private TextView tvSyncGpsDataStatus;

    private EditText etGPSPath;
    private Button btGPSSelect, btUpgradeGPS, btUpgradeEPO;
    private TextView tvGPSProgress, tvGPSVersion, tvLastEPOUpgradeTime, tvEPOProgress, tvAGPSProgress;
    private LinearLayout llGPS, llEPO;
    private Switch swEPOMode;
    private EditText hotStartPara_ETtcxoOffset, hotStartPara_ETlongitude, hotStartPara_ETlatitude, hotStartPara_ETaltitude;

    private String erroMsg = "";

    private Boolean isDownloadSuccess = false;

    /**
     * 一天的毫秒值
     */
    private static final int TIME_MILLIONS_OF_DAY = 1000 * 60 * 60 * 24;

    /**
     * 4小时的毫秒值
     */
    private static final int TIME_MILLIONS_OF_4_HOUR = 1000 * 60 * 60 * 4;

    private static final int SELECT_GPS_FILE_REQ = 1;
    private File file;
    private boolean isErro = true;

    private void openFileChooser(int requestCode) {
        final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("application/bin");
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        if (intent.resolveActivity(getPackageManager()) != null) {
            // file browser has been found on the device
            startActivityForResult(intent, requestCode);
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps_main);
        tvSyncGpsDataStatus = findViewById(R.id.sync_gps_status_tv);
        btUpgradeEPO = findViewById(R.id.btUpgradeEPO);
        llGPS = findViewById(R.id.llGPS);
        etGPSPath = findViewById(R.id.etGPSPath);
        llEPO = findViewById(R.id.llEPO);
        swEPOMode = findViewById(R.id.swEPOMode);
        tvLastEPOUpgradeTime = findViewById(R.id.tvLastEPOUpgradeTime);
        tvEPOProgress = findViewById(R.id.tvEPOProgress);
        tvGPSVersion = findViewById(R.id.tvGPSVersion);
        btGPSSelect = findViewById(R.id.btGPSSelect);
        btUpgradeGPS = findViewById(R.id.btUpgradeGPS);
        tvAGPSProgress = findViewById(R.id.tvAGPSProgress);
        tvGPSProgress = findViewById(R.id.tvGPSProgress);

        hotStartPara_ETtcxoOffset = (EditText) findViewById(R.id.tcxo_offset_et);
        hotStartPara_ETlongitude = (EditText) findViewById(R.id.longitude_et);
        hotStartPara_ETlatitude = (EditText) findViewById(R.id.latitude_et);
        hotStartPara_ETaltitude = (EditText) findViewById(R.id.altitude_et);

        etGPSPath.setText(DataUtils.getInstance().getGPSPath());

        BLEManager.registerDeviceReplySetGpsCallBack(deviceReplySetGpsCallBack);
        BLEManager.registerTranAgpsFileCallBack(tranAgpsFileCallBack);
        BLEManager.registerGetGpsInfoCallBack(this);
        if (isSupportGPSUpgrade()) {
            llGPS.setVisibility(View.VISIBLE);
            if (BLEManager.isConnected()) {
                BLEManager.getGpsInfo();
            }
        }

        if (isSupportEPO()) {
            llEPO.setVisibility(View.VISIBLE);
            boolean isAutoUpgradeEPO = DataUtils.getInstance().isAutoUpgradeEPO();
            swEPOMode.setChecked(isAutoUpgradeEPO);
            btUpgradeEPO.setEnabled(!isAutoUpgradeEPO);
            swEPOMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
                btUpgradeEPO.setEnabled(!isChecked);
                DataUtils.getInstance().saveEPOUpgradeMode(isChecked);
            });
            EpoUpgradeHelper.getInstance().setEpoUpgradeListener(this);
            if (isAutoUpgradeEPO) {
                EpoUpgradeHelper.getInstance().startUpgradeEpo();
            }
        }

        String path = APP.getAppContext().getExternalCacheDir().getAbsolutePath() + (File.separator) + "eph/";
        file = new File(path);
        if (file.exists()) {
            isDownloadSuccess = true;
        } else {
            Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BLEManager.unregisterDeviceReplySetGpsCallBack(deviceReplySetGpsCallBack);
        BLEManager.unregisterTranAgpsFileCallBack(tranAgpsFileCallBack);
        EpoUpgradeHelper.getInstance().unregisterListener();
    }

    public void tranAgpsFileonline(View view) {
        BLEManager.registerGetGpsInfoCallBack(new GpsCallBack.IGetGpsInfoCallBack() {
            @Override
            public void onGetGpsInfo(GPSInfo gpsInfo) {

            }

            @Override
            public void onGetHotStartGpsPara(GpsHotStartParam param) {

            }

            @Override
            public void onGetGpsStatus(GpsStatus gpsStatus) {
                //GPS非空闲状态，不处理
                if (GpsStatus.STATUS_IDLE != gpsStatus.gps_run_status) {
                    return;
                }
                //下载Agps文件
                downloadAgpsFile();//按协议实现
            }
        });
        BLEManager.getGpsStatus();
    }


    /**
     * 下载Agps文件处理
     */
    private void downloadAgpsFile() {
        long timeMillis = System.currentTimeMillis();

        if (isSupportOfflineUpgrade() && Math.abs(getLastAgpsOfflineUpgradeTime() - timeMillis) >= TIME_MILLIONS_OF_DAY) {
            String AGPS_OFFLINE_FILE_URL = "http://offline-live1.services.u-blox.com/GetOfflineData.ashx?token=vB6zs0P4F0ayAYBMCzx4rw&gnss=gps,glo&period=1&resolution=1";//offline file url
            //download offline file
            //file name muse  "agps.ubx"
            // TODO: 2022/6/21 The logic of downloading files is supplemented by yourself
            //transferAgpsFile2Device(String path) after  download complete
        } else if (isSupportOnlineUpgrade() && Math.abs(getLastAgpsOnlineUpgradeTime() - timeMillis) >= TIME_MILLIONS_OF_4_HOUR) {
            String AGPS_ONLINE_FILE_URL = "http://online-live1.services.u-blox.com/GetOnlineData.ashx?token=vB6zs0P4F0ayAYBMCzx4rw&gnss=gps,qzss,glo,bds,gal&datatype=eph&format=mga";//online file url
            //download online file
            //file name must "online.ubx"
            // TODO: 2022/6/21 The logic of downloading files is supplemented by yourself
            //transferAgpsFile2Device(String path) after  download complete
        }
    }

    /**
     * transfile
     *
     * @param path filepath
     */
    private void transferAgpsFile2Device(String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        if (!BLEManager.isConnected()) {
            return;
        }
        FileTransferConfig config = FileTransferConfig.getDefaultUbloxAGpsFileConfig(path, new IFileTransferListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onProgress(int progress) {

            }

            @Override
            public void onSuccess() {
                // success
            }

            @Override
            public void onFailed(String errorMsg) {

            }
        });
        config.firmwareSpecName = FileUtil.getFileNameFromPath(path);
        config.maxRetryTimes = 0;
        config.zipType = FileTransferConfig.ZIP_TYPE_NULL;
        BLEManager.startTranCommonFile(config);
    }

    /**
     * issupport  offline
     *
     * @return
     */
    private boolean isSupportOfflineUpgrade() {
        SupportFunctionInfo functionInfo = LocalDataManager.getSupportFunctionInfo();
        return functionInfo != null && functionInfo.ex_gps && functionInfo.agps_offline;
    }

    /**
     * issupport  online
     *
     * @return
     */
    private boolean isSupportOnlineUpgrade() {
        SupportFunctionInfo functionInfo = LocalDataManager.getSupportFunctionInfo();
        return functionInfo != null && functionInfo.ex_gps && functionInfo.agps_online;
    }


    /**
     * 保存Agps online升级的时间戳
     *
     * @param time
     */
    public static void saveAgpsOnlineUpgradeTime(long time) {
        SPUtils.put(AGPS_ONLINE_UPGRADE_TIME, time);
    }

    /**
     * 获取最近一次Agps online升级的时间戳
     *
     * @return
     */
    public static long getLastAgpsOnlineUpgradeTime() {
        return (long) SPUtils.get(AGPS_ONLINE_UPGRADE_TIME, 0L);
    }

    /**
     * 保存Agps offline升级的时间戳
     *
     * @param time
     */
    public static void saveAgpsOfflineUpgradeTime(long time) {
        SPUtils.put(AGPS_OFFLINE_UPGRADE_TIME, time);
    }

    /**
     * 获取最近一次Agps offline升级的时间戳
     *
     * @return
     */
    public static long getLastAgpsOfflineUpgradeTime() {
        return (long) SPUtils.get(AGPS_OFFLINE_UPGRADE_TIME, 0L);
    }

    /**
     * Agps online升级的时间戳
     */
    private static final String AGPS_ONLINE_UPGRADE_TIME = "agps_online_upgrade_time";

    /**
     * Agps offline升级的时间戳
     */
    private static final String AGPS_OFFLINE_UPGRADE_TIME = "agps_offline_upgrade_time";


    private boolean isSupportEPO() {
        SupportFunctionInfo functionInfo = LocalDataManager.getSupportFunctionInfo();
//        return functionInfo != null && functionInfo.Airoha_gps_chip;
        return true;
    }

    private boolean isSupportGPSUpgrade() {
        SupportFunctionInfo functionInfo = LocalDataManager.getSupportFunctionInfo();
        return functionInfo != null && functionInfo.support_update_gps;
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {
            case SELECT_GPS_FILE_REQ: {
                Uri uri = data.getData();
                String path = GetFilePathFromUri.getFileAbsolutePath(this, uri);
                if (!TextUtils.isEmpty(path)) {
                    etGPSPath.setText(path);
                    DataUtils.getInstance().saveGPSPath(path);
                }
            }
        }
    }

    public void btUpgradeGPS(View view) {
        String filepath = etGPSPath.getText().toString().trim();
        if (!TextUtils.isEmpty(filepath)) {
            GpsFileTransferConfig config = new GpsFileTransferConfig();
            config.filePath = filepath;
            config.stateListener = new GpsFileTransferListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onProgress(int i) {
                    tvGPSProgress.setText("" + i);
                }

                @Override
                public void onSuccess() {
                    tvGPSProgress.setText("success");
                    btUpgradeGPS.setEnabled(true);
                }

                @Override
                public void onFailed(int i, String s) {
                    tvGPSProgress.setText("failed: " + i);
                    btUpgradeGPS.setEnabled(true);
                }
            };
            tvGPSProgress.setText("starting...");
            btUpgradeGPS.setEnabled(false);
            BLEManager.startTranGpsFile(config);
        }
    }

    public void btGPSSelect(View view) {
        openFileChooser(SELECT_GPS_FILE_REQ);
    }

    @Override
    public void onGetGpsInfo(GPSInfo gpsInfo) {
        tvGPSVersion.setText("GPS Version: " + GpsUtils.getGpsVersion(gpsInfo.fwVersion));
    }

    @Override
    public void onGetHotStartGpsPara(GpsHotStartParam gpsHotStartParam) {

    }

    @Override
    public void onGetGpsStatus(GpsStatus gpsStatus) {

    }

    public void btUpgradeEPO(View view) {
        isDownloadSuccess = true;
        EpoUpgradeHelper.getInstance().startUpgradeEpo();
    }

    @Override
    public void onDownloadStart() {
        tvEPOProgress.setText("starting...");
    }

    @Override
    public void onDownloadProgress(int index, int totalCount, int progress) {
        tvEPOProgress.setText("(" + index + "/" + totalCount + ")download progress = " + progress + "");
    }

    @Override
    public void onDownloadSuccess() {
        tvEPOProgress.setText("download success!");
        isDownloadSuccess = true;
    }

    @Override
    public void onPackaging() {
        tvEPOProgress.setText("packaging...");
    }

    @Override
    public void onTransferStart() {
        tvEPOProgress.setText("transfer starting...");
    }

    @Override
    public void onTransferProgress(int progress) {
        tvEPOProgress.setText("transfer progress = " + progress);
    }

    @Override
    public void onTransferSuccess() {
        tvEPOProgress.setText("Transfer success, Wait for the device end upgrade to complete");
    }

    @Override
    public void onFailed(@NonNull String errorMsg, int code) {
        tvEPOProgress.setText("failed: " + errorMsg);
        isErro = false;
        erroMsg = errorMsg;
        if (code == 2) {
            isDownloadSuccess = false;
        }

    }

    @Override
    public void onSuccess() {
        tvEPOProgress.setText("upgrade success!");
    }

    public void btTransferIcoeEpo(View view) {

        if (isDownloadSuccess && file.exists()) {
            if (!isErro) {
                Toast.makeText(this, erroMsg, Toast.LENGTH_SHORT).show();
            }
            EpoUpgradeHelper.getInstance().startTransferIcoeEpoFile();
        } else {
            Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
        }

    }

    public void btTransferEPO(View view) {
        if (isDownloadSuccess && file.exists()) {
            if (!isErro) {
                Toast.makeText(this, erroMsg, Toast.LENGTH_SHORT).show();
            }
            EpoUpgradeHelper.getInstance().startTransferEpoFile();
        } else {
            Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
        }
    }

    public void setGpsHotStartPara(View view) {
        GpsHotStartParam hotStartParam = new GpsHotStartParam();
        hotStartParam.setTcxo_offset(Integer.parseInt(hotStartPara_ETtcxoOffset.getText().toString()));
        hotStartParam.setLongitude(Double.parseDouble(hotStartPara_ETlongitude.getText().toString()));
        hotStartParam.setLatitude(Double.parseDouble(hotStartPara_ETlatitude.getText().toString()));
        hotStartParam.setAltitude(Double.parseDouble(hotStartPara_ETaltitude.getText().toString()));
        BLEManager.setGpsHotPara(hotStartParam);
    }

    public void getGpsHotStartPara(View view) {
        BLEManager.getGpsHotPara();
    }

    private GpsCallBack.IDeviceReplySetGpsCallBack deviceReplySetGpsCallBack = new GpsCallBack.IDeviceReplySetGpsCallBack() {
        @Override
        public void onSetHotStartGpsPara(boolean isSuccess) {
            String msg = isSuccess ? "设置热启动参数成功" : "设置热启动参数失败";
            Toast.makeText(GpsMainActivity.this, msg, Toast.LENGTH_SHORT).show();
            tvAGPSProgress.setText(msg);
        }

        @Override
        public void onSetConnParam(ConnParamReply connParamReply) {
            tvAGPSProgress.append("\n返回:\n");
            if (connParamReply != null) {
                tvAGPSProgress.append(connParamReply.toString());
            } else {
                tvAGPSProgress.append("设置失败");
            }
        }

        @Override
        public void onControlGps(ControlGpsReply controlGpsReply) {
            tvAGPSProgress.append("\n返回:\n");
            if (controlGpsReply != null) {
                tvAGPSProgress.append(controlGpsReply.toString());
            } else {
                tvAGPSProgress.append("设置失败");
            }
        }

        @Override
        public void onSetConfigGps(boolean isSuccess) {
            if (isSuccess) {
                Toast.makeText(GpsMainActivity.this, "设置GPS参数成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(GpsMainActivity.this, "设置GPS参数失败", Toast.LENGTH_SHORT).show();
            }
        }
    };


    private GpsCallBack.ITranAgpsFileCallBack tranAgpsFileCallBack = new GpsCallBack.ITranAgpsFileCallBack() {
        @Override
        public void onProgress(int progress) {
            tvAGPSProgress.setText("transfer progress =" + progress);
        }

        @Override
        public void onFinish() {
            tvAGPSProgress.setText("onFinish");
        }

        @Override
        public void onFailed(int error) {
            tvAGPSProgress.setText("error = " + error);
        }

        @Override
        public void onFailed(int i, Object o) {
        }
    };

}

