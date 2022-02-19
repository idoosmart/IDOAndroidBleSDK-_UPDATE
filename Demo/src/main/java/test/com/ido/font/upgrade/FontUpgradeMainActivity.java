package test.com.ido.font.upgrade;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ido.ble.bluetooth.device.BLEDevice;

import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;
import test.com.ido.font.upgrade.present.FontUpgradePresenter;
import test.com.ido.font.upgrade.present.IViewUpdate;
import test.com.ido.logoutput.LogOutput;
import test.com.ido.utils.DataUtils;

public class FontUpgradeMainActivity extends BaseAutoConnectActivity implements IViewUpdate {

    private TextView tvConnectDevice, tvFontFilePath, tvBinFilePath, tvOTAFilePath, tvUpgradeStatus, tvLostTime;
    private Button btnSelectFontFile, btnSelectBinFile, btnSelectOTAFile;
    private FontUpgradePresenter fontUpgradePresenter;
    private ProgressBar pbFontFile, pbBinFile, pbOtaFile;
    private TextView tvProgressFontFile, tvProgressBinFile, tvProgressOtaFile;
    private TextView tvMtuRx, tvMtuTx, tvMtuDLE, tvMtuPHY;

    private TextView tvBgFontFile, tvBgBinFile, tvBgOtaFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_font_upgrade_main);


        initView();
        initPara();

    }

    private void initPara() {
        BLEDevice device = (BLEDevice) getIntent().getSerializableExtra("device");
        tvConnectDevice.setText(device.mDeviceName + "  <" + device.mDeviceAddress + ">");
        fontUpgradePresenter = new FontUpgradePresenter(this, device);


        tvFontFilePath.setText(DataUtils.getInstance().getFontUpgradeFontFilePath());
        tvBinFilePath.setText(DataUtils.getInstance().getFontUpgradeBinFilePath());
        tvOTAFilePath.setText(DataUtils.getInstance().getFontUpgradeOtaFilePath());

        MtuInfo mtuInfo = (MtuInfo) getIntent().getSerializableExtra("mtu");
        tvMtuRx.setText("接收MTU：" + mtuInfo.rx_mtu);
        tvMtuTx.setText("发送MTU：" + mtuInfo.tx_mtu);
        tvMtuPHY.setText("PHY：" + mtuInfo.phy_speed);
        tvMtuDLE.setText("DLE长度：" + mtuInfo.dle_length);
    }

    private void initView() {
        tvConnectDevice = findViewById(R.id.font_upgrade_connect_device_tv);
        btnSelectFontFile = findViewById(R.id.font_select_so_file_btn);
        btnSelectBinFile = findViewById(R.id.font_select_bin_file_btn);
        btnSelectOTAFile = findViewById(R.id.font_select_ota_file_btn);

        tvFontFilePath = findViewById(R.id.font_so_file_path_tv);
        tvBinFilePath = findViewById(R.id.font_bin_file_path_tv);
        tvOTAFilePath = findViewById(R.id.font_ota_file_path_tv);

        pbFontFile = findViewById(R.id.progressBar_so_file);
        pbBinFile = findViewById(R.id.progressBar_bin_file);
        pbOtaFile = findViewById(R.id.progressBar_ota_file);

        tvProgressFontFile = findViewById(R.id.progress_font_file_tv);
        tvProgressBinFile = findViewById(R.id.progress_bin_file_tv);
        tvProgressOtaFile = findViewById(R.id.progress_ota_file_tv);

        tvUpgradeStatus = findViewById(R.id.font_upgrade_status_tv);

        tvLostTime = findViewById(R.id.font_upgrade_lost_time_tv);






        tvBgFontFile = findViewById(R.id.font_upgrade_font_file_bg_tv);
        tvBgBinFile = findViewById(R.id.font_upgrade_bin_file_bg_tv);
        tvBgOtaFile = findViewById(R.id.font_upgrade_ota_file_bg_tv);



        tvMtuRx = findViewById(R.id.font_upgrade_mtu_rx);
        tvMtuTx = findViewById(R.id.font_upgrade_mtu_tx);
        tvMtuDLE = findViewById(R.id.font_upgrade_dle);
        tvMtuPHY = findViewById(R.id.font_upgrade_phy);


        btnSelectFontFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser(SELECT_FONT_FILE_REQ);
            }
        });

        btnSelectBinFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser(SELECT_BIN_FILE_REQ);
            }


        });
        btnSelectOTAFile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                openFileChooser(SELECT_OTA_FILE_REQ);
            }


        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fontUpgradePresenter = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.font_upgrade_more, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.log_out_by_self:
                LogOutput.enableSelf(this.getApplicationContext(), getIntent().getComponent());
                break;
            case R.id.font_upgrade_setting:
                startActivity(new Intent(this, FontUpgradeSettingActivity.class));
                break;
        }
        return true;
    }

    public void startOrRetryUpgrade(View view){
        fontUpgradePresenter.start();
    }

    public void stopUpgrade(View view){
        fontUpgradePresenter.stop();
    }

    @Override
    public void onTaskFailed() {
    }

    @Override
    public void onTaskSuccess() {
        tvUpgradeStatus.setText("全部升级完成");


        tvBgFontFile.setVisibility(View.GONE);
        tvBgBinFile.setVisibility(View.GONE);
        tvBgOtaFile.setVisibility(View.GONE);

    }

    @Override
    public void onTaskStart() {

    }

    @Override
    public void onLostTime(long second) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvLostTime.setText("已经耗时：" + second + " 秒");
            }
        });

    }


    private static final int SELECT_FONT_FILE_REQ = 1;
    private static final int SELECT_BIN_FILE_REQ = 2;
    private static final int SELECT_OTA_FILE_REQ = 3;
    private void openFileChooser(int req) {
        final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/zip");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, req);
        }else {
            Toast.makeText(this, "无法选择文件", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (resultCode != RESULT_OK)
            return;
        // and read new one
        final Uri uri = data.getData();
        /*
         * The URI returned from application may be in 'file' or 'content' schema. 'File' schema allows us to create a File object and read details from if
         * directly. Data from 'Content' schema must be read by Content Provider. To do that we are using a Loader.
         */
        String filePath = "";
        if (uri.getScheme().equals("file") || uri.getScheme().equals("content")) {
            // the direct path to the file has been returned
            filePath = uri.getPath();
        }

        switch (requestCode) {
            case SELECT_FONT_FILE_REQ:
                tvFontFilePath.setText(filePath);
                DataUtils.getInstance().saveFontUpgradeFontFilePath(filePath);
                break;
            case SELECT_BIN_FILE_REQ:
                tvBinFilePath.setText(filePath);
                DataUtils.getInstance().saveFontUpgradeBinFilePath(filePath);
                break;
            case SELECT_OTA_FILE_REQ:
                tvOTAFilePath.setText(filePath);
                DataUtils.getInstance().saveFontUpgradeOtaFilePath(filePath);
                break;

        }
    }

    @Override
    public void onUpdateFontFileStart() {
        tvUpgradeStatus.setText("升级字库更新文件。。。");
    }

    @Override
    public void onUpdateFontFileProgress(int progress) {
        pbFontFile.setProgress(progress);
        tvProgressFontFile.setText(progress + "%");
    }

    @Override
    public void onUpdateFontFileSuccess() {
        tvUpgradeStatus.setText("升级字库更新文件 成功");
        tvBgFontFile.setVisibility(View.VISIBLE);
    }

    @Override
    public void onUpdateFontFileFailed() {
        tvUpgradeStatus.setText("升级字库更新文件 失败");
    }

    @Override
    public void onUpdateBinFileStart() {
        tvUpgradeStatus.setText("升级Bin文件中。。。");
    }

    @Override
    public void onUpdateBinFileProgress(int progress) {
        pbBinFile.setProgress(progress);
        tvProgressBinFile.setText(progress + "%");
    }

    @Override
    public void onUpdateBinFileSuccess() {
        tvUpgradeStatus.setText("升级Bin文件中 成功");
        tvBgBinFile.setVisibility(View.VISIBLE);
    }

    @Override
    public void onUpdateBinFileFailed() {
        tvUpgradeStatus.setText("升级Bin文件中 失败");
    }

    @Override
    public void onUpdateOtaFileStart() {
        tvUpgradeStatus.setText("升级OTA文件中。。。");
    }

    @Override
    public void onUpdateOtaFileProgress(int progress) {
        pbOtaFile.setProgress(progress);
        tvProgressOtaFile.setText(progress + "%");
    }

    @Override
    public void onUpdateOtaFileSuccess() {
        tvUpgradeStatus.setText("升级OTA文件中 成功");
        tvBgOtaFile.setVisibility(View.VISIBLE);
    }

    @Override
    public void onUpdateOtaFileFailed() {
        tvUpgradeStatus.setText("升级OTA文件中 失败");
    }


}
