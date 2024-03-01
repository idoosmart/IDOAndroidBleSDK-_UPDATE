package test.com.ido.notice;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.ido.ble.BLEManager;
import com.ido.ble.LocalDataManager;
import com.ido.ble.callback.DeviceParaChangedCallBack;
import com.ido.ble.callback.PhoneMsgNoticeCallBack;
import com.ido.ble.callback.SettingCallBack;
import com.ido.ble.protocol.model.DeviceChangedPara;
import com.ido.ble.protocol.model.IncomingCallInfo;
import com.ido.ble.protocol.model.NewMessageInfo;
import com.ido.ble.protocol.model.NoticeReminderSwitchStatus;
import com.ido.ble.protocol.model.QuickReplyInfo;
import com.ido.ble.protocol.model.SupportFunctionInfo;
import com.ido.ble.protocol.model.V3MessageNotice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;
import test.com.ido.utils.ResourceUtil;

public class PhoneNoticeActivity extends BaseAutoConnectActivity {

    private static String TAG = "PhoneNoticeActivity";
    private static final String ACTION_SEND_SMS = "com.ido.life.util.ACTION_SEND_SMS";
    private EditText etIncomingName, etIncomingPhoneNumber;
    private EditText etNewMsgContent, etNewMsgName, etNewMsgNumber, etMsgType;
    private RadioButton rbSMS, rbEmail, rbWX;

    //正在回复来电
    private static boolean isInComingCallReplying = false;
    private static DeviceChangedPara mDeviceChangedPara;
    private boolean isV3;
    private PhoneMsgNoticeCallBack.ICallBack iCallBack = new PhoneMsgNoticeCallBack.ICallBack() {
        @Override
        public void onCalling() {
            Toast.makeText(PhoneNoticeActivity.this, R.string.phone_notice_tip_msg_ok, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onStopCall() {
            Toast.makeText(PhoneNoticeActivity.this, "挂断电话", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onNewMessage() {
            Toast.makeText(PhoneNoticeActivity.this, R.string.phone_notice_tip_msg_ok, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onUnReadMessage() {
            Toast.makeText(PhoneNoticeActivity.this, R.string.phone_notice_tip_msg_ok, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onV3MessageNotice(int error) {
            Toast.makeText(PhoneNoticeActivity.this, "result=" + error, Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_notice);
        //    initFunction();
        BLEManager.registerPhoneMsgNoticeCallBack(iCallBack);
        initView();
        initFunction();
        if (isSupportInComingCallQuickReply()) {
            BLEManager.unregisterDeviceParaChangedCallBack(mMsgReplyICallBack);
            BLEManager.registerDeviceParaChangedCallBack(mMsgReplyICallBack);
            requestPermissions(new String[]{Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS}, 1000);
        }
        sendQuickIncomingReplyInfo2Device();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    showToast("授权失败！");
                    return;
                }
            }
        }
    }

    /**
     * 设置默认来电快捷回复列表
     */
    private void sendQuickIncomingReplyInfo2Device() {
        if (isSupportInComingCallQuickReply()) {//支持快捷回复
            List<String> msgList = Arrays.asList(ResourceUtil.getStringArray(R.array.quick_incoming_call_reply_default_msg));
            QuickReplyInfo quickReplyInfo = new QuickReplyInfo();
            quickReplyInfo.num = msgList.size();
            quickReplyInfo.fast_items = new ArrayList<>();
            for (int i = 0; i < msgList.size(); i++) {
                QuickReplyInfo.QuickMsg msg = new QuickReplyInfo.QuickMsg();
                msg.msg_id = i + 1;
                msg.on_off = 1;
                msg.msg_data = msgList.get(i);
                quickReplyInfo.fast_items.add(msg);
            }
            BLEManager.unregisterSettingCallBack(mQuickIncomingCallSettingCallBack);
            BLEManager.registerSettingCallBack(mQuickIncomingCallSettingCallBack);
            //设置来电提醒快捷回复
            quickReplyInfo.version = 1;
            BLEManager.setQuickReplyInfo(quickReplyInfo);
        }
    }

    private boolean isSupportInComingCallQuickReply() {
        SupportFunctionInfo funcInfo = LocalDataManager.getSupportFunctionInfo();
        return funcInfo != null && funcInfo.support_calling_quick_reply;
    }

    private SettingCallBack.ICallBack mQuickIncomingCallSettingCallBack = new SettingCallBack.ICallBack() {

        @Override
        public void onSuccess(SettingCallBack.SettingType settingType, Object o) {
            if (settingType == SettingCallBack.SettingType.QUICK_REPLY_INFO) {
                showToast("来电快捷回复预置内容设置成功");
            }
        }

        @Override
        public void onFailed(SettingCallBack.SettingType settingType) {
            if (settingType == SettingCallBack.SettingType.QUICK_REPLY_INFO) {
                showToast("来电快捷回复预置内容设置失败");
            }
        }
    };

    private final DeviceParaChangedCallBack.ICallBack mMsgReplyICallBack = deviceChangedPara -> {
        try {
            if (deviceChangedPara != null && (deviceChangedPara.msg_ID > 0 || deviceChangedPara.msg_type == 0x01) && deviceChangedPara.msg_notice > 0) {
                Log.d(TAG, "【" + Thread.currentThread().getName() + "】 mMsgReplyICallBack=" + deviceChangedPara);
                if (deviceChangedPara.msg_type == 0x01) {
                    Log.d(TAG, "来电快捷回复！");
                    if (!isInComingCallReplying) {
                        isInComingCallReplying = true;
                        mDeviceChangedPara = deviceChangedPara;
                        replyIncomingCallMsg(mDeviceChangedPara);
                        //通知固件
                    } else {
                        Log.d(TAG, "正在回复消息，不处理");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendInComingCallReplyResult2Device(false);
            Log.d(TAG, "快捷回复处理异常：" + e);
        }
    };

    /**
     * 短信回复超时计时器
     */
    private static final Handler mSmsReplyTimeoutTimer = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Log.d(TAG, "mSmsReplyTimeoutTimer, 发送来电快捷回复超时，重置标志位");
                    sendInComingCallReplyResult2Device(false);
                    break;
            }
        }
    };

    private void replyIncomingCallMsg(DeviceChangedPara mDeviceChangedPara) {
        String mIncomingCallPhone = etIncomingPhoneNumber.getText().toString();
        List<String> msgList = Arrays.asList(ResourceUtil.getStringArray(R.array.quick_reply_default_msg));
        String replyContent = msgList.get(mDeviceChangedPara.msg_notice - 1);
        showToast("收到设备端来电快捷回复内容：" + replyContent);
        if (TextUtils.isEmpty(replyContent)) {
            showToast("回复失败，内容为空！");
            sendInComingCallReplyResult2Device(false);
            return;
        }
        //来电快捷回复没有msgId,此处使用默认的值标识来电
        mSmsReplyTimeoutTimer.sendEmptyMessageDelayed(1, 30000);
        boolean replySuccess = reply(0/*TODO sim卡id*/, -2000, mIncomingCallPhone, replyContent);
        if (!replySuccess) {//失败直接通知设备
            showToast("发送失败！");
            sendInComingCallReplyResult2Device(false);
        }
    }

    /**
     * 短信发送结果广播
     */
    private final  BroadcastReceiver mSmsSentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "action = " + action);
            if (!TextUtils.isEmpty(action) && action.contains(ACTION_SEND_SMS)) {
                int msgId = -1;
                if (action.contains("/")) {
                    String[] sps = action.split("/");
                    if (!TextUtils.isEmpty(sps[1])) {
                        try {
                            msgId = Integer.parseInt(sps[1]);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                }
                int resultCode = getResultCode();
                boolean replySuccess = resultCode == Activity.RESULT_OK;
                Log.d(TAG, "发送结果：resultCode = " + resultCode + ", msgId = " + msgId);
                showToast(replySuccess ? "来电快捷回复成功" : "来电快捷回复失败");
                if (msgId == -2000) {
                    sendInComingCallReplyResult2Device(replySuccess);
                }
            }
        }
    };

    private void registerMsgReplyResultReceiver(String action) {
        try {
            unregisterReceiver(mSmsSentReceiver);
        } catch (Exception e) {
        }
        registerReceiver(mSmsSentReceiver, new IntentFilter(action));
    }

    private boolean reply(int subId, int msgID, String phoneNumber, String autoReplyContents) {
        try {
//            SmsManager smsm;
//            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                try {
//                    smsm = SmsManager.getSmsManagerForSubscriptionId(SubscriptionManager.getDefaultSmsSubscriptionId());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    smsm = SmsManager.getDefault();
//                }
//            } else {
//                smsm = SmsManager.getDefault();
//            }
            if (TextUtils.isEmpty(phoneNumber)) {
                Log.d(TAG, "phoneNumber is null or empty!");
                return false;
            }
            String action = ACTION_SEND_SMS + "/" + msgID;
            registerMsgReplyResultReceiver(action);
            int flag = PendingIntent.FLAG_ONE_SHOT;
            if (Build.VERSION.SDK_INT >= 31) {
                flag = PendingIntent.FLAG_IMMUTABLE;
            }
//            ArrayList<String> divideContents = smsm.divideMessage(autoReplyContents);
            PendingIntent sentIntent = PendingIntent.getBroadcast(this, msgID, new Intent(action), flag);
//            ArrayList<PendingIntent> sentIntents = new ArrayList<>();
//            sentIntents.add(sentIntent);
            Log.d(TAG, "reply (" + msgID + ") '" + autoReplyContents + "' to " + phoneNumber);
            SmsManager.getDefault().sendTextMessage(phoneNumber, null, autoReplyContents, sentIntent, null);
            Log.d(TAG, "reply (" + msgID + ") '" + autoReplyContents + "' to " + phoneNumber + " has send to SmsManager!");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "reply failed: " + e);
            return false;
        }
    }


    /**
     * 来电快捷回复响应
     *
     * @param replySuccess
     */
    private static void sendInComingCallReplyResult2Device(boolean replySuccess) {
        Log.d(TAG, "sendInComingCallReplyResult2Device, mDeviceChangedPara = " + mDeviceChangedPara + ", replySuccess = " + replySuccess);
        try {
            if (mDeviceChangedPara != null) {
                mDeviceChangedPara.is_success = replySuccess ? 1 : 0;
                BLEManager.setNoticeReply(mDeviceChangedPara);
            }
            mSmsReplyTimeoutTimer.removeMessages(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        isInComingCallReplying = false;
    }

    private void initFunction() {
        SupportFunctionInfo functionInfo = LocalDataManager.getSupportFunctionInfo();

        if (functionInfo.ex_table_main10_v3_notify_msg) {
            //  BLEManager.setV3MessageNotice(V3MessageNotice v3MessageNotice)
            isV3 = true;
        } else {
            //  BLEManager.setNewMessageDetailInfo();
        }
    }


    private void initView() {
        etIncomingName = (EditText) findViewById(R.id.phone_notice_incoming_name_et);
        etIncomingPhoneNumber = (EditText) findViewById(R.id.phone_notice_incoming_phone_number_et);

        etNewMsgName = (EditText) findViewById(R.id.phone_notice_new_msg_name_et);
        etNewMsgNumber = (EditText) findViewById(R.id.phone_notice_new_msg_number_et);
        etNewMsgContent = (EditText) findViewById(R.id.phone_notice_new_msg_content_et);
        etMsgType = findViewById(R.id.phone_notice_new_msg_type_et);

        rbSMS = (RadioButton) findViewById(R.id.phone_notice_new_msg_type_sms_rb);
        rbEmail = (RadioButton) findViewById(R.id.phone_notice_new_msg_type_email_rb);
        rbWX = (RadioButton) findViewById(R.id.phone_notice_new_msg_type_wx_rb);

    }



    /**
     * 27 CallReminderSwitch
     * @param v
     */
    public void CallReminderSwitch(View v){
        SupportFunctionInfo supportFunctionInfo =  LocalDataManager.getSupportFunctionInfo();
        if(supportFunctionInfo!=null && supportFunctionInfo.V3_support_sync_contact){
            NoticeReminderSwitchStatus status = new NoticeReminderSwitchStatus();
            status.notify_switch = NoticeReminderSwitchStatus.NOTIFY_88;
            status.call_switch = NoticeReminderSwitchStatus.SWITCH_ON;
            BLEManager.setNoticeReminderSwitchStatus(status);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BLEManager.unregisterPhoneMsgNoticeCallBack(iCallBack);
        BLEManager.unregisterSettingCallBack(mQuickIncomingCallSettingCallBack);
    }


    public void noticeIncomingCall(View v) {
        IncomingCallInfo incomingCallInfo = new IncomingCallInfo();
        incomingCallInfo.name = etIncomingName.getText().toString();
        incomingCallInfo.phoneNumber = etIncomingPhoneNumber.getText().toString();
        BLEManager.setIncomingCallInfo(incomingCallInfo);
    }

    public void v3noticeIncomingCall(View v) {
        V3MessageNotice v3MessageNotice = new V3MessageNotice();
        v3MessageNotice.evtType = V3MessageNotice.TYPE_CALL;
        v3MessageNotice.contact = etIncomingName.getText().toString();
        v3MessageNotice.phoneNumber = etIncomingPhoneNumber.getText().toString();
        v3MessageNotice.supportHangUp = true;
        v3MessageNotice.msgID = 1000;
        BLEManager.setV3MessageNotice(v3MessageNotice);
    }


    public void setStopInComingCall(View v) {
        BLEManager.setStopInComingCall();
    }

    public void noticeNewMsg(View v) {
        if (isV3) {
            v3noticeNewMsg();
        } else {
            NewMessageInfo newMessageInfo = new NewMessageInfo();
            String typeString = etMsgType.getText().toString();
            typeString = typeString.replace("x", "0");
            typeString = typeString.replace("X", "0");
            newMessageInfo.type = Integer.parseInt(typeString, 16);
            newMessageInfo.name = etNewMsgName.getText().toString();
            newMessageInfo.number = etNewMsgNumber.getText().toString();
            newMessageInfo.content = etNewMsgContent.getText().toString();
            BLEManager.setNewMessageDetailInfo(newMessageInfo);
        }
    }



    public void v3noticeNewMsg() {
        V3MessageNotice v3MessageNotice = new V3MessageNotice();
        String typeString = etMsgType.getText().toString();
        typeString = typeString.replace("x", "0");
        typeString = typeString.replace("X", "0");
        v3MessageNotice.evtType = Integer.parseInt(typeString, 16) + 0x2000;
        v3MessageNotice.contact = etNewMsgName.getText().toString();
        v3MessageNotice.dataText = etNewMsgContent.getText().toString();
        BLEManager.setV3MessageNotice(v3MessageNotice);
    }

}
