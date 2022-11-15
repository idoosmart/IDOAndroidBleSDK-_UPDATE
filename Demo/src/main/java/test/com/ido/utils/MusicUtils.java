package test.com.ido.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import test.com.ido.APP;
import test.com.ido.model.MusicModel;

/**
 * @author tianwei
 * @date 2022/11/11
 * @time 15:42
 * 用途:
 */
public class MusicUtils {

    public static List<MusicModel> getLocalMusic() {
        ContentResolver resolver = APP.getAppContext().getContentResolver();
        List<MusicModel> musicModeList = new ArrayList<>();
        if (resolver == null) {
            return musicModeList;
        }
        String[] projection = new String[]{
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.IS_MUSIC,
                MediaStore.Audio.Media.DURATION
        };
        Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            MusicModel musicModel = new MusicModel();
            musicModel.path = cursor.getString(0);
            String sizeStr = cursor.getString(1);
            if (!TextUtils.isEmpty(sizeStr)) {
                musicModel.size = Long.parseLong(sizeStr);
            }
            musicModel.name = FileUtil.getFileNameFromPath(musicModel.path);
            String signer = cursor.getString(4);
            if (!TextUtils.isEmpty(signer)) {
                if ("<unknown>".equals(signer)) {
                    signer = "";
                    musicModel.singer = signer;
                }
            }
            musicModel.album = cursor.getString(5);

            boolean isMusic = false;
            if (!TextUtils.isEmpty(cursor.getString(6))) {
                isMusic = Integer.parseInt(cursor.getString(6)) != 0;
            }
            String durationStr = cursor.getString(7);
            if (!TextUtils.isEmpty(durationStr)) {
                musicModel.duration = Long.parseLong(durationStr);
            }
            if (isMusic && musicModel.path.toLowerCase().endsWith(".mp3")) {
                if (isMusic) {
                    musicModeList.add(musicModel);
                }
            }
        }
        return musicModeList;
    }
}