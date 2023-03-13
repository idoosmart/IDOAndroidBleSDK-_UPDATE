package test.com.ido.set;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ido.ble.BLEManager;
import com.ido.ble.callback.OtherProtocolCallBack;
import com.ido.ble.callback.SettingCallBack;
import com.ido.ble.custom.MakeSmartCompetitorFileConfig;
import com.ido.ble.file.transfer.FileTransferConfig;
import com.ido.ble.file.transfer.IFileTransferListener;
import com.ido.ble.protocol.model.SmartCompetitorInfo;
import com.ido.ble.watch.custom.callback.WatchPlateCallBack;
import com.ido.ble.watch.custom.model.DialPlateParam;
import com.ido.ble.watch.custom.model.WatchPlateFileInfo;
import com.ido.ble.watch.custom.model.WatchPlateScreenInfo;

import java.io.File;

import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;
import test.com.ido.log.LogPathImpl;
import test.com.ido.utils.GsonUtil;

/**
 * 智能陪跑
 */
public class SmartCompetitorActivity extends BaseAutoConnectActivity {
    private TextView tvFilePath, tvState, tvOperateTv;
    private String smartFilename = "sport.isf";
   // private String dir = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"smartfile/";
    private String dir = LogPathImpl.getInstance().getSportfile();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart);
        BLEManager.getFunctionTables();
        tvFilePath = findViewById(R.id.file_path_tv);
        tvState = findViewById(R.id.state_tv);
        tvOperateTv = findViewById(R.id.operate_info_tv);

        BLEManager.registerWatchOperateCallBack(iOperateCallBack);
        File file = new File(dir);
        if(!file.exists()){
           boolean d =   file.mkdirs();
           Log.e("dd:",d+"");
        }
        tvFilePath.setText("请把文件存到"+dir+"");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public void startSet(View view){
        setplat();
    }

    public void SetColor(View view){
        SmartCompetitorInfo info = new SmartCompetitorInfo();
        info.background_font_color = 0x00ff00;
        info.operate = SmartCompetitorInfo.Oprate_SET;
        BLEManager.registerSettingCallBack(new SettingCallBack.ICallBack() {
            @Override
            public void onSuccess(SettingCallBack.SettingType type, Object data) {
                if(type == SettingCallBack.SettingType.SMART_COMPETITOR_INFO_SET){
                    tvState.setText("set success");
                }
            }

            @Override
            public void onFailed(SettingCallBack.SettingType type) {
                if(type == SettingCallBack.SettingType.SMART_COMPETITOR_INFO_SET){
                    tvState.setText("set failed");
                }
            }
        });
       BLEManager.setSmartCompetitorInfo(info);
    }

    private void setplat() {
         BLEManager.getWatchPlateScreenInfo();
    }



    WatchPlateCallBack.IOperateCallBack iOperateCallBack = new WatchPlateCallBack.IOperateCallBack() {


        @Override
        public void onGetPlateFileInfo(WatchPlateFileInfo watchPlateFileInfo) {

        }

        @Override
        public void onGetScreenInfo(WatchPlateScreenInfo screenInfo) {
            tvOperateTv.setText(GsonUtil.toJson(screenInfo));
            if(screenInfo!=null){
                MakeSmartCompetitorFileConfig config = new MakeSmartCompetitorFileConfig();
                config.filePath = dir;
                config.outFileName = smartFilename;
                config.blockSize = screenInfo.blockSize;
                config.format = screenInfo.format;
                BLEManager.registerOtherProtocolCallBack(new OtherProtocolCallBack.ICallBack() {
                    @Override
                    public void onSuccess(OtherProtocolCallBack.SettingType type) {
                        if(type == OtherProtocolCallBack.SettingType.MAKE_SMART_COMPETITOR_FILE){
                            tvState.setText("make file success");
                            startTransferSmartCompetitorFile();

                        }
                    }

                    @Override
                    public void onFailed(OtherProtocolCallBack.SettingType type) {
                        if(type == OtherProtocolCallBack.SettingType.MAKE_SMART_COMPETITOR_FILE){
                              tvState.setText("make file failed");
                        }
                    }
                });
                BLEManager.mkSmartCompetitorisfFile(config);
            }
        }

        @Override
        public void onGetCurrentPlate(String uniqueID) {

        }

        @Override
        public void onSetPlate(boolean isSuccess) {

        }

        @Override
        public void onDeletePlate(boolean isSuccess) {

        }

        @Override
        public void onGetDialPlateParam(DialPlateParam dialPlateParam) {


        }
    };


    /**
     * 设置智能陪跑文件
     */
    public void startTransferSmartCompetitorFile(){
        String file = dir + smartFilename+".slz";
        FileTransferConfig config = FileTransferConfig.getDefaultUbloxAGpsFileConfig(file, new IFileTransferListener() {
            @Override
            public void onStart() {
                tvState.setText("start transfer");
            }

            @Override
            public void onProgress(int progress) {
                tvState.setText("progress "+progress);
            }

            @Override
            public void onSuccess() {
                tvState.setText("file transfer success");
            }

            @Override
            public void onFailed(String errorMsg) {
                tvState.setText("file transfer onFailed");
            }
        });
        config.firmwareSpecName = smartFilename+".slz";;
        BLEManager.stopTranCommonFile();
        BLEManager.startTranCommonFile(config);
    }

}
