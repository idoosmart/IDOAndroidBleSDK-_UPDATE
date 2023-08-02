package test.com.ido.runplan;

public class H5ToAppConstant {
    /**
     *.4.app通知h5   sendNotificationToWeb
     |
     | 'runningPlanStartTraining'                    | 表示开始训练，通知 h5                                        |
     | --------------------------------------------- | ------------------------------------------------------------ |
     | 'runningPlanSuspendTraining'                  | 表示暂停训练，通知 h5 暂停训练                               |
     | 'runningPlanRenewTraining'                    | 表示恢复训练，通知 h5 恢复训练                               |
     | 'runningPlanStopTraining'                     | 表示停止训练，通知 h5 停止训练                               |
     | 'runningPlanSyncDeviceData'                   | 表示同步设备数据到 app，通知 h5 弹窗提示用户，并禁止页面操作 |
     | 'runningPlanIsSetPlanSuccess'                 | 表示设置计划成功，通知 h5 弹窗提示用户                       |
     | 'runningPlanIsSetPlanFail'                    | 表示设置计划失败，通知 h5 弹窗提示用户
     | 'runningPlanConnectingDeviceSuccessAndRateNoRun'   | 表示连接设备成功，且不支持跑步计划，但支持心率，通知 h5 弹窗提示用户 |
     | -------------------------------------------------- | ------------------------------------------------------------ |
     | 'runningPlanConnectingDeviceSuccessAndNoRateNoRun' | 表示连接设备成功，且不支持心率，通知 h5 弹窗提示用户         ||
     | 'runningPlanIsSynchroPlanSuccess'             | 表示同步计划成功，通知 h5 弹窗提示用户                       |
     | 'runningPlanIsSynchroPlanFail'                | 表示同步计划失败，通知 h5 弹窗提示用户                       |
     | 'runningPlanIsEndPlanSuccess'                 | 表示结束计划成功，通知 h5 弹窗提示用户                       |
     | 'runningPlanIsEndPlanFail'                    | 表示结束计划失败，通知 h5 弹窗提示用户                       |
     | 'runningPlanConnectingDeviceSuccessAndSame'   | 表示连接设备成功，通知 h5 弹窗提示用户                       |
     | 'runningPlanConnectingDeviceSuccessAndNull'   | 表示连接设备成功，且跑步计划为空，通知 h5 弹窗提示用户       |
     | 'runningPlanConnectingDeviceSuccessAndNoSame' | 表示连接设备成功，且设备跑步计划与用户计划不一致，通知 h5 弹窗提示用户 |
     | 'runningPlanConnectingDeviceFail'             | 表示连接设备失败，通知 h5 弹窗提示用户                       |
     | 'runningPlanConnectingBluetoothSuccess'       | 表示连接蓝牙连接成功，通知 h5                                |
     | 'runningPlanConnectingDeviceFail'             | 表示失败，通知 h5 弹窗提示用户                               |         |
     */
    public static String RUNNING_PLAN_SET_PLAN="runningPlanSetPlan";
    public static String SEND_JSON_DATA_TO_APP="sendJsonDataToApp";
    public static String SEND_NOTIFICATION_TO_APP="sendNotificationToAPP";
    public static String RUNNING_PLAN_TOGGLE_TRAINING="runningPlanToggleTraining";
    public static String SEND_NOTIFICATION_TO_WEB="sendNotificationToWeb";
    public static String RUNNING_PLAN_START_TRAINING="runningPlanStartTraining";
    public static String RUNNING_PLAN_SUSPEND_TRAINING="runningPlanSuspendTraining";
    public static String RUNNING_PLAN_RENEW_TRAINING="runningPlanRenewTraining";
    public static String RUNNING_PLAN_STOP_TRAINING="runningPlanStopTraining";
    public static String RUNNING_PLAN_START_TRAINING_FAIL="runningPlanStartTrainingFail";
    public static String RUNNING_PLAN_SUSPEND_TRAINING_FAIL="runningPlanSuspendTrainingFail";
    public static String RUNNING_PLAN_RENEW_TRAINING_FAIL="runningPlanRenewTrainingFail";
    public static String RUNNING_PLAN_STOP_TRAINING_FAIL="runningPlanStopTrainingFail";


    public static String RUNNING_PLAN_SYNC_DEVICE_DATA="runningPlanSyncDeviceData";
    public static String RUNNING_PLAN_ISSET_PLAN_SUCCESS="runningPlanIsSetPlanSuccess";
    public static String RUNNING_PLAN_ISSET_PLAN_FAIL="runningPlanIsSetPlanFail";
    public static String RUNNING_PLAN_CONNECTING_DEVICE_SUCCESS="runningPlanConnectingDeviceSuccess";
    public static String RUNNING_PLAN_CONNECTING_DEVICE_FAIL="runningPlanConnectingDeviceFail";
    public static String RUNNING_PLAN_SEND_IMAGE_MESSAGE="runningPlanSendImageMessage";
    public static String RUNNING_PLAN_LOCK_SCREEN="runningPlanLockScreen";//表示锁屏，app控制手机锁屏（训练中，能否禁用手机返回键，主要是安卓下方的操作按钮）
    public static String GOTOEXERCISE="goToExercise";

    public static String RUNNING_PLAN_OPEN_VIBRATION="runningPlanOpenVibration";//表示开始震动+提示音
    public static String RUNNING_PLAN_OPEN_VIBRATION_TWO="runningPlanOpenVibrationTwo";//表示开始震动+提示音
    public static String BLUETOOTH_DEVICE_LIST="bluetoothDeviceList";//表示告知app端返回蓝牙设备列表
    public static String CONNECT_DEVICE_WEIGHT_PLAN="connectdeviceweightplan";//表示告知app端去鏈接設備
    public static String RUNNING_PLAN_SEND_ACTION_TOGGLE_DATA_TO_WEB="runningPlanSendActionToggleDataToWeb";
    public static String RUNNING_PLAN_SEND_TRAINING_DATA_TO_WEB="runningPlanSendTrainingDataToWeb";
    public static String GET_BLUETOOTH_DEVICE_LIST_TO_WEB="getBluetoothDeviceListToWeb";
    public static String CONNECT_BLUETOOTH_DEVICE_TO_APP="connectBluetoothDeviceToApp";
    public static String RUNNING_PLAN_GET_USER_INFO_APP="runningPlanGetUserInfoAPP";
    public static String GET_USER_INFO_APP="getUserInfoAPP";//1.获取用户信息  runningPlanGetUserInfoAPP
    public static String SET_USER_INFO_MEDAL="setUserInfoMedal";//1.获取用户信息  runningPlanGetUserInfoAPP
    public static String PLAY_MUSIC_APP ="playMusicApp ";//播放音乐信息给H5
    public static String BACK_TO_BOUND_DEVICE="backToBoundDevice";//表示告知app端返回蓝牙设备列表
    public static String ADDEDFOOD="addedFood";//标示用户体重计划添加了食物  需要计算奖章

    public static String CONNECT_DEVICE_ALL_STATUS="connectDeviceAllStatus";//表示告知app端返回蓝牙设备列表
    public static String RUNNING_PLAN_ALL_DEVICE_STATUS="runningPlanAllDeviceStatus";//把状态的返回H5
    public static String RUNNING_PLAN_CONNECTING_DEVICE_SUCCESS_AND_SAME="runningPlanConnectingDeviceSuccessAndRateRunSame";//表示连接设备成功
    public static String RUNNING_PLAN_CONNECTING_DEVICE_SUCCESS_AND_NO_SAME="runningPlanConnectingDeviceSuccessAndRateRunNull";//表示连接设备成功，且设备跑步计划与用户计划不一致，通知 h5 弹窗提示用户 |
    public static String RUNNING_PLAN_CONNECTING_DEVICE_SUCCESS_AND_NULL="runningPlanConnectingDeviceSuccessAndNull";//表示连接设备成功，且跑步计划为空，通知 h5 弹窗提示用户
  //  public static String RUNNING_PLAN_CONNECTING_DEVICE_SUCCESS_AND_NO_SAME="runningPlanConnectingDeviceSuccessAndRateNoRun";//表示连接设备成功，且设备跑步计划与用户计划不一致，通知 h5 弹窗提示用户 |
 //   public static String RUNNING_PLAN_CONNECTING_DEVICE_SUCCESS_AND_NULL="runningPlanConnectingDeviceSuccessAndNull";//表示连接设备成功，且跑步计划为空，通知 h5 弹窗提示用户       |//  |
    public static String RUNNING_PLAN_IS_END_PLAN_SUCCESS="runningPlanIsEndPlanSuccess";
    public static String RUNNING_PLAN_IS_END_PLAN_FAIL="runningPlanIsEndPlanFail";
    public static String RUNNING_PLAN_CONNECTING_DEVICE_SUCCESS_AND_RATE_NO_RUN="runningPlanConnectingDeviceSuccessAndRateNoRun";
    public static String RUNNING_PLAN_CONNECTING_DEVICE_SUCCESS_AND_NO_RATE_NO_RUN="runningPlanConnectingDeviceSuccessAndNoRateNoRun";
    public static String CLOSE_SOUND="closeSound";
    public static String OPEN_SOUND="openSound";
    public static String RUNNING_PLAN_CONNECTING_DEVICE_STATUS="runningPlanConnectingDeviceStatus";
    public static String RUNNING_PLAN_SEND_AVERAGE_HEART_RATE_TO_WEB="runningPlanSendAverageHeartRateToWeb";
    public static String RUNNING_PLAN_CURRENT_TRAINING_RECORD_TO_WEB="runningPlanCurrentTrainingRecordToWeb";
    public static String RUNNING_PLAN_CONNECTING_BLUETOOTH_SUCCESS="runningPlanConnectingBluetoothSuccess";
    public static String RUNNING_PLAN_CONNECTING_BLUETOOTH_FAIL="runningPlanConnectingBluetoothFail";
}
