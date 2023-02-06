package test.com.ido.CallBack;

import com.ido.ble.callback.GetDeviceParaCallBack;
import com.ido.ble.protocol.model.ActivitySwitch;
import com.ido.ble.protocol.model.AlarmV3;
import com.ido.ble.protocol.model.AllHealthMonitorSwitch;
import com.ido.ble.protocol.model.BtA2dpHfpStatus;
import com.ido.ble.protocol.model.CalorieAndDistanceGoal;
import com.ido.ble.protocol.model.DeviceBeepInfo;
import com.ido.ble.protocol.model.DeviceUpgradeState;
import com.ido.ble.protocol.model.FirmwareAndBt3Version;
import com.ido.ble.protocol.model.MenuList;
import com.ido.ble.protocol.model.NotDisturbPara;
import com.ido.ble.protocol.model.PressCalibrationValue;
import com.ido.ble.protocol.model.ScheduleReminderV3;
import com.ido.ble.protocol.model.ScreenBrightness;
import com.ido.ble.protocol.model.SupportSportInfoV3;
import com.ido.ble.protocol.model.UpHandGesture;
import com.ido.ble.protocol.model.WalkReminder;

import java.util.List;

/**
 * @author tianwei
 * @date 2023/2/3
 * @time 9:14
 * 用途:
 */
public class BaseGetDeviceParaCallBack implements GetDeviceParaCallBack.ICallBack {
    @Override
    public void onGetDoNotDisturbPara(NotDisturbPara notDisturbPara) {

    }

    @Override
    public void onGetAlarmV3(List<AlarmV3> list) {

    }

    @Override
    public void onGetScheduleReminderV3(List<ScheduleReminderV3> list) {

    }

    @Override
    public void onGetSupportSportInfoV3(SupportSportInfoV3 supportSportInfoV3) {

    }

    @Override
    public void onGetScreenBrightness(ScreenBrightness screenBrightness) {

    }

    @Override
    public void onGetUpHandGesture(UpHandGesture upHandGesture) {

    }

    @Override
    public void onGetDeviceUpgradeState(DeviceUpgradeState deviceUpgradeState) {

    }

    @Override
    public void onGetMenuList(MenuList.DeviceReturnInfo deviceReturnInfo) {

    }

    @Override
    public void onGetWalkReminder(WalkReminder walkReminder) {

    }

    @Override
    public void onGetSportThreeCircleGoal(CalorieAndDistanceGoal calorieAndDistanceGoal, String s) {

    }

    @Override
    public void onGetActivitySwitch(ActivitySwitch activitySwitch) {

    }

    @Override
    public void onGetAllHealthMonitorSwitch(AllHealthMonitorSwitch allHealthMonitorSwitch) {

    }

    @Override
    public void onGetFirmwareAndBt3Version(FirmwareAndBt3Version firmwareAndBt3Version) {

    }

    @Override
    public void onGetPressCalibrationValue(PressCalibrationValue pressCalibrationValue) {

    }

    @Override
    public void onGetBtA2dpHfpStatus(BtA2dpHfpStatus btA2dpHfpStatus) {

    }

    @Override
    public void onGetContactReceiveTime(boolean b) {

    }

    @Override
    public void onGetDeviceBeepInfo(DeviceBeepInfo deviceBeepInfo) {

    }
}
