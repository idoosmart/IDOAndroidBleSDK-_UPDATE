package test.com.ido.file.transfer;

import static android.content.pm.ApplicationInfo.FLAG_SYSTEM;
import static android.content.pm.ApplicationInfo.FLAG_UPDATED_SYSTEM_APP;


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
import com.ido.ble.callback.DeviceParaChangedCallBack;
import com.ido.ble.callback.OperateCallBack;
import com.ido.ble.icon.transfer.IIconTransferListener;
import com.ido.ble.icon.transfer.IconTranConfig;
import com.ido.ble.protocol.model.CanDownLangInfoV3;
import com.ido.ble.protocol.model.MessageNotifyState;
import com.ido.ble.protocol.model.MessageNotifyStateCmdParaWrapper;
import com.ido.ble.protocol.model.NotificationPara;
import com.ido.ble.protocol.model.NotifyType;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import test.com.ido.CallBack.BaseGetDeviceInfoCallBack;
import test.com.ido.R;
import test.com.ido.utils.BitmapUtil;
import test.com.ido.utils.ExecutorDispatcher;
import test.com.ido.connect.BaseAutoConnectActivity;

public class NotificationIconTransferActivity extends BaseAutoConnectActivity {
    private static String TAG = "NotificationIconTransfer";
    EditText etMsg;
    FrameLayout dialog;
    FrameLayout loading;
    ListView listview;
    MyAdapter adapter;

    /**
     * 生成app唯一整型类型值
     *
     * @param pkg
     * @return
     */
    private HashMap<String, Integer> mAlphabet = new HashMap<>();
    private int lastValue = 0;
    private String lastPkg = "";


    /**
     * 已安装应用
     */
    ConcurrentHashMap<String, TranIconBean> allNoticeAppBeans = new ConcurrentHashMap<>();

    ConcurrentHashMap<Integer, String> allNoticeAppTypeBeans = new ConcurrentHashMap<>();

    List<TranIconBean> apps = new ArrayList<>();

    List<MessageNotifyState> stateList;
    TranIconBean mClickedApp;

    //设备支持的语言
    private List<Integer> languages = new ArrayList<>();

    private void loadInstalledApp() {
        ExecutorDispatcher.getInstance().dispatch(() -> {
            initAlphabet();
            allNoticeAppTypeBeans.clear();
            allNoticeAppBeans.clear();
            PackageManager pm = NotificationIconTransferActivity.this.getPackageManager();
            List<ApplicationInfo> listAppcations = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
            Collections.sort(listAppcations, new ApplicationInfo.DisplayNameComparator(pm)); // 字典排序
            TranIconBean bean;
            for (ApplicationInfo app : listAppcations) {
                if ((app.flags & FLAG_SYSTEM) <= 0 || (app.flags & FLAG_UPDATED_SYSTEM_APP) != 0) {
                    //非系统程序
                    //本来是系统程序，被用户手动更新后，该系统程序也成为第三方应用程序了
                    bean = getAppInfo(app, pm);
                    bean.type = convertPkg2Type(bean.pkgName);
                    allNoticeAppBeans.put(bean.pkgName, bean);
                    apps.add(bean);
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
                                throw new IllegalArgumentException("app is null");
                            }
                            Bitmap mBitmap = BitmapUtil.transform2CycleBitmap(BitmapUtil.drawableToBitmap(app.icon), 0);
                            String mIconPath = getFilesDir().getPath() + "/notification_icons" + "/" + iconTranConfig.index + "_" + app.pkgName;
                            try {
                                File iconFile = new File(mIconPath);
                                if (!iconFile.exists()) {
                                    iconFile.getParentFile().mkdirs();
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "图标：type = $type, 目录创建失败");
                            }
                            BitmapUtil.saveBitmap(BitmapUtil.zoomImg(mBitmap, width, height), mIconPath);
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
        for (int index = 0; index < pkg.length(); index++) {
            String it = String.valueOf(pkg.charAt(index));
            Integer v = mAlphabet.get(it);
            value += (v != null ? v : 0) * index;
        }
        lastValue = value;
        lastPkg = pkg;
        allNoticeAppTypeBeans.put(value, pkg);
        return value;
    }

    private void initAlphabet() {
        mAlphabet.clear();
        for (int index = 97; index <= 122; index++) {
            mAlphabet.put(String.valueOf((char) (index + '0')), index);
        }
        int size = mAlphabet.size();
        for (int index = 65; index <= 90; index++) {
            mAlphabet.put(String.valueOf((char) (index + '0')), index + size);
        }
        mAlphabet.put(".", mAlphabet.size());
        mAlphabet.put("_", mAlphabet.size());
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
        sendNotification2Device(content);
    }

    public void sendNotification2Device(String body) {
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