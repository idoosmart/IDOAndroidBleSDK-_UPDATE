package test.com.ido.music;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ido.ble.BLEManager;
import com.ido.ble.LocalDataManager;
import com.ido.ble.callback.SettingCallBack;
import com.ido.ble.protocol.model.SupportFunctionInfo;

import java.util.ArrayList;
import java.util.List;

import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;
import test.com.ido.utils.DataUtils;

public class MusicControlActivity extends BaseAutoConnectActivity {

    Switch mSwitch;
    Switch mNameSwitch;
    RecyclerView listview;
    MusicAdapter musicAdapter;

    MusicService musicService;
    List<MusicService.ControllerMonitor> monitors = new ArrayList<>();

    PackageManager pm;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BLEManager.unregisterSettingCallBack(mSettingCallback);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pm = getPackageManager();
        setContentView(R.layout.activity_music_control);
        listview = findViewById(R.id.listview);
        mSwitch = findViewById(R.id.music_control_switch);
        mNameSwitch = findViewById(R.id.music_name_switch);
        musicAdapter = new MusicAdapter();
        listview.setAdapter(musicAdapter);
        BLEManager.registerSettingCallBack(mSettingCallback);
        SupportFunctionInfo func = LocalDataManager.getSupportFunctionInfo();
        if (func != null && func.V3_support_set_v3_music_name) {
            mNameSwitch.setVisibility(View.VISIBLE);
        } else {
            mNameSwitch.setVisibility(View.GONE);
        }
        boolean musicNameSwitch = DataUtils.getInstance().getMusicNameSwitch();
        mNameSwitch.setChecked(musicNameSwitch);
        startService();
        mNameSwitch.setChecked(musicNameSwitch && isNotificationEnabled());
        mNameSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (isNotificationEnabled()) {
                    startService();
                } else {
                    mNameSwitch.setChecked(false);
                    AlertDialog dialog = new AlertDialog.Builder(MusicControlActivity.this)
                            .setTitle(R.string.music_control_tips)
                            .setMessage(String.format(getResources().getString(R.string.music_control_msg), getResources().getString(R.string.app_name)))
                            .setPositiveButton(R.string.confirm, (dialog1, which) -> {
                                jump2SettingActivity();
                            })
                            .setNegativeButton(R.string.cancel, (dialog12, which) -> {
                                dialog12.dismiss();
                            })
                            .create();
                    dialog.show();
                }
            }
            DataUtils.getInstance().saveMusicNameSwitch(isChecked);
        });
        mSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                startService();
            }
            BLEManager.setMusicSwitch(isChecked);
        });
        boolean musicSwitch = LocalDataManager.getMusicSwitch();
        mSwitch.setChecked(musicSwitch);
    }

    private class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.VH> {
        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music_player, null, false));
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            MusicService.ControllerMonitor monitor = monitors.get(position);
            try {
                ApplicationInfo app = pm.getApplicationInfo(monitor.getPkg(), PackageManager.GET_META_DATA);
                holder.tvPlayerName.setText(pm.getApplicationLabel(app));
                holder.ivIcon.setImageDrawable(pm.getApplicationIcon(app));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            setMusicInfo(holder, monitor);
            holder.btNext.setOnClickListener(v -> monitor.next());
            holder.btPre.setOnClickListener(v -> monitor.previous());
            holder.btPause.setOnClickListener(v -> {
                if (monitor.getPlayState() != PlaybackState.STATE_PLAYING) {
                    monitor.play();
                } else {
                    monitor.pause();
                }
            });
            monitor.musicCallback = () -> {
                setMusicInfo(holder, monitor);
            };
        }

        private void setMusicInfo(@NonNull VH holder, MusicService.ControllerMonitor monitor) {
            holder.tvMusicName.setText(monitor.getMusicName() + " - " + monitor.getMusicArtist());
            String state = "";
            switch (monitor.getPlayState()) {
                case PlaybackState.STATE_PLAYING:
                    state = getResources().getString(R.string.music_pause);
                    break;
                case PlaybackState.STATE_BUFFERING:
                    state = getResources().getString(R.string.music_buffering);
                    break;
                case PlaybackState.STATE_PAUSED:
                case PlaybackState.STATE_NONE:
                case PlaybackState.STATE_STOPPED:
                    state = getResources().getString(R.string.music_play);
                    break;
            }
            holder.btPause.setEnabled(monitor.getPlayState() != PlaybackState.STATE_BUFFERING);
            holder.btPause.setText(state);
            holder.tvMusicProgress.setText(monitor.getCurrentPosition() + "/" + monitor.getMusicDuration());
            holder.ivMusicIcon.setImageBitmap(monitor.getMusicBitmap());
        }

        @Override
        public int getItemCount() {
            return monitors.size();
        }

        class VH extends RecyclerView.ViewHolder {
            ImageView ivIcon;
            ImageView ivMusicIcon;
            TextView tvPlayerName;
            TextView tvMusicName;
            TextView tvMusicProgress;
            Button btPre;
            Button btPause;
            Button btNext;

            public VH(@NonNull View itemView) {
                super(itemView);
                ivIcon = itemView.findViewById(R.id.ivIcon);
                ivMusicIcon = itemView.findViewById(R.id.ivMusicIcon);
                tvPlayerName = itemView.findViewById(R.id.tvPlayerName);
                tvMusicName = itemView.findViewById(R.id.tvMusicName);
                tvMusicProgress = itemView.findViewById(R.id.tvMusicProgress);
                btPre = itemView.findViewById(R.id.btPre);
                btPause = itemView.findViewById(R.id.btPause);
                btNext = itemView.findViewById(R.id.btNext);
            }
        }
    }

    private final SettingCallBack.ICallBack mSettingCallback = new SettingCallBack.ICallBack() {
        @Override
        public void onSuccess(SettingCallBack.SettingType settingType, Object o) {
            if (settingType == SettingCallBack.SettingType.MUSIC_SWITCH) {

            } else if (settingType == SettingCallBack.SettingType.MUSIC_CONTROL_INFO) {

            }
        }

        @Override
        public void onFailed(SettingCallBack.SettingType settingType) {
            if (settingType == SettingCallBack.SettingType.MUSIC_SWITCH) {

            } else if (settingType == SettingCallBack.SettingType.MUSIC_CONTROL_INFO) {

            }
        }
    };

    private MusicService.MusicSessionChangeCallback musicCallback = () -> {
        Log.d("MusicService", "onSessionsChanged: " + musicService.monitorMap.keySet());
        refresh();
    };

    private void refresh() {
        monitors.clear();
        for (String pkg : musicService.monitorMap.keySet()) {
            monitors.add(musicService.monitorMap.get(pkg));
        }
        musicAdapter.notifyDataSetChanged();
    }

    private void startService() {
        Intent intent = new Intent(this, MusicService.class);
        startService(intent);
        bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                musicService = ((MusicService.MusicBinder) service).getService();
                musicService.registerMusicCallback(musicCallback);
                refresh();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                monitors.clear();
                musicAdapter.notifyDataSetChanged();
                musicService.unregisterMusicCallback();
                musicService = null;
            }
        }, Context.BIND_AUTO_CREATE);
    }


    public boolean isNotificationEnabled() {
        String flat = Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
        if (!TextUtils.isEmpty(flat)) {
            return flat.contains(getPackageName());
        } else {
            return false;
        }
    }

    /**
     * 跳转到设置界面
     */
    private void jump2SettingActivity() {
        try {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivityForResult(intent, 1000);
        } catch (ActivityNotFoundException e) {
        }
    }
}