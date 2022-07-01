package test.com.ido.exgdata.demo;

import static com.ido.ble.protocol.model.AppExchangeDataStartPara.FORCE_START_INVALID;

import android.os.Bundle;
import android.os.Handler;
import android.text.format.Time;
import android.view.View;
import android.widget.TextView;

import com.ido.ble.BLEManager;
import com.ido.ble.LocalDataManager;
import com.ido.ble.callback.AppExchangeDataCallBack;
import com.ido.ble.callback.V3AppExchangeDataCallBack;
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
import com.ido.ble.protocol.model.SportType;
import com.ido.ble.protocol.model.SupportFunctionInfo;
import com.ido.ble.protocol.model.V3AppExchangeDataDeviceReplayEndData;
import com.ido.ble.protocol.model.V3AppExchangeDataHeartRate;
import com.ido.ble.protocol.model.V3AppExchangeDataIngDeviceReplyData;
import com.ido.ble.protocol.model.V3AppExchangeDataIngPara;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TimerTask;

import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;
import test.com.ido.utils.DateUtil;

/**
 * sport demo ,IDO sport Interactive logic
 * for reference
 */
public class SportRunDemoActivity extends BaseAutoConnectActivity {
    private SportRunData trainDomain;
    private Handler updateHandler = new Handler();
    RunTimerTask runTimerTask = new RunTimerTask();
    /**
     * app和手环交互开始时的数据结构
     */
    private AppExchangeDataStartPara switchDataAppStart = new AppExchangeDataStartPara();
    /**
     * app和手环交互数据时的数据结构
     */
    private AppExchangeDataIngPara switchDataAppIng = new AppExchangeDataIngPara();

    private long changeIndex;
    private long lastCurrentTimeMillis = 0;
    /**
     * 交互数据时发送给手环的距离
     */
    private int sendDistance = 0;

    /**
     * 运动状态
     */
    private final int SPORT_STATE_NONE = 0;
    private final int SPORT_STATE_RUNNING = 1;
    private final int SPORT_STATE_STOP = 2;
    private final int SPORT_STATE_PAUSE = 3;
    /**
     * app和手环交互数据的数据结构
     */
    private V3AppExchangeDataIngPara mV3SwitchDataAppIng = new V3AppExchangeDataIngPara();
    /**
     * 连接手环一起运动时为false,
     * 未连接手环时APP单独运动为true
     */
    private boolean isAppComplete;

    int GPS_VALID = 0x00;    //有效
    int GPS_INVALID = 0x01;    //无效
    int GPS_BAD = 0x02;    //GPS信号弱
    /**
     * gps信号值
     */
    private int gpsSignValue = GPS_INVALID;

    private int sportState = SPORT_STATE_NONE;

    // ble设备回复交换运动数据开始请求
    final  static  int BLE_START_SUCCESS = 0x00;    //成功
     final  static  int BLE_START_SPORT_FAIL = 0x01;    //设备已经进入运动模式失败
    final  static  int BLE_START_LOW_POWER = 0x02;    //设备电量低失败
    /**
     * 带序列号的心率数组
     */
    private LinkedHashMap<Integer, int[]> serialHeartRate = new LinkedHashMap<>();

    /**
     * 心率
     */
    private List<Integer> heartRateList = new ArrayList<>();
    private int heartRate;
    private int maxHeartRate = 0;
    // 手环点击结束
    int NO_SAVE_END = 0x00;    //不保存 not save
    int SAVE_END = 0x01;    //保存 save

    /**
     * 运动是否完成
     */
    protected boolean isCompleteRun = true;
    /**
     * 运动类型
     */
    private int type;

    /**
     * v3协议
     */
    private boolean mIsV3Exchange = false;
    private AppExchangeDataCallBack.ICallBack iCallBack = new AppExchangeDataCallBack.ICallBack() {


        @Override
        public void onReplyExchangeDataStart(AppExchangeDataStartDeviceReplyData data) {
            handlerReplay(data.ret_code);
        }

        @Override
        public void onReplyExchangeDateIng(AppExchangeDataIngDeviceReplyData data) {
            // action sport data
            if (data.status == AppExchangeDataIngDeviceReplyData.STATUS_SUCCESS) {
                int[] hrs = data.hr_value;
                //心率时间间隔大于0的心率数据才有用
                if (hrs != null && hrs.length > 0 && data.interval_second > 0) {

                    serialHeartRate.put(data.hr_value_serial, hrs);
                    for (int j = 0; j < hrs.length; j++) {
                        heartRateList.add(hrs[j]);
                    }
                    int sum = 0, size = heartRateList.size();
                    for (int j = 0; j < size; j++) {
                        sum += heartRateList.get(j);
                        maxHeartRate = Math.max(maxHeartRate, heartRateList.get(j));
                    }

                    trainDomain.setAvg_hr_value(sum / size);
                    trainDomain.setMax_hr_value(maxHeartRate);

                }
                trainDomain.setCalories(data.calories);
                trainDomain.setStep(data.step);
                trainDomain.setDistance(data.distance);
            }
        }

        @Override
        public void onReplyExchangeDateStop(AppExchangeDataStopDeviceReplyData data) {
            stopSuccess();
        }

        @Override
        public void onReplyExchangeDatePause(AppExchangeDataPauseDeviceReplyData data) {
            pauseSuccess();
        }

        @Override
        public void onReplyExchangeDateResume(AppExchangeDataResumeDeviceReplyData data) {
            resumeSuccess();
        }




        @Override
        public void onDeviceNoticeAppStop(DeviceNoticeAppExchangeDataStopPara para) {
            DeviceNoticeAppExchangeDataStopAppReplyData d = new DeviceNoticeAppExchangeDataStopAppReplyData();
            d.err_code = DeviceNoticeAppExchangeDataStopAppReplyData.CODE_SUCCESS;
            if(null != trainDomain) {
                d.calories = trainDomain.getCalories();
                d.duration = trainDomain.getDurations();
                d.distance = trainDomain.getDistance();
            }
            BleSdkWrapper.replyDeviceNoticeAppExchangeDataStop(d);
            stopSuccess();
        }

        @Override
        public void onDeviceNoticeAppPause(DeviceNoticeAppExchangeDataPausePara para) {
            DeviceNoticeAppExchangeDataPauseAppReplyData data = new DeviceNoticeAppExchangeDataPauseAppReplyData();
            data.err_code = DeviceNoticeAppExchangeDataPauseAppReplyData.CODE_SUCCESS;
            BleSdkWrapper.replyDeviceNoticeAppExchangeDataPause(data);
             pauseSuccess();
        }

        @Override
        public void onDeviceNoticeAppResume(DeviceNoticeAppExchangeDataResumePara para) {
            DeviceNoticeAppExchangeDataResumeAppReplyData data = new DeviceNoticeAppExchangeDataResumeAppReplyData();
            data.err_code = DeviceNoticeAppExchangeDataResumeAppReplyData.CODE_SUCCESS;
            BleSdkWrapper.replyDeviceNoticeAppExchangeDataResume(data);
            resumeSuccess();
        }
    };


    /**
     * v3的数据交换
     */
    private V3AppExchangeDataCallBack.ICallBack changeV3CallBack = new V3AppExchangeDataCallBack.ICallBack() {

        @Override
        public void onReplyExchangeDateIng(V3AppExchangeDataIngDeviceReplyData data) {
            // device return step duration data
             //data.steps ,  data.real_time_calories ,data.duration,data.diatance
        }

        @Override
        public void onReplyExchangeDataEndData(V3AppExchangeDataDeviceReplayEndData data) {
              //return data detail , app save data, otherwise ,the motion data can only be obtained after sync

        }

        @Override
        public void onReplyExchangeHeartRateData(V3AppExchangeDataHeartRate data) {
            // hearRata data detail
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_exchange);
        getFunction();
        if(mIsV3Exchange){
            BLEManager.registerV3AppExchangeDataCallBack(changeV3CallBack);
        }
        BLEManager.registerAppExchangeDataCallBack(iCallBack);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BLEManager.unregisterAppExchangeDataCallBack(iCallBack);
    }

    /**
     * 获取功能表走v3协议还是v2协议
     */
    private void getFunction() {
        SupportFunctionInfo supportFunctionInfo = LocalDataManager.getSupportFunctionInfo();
        if (supportFunctionInfo != null && BleSdkWrapper.isConnected()) {
            mIsV3Exchange = supportFunctionInfo.ex_table_main9_v3_activity_exchange_data;
        }
    }

    /**
     * start sport
     * @param v
     */
    public void start(View v){
        switchDataAppStart.force_start = FORCE_START_INVALID;
        /**
         * todo 需要初始化运动类型,发起运动前需要选择一种运动 需要对type 赋值
         * type = SportTypeBean.type
         */
        BleSdkWrapper.appSwitchDataStart(switchDataAppStart);
    }

    /**
     * end sport
     * @param v
     */
    public void stop(View v){
        int durations = trainDomain.getDurations();
        int calories = trainDomain.getCalories();
        int distance = trainDomain.getDistance();
        BleSdkWrapper.appSwitchDataEnd(switchDataAppStart.day, switchDataAppStart.hour, switchDataAppStart.minute, switchDataAppStart.second, type, durations, calories, distance, SAVE_END);

    }

    /**
     * sport pause
     * @param v
     */
    public void pause(View v){
        BleSdkWrapper.appSwitchPause(switchDataAppStart.day, switchDataAppStart.hour, switchDataAppStart.minute, switchDataAppStart.second);
        /**
         * todo 更新暂停ui update ui
         */
    }

    /**
     * sport resume
     * @param v
     */
    public void resume(View v){
        BleSdkWrapper.appSwitchRestore(switchDataAppStart.day, switchDataAppStart.hour, switchDataAppStart.minute, switchDataAppStart.second);
        /**
         * todo 更新ui   update ui
         */
    }

    /**
     * 定时器任务每2S执行changeCmd方法
     * 每1S回调sportRunning方法
     */
    private class RunTimerTask extends TimerTask {
        @Override
        public void run() {
            long currentTimeMillis = System.currentTimeMillis() / 1000;
            if (sportState != SPORT_STATE_RUNNING) {
                return;
            }
            int dtime = (int) (currentTimeMillis - lastCurrentTimeMillis);
            dtime = Math.max(1, dtime);
            trainDomain.setDurations(trainDomain.getDurations() + dtime);
            lastCurrentTimeMillis = currentTimeMillis;
            /**
             * todo update ui ,show trainDomain data ,example distance,calories
             */
            changeIndex++;
            if(!isAppComplete && BleSdkWrapper.isConnected()){
                //support v3
                if (mIsV3Exchange) {
                    if (changeIndex % 2 == 0) {
                        changeV3Cmd();
                    }
                    //30 秒获取一次心率数据
                    if (changeIndex % 30 == 0) {
                        changeV3Rate();
                    }
                    if (changeIndex % 40 == 0) {
                        endV3Cmd();
                    }
                } else {
                    if (changeIndex % 2 == 0) {
                        changeCmd();
                    }
                }
            }

        }
    }

    /**
     * 交换命令
     */
    private void changeCmd() {
        int duration = trainDomain.getDurations();
        int calories = trainDomain.getCalories();
        switchDataAppIng.status = gpsSignValue;
        switchDataAppIng.duration = duration;
        switchDataAppIng.calories = calories;
        switchDataAppIng.distance = sendDistance;
        BleSdkWrapper.appSwitchDataIng(switchDataAppIng);
    }


    /**
     * todo  The user needs to access the map, monitor the location change, and start this method
     *
     */
//    private void locationChange(LocationMessage locationMessage) {
//        gpsSignValue = locationMessage.gpsAccuracyStatus;
//        if (isAppComplete || !BleSdkWrapper.isConnected()) {
//            trainDomain.setDistance(locationMessage.totalDistance);
//        } else {
//            sendDistance = locationMessage.totalDistance;
//        }
//        currentLatLng = locationMessage.getData();
//        if (currentLatLng != null) {
//            latLngDomainList.add(currentLatLng);
//        }
//    }


    /**
     * 初始化交互数据结构对象
     *
     * @param sportType
     * @param target_type
     * @param target_value
     * @param force_start
     */
    public void initSwithchData(int sportType, int target_type, int target_value, int force_start) {
        Time t = new Time(Time.getCurrentTimezone());
        t.setToNow();
        switchDataAppStart.day = t.monthDay;
        switchDataAppStart.hour = t.hour;
        switchDataAppStart.minute = t.minute;
        switchDataAppStart.second = t.second;
        switchDataAppStart.sportType = sportType;
        switchDataAppStart.target_type = target_type;
        switchDataAppStart.target_value = target_value;
        switchDataAppStart.force_start = force_start;

        switchDataAppIng.day = switchDataAppStart.day;
        switchDataAppIng.hour = switchDataAppStart.hour;
        switchDataAppIng.minute = switchDataAppStart.minute;
        switchDataAppIng.second = switchDataAppStart.second;

    }

    private void handlerReplay(int startRelayCode) {
        switch (startRelayCode) {
            //如果开启运动成功...
            case BLE_START_SUCCESS://成功
                sportStartSuccess();
                break;
            case BLE_START_SPORT_FAIL://设备已经进入运动模式失败
                //todo 更新ui 状态  update ui
                break;
            case BLE_START_LOW_POWER://设备电量低失败
                //todo 更新ui 状态  update ui
                break;
            case 3://
                //todo 更新ui 状态 update ui
                break;
        }
    }

    /**
     * 开始运动成功
     */
    private void  sportStartSuccess() {
        sportState = SPORT_STATE_RUNNING;
        initTrainDomain();
        changeIndex = 0;
        gpsSignValue = GPS_INVALID;
        lastCurrentTimeMillis = System.currentTimeMillis() / 1000;
        updateHandler.removeCallbacks(runTimerTask);
        updateHandler.post(runTimerTask);// 启动定时器交互数据
        /**
         * todo update sport start ui
         */

    }

    /**
     * 初始化运动数据
     */
    private void initTrainDomain() {
//        long startTime = DateUtil.getSportStartTime(switchDataAppStart.hour, switchDataAppStart.minute, switchDataAppStart.second);
        trainDomain = new SportRunData();
        trainDomain.setType(type);
        trainDomain.setDate(DateUtil.getDateByHMS(switchDataAppStart.hour, switchDataAppStart.minute, switchDataAppStart.second));
        trainDomain.setDataFrom( SportRunData.DATA_FROM_APP);
        int[] yms = DateUtil.todayYearMonthDay();
        trainDomain.setYear(yms[0]);
        trainDomain.setMonth(yms[1]);
        trainDomain.setDay(yms[2]);
        trainDomain.setHour(switchDataAppStart.hour);
        trainDomain.setMinute(switchDataAppStart.minute);
        trainDomain.setSecond(switchDataAppStart.second);
        trainDomain.setMacAddress(BleSdkWrapper.getBindMac());
    }

    /**
     *
     */
    private void stopSuccess() {
        isCompleteRun = true;
        sportState = SPORT_STATE_STOP;

        BleSdkWrapper.unregisterAppExchangeDataCallBack(iCallBack);

        /**
         * todo 保存数据，已经更新ui，运动结束，停止定时器(save sport date,update ui ,stop RunTimerTask, sport end)
         */
    }

    private void pauseSuccess() {
        sportState = SPORT_STATE_PAUSE;
        /**
         * todo 更新ui update ui sport pause
         */
    }

    private void resumeSuccess() {
        sportState = SPORT_STATE_RUNNING;
        /**
         * todo 更新ui update ui sport resume
         */
    }

    /**
     * v3 get data detail
     *
     */
    public void endV3Cmd() {
        BleSdkWrapper.v3AppSwitchDataEnd();
    }

    /**
     * get v3 heartRate data
     */
    private void changeV3Rate() {
        BleSdkWrapper.getExChangeV3DataHeartRateInterval();
    }

    /**
     * v3 data switch
     */
    private void changeV3Cmd() {
        mV3SwitchDataAppIng.signalFlag = 1;
        mV3SwitchDataAppIng.distance = 200;
        BleSdkWrapper.v3AppSwitchDataIng(mV3SwitchDataAppIng);
    }

}
