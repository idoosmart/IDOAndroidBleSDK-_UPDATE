package test.com.ido.gps;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.ido.ble.BLEManager;
import com.ido.ble.LocalDataManager;
import com.ido.ble.callback.GetDeviceInfoCallBack;
import com.ido.ble.file.transfer.FileTransferConfig;
import com.ido.ble.file.transfer.IFileTransferListener;
import com.ido.ble.gps.callback.GpsCallBack;
import com.ido.ble.gps.database.HealthGps;
import com.ido.ble.gps.database.HealthGpsItem;
import com.ido.ble.gps.model.GPSInfo;
import com.ido.ble.gps.model.GpsHotStartParam;
import com.ido.ble.gps.model.GpsStatus;
import com.ido.ble.protocol.model.SupportFunctionInfo;

import java.util.List;

import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;
import test.com.ido.utils.FileUtil;
import test.com.ido.utils.SPUtils;

public class GpsMainActivity extends BaseAutoConnectActivity {

    private TextView tvSyncGpsDataStatus;
    /**
     * 一天的毫秒值
     */
    private static final int TIME_MILLIONS_OF_DAY = 1000 * 60 * 60 * 24;

    /**
     * 4小时的毫秒值
     */
    private static final int TIME_MILLIONS_OF_4_HOUR = 1000 * 60 * 60 * 4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps_main);
        tvSyncGpsDataStatus = findViewById(R.id.sync_gps_status_tv);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void tranAgpsFileonline(View view){
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
        }
        else if (isSupportOnlineUpgrade() && Math.abs(getLastAgpsOnlineUpgradeTime() - timeMillis) >= TIME_MILLIONS_OF_4_HOUR) {
            String AGPS_ONLINE_FILE_URL = "http://online-live1.services.u-blox.com/GetOnlineData.ashx?token=vB6zs0P4F0ayAYBMCzx4rw&gnss=gps,qzss,glo,bds,gal&datatype=eph&format=mga";//online file url
            //download online file
            //file name must "online.ubx"
            // TODO: 2022/6/21 The logic of downloading files is supplemented by yourself
            //transferAgpsFile2Device(String path) after  download complete
        }
    }

    /**
     *
     *transfile
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
        return functionInfo != null && functionInfo.ex_gps && functionInfo.agps_online;    }


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
}

