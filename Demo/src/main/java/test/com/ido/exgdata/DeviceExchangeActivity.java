package test.com.ido.exgdata;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.ido.ble.BLEManager;
import com.ido.ble.callback.DeviceExchangeDataCallBack;
import com.ido.ble.protocol.model.DeviceExchangeDataIngAppReplyPara;
import com.ido.ble.protocol.model.DeviceExchangeDataIngPara;
import com.ido.ble.protocol.model.DeviceExchangeDataPauseAppReplyData;
import com.ido.ble.protocol.model.DeviceExchangeDataPausePara;
import com.ido.ble.protocol.model.DeviceExchangeDataResumeAppReplyData;
import com.ido.ble.protocol.model.DeviceExchangeDataResumePara;
import com.ido.ble.protocol.model.DeviceExchangeDataStartAppReplyData;
import com.ido.ble.protocol.model.DeviceExchangeDataStartPara;
import com.ido.ble.protocol.model.DeviceExchangeDataStopAppReplyData;
import com.ido.ble.protocol.model.DeviceExchangeDataStopPara;

import test.com.ido.R;

public class DeviceExchangeActivity extends Activity {

    private TextView tvReceiveDeviceCmd;

    //test
    private int mDistance = 0;
    private DeviceExchangeDataCallBack.ICallBack iCallBack = new DeviceExchangeDataCallBack.ICallBack() {
        @Override
        public void onExchangeDataStart(DeviceExchangeDataStartPara para) {
            tvReceiveDeviceCmd.setText(para.toString());

            DeviceExchangeDataStartAppReplyData replyPara = new DeviceExchangeDataStartAppReplyData();
            replyPara.ret_code = DeviceExchangeDataStartAppReplyData.CODE_SUCCESS;
            BLEManager.replyDeviceExchangeDataStart(replyPara);
        }

        @Override
        public void onExchangeDateIng(DeviceExchangeDataIngPara para) {
            tvReceiveDeviceCmd.setText(para.toString());

            DeviceExchangeDataIngAppReplyPara replyPara = new DeviceExchangeDataIngAppReplyPara();
            replyPara.distance = mDistance;
            mDistance += 0.1;
            BLEManager.replyDeviceExchangeDataIng(replyPara);
        }

        @Override
        public void onExchangeDateStop(DeviceExchangeDataStopPara para) {
            tvReceiveDeviceCmd.setText(para.toString());

            DeviceExchangeDataStopAppReplyData replyPara = new DeviceExchangeDataStopAppReplyData();
            replyPara.ret_code = DeviceExchangeDataStopAppReplyData.CODE_SUCCESS;
            replyPara.distance = mDistance;
            BLEManager.replyDeviceExchangeDataStop(replyPara);
        }

        @Override
        public void onExchangeDatePause(DeviceExchangeDataPausePara para) {
            tvReceiveDeviceCmd.setText(para.toString());

            DeviceExchangeDataPauseAppReplyData replyPara = new DeviceExchangeDataPauseAppReplyData();
            replyPara.ret_code = DeviceExchangeDataPauseAppReplyData.CODE_SUCCESS;
            BLEManager.replyDeviceExchangeDataPause(replyPara);
        }

        @Override
        public void onExchangeDateResume(DeviceExchangeDataResumePara para) {
            tvReceiveDeviceCmd.setText(para.toString());

            DeviceExchangeDataResumeAppReplyData replyPara = new DeviceExchangeDataResumeAppReplyData();
            replyPara.ret_code = DeviceExchangeDataResumeAppReplyData.CODE_SUCCESS;
            BLEManager.replyDeviceExchangeDataResume(replyPara);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_exchange);
        tvReceiveDeviceCmd = (TextView) findViewById(R.id.device_exchange_data_receive_device_cmd);
        BLEManager.registerDeviceExchangeDataCallBack(iCallBack);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BLEManager.unregisterDeviceExchangeDataCallBack(iCallBack);
    }
}
