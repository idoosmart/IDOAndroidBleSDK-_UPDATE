package test.com.ido.exgdata;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


import com.ido.ble.BLEManager;
import com.ido.ble.callback.AppExchangeDataCallBack;
import com.ido.ble.protocol.model.AppExchangeDataIngDeviceReplyData;
import com.ido.ble.protocol.model.AppExchangeDataIngPara;
import com.ido.ble.protocol.model.AppExchangeDataPauseDeviceReplyData;
import com.ido.ble.protocol.model.AppExchangeDataPausePara;
import com.ido.ble.protocol.model.AppExchangeDataResumeDeviceReplyData;
import com.ido.ble.protocol.model.AppExchangeDataResumePara;
import com.ido.ble.protocol.model.AppExchangeDataStartDeviceReplyData;
import com.ido.ble.protocol.model.AppExchangeDataStartPara;
import com.ido.ble.protocol.model.AppExchangeDataStopDeviceReplyData;
import com.ido.ble.protocol.model.AppExchangeDataStopPara;
import com.ido.ble.protocol.model.DeviceNoticeAppExchangeDataPauseAppReplyData;
import com.ido.ble.protocol.model.DeviceNoticeAppExchangeDataPausePara;
import com.ido.ble.protocol.model.DeviceNoticeAppExchangeDataResumeAppReplyData;
import com.ido.ble.protocol.model.DeviceNoticeAppExchangeDataResumePara;
import com.ido.ble.protocol.model.DeviceNoticeAppExchangeDataStopAppReplyData;
import com.ido.ble.protocol.model.DeviceNoticeAppExchangeDataStopPara;

import java.util.Calendar;

import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;

public class AppExchangeActivity extends BaseAutoConnectActivity {
    private TextView tvReceiveDeviceNotice, tvReceiveDeviceFeedbackData, tvAppSendPara;

    private int mDuration = 0;
    private int mCalories = 0;
    private int mDistance = 0;

    private int mDay = 0;
    private int mHour = 0;
    private int mMin = 0;
    private int mSecond = 0;

    private AppExchangeDataCallBack.ICallBack iCallBack = new AppExchangeDataCallBack.ICallBack() {


        @Override
        public void onReplyExchangeDataStart(AppExchangeDataStartDeviceReplyData data) {
            tvReceiveDeviceFeedbackData.setText(data.toString());
        }

        @Override
        public void onReplyExchangeDateIng(AppExchangeDataIngDeviceReplyData data) {
            tvReceiveDeviceFeedbackData.setText(data.toString());
        }

        @Override
        public void onReplyExchangeDateStop(AppExchangeDataStopDeviceReplyData data) {
            tvReceiveDeviceFeedbackData.setText(data.toString());
        }

        @Override
        public void onReplyExchangeDatePause(AppExchangeDataPauseDeviceReplyData data) {
            tvReceiveDeviceFeedbackData.setText(data.toString());
        }

        @Override
        public void onReplyExchangeDateResume(AppExchangeDataResumeDeviceReplyData data) {
            tvReceiveDeviceFeedbackData.setText(data.toString());
        }




        @Override
        public void onDeviceNoticeAppStop(DeviceNoticeAppExchangeDataStopPara para) {
            tvReceiveDeviceNotice.setText(para.toString());

            DeviceNoticeAppExchangeDataStopAppReplyData data = new DeviceNoticeAppExchangeDataStopAppReplyData();
            data.err_code = DeviceNoticeAppExchangeDataStopAppReplyData.CODE_SUCCESS;
            data.duration = mDuration;
            data.calories = mCalories;
            data.distance = mDistance;
            BLEManager.replyDeviceNoticeAppExchangeDataStop(data);
        }

        @Override
        public void onDeviceNoticeAppPause(DeviceNoticeAppExchangeDataPausePara para) {
            tvReceiveDeviceNotice.setText(para.toString());

            DeviceNoticeAppExchangeDataPauseAppReplyData data = new DeviceNoticeAppExchangeDataPauseAppReplyData();
            data.err_code = DeviceNoticeAppExchangeDataPauseAppReplyData.CODE_SUCCESS;
            BLEManager.replyDeviceNoticeAppExchangeDataPause(data);
        }

        @Override
        public void onDeviceNoticeAppResume(DeviceNoticeAppExchangeDataResumePara para) {
            tvReceiveDeviceNotice.setText(para.toString());

            DeviceNoticeAppExchangeDataResumeAppReplyData data = new DeviceNoticeAppExchangeDataResumeAppReplyData();
            data.err_code = DeviceNoticeAppExchangeDataResumeAppReplyData.CODE_SUCCESS;
            BLEManager.replyDeviceNoticeAppExchangeDataResume(data);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_exchange);
        tvReceiveDeviceNotice = (TextView) findViewById(R.id.app_exchange_data_receive_device_notice);
        tvReceiveDeviceFeedbackData = (TextView) findViewById(R.id.app_exchange_data_receive_device_feedback_data);
        tvAppSendPara = (TextView) findViewById(R.id.app_exchange_data_app_send_para);
        BLEManager.registerAppExchangeDataCallBack(iCallBack);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BLEManager.unregisterAppExchangeDataCallBack(iCallBack);
    }

    public void start(View v){
        AppExchangeDataStartPara para = new AppExchangeDataStartPara();
        para.day = mDay =Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        para.hour = mHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        para.minute = mMin = Calendar.getInstance().get(Calendar.MINUTE);
        para.second = mSecond = Calendar.getInstance().get(Calendar.SECOND);

        para.sportType = AppExchangeDataStartPara.SPORT_TYPE_DANCING;
        para.target_type = AppExchangeDataStartPara.TARGET_TYPE_DURATIONS;
        para.target_value = 1;
        para.force_start = AppExchangeDataStartPara.FORCE_START_VALID;

        tvAppSendPara.setText(para.toString());
        BLEManager.appExchangeDataStart(para);

    }


    public void sending(View v){

        mDuration += 15;
        mCalories += 100;
        mDistance += 1;

        AppExchangeDataIngPara para = new AppExchangeDataIngPara();
        para.day = mDay ;
        para.hour = mHour ;
        para.minute = mMin ;
        para.second = mSecond ;

        para.status = AppExchangeDataIngPara.STATUS_ALL_VALID;
        para.duration = mDuration;
        para.calories = mCalories;
        para.distance = mDistance;

        tvAppSendPara.setText(para.toString());
        BLEManager.appExchangeDataIng(para);

    }
    public void stop(View v){
        AppExchangeDataStopPara para = new AppExchangeDataStopPara();
        para.day = mDay ;
        para.hour = mHour ;
        para.minute = mMin ;
        para.second = mSecond ;

        para.durations = mDuration;
        para.calories = mCalories;
        para.distance = mDistance;
        para.sport_type = AppExchangeDataStartPara.SPORT_TYPE_DANCING;
        para.is_save = AppExchangeDataStopPara.IS_SAVE_YES;

        tvAppSendPara.setText(para.toString());
        BLEManager.appExchangeDataStop(para);

    }
    public void pause(View v){
        AppExchangeDataPausePara para = new AppExchangeDataPausePara();
        para.day = mDay ;
        para.hour = mHour ;
        para.minute = mMin ;
        para.second = mSecond ;

        tvAppSendPara.setText(para.toString());
        BLEManager.appExchangeDataPause(para);

    }
    public void resume(View v){

        AppExchangeDataResumePara para = new AppExchangeDataResumePara();
        para.day = mDay ;
        para.hour = mHour ;
        para.minute = mMin ;
        para.second = mSecond ;

        tvAppSendPara.setText(para.toString());
        BLEManager.appExchangeDataResume(para);
    }
}
