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
     * app???????????????????????????????????????
     */
    private AppExchangeDataStartPara switchDataAppStart = new AppExchangeDataStartPara();
    /**
     * app???????????????????????????????????????
     */
    private AppExchangeDataIngPara switchDataAppIng = new AppExchangeDataIngPara();

    private long changeIndex;
    private long lastCurrentTimeMillis = 0;
    /**
     * ???????????????????????????????????????
     */
    private int sendDistance = 0;

    /**
     * ????????????
     */
    private final int SPORT_STATE_NONE = 0;
    private final int SPORT_STATE_RUNNING = 1;
    private final int SPORT_STATE_STOP = 2;
    private final int SPORT_STATE_PAUSE = 3;
    /**
     * app????????????????????????????????????
     */
    private V3AppExchangeDataIngPara mV3SwitchDataAppIng = new V3AppExchangeDataIngPara();
    /**
     * ??????????????????????????????false,
     * ??????????????????APP???????????????true
     */
    private boolean isAppComplete;

    int GPS_VALID = 0x00;    //??????
    int GPS_INVALID = 0x01;    //??????
    int GPS_BAD = 0x02;    //GPS?????????
    /**
     * gps?????????
     */
    private int gpsSignValue = GPS_INVALID;

    private int sportState = SPORT_STATE_NONE;

    // ble??????????????????????????????????????????
    final  static  int BLE_START_SUCCESS = 0x00;    //??????
     final  static  int BLE_START_SPORT_FAIL = 0x01;    //????????????????????????????????????
    final  static  int BLE_START_LOW_POWER = 0x02;    //?????????????????????
    /**
     * ???????????????????????????
     */
    private LinkedHashMap<Integer, int[]> serialHeartRate = new LinkedHashMap<>();

    /**
     * ??????
     */
    private List<Integer> heartRateList = new ArrayList<>();
    private int heartRate;
    private int maxHeartRate = 0;
    // ??????????????????
    int NO_SAVE_END = 0x00;    //????????? not save
    int SAVE_END = 0x01;    //?????? save

    /**
     * ??????????????????
     */
    protected boolean isCompleteRun = true;
    /**
     * ????????????
     */
    private int type;

    /**
     * v3??????
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
                //????????????????????????0????????????????????????
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
     * v3???????????????
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
     * ??????????????????v3????????????v2??????
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
         * todo ???????????????????????????,??????????????????????????????????????? ?????????type ??????
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
         * todo ????????????ui update ui
         */
    }

    /**
     * sport resume
     * @param v
     */
    public void resume(View v){
        BleSdkWrapper.appSwitchRestore(switchDataAppStart.day, switchDataAppStart.hour, switchDataAppStart.minute, switchDataAppStart.second);
        /**
         * todo ??????ui   update ui
         */
    }

    /**
     * ??????????????????2S??????changeCmd??????
     * ???1S??????sportRunning??????
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
                    //30 ???????????????????????????
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
     * ????????????
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
     * ?????????????????????????????????
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
            //????????????????????????...
            case BLE_START_SUCCESS://??????
                sportStartSuccess();
                break;
            case BLE_START_SPORT_FAIL://????????????????????????????????????
                //todo ??????ui ??????  update ui
                break;
            case BLE_START_LOW_POWER://?????????????????????
                //todo ??????ui ??????  update ui
                break;
            case 3://
                //todo ??????ui ?????? update ui
                break;
        }
    }

    /**
     * ??????????????????
     */
    private void  sportStartSuccess() {
        sportState = SPORT_STATE_RUNNING;
        initTrainDomain();
        changeIndex = 0;
        gpsSignValue = GPS_INVALID;
        lastCurrentTimeMillis = System.currentTimeMillis() / 1000;
        updateHandler.removeCallbacks(runTimerTask);
        updateHandler.post(runTimerTask);// ???????????????????????????
        /**
         * todo update sport start ui
         */

    }

    /**
     * ?????????????????????
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
         * todo ???????????????????????????ui?????????????????????????????????(save sport date,update ui ,stop RunTimerTask, sport end)
         */
    }

    private void pauseSuccess() {
        sportState = SPORT_STATE_PAUSE;
        /**
         * todo ??????ui update ui sport pause
         */
    }

    private void resumeSuccess() {
        sportState = SPORT_STATE_RUNNING;
        /**
         * todo ??????ui update ui sport resume
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
