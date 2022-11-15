package test.com.ido.set;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.ido.ble.BLEManager;
import com.ido.ble.callback.OperateCallBack;
import com.ido.ble.file.transfer.IFileTransferListener;
import com.ido.ble.file.transfer.spp.SPPFileTransferConfig;
import com.ido.ble.protocol.model.MusicOperate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import test.com.ido.CallBack.BaseOperateCallback;
import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;
import test.com.ido.utils.DataUtils;
import test.com.ido.utils.FileUtil;
import test.com.ido.utils.GetFilePathFromUri;
import test.com.ido.utils.GsonUtil;
import test.com.ido.utils.MusicUtils;

public class MusicManagerActivity extends BaseAutoConnectActivity {
    private static final String TAG = "MusicManagerActivity";

    EditText etResult;
    EditText etSheetName;
    EditText etSheetId;
    EditText etMusicPath;
    EditText etMusicName;
    EditText etMusicId;
    EditText etAddMusicId;
    EditText etAddSheetId;
    Spinner sp;
    CheckBox cbAddMusic2Sheet;

    MusicOperate.MusicAndFolderInfo musicAndFolderInfo;
    private int selectMusicFolderPosition = 0;

    //设置中的歌曲信息
    private String musicPath;
    private String musicName;
    private int musicId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_manager);
        cbAddMusic2Sheet = findViewById(R.id.cbAddMusic2Sheet);
        etResult = findViewById(R.id.etResult);
        etSheetName = findViewById(R.id.etSheetName);
        etAddMusicId = findViewById(R.id.etAddMusicId);
        etAddSheetId = findViewById(R.id.etAddSheetId);
        etSheetId = findViewById(R.id.etSheetId);
        etMusicPath = findViewById(R.id.etMusicPath);
        etMusicName = findViewById(R.id.etMusicName);
        etMusicId = findViewById(R.id.etMusicId);
        sp = findViewById(R.id.sp);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectMusicFolderPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        cbAddMusic2Sheet.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (musicAndFolderInfo == null) {
                    showToast("请先点击”查询歌曲信息“");
                    cbAddMusic2Sheet.setChecked(false);
                } else if (musicAndFolderInfo.folder_num == 0) {
                    showToast("您还没有创建歌单");
                    cbAddMusic2Sheet.setChecked(false);
                }
            }
        });
        BLEManager.registerOperateMusicCallBack(callBack);
        BLEManager.registerOperateCallBack(queryBack);
        String musicPath = DataUtils.getInstance().getMusicPath();
        if (!TextUtils.isEmpty(musicPath)) {
            etMusicPath.setText(musicPath);
        }
        String musicName = DataUtils.getInstance().getMusicName();
        if (!TextUtils.isEmpty(musicName)) {
            etMusicName.setText(musicName);
        }
        etResult.setText("start query music...");
        BLEManager.queryMusicAndFolderInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BLEManager.unregisterOperateMusicCallBack(callBack);
        BLEManager.unregisterOperateCallBack(queryBack);
    }

    private OperateCallBack.IMusicCallBack callBack = new OperateCallBack.IMusicCallBack() {
        @Override
        public void onInvalid(OperateCallBack.OperateType operateType, boolean b) {

        }

        @Override
        public void onDeleteMusic(OperateCallBack.OperateType operateType, boolean b) {
            showToast(b ? "歌曲删除成功！" : "歌曲删除失败！");
            refresh();
        }

        @Override
        public void onAddMusic(OperateCallBack.OperateType operateType, boolean b, int i) {
            showToast(b && i > 0 ? "歌曲添加成功！" : "歌曲添加失败！");
            if (b && i > 0) {
//            BLEManager.queryMusicAndFolderInfo();
                musicId = i;
                transferMusicFile();
            } else {
                refresh();
            }
        }

        @Override
        public void onDeleteFolder(OperateCallBack.OperateType operateType, boolean b) {
            showToast(b ? "歌单删除成功！" : "歌单删除失败！");
            refresh();
        }

        @Override
        public void onAddFolder(OperateCallBack.OperateType operateType, boolean b) {
            showToast(b ? "歌单创建成功！" : "歌单创建失败！");
            refresh();
        }

        @Override
        public void onModifyFolder(OperateCallBack.OperateType operateType, boolean b) {
            showToast(b ? "歌单编辑成功！" : "歌单编辑失败！");
            refresh();
        }

        @Override
        public void onImportFolder(OperateCallBack.OperateType operateType, boolean b) {
            showToast(b ? "歌曲导入成功！" : "歌曲导入失败！");
            refresh();
        }

        @Override
        public void onDeleteFolderMusic(OperateCallBack.OperateType operateType, boolean b) {
            showToast(b ? "删除成功！" : "删除失败！");
            refresh();
        }
    };

    private void refresh() {
        closeProgressDialog();
        BLEManager.queryMusicAndFolderInfo();
    }

    private OperateCallBack.ICallBack queryBack = new BaseOperateCallback() {

        @Override
        public void onQueryResult(OperateCallBack.OperateType operateType, Object o) {
            if (operateType == OperateCallBack.OperateType.MUSIC_AND_FOLDER) {
                MusicOperate.MusicAndFolderInfo info = (MusicOperate.MusicAndFolderInfo) o;
                musicAndFolderInfo = info;
                etResult.setText(GsonUtil.toJson(info));
                List<MusicOperate.MusicFolder> list = musicAndFolderInfo.folder_items;
                List<String> names = new ArrayList<>();
                if (list != null) {
                    for (MusicOperate.MusicFolder folder : list) {
                        names.add(folder.folder_name + " (id: " + folder.folder_id + ")");
                    }
                }
                selectMusicFolderPosition = 0;
                cbAddMusic2Sheet.setChecked(false);
                sp.setAdapter(new ArrayAdapter<>(MusicManagerActivity.this, R.layout.item_music_folder, names));
            }
        }
    };

    private void transferMusicFile() {
        Log.d(TAG, "transferMusicFile, musicPath = " + musicPath + ", musicName = " + musicName);
        if (TextUtils.isEmpty(musicPath) || TextUtils.isEmpty(musicName)) {
            showToast("传输歌曲信息为空!");
            closeProgressDialog();
            return;
        }
        progressDialog.setTitle("正在传输歌曲...");
        SPPFileTransferConfig config = SPPFileTransferConfig.getDefaultMusicFileConfig(musicPath, new IFileTransferListener() {
            @Override
            public void onStart() {
                Log.d(TAG, "transferMusicFile onStart");
            }

            @Override
            public void onProgress(int i) {
                Log.d(TAG, "transferMusicFile onProgress: " + i);
                if (!progressDialog.isShowing()) {
                    showProgressDialog("正在传输歌曲...");
                }
            }

            @Override
            public void onSuccess() {
                Log.d(TAG, "transferMusicFile onSuccess");
                showToast("歌曲文件传输完成！");
                Log.d(TAG, "isChecked = " + cbAddMusic2Sheet.isChecked() + ", musicId = " + musicId + ", musicAndFolderInfo = " + musicAndFolderInfo + ", selectMusicFolderPosition = " + selectMusicFolderPosition);
                //添加歌曲到歌单
                if (cbAddMusic2Sheet.isChecked() && musicId > 0 && musicAndFolderInfo != null && musicAndFolderInfo.folder_num > 0 && selectMusicFolderPosition >= 0) {
                    MusicOperate.MusicFolder folder = musicAndFolderInfo.folder_items.get(selectMusicFolderPosition);
                    if (folder.music_index == null) {
                        folder.music_index = new ArrayList<>();
                    }
                    folder.music_num++;
                    folder.music_index.add(musicId);
                    BLEManager.moveMusicIntoFolder(folder);
                    Log.d(TAG, "moveMusicIntoFolder");
                } else {
                    refresh();
                }
            }

            @Override
            public void onFailed(String s) {
                Log.d(TAG, "transferMusicFile onFailed: " + s);
                showToast("歌曲文件传输失败，请确认BT蓝牙是否连接正常！");
                refresh();
            }
        });
        config.firmwareSpecName = musicName;
        BLEManager.startSppTranFile(config);
    }

    public void btQueryMusic(View view) {
        BLEManager.queryMusicAndFolderInfo();
        etResult.setText("start query music...");
    }

    public void btNewSheet(View view) {
        String name = etSheetName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            showToast("请输入歌单名称");
            etSheetName.requestFocus();
            return;
        }
        MusicOperate.MusicFolder folder = new MusicOperate.MusicFolder();
        folder.folder_id = getNewSheetId();
        folder.folder_name = name;
        BLEManager.addMusicFolder(folder);
        showProgressDialog("Add...");
    }

    private int getNewSheetId() {
        if (musicAndFolderInfo == null || musicAndFolderInfo.folder_num == 0) {
            return 1;
        }
        int id = 0;
        for (MusicOperate.MusicFolder folder : musicAndFolderInfo.folder_items) {
            id = Math.max(id, folder.folder_id);
        }
        return id + 1;
    }

    public void btDeleteSheet(View view) {
        String id = etSheetId.getText().toString().trim();
        if (TextUtils.isEmpty(id)) {
            showToast("请输入歌单ID");
            etSheetId.requestFocus();
            return;
        } else if (!TextUtils.isDigitsOnly(id)) {
            showToast("歌单ID只能是数字，参考歌曲信息！");
            etSheetId.requestFocus();
            return;
        }

        if (musicAndFolderInfo == null) {
            showToast("请先查下歌曲信息！");
            return;
        }
        boolean exist = false;
        String folderName = "";
        if (musicAndFolderInfo.folder_items != null) {
            for (MusicOperate.MusicFolder folder : musicAndFolderInfo.folder_items) {
                if (Integer.parseInt(id) == folder.folder_id) {
                    exist = true;
                    folderName = folder.folder_name;
                    break;
                }
            }
        }
        if (!exist) {
            showToast("歌单不存在！");
            return;
        }
        MusicOperate.MusicFolder folder = new MusicOperate.MusicFolder();
        folder.folder_id = Integer.parseInt(id);
        folder.folder_name = folderName;
        BLEManager.deleteMusicFolder(folder);
        showProgressDialog("Delete....");
    }

    public void btSelectMusicFile(View view) {
        openFileChooser();
    }

    public void btAddMusic(View view) {
        String path = etMusicPath.getText().toString().trim();
        if (TextUtils.isEmpty(path)) {
            showToast("请选择歌曲文件");
            etMusicPath.requestFocus();
            return;
        }

        String name = etMusicName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            showToast("请输入歌曲名称");
            etMusicName.requestFocus();
            return;
        } else if (!name.toLowerCase(Locale.ROOT).endsWith(".mp3")) {
            showToast("歌曲文件只支持MP3");
            etMusicName.requestFocus();
            return;
        }

        File file = new File(path);
        if (!file.exists()) {
            showToast("歌曲文件不存在！");
            return;
        }
        musicPath = path;
        musicName = name;
        MusicOperate.MusicFile musicFile = new MusicOperate.MusicFile();
        musicFile.music_memory = file.length();
        musicFile.music_name = name;
        musicFile.singer_name = "";
        BLEManager.addMusicFile(musicFile);
        showProgressDialog("Add...");
    }

    public void btDeleteMusic(View view) {
        String id = etMusicId.getText().toString().trim();
        if (TextUtils.isEmpty(id)) {
            showToast("请输入歌曲ID");
            etMusicId.requestFocus();
            return;
        } else if (!TextUtils.isDigitsOnly(id)) {
            showToast("歌曲ID只能是数字，参考歌曲信息！");
            etMusicId.requestFocus();
            return;
        }
        MusicOperate.MusicFile musicFile = new MusicOperate.MusicFile();
        musicFile.music_id = Integer.parseInt(id);
        BLEManager.deleteMusicFile(musicFile);
        showProgressDialog("Delete...");
    }

    public void btAddMusic2Folder(View view) {
        if (musicAndFolderInfo.folder_num == 0) {
            showToast("请先创建歌单！");
            return;
        }
        String musicId = etAddMusicId.getText().toString().trim();
        if (TextUtils.isEmpty(musicId)) {
            showToast("请输入歌曲ID");
            etAddMusicId.requestFocus();
            return;
        } else if (!TextUtils.isDigitsOnly(musicId)) {
            showToast("歌曲ID只能是数字，参考歌曲信息！");
            etAddMusicId.requestFocus();
            return;
        }

        String folderId = etAddSheetId.getText().toString().trim();
        if (TextUtils.isEmpty(folderId)) {
            showToast("请输入歌单ID");
            etAddSheetId.requestFocus();
            return;
        } else if (!TextUtils.isDigitsOnly(folderId)) {
            showToast("歌单ID只能是数字，参考歌曲信息！");
            etAddSheetId.requestFocus();
            return;
        }
        if (musicAndFolderInfo == null) {
            showToast("请先查下歌曲信息！");
            return;
        }

        MusicOperate.MusicFolder selectedFolder = null;
        if (musicAndFolderInfo.folder_items != null) {
            for (MusicOperate.MusicFolder folder : musicAndFolderInfo.folder_items) {
                if (Integer.parseInt(folderId) == folder.folder_id) {
                    selectedFolder = folder;
                    break;
                }
            }
        }

        if (selectedFolder == null) {
            showToast("歌单不存在！");
            return;
        }

        if (musicAndFolderInfo.music_num == 0) {
            showToast("歌曲不存在！");
            return;
        }
        MusicOperate.MusicFile music = null;
        if (musicAndFolderInfo.music_items != null) {
            for (MusicOperate.MusicFile musicFile : musicAndFolderInfo.music_items) {
                if (Integer.parseInt(musicId) == musicFile.music_id) {
                    music = musicFile;
                    break;
                }
            }
        }

        if (music == null) {
            showToast("歌曲不存在！");
            return;
        }

        if (selectedFolder.music_index == null) {
            selectedFolder.music_index = new ArrayList<>();
        }

        if (selectedFolder.music_index.contains(music.music_id)) {
            showToast("歌单中已存在该歌曲！");
            return;
        }
        selectedFolder.music_num++;
        selectedFolder.music_index.add(music.music_id);
        BLEManager.moveMusicIntoFolder(selectedFolder);
        showProgressDialog("Move...");
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
        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case SELECT_FILE_REQ: {
                Uri uri = data.getData();
                String path = GetFilePathFromUri.getFileAbsolutePath(this, uri);
                if (!TextUtils.isEmpty(path)) {
                    etMusicPath.setText(path);
                    String name = FileUtil.getFileNameFromPath(path);
                    etMusicName.setText(name);
                    DataUtils.getInstance().saveMusicPath(path);
                    DataUtils.getInstance().saveMusicName(name);
                }
            }
        }
    }

}