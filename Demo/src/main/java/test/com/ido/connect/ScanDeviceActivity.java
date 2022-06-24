package test.com.ido.connect;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.ido.ble.BLEManager;
import com.ido.ble.BLESpecialAPI;
import com.ido.ble.bluetooth.connect.ConnectFailedReason;
import com.ido.ble.bluetooth.device.BLEDevice;
import com.ido.ble.callback.BindCallBack;
import com.ido.ble.callback.ConnectCallBack;
import com.ido.ble.callback.ScanCallBack;
import com.ido.ble.callback.UnbindCallBack;

import java.util.ArrayList;
import java.util.Collections;

import test.com.ido.HomeActivity;
import test.com.ido.R;
import test.com.ido.dfu.MainDfuActivity;
import test.com.ido.font.upgrade.FontScanDeviceActivity;
import test.com.ido.logoutput.LogOutput;
import test.com.ido.set.SetUserInfoActivity;
import test.com.ido.utils.DataUtils;
import test.com.ido.utils.DialogUtil;

public class ScanDeviceActivity extends Activity implements View.OnClickListener {


    private ListView lView;
    private Button btnScan, btnBind;
    private ScanAdapter adapter;
    private Handler mHandler = new Handler();
    private ArrayList<BLEDevice> list = new ArrayList<BLEDevice>();

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
            btnBind.setVisibility(View.VISIBLE);
        }

        @Override
        public void onConnectFailed(ConnectFailedReason reason,String macAddress) {
            closeProgressDialog();
            setTitle(getString(R.string.connect_state_failed) + reason);
        }

        @Override
        public void onConnectBreak(String macAddress) {
            closeProgressDialog();
            setTitle(getString(R.string.connect_state_break));
        }

        @Override
        public void onInDfuMode(BLEDevice bleDevice) {
            closeProgressDialog();
            setTitle(getString(R.string.connect_state_dfu) + "_"+bleDevice.mDeviceId);
            handleDfuState(bleDevice);
        }

        @Override
        public void onDeviceInNotBindStatus(String macAddress) {

        }

        @Override
        public void onInitCompleted(String macAddress) {
        }

    };

    private void handleDfuState(final BLEDevice bleDevice){
        new AlertDialog.Builder(this).setTitle("提示")
                .setMessage("设备处于升级模式，是否去升级？").setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent();
                intent.setClass(ScanDeviceActivity.this, MainDfuActivity.class);
                intent.putExtra("mac_address", bleDevice.mDeviceAddress);
                intent.putExtra("device_id", bleDevice.mDeviceId + "");
                startActivity(intent);
            }
        }).create().show();
    }

    private void retry(){
        list.clear();
        adapter.clear();
        btnScan.setEnabled(false);
        BLEManager.stopScanDevices();
        BLEManager.startScanDevices();
    }

    private UnbindCallBack.ICallBack unbindCallBack = new UnbindCallBack.ICallBack() {
        @Override
        public void onSuccess() {
            BLEManager.unregisterUnbindCallBack(this);
            Toast.makeText(ScanDeviceActivity.this, "解绑成功，请重新绑定", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onFailed() {
            BLEManager.unregisterUnbindCallBack(this);
            Toast.makeText(ScanDeviceActivity.this, "解绑失败", Toast.LENGTH_LONG).show();
        }
    };

    private BindCallBack.ICallBack bindCallBack = new BindCallBack.ICallBack() {
        @Override
        public void onSuccess() {
            closeProgressDialog();
            Toast.makeText(ScanDeviceActivity.this, "bind ok", Toast.LENGTH_LONG).show();

            boolean isAddDeviceIntent = getIntent().getBooleanExtra("isAddDeviceIntent",false);
            if (isAddDeviceIntent){
                finish();
                return;
            }
            gotoNextPage();
        }

        @Override
        public void onFailed(BindCallBack.BindFailedError bindFailedError) {
            closeProgressDialog();
            if (bindFailedError == BindCallBack.BindFailedError.ERROR_DEVICE_ALREADY_IN_BIND_STATE){
                DialogUtil.showDialog(ScanDeviceActivity.this, "取消", "手环已处于绑定状态，是否解绑", "确定", new DialogUtil.OnWheelChangeingListener() {
                    @Override
                    public void OnYes() {
                        BLEManager.registerUnbindCallBack(unbindCallBack);
                        BLEManager.unbind();
                    }

                    @Override
                    public void OnNo() {

                    }
                });
            }else {
                Toast.makeText(ScanDeviceActivity.this, "bind failed", Toast.LENGTH_LONG).show();
            }

        }


        @Override
        public void onCancel() {
            closeProgressDialog();
            Toast.makeText(ScanDeviceActivity.this, "bind cancel", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onReject() {
            closeProgressDialog();
            Toast.makeText(ScanDeviceActivity.this, "bind reject", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onNeedAuth(int authLength) {
            closeProgressDialog();
//            Toast.makeText(ScanDeviceActivity.this, "need Auth", Toast.LENGTH_LONG).show();
            DialogUtil.showNormalDialog(ScanDeviceActivity.this, "请输入绑定码:", "取消", "确定", new DialogUtil.OnNormalDialogListener() {
                @Override
                public void OnYes(int[] auth_code) {
                    BLEManager.setBindAuth(auth_code);
                }

                @Override
                public void OnNo() {

                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        initView();
        addListener();

        BLESpecialAPI.statScanStatus(true);
        BLEManager.registerScanCallBack(scanCallBack);
        BLEManager.registerConnectCallBack(connectCallBack);
        BLEManager.registerBindCallBack(bindCallBack);

        BLEManager.startScanDevices();

        findViewById(R.id.btn_bind).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                gotoNextPage();
                return false;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void initView() {
        lView = (ListView) findViewById(R.id.lv_device);
        btnBind = (Button) findViewById(R.id.btn_bind);
        btnScan = (Button) findViewById(R.id.btn_scan);

        btnScan.setEnabled(false);
        adapter = new ScanAdapter(this, null);
        lView.setAdapter(adapter);
        lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
                adapter.setSelectDevice(list.get(index));
                showProgressDialog("connecting...");

                BLEManager.connect(list.get(index));
//                BLEManager.scanAndConnect(list.get(index).mDeviceAddress);
            }
        });
        lView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return false;
            }
        });
    }

    public void addListener() {
        btnBind.setOnClickListener(this);
        btnScan.setOnClickListener(this);
    }


    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BLESpecialAPI.statScanStatus(false);
        BLEManager.stopScanDevices();
        BLEManager.unregisterScanCallBack(scanCallBack);
        BLEManager.unregisterConnectCallBack(connectCallBack);
        BLEManager.unregisterBindCallBack(bindCallBack);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_bind:
                bind();
                break;
            case R.id.btn_scan:
                btnBind.setVisibility(View.INVISIBLE);
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


    private void bind(){
        showProgressDialog("bind...");
        BLEManager.bind();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.scan_device_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
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
            case R.id.font_upgrade:
                gotoFontUpgradePage();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void gotoFontUpgradePage(){
        BLEManager.stopScanDevices();

        Intent intent = new Intent();
        intent.setClass(this, FontScanDeviceActivity.class);
        startActivity(intent);
        finish();
    }

    private void gotoNextPage(){
        Intent intent ;
        if (DataUtils.getInstance().isFirst()) {
            intent = new Intent(ScanDeviceActivity.this, SetUserInfoActivity.class);
            intent.putExtra("isFromScanPage", true);
        }else {
            intent = new Intent(ScanDeviceActivity.this, HomeActivity.class);
        }
        startActivity(intent);
        finish();
    }

}
