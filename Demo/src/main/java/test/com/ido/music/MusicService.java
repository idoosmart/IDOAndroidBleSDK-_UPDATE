package test.com.ido.music;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaMetadata;
import android.media.MediaMetadataEditor;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ido.ble.BLEManager;
import com.ido.ble.LocalDataManager;
import com.ido.ble.bluetooth.connect.ConnectFailedReason;
import com.ido.ble.callback.DeviceControlAppCallBack;
import com.ido.ble.protocol.model.MusicControlInfo;
import com.ido.ble.protocol.model.PhoneVoice;
import com.ido.ble.protocol.model.SupportFunctionInfo;

import java.util.HashMap;
import java.util.List;

import test.com.ido.CallBack.BaseConnCallback;
import test.com.ido.CallBack.BaseDeviceControlAppCallBack;

public class MusicService extends NotificationListenerService implements MediaSessionManager.OnActiveSessionsChangedListener {
    private static final String TAG = "MusicService";
    MediaSessionManager sessionManager;
    public final HashMap<String, ControllerMonitor> monitorMap = new HashMap<>();

    private MusicBinder binder = new MusicBinder();

    private MusicSessionChangeCallback musicCallback;
    private static AudioManager mAudioManager;
    private VolumeBroadcastReceiver volumeBroadcastReceiver;

    public void registerMusicCallback(MusicSessionChangeCallback callback) {
        musicCallback = callback;
    }

    public void unregisterMusicCallback() {
        musicCallback = null;
    }

    public MusicService() {
    }

    BaseDeviceControlAppCallBack mCallBack = new BaseDeviceControlAppCallBack() {
        @Override
        public void onControlEvent(DeviceControlAppCallBack.DeviceControlEventType eventType, int var2) {
            Log.d(TAG, "onControlEvent:" + eventType.toString() + ", var2 = " + var2);
            if (isSupportMusicControl()) {
                switch (eventType) {
                    case START:
                        Log.d(TAG, "receive startMusic");
                        startMusic();
                        break;
                    case PAUSE:
                        Log.d(TAG, "receive pauseMusic");
                        stopMusic();
                        break;
                    case STOP:
                        stopMusic();
                        break;
                    case PREVIOUS:
                        preMusic();
                        break;
                    case NEXT:
                        nextMusic();
                        break;
                    case VOLUME_UP:
                        volumeUp();
                        break;
                    case VOLUME_DOWN:
                        volumeDown();
                        break;
                    case VOLUME_PERCENTAGE:
                        setVolume(var2);
                        break;
                }
            } else {
                Log.d(TAG, "不支持音乐控制或控制开关未开启");
            }
        }
    };

    void setVolume(int level) {
        int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mAudioManager.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                (int) ((level / 100f) * max),
                AudioManager.FLAG_SHOW_UI
        );
    }

    /**
     * 通过点按方式控制音量加减（解决oppo机型app切换后台无法改变音量大小）
     *
     * @param ratio 音量百分比
     */
    private void tapControlVolume(int ratio) {
        //1.获取当前音量和最大音量
        //2.根据百分比计算设置的目标音量
        //3.循环触发音量按键，并检测一次按键音量的调节值，然后根据每次按键调节的音量值修正触发次数
        int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        // 需要调节音量值
        int result = (int) ((ratio / 100f) * max);
        // 需要点按次数
        int count = result - current;
        // 判断音量加减
        boolean isUp = count > 0;
        Log.d(TAG, "音乐最大音量：" + max + ", 当前音量值：" + current + ", 设置音量百分比：" + ratio + "");
        for (int i = 0; i < Math.abs(count); i++) {
            if (isUp) {
                // 控制音量增
                mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
            } else {
                // 控制音量减
                mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
            }
        }
    }

    private void volumeDown() {
        Log.d(TAG, "receive volumeDown");
        sendMusicKeyDownEvent(KeyEvent.KEYCODE_VOLUME_DOWN);
    }

    private void volumeUp() {
        Log.d(TAG, "receive volumeUp");
        sendMusicKeyDownEvent(KeyEvent.KEYCODE_VOLUME_UP);
    }

    private void nextMusic() {
        Log.d(TAG, "receive nextMusic");
        if (!mAudioManager.isMusicActive()) {
            sendMusicKeyEvent(KeyEvent.KEYCODE_MEDIA_PLAY);
        }
        sendMusicKeyEvent(KeyEvent.KEYCODE_MEDIA_NEXT);
    }

    public boolean sendMusicKeyDownEvent(int keyCode) {
        long eventTime = SystemClock.uptimeMillis() - 1;
        KeyEvent key = new KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN,
                keyCode, 0);
        dispatchMediaKeyToAudioService(key, keyCode);
//        dispatchMediaKeyToAudioService(KeyEvent.changeAction(key, KeyEvent.ACTION_UP), keyCode);
        return false;
    }

    private void startMusic() {
        Log.d(TAG, "receive startMusic: " + mAudioManager.isMusicActive());
        // if (!mAudioManager.isMusicActive()) { //有时候暂停了这个 也为true,去掉这个限制
        Log.d(TAG, "startMusic");
        //TODO 测试发现在oppo reno android 11上存在兼容问题，KEYCODE_MEDIA_PLAY与KEYCODE_MEDIA_PAUSE值均为127
//            sendMusicKeyEvent(KeyEvent.KEYCODE_MEDIA_PLAY);
        sendMusicKeyEvent(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
        //  }
    }

    private void stopMusic() {
        Log.d(TAG, "receive stopMusic：" + mAudioManager.isMusicActive());
        if (mAudioManager.isMusicActive()) {
            Log.d(TAG, "stopMusic：");
            sendMusicKeyEvent(KeyEvent.KEYCODE_MEDIA_PAUSE);
        }
    }

    private void preMusic() {
        Log.d(TAG, "receive preMusic");
        if (!mAudioManager.isMusicActive()) {
            sendMusicKeyEvent(KeyEvent.KEYCODE_MEDIA_PLAY);
        }
        sendMusicKeyEvent(KeyEvent.KEYCODE_MEDIA_PREVIOUS);
    }

    public boolean sendMusicKeyEvent(int keyCode) {
        long eventTime = SystemClock.uptimeMillis() - 1;
        KeyEvent key = new KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN,
                keyCode, 0);
        dispatchMediaKeyToAudioService(key, keyCode);
        dispatchMediaKeyToAudioService(KeyEvent.changeAction(key, KeyEvent.ACTION_UP), keyCode);
        return false;
    }

    private void dispatchMediaKeyToAudioService(KeyEvent event, int keyCode) {
        Log.d(TAG, "dispatchMediaKeyToAudioService keyCode：" + keyCode);

        if (mAudioManager == null) {
            Log.d(TAG, "dispatchMediaKeyToAudioService, mAudioManager is null");
            return;
        }

        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            // 控制音量增
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                    AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            // 控制音量减
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                    AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
        } else {
            try {
                mAudioManager.dispatchMediaKeyEvent(event);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "dispatchMediaKeyEvent Exception：" + e.toString());
            }
        }
    }

    private static boolean isSupportMusicControl() {
        SupportFunctionInfo functionInfo = LocalDataManager.getSupportFunctionInfo();
        return functionInfo != null && functionInfo.bleControlMusic && LocalDataManager.getMusicSwitch();
    }


    BaseConnCallback baseConnCallback = new BaseConnCallback() {
        @Override
        public void onConnectSuccess(String macAddress) {
            super.onConnectSuccess(macAddress);
            VolumeBroadcastReceiver.sendVoice2Device();
        }
    };

    private static class VolumeBroadcastReceiver extends BroadcastReceiver {
        private static final String VOLUME_CHANGED_ACTION = "android.media.VOLUME_CHANGED_ACTION";
        private static final String EXTRA_VOLUME_STREAM_TYPE = "android.media.EXTRA_VOLUME_STREAM_TYPE";
        private static int sLastVolume = -1;
        private Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                sendVoice2Device();
            }
        };

        public VolumeBroadcastReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            //媒体音量改变才通知
            if (VOLUME_CHANGED_ACTION.equals(intent.getAction())
                    && (intent.getIntExtra(EXTRA_VOLUME_STREAM_TYPE, -1) == AudioManager.STREAM_MUSIC)) {
                Log.d(TAG, "VolumeBroadcastReceiver 监听到手机媒体音量变化");
                //500毫秒防抖动
                handler.removeMessages(1);
                handler.sendEmptyMessageDelayed(1, 500);
            }
        }

        /**
         * 设置音量到设备
         */
        public static void sendVoice2Device() {
            SupportFunctionInfo functionInfo = LocalDataManager.getSupportFunctionInfo();
            //不支持音量设置
            if (functionInfo == null || !functionInfo.ex_table_main10_set_phone_voice) {
                return;
            }

            if (!BLEManager.isConnected()) {
                Log.d(TAG, "sendVoice2Device 设备未连接，不发送音量");
                return;
            }

            if (!isSupportMusicControl()) {
                Log.d(TAG, "不支持音乐控制或音乐开关未开启");
                return;
            }

            try {
                //当前音量
                int volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                if (sLastVolume == volume) {
                    Log.d(TAG, "sendVoice2Device 与上一次音量相同，不设置");
                    return;
                }
                PhoneVoice phoneVoice = new PhoneVoice();
                //最大音量
                phoneVoice.total_voice = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                phoneVoice.now_voice = volume;
                Log.d(TAG, "sendVoice2Device 音量设置 ：" + phoneVoice.toString());
                BLEManager.setPhoneVoice(phoneVoice);
//                sLastVolume = volume;
            } catch (Exception e) {
                Log.d(TAG, "sendVoice2Device 音量设置异常 ：" + e.toString());
            }
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
        sessionManager = (MediaSessionManager) getSystemService(MEDIA_SESSION_SERVICE);
        if (mAudioManager == null) {
            mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        }
        BLEManager.registerDeviceControlAppCallBack(mCallBack);
        BLEManager.registerConnectCallBack(baseConnCallback);
        IntentFilter filter = new IntentFilter();
        filter.addAction(VolumeBroadcastReceiver.VOLUME_CHANGED_ACTION);
        volumeBroadcastReceiver = new VolumeBroadcastReceiver();
        registerReceiver(volumeBroadcastReceiver, filter);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BLEManager.unregisterDeviceControlAppCallBack(mCallBack);
        unregisterReceiver(volumeBroadcastReceiver);
        BLEManager.unregisterConnectCallBack(baseConnCallback);
    }

    class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ComponentName notificationListener = new ComponentName(this, MusicService.class);
        sessionManager.addOnActiveSessionsChangedListener(this, notificationListener);
        Log.d(TAG, "onStartCommand, controllers = " + sessionManager.getActiveSessions(notificationListener));
        onActiveSessionsChanged(sessionManager.getActiveSessions(notificationListener));
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onActiveSessionsChanged(@Nullable List<MediaController> controllers) {
        Log.d(TAG, "onActiveSessionsChanged: " + controllers);
        for (String key : monitorMap.keySet()) {
            ControllerMonitor monitor = monitorMap.get(key);
            if (monitor != null) {
                monitor.destroy();
            }
        }
        monitorMap.clear();
        if (controllers != null && controllers.size() > 0) {
            for (MediaController controller : controllers) {
                Log.d(TAG, "pkg: " + controller.getPackageName());
                ControllerMonitor monitor = new ControllerMonitor(controller);
                monitor.run();
                monitorMap.put(controller.getPackageName(), monitor);
            }
        } else {
            Log.d(TAG, "音乐播放器全部关闭");
        }
        if (musicCallback != null) {
            musicCallback.onSessionsChanged();
        }
    }

    private static void logD(String msg) {
        Log.d(TAG, msg);
    }

    private static void logE(String msg) {
        Log.e(TAG, msg);
    }

    public interface MusicSessionChangeCallback {
        void onSessionsChanged();
    }

    public interface MusicCallback {
        void onMusicChanged();
    }

    public static class ControllerMonitor {
        private final MediaController mController;
        MusicCallback musicCallback;
        MediaMetadata metadata;
        PlaybackState state;
        private MediaController.Callback mControllerCallback = new MediaController.Callback() {
            @Override
            public void onSessionDestroyed() {
                logD("onSessionDestroyed. Enter q to quit.");
                sendMusic(PlaybackState.STATE_PAUSED);
            }

            @Override
            public void onSessionEvent(String event, Bundle extras) {
                logD("onSessionEvent event=" + event + ", extras=" + extras);
            }

            @Override
            public void onPlaybackStateChanged(PlaybackState state) {
                logD("onPlaybackStateChanged " + state);
                ControllerMonitor.this.state = state;
                if (musicCallback != null) {
                    musicCallback.onMusicChanged();
                }
                sendMusic();
            }

            @Override
            public void onMetadataChanged(MediaMetadata metadata) {
                String mmString = metadata == null ? null : "title=" + metadata.getDescription();
                logD("onMetadataChanged " + mmString);
                ControllerMonitor.this.metadata = metadata;
                if (musicCallback != null) {
                    musicCallback.onMusicChanged();
                }
                sendMusic();
            }

            @Override
            public void onQueueChanged(List<MediaSession.QueueItem> queue) {
                logD("onQueueChanged, " + (queue == null ? "null queue" : " size=" + queue.size()));
            }

            @Override
            public void onQueueTitleChanged(CharSequence title) {
                logD("onQueueTitleChange " + title);
            }

            @Override
            public void onExtrasChanged(Bundle extras) {
                logD("onExtrasChanged " + extras);
            }

            @Override
            public void onAudioInfoChanged(MediaController.PlaybackInfo info) {
                logD("onAudioInfoChanged " + info);
            }
        };

        private void sendMusic() {
            sendMusic(getPlayState());
        }

        private void sendMusic(int state) {
            MusicControlInfo musicControlInfo = new MusicControlInfo();
            musicControlInfo.musicName = getMusicName();
            musicControlInfo.singerName = getMusicArtist();
            musicControlInfo.curTimeSecond = (int) getCurrentPosition();
            musicControlInfo.totalTimeSecond = (int) getMusicDuration();
            musicControlInfo.status = getPlayState();
            BLEManager.setMusicControlInfo(musicControlInfo);
        }

        ControllerMonitor(MediaController controller) {
            mController = controller;
            mController.registerCallback(mControllerCallback);
            this.metadata = controller.getMetadata();
            this.state = controller.getPlaybackState();
        }

        private void destroy() {
            mController.unregisterCallback(mControllerCallback);
            mControllerCallback = null;
        }

        public long getMusicDuration() {
            return (long) (metadata.getLong(MediaMetadata.METADATA_KEY_DURATION) / 1000f);
        }

        public String getMusicArtist() {
            return metadata.getString(MediaMetadata.METADATA_KEY_ARTIST);
        }

        public String getMusicAlbum() {
            return metadata.getString(MediaMetadata.METADATA_KEY_ALBUM);
        }

        public String getMusicName() {
            return metadata.getString(MediaMetadata.METADATA_KEY_TITLE);
        }

        public String getMusicDesc() {
            return metadata.getDescription().getDescription().toString();
        }

        public Bitmap getMusicBitmap() {
            if (metadata.containsKey(MediaMetadata.METADATA_KEY_ART)) {
                return metadata.getBitmap(MediaMetadata.METADATA_KEY_ART);
            } else if (metadata.containsKey(MediaMetadata.METADATA_KEY_ALBUM_ART)) {
                return metadata.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART);
            }
            return null;
        }

        public int getPlayState() {
            if (state == null) {
                return PlaybackState.STATE_NONE;
            }
            return state.getState();
        }

        public long getCurrentPosition() {
            return (long) (state.getPosition() / 1000f);
        }

        public String getPkg() {
            return mController.getPackageName();
        }

        private void printUsageMessage() {
            try {
                logD("V2Monitoring session " + mController.getPackageName() + "...  available commands: play, pause, next, previous");
            } catch (RuntimeException e) {
                logD("Error trying to monitor session!");
            }
            logD("(q)uit: finish monitoring");
        }

        public void play() {
            dispatchKeyCode(KeyEvent.KEYCODE_MEDIA_PLAY);
        }

        public void pause() {
            dispatchKeyCode(KeyEvent.KEYCODE_MEDIA_PAUSE);
        }

        public void next() {
            dispatchKeyCode(KeyEvent.KEYCODE_MEDIA_NEXT);
        }

        public void previous() {
            dispatchKeyCode(KeyEvent.KEYCODE_MEDIA_PREVIOUS);
        }

        private void run() {
            printUsageMessage();
        }

        private void dispatchKeyCode(int keyCode) {
            final long now = SystemClock.uptimeMillis();
            KeyEvent down = new KeyEvent(now, now, KeyEvent.ACTION_DOWN, keyCode, 0, 0, KeyCharacterMap.VIRTUAL_KEYBOARD, 0, 0, InputDevice.SOURCE_KEYBOARD);
            KeyEvent up = new KeyEvent(now, now, KeyEvent.ACTION_UP, keyCode, 0, 0, KeyCharacterMap.VIRTUAL_KEYBOARD, 0, 0, InputDevice.SOURCE_KEYBOARD);
            try {
                mController.dispatchMediaButtonEvent(down);
                mController.dispatchMediaButtonEvent(up);
            } catch (RuntimeException e) {
                logE("Failed to dispatch " + keyCode);
            }
        }
    }
}