package test.com.ido;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.ido.ble.BLEManager;
import com.ido.ble.InitParam;
import com.ido.ble.LocalDataManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import test.com.ido.connect.ScanDeviceActivity;
import test.com.ido.log.LogPathImpl;

public class SplashActivity extends Activity {

    /*权限*/
    private final int SDK_PERMISSION_REQUEST = 127;

    private List<String> permissionList = new ArrayList<>();
    private int requestPermissonCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissionList.add(Manifest.permission.BLUETOOTH_ADMIN);
        permissionList.add(Manifest.permission.BLUETOOTH);
        getPermission();




    }

    @TargetApi(23)
    private void getPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && requestPermissonCount < 5) {
            if (permissionList.size() != 0){
                requestPermissonCount ++;
                requestPermissions(permissionList.toArray(new String[permissionList.size()]), SDK_PERMISSION_REQUEST);
            }else {
                initSDK();
                jumpNextPage();
            }
        }else {
            initSDK();
            jumpNextPage();
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SDK_PERMISSION_REQUEST) {
            for (int i = 0; i < permissions.length; i++){
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED){
                    permissionList.remove(permissions[i]);
                }
            }

            getPermission();
        }
    }

    private void jumpNextPage(){
        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (LocalDataManager.isBind()){
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                }else {
                    startActivity(new Intent(SplashActivity.this, ScanDeviceActivity.class));
                }

                finish();
            }
        }, 500);
    }
    private void initSDK(){
        InitParam param = new InitParam();
        param.log_save_path = LogPathImpl.getInstance().getBleSdkLogPath();
        param.isSaveDeviceDataToDB = false;
        param.isNeedSoLibAutoSyncConfigIfReboot = false;
        param.isEncryptedSPData = true;
        if (true) {
            try {
                String soPath = LogPathImpl.getInstance().getSoPath();
                File file = new File(soPath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                param.soJinLogSavePath = soPath;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            param.soJinLogSavePath = "";
        }
        BLEManager.init(param);
    }

}
