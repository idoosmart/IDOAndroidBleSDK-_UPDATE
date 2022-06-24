package test.com.ido.connect;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ido.ble.BLEManager;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import test.com.ido.R;

public class ConnectManageActivity extends BaseAutoConnectActivity {

    private TextView tvConnectState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_manage);
        tvConnectState = (TextView) findViewById(R.id.connect_state_tv);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onResume() {
        super.onResume();
        tvConnectState.setText(getState());
    }

    public void disConnect(View view){
        BLEManager.disConnect();
    }

    public void autoConnect(View view){
        BLEManager.autoConnect();
    }

    public void checkState(View view){
        Toast.makeText(this,  getState(), Toast.LENGTH_SHORT).show();
        tvConnectState.setText(getState());
    }
    private String getState(){
        if (BLEManager.isConnected()){
            return "ok";
        }else {
            return "break";
        }
    }


    private EditText statusEt, newStateEt;

    public void onDeviceManager(View view){
        startActivity(new Intent(this, DeviceManageActivity.class));
    }

    private void getAddress(double latitude, double longitude) {
        //Geocoder通过经纬度获取具体信息
        Geocoder gc = new Geocoder(this, Locale.JAPANESE);
        try {
            List<Address> locationList = gc.getFromLocation(latitude, longitude, 1);

            if (locationList != null && locationList.size() != 0) {
                Address address = locationList.get(0);
                String countryName = address.getCountryName();//国家
                String countryCode = address.getCountryCode();
                String adminArea = address.getAdminArea();//省
                String locality = address.getLocality();//市
                String subLocality = address.getSubLocality();//区
                String featureName = address.getFeatureName();//街道

                for (int i = 0; address.getAddressLine(i) != null; i++) {
                    String addressLine = address.getAddressLine(i);
                    //街道名称:广东省深圳市罗湖区蔡屋围一街深圳瑞吉酒店
                    System.out.println("addressLine=====" + addressLine);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void btConnect(View view){
        BLEManager.connectBT();
    }

}
