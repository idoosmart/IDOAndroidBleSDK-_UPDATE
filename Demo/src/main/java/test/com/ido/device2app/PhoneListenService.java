package test.com.ido.device2app;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.ido.ble.BLEManager;
import com.ido.ble.LocalDataManager;
import com.ido.ble.callback.DeviceControlAppCallBack;
import com.ido.ble.protocol.model.IncomingCallInfo;
import com.ido.ble.protocol.model.SupportFunctionInfo;

import test.com.ido.APP;
import test.com.ido.CallBack.BaseDeviceControlAppCallBack;
import test.com.ido.utils.PermissionUtil;
import test.com.ido.utils.PhoneUtil;

public class PhoneListenService extends Service {

    private static final String TAG = "PhoneListenService";

    private TelephonyManager tpm;
    private PhoneStateListener phoneStateListener;
    private boolean hasFirstRegisterPhone;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        tpm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        phoneStateListener = new MyPhoneStateListener();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        boolean b = PermissionUtil.checkSelfPermission(getBaseContext(), PermissionUtil.getPhonePermission());
        Log.d(TAG, "onStartCommand: 开启DeviceAssistService服务" + b);
        if (b) {
            unregisterPhoneListener();
            registerPhoneListener();
        }

        BLEManager.unregisterDeviceControlAppCallBack(callBack);
        BLEManager.registerDeviceControlAppCallBack(callBack);
        return super.onStartCommand(intent, flags, startId);
    }

    private static PhoneListenService phoneListenService;

    public PhoneListenService() {
    }


    private class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallForwardingIndicatorChanged(boolean cfi) {
            super.onCallForwardingIndicatorChanged(cfi);
        }

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            Log.d(TAG, "接收到电话状态变化onCallStateChanged ：TelephonyManager   State = " + state);

            // 刚刚注册监听的时候，会调用一次onCallStateChange,需要屏蔽掉
            if (!hasFirstRegisterPhone) {
                hasFirstRegisterPhone = true;
                return;
            }
            handleCallReminder(state, incomingNumber,getContactNameFromPhoneBook(APP.getAppContext(),incomingNumber) );
        }
    }

    public static String getContactNameFromPhoneBook(Context context, String number) {
        String contactName = number;
        if (TextUtils.isEmpty(number)) {
            return contactName;
        }

        //申请PERMISSION_GRANTED权限
        boolean hasPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
        if (!hasPermission) {
            return contactName;
        }
//        if (isPermissions) {
////            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 3);
//            return contactName;
//        } else {
        String[] projection = {ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.PhoneLookup.NUMBER};
        Uri uri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number));
        Cursor cursor = context.getContentResolver().query(uri, projection,
                null, null, null);
        if (cursor==null)return "";
        if (cursor.moveToFirst()) {
            contactName = cursor
                    .getString(cursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            cursor.close();
        }
        return contactName;
//        }
    }
    /**
     * 获取功能列表
     *
     * @return
     */
    public SupportFunctionInfo getSupportFunctionInfo() {
        SupportFunctionInfo functionInfo = LocalDataManager.getSupportFunctionInfo();
        return functionInfo == null ? new SupportFunctionInfo() : functionInfo;
    }

    private void handleCallReminder(int state, String incomingNumber, String contactName) {
        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE: // 空闲【拒接电话】
                Log.d(TAG, "handleCallReminder: 空闲");

                if (BLEManager.isConnected()) {
                    if (getSupportFunctionInfo().v2_set_notice_missed_call) {
                        Log.d(TAG, "handleCallReminder: 支持v2_set_notice_missed_call");
                        //    BLEManager.setStopInComingCall();
                        BLEManager.missedInComingCall();
                        //    ProtocolSetCmd.getInstance().ProtocolMissedCallEvt();
                    } else {
                        Log.d(TAG, "handleCallReminder: 不支持v2_set_notice_missed_call");
                        BLEManager.setStopInComingCall();
                    }
                }
                break;
            case TelephonyManager.CALL_STATE_RINGING: // 来电
                Log.d(TAG, "handleCallReminder: 来电" + PermissionUtil.checkSelfPermission(getBaseContext(), PermissionUtil.getPhonePermission()));
                SupportFunctionInfo supportFunctionInfo = LocalDataManager.getSupportFunctionInfo();
                if (PermissionUtil.checkSelfPermission(getBaseContext(), PermissionUtil.getPhonePermission())) {
                    Log.d(TAG, "handleCallReminder: 来电" + incomingNumber);
                    if (BLEManager.isConnected()) {
                            sendCallReminder2DeviceNew(incomingNumber, contactName);
                    }
                }
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK: // 摘机【接听来电】
                if (BLEManager.isConnected()) {
                    BLEManager.setStopInComingCall();
                }
                break;
        }
    }

    public static void sendCallReminder2DeviceNew(final String phoneNumber, final String contactName) {

        Log.d(TAG, "sendCallReminder2DeviceNew: 发送命令到手环");
        Log.d(TAG, "sendCallReminder2DeviceNew: sendData   phoneNumber: " + phoneNumber + " ---contactName:" + contactName );
        IncomingCallInfo incomingCallInfo = new IncomingCallInfo();
        incomingCallInfo.name = contactName;
        incomingCallInfo.phoneNumber = contactName;

        BLEManager.setIncomingCallInfo(incomingCallInfo);
    }

    BaseDeviceControlAppCallBack callBack = new BaseDeviceControlAppCallBack() {

        @Override
        public void onFindPhone(boolean isStart, long timeOut) {
            super.onFindPhone(isStart, timeOut);
            boolean findPhoneOnOff = LocalDataManager.getFindPhoneSwitch();
            Log.d(TAG, "onFindPhone: 收到寻找手机的命令：isStart=" + isStart + ",timeOut=" + timeOut + ",findPhoneOnOff=" + findPhoneOnOff);
            if (findPhoneOnOff) {
                if (isStart) {
                    //收到开始寻找手机指令
                } else {
                    //收到停止寻找手机指令
                }
            }
        }

        @Override
        public void onAntiLostNotice(boolean b, long l) {
            super.onAntiLostNotice(b, l);
        }

        @Override
        public void onOneKeySOS(boolean b, long l) {
            super.onOneKeySOS(b, l);
        }

        private boolean isSendCall = false;

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onControlEvent(DeviceControlAppCallBack.DeviceControlEventType eventType, int var2) {

            if (eventType == DeviceControlAppCallBack.DeviceControlEventType.ANSWER_PHONE) {
                //接听电话的回调
                PhoneUtil.answerRingingCall(getApplicationContext());
                Log.d(TAG, "onControlEvent: 接收到接听电话的命令");
            } else if (eventType == DeviceControlAppCallBack.DeviceControlEventType.REJECT_PHONE) {//拒接电话的回调
                Log.d(TAG, "onControlEvent: 接收到拒接电话的命令");

                PhoneUtil.endCall(getApplicationContext());
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterPhoneListener();
        BLEManager.unregisterDeviceControlAppCallBack(callBack);
    }

    //监听电话状态改变事件
    private void registerPhoneListener() {
        // 创建一个监听对象，监听电话状态改变事件
        Log.d(TAG, "registerPhoneListener: 注册电话状态监听");
        tpm.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    private void unregisterPhoneListener() {
        if (tpm != null && phoneStateListener != null) {
            Log.d(TAG, "registerPhoneListener: 移除电话状态监听");
            tpm.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
    }

}

