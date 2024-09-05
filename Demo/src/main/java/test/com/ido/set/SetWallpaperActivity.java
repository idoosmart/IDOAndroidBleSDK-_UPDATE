package test.com.ido.set;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ido.ble.BLEManager;
import com.ido.ble.callback.SettingCallBack;
import com.ido.ble.file.transfer.FileTransferConfig;
import com.ido.ble.file.transfer.IFileTransferListener;
import com.ido.ble.protocol.model.SetListStyle;

import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;
import test.com.ido.log.LogPathImpl;
import test.com.ido.utils.DataUtils;
import test.com.ido.utils.FileUtil;
import test.com.ido.utils.GetFilePathFromUri;
import test.com.ido.utils.ImageUtil;

public class SetWallpaperActivity extends BaseAutoConnectActivity {
    private static final int SELECT_FILE_REQ = 1;
    private static final int SELECT_IMAGE_REQ = 2;
    private String filePath;
    private String photoInputPath = "";
    private String photoInputPath_crop = LogPathImpl.getInstance().getPicPath() + "alwpaper.png";
    private String photoInputPath_compressed = LogPathImpl.getInstance().getPicPath() + "compressed_image.png";
    private String photoInputPath_bin = LogPathImpl.getInstance().getPicPath() + "alwpaper.bin";

    private TextView tvProgress;
    private TextView wallpaper_response;
    private EditText imagPath, wallpaper_color;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_wallpaper);
        imagPath = findViewById(R.id.wallpaper_ed);
        tvProgress = findViewById(R.id.wallpaper_progress);
        wallpaper_response = findViewById(R.id.wallpaper_response);
        wallpaper_color = findViewById(R.id.wallpaper_color);
        BLEManager.registerSettingCallBack(new SettingCallBack.ICallBack() {
            @Override
            public void onSuccess(SettingCallBack.SettingType settingType, Object o) {
                wallpaper_response.setText("success");
            }

            @Override
            public void onFailed(SettingCallBack.SettingType settingType) {
                wallpaper_response.setText("failed");

            }
        });
    }

    public void selectImage(View view) {
        openImageChooser(SELECT_IMAGE_REQ);
    }

    private void openImageChooser(int code) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"),
                    code);
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, code);
        }
    }


    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
                    DataUtils.getInstance().saveFilePath(path);
                }
                break;
            case SELECT_IMAGE_REQ:
                final Uri image_url = data.getData();
                BLEManager.getWatchPlateScreenInfo();//获取屏幕信息
                photoInputPath = GetFilePathFromUri.getFileAbsolutePath(this, image_url);
                Bitmap wallPaper = BitmapFactory.decodeFile(photoInputPath);
                imagPath.setText(photoInputPath_crop);
                //320x385
                ImageUtil.saveWallPaper(Bitmap.createScaledBitmap(wallPaper, 320, 385, true), photoInputPath_crop);
                FileUtil.savePngByBin(photoInputPath_crop, photoInputPath_bin);
                break;
        }
    }

    public void setWallpaper(View view) {

        FileTransferConfig config = new FileTransferConfig();
        config.iFileTransferListener = new IFileTransferListener() {
            @Override
            public void onStart() {
                tvProgress.setText("start");
            }

            @Override
            public void onProgress(int progress) {
                tvProgress.setText("" + progress);
            }

            @Override
            public void onSuccess() {
                tvProgress.setText("success");
            }

            @Override
            public void onFailed(String errorMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvProgress.setText(errorMsg);
                    }
                });

            }
        };

        config.PRN = Integer.parseInt("10");
        config.filePath = photoInputPath_bin;
        config.firmwareSpecName = "alwpaper.bin";
        config.dataType = 255;
        config.zipType = 0;
        config.maxRetryTimes = 0;
        config.isNeedChangeSpeedMode = false;
        BLEManager.startTranCommonFile(config);
    }

    public void setWallpaperColor(View view) {
        String s = wallpaper_color.getText().toString();
        if (s.isEmpty()) {
            Toast.makeText(this, "请输入颜色值", Toast.LENGTH_SHORT).show();
            return;
        }
        s = s.replace("x", "0");
        s = s.replace("X", "0");
        int intValue = Integer.valueOf(s, 16);
        SetListStyle setListStyle = new SetListStyle();
        setListStyle.font_color = intValue;
//        BLEManager.setListColor(setListStyle);
        //16776960  16777215
    }
}