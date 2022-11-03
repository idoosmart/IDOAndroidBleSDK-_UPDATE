package test.com.ido.dial;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ido.ble.BLEManager;
import com.ido.ble.LocalDataManager;
import com.ido.ble.watch.custom.WatchPlateSetConfig;
import com.ido.ble.watch.custom.callback.WatchPlateCallBack;
import com.ido.ble.watch.custom.model.DialPlateParam;
import com.ido.ble.watch.custom.model.WatchPlateFileInfo;
import com.ido.ble.watch.custom.model.WatchPlateScreenInfo;

import java.io.File;

import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;
import test.com.ido.utils.DataUtils;
import test.com.ido.utils.GetFilePathFromUri;
import test.com.ido.utils.FileUtil;

/**
 * @author tianwei
 * @date 2022/10/31
 * @time 9:50
 * 用途:表盘
 */
public class DialActivity extends BaseAutoConnectActivity {

    EditText etResult;
    EditText etDialPath;
    EditText et_set_dial_name;
    EditText et_delete_dial_name;
    EditText et_switch_dial_name;
    TextView tvSetStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dial);
        etResult = findViewById(R.id.etResult);
        etDialPath = findViewById(R.id.etDialPath);
        et_set_dial_name = findViewById(R.id.et_set_dial_name);
        et_delete_dial_name = findViewById(R.id.et_delete_dial_name);
        et_switch_dial_name = findViewById(R.id.et_switch_dial_name);
        tvSetStatus = findViewById(R.id.tvSetStatus);
        BLEManager.registerWatchOperateCallBack(callBack);
        String dialPath = DataUtils.getInstance().getDialPackagePath();
        if (!TextUtils.isEmpty(dialPath)) {
            etDialPath.setText(dialPath);
        }
        String dialName = DataUtils.getInstance().getDialName();
        if (!TextUtils.isEmpty(dialName)) {
            et_set_dial_name.setText(dialName);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BLEManager.unregisterWatchOperateCallBack(callBack);
    }

    WatchPlateCallBack.IOperateCallBack callBack = new WatchPlateCallBack.IOperateCallBack() {

        @Override
        public void onGetPlateFileInfo(WatchPlateFileInfo watchPlateFileInfo) {
            etResult.setText(watchPlateFileInfo.toString());
        }

        @Override
        public void onGetScreenInfo(WatchPlateScreenInfo watchPlateScreenInfo) {

        }

        @Override
        public void onGetCurrentPlate(String s) {
            et_delete_dial_name.setText(s);
        }

        @Override
        public void onSetPlate(boolean b) {
            Toast.makeText(DialActivity.this, b ? "set succeed" : "set failed", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onDeletePlate(boolean b) {
            Toast.makeText(DialActivity.this, b ? "delete succeed" : "delete failed", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onGetDialPlateParam(DialPlateParam dialPlateParam) {
            StringBuilder content = new StringBuilder("now_show_watch_name: " + dialPlateParam.now_show_watch_name);
            content.append("\nwatch_capacity_size：").append(dialPlateParam.watch_capacity_size / 1024).append("kb");
            content.append("\nuser_watch_capacity_size：").append(dialPlateParam.user_watch_capacity_size / 1024).append("kb");
            content.append("\nusable_max_download_space_size：").append(dialPlateParam.usable_max_download_space_size / 1024).append("kb");
            content.append("\ncloud_watch_num：").append(dialPlateParam.cloud_watch_num);
            content.append("\nuser_cloud_watch_num：").append(dialPlateParam.user_cloud_watch_num);
            content.append("\nlocal_watch_num：").append(dialPlateParam.local_watch_num);
            content.append("\nwatch_frame_main_version：").append(dialPlateParam.watch_frame_main_version);
            content.append("\nwallpaper_watch_num").append(dialPlateParam.wallpaper_watch_num);
            content.append("\nuser_wallpaper_watch_num").append(dialPlateParam.user_wallpaper_watch_num);
            content.append("\nuser_wallpaper_watch_num").append(dialPlateParam.user_wallpaper_watch_num);
            for (DialPlateParam.PlateFileInfo info : dialPlateParam.item) {
                content.append("\n").append(info.name).append("：").append(info.size / 1024).append("kb");
            }
            etResult.setText(content.toString());
        }
    };

    public void btQuery(View view) {
        BLEManager.getCurrentWatchPlate();
        if (LocalDataManager.getSupportFunctionInfo().V3_get_watch_list_new) {
            BLEManager.getDialPlateParam();
        } else {
            BLEManager.getWatchPlateList();
        }
    }

    public void btDelete(View view) {
        String dialName = et_delete_dial_name.getText().toString().trim();
        if (TextUtils.isEmpty(dialName)) {
            Toast.makeText(this, "pls input dial name", Toast.LENGTH_LONG).show();
            return;
        }
        BLEManager.deleteWatchPlate(dialName);
    }

    public void btSwitch(View view) {
        String dialName = et_switch_dial_name.getText().toString().trim();
        if (TextUtils.isEmpty(dialName)) {
            Toast.makeText(this, "pls input dial name", Toast.LENGTH_LONG).show();
            return;
        }
        BLEManager.setWatchPlate(dialName);
    }

    public void btSet(View view) {
        String path = etDialPath.getText().toString().trim();
        if (TextUtils.isEmpty(path)) {
            Toast.makeText(this, "pls select dial package", Toast.LENGTH_LONG).show();
            return;
        }

        String dialName = et_set_dial_name.getText().toString().trim();
        if (TextUtils.isEmpty(dialName)) {
            Toast.makeText(this, "pls input dial name", Toast.LENGTH_LONG).show();
            return;
        }

        WatchPlateSetConfig config = new WatchPlateSetConfig();
        config.filePath = path;
        config.uniqueID = dialName;
        config.isOnlyTranslateWatchFile = false;
        config.errorCallback = i -> tvSetStatus.setText("failed: " + i);
        //sdk内部自动处理 24、25的code码
        config.isAutoProcessDiskDefrag = true;
        config.stateListener = new WatchPlateCallBack.IAutoSetPlateCallBack() {
            @Override
            public void onStart() {
                tvSetStatus.setText("start");
            }

            @Override
            public void onProgress(int i) {
                tvSetStatus.setText("" + i);
            }

            @Override
            public void onSuccess() {
                tvSetStatus.setText("success");
            }

            @Override
            public void onFailed() {
                tvSetStatus.setText("failed");
            }
        };
        BLEManager.startSetPlateFileToWatch(config);
    }

    public void btSelect(View view) {
        openFileChooser();
    }


    private static final int SELECT_FILE_REQ = 1;

    private void openFileChooser() {
        final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("application/bin");
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        if (intent.resolveActivity(getPackageManager()) != null) {
            // file browser has been found on the device
            startActivityForResult(intent, SELECT_FILE_REQ);
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {
            case SELECT_FILE_REQ: {
                Uri uri = data.getData();
                String path = GetFilePathFromUri.getFileAbsolutePath(this, uri);
                if (!TextUtils.isEmpty(path)) {
                    etDialPath.setText(path);
                    String name = FileUtil.getNoSuffixFileNameFromPath(path);
                    et_set_dial_name.setText(name);
                    DataUtils.getInstance().saveDialPackagePath(path);
                    DataUtils.getInstance().saveDialName(name);
                }
            }
        }
    }
}
