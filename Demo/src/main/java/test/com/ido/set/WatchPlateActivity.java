package test.com.ido.set;

import static com.ido.ble.LocalDataManager.getSupportFunctionInfo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ido.ble.BLEManager;
import com.ido.ble.LocalDataManager;
import com.ido.ble.dfu.BleDFUConfig;
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
import test.com.ido.localdata.Wallpaper;
import test.com.ido.log.LogPathImpl;
import test.com.ido.utils.DataUtils;
import test.com.ido.utils.GetFilePathFromUri;
import test.com.ido.utils.GsonUtil;
import test.com.ido.utils.ImageUtil;

public class WatchPlateActivity extends BaseAutoConnectActivity {
    private String filePath;
    private TextView tvFilePath, tvState, tvOperateTv, tvUniqueID ,tvPhoto;
    WatchPlateScreenInfo mScreenInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_plate);

        tvFilePath = findViewById(R.id.file_path_tv);
        tvState = findViewById(R.id.state_tv);
        tvOperateTv = findViewById(R.id.operate_info_tv);
        tvUniqueID = findViewById(R.id.unique_id_tv);
        tvPhoto = findViewById(R.id.photo_tv);
        BLEManager.getFunctionTables();
        BLEManager.registerWatchOperateCallBack(iOperateCallBack);

        BLEManager.getWatchPlateScreenInfo();
        filePath = DataUtils.getInstance().getFilePath();
        if (TextUtils.isEmpty(filePath)){
            tvFilePath.setText("please select watch file(.zip)");
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
    private static final int SELECT_IMAGE_REQ = 2;
    private String photoInputPath = "";
    private String photoInputPath_crop = LogPathImpl.getInstance().getPicPath()+"input.png";
    private void openFileChooser() {
        final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/zip");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            // file browser has been found on the device
            startActivityForResult(intent, SELECT_FILE_REQ);
        }
    }

    private void openImageChooser() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"),
                    SELECT_IMAGE_REQ);
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, SELECT_IMAGE_REQ);
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case SELECT_FILE_REQ:
                // and read new one
                final Uri uri = data.getData();
                /*
                 * The URI returned from application may be in 'file' or 'content' schema. 'File' schema allows us to create a File object and read details from if
                 * directly. Data from 'Content' schema must be read by Content Provider. To do that we are using a Loader.
                 */

                if (uri.getScheme().equals("file") || uri.getScheme().equals("content")) {
                    // the direct path to the file has been returned
                  //  final String path = uri.getPath();
                    String path = GetFilePathFromUri.getFileAbsolutePath(this, uri);
                    filePath = path;
                    tvFilePath.setText(path);
                    DataUtils.getInstance().saveFilePath(path);
                }
                 break;
            case  SELECT_IMAGE_REQ:
                final Uri image_url = data.getData();
                photoInputPath = GetFilePathFromUri.getFileAbsolutePath(this, image_url);
                tvPhoto.setText(photoInputPath);
                Bitmap wallPaper = BitmapFactory.decodeFile(photoInputPath);
                if(mScreenInfo != null && mScreenInfo.width>0){//Crop the watch screen size picture
                    ImageUtil.saveWallPaper(Bitmap.createScaledBitmap(wallPaper, mScreenInfo.width
                            ,  mScreenInfo.height, true), photoInputPath_crop);
                }else {
                    Toast.makeText(this,"please CALL BLEManager.getWatchPlateScreenInfo();",Toast.LENGTH_LONG).show();
                }
                break;
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
            mScreenInfo = screenInfo;
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
        if (LocalDataManager.getSupportFunctionInfo().V3_get_watch_list_new) {  //根据功能表盘判断，调用不同的方法
            BLEManager.getDialPlateParam();
        } else {
            BLEManager.getWatchPlateList();
        }
    }

    public void getScreenInfo(View view){
        BLEManager.getWatchPlateScreenInfo();
    }

    public void getCurrentPlate(View view){
        BLEManager.getCurrentWatchPlate();
    }


    public void deletePlate(View view){
        //the name must contain .iwf ,example "watch.iwf"
        BLEManager.deleteWatchPlate(tvUniqueID.getText().toString());
    }


    public void setPlate(View view){
        BLEManager.setWatchPlate(tvUniqueID.getText().toString());
    }

    public void selectImage(View view){
        openImageChooser();
    }

    /**
     * 设置壁纸表盘
     */
    public void setPhotoWatch(View view){
       if(TextUtils.isEmpty(photoInputPath_crop)){
           Toast.makeText(this,"please select imamge",Toast.LENGTH_LONG).show();
           return;
       }
        String outfile = LogPathImpl.getInstance().getPicPath()+"output.png";
        Wallpaper wallpaper = new Wallpaper();
        wallpaper.setFileName(photoInputPath_crop);// 裁剪的图片存储的地方
        wallpaper.setSaveFileName(outfile);
        wallpaper.setFormat(5);
        String json = GsonUtil.toJson(wallpaper);
        BLEManager.setParaToDeviceByTypeAndJson(5500, json);//请求制作照片
        Log.e("ddd",outfile);
        //制作完成，传输壁纸文件到设备
        FileTransferConfig config1 = FileTransferConfig.getDefaultTransPictureConfig(outfile+".lz", new IFileTransferListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onProgress(int progress) {
                Log.e("ddd",progress+"watch file");
            }

            @Override
            public void onSuccess() {
                Log.e("ddd","success");
            }

            @Override
            public void onFailed(String errorMsg) {

            }
        });
        BLEManager.startTranCommonFile(config1);
    }
}
