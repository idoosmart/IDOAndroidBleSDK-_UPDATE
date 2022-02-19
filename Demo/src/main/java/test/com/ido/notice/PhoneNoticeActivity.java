package test.com.ido.notice;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.ido.ble.BLEManager;
import com.ido.ble.callback.PhoneMsgNoticeCallBack;
import com.ido.ble.protocol.model.IncomingCallInfo;
import com.ido.ble.protocol.model.MessageNotifyState;
import com.ido.ble.protocol.model.NewMessageInfo;
import com.ido.ble.protocol.model.NotificationPara;
import com.ido.ble.protocol.model.V3MessageNotice;

import java.util.ArrayList;
import java.util.List;

import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;

public class PhoneNoticeActivity extends BaseAutoConnectActivity {

    private EditText etIncomingName, etIncomingPhoneNumber;
    private EditText etNewMsgContent, etNewMsgName, etNewMsgNumber, etMsgType;
    private RadioButton rbSMS,rbEmail, rbWX;
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
        BLEManager.registerPhoneMsgNoticeCallBack(iCallBack);
        initView();
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

    public void setStopInComingCall(View v){
        BLEManager.setStopInComingCall();

    }

    public void noticeNewMsg(View v){
        NewMessageInfo newMessageInfo = new NewMessageInfo();

//        if (rbSMS.isChecked()){
//            newMessageInfo.type = NewMessageInfo.TYPE_SMS;
//        }else if (rbEmail.isChecked()){
//            newMessageInfo.type = NewMessageInfo.TYPE_EMAIL;
//        }else if (rbWX.isChecked()){
//            newMessageInfo.type = NewMessageInfo.TYPE_WX;
//        }
        String typeString = etMsgType.getText().toString();
        typeString = typeString.replace("x", "0");
        typeString = typeString.replace("X", "0");
        newMessageInfo.type = Integer.parseInt(typeString, 16);
        newMessageInfo.name = etNewMsgName.getText().toString();
        newMessageInfo.number = etNewMsgNumber.getText().toString();
        newMessageInfo.content = etNewMsgContent.getText().toString();
        BLEManager.setNewMessageDetailInfo(newMessageInfo);

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

    public void v3setStopInComingCall(View v){
        BLEManager.setStopInComingCall();

    }

    public void v3noticeNewMsg(View v){
        V3MessageNotice v3MessageNotice = new V3MessageNotice();

//        if (rbSMS.isChecked()){
//            v3MessageNotice.evtType = V3MessageNotice.TYPE_SMS;
//        }else if (rbEmail.isChecked()){
//            v3MessageNotice.evtType = V3MessageNotice.TYPE_EMAIL;
//        }else if (rbWX.isChecked()){
//            v3MessageNotice.evtType = V3MessageNotice.TYPE_WX;
//        }
        String typeString = etMsgType.getText().toString();
        typeString = typeString.replace("x", "0");
        typeString = typeString.replace("X", "0");
        v3MessageNotice.evtType = Integer.parseInt(typeString, 16) + 0x2000;


        v3MessageNotice.contact = etNewMsgName.getText().toString();
        v3MessageNotice.dataText = etNewMsgContent.getText().toString();
        BLEManager.setV3MessageNotice(v3MessageNotice);
    }

    public void OpenDeviceNoticeSwitch(View view){
        List<MessageNotifyState> stateList = new ArrayList<>();
        MessageNotifyState state = new MessageNotifyState();

        String typeString = etMsgType.getText().toString();
        typeString = typeString.replace("x", "0");
        typeString = typeString.replace("X", "0");
        state.evt_type = Integer.parseInt(typeString, 16) ;
        state.notify_state = 1;
        stateList.add(state);
        BLEManager.modifyMessageNotifyState(stateList, 0, 0);
    }
    public void IconAndNameMsg(View view){
        NotificationPara para = new NotificationPara();
        para.contact = etNewMsgName.getText().toString();
        para.msg_data = etNewMsgContent.getText().toString();
        String typeString = etMsgType.getText().toString();
        typeString = typeString.replace("x", "0");
        typeString = typeString.replace("X", "0");
        para.evt_type = 1;
        para.notify_type = Integer.parseInt(typeString, 16);
        para.items = new ArrayList<>();

        for(int i = 1 ; i <=8 ; i++){
            NotificationPara.AppNames appNames = new NotificationPara.AppNames();
            appNames.language = i;
            appNames.name = etNewMsgNumber.getText().toString();
            para.items.add(appNames);
        }


        BLEManager.sendNotification(para);
    }

    public void noticeUnreadMsg(View v){

    }
}
