package test.com.ido.font.upgrade.present;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.ido.ble.BLEManager;
import com.ido.ble.BLESpecialAPI;
import com.ido.ble.bluetooth.connect.ConnectFailedReason;
import com.ido.ble.bluetooth.device.BLEDevice;
import com.ido.ble.bluetooth.setting.BluetoothGattSettingListener;
import com.ido.ble.callback.ConnectCallBack;
import com.ido.ble.dfu.BleDFUConfig;
import com.ido.ble.dfu.BleDFUState;
import com.ido.ble.gps.callback.GpsCallBack;
import com.veryfit.multi.nativeprotocol.Protocol;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import test.com.ido.utils.DataUtils;

public class FontUpgradePresenter {


    private static final int TASK_NONE = 0;
    private static final int TASK_UPDATE_FONT_FILE = 1;
    private static final int TASK_UPDATE_BIN_FILE = 2;
    private static final int TASK_UPDATE_OTA_FILE = 3;

    private BLEDevice mTargetDevice;
    private IViewUpdate mIViewUpdate;
    private int mCurrentTask = TASK_NONE;
    private Timer mTimer;
    private long mStartTimeMS;

    private Handler mDelayHandler = new Handler(Looper.myLooper());


    private ConnectCallBack.ICallBack connectListener = new ConnectCallBack.ICallBack() {
        @Override
        public void onConnectStart(String macAddress) {

        }

        @Override
        public void onConnecting(String macAddress) {

        }

        @Override
        public void onRetry(int count, String macAddress) {

        }

        @Override
        public void onConnectSuccess(String macAddress) {
            BLEManager.unregisterConnectCallBack(connectListener);

            mDelayHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    doNextTask();
                }
            }, 2000);

        }

        @Override
        public void onConnectFailed(ConnectFailedReason reason,String macAddress) {
            BLEManager.unregisterConnectCallBack(connectListener);
            doNextTask();
        }

        @Override
        public void onConnectBreak(String macAddress) {

        }

        @Override
        public void onInDfuMode(BLEDevice bleDevice) {
//            failed();
        }

        @Override
        public void onDeviceInNotBindStatus(String macAddress) {

        }

        @Override
        public void onInitCompleted(String macAddress) {

        }
    };

    private UpdateFontFileStateListener mUpdateFontFileStateListener;
    /***************** 更新字库文件************************************/
    private void startUpdateFontFile(){

        mCurrentTask = TASK_UPDATE_FONT_FILE;

        if (mUpdateFontFileStateListener != null){
            BLEManager.removeDFUStateListener(mUpdateFontFileStateListener);
        }
        mUpdateFontFileStateListener = new UpdateFontFileStateListener();
        BLEManager.addDFUStateListener(mUpdateFontFileStateListener);


        BleDFUConfig dfuConfig = new BleDFUConfig();
        dfuConfig.setPRN(DataUtils.getInstance().getFontUpgradeNRFPRN());
        dfuConfig.setDeviceId(mTargetDevice.mDeviceId +"");
        dfuConfig.setFilePath(DataUtils.getInstance().getFontUpgradeFontFilePath());
        dfuConfig.setMacAddress(mTargetDevice.mDeviceAddress);
        BLEManager.startDFU(dfuConfig);
    }


    private UpdateBinFileStateListener mUpdateBinFileStateListener;
    private class UpdateBinFileStateListener implements GpsCallBack.ITranAgpsFileCallBack {

        @Override
        public void onProgress(int progress) {
            notifyBinFileProgress(progress);
        }

        @Override
        public void onFinish() {
            notifyBinFileProgress(100);
            notifyBinFileSuccess();
        }

        @Override
        public void onFailed(int error) {
            notifyBinFileFailed();
        }

        @Override
        public void onFailed(int error, Object value) {

        }
    }


    private UpdateOTAFileStateListener mUpdateOTAFileStateListener;

    /***************** 更新OTA文件************************************/
    private void startUpdateOtaFile(){
        mCurrentTask = TASK_UPDATE_OTA_FILE;

        if (mUpdateOTAFileStateListener != null){
            BLEManager.removeDFUStateListener(mUpdateOTAFileStateListener);
        }
        mUpdateOTAFileStateListener = new UpdateOTAFileStateListener();
        BLEManager.addDFUStateListener(mUpdateOTAFileStateListener);


        BleDFUConfig dfuConfig = new BleDFUConfig();
        dfuConfig.setPRN(DataUtils.getInstance().getFontUpgradeNRFPRN());
        dfuConfig.setDeviceId(mTargetDevice.mDeviceId +"");
        dfuConfig.setFilePath(DataUtils.getInstance().getFontUpgradeOtaFilePath());
        dfuConfig.setMacAddress(mTargetDevice.mDeviceAddress);
        BLEManager.startDFU(dfuConfig);
    }




    /***************** 主控制流程************************************/
    public FontUpgradePresenter(IViewUpdate iViewUpdate, BLEDevice device){
        mIViewUpdate = iViewUpdate;
        mTargetDevice = device;
        //清除这个标志
        mTargetDevice.mIsInDfuMode = false;
        setGatt();
    }

    private void setGatt(){
        BLESpecialAPI.setBluetoothGattSettingListener(new BluetoothGattSettingListener.IListener() {
            @Override
            public BluetoothGattCharacteristic addParaToCharacteristic(BluetoothGattCharacteristic characteristic) {
                characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                return characteristic;
            }
        });
    }


    public void start(){
        mStartTimeMS = System.currentTimeMillis();
        startTimer();
        mIViewUpdate.onTaskStart();

        if (mCurrentTask == TASK_NONE){
            mCurrentTask = TASK_UPDATE_FONT_FILE;
        }
        doNextTask();
    }

    public void stop(){
        stopTask();
    }


    private void doNextTask(){

        if (mCurrentTask == TASK_UPDATE_FONT_FILE){
            startUpdateFontFile();
        }else if (mCurrentTask == TASK_UPDATE_BIN_FILE){
            if (connectDevice()) {
                startUpdateBinFile();
            }
        }else if (mCurrentTask == TASK_UPDATE_OTA_FILE){
            startUpdateOtaFile();
        }else if (mCurrentTask == TASK_NONE){
            success();
        }
    }
    private void stopTask(){
        endTimer();

        if (mCurrentTask == TASK_UPDATE_FONT_FILE){
            stopUpdateFontFile();
        }else if (mCurrentTask == TASK_UPDATE_BIN_FILE){
            stopUpdateBinFile();
        }else if (mCurrentTask == TASK_UPDATE_OTA_FILE){
            stopUpdateOtaFile();
        }
    }

    private boolean connectDevice(){
        if (BLEManager.isConnected()){
            return true;
        }

        BLEManager.registerConnectCallBack(connectListener);
        BLEManager.connect(mTargetDevice);
        return false;
    }
    private void success(){
        endTimer();
        mCurrentTask = TASK_NONE;
        mIViewUpdate.onTaskSuccess();
    }
    private void failed(){
        endTimer();
        mIViewUpdate.onTaskFailed();
    }

    private void startTimer(){
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mIViewUpdate.onLostTime((System.currentTimeMillis() - mStartTimeMS) / 1000);
            }
        }, 0, 1000);
    }
    private void endTimer(){
        mTimer.cancel();
    }

    private class UpdateFontFileStateListener implements BleDFUState.IListener{

        @Override
        public void onPrepare() {
            notifyFontFileStart();
        }

        @Override
        public void onDeviceInDFUMode() {
        }

        @Override
        public void onProgress(int progress) {
            if (progress <= 99) {
                notifyFontFileProgress(progress);
            }
        }

        @Override
        public void onSuccess() {
            notifyFontFileProgress(100);
            notifyFontFileSuccess();
        }

        @Override
        public void onSuccessAndNeedToPromptUser() {
            notifyFontFileSuccess();
        }

        @Override
        public void onFailed(BleDFUState.FailReason failReason) {
            notifyFontFileFailed();
        }

        @Override
        public void onCanceled() {

        }

        @Override
        public void onRetry(int count) {

        }
    }
    private void stopUpdateFontFile(){
        BLEManager.cancelDFU();
    }
    private void notifyFontFileStart(){
        mIViewUpdate.onUpdateFontFileStart();
    }
    private void notifyFontFileProgress(int progress){
        mIViewUpdate.onUpdateFontFileProgress(progress);

    }

    private void notifyFontFileSuccess(){
        BLEManager.removeDFUStateListener(mUpdateFontFileStateListener);
        mIViewUpdate.onUpdateFontFileSuccess();

        mCurrentTask = TASK_UPDATE_BIN_FILE;
        doNextTask();
    }
    private void notifyFontFileFailed(){
        BLEManager.removeDFUStateListener(mUpdateFontFileStateListener);
        failed();
        mIViewUpdate.onUpdateFontFileFailed();
    }



    /***************** 更新Bin文件************************************/
    private void startUpdateBinFile(){
        mCurrentTask = TASK_UPDATE_BIN_FILE;
//        AgpsFileTransConfig config = new AgpsFileTransConfig();
//        config.listener = new UpdateBinFileStateListener();
//        config.filePath = DataUtils.getInstance().getFontUpgradeBinFilePath();
//        config.PRN = DataUtils.getInstance().getFontUpgradeBinPRN();
//        BLEManager.startTranAgpsFile(config);

        if (mUpdateBinFileStateListener != null){
            BLEManager.unregisterTranAgpsFileCallBack(mUpdateBinFileStateListener);
        }
        mUpdateBinFileStateListener = new UpdateBinFileStateListener();
        BLEManager.registerTranAgpsFileCallBack(mUpdateBinFileStateListener);
        if (DataUtils.getInstance().getFontUpgradeBinPRN() > 0) {
            Protocol.getInstance().tranDataSetPRN(DataUtils.getInstance().getFontUpgradeBinPRN());
        }else {
            Protocol.getInstance().tranDataSetPRN(1);
        }


        byte[] aGpsData = getBytes(DataUtils.getInstance().getFontUpgradeBinFilePath());
        if (aGpsData != null && aGpsData.length > 0) {
           // int errorCode = SoLibNativeMethodWrapper.setAgpsFileTranPara(aGpsData);
           // Log.e("AGPS", "tranDataSetBuff return code is " + errorCode);
        } else {
            Log.e("AGPS", "aGpsData is null");
        }

        Protocol.getInstance().tranDataStart();
        notifyBinFileStart();
    }
    private void stopUpdateBinFile(){
//        BLEManager.stopTranAgpsFile();
        Protocol.getInstance().tranDataStop();
    }
    private void notifyBinFileStart(){
        mIViewUpdate.onUpdateBinFileStart();
    }
    private void notifyBinFileProgress(int progress){
        mIViewUpdate.onUpdateBinFileProgress(progress);
    }
    private void notifyBinFileSuccess(){
        mIViewUpdate.onUpdateBinFileSuccess();

        mCurrentTask = TASK_UPDATE_OTA_FILE;
        doNextTask();
    }
    private void notifyBinFileFailed(){
        failed();
        mIViewUpdate.onUpdateBinFileFailed();
    }

    private class UpdateOTAFileStateListener implements BleDFUState.IListener{

        @Override
        public void onPrepare() {
            notifyOTAFileStart();
        }

        @Override
        public void onDeviceInDFUMode() {
        }

        @Override
        public void onProgress(int progress) {
            if (progress <= 99) {
                notifyOTAFileProgress(progress);
            }
        }

        @Override
        public void onSuccess() {
            notifyOTAFileProgress(100);
            notifyOTAFileSuccess();
        }

        @Override
        public void onSuccessAndNeedToPromptUser() {
            notifyOTAFileSuccess();
        }

        @Override
        public void onFailed(BleDFUState.FailReason failReason) {
            notifyOTAFileFailed();
        }

        @Override
        public void onCanceled() {

        }

        @Override
        public void onRetry(int count) {

        }
    }
    private void stopUpdateOtaFile(){
        BLEManager.cancelDFU();
    }
    private void notifyOTAFileStart(){
        mIViewUpdate.onUpdateOtaFileStart();
    }
    private void notifyOTAFileProgress(int progress){
        mIViewUpdate.onUpdateOtaFileProgress(progress);
    }

    private void notifyOTAFileSuccess(){
        BLEManager.removeDFUStateListener(mUpdateOTAFileStateListener);
        mIViewUpdate.onUpdateOtaFileSuccess();

        mCurrentTask = TASK_NONE;
        doNextTask();
    }
    private void notifyOTAFileFailed(){
        BLEManager.removeDFUStateListener(mUpdateOTAFileStateListener);
        failed();
        mIViewUpdate.onUpdateOtaFileFailed();
    }

    /**
     * 获得指定文件的byte数组
     */
    public static byte[] getBytes(String filePath) {
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream((int) file.length());
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }


}
