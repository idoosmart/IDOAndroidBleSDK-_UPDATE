package test.com.ido.connect;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ido.ble.BLEManager;
import com.ido.ble.LocalDataManager;
import com.ido.ble.bluetooth.device.BLEDevice;
import com.ido.ble.business.multidevice.ICommonListener;

import java.util.List;

import test.com.ido.R;

public class DeviceManageActivity extends Activity {

    private List<String> bindMacAddressList;
    private ListView listView;
    private BaseAdapter baseAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return bindMacAddressList.size();
        }

        @Override
        public Object getItem(int position) {
            return bindMacAddressList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = new TextView(DeviceManageActivity.this);
            textView.setHeight(200);
            textView.setGravity(Gravity.CENTER);
            textView.setText(bindMacAddressList.get(position));


            if (bindMacAddressList.get(position).equals(LocalDataManager.getLastConnectedDeviceInfo().mDeviceAddress)){
                if (BLEManager.isConnected()) {
                    textView.setTextColor(Color.RED);
                }
            }else {
                textView.setTextColor(Color.BLACK);
            }
            return textView;
        }
    };
    private ProgressDialog progressDialog;

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }

    private void refreshData(){
        bindMacAddressList = LocalDataManager.getBindMacAddressList();
        listView.setAdapter(baseAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_manage);

//        bindMacAddressList = SPDataUtils.getInstance().getBindMacAddressList();
        listView = findViewById(R.id.listview);
//        listView.setAdapter(baseAdapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                BLEManager.unbind(bindMacAddressList.get(position), new ICommonListener() {
                    @Override
                    public void onSuccess(String macAddress) {
                        Toast.makeText(DeviceManageActivity.this, macAddress +" unbind ok", Toast.LENGTH_LONG).show();
                        refreshData();
                    }

                    @Override
                    public void onFailed(String macAddress) {
                        Toast.makeText(DeviceManageActivity.this, macAddress + " unbind failed", Toast.LENGTH_LONG).show();
                    }
                });
                return true;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switchDevice(bindMacAddressList.get(position));
//                BLEManager.autoConnect(bindMacAddressList.get(position));
            }
        });
    }

    public void addDevice(View view){
        BLEManager.disConnect();
        Intent intent = new Intent(this, ScanDeviceActivity.class);
        intent.putExtra("isAddDeviceIntent", true);
        startActivity(intent);
    }

    private void switchDevice(String macAddress){
        showProgressDialog("switching");
        BLEManager.switchToDevice(macAddress, new ICommonListener() {
            @Override
            public void onSuccess(String macAddress) {
                Toast.makeText(DeviceManageActivity.this, "switch ok", Toast.LENGTH_LONG).show();
                switchFinished();

            }

            @Override
            public void onFailed(String macAddress) {
                Toast.makeText(DeviceManageActivity.this, "switch failed", Toast.LENGTH_LONG).show();
                switchFinished();
            }
        });
    }

    private void switchFinished(){
        closeProgressDialog();
        baseAdapter.notifyDataSetChanged();
    }

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
}
