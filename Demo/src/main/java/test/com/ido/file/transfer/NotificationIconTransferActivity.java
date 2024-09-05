package test.com.ido.file.transfer;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.ido.ble.BLEManager;
import com.ido.ble.LocalDataManager;
import com.ido.ble.callback.DeviceParaChangedCallBack;
import com.ido.ble.callback.OperateCallBack;
import com.ido.ble.callback.SettingCallBack;
import com.ido.ble.icon.transfer.IIconTransferListener;
import com.ido.ble.icon.transfer.IconTranConfig;
import com.ido.ble.protocol.model.CanDownLangInfoV3;
import com.ido.ble.protocol.model.DeviceChangedPara;
import com.ido.ble.protocol.model.MessageNotifyState;
import com.ido.ble.protocol.model.MessageNotifyStateCmdParaWrapper;
import com.ido.ble.protocol.model.NotificationPara;
import com.ido.ble.protocol.model.NotifyType;
import com.ido.ble.protocol.model.QuickReplyInfo;
import com.ido.ble.protocol.model.SupportFunctionInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import test.com.ido.CallBack.BaseGetDeviceInfoCallBack;
import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;
import test.com.ido.utils.BitmapUtil;
import test.com.ido.utils.ExecutorDispatcher;
import test.com.ido.utils.ResourceUtil;

public class NotificationIconTransferActivity extends BaseAutoConnectActivity {
    private static String TAG = "NotificationIconTransfer";
    EditText etMsg;
    FrameLayout dialog;
    FrameLayout loading;
    ListView listview;
    MyAdapter adapter;

    /**
     * 已安装应用
     */
    ConcurrentHashMap<String, TranIconBean> allNoticeAppBeans = new ConcurrentHashMap<>();

    ConcurrentHashMap<Integer, String> allNoticeAppTypeBeans = new ConcurrentHashMap<>();

    private static Map<Integer, Notification> sNotificationMap;

    List<TranIconBean> apps = new ArrayList<>();

    List<MessageNotifyState> stateList;
    TranIconBean mClickedApp;

    private int msgId = 0;
    //正在回复消息
    private static boolean isReplying = false;
    //正在回复来电
    private static boolean isInComingCallReplying = false;
    private static DeviceChangedPara mDeviceChangedPara;
    private static final int WHAT_SMS_REPLY = 1;
    private static final int WHAT_INCOMING_CALL_SMS_REPLY = 2;

    //设备支持的语言
    private List<Integer> languages = new ArrayList<>();

    private void loadInstalledApp() {
        ExecutorDispatcher.getInstance().dispatch(() -> {
            allNoticeAppTypeBeans.clear();
            allNoticeAppBeans.clear();
            PackageManager pm = NotificationIconTransferActivity.this.getPackageManager();
            List<ApplicationInfo> listAppcations = pm.getInstalledApplications(0);
            Collections.sort(listAppcations, new ApplicationInfo.DisplayNameComparator(pm)); // 字典排序
            TranIconBean bean;

            Log.d(TAG, "loadInstalledApp1: "+listAppcations.size());

            for (ApplicationInfo app : listAppcations) {
                if ((app.flags & ApplicationInfo.FLAG_SYSTEM) == 0
                        && (app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 0) {
                    //非系统程序
                    //本来是系统程序，被用户手动更新后，该系统程序也成为第三方应用程序了
                    bean = getAppInfo(app, pm);
                    bean.type = convertPkg2Type(bean.pkgName);
                    allNoticeAppBeans.put(bean.pkgName, bean);
                    apps.add(bean);

                    Log.d(TAG, "loadInstalledApp: "+apps.size());
                }
            }

            ExecutorDispatcher.getInstance().dispatchOnMainThread(new Runnable() {
                @Override
                public void run() {
                    if (!apps.isEmpty() && (stateList != null && !stateList.isEmpty())) {
                        //刷新ui状态
                        for (MessageNotifyState state : stateList) {
                            for (int index = 0; index < apps.size(); index++) {
                                if (apps.get(index).type == state.evt_type) {
                                    apps.get(index).status = state.notify_state;
                                }
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                    loading.setVisibility(View.GONE);
                    BLEManager.registerOperateCallBack(mOperateCallback);
                    BLEManager.registerDeviceParaChangedCallBack(mNotificationIconNotifyCallback);
                    BLEManager.queryMessageNotifyState();
                }
            });
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_icon_transfer);
        dialog = findViewById(R.id.dialog);
        etMsg = findViewById(R.id.etMsg);
        loading = findViewById(R.id.loading);
        listview = findViewById(R.id.listview);
        adapter = new MyAdapter();
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mClickedApp = apps.get(position);
                dialog.setVisibility(View.VISIBLE);
            }
        });
        loading.setOnTouchListener((v, event) -> true);
        dialog.setOnTouchListener((v, event) -> true);
        loadInstalledApp();
        //查询设备语言列表
        BLEManager.registerGetDeviceInfoCallBack(deviceInfoCallback);
        BLEManager.getCanDownloadLangInfoV3();
        sendQuickMsgReplyInfo2Device();
        sendQuickIncomingReplyInfo2Device();
        if (isSupportMsgReply() || isSupportInComingCallQuickReply()) {
            BLEManager.unregisterDeviceParaChangedCallBack(mMsgReplyICallBack);
            BLEManager.registerDeviceParaChangedCallBack(mMsgReplyICallBack);
        }
    }

    /**
     * 消息回复的回调
     */
    private final DeviceParaChangedCallBack.ICallBack mMsgReplyICallBack = deviceChangedPara -> {
        try {
            if (deviceChangedPara != null && (deviceChangedPara.msg_ID > 0 || deviceChangedPara.msg_type == 0x01) && deviceChangedPara.msg_notice > 0) {
                Log.d(TAG, "【" + Thread.currentThread().getName() + "】 mMsgReplyICallBack=" + deviceChangedPara);
                if (deviceChangedPara.msg_type != 0x01) {
                    if (!isReplying) {
                        isReplying = true;
                        mDeviceChangedPara = deviceChangedPara;
                        replyMsg(mDeviceChangedPara);
                        //通知固件
                    } else {
                        Log.d(TAG, "正在回复消息，不处理");
                    }
                } else {
                    Log.d(TAG, "来电快捷回复！");
                    if (!isInComingCallReplying) {
                        isInComingCallReplying = true;
                        mDeviceChangedPara = deviceChangedPara;
                        replyIncomingCallMsg(mDeviceChangedPara);
                        //通知固件
                    } else {
                        Log.d(TAG, "正在回复消息，不处理");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendReplyResult2Device(false);
            Log.d(TAG, "快捷回复处理异常：" + e);
        }
    };

    private void replyIncomingCallMsg(DeviceChangedPara mDeviceChangedPara) {
        List<String> msgList = Arrays.asList(ResourceUtil.getStringArray(R.array.quick_reply_default_msg));
        String replyContent = msgList.get(mDeviceChangedPara.msg_notice - 1);
        if (TextUtils.isEmpty(replyContent)) {
            sendInComingCallReplyResult2Device(false);
            return;
        }
        //TODO  SmsManager.getDefault().sendTextMessage(phoneNumber, null, replyContent, sentIntent, null);
        //TODO boolean result = send result;
        boolean result = true;
        sendInComingCallReplyResult2Device(result);
    }

    private void replyMsg(DeviceChangedPara mDeviceChangedPara) {
        List<String> msgList = Arrays.asList(ResourceUtil.getStringArray(R.array.quick_reply_default_msg));
        String replyContent = msgList.get(mDeviceChangedPara.msg_notice - 1);
        if (TextUtils.isEmpty(replyContent)) {
            sendReplyResult2Device(false);
            return;
        }

        //TODO 1.使用Notification回复消息
        //获取保存的消息
        Notification notification = sNotificationMap.get(mDeviceChangedPara.msg_ID);
        if (notification == null) {
            sendReplyResult2Device(false);
            return;
        }
        boolean result = reply(notification, replyContent);
//        sendReplyResult2Device(result);
        sendReplyResult2Device(true/*模拟成功*/);
        //TODO 2.使用系统SMS回复消息
        //TODO  SmsManager.getDefault().sendTextMessage(phoneNumber, null, replyContent, sentIntent, null);
    }


    private boolean reply(Notification notification, String autoReplyContents) {
        try {
            PendingIntent pendingReply = notification.contentIntent;
            Notification.Action action = getReplyAction(notification);
            if (action != null) {
                android.app.RemoteInput remoteInput = action.getRemoteInputs()[0];
                String key = remoteInput.getResultKey();
                if (pendingReply != null) {
                    saveLog("带快捷回复的通知===action.getRemoteInputs() length=" + action.getRemoteInputs().length + "   ResultKey=" + remoteInput.getResultKey());
                    Intent localIntent = new Intent();
                    Bundle resultBundle = new Bundle();
                    resultBundle.putString(key, autoReplyContents);
                    RemoteInput.addResultsToIntent(new RemoteInput[]{new RemoteInput.Builder(key).build()}, localIntent, resultBundle);
                    try {
//                pendingReply.getIntentSender().sendIntent(this, 0, localIntent, null, null);
                        action.actionIntent.send(NotificationIconTransferActivity.this, 1001, localIntent);
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        saveLog("reply send failed: " + e);
                    }
                }
            } else {
                saveLog("不带快捷回复的通知");
            }
        } catch (Exception e) {
            e.printStackTrace();
            saveLog("reply failed: " + e);
        }
        return false;
    }

    private void saveLog(String msg) {
        Log.d(TAG, msg);
    }

    /**
     * 通知快捷回复响应
     *
     * @param replySuccess
     */
    private static void sendReplyResult2Device(boolean replySuccess) {
        Log.d(TAG, "sendReplyResult2Device, mDeviceChangedPara = " + mDeviceChangedPara + ", replySuccess = " + replySuccess);
        try {
            if (mDeviceChangedPara != null) {
                mDeviceChangedPara.is_success = replySuccess ? 1 : 0;
                BLEManager.setNoticeReply(mDeviceChangedPara);
            }
            mSmsReplyTimeoutTimer.removeMessages(WHAT_SMS_REPLY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        isReplying = false;
    }

    /**
     * 来电快捷回复响应
     *
     * @param replySuccess
     */
    private static void sendInComingCallReplyResult2Device(boolean replySuccess) {
        Log.d(TAG, "sendInComingCallReplyResult2Device, mDeviceChangedPara = " + mDeviceChangedPara + ", replySuccess = " + replySuccess);
        try {
            if (mDeviceChangedPara != null) {
                mDeviceChangedPara.is_success = replySuccess ? 1 : 0;
                BLEManager.setNoticeReply(mDeviceChangedPara);
            }
            mSmsReplyTimeoutTimer.removeMessages(WHAT_INCOMING_CALL_SMS_REPLY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        isInComingCallReplying = false;
    }

    /**
     * 短信回复超时计时器
     */
    private static final Handler mSmsReplyTimeoutTimer = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT_SMS_REPLY:
                    Log.d(TAG, "mSmsReplyTimeoutTimer, 发送短信快捷回复超时，重置标志位");
                    sendReplyResult2Device(false);
                    break;
                case WHAT_INCOMING_CALL_SMS_REPLY:
                    Log.d(TAG, "mSmsReplyTimeoutTimer, 发送来电快捷回复超时，重置标志位");
                    sendInComingCallReplyResult2Device(false);
                    break;
            }
        }
    };

    private SettingCallBack.ICallBack mSettingCallBack = new SettingCallBack.ICallBack() {

        @Override
        public void onSuccess(SettingCallBack.SettingType settingType, Object o) {
            if (settingType == SettingCallBack.SettingType.QUICK_REPLY_INFO) {

            }
        }

        @Override
        public void onFailed(SettingCallBack.SettingType settingType) {
            if (settingType == SettingCallBack.SettingType.QUICK_REPLY_INFO) {

            }
        }
    };
    private SettingCallBack.ICallBack mQuickIncomingCallSettingCallBack = new SettingCallBack.ICallBack() {

        @Override
        public void onSuccess(SettingCallBack.SettingType settingType, Object o) {
            if (settingType == SettingCallBack.SettingType.QUICK_REPLY_INFO) {

            }
        }

        @Override
        public void onFailed(SettingCallBack.SettingType settingType) {
            if (settingType == SettingCallBack.SettingType.QUICK_REPLY_INFO) {

            }
        }
    };

    /**
     * 设置默认消息快捷回复列表
     */
    private void sendQuickMsgReplyInfo2Device() {
        if (isSupportMsgReply()) {//支持快捷回复
            List<String> msgList = Arrays.asList(ResourceUtil.getStringArray(R.array.quick_reply_default_msg));
            QuickReplyInfo quickReplyInfo = new QuickReplyInfo();
            quickReplyInfo.num = msgList.size();
            quickReplyInfo.fast_items = new ArrayList<>();
            for (int i = 0; i < msgList.size(); i++) {
                QuickReplyInfo.QuickMsg msg = new QuickReplyInfo.QuickMsg();
                msg.msg_id = i + 1;
                msg.on_off = 1;
                msg.msg_data = msgList.get(i);
                quickReplyInfo.fast_items.add(msg);
            }
            BLEManager.unregisterSettingCallBack(mSettingCallBack);
            BLEManager.registerSettingCallBack(mSettingCallBack);
            BLEManager.setQuickReplyInfo(quickReplyInfo);
        }
    }

    /**
     * 设置默认来电快捷回复列表
     */
    private void sendQuickIncomingReplyInfo2Device() {
        if (isSupportInComingCallQuickReply()) {//支持快捷回复
            List<String> msgList = Arrays.asList(ResourceUtil.getStringArray(R.array.quick_incoming_call_reply_default_msg));
            QuickReplyInfo quickReplyInfo = new QuickReplyInfo();
            quickReplyInfo.num = msgList.size();
            quickReplyInfo.fast_items = new ArrayList<>();
            for (int i = 0; i < msgList.size(); i++) {
                QuickReplyInfo.QuickMsg msg = new QuickReplyInfo.QuickMsg();
                msg.msg_id = i + 1;
                msg.on_off = 1;
                msg.msg_data = msgList.get(i);
                quickReplyInfo.fast_items.add(msg);
            }
            BLEManager.unregisterSettingCallBack(mQuickIncomingCallSettingCallBack);
            BLEManager.registerSettingCallBack(mQuickIncomingCallSettingCallBack);
            //设置来电提醒快捷回复
            quickReplyInfo.version = 1;
            BLEManager.setQuickReplyInfo(quickReplyInfo);
        }
    }

    private boolean isSupportInComingCallQuickReply() {
        SupportFunctionInfo funcInfo = LocalDataManager.getSupportFunctionInfo();
        return funcInfo != null && funcInfo.support_calling_quick_reply;
    }

    private boolean isSupportMsgReply() {
        SupportFunctionInfo funcInfo = LocalDataManager.getSupportFunctionInfo();
        return funcInfo != null && funcInfo.v3_fast_msg_data;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BLEManager.unregisterGetDeviceInfoCallBack(deviceInfoCallback);
        BLEManager.unregisterOperateCallBack(mOperateCallback);
        BLEManager.unregisterDeviceParaChangedCallBack(mNotificationIconNotifyCallback);
    }

    private DeviceParaChangedCallBack.ICallBack mNotificationIconNotifyCallback = deviceChangedPara -> {
        if (deviceChangedPara.dataType == 13) {
            Log.e(TAG, "图标需要更新");
            BLEManager.queryMessageNotifyState();
        } else if (deviceChangedPara.dataType == 20) {
            Log.e(TAG, "设备端修改了状态");
        }
    };

    private BaseGetDeviceInfoCallBack deviceInfoCallback = new BaseGetDeviceInfoCallBack() {
        @Override
        public void onGetCanDownloadLangInfoV3(CanDownLangInfoV3 canDownLangInfoV3) {
            super.onGetCanDownloadLangInfoV3(canDownLangInfoV3);
            if (canDownLangInfoV3 != null) {
                for (CanDownLangInfoV3.Item item : canDownLangInfoV3.items_user) {
                    languages.add(item.language_type);
                }
            }
        }
    };

    private OperateCallBack.ICallBack mOperateCallback = new OperateCallBack.ICallBack() {
        @Override
        public void onSetResult(OperateCallBack.OperateType operateType, boolean b) {
            if (operateType == OperateCallBack.OperateType.MESSAGE_NOTIFY_STATE) {
                Log.d(TAG, "onSetResult: " + b);
                BLEManager.queryMessageNotifyState();
            }
        }

        @Override
        public void onAddResult(OperateCallBack.OperateType operateType, boolean b) {
            if (operateType == OperateCallBack.OperateType.MESSAGE_NOTIFY_STATE) {
                Log.d(TAG, "onAddResult: " + b);
                loading.setVisibility(View.GONE);
                BLEManager.queryMessageNotifyState();
            }
        }

        @Override
        public void onDeleteResult(OperateCallBack.OperateType operateType, boolean b) {

        }

        @Override
        public void onModifyResult(OperateCallBack.OperateType operateType, boolean b) {
            if (operateType == OperateCallBack.OperateType.MESSAGE_NOTIFY_STATE) {
                Log.d(TAG, "onModifyResult: " + b);
                loading.setVisibility(View.GONE);
                BLEManager.queryMessageNotifyState();
            }
        }

        @Override
        public void onQueryResult(OperateCallBack.OperateType operateType, Object o) {
            if (operateType == OperateCallBack.OperateType.MESSAGE_NOTIFY_STATE && o instanceof MessageNotifyStateCmdParaWrapper.Response) {
                if (((MessageNotifyStateCmdParaWrapper.Response) o).err_code == 0) {
                    List<MessageNotifyState> items = ((MessageNotifyStateCmdParaWrapper.Response) o).items;
                    processNotifyState(items);
                } else {
                    Log.d(TAG, "获取应用通知状态列表失败");
                }
            }
        }
    };

    private void processNotifyState(List<MessageNotifyState> items) {
        Log.e(TAG, "processNotifyState items = " + items);
        stateList = items;
        if (stateList == null) {
            //没有设置过列表，此处可以设置产品定义的默认清单
            return;
        }
        //刷新UI状态
        noIconStateList.clear();
        for (MessageNotifyState state : items) {
            if (!apps.isEmpty()) {
                for (int index = 0; index < apps.size(); index++) {
                    if (apps.get(index).type == state.evt_type) {
                        //notify_state 状态参考NotifyType类
                        apps.get(index).status = state.notify_state;
                    }
                }
            }
            if (state.pic_flag == 2) {
                noIconStateList.add(state);
            }
        }
        ExecutorDispatcher.getInstance().dispatchOnMainThread(() -> adapter.notifyDataSetChanged());

        //trans icon
        if (noIconStateList.isEmpty()) {
            //no icon need to transfer
            Log.e(TAG, "no icon need to transfer");
            return;
        } else {
            transferIcons();
        }
    }


    IconTranConfig config;
    List<MessageNotifyState> noIconStateList = new ArrayList<>();
    private int index = 0;
    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                //开始下一个
                if (index < noIconStateList.size()) {
                    Log.e(TAG, "传输第" + (index + 1) + "个图标，总共：" + noIconStateList.size());
                    config = new IconTranConfig();
                    config.type = IconTranConfig.TYPE_MSG;
                    config.index = noIconStateList.get(index).evt_type;
                    config.maxRetryTimes = 3;
                    BLEManager.startTranIcon(config, new IIconTransferListener() {
                        @Override
                        public void onStart(IconTranConfig iconTranConfig) {
                            Log.e(TAG, "传输开始：" + iconTranConfig);
                        }

                        @Override
                        public void onProgress(IconTranConfig iconTranConfig, int i) {
                            Log.e(TAG, "传输中：" + iconTranConfig + ", 进度：" + i);
                        }

                        @Override
                        public void onSuccess(IconTranConfig iconTranConfig) {
                            Log.e(TAG, "传输完成：" + iconTranConfig);
                            config = null;
                            index++;
                            handler.sendEmptyMessage(1);
                        }

                        @Override
                        public void onFailed(IconTranConfig iconTranConfig) {
                            Log.e(TAG, "传输失败：" + iconTranConfig);
                            config = null;
                            index++;
                            handler.sendEmptyMessage(1);
                        }

                        @Override
                        public void onBusy(IconTranConfig iconTranConfig) {
                            Log.e(TAG, "传输忙：" + iconTranConfig);
                        }

                        @Override
                        public String onHandlePicFile(IconTranConfig iconTranConfig, int width, int height) {
                            Log.e(TAG, "处理图片：" + iconTranConfig + ", width x height: " + width + " x " + height);
                            TranIconBean app = findApp(iconTranConfig.index);
                            if (app == null) {
                                Log.e(TAG,"未找到App："+iconTranConfig.index);
                                return "";
                            }
                            Bitmap mBitmap = BitmapUtil.transform2CycleBitmap(BitmapUtil.drawableToBitmap(app.icon), 12);
                            String mIconPath = getFilesDir().getPath() + "/notification_icons" + "/" + iconTranConfig.index + "_" + app.pkgName;
                            try {
                                File iconFile = new File(mIconPath);
                                if (!iconFile.exists()) {
                                    iconFile.getParentFile().mkdirs();
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "图标：type = $type, 目录创建失败");
                            }
                            BitmapUtil.saveBitmap(BitmapUtil.zoomImgWithBorder(mBitmap, width, height), mIconPath);
                            return mIconPath;
                        }
                    });
                } else {
                    Log.e(TAG, "全部完成");
                }
            }
        }
    };

    private void transferIcons() {
        index = 0;
        BLEManager.stopTranIcon();
        handler.removeMessages(1);
        handler.sendEmptyMessage(1);
    }

    /**
     * 注意：这里是测试的简易版类型生成，可自己计算生成
     *
     * @param pkg
     * @return
     */
    private int convertPkg2Type(String pkg) {
        int value = 0;
        value = convertPackageToNumber(pkg);
        Log.d(TAG, "convertPkg2Type, pkg = " + pkg + ", type = " + value);
        if (value >= 20000) {
            Log.d(TAG, "convertPkg2Type, pkg = " + pkg + ", type = " + value + ", 超限了");
        }
        if (allNoticeAppTypeBeans.containsKey(value)) {
            Log.d(TAG, "convertPkg2Type, pkg = " + pkg + ", type = " + value + ", 重复了，" + allNoticeAppTypeBeans.get(value));
        }
        allNoticeAppTypeBeans.put(value, pkg);
        return value;
    }

    private int convertPackageToNumber(String packageName) {
        int hash = packageName.hashCode();
        return Math.abs(hash % 20001); // 取绝对值并限制在 0 到 20000 之间
    }

    /**
     * 构造一个AppInfo对象 ，并赋值
     */
    private TranIconBean getAppInfo(ApplicationInfo app, PackageManager pm) {
        TranIconBean appInfo = new TranIconBean();
        try {
            appInfo.appName = pm.getApplicationLabel(app).toString(); //应用名称
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            appInfo.icon = app.loadIcon(pm); //应用icon
        } catch (Exception e) {
            e.printStackTrace();
        }
        appInfo.pkgName = app.packageName; //应用包名，用来卸载
        PackageInfo packageInfo = null;
        try {
            packageInfo = pm.getPackageInfo(app.packageName, 0);
            long lastUpdateTime = packageInfo.lastUpdateTime; //应用最近一次更新时间
            appInfo.appUpdateTime = lastUpdateTime;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appInfo;
    }//获取短信包名

    private boolean findAppState(int type) {
        if (stateList != null && !stateList.isEmpty()) {
            for (MessageNotifyState state : stateList) {
                if (state.evt_type == type) {
                    return true;
                }
            }
        }
        return false;
    }

    private TranIconBean findApp(int type) {
        for (TranIconBean app : apps) {
            if (app.type == type) {
                return app;
            }
        }
        return null;
    }

    private void setNotificationState(int state) {
        MessageNotifyState messageNotifyState = new MessageNotifyState();
        messageNotifyState.evt_type = mClickedApp.type;
        messageNotifyState.notify_state = state;
        List<MessageNotifyState> list = new ArrayList<>();
        list.add(messageNotifyState);
        //如果查询列表中存在则调用修改
        if (findAppState(mClickedApp.type)) {
            BLEManager.modifyMessageNotifyState(list, 0, 0);
        } else {
            BLEManager.addMessageNotifyState(list, 0, 0);
        }
        loading.setVisibility(View.VISIBLE);
    }

    public void btPermitNotification(View view) {
        dialog.setVisibility(View.GONE);
        setNotificationState(NotifyType.ALLOW);
    }

    public void btSilenceNotification(View view) {
        dialog.setVisibility(View.GONE);
        setNotificationState(NotifyType.SLIENT);
    }

    public void btCloseNotification(View view) {
        dialog.setVisibility(View.GONE);
        setNotificationState(NotifyType.CLOSE);
    }

    public void btClose(View view) {
        dialog.setVisibility(View.GONE);
    }

    public void btSendMsg(View view) {
        String content = etMsg.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "请输入通知内容！", Toast.LENGTH_SHORT).show();
            return;
        }
        sendNotification2Device(content, false);
    }

    public void btSendMsgAndReply(View view) {
        String content = etMsg.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "请输入通知内容！", Toast.LENGTH_SHORT).show();
            return;
        }

        //模拟收到通知的场景，实际应该是通过NotificationListenerService.
//        class MsgService extends NotificationListenerService {
//            @Override
//            public void onNotificationPosted(StatusBarNotification sbn) {
//                super.onNotificationPosted(sbn);
//                Notification notification = sbn.getNotification();
//            }
//        }
        //此处只是模拟
        saveNotification(new Notification());
        sendNotification2Device(content, true);
    }

    private void saveNotification(Notification notification) {
        if (sNotificationMap == null) {
            sNotificationMap = new HashMap<>();
        }
        msgId++;
        sNotificationMap.put(msgId, notification);
    }

    private static Notification.Action getReplyAction(Notification notification) {
        Notification.Action[] actions = notification.actions;
        if (actions != null) {
            for (Notification.Action action : actions) {
                if (action != null) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                        CommonLogUtil.d("快捷回复的通知 action.getSemanticAction()="+action.getSemanticAction());
//                        if(action.getSemanticAction() == Notification.Action.SEMANTIC_ACTION_REPLY){
//                            return action;
//                        }
//                    }else {
                    if (action.getRemoteInputs() != null && action.getRemoteInputs().length > 0) {
                        return action;
                    }
//                    }
                }

            }
        }
        return null;
    }

    /**
     * 是否支持快捷回复
     *
     * @return
     */
    public static boolean isSupportQuickReply(Notification notification) {
        return getReplyAction(notification) != null;
    }

    public void sendNotification2Device(String body, boolean supportReply) {
        if (!BLEManager.isConnected()) {
            Log.d(TAG, "sendNotification2DeviceV3，device not connected");
            return;
        }

        if (mClickedApp == null) {
            return;
        }

        String mDefaultAppName = mClickedApp.appName;
        NotificationPara v3MessageNotice = new NotificationPara();
        v3MessageNotice.notify_type = mClickedApp.type;

        v3MessageNotice.contact = mDefaultAppName;
        v3MessageNotice.msg_data = body;
        v3MessageNotice.evt_type = 1; //当前处于那种模式  0：无效； 1:消息提醒; 2：打电话；

        //支持快捷回复，必须传msgID，且msgID > 0，msgID可以关联发送的消息
        if (supportReply) {
            v3MessageNotice.msg_ID = msgId;
        } else {
            v3MessageNotice.msg_ID = 0;
        }

        int length = 1;//items的长度
        boolean hasMultiLanguage = false;
        if (!languages.isEmpty()) {
            length = languages.size();
            hasMultiLanguage = true;
        }
        v3MessageNotice.app_items_len = length;

        v3MessageNotice.items = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            NotificationPara.AppNames item_obj = new NotificationPara.AppNames();
            if (hasMultiLanguage) {
                item_obj.language = languages.get(i);
                item_obj.name = mDefaultAppName;//此处的mDefaultAppName只是测试使用，实际的app名称应该跟languages对应，比如微信，中文名：微信，英文名：wechat
            } else {//默认名称
                Log.d(TAG, " 没有取到名称列表，使用默认名称：" + mDefaultAppName);
                item_obj.language = 0x01 + i;//默认英文
                item_obj.name = mDefaultAppName;
            }
            v3MessageNotice.items.add(item_obj);
        }
        Log.d(TAG, "sendNotification2DeviceV3 v3MessageNotice = " + v3MessageNotice);
        BLEManager.sendNotification(v3MessageNotice);

    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return apps.size();
        }

        @Override
        public Object getItem(int position) {
            return apps.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(parent.getContext(), R.layout.item_notification_layout, null);
                holder = new ViewHolder();
                holder.ivIcon = convertView.findViewById(R.id.ivIcon);
                holder.tvName = convertView.findViewById(R.id.tvName);
                holder.tvStatus = convertView.findViewById(R.id.tvStatus);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            TranIconBean data = apps.get(position);
            holder.ivIcon.setImageDrawable(data.icon);
            holder.tvName.setText(data.appName);
            holder.tvStatus.setText(data.status == NotifyType.ALLOW ? R.string.notice_permit : data.status == NotifyType.SLIENT ? R.string.notice_silence : R.string.notice_close);
            return convertView;
        }
    }

    class ViewHolder {
        ImageView ivIcon;
        TextView tvName;
        TextView tvStatus;
    }


    static class TranIconBean {
        //通知类型
        public int type;
        //app包名
        public String pkgName;
        //app更新时间
        public long appUpdateTime;
        //app名字
        public String appName;
        public Drawable icon;
        public int status;
    }
}