package test.com.ido.file.transfer;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ido.ble.BLEManager;
import com.ido.ble.file.transfer.FileTransferConfig;
import com.ido.ble.file.transfer.IFileTransferListener;
import com.ido.ble.file.transfer.spp.SPPFileTransferConfig;
import com.ido.ble.icon.transfer.IIconTransferListener;
import com.ido.ble.icon.transfer.IconTranConfig;

import java.io.File;

import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;
import test.com.ido.utils.DataUtils;
import test.com.ido.utils.GetFilePathFromUri;

public class FileTransferActivity extends BaseAutoConnectActivity {

    private EditText etPRN, etSpecName,etZipType, dataType, etIconType, etIconValue;
    private TextView tvFilePath, tvProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_transfer);

        etPRN = findViewById(R.id.transfer_file_prn);
        etSpecName = findViewById(R.id.transfer_file_spec_name);
        tvFilePath = findViewById(R.id.transfer_file_file_path);
        tvProgress = findViewById(R.id.transfer_file_progress);
        etZipType = findViewById(R.id.transfer_file_zip_type);
        dataType = findViewById(R.id.transfer_data_type);
        etIconType = findViewById(R.id.transfer_icon_type);
        etIconValue = findViewById(R.id.transfer_icon_value);

        String path = DataUtils.getInstance().getFilePath();
        if (!TextUtils.isEmpty(path)) {
            tvFilePath.setText(path);
            File file = new File(path);
//            etSpecName.setText("." + file.getName().split("\\.")[1]);
            etSpecName.setText(file.getName());
        }

    }

    public void stopTransFile(View view){
        BLEManager.stopTranCommonFile();
    }

    public void startTransFile(View view){

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
        config.PRN = Integer.parseInt(etPRN.getText().toString());
        config.filePath = tvFilePath.getText().toString();
        config.firmwareSpecName = etSpecName.getText().toString();
        config.zipType = Integer.parseInt(etZipType.getText().toString());
        config.dataType = Integer.parseInt(dataType.getText().toString());
        config.maxRetryTimes = 0;
        config.isNeedChangeSpeedMode = false;
        BLEManager.startTranCommonFile(config);
    }

    public void stopSPPTransFile(View view){
        BLEManager.stopSppTranFile();
    }
    public void startSPPTransFile(View view){
        SPPFileTransferConfig config = new SPPFileTransferConfig();
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
        config.PRN = Integer.parseInt(etPRN.getText().toString());
        config.filePath = tvFilePath.getText().toString();
        //config.firmwareSpecName = ".mp3";
        config.firmwareSpecName = etSpecName.getText().toString();
        config.zipType = Integer.parseInt(etZipType.getText().toString());
        config.dataType = Integer.parseInt(dataType.getText().toString());
        config.maxRetryTimes = 0;
        BLEManager.startSppTranFile(config);
    }

    public void startTransICON(View view){
        IconTranConfig config = new IconTranConfig();
        config.type = Integer.parseInt(etIconType.getText().toString());
        config.index = Integer.parseInt(etIconValue.getText().toString());
        IIconTransferListener listener = new IIconTransferListener() {
            @Override
            public void onStart(IconTranConfig config) {
                tvProgress.setText("start");
            }

            @Override
            public void onProgress(IconTranConfig config, int progress) {
                tvProgress.setText("progress=" + progress);
            }

            @Override
            public void onSuccess(IconTranConfig config) {
                tvProgress.setText("success");
            }

            @Override
            public void onFailed(IconTranConfig config) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvProgress.setText("failed");
                    }
                });
            }

            @Override
            public void onBusy(IconTranConfig config) {
                tvProgress.setText("busy");
            }

            @Override
            public String onHandlePicFile(IconTranConfig config, int width, int height) {
                return tvFilePath.getText().toString();
            }
        };

        BLEManager.startTranIcon(config, listener);
    }

    public void selectFile(View view){
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
                // and read new one
                /*
                 * The URI returned from application may be in 'file' or 'content' schema. 'File' schema allows us to create a File object and read details from if
                 * directly. Data from 'Content' schema must be read by Content Provider. To do that we are using a Loader.
                 */
                Uri uri = data.getData();
//                String path = getFileAbsolutePath(this, uri);
                String path = GetFilePathFromUri.getFileAbsolutePath(this, uri);
                if (!TextUtils.isEmpty(path)) {
                    tvFilePath.setText(path);
                    File file = new File(path);
//                    etSpecName.setText("." + file.getName().split("\\.")[1]);
                    etSpecName.setText(file.getName());
                    DataUtils.getInstance().saveFilePath(path);
                }
            }

        }
    }

    /**
     * 根据Uri获取文件的绝对路径，解决Android4.4以上版本Uri转换
     *
     * @param context
     * @param fileUri
     */
    @TargetApi(19)
    private String getFileAbsolutePath(Activity context, Uri fileUri) {
        if (context == null || fileUri == null)
            return null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, fileUri)) {
            if (isExternalStorageDocument(fileUri)) {
                String docId = DocumentsContract.getDocumentId(fileUri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(fileUri)) {
                String id = DocumentsContract.getDocumentId(fileUri);
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(fileUri)) {
                String docId = DocumentsContract.getDocumentId(fileUri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } // MediaStore (and general)
        else if ("content".equalsIgnoreCase(fileUri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(fileUri))
                return fileUri.getLastPathSegment();
            return getDataColumn(context, fileUri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(fileUri.getScheme())) {
            return fileUri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}
