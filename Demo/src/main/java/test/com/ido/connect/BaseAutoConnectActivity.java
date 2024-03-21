package test.com.ido.connect;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothHidDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.ido.ble.BLEManager;
import com.ido.ble.bluetooth.connect.ConnectFailedReason;
import com.ido.ble.bluetooth.device.BLEDevice;
import com.ido.ble.callback.ConnectCallBack;

import java.io.File;
import java.lang.reflect.Method;
import java.util.concurrent.Future;

import test.com.ido.R;
import test.com.ido.dfu.MainDfuActivity;
import test.com.ido.logoutput.LogOutput;
import test.com.ido.set.data.ExecutorDispatcher;
import test.com.ido.set.data.Func;
import test.com.ido.set.data.listener.Callback;
import test.com.ido.utils.FileUtilLib;
import test.com.ido.utils.HidConncetUtil;

/**
 * @author: zhouzj
 * @date: 2017/10/23 11:38
 */

public class BaseAutoConnectActivity extends Activity {

    protected ProgressDialog progressDialog;
    protected void showProgressDialog(String title){
        if (progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle(title);
        progressDialog.show();
    }

    protected void closeProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
    private ConnectCallBack.ICallBack connectCallBack = new ConnectCallBack.ICallBack() {

        @Override
        public void onConnectStart(String macAddress) {
            setTitle(getString(R.string.connect_state_start));
            onNotConnect();
        }

        @Override
        public void onConnecting(String macAddress) {
            setTitle(getString(R.string.connect_state_ing));
            onNotConnect();
        }

        @Override
        public void onRetry(int count, String macAddress) {

        }

        @Override
        public void onConnectSuccess(String macAddress) {
            setTitle(getString(R.string.connect_state_ok));
            onConnected();
        }

        @Override
        public void onConnectFailed(ConnectFailedReason reason,String macAddress) {
            setTitle(getString(R.string.connect_state_failed) + reason);
            onNotConnect();
        }

        @Override
        public void onConnectBreak(String macAddress) {
            setTitle(getString(R.string.connect_state_break));
            onNotConnect();
        }

        @Override
        public void onInDfuMode(BLEDevice bleDevice) {
            setTitle(getString(R.string.connect_state_dfu));
//            handleDfuState(bleDevice);
        }

        @Override
        public void onDeviceInNotBindStatus(String macAddress) {

        }

        @Override
        public void onInitCompleted(String macAddress) {

            Toast.makeText(BaseAutoConnectActivity.this, "device init ok", Toast.LENGTH_SHORT).show();
            BLEManager.getFunctionTables();
            initCompleted();
        }

    };

    private AlertDialog dfuAlertDialog;
    protected void handleDfuState(BLEDevice bleDevice){
        if (dfuAlertDialog != null && dfuAlertDialog.isShowing()){
            return;
        }
        dfuAlertDialog = new AlertDialog.Builder(this).setTitle("提示")
                .setMessage(bleDevice.mDeviceName +"处于升级模式，是否去升级？").setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(BaseAutoConnectActivity.this, MainDfuActivity.class));
                    }
                }).create();
        dfuAlertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.base_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.log_out_by_self:
                LogOutput.enableSelf(this.getApplicationContext(), getIntent().getComponent());
                break;
            case R.id.log_out_by_service:
                LogOutput.enableService(this.getApplication());
                break;
            case R.id.log_out_by_bluetooth:
                LogOutput.enableBluetooth(this.getApplicationContext());
                break;
            case R.id.install_tool:
                LogOutput.installTool(this);
                break;
            case R.id.share_tool:
                LogOutput.shareTool(this);
                break;
            case R.id.share_log:
                LogOutput.shareLog(this);
                break;
            case R.id.export_db:
                copyDbAndShare();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void connectDeviceHID(String mMacString) {
                HidConncetUtil hidConncetUtil = new HidConncetUtil(this);
//                hidConncetUtil.connect(mMacString);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_home);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        BLEManager.registerConnectCallBack(connectCallBack);

        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (BLEManager.isConnected()) {
                    setTitle(getString(R.string.connect_state_ok));
                } else {
                    setTitle(getString(R.string.connect_state_start));
                    BLEManager.autoConnect();
                }
            }
        }, 300);


//        registerBlueReceiver();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        BLEManager.unregisterConnectCallBack(connectCallBack);
    }

    protected void onConnected() {
//        HIDConnectManager.getManager().connect(LocalDataManager.getCurrentDeviceInfo().mDeviceAddress);
    }

    protected void onNotConnect() {

    }

    protected void initCompleted(){

    }


    private Handler mHandler = null;
    public void init() {
        mHandler = new Handler(Looper.getMainLooper());
    }
    /**
     * @author tianwei
     * @date 2021/7/29
     * @time 11:48
     * 用途:简单封装异步操作，需手动取消
     */
    protected <T> Future addSubscriber(Func<T> func, Callback<T> callback) {
        return ExecutorDispatcher.getInstance().dispatch(() -> {
            try {
                T data = func.call();
                if (mHandler != null) {
                    mHandler.post(() -> callback.onSuccess(data));
                }
            } catch (Exception e) {
                if (mHandler != null) {
                    mHandler.post(() -> callback.onFailed(""));
                }
            }
        });
    }
    private void copyDbAndShare() {
        String sdkDbName = "idoLib.db";
        String sdkDbTempName = "idoLib_temp.db";
        final File file = getDatabasePath(sdkDbName);
        File outFile = new File(Environment.getExternalStorageDirectory(), sdkDbTempName);
        FileUtilLib.fileCopy(file, outFile);

        Toast.makeText(this, "(idoLib_temp.db)已导出到SD卡根目录", Toast.LENGTH_LONG).show();
//        shareFile(this, outFile);
    }

    // 調用系統方法分享文件
    public void shareFile(Context context, File file) {
        if (null != file && file.exists()) {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            share.setType(getMimeType(file.getAbsolutePath()));//此处可发送多种文件
            share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(Intent.createChooser(share, file.getName()));
        } else {
            Toast.makeText(this, "分享文件不存在", Toast.LENGTH_LONG).show();
        }
    }

    // 根据文件后缀名获得对应的MIME类型。
    private String getMimeType(String filePath) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        String mime = "*/*";
        if (filePath != null) {
            try {
                mmr.setDataSource(filePath);
                mime = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
            } catch (IllegalStateException e) {
                return mime;
            } catch (IllegalArgumentException e) {
                return mime;
            } catch (RuntimeException e) {
                return mime;
            }
        }
        return mime;
    }


    private BluetoothA2dp mBluetoothA2dp;
    private BluetoothHeadset mBluetoothHeadset;
    private BluetoothHidDevice mBluetoothHidDevice;
    public void registerBlueReceiver() {
        BluetoothManager mBluetoothManager = (BluetoothManager)
                getSystemService(Context.BLUETOOTH_SERVICE);
        if (mBluetoothManager == null) {
            return;
        }
        BluetoothAdapter mBluetoothAdapter = mBluetoothManager.getAdapter();
        mBluetoothAdapter.getProfileProxy(this, mProfileServiceListener, BluetoothProfile.A2DP);
        mBluetoothAdapter.getProfileProxy(this, mProfileServiceListener, BluetoothProfile.HEADSET);
        mBluetoothAdapter.getProfileProxy(this, mProfileServiceListener, BluetoothProfile.HID_DEVICE);

    }


    private BluetoothProfile.ServiceListener mProfileServiceListener = new BluetoothProfile.ServiceListener() {

        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            if (profile == BluetoothProfile.A2DP) {//播放音乐
                mBluetoothA2dp = (BluetoothA2dp) proxy;
            } else if (profile == BluetoothProfile.HEADSET) {//打电话
                mBluetoothHeadset = (BluetoothHeadset) proxy;
            }
        }

        @Override
        public void onServiceDisconnected(int profile) {

        }
    };

    /**
     * 对蓝牙设备做连接处理
     */
    public void setBlueAirConnected(BluetoothDevice device) {
        connectA2dpAndHeadSet(device, BluetoothHeadset.class, mBluetoothHeadset);
        connectA2dpAndHeadSet(device, BluetoothA2dp.class, mBluetoothA2dp);
    }

    /**
     * 连接A2dp 与 HeadSet
     */
    private boolean connectA2dpAndHeadSet(BluetoothDevice blueDevice, Class btClass, BluetoothProfile bluetoothProfile) {
        setPriority(blueDevice, 100); //设置priority
        try {
            //通过反射获取BluetoothA2dp中connect方法（hide的），进行连接。
            Method connectMethod = btClass.getMethod("connect",
                    BluetoothDevice.class);
            connectMethod.setAccessible(true);
            connectMethod.invoke(bluetoothProfile, blueDevice);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void setPriority(BluetoothDevice device, int priority) {
        if (mBluetoothA2dp == null) return;
        try {//通过反射获取BluetoothA2dp中setPriority方法（hide的），设置优先级
            Method connectMethod = BluetoothA2dp.class.getMethod("setPriority",
                    BluetoothDevice.class, int.class);
            connectMethod.setAccessible(true);
            connectMethod.invoke(mBluetoothA2dp, device, priority);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    protected void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    protected void showToast(int msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
