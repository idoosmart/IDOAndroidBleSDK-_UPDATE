package test.com.ido.notice;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.ido.ble.BLEManager;
import com.ido.ble.LocalDataManager;
import com.ido.ble.callback.PhoneMsgNoticeCallBack;
import com.ido.ble.protocol.model.IncomingCallInfo;
import com.ido.ble.protocol.model.MessageNotifyState;
import com.ido.ble.protocol.model.NewMessageInfo;
import com.ido.ble.protocol.model.NotificationPara;
import com.ido.ble.protocol.model.SupportFunctionInfo;
import com.ido.ble.protocol.model.V3MessageNotice;

import java.util.ArrayList;
import java.util.List;

import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;

public class PhoneNoticeActivity extends BaseAutoConnectActivity {

    private EditText etIncomingName, etIncomingPhoneNumber;
    private EditText etNewMsgContent, etNewMsgName, etNewMsgNumber, etMsgType;
    private RadioButton rbSMS,rbEmail, rbWX;
    private  boolean isV3;
    private PhoneMsgNoticeCallBack.ICallBack iCallBack = new PhoneMsgNoticeCallBack.ICallBack() {
        @Override
        public void onCalling() {
            Toast.makeText(PhoneNoticeActivity.this, R.string.phone_notice_tip_msg_ok, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onStopCall() {
            Toast.makeText(PhoneNoticeActivity.this, R.string.phone_notice_tip_msg_ok, Toast.LENGTH_LONG).show();
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
            Toast.makeText(PhoneNoticeActivity.this, "result="+error, Toast.LENGTH_LONG).show();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_notice);
    //    initFunction();
        BLEManager.registerPhoneMsgNoticeCallBack(iCallBack);
        initView();
    }

    private void initFunction(){
        SupportFunctionInfo functionInfo = LocalDataManager.getSupportFunctionInfo();

        if (functionInfo.ex_table_main10_v3_notify_msg ){
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BLEManager.unregisterPhoneMsgNoticeCallBack(iCallBack);
    }

    public void noticeIncomingCall(View v){
        IncomingCallInfo incomingCallInfo = new IncomingCallInfo();
        incomingCallInfo.name = etIncomingName.getText().toString();
        incomingCallInfo.phoneNumber = etIncomingPhoneNumber.getText().toString();
        BLEManager.setIncomingCallInfo(incomingCallInfo);
    }

    public void v3noticeIncomingCall(View v){
        V3MessageNotice v3MessageNotice = new V3MessageNotice();
        v3MessageNotice.evtType = V3MessageNotice.TYPE_CALL;
        v3MessageNotice.contact = etIncomingName.getText().toString();
        v3MessageNotice.phoneNumber = etIncomingPhoneNumber.getText().toString();
        v3MessageNotice.supportHangUp = true;
        v3MessageNotice.msgID = 1000;
        BLEManager.setV3MessageNotice(v3MessageNotice);
    }


    public void setStopInComingCall(View v){
        BLEManager.setStopInComingCall();
    }

    public void noticeNewMsg(View v){
        if(isV3){
           v3noticeNewMsg();
        }else {
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


    public void v3noticeNewMsg(){
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
