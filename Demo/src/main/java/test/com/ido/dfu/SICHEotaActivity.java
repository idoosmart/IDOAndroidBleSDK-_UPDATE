package test.com.ido.dfu;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ido.ble.BLEManager;
import com.ido.ble.LocalDataManager;
import com.ido.ble.bluetooth.device.BLEDevice;
import com.ido.ble.dfu.BleDFUConfig;
import com.ido.ble.dfu.BleDFUState;
import com.ido.ble.protocol.model.BasicInfo;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import test.com.ido.R;
import test.com.ido.utils.DataUtils;
import test.com.ido.utils.GetFilePathFromUri;

public class SICHEotaActivity extends Activity {
    private static String TAG = "dfu_siche";
    private String macAddress, deviceId;
    private String filePath;
    private TextView tvError, tvFilePath, tvLog, tvLostTime, tvLiveLostTime, progress_tv;
    private Button btnStartUpgrade;
    private ProgressBar progressBar, ingProgressBar;
    private ScrollView scrollView;
    private CheckBox cbxAutoTestMode;
    boolean isDfu = false;
    private Timer timer;

    private long dfuStartTime = 0;
    private int mRetryTimes = 0;

    private final String TEST_MAC = "F7:19:3E:91:D8:15";

    private BleDFUState.IListener iListener = new BleDFUState.IListener() {
        @Override
        public void onPrepare() {
            setTitle("prepare...");
            ingProgressBar.setVisibility(View.VISIBLE);
            tvError.setText("");
            progressBar.setProgress(0);
            dfuStartTime = System.currentTimeMillis();
        }

        @Override
        public void onDeviceInDFUMode() {

        }

        @Override
        public void onProgress(int progress) {
            progressBar.setProgress(progress);
            setTitle(progress + "%");
            progress_tv.setText(progress+"%");
            Log.e(TAG,"progress: "+progress);
        }

        @Override
        public void onSuccess() {
            setTitle("success");
            Log.e(TAG,"success: ");
            ingProgressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onSuccessAndNeedToPromptUser() {


            ingProgressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onFailed(BleDFUState.FailReason failReason) {
            setTitle("failed");
            tvError.setText(failReason + "");
            BLEManager.removeDFUStateListener(iListener);


            ingProgressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onCanceled() {
            setTitle("canceled");
            tvError.setText("canceled");
            BLEManager.removeDFUStateListener(iListener);

        }

        @Override
        public void onRetry(int count) {
            mRetryTimes = count;
        }
    };


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        BLEDevice device = LocalDataManager.getCurrentDeviceInfo();
        isDfu = getIntent().getBooleanExtra("dfu",false);
        if(device!=null){
            macAddress = device.mDeviceAddress;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rtk_dfu);
        Log.e(TAG,"device: "+macAddress);
    /*    macAddress = getIntent().getStringExtra("mac_address");
        deviceId = getIntent().getStringExtra("device_id");*/
        filePath = DataUtils.getInstance().getDfuFilePath();
        tvError = findViewById(R.id.dfu_error_tv);
        tvFilePath = findViewById(R.id.dfu_file_path_tv);
        tvLostTime = findViewById(R.id.dfu_lost_time_tv);
        tvLiveLostTime = findViewById(R.id.live_lost_time_tv);
        btnStartUpgrade = findViewById(R.id.btn_start_upgrade);
        tvLog = findViewById(R.id.dfu_log_tv);
        progressBar = findViewById(R.id.dfu_progressBar);
        ingProgressBar = findViewById(R.id.dfu_ing_progressBar);
        scrollView = findViewById(R.id.duf_result_scroll);
        cbxAutoTestMode = findViewById(R.id.dfu_is_auto_test_cbx);

        if (TextUtils.isEmpty(filePath)){
            tvFilePath.setText("请先选择固件包(.zip)");
        }else {
            tvFilePath.setText(filePath);
        }

        tvFilePath.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                scrollView.setVisibility(View.VISIBLE);
                return true;
            }
        });

        btnStartUpgrade.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });

/*

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (dfuStartTime != 0) {
                            tvLiveLostTime.setText("耗时:" + (System.currentTimeMillis() - dfuStartTime) /1000);
                        }
                    }
                });
            }
        }, 0, 1000);

        LogOutput.enableSelf();
        LogOutput.setLocalLogOutputListener(new LogOutput.LogListener() {
            @Override
            public void onLog(final String log) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        tvLog.append(log);
                        if (tvLog.getText().length() > 10000){
                            tvLog.setText(log);
                        }
                        btnStartUpgrade.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                            }
                        }, 50);

                    }
                });
            }
        });
*/

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(timer!=null){
            timer.cancel();
            timer = null;

        }
    }

    public void startUpgrade(View view){
        Log.e(TAG,"startUpgrade: ");
        tvError.setText("start");
        upgrade_siche();
    }

   private void upgrade_siche(){
       BLEManager.addDFUStateListener(iListener);
       macAddress = LocalDataManager.getCurrentDeviceInfo().mDeviceAddress;
       BasicInfo basicInfo = LocalDataManager.getBasicInfo();
       BLEManager.startDFU(new BleDFUConfig().setFilePath(filePath)
               .setMacAddress(macAddress).setDeviceId("11212").setIsDfu(isDfu).setPlatform(basicInfo.platform));
   }
/*
   private void regitsterDfuLocalBroadcast(){
       IntentFilter intentFilter = new IntentFilter();
       intentFilter.addAction(SifliDFUService.BROADCAST_DFU_LOG);
       intentFilter.addAction(SifliDFUService.BROADCAST_DFU_PROGRESS);
       intentFilter.addAction(SifliDFUService.BROADCAST_DFU_STATE);
       LocalBroadcastManager.getInstance(getApplication()).registerReceiver(new LocalBroadcastReceiver(),intentFilter);
   }

   class LocalBroadcastReceiver extends BroadcastReceiver{

       @Override
       public void onReceive(Context context, Intent intent) {
           String action = intent.getAction();
           switch (action){
               case SifliDFUService.BROADCAST_DFU_PROGRESS:
                   int progress = intent.getIntExtra(SifliDFUService.EXTRA_DFU_PROGRESS,0);
                   int type = intent.getIntExtra(SifliDFUService.EXTRA_DFU_PROGRESS_TYPE,0);
                   Log.e(TAG,"progress: "+progress);
                   progress_tv.setText(progress+"%");
                   Log.e(TAG,"type: "+type);
                   progressBar.setProgress(progress);
                   break;
               case SifliDFUService.BROADCAST_DFU_LOG:
                   String dfulog = intent.getStringExtra(SifliDFUService.EXTRA_LOG_MESSAGE);
                   Log.e(TAG,"dfulog: "+dfulog);
                   break;
               case SifliDFUService.BROADCAST_DFU_STATE:
                   int dfuState = intent.getIntExtra(SifliDFUService.EXTRA_DFU_STATE,0);
                   int dfuStateResult = intent.getIntExtra(SifliDFUService.EXTRA_DFU_STATE_RESULT,0);
                   Log.e(TAG,"dfuState: "+dfuState);
                   Log.e(TAG,"dfuStateResult: "+dfuStateResult);
                   tvError.setText("result:"+dfuStateResult);
                   if(dfuState==100){
                       tvError.setText("result:success");
                   }
           }
       }
   }*/


    public void cancel(View view){
//        BLEManager.disConnect();

    }

    public void disConnect(View view){
    }

    public void selectFile(View view){
        openFileChooser();
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
                    String path = GetFilePathFromUri.getFileAbsolutePath(this, uri);
                    filePath = path;
                    tvFilePath.setText(path);
                    Log.e(TAG,"image path: "+path);
                  //  initUpdatefile(path);
                    DataUtils.getInstance().saveDfuFilePath(path);
                  /*  String folder_path = path.substring(0,path.lastIndexOf("/")+1);
                    Log.e(TAG,"image ctrl path folder: "+folder_path+"ctrl_packet.bin");
                    DataUtils.getInstance().saveDfuFilePath(path);*/

                }
            }

        }
    }

    private void initUpdatefile(String path) {
      /*  if(!filePath.contains(".zip")){
            Log.e(TAG,"需要选择压缩包文件 ");
            return;
        }
        String folder_path = path.substring(0, path.lastIndexOf("/")+1)+"updatefolder/";
        File forlderFile = new File(folder_path);
        if(!forlderFile.exists()){
            forlderFile.mkdirs();
        }
        boolean isunzip = unpackCopyZip(folder_path,filePath);
        Log.e(TAG,"forlderFile path: "+folder_path +"---isunzip:"+isunzip);
        paths.clear();
        //  File forlderFile = new File(folder_path);
        if(forlderFile.exists()){
            Log.e(TAG,"forlderFile exist: "+ path +"--list file:"+forlderFile.listFiles());
      //  if(forlderFile.isDirectory()){
             for (File file:forlderFile.listFiles()){
                 Log.e(TAG,"file path: "+file.getPath());
                 filePath = file.getPath();
                 int id = 0;
                 if(filePath.contains(".zip")){
                     id = Protocol.IMAGE_ID_RES;
                 }else if(filePath.contains("ctrl_packet.bin")){
                     id = Protocol.IMAGE_ID_CTRL;
                 }
                 else if(filePath.contains("outER_IROM1.bin")){
                     id = Protocol.IMAGE_ID_HCPU;
                 }
                 else if(filePath.contains("outlcpu_flash.bin")){
                     id = Protocol.IMAGE_ID_LCPU;
                 }
                 else if(filePath.contains("outlcpu_rom_patch.bin")){
                     id = Protocol.IMAGE_ID_NAND_LCPU_PATCH;
                 }
                 else if(filePath.contains("outroot.bin")){
                     id= Protocol.IMAGE_ID_RES;
                 }
                 Log.e(TAG,"image type: "+id);
                 Uri uri_file = null;
                 if(Build.VERSION.SDK_INT>=24){
                     uri_file =  FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID+".provider",file);
                 }else {
                     uri_file = Uri.fromFile(file);
                 }
                 //  DFUImagePath ctrlPath = new DFUImagePath("",Uri.fromFile(new File(folder_path)), Protocol.IMAGE_ID_CTRL);
                 DFUImagePath imagePath = new DFUImagePath("",uri_file, id);
                 //  paths.clear();
                 // paths.add(ctrlPath);
                 paths.add(imagePath);
             }
        }*/
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, fileUri)) {
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

    /**
     * 解压Zip文件
     *
     * @param desPath 解压目的地目录
     * @param zipFile zip文件名(包含去路径)
     * @return true 表示解压成功,false 表示解压失败.
     * modify: google play console 提示的zip路径遍历漏洞修复,去除zipEntry.getName中的”../“
     */
    public static boolean unpackCopyZip(String desPath, String zipFile) {
        if (!zipFile.endsWith(".zip")) {
            return false;
        }
        File des = new File(desPath);
        if (!des.exists()) {
            des.mkdirs();
        }
        InputStream is = null;
        ZipInputStream zis = null;
        FileOutputStream fout = null;
        try {
            String filename;
            is = new FileInputStream(zipFile);
            zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry ze;
            byte[] buffer = new byte[1024];
            int count;

            while ((ze = zis.getNextEntry()) != null) {
                String zeName = ze.getName().replace("../", "");
                if (zeName.contains(File.separator)) {
                    filename = zeName.substring(0, zeName.lastIndexOf(File.separator));
                    File file = new File(desPath + File.separator + filename);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                } else {
                    filename = zeName;
                }

                // Need to create directories if not exists, or
                // it will generate an Exception...
                if (ze.isDirectory()) {
                    File fmd = new File(desPath + ze);
                    fmd.mkdirs();
                    continue;
                }

                fout = new FileOutputStream(desPath + zeName);

                while ((count = zis.read(buffer)) != -1) {
                    fout.write(buffer, 0, count);
                }

                fout.close();
                zis.closeEntry();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            close(is);
            close(zis);
            close(fout);
        }

        return true;
    }

    /**
     * 关闭IO.可用于任何IO类的close操作
     * @param closeable
     */
    public static void close(Closeable closeable){
        if(closeable != null){
            try {
                closeable.close();
                closeable = null;
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
