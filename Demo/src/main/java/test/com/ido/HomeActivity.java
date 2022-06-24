package test.com.ido;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.ido.ble.BLEManager;
import com.ido.ble.callback.DeviceParaChangedCallBack;
import com.ido.ble.callback.RebootCallback;
import com.ido.ble.protocol.model.DeviceChangedPara;

import test.com.ido.app2device.AppControlDeviceActivity;
import test.com.ido.appsenddata.AppSendDataActivity;
import test.com.ido.connect.BaseAutoConnectActivity;
import test.com.ido.connect.ConnectManageActivity;
import test.com.ido.device2app.DeviceControlAppActivity;
import test.com.ido.dfu.MainUpgradeActivity;
import test.com.ido.exgdata.ExchangeDataMainActivity;
import test.com.ido.file.transfer.BTTransferActivity;
import test.com.ido.file.transfer.FileTransferActivity;
import test.com.ido.file.transfer.MultLangTrainActivity;
import test.com.ido.get.GetInfoActivity;
import test.com.ido.gps.GpsMainActivity;
import test.com.ido.localdata.MainLocalDataActivity;
import test.com.ido.logoutput.bluetooth.BluetoothLogoutManager;
import test.com.ido.notice.PhoneNoticeActivity;
import test.com.ido.set.MainSetActivity;
import test.com.ido.set.MusicFolderActivity;
import test.com.ido.set.WatchPlateActivity;
import test.com.ido.sync.SyncDataActivity;
import test.com.ido.unbind.UnbindActivity;

public class HomeActivity extends BaseAutoConnectActivity {


    private boolean isRebooting = false;
    private Handler mHander = new Handler();

    private DeviceParaChangedCallBack.ICallBack changeC = new DeviceParaChangedCallBack.ICallBack() {
        @Override
        public void onChanged(DeviceChangedPara deviceParaChange) {
            Toast.makeText(HomeActivity.this, deviceParaChange.toString(), Toast.LENGTH_LONG).show();
        }
    };


    @Override
    protected void initCompleted() {
        if (isRebooting){
            BLEManager.startSyncHealthData();
            isRebooting = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BLEManager.registerDeviceParaChangedCallBack(changeC);
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

    public void upgrade(View view){
        //进入升级模式前，先获取一下电量信息
        BLEManager.getBasicInfo();
        startActivity(new Intent(this, MainUpgradeActivity.class));
    }

    public void gps(View view){
        startActivity(new Intent(this, GpsMainActivity.class));
    }
    public void watchPlate(View view){
        startActivity(new Intent(this, WatchPlateActivity.class));
    }

    public void fileTransfer(View view){
        startActivity(new Intent(this, FileTransferActivity.class));
    }

    public void multLangTrain(View view){
        startActivity(new Intent(this, MultLangTrainActivity.class));
    }


    public void musicTrans(View view){
        startActivity(new Intent(this, MusicFolderActivity.class));
    }

   public void btUpgrade(View view){
       startActivity(new Intent(this, BTTransferActivity.class));

   }

}
