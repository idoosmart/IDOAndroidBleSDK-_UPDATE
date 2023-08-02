package test.com.ido.runplan;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.github.lzyzsd.jsbridge.DefaultHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.ido.ble.BLEManager;
import com.ido.ble.logs.LogTool;
import com.ido.ble.protocol.model.SportPlan;

import test.com.ido.R;

@Deprecated
public class HealthManagerActivity extends Activity implements PageLoadView, IHealthManagerView {

    private CustomWebChromClient mCustomWebChromClient = new CustomWebChromClient();
    private String TAG = "HealthManagerActivity";
    private String url = "";
    private String form = "";
    private boolean isSound = true;
    SetRunPlanH5Info mRunPlanBean;
    private Handler callHandler = new Handler(Looper.getMainLooper());
    private HealthManagerPresenter mPresenter = new HealthManagerPresenter(this);

    private BridgeWebView wv_h5;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_manager_layout);
        initView();
        initData();
    }

    private void initData() {
        url = getIntent().getStringExtra("intent_url");
        form = getIntent().getStringExtra("intent_form_manager");
        WebSettings webSettings = wv_h5.getSettings();
        webSettings.setDomStorageEnabled( true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);  // 不用cache
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true) ;
        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls( false);
        webSettings.setDomStorageEnabled(true) ;
        webSettings.setDatabaseEnabled(true);
        webSettings.setBlockNetworkLoads(false);
        webSettings.setBlockNetworkImage(false);
        webSettings.setUseWideViewPort( true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setAppCacheEnabled(true);

        webSettings.setSupportMultipleWindows(true);
        webSettings.setDomStorageEnabled( true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
//        webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        wv_h5.setDefaultHandler(new DefaultHandler());
        wv_h5.loadUrl(url);
        wv_h5.setWebChromeClient( mCustomWebChromClient);
        //监听H5发送给APP得数据
        H5SendAppDataListener();
        //监听H5发送给APP需要奖章
        //周报分享
        H5RunPlanDataRegisterHandler();
        mPresenter.sendInitDataToWeb();
        //打开健康管理  发送主题色 语言 token appkey到H5
        mPresenter.sportPlanuUnregisterSportPlanCallBack();
        mPresenter.registerCallBack();

        wv_h5.setWebViewClient(new WebViewClient(wv_h5, getApplicationContext(), this));

    }

    private void initView() {
        wv_h5 = findViewById(R.id.wv_h5);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCustomWebChromClient.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void LoadPageFinish() {

    }

    private void H5RunPlanDataRegisterHandler(){
        H5SendAppSport();
        H5SendAppSetPlan();
        H5SendAppWeightPlanNotiData();
    }

    private void H5SendAppWeightPlanNotiData(){

    }

    private void H5SendAppSetPlan(){
        wv_h5.registerHandler(H5ToAppConstant.RUNNING_PLAN_SET_PLAN, new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                LogTool.e(
                        TAG,
                        data.toString()
                );
                //  showToast(data.toString())
                Gson gson = new GsonBuilder().create();
                mRunPlanBean =
                        gson.fromJson(data.toString(), SetRunPlanH5Info.class);
                if (mRunPlanBean != null && mRunPlanBean.getRunningPlanObj() != null) {
                    mPresenter.setCurrRunPlan(mRunPlanBean);
                    if (mRunPlanBean.getRunningPlanObj().getOperate() == 1) {
                        mPresenter.sendSetPlanToDevice(mRunPlanBean, SportPlan.OPERATE_SEND);
                        //制定计划
                    } else if (mRunPlanBean.getRunningPlanObj().getOperate() == 2) {

                    } else {
                        //结束计划
                        SportPlan endSportPlan = new SportPlan();
                        endSportPlan.operate = SportPlan.OPERATE_END;
                        endSportPlan.type = mRunPlanBean.getRunningPlanObj().getType();
                        endSportPlan.year = mRunPlanBean.getRunningPlanObj().getYear();
                        endSportPlan.month =  mRunPlanBean.getRunningPlanObj().getMonth();
                        endSportPlan.day = mRunPlanBean.getRunningPlanObj().getDay();
                        endSportPlan.hour = mRunPlanBean.getRunningPlanObj().getHour();
                        endSportPlan.min =  mRunPlanBean.getRunningPlanObj().getMinute();
                        endSportPlan.sec = mRunPlanBean.getRunningPlanObj().getSecond();
                        BLEManager.setSportPlanEnd(endSportPlan);

                    }
                } else {
                    //下发计划失败
                    mPresenter.sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_ISSET_PLAN_FAIL);
                }
            }
        });
    }

    private void H5SendAppSport() {
        wv_h5.registerHandler(H5ToAppConstant.RUNNING_PLAN_TOGGLE_TRAINING, new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                LogTool.e(
                        TAG,
                        H5ToAppConstant.RUNNING_PLAN_TOGGLE_TRAINING + data.toString()
                );
                Gson gson = new GsonBuilder().create();
                RunPlanToggleH5Info mRunPlanToggleH5Info =
                        gson.fromJson(data.toString(), RunPlanToggleH5Info.class);
                if (mPresenter != null && mPresenter.getSupportFunctionInfo() != null && mPresenter.getSupportFunctionInfo().v3_support_sports_plan) {
                    //支持跑步计划
                    //查询设备的跑步计划是否与当前用户计划
                    mPresenter.sendOperationPlanToDevice(mRunPlanToggleH5Info);
                }
            }
        });
        wv_h5.registerHandler(H5ToAppConstant.CONNECT_BLUETOOTH_DEVICE_TO_APP, new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                Gson gson = new GsonBuilder().create();
                ConnectBluetoothDeviceH5Bean mConnectBluetoothDevice =
                        gson.fromJson(data.toString(), ConnectBluetoothDeviceH5Bean.class);
//                var deviceList = SPHelper.getDeviceList();
//                for (device in deviceList) {
//                    if (mConnectBluetoothDevice.bluetoothDevice != null && !TextUtils.isEmpty(
//                            mConnectBluetoothDevice.bluetoothDevice.mac
//                    ) && mConnectBluetoothDevice.bluetoothDevice.mac == device.getMac()
//                    ) {
//                        mPresenter?.connectDevice(device)
//                    }
//                }
            }
        });
    }

    private void H5SendAppDataListener(){
        wv_h5.registerHandler(H5ToAppConstant.GET_USER_INFO_APP, new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                LogTool.e(
                        TAG, "getUserInfo"+ data.toString()
                );
                mPresenter.sendConnectDeviceAllStatusToWeb();
            }
        });
        wv_h5.registerHandler(
                H5ToAppConstant.SEND_NOTIFICATION_TO_APP, new BridgeHandler() {
                    @Override
                    public void handler(String data, CallBackFunction function) {
                        LogTool.e(
                                TAG,
                                data.toString()
                        );
                        //  showToast(data.toString())
                        Gson gson =new Gson();
                        HeathManagerH5Info homeNavigatorBean =
                                gson.fromJson(data.toString(), HeathManagerH5Info.class);
                        //message 传'stopSleepPlan'表示用户点击终止计划，通知app停止计划；
                        //        传'updateSleepTime'表示用户更改了睡眠计划的起床入睡时间
                        //        传'backToApp'表示通知app关闭当前h5；
                        //        传'backToLogin'表示token失效，通知app关闭当前h5并返回登录页重新登录；
                        //        传''表示不做任何操作
                        switch (homeNavigatorBean.message) {
                            case  Constants.STOP_SLEEP_PLAN : {
                                //终止计划   requestPullData()
//                                EventBusHelper.post(EventConstants.USER_SET_SLEEP_MANAGER);
                                break;
                            }

                            case Constants.BACK_TO_APP: {
                                finish();
                                break;
                            }
                            case Constants.BACK_TO_LOGIN : {
                                //Token时效
                                break;
//                                gotoPreLoginPage(Constants.LOGIN_GET_USERINFO_FAIL_THREE)
                            }
                            case Constants.OPEN_SYNC_HEALTHINFO:
                                JsonObject json = new JsonObject();
                                json.addProperty("data", "updateSleepData");
                                backH5(Constants.SEND_NOTIFICATION_TOWEB, json.toString());
                                break;

                            case "runningPlanAllDeviceStatus" :
                                //表示告知app端需要返回设备和蓝牙状态给H5   需要查询设备跑步计划
                                //当做开始训练调试用
                                if (mPresenter != null && BLEManager.isConnected() && mPresenter.getSupportFunctionInfo().v3_support_sports_plan) {
                                    SportPlan getSportPlanBean = new SportPlan();
                                    getSportPlanBean.operate = 4;
                                    BLEManager.getSportPlan(getSportPlanBean);
                                }
                                if (mPresenter != null && !BLEManager.isConnected()) {
                                    mPresenter.sendConnectDeviceAllStatusToWeb();
                                }
                                break;

                        }
                    }
                }
        );
    }

    @Override
    public void runPlanAppSendH5(JsonObject json, String name) {
        wv_h5.callHandler(name, json.toString(), new CallBackFunction() {
            @Override
            public void onCallBack(String data) {
                LogTool.e(TAG, data.toString());
            }
        });
    }

    /**
     * 统一回复H5yes
     */
    private void backH5(String name , String data ) {
        wv_h5.callHandler(
                name,
                data, new CallBackFunction() {
                    @Override
                    public void onCallBack(String data) {

                    }
                }) ;
    }

    @Override
    public void AppSendH5Medal(JsonObject json, String name) {

    }

    @Override
    public void showSportStartSuccess() {
        mPresenter.sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_START_TRAINING);
    }

    @Override
    public void showSportStartFailedLowPower() {

    }

    @Override
    public void showSportStartFail() {

    }

    @Override
    public void showSportStartFailedInCalling() {

    }

    @Override
    public void showSportStartFailedChargePower() {

    }

    @Override
    public void showSportStartError(String msg) {

    }

    @Override
    public void showMessage(String message) {

    }

    @Override
    public void showLoading(String message) {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showDisconnectDialog() {

    }

    @Override
    public void setSportStatus(Boolean isRunning) {
        if (isRunning) {
            mPresenter.sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_RENEW_TRAINING);
        } else {
            mPresenter.sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_SUSPEND_TRAINING);
        }
    }

    class CustomWebChromClient extends WebChromeClient{
        ValueCallback<Uri[]> uploadMessages  = null;
        private int FILECHOOSER_RESULTCODE = 0x01;
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            uploadMessages = filePathCallback;
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType(generateType(fileChooserParams.getAcceptTypes()));
            startActivityForResult(
                    Intent.createChooser(intent, "File Chooser"), FILECHOOSER_RESULTCODE
            );
            return true;
        }
        private String generateType(String[] types) {
            StringBuilder stringBuilder = new StringBuilder();
            if (types != null && types.length > 0) {
                for (int i = 0; i < types.length; i++) {
                    if (i > 0) {
                        stringBuilder.append(",");
                    }
                    stringBuilder.append(types[i]);
                }
            }
            String type = stringBuilder.toString();
            if (TextUtils.isEmpty(type)) {
               return  "*/*"; //所有类型的文件
            } else {
                return type;
            }
        }
        public void onActivityResult(int requestCode, int resultCode, Intent intent) {
            if (requestCode == FILECHOOSER_RESULTCODE) {
                Uri result = null;
                if (intent == null || resultCode != Activity.RESULT_OK) {
                    result = null;
                } else {
                    result = intent.getData();
                }
                if (uploadMessages != null) {
                    if (result == null) {
                        uploadMessages.onReceiveValue(null);
                    } else {
                        uploadMessages.onReceiveValue(new Uri[]{result});
                    }
                    uploadMessages = null;
                }
            }
        }
    }

    public static void startActivity(Activity activity){
        String COMMON_RUN_PLAN_MANAGER_URL_NEW = "https://%d-healthmanage.idoocloud.com/running";
        Intent intent = new Intent(activity, HealthManagerActivity.class);
        intent.putExtra(Constants.INTENT_URL,COMMON_RUN_PLAN_MANAGER_URL_NEW.replace("%d","en"));
        intent.putExtra(Constants.INTENT_FROM_RUN_PLAN,Constants.INTENT_FROM_RUN_PLAN);
        activity.startActivity(intent);
    }
}
