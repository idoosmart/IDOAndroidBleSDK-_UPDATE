package test.com.ido.CallBack;

import com.ido.ble.callback.GetDeviceInfoCallBack;
import com.ido.ble.protocol.model.ActivityDataCount;
import com.ido.ble.protocol.model.BasicInfo;
import com.ido.ble.protocol.model.BatteryInfo;
import com.ido.ble.protocol.model.CanDownLangInfo;
import com.ido.ble.protocol.model.CanDownLangInfoV3;
import com.ido.ble.protocol.model.DeviceSummarySoftVersionInfo;
import com.ido.ble.protocol.model.FlashBinInfo;
import com.ido.ble.protocol.model.HIDInfo;
import com.ido.ble.protocol.model.LiveData;
import com.ido.ble.protocol.model.MacAddressInfo;
import com.ido.ble.protocol.model.NoticeReminderSwitchStatus;
import com.ido.ble.protocol.model.NoticeSwitchInfo;
import com.ido.ble.protocol.model.SNInfo;
import com.ido.ble.protocol.model.SupportFunctionInfo;
import com.ido.ble.protocol.model.SystemTime;

public class BaseGetDeviceInfoCallBack implements GetDeviceInfoCallBack.ICallBack {
    @Override
    public void onGetBasicInfo(BasicInfo basicInfo) {

    }

    @Override
    public void onGetFunctionTable(SupportFunctionInfo supportFunctionInfo) {

    }

    @Override
    public void onGetTime(SystemTime time) {

    }

    @Override
    public void onGetMacAddress(MacAddressInfo macAddressInfo) {

    }

    @Override
    public void onGetBatteryInfo(BatteryInfo batteryInfo) {

    }

    @Override
    public void onGetSNInfo(SNInfo snInfo) {

    }

    @Override
    public void onGetNoticeCenterSwitchStatus(NoticeSwitchInfo info) {

    }

    @Override
    public void onGetLiveData(LiveData liveData) {

    }

    @Override
    public void onGetHIDInfo(HIDInfo hidInfo) {

    }

    @Override
    public void onGetActivityCount(ActivityDataCount activityDataCount) {

    }

    @Override
    public void onGetDeviceSummarySoftVersionInfo(DeviceSummarySoftVersionInfo info) {

    }

    @Override
    public void onGetCanDownloadLangInfo(CanDownLangInfo canDownLangInfo) {

    }

    @Override
    public void onGetFlashBinInfo(FlashBinInfo flashBinInfo) {

    }

    @Override
    public void onGetCanDownloadLangInfoV3(CanDownLangInfoV3 canDownLangInfoV3) {

    }

    @Override
    public void onGetNoticeReminderSwitchStatus(NoticeReminderSwitchStatus noticeReminderSwitchStatus) {
        
    }
}
