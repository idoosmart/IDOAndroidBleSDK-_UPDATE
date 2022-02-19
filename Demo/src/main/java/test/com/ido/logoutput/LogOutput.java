package test.com.ido.logoutput;

import android.app.Application;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.StrictMode;
import android.widget.Toast;

import com.ido.ble.BLEManager;
import com.ido.ble.logs.LogTool;
import com.ido.logoutput.ILogAidlInterface;

import java.io.File;

import test.com.ido.logoutput.bluetooth.BluetoothLogoutManager;
import test.com.ido.utils.Common;
import test.com.ido.utils.Constant;

/**
 * @author: zhouzj
 * @date: 2017/11/14 19:34
 */

public class LogOutput {

    private static LogListener mLocalLogOutputListener;
    public interface LogListener{
        void onLog(String log);
    }

    public static void setLocalLogOutputListener(LogListener logListener){
        mLocalLogOutputListener = logListener;
    }

    private static ILogAidlInterface iLogAidlInterface;
    private static LogTool.LogListener logListener = new LogTool.LogListener() {
        @Override
        public void onLog(String log) {
            if (iLogAidlInterface != null){
                try {
                    iLogAidlInterface.onLog(log);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            if (mLocalLogOutputListener != null){
                mLocalLogOutputListener.onLog(log);
            }

            BluetoothLogoutManager.write(log);
        }
    };

    private static ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iLogAidlInterface = ILogAidlInterface.Stub.asInterface(service);

            Toast.makeText(mApplication.getApplicationContext(), "开启成功，请在本手机打开“LogOutput”应用", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    public static void enableSelf(){
        BLEManager.setLogListener(logListener);
    }
    public static void enableSelf(Context context, ComponentName componentName){
        BLEManager.setLogListener(logListener);
        Intent intent = new Intent(context, LogOutputActivity.class);
        intent.putExtra("className", componentName.getClassName());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    private static Application mApplication;
    public static void enableService(Application application){
        BLEManager.setLogListener(logListener);
        Intent intent = new Intent();
        intent.setClassName("com.ido.logoutput","com.ido.logoutput.LogService");
        application.bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE);
        mApplication = application;
    }
    public static void enableBluetooth(Context context){
        BLEManager.setLogListener(logListener);
        BluetoothLogoutManager.getManager().start();
        Toast.makeText(context, "开启成功，请在另一台手机上打开“LogOutput”应用", Toast.LENGTH_LONG).show();
    }

    public static void installTool(Context context){
        if (Common.copy(context, Constant.TOOL_APK_NAME, Constant.LOG_OUTPUT_TOOL_PATH, Constant.TOOL_APK_NAME)){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
            }

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(Constant.LOG_OUTPUT_TOOL_PATH + Constant.TOOL_APK_NAME)),
                    "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
        }
    }

    public static void shareTool(Context context){
        if (!Common.copy(context, Constant.TOOL_APK_NAME, Constant.LOG_OUTPUT_TOOL_PATH, Constant.TOOL_APK_NAME)){
            return;
        }

        Uri uri = Uri.fromFile(new File(Constant.LOG_OUTPUT_TOOL_PATH + Constant.TOOL_APK_NAME));
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享日志输出工具");

        // 设置弹出框标题
//        if (dlgTitle != null && !"".equals(dlgTitle)) { // 自定义标题
            context.startActivity(Intent.createChooser(intent, "分享日志输出工具"));
//        } else { // 系统默认标题
//            startActivity(intent);
//        }
    }


}
