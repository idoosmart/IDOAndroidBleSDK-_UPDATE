package test.com.ido.font.upgrade;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.ido.ble.BLEManager;
import com.ido.ble.BLESpecialAPI;
import com.ido.ble.bluetooth.connect.ConnectFailedReason;
import com.ido.ble.bluetooth.device.BLEDevice;
import com.ido.ble.callback.ConnectCallBack;
import com.ido.ble.callback.DeviceResponseCommonCallBack;
import com.ido.ble.callback.ScanCallBack;

import java.util.ArrayList;
import java.util.Collections;

import test.com.ido.R;
import test.com.ido.connect.ScanAdapter;

public class FontScanDeviceActivity extends Activity  implements View.OnClickListener {

    private ListView lView;
    private Button btnScan, btnOk;
    private ScanAdapter adapter;
    private Handler mHandler = new Handler();
    private ArrayList<BLEDevice> list = new ArrayList<BLEDevice>();
    private BLEDevice selectedDevice;
    private MtuInfo mtuInfo;

    private ProgressDialog progressDialog;
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

    private ScanCallBack.ICallBack scanCallBack = new ScanCallBack.ICallBack() {
        @Override
        public void onStart() {
            setTitle(R.string.scanning);
        }

        @Override
        public void onFindDevice(BLEDevice device) {
            showList(device);
            list.add(device);
            Collections.sort(list);
        }

        @Override
        public void onScanFinished() {
            if (getTitle().toString().equals(getText(R.string.connect_state_ing))) {
                setTitle(getText(R.string.scan_finish) + "/" + getText(R.string.connect_state_ing));
            }else if (getTitle().toString().equals(getText(R.string.connect_state_ok))){

            }else {
                setTitle(R.string.scan_finish);
            }
            btnScan.setEnabled(true);

            if (list.size() == 0){
            }
        }
    };

    private ConnectCallBack.ICallBack connectCallBack = new ConnectCallBack.ICallBack(){

        @Override
        public void onConnectStart(String macAddress) {
            setTitle(getString(R.string.connect_state_start));

        }

        @Override
        public void onConnecting(String macAddress) {
            setTitle(getString(R.string.connect_state_ing));
        }

        @Override
        public void onRetry(int count, String macAddress) {

        }

        @Override
        public void onConnectSuccess(String macAddress) {
            closeProgressDialog();
            setTitle(getString(R.string.connect_state_ok));
            btnOk.setVisibility(View.VISIBLE);
        }

        @Override
        public void onConnectFailed(ConnectFailedReason reason,String macAddress) {
            closeProgressDialog();
            setTitle(getString(R.string.connect_state_failed));
        }

        @Override
        public void onConnectBreak(String macAddress) {
            closeProgressDialog();
            setTitle(getString(R.string.connect_state_break));
        }

        @Override
        public void onInDfuMode(BLEDevice bleDevice) {
            closeProgressDialog();
            setTitle("OTA");
            selectedDevice.mIsInDfuMode = true;
            btnOk.setVisibility(View.VISIBLE);
        }

        @Override
        public void onDeviceInNotBindStatus(String macAddress) {

        }

        @Override
        public void onInitCompleted(String macAddress) {
        }

    };

    private void handleDfuState(final BLEDevice bleDevice){
//        new AlertDialog.Builder(this).setTitle("提示")
//                .setMessage("设备处于升级模式，是否去升级？").setNegativeButton("否", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//
//            }
//        }).setPositiveButton("是", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
////                Intent intent = new Intent();
////                intent.setClass(ScanDeviceActivity.this, MainDfuActivity.class);
////                intent.putExtra("mac_address", bleDevice.mDeviceAddress);
////                intent.putExtra("device_id", bleDevice.mDeviceId + "");
////                startActivity(intent);
//            }
//        }).create().show();
    }

    private void retry(){
        list.clear();
        adapter.clear();
        btnScan.setEnabled(false);
        BLEManager.stopScanDevices();
        BLEManager.startScanDevices();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_font_scan_device);

        initView();
        addListener();


    }

    public void initView() {
        lView = (ListView) findViewById(R.id.lv_device);
        btnOk = (Button) findViewById(R.id.btn_ok);
        btnScan = (Button) findViewById(R.id.btn_scan);

        adapter = new ScanAdapter(this, null);
        lView.setAdapter(adapter);
        lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
                adapter.setSelectDevice(list.get(index));
                selectedDevice = list.get(index);
//                btnOk.setVisibility(View.VISIBLE);
                showProgressDialog("connecting...");
                BLEManager.connect(list.get(index));

            }
        });
    }

    public void addListener() {
        btnOk.setOnClickListener(this);
        btnScan.setOnClickListener(this);
        btnOk.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                gotoNextPage();
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        BLEManager.registerScanCallBack(scanCallBack);
        BLEManager.registerConnectCallBack(connectCallBack);
        BLEManager.registerDeviceResponseCommonCallBack(deviceResponsCallBack);
    }

    @Override
    protected void onStop() {
        super.onStop();
        BLEManager.stopScanDevices();
        BLEManager.unregisterScanCallBack(scanCallBack);
        BLEManager.unregisterConnectCallBack(connectCallBack);
        BLEManager.unregisterDeviceResponseCommonCallBack(deviceResponsCallBack);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                getMTU();
                break;
            case R.id.btn_scan:
                btnOk.setVisibility(View.INVISIBLE);
                BLEManager.disConnect();
                retry();
                break;
            default:
                break;
        }
    }

    private void showList(final BLEDevice device) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                adapter.upDada(device);
            }
        });
    }

    private void gotoNextPage(){
        Intent intent = new Intent(this, FontUpgradeMainActivity.class);
        intent.putExtra("device", selectedDevice);
        if (mtuInfo == null){
            mtuInfo = new MtuInfo();
        }
        intent.putExtra("mtu", mtuInfo);
        startActivity(intent);
    }


    private void getMTU(){
        showProgressDialog("获取MTU中");
        btnOk.postDelayed(new Runnable() {
            @Override
            public void run() {
                BLESpecialAPI.getMTU();
            }
        }, 2500);


    }

    private DeviceResponseCommonCallBack.ICallBack deviceResponsCallBack = new DeviceResponseCommonCallBack.ICallBack() {
        @Override
        public void onResponse(int eventType, String jsonData) {

        }


    };
}
