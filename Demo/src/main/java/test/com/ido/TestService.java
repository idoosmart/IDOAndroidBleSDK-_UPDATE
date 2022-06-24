package test.com.ido;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class TestService extends Service{
//    private BroadcastReceiver mBluetoothStatusReceiver = new BroadcastReceiver() {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
//                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
//                if (state == BluetoothAdapter.STATE_OFF) {
//                    LogTool.p(ConnectConstants.LOG_TGA, "bluetooth switch is turn off");
////                    ConnectManager.getManager().bluetoothSwitchStateChanged(false);
//                    ConnectStateHelper.getHelper().onPhoneBlueToothSwitchClosed();
//                } else if (state == BluetoothAdapter.STATE_ON) {
//                    LogTool.p(ConnectConstants.LOG_TGA, "bluetooth switch is turn on");
////                    ConnectManager.getManager().bluetoothSwitchStateChanged(true);
//                    ConnectStateHelper.getHelper().onPhoneBlueToothSwitchOpen();
//                }
//            }
//        }
//    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
//        registerReceiver(mBluetoothStatusReceiver, filter);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager == null) {
            return;
        }
//        NotificationChannel channel = new NotificationChannel(getPackageName(), "Test", NotificationManager.IMPORTANCE_DEFAULT);
//        channel.setSound(null, null);
//        manager.createNotificationChannel(channel);
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getPackageName());
//        builder.setChannelId(getPackageName());
//        builder.setContentText("TEST");
//        builder.setContentTitle("DEMO");
//        builder.setSmallIcon(R.drawable.ic_stat_notify_dfu);
//        manager.notify(1, builder.build());
        startForeground(1, getNotification("Start", 1));
    }

    private void update(int i){
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager == null) {
            return;
        }

        manager.notify(1,getNotification("", i));

    }

    private Notification getNotification(String title, int progress) {
        Intent intent = new Intent(this, HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,getPackageName());
        builder.setContentTitle(title);
        builder.setContentText(title + progress);
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.drawable.ido_bg);
        if (progress >= 0) {
            // 当progress 大于0时才需要显示下载进度
            builder.setContentText(progress + "%");
            builder.setProgress(100, progress, false);
        }
        return builder.build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (true){
                    i ++;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    update(i);
                }
            }
        }).start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        unregisterReceiver(mBluetoothStatusReceiver);
    }

}
