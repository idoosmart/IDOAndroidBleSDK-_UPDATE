package test.com.ido.music;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;

import com.realsil.sdk.core.utility.PermissionUtil;

import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;
import test.com.ido.log.LogPathImpl;

public class MusicActivity extends BaseAutoConnectActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
    }

    public void btMusicManager(View view) {
        startActivity(new Intent(this, MusicManagerActivity.class));
    }

    public void btMusicControl(View view) {
        startActivity(new Intent(this, MusicControlActivity.class));
    }

}