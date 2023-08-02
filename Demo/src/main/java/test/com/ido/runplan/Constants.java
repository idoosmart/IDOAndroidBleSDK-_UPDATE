package test.com.ido.runplan;

public interface Constants {
    /**
     * 用户点击终止计划，通知app停止计划；
     */
    String STOP_SLEEP_PLAN = "stopSleepPlan";
    /**
     * 用户点击终止体重计划，通知app停止计划；
     */
    String STOP_WEIGHT_PLAN = "stopWeightPlan";
    /**
     * 用户点击终止计划，通知app停止计划；
     */
    String BACK_TO_APP = "backToApp";
    /**
     * 用户点击终止计划，通知app停止计划；
     */
    String BACK_TO_LOGIN = "backToLogin";
    String APP_KEY = "299dce40192d40f6aee295ef489bc094";//mentech的
    /**
     * 表示开启计划，通知用户打开更新数据信息；
     */
    String SEND_NOTIFICATION_TOWEB = "sendNotificationToWeb";
    /**
     * 来源跑步计划
     */
    String INTENT_FROM_RUN_PLAN = "intent_form_run_plan";
    String INTENT_URL = "intent_url";
    /**
     * 健康管理页面来源
     */
    String INTENT_FROM_MANAGER = "intent_form_manager";
    String OPEN_SYNC_HEALTHINFO = "openSyncHealthInfo";

    String TOKEN = "Bearer_token";
    int GPS_INVALID = 0x00;    //无效
    int GPS_VALID = 0x01;    //有效
    int GPS_BAD = 0x02;    //GPS信号弱
}
