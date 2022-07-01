package test.com.ido.file.transfer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ido.ble.BLEManager;
import com.ido.ble.LocalDataManager;
import com.ido.ble.callback.GetDeviceInfoCallBack;
import com.ido.ble.file.transfer.FileTransferConfig;
import com.ido.ble.file.transfer.IFileTransferListener;
import com.ido.ble.protocol.model.CanDownLangInfo;
import com.ido.ble.protocol.model.CanDownLangInfoV3;
import com.ido.ble.protocol.model.Units;

import test.com.ido.CallBack.BaseGetDeviceInfoCallBack;
import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;
import test.com.ido.utils.DataUtils;

public class MultLangTrainActivity extends BaseAutoConnectActivity {
    private TextView tvFilePath, tvLangInfo, tvState;
    private EditText etLangValue;

    private GetDeviceInfoCallBack.ICallBack  callBack = new BaseGetDeviceInfoCallBack(){
        @Override
        public void onGetCanDownloadLangInfo(CanDownLangInfo canDownLangInfo) {
            if (canDownLangInfo == null){
                tvLangInfo.setText("null");
            }else {
                tvLangInfo.setText(canDownLangInfo.toString());
            }
        }

        @Override
        public void onGetCanDownloadLangInfoV3(CanDownLangInfoV3 canDownLangInfoV3) {
            super.onGetCanDownloadLangInfoV3(canDownLangInfoV3);
            if (canDownLangInfoV3 == null){
                tvLangInfo.setText("null");
            }else {
                tvLangInfo.setText(canDownLangInfoV3.toString());
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mult_lang_train);

        tvFilePath = findViewById(R.id.tran_lang_file_path_tv);
        tvLangInfo = findViewById(R.id.tran_lang_info_tv);
        etLangValue = findViewById(R.id.tran_lang_lang_value_et);
        tvState = findViewById(R.id.tran_lang_state_tv);
        tvFilePath.setText(DataUtils.getInstance().getFilePath());

        BLEManager.registerGetDeviceInfoCallBack(callBack);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        BLEManager.unregisterGetDeviceInfoCallBack(callBack);
    }

    public void selectFile(View view){
        openFileChooser();
    }

    public void setLang(View view){
        int langValue = Integer.parseInt(etLangValue.getText().toString());
        Units units = LocalDataManager.getUnits();
        if (units == null){
            units = new Units();
        }
        units.language = langValue;
        BLEManager.setUnit(units);
    }


    public void getLangInfo(View view){
        BLEManager.getCanDownloadLangInfo();
    }

    public void getCanDownloadLangInfoV3(View view){
        BLEManager.getCanDownloadLangInfoV3();
    }

    public void startTrans(View view){
        BLEManager.startTranCommonFile(FileTransferConfig.getDefaultTransLangFileConfig(tvFilePath.getText().toString(), new IFileTransferListener() {
            @Override
            public void onStart() {
                tvState.setText("onStart");
            }

            @Override
            public void onProgress(int progress) {
                tvState.setText("" + progress);
            }

            @Override
            public void onSuccess() {
                tvState.setText("onSuccess");
            }

            @Override
            public void onFailed(String errorMsg) {
                tvState.setText(errorMsg);
            }
        }));
    }

    private static final int SELECT_FILE_REQ = 1;
    private void openFileChooser() {
        final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/zip");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
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
                // and read new one
                final Uri uri = data.getData();
                /*
                 * The URI returned from application may be in 'file' or 'content' schema. 'File' schema allows us to create a File object and read details from if
                 * directly. Data from 'Content' schema must be read by Content Provider. To do that we are using a Loader.
                 */
                if (uri.getScheme().equals("file") || uri.getScheme().equals("content")) {
                    // the direct path to the file has been returned
                    final String path = uri.getPath();
                    tvFilePath.setText(path);
                    DataUtils.getInstance().saveFilePath(path);
                }
            }

        }
    }
}
