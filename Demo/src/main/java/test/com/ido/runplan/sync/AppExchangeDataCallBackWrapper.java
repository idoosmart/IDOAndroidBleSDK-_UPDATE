package test.com.ido.runplan.sync;

import com.ido.ble.callback.AppExchangeDataCallBack;
import com.ido.ble.protocol.model.AppExchangeDataIngDeviceReplyData;
import com.ido.ble.protocol.model.AppExchangeDataPauseDeviceReplyData;
import com.ido.ble.protocol.model.AppExchangeDataResumeDeviceReplyData;
import com.ido.ble.protocol.model.AppExchangeDataStartDeviceReplyData;
import com.ido.ble.protocol.model.AppExchangeDataStopDeviceReplyData;
import com.ido.ble.protocol.model.DeviceNoticeAppExchangeDataPausePara;
import com.ido.ble.protocol.model.DeviceNoticeAppExchangeDataResumePara;
import com.ido.ble.protocol.model.DeviceNoticeAppExchangeDataStopPara;

/**
 * Copyright (c) 2019 深圳市爱都科技有限公司. All rights reserved.
 *
 * @Author: xyb
 * @CreateDate: 2020-05-06 19:44
 * @description
 */
public class AppExchangeDataCallBackWrapper implements AppExchangeDataCallBack.ICallBack{
    @Override
    public void onReplyExchangeDataStart(AppExchangeDataStartDeviceReplyData appExchangeDataStartDeviceReplyData) {

    }

    @Override
    public void onReplyExchangeDateIng(AppExchangeDataIngDeviceReplyData appExchangeDataIngDeviceReplyData) {

    }

    @Override
    public void onReplyExchangeDateStop(AppExchangeDataStopDeviceReplyData appExchangeDataStopDeviceReplyData) {

    }

    @Override
    public void onReplyExchangeDatePause(AppExchangeDataPauseDeviceReplyData appExchangeDataPauseDeviceReplyData) {

    }

    @Override
    public void onReplyExchangeDateResume(AppExchangeDataResumeDeviceReplyData appExchangeDataResumeDeviceReplyData) {

    }

    @Override
    public void onDeviceNoticeAppStop(DeviceNoticeAppExchangeDataStopPara deviceNoticeAppExchangeDataStopPara) {

    }

    @Override
    public void onDeviceNoticeAppPause(DeviceNoticeAppExchangeDataPausePara deviceNoticeAppExchangeDataPausePara) {

    }

    @Override
    public void onDeviceNoticeAppResume(DeviceNoticeAppExchangeDataResumePara deviceNoticeAppExchangeDataResumePara) {

    }
}
