package test.com.ido.gps;

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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ido.ble.BLEManager;
import com.ido.ble.gps.agps.AgpsFileTransConfig;
import com.ido.ble.gps.agps.IAGpsTranslateStateListener;

import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;

public class AGpsAutoTranslateActivity extends BaseAutoConnectActivity {

    private TextView tvState, tvFilePath;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agps_auto_translate);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        tvState = (TextView) findViewById(R.id.state_tv);
        tvFilePath = (TextView) findViewById(R.id.file_path_tv);

        downLoadAGpsFile();

        tvFilePath.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                openFileChooser();
                return true;
            }
        });
    }

    private void downLoadAGpsFile() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                // 检查是否有网络
//                if (!NetWorkUtil.isNetWorkConnected(getApplicationContext())) {
//                    return;
//                }
//
//                OkHttpUtil.getInstance().checkAgpsFile(new IOkHttpCallBack<String>() {
//                    @Override
//                    public void success(String s) {
//                        String dir = Environment.getExternalStorageDirectory() + File.separator + "veryfit2.2" + File.separator + "agps";
//                        String fileNameList = "cep_pak.bin";
//                        File file = new File(dir, fileNameList);
//                        String checkCode = AppSharedPreferencesUtils.getInstance().getApsCheckCode();
//                        if (!TextUtils.isEmpty(s) && s.equals(checkCode) && file.exists()) {
//                            showMsg("校验AGPS文件未更新");
//                            return;
//                        }
//
//                        // 保存校验码
//                        AppSharedPreferencesUtils.getInstance().setApsCheckCode(s);
//                        // 下载Agps文件
//                        OkHttpUtil.getInstance().downLoadAgpsFile(dir, fileNameList, new IOkHttpCallBack<String>() {
//                            @Override
//                            public void success(String s) {
//                                showMsg("下载Agps文件成功 , " + s);
//                            }
//
//                            @Override
//                            public void fail(AGException e) {
//                                showMsg("下载Agps文件失败 , " + e.getMessage());
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void fail(AGException e) {
//                        showMsg("校验AGPS文件失败 , " + e.getMessage());
//                    }
//                });
//            }
//        }).start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        BLEManager.stopTranAgpsFile();
    }

    public void startTranslate(View view) {
        String path = tvFilePath.getText().toString();
        if (TextUtils.isEmpty(path)) {
            return;
        }
        AgpsFileTransConfig config = new AgpsFileTransConfig();
        config.PRN = 10;
        config.filePath = path;
        config.fileType = AgpsFileTransConfig.FILE_TYPE_LANG;
        config.listener = new IAGpsTranslateStateListener() {
            @Override
            public void onStart() {
                tvState.setText("start...");
            }

            @Override
            public void onProgress(int progress) {
                tvState.setText(progress + "%");
                progressBar.setProgress(progress);
            }

            @Override
            public void onSuccess() {
                tvState.setText("success");
            }

            @Override
            public void onFailed(String errorMsg) {
                tvState.setText(errorMsg);
            }
        };
        BLEManager.startTranAgpsFile(config);
//        BLEManager.startTranAgpsFile(path, new IAGpsTranslateStateListener() {
//            @Override
//            public void onStart() {
//                tvState.setText("start...");
//            }
//
//            @Override
//            public void onProgress(int progress) {
//                tvState.setText(progress + "%");
//                progressBar.setProgress(progress);
//            }
//
//            @Override
//            public void onSuccess() {
//                tvState.setText("success");
//            }
//
//            @Override
//            public void onFailed(String errorMsg) {
//                tvState.setText(errorMsg);
//            }
//        });
    }

    private static final int SELECT_FILE_REQ = 1;

    private void openFileChooser() {
        final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/bin");
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
            /*
             * The URI returned from application may be in 'file' or 'content' schema. 'File' schema allows us to create a File object and read details from if
			 * directly. Data from 'Content' schema must be read by Content Provider. To do that we are using a Loader.
			 */
                Uri uri = data.getData();
                String path = getFileAbsolutePath(this, uri);
                if (!TextUtils.isEmpty(path)) {
                    tvFilePath.setText(path);
                }
            }

        }
    }

    private void showMsg(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
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
