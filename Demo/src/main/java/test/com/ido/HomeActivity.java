package test.com.ido;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ido.ble.BLEManager;
import com.ido.ble.callback.DeviceParaChangedCallBack;
import com.ido.ble.protocol.model.DeviceChangedPara;
import com.ido.ble.protocol.model.SupportFunctionInfo;

import test.com.ido.CallBack.BaseGetDeviceInfoCallBack;
import test.com.ido.connect.BaseAutoConnectActivity;
import test.com.ido.connect.ConnectManageActivity;
import test.com.ido.device2app.DeviceControlAppActivity;
import test.com.ido.device2app.PhoneListenService;
import test.com.ido.dfu.MainUpgradeActivity;
import test.com.ido.dial.DialActivity;
import test.com.ido.ephemeris.EphemerisActivity;
import test.com.ido.exgdata.ExchangeDataMainActivity;
import test.com.ido.file.transfer.BTTransferActivity;
import test.com.ido.file.transfer.FileTransferActivity;
import test.com.ido.file.transfer.IconTransferActivity;
import test.com.ido.file.transfer.MultLangTrainActivity;
import test.com.ido.file.transfer.NotificationIconTransferActivity;
import test.com.ido.get.GetInfoActivity;
import test.com.ido.gps.GpsMainActivity;
import test.com.ido.logoutput.bluetooth.BluetoothLogoutManager;
import test.com.ido.music.MusicActivity;
import test.com.ido.notice.PhoneNoticeActivity;
import test.com.ido.runplan.RunPlanActivity;
import test.com.ido.set.MainSetActivity;
import test.com.ido.set.SetSportActivity;
import test.com.ido.set.WatchPlateActivity;
import test.com.ido.sync.SyncDataActivity;
import test.com.ido.unbind.UnbindActivity;
import test.com.ido.utils.PermissionUtil;
import test.com.ido.widgets.WidgetsActivity;
import test.com.ido.worldtime.WorldTimeActivity;

public class HomeActivity extends BaseAutoConnectActivity {
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private Button btSetSport;
    private Button btSetNotification;

    private boolean isRebooting = false;
    private Handler mHander = new Handler();

    private DeviceParaChangedCallBack.ICallBack changeC = new DeviceParaChangedCallBack.ICallBack() {
        @Override
        public void onChanged(DeviceChangedPara deviceParaChange) {
            Toast.makeText(HomeActivity.this, deviceParaChange.toString(), Toast.LENGTH_LONG).show();
        }
    };

    private BaseGetDeviceInfoCallBack infoCallBack = new BaseGetDeviceInfoCallBack() {
        @Override
        public void onGetFunctionTable(SupportFunctionInfo supportFunctionInfo) {
            super.onGetFunctionTable(supportFunctionInfo);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (supportFunctionInfo == null) return;
                    if (supportFunctionInfo.V3_support_set_v3_notify_add_app_name) {
                        btSetNotification.setVisibility(View.VISIBLE);
                    } else {
                        btSetNotification.setVisibility(View.GONE);
                    }
                    if (supportFunctionInfo.V3_set_100_sport_sort) {
                        btSetSport.setVisibility(View.VISIBLE);
                    } else {
                        btSetSport.setVisibility(View.GONE);
                    }
                }
            });
        }
    };

    @Override
    protected void initCompleted() {
        if (isRebooting) {
            BLEManager.startSyncHealthData();
            isRebooting = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BLEManager.registerDeviceParaChangedCallBack(changeC);
        btSetSport = findViewById(R.id.btSetSport);
        btSetNotification = findViewById(R.id.btSetNotification);
        BLEManager.registerGetDeviceInfoCallBack(infoCallBack);

        boolean b = PermissionUtil.checkSelfPermission(getBaseContext(), PermissionUtil.getPhonePermission());

        // 请求权限
        if (!b) {
            requestPermissions(new String[]{
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.ANSWER_PHONE_CALLS//9.0之后接听电话需要这个权限
            }, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            startService(new Intent(this, PhoneListenService.class));
            Log.d(HomeActivity.class.getName(), "onCreate: 已获得权限");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        BluetoothLogoutManager.getManager().stop();

        System.exit(0);
    }

    public void unbind(View v) {
        startActivity(new Intent(this, UnbindActivity.class));
    }

    public void connectManage(View v) {
        startActivity(new Intent(this, ConnectManageActivity.class));
    }

    public void getInfo(View v) {
        startActivity(new Intent(this, GetInfoActivity.class));
    }

    public void notice(View v) {
        startActivity(new Intent(this, PhoneNoticeActivity.class));
    }

    public void syncData(View v) {
        startActivity(new Intent(this, SyncDataActivity.class));
    }

    public void exchangeData(View v) {
        startActivity(new Intent(this, ExchangeDataMainActivity.class));
    }

    public void setPara(View v) {
        startActivity(new Intent(this, MainSetActivity.class));
    }

    public void upgrade(View view) {
        //进入升级模式前，先获取一下电量信息
        BLEManager.getBasicInfo();
        startActivity(new Intent(this, MainUpgradeActivity.class));
    }

    public void gps(View view) {
        startActivity(new Intent(this, GpsMainActivity.class));
    }

    public void watchPlate(View view) {
        startActivity(new Intent(this, WatchPlateActivity.class));
    }

    public void fileTransfer(View view) {
        startActivity(new Intent(this, FileTransferActivity.class));
    }

    public void multLangTrain(View view) {
        startActivity(new Intent(this, MultLangTrainActivity.class));
    }
  public void deviceControlApp(View view) {
      startActivity(new Intent(this, DeviceControlAppActivity.class));
    }


    public void musicTrans(View view) {
        startActivity(new Intent(this, MusicActivity.class));
    }

    public void btUpgrade(View view) {
        startActivity(new Intent(this, BTTransferActivity.class));

    }

    public void btDial(View view) {
        startActivity(new Intent(this, DialActivity.class));
    }

    public void btIconTrans(View view) {
        startActivity(new Intent(this, IconTransferActivity.class));
    }

    public void btMiniWidgets(View view) {
        startActivity(new Intent(this, WidgetsActivity.class));
    }

    public void btWorldTime(View view) {
        startActivity(new Intent(this, WorldTimeActivity.class));
    }

    public void setSport(View view) {
        startActivity(new Intent(this, SetSportActivity.class));
    }

    public void setNotification(View view) {
        startActivity(new Intent(this, NotificationIconTransferActivity.class));
    }

    public void runPlan(View view) {
        startActivity(new Intent(this, RunPlanActivity.class));
    }

    public void Ephemeris(View view) {
        startActivity(new Intent(this, EphemerisActivity.class));
    }
}
