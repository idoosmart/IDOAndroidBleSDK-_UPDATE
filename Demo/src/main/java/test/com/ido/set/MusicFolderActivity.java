package test.com.ido.set;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ido.ble.BLEManager;
import com.ido.ble.callback.OperateCallBack;
import com.ido.ble.protocol.model.MusicOperate;


import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;

public class MusicFolderActivity extends BaseAutoConnectActivity {

    private TextView tvInfo;
    private EditText etFolderId,etFolderName, etMusicId, etMusicName, etMusicFolderFlag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_folder);

        tvInfo = findViewById(R.id.info_tv);

        etMusicId = findViewById(R.id.music_id_et);
        etMusicName = findViewById(R.id.music_name_et);
        etMusicFolderFlag = findViewById(R.id.music_folder_flag_et);

        etFolderId = findViewById(R.id.folder_id_et);
        etFolderName = findViewById(R.id.folder_name_et);
        BLEManager.registerOperateCallBack(new OperateCallBack.ICallBack() {
            @Override
            public void onSetResult(OperateCallBack.OperateType type, boolean isSuccess) {
                tvInfo.setText(isSuccess ? "成功":"失败");
            }

            @Override
            public void onAddResult(OperateCallBack.OperateType type, boolean isSuccess) {
                tvInfo.setText(isSuccess ? "成功":"失败");
            }

            @Override
            public void onDeleteResult(OperateCallBack.OperateType type, boolean isSuccess) {
                tvInfo.setText(isSuccess ? "成功":"失败");
            }

            @Override
            public void onModifyResult(OperateCallBack.OperateType type, boolean isSuccess) {
                tvInfo.setText(isSuccess ? "成功":"失败");
            }

            @Override
            public void onQueryResult(OperateCallBack.OperateType type, Object returnData) {
                if (type == OperateCallBack.OperateType.MUSIC_AND_FOLDER){
                    MusicOperate.MusicAndFolderInfo info = (MusicOperate.MusicAndFolderInfo) returnData;
                    if (info != null){
                        tvInfo.setText(info.toString());
                    }
                }
            }
        });


    }

    public void queryInfo(View view){
        BLEManager.queryMusicAndFolderInfo();
    }

    public void  addFolder(View view){
        MusicOperate.MusicFolder musicFolder = new MusicOperate.MusicFolder();
        if (!TextUtils.isEmpty(etFolderId.getText().toString())) {
            musicFolder.folder_id = Integer.parseInt(etFolderId.getText().toString());
        }
        musicFolder.folder_name = etFolderName.getText().toString();
        BLEManager.addMusicFolder(musicFolder);
    }

    public void  deleteFolder(View view){
        MusicOperate.MusicFolder musicFolder = new MusicOperate.MusicFolder();
        if (!TextUtils.isEmpty(etFolderId.getText().toString())) {
            musicFolder.folder_id = Integer.parseInt(etFolderId.getText().toString());
        }
        musicFolder.folder_name = etFolderName.getText().toString();
        BLEManager.deleteMusicFolder(musicFolder);
    }

    public void  updateFolder(View view){
        MusicOperate.MusicFolder musicFolder = new MusicOperate.MusicFolder();
        if (!TextUtils.isEmpty(etFolderId.getText().toString())) {
            musicFolder.folder_id = Integer.parseInt(etFolderId.getText().toString());
        }
        musicFolder.folder_name = etFolderName.getText().toString();
        BLEManager.updateMusicFolder(musicFolder);
    }

    public void  addMusic(View view){
        MusicOperate.MusicFile musicFile = new MusicOperate.MusicFile();
        if (!TextUtils.isEmpty(etMusicId.getText().toString())) {
            musicFile.music_id = Integer.parseInt(etMusicId.getText().toString());
        }
        musicFile.music_name = etMusicName.getText().toString();
        BLEManager.addMusicFile(musicFile);
    }

    public void  deleteMusic(View view){
        MusicOperate.MusicFile musicFile = new MusicOperate.MusicFile();
        if (!TextUtils.isEmpty(etMusicId.getText().toString())) {
            musicFile.music_id = Integer.parseInt(etMusicId.getText().toString());
        }
        musicFile.music_name = etMusicName.getText().toString();
        BLEManager.deleteMusicFile(musicFile);
    }

    public void attachMusicAndFolder(View view){
        MusicOperate.MusicFile musicFile = new MusicOperate.MusicFile();
        if (!TextUtils.isEmpty(etMusicId.getText().toString())) {
            musicFile.music_id = Integer.parseInt(etMusicId.getText().toString());
        }
        musicFile.music_name = etMusicName.getText().toString();

        MusicOperate.MusicFolder musicFolder = new MusicOperate.MusicFolder();
        if (!TextUtils.isEmpty(etFolderId.getText().toString())) {
            musicFolder.folder_id = Integer.parseInt(etFolderId.getText().toString());
        }
        musicFolder.folder_name = etFolderName.getText().toString();

        BLEManager.moveMusicIntoFolder(musicFolder);//, musicFile
    }

    public void removeMusicFromFolder(View view){
        MusicOperate.MusicFile musicFile = new MusicOperate.MusicFile();
        if (!TextUtils.isEmpty(etMusicId.getText().toString())) {
            musicFile.music_id = Integer.parseInt(etMusicId.getText().toString());
        }
        musicFile.music_name = etMusicName.getText().toString();

        MusicOperate.MusicFolder musicFolder = new MusicOperate.MusicFolder();
        if (!TextUtils.isEmpty(etFolderId.getText().toString())) {
            musicFolder.folder_id = Integer.parseInt(etFolderId.getText().toString());
        }
        musicFolder.folder_name = etFolderName.getText().toString();

        BLEManager.removeMusicFromFolder(musicFolder);//, musicFile
    }

}