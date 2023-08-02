package test.com.ido.runplan.sync;

import com.ido.ble.callback.V3AppExchangeDataCallBack;
import com.ido.ble.protocol.model.V3AppExchangeDataDeviceReplayEndData;
import com.ido.ble.protocol.model.V3AppExchangeDataHeartRate;
import com.ido.ble.protocol.model.V3AppExchangeDataIngDeviceReplyData;


/**
 * Copyright (c) 2019 深圳市爱都科技有限公司. All rights reserved.
 *
 * @Author: xyb
 * @CreateDate: 2020-05-06 19:44
 * @description
 */
public class AppExchangeV3DataCallBackWrapper implements V3AppExchangeDataCallBack.ICallBack{

    @Override
    public void onReplyExchangeDateIng(V3AppExchangeDataIngDeviceReplyData v3AppExchangeDataIngDeviceReplyData) {

    }

    @Override
    public void onReplyExchangeDataEndData(V3AppExchangeDataDeviceReplayEndData v3AppExchangeDataDeviceReplayEndData) {

    }

    @Override
    public void onReplyExchangeHeartRateData(V3AppExchangeDataHeartRate v3AppExchangeDataHeartRate) {

    }
}
