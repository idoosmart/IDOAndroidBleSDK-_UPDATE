package test.com.ido.set;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.ido.ble.BLEManager;
import com.ido.ble.file.transfer.FileTransferConfig;
import com.ido.ble.file.transfer.IFileTransferListener;
import com.ido.ble.protocol.model.WallpaperFileCreateConfig;
import com.ido.ble.watch.custom.WatchPlateSetConfig;
import com.ido.ble.watch.custom.callback.WatchPlateCallBack;
import com.ido.ble.watch.custom.model.DialPlateParam;
import com.ido.ble.watch.custom.model.WatchPlateFileInfo;
import com.ido.ble.watch.custom.model.WatchPlateScreenInfo;

import java.io.File;

import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;
import test.com.ido.utils.DataUtils;
import test.com.ido.utils.GsonUtil;

public class WatchPlateActivity extends BaseAutoConnectActivity {
    private String filePath;
    private TextView tvFilePath, tvState, tvOperateTv, tvUniqueID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_plate);

        tvFilePath = findViewById(R.id.file_path_tv);
        tvState = findViewById(R.id.state_tv);
        tvOperateTv = findViewById(R.id.operate_info_tv);
        tvUniqueID = findViewById(R.id.unique_id_tv);

        BLEManager.registerWatchOperateCallBack(iOperateCallBack);


        filePath = DataUtils.getInstance().getFilePath();
        if (TextUtils.isEmpty(filePath)){
            tvFilePath.setText("请先选择表盘压缩包(.zip)");
        }else {
            tvFilePath.setText(filePath);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BLEManager.unregisterWatchOperateCallBack(iOperateCallBack);
    }

    public void selectFile(View view){
        openFileChooser();
    }

    public void startSet(View view){
        WatchPlateSetConfig config = new WatchPlateSetConfig();
        File file = new File(filePath);
        config.uniqueID = file.getName().replace(".zip", "");
        config.filePath = filePath;
        config.isOnlyTranslateWatchFile = false;
        config.stateListener = new WatchPlateCallBack.IAutoSetPlateCallBack() {
            @Override
            public void onStart() {
                tvState.setText("start");
            }

            @Override
            public void onProgress(int progress) {
                tvState.setText("progress = " + progress);
            }

            @Override
            public void onSuccess() {
                tvState.setText("success");
            }

            @Override
            public void onFailed() {
                tvState.setText("failed");
            }
        };
        BLEManager.startSetPlateFileToWatch(config);
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
                    filePath = path;
                    tvFilePath.setText(path);
                    DataUtils.getInstance().saveFilePath(path);
                }
            }

        }
    }



    WatchPlateCallBack.IOperateCallBack iOperateCallBack = new WatchPlateCallBack.IOperateCallBack() {
//        @Override
//        public void onGetPlateList(List<String> uniqueIDList) {
//            tvOperateTv.setText(GsonUtil.toJson(uniqueIDList));
//        }

        @Override
        public void onGetPlateFileInfo(WatchPlateFileInfo watchPlateFileInfo) {
            if (watchPlateFileInfo == null || watchPlateFileInfo.fileNameList == null){
                return;
            }
            tvOperateTv.setText(watchPlateFileInfo.toString());
        }

        @Override
        public void onGetScreenInfo(WatchPlateScreenInfo screenInfo) {
            tvOperateTv.setText(GsonUtil.toJson(screenInfo));
        }

        @Override
        public void onGetCurrentPlate(String uniqueID) {
            tvOperateTv.setText("当前使用的表盘："  + uniqueID);
        }

        @Override
        public void onSetPlate(boolean isSuccess) {
            tvOperateTv.setText(isSuccess ? "设置成功" : "设置失败");
        }

        @Override
        public void onDeletePlate(boolean isSuccess) {
            tvOperateTv.setText(isSuccess ? "删除成功" : "删除失败");
        }

        @Override
        public void onGetDialPlateParam(DialPlateParam dialPlateParam) {

        }
    };

    public void getPlateList(View view){
        BLEManager.getWatchPlateList();
    }

    public void getScreenInfo(View view){
        BLEManager.getWatchPlateScreenInfo();
    }

    public void getCurrentPlate(View view){
        BLEManager.getCurrentWatchPlate();
    }

    public void deletePlate(View view){
        BLEManager.deleteWatchPlate(tvUniqueID.getText().toString());
    }

    public void setPlate(View view){
        BLEManager.setWatchPlate(tvUniqueID.getText().toString());
    }

    /**
     * 设置壁纸表盘
     */
    public void setWallpaperPNG(){
        WallpaperFileCreateConfig config = new WallpaperFileCreateConfig();
        //App裁剪用户选择的图片，生成的PNG原始图片地址
        config.setSourceFilePath("/asdcard/demo/test.png");
        //经过sdk处理之后会输出新的壁纸文件
        config.setOutFilePath("/adcard1/demo/outFile.png");
        //开始制作壁纸文件
        BLEManager.createPlateWallpaperFile(config);
        //制作完成，传输壁纸文件到设备
        FileTransferConfig config1 = FileTransferConfig.getDefaultTransPictureConfig(config.getOutFilePath(), new IFileTransferListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onProgress(int progress) {

            }

            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailed(String errorMsg) {

            }
        });
        BLEManager.stopTranCommonFile();
        BLEManager.startTranCommonFile(config1);
    }
}
