package test.com.ido.file.transfer;

import static test.com.ido.utils.BmpUtil.saveBmp;
import static test.com.ido.utils.SportIconUtils.RESOURCE_NAME;
import static test.com.ido.utils.SportIconUtils.RESOURCE_VERSION;
import static test.com.ido.utils.SportIconUtils.getResourceDir;
import static test.com.ido.utils.SportIconUtils.isSupportSetTransferInfo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.ido.ble.BLEManager;
import com.ido.ble.LocalDataManager;
import com.ido.ble.callback.OperateCallBack;
import com.ido.ble.callback.SettingCallBack;
import com.ido.ble.icon.transfer.IIconTransferListener;
import com.ido.ble.icon.transfer.IconTranConfig;
import com.ido.ble.protocol.model.IconTransInformation;
import com.ido.ble.protocol.model.Sport100TypeItem;
import com.ido.ble.protocol.model.Sport100TypeSort;
import com.ido.ble.protocol.model.SupportFunctionInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import test.com.ido.CallBack.BaseOperateCallback;
import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;
import test.com.ido.utils.Constant;
import test.com.ido.utils.DataUtils;
import test.com.ido.utils.ExecutorDispatcher;
import test.com.ido.utils.FileUtil;
import test.com.ido.utils.OnItemClickListener;
import test.com.ido.utils.ResUtils;
import test.com.ido.utils.SportIconUtils;
import test.com.ido.utils.ZipUtils;

public class SportIconTransferActivity extends BaseAutoConnectActivity {
    static final String TAG = "SportIconTransferActivity";
    static final int WHAT_NOTIFY = 1;
    static final long NOTIFY_TIMEOUT = 30 * 1000L;

    static final String NORMAL = "normal";
    static final String ANIM = "anim";
    static final String PIC_SUFFIX = ".bmp";

    static final int MOTION_TYPE_48 = 48;
    static final int MOTION_TYPE_49 = 49;
    static final int MOTION_TYPE_52 = 52;
    static final int MOTION_TYPE_53 = 53;

    TextView tvAdded;
    TextView tvUnAdded;
    RecyclerView listview;
    RecyclerView listview_un_added;
    FrameLayout loading;
    FrameLayout fl_progress;
    ProgressBar progress_bar;

    MyAdapter adapter;
    MyAdapter un_added_adapter;

    Sport100TypeSort sport100TypeSort;
    List<Sport100TypeItem> list = new ArrayList<>();
    List<Sport100TypeItem> un_add_list = new ArrayList<>();
    List<IconTransTask> taskList = new ArrayList<>();
    private int mNotifyState = -1;

    interface Callback {
        void onResult(boolean success);
    }

    /**
     * 加载/assets/motion_types.zip并解压
     * TODO 是否增加重试机制
     */
    private void loadMotionTypesResource(Callback callback) {
        ExecutorDispatcher.getInstance().dispatch(() -> {
            File mResDir = getResourceDir(SportIconTransferActivity.this);
            boolean success = false;
            try {
                Log.d(TAG, "clean the old resource file: " + mResDir.getAbsolutePath());
                FileUtil.cleanDirectory(mResDir);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                boolean status = ZipUtils.unzip(SportIconTransferActivity.this.getAssets().open(RESOURCE_NAME), mResDir.getAbsolutePath());
                Log.d(TAG, "loadMotionTypesResource: " + status);
                if (status) {
                    DataUtils.getInstance().saveMotionTypeVersion(RESOURCE_VERSION);
                    Log.d(TAG, "save the motion resource version " + RESOURCE_VERSION);
                    success = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            boolean finalSuccess = success;
            ExecutorDispatcher.getInstance().dispatchOnMainThread(() -> {
                if (callback != null) {
                    callback.onResult(finalSuccess);
                }
            });
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport_icon_transfer);
        fl_progress = findViewById(R.id.fl_progress);
        progress_bar = findViewById(R.id.progress_bar);
        loading = findViewById(R.id.loading);
        tvAdded = findViewById(R.id.tvAdded);
        tvUnAdded = findViewById(R.id.tvUnAdded);
        listview = findViewById(R.id.listview);
        adapter = new MyAdapter(list, true, position -> {
            if (list.size() <= sport100TypeSort.min_show_num) {
                Toast.makeText(SportIconTransferActivity.this, "最少保留" + sport100TypeSort.min_show_num + "个运动", Toast.LENGTH_SHORT).show();
                return;
            }
            Sport100TypeItem item = list.remove(position);
            adapter.notifyItemRemoved(position);
            un_add_list.add(0, item);
            un_added_adapter.notifyDataSetChanged();
            tvAdded.setText("added(" + list.size() + "/" + sport100TypeSort.max_show_num + ")");
        });
        listview.setAdapter(adapter);
        listview.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        listview_un_added = findViewById(R.id.listview_unselected);
        un_added_adapter = new MyAdapter(un_add_list, false, position -> {
            if (list.size() >= sport100TypeSort.max_show_num) {
                Toast.makeText(SportIconTransferActivity.this, "最多添加" + sport100TypeSort.max_show_num + "个运动", Toast.LENGTH_SHORT).show();
                return;
            }
            Sport100TypeItem item = un_add_list.remove(position);
            un_added_adapter.notifyItemRemoved(position);
            list.add(item);
            adapter.notifyDataSetChanged();
            tvAdded.setText("added(" + list.size() + "/" + sport100TypeSort.max_show_num + ")");
        });
        listview_un_added.setAdapter(un_added_adapter);
        listview_un_added.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        loading.setVisibility(View.VISIBLE);
        loading.setOnTouchListener((v, event) -> true);
        BLEManager.registerOperateCallBack(callback);
        BLEManager.querySport100TypeSort();

    }

    public void btSet(View view) {
        List<Integer> newList = new ArrayList<>();
        for (Sport100TypeItem item : list) {
            newList.add(item.type);
        }
        for (Sport100TypeItem item : un_add_list) {
            newList.add(item.type);
        }
        loading.setVisibility(View.VISIBLE);
        BLEManager.setSport100TypeSort(newList, list.size());
    }

    private BaseOperateCallback callback = new BaseOperateCallback() {
        @Override
        public void onQueryResult(OperateCallBack.OperateType operateType, Object o) {
            super.onQueryResult(operateType, o);
            if (operateType == OperateCallBack.OperateType.SPORT_100_TYPE_SORT && o instanceof Sport100TypeSort) {
                list.clear();
                un_add_list.clear();
                sport100TypeSort = (Sport100TypeSort) o;
                List<Sport100TypeItem> items = sport100TypeSort.items;
                if (items != null && items.size() > 0) {
                    for (int i = 0; i < items.size(); i++) {
                        if (i < sport100TypeSort.now_user_location) {
                            list.add(items.get(i));
                        } else {
                            un_add_list.add(items.get(i));
                        }
                    }
                }
                tvAdded.setText("added(" + list.size() + "/" + sport100TypeSort.max_show_num + ")");
                adapter.notifyDataSetChanged();
                un_added_adapter.notifyDataSetChanged();
                loading.setVisibility(View.GONE);
            }
        }

        @Override
        public void onSetResult(OperateCallBack.OperateType operateType, boolean b) {
            super.onSetResult(operateType, b);
            if (operateType == OperateCallBack.OperateType.SPORT_100_TYPE_SORT) {
                Toast.makeText(SportIconTransferActivity.this, b ? "successful" : "failed", Toast.LENGTH_SHORT).show();
                loading.setVisibility(View.GONE);
                if (isSupportTransferSportIcon()) {
                    //需要设置图标
                    transferIcon();
                }
            }
        }
    };

    private boolean isSupportTransferSportIcon() {
        SupportFunctionInfo func = LocalDataManager.getSupportFunctionInfo();
        return func != null && func.V3_support_v3_notify_icon_adaptive;
    }

    private void transferIcon() {
        Log.d(TAG, "transferIcon start...");
        //判断是否有图标
        if (SportIconUtils.isMotionTypeIconsExist(this) && SportIconUtils.checkMotionResourceVersionChanged()) {
            Log.d(TAG, "motion resource exist and version has not changed, so it's necessary to unzip!!!");
            readyStartTransfer();
        } else {
            loadMotionTypesResource(new Callback() {
                @Override
                public void onResult(boolean success) {
                    readyStartTransfer();
                }
            });
        }
    }

    int index = 0;

    private void readyStartTransfer() {
        //每个运动类型的图标总量
        int eachMax = SportIconUtils.getMaxIconCountForEachSport();
        //所有运动类型的图标总量
        int totalTaskCount = list.size() * eachMax;

        for (Sport100TypeItem item : list) {
            int transferredCount = createTaskAndAdd(item.type, taskList, item.flag);
            totalTaskCount -= transferredCount;
        }
        Log.d(TAG, "总共需要传输的图标数量：" + totalTaskCount);
        progress_bar.setMax(taskList.size());
        if (isSupportSetTransferInfo()) {
            //通知设备开始，用于统一所有图标进度条，设备只显示一条进度条
            handler.post(() -> fl_progress.setVisibility(View.VISIBLE));
            progress_bar.setProgress(0);
            notifyDeviceTransferStart();
        } else {
            //设备上一条一条显示传输进度条
            startTransfer();
        }
    }

    private void startTransfer() {
        progress_bar.setProgress(0);
        index = 0;
        BLEManager.stopTranIcon();
        handler.removeMessages(1);
        handler.sendEmptyMessage(1);
        handler.post(() -> fl_progress.setVisibility(View.VISIBLE));
    }

    private final Handler mNotifyTimer = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "notifyDeviceTransferStart time out");
            notifyFailed();
        }
    };

    /**
     * 通知设备开始传输图标
     */
    private void notifyDeviceTransferStart() {
        IconTransInformation info = new IconTransInformation();
        info.icon_num = taskList.size();
        info.states = IconTransInformation.START_TRAN;
        BLEManager.unregisterSettingCallBack(mSettingCallback);
        BLEManager.registerSettingCallBack(mSettingCallback);
        Log.d(TAG, "notifyDeviceTransferStart, info = " + info);
        mNotifyState = IconTransInformation.START_TRAN;
        BLEManager.setTranSportIconInformation(info);
        mNotifyTimer.sendEmptyMessageDelayed(WHAT_NOTIFY, NOTIFY_TIMEOUT);
    }

    /**
     * 通知设备运动类型图标传输结束
     */
    private void notifyDeviceTransferTerminated() {
        IconTransInformation info = new IconTransInformation();
        info.states = IconTransInformation.END_TRAN;
        BLEManager.unregisterSettingCallBack(mSettingCallback);
        BLEManager.registerSettingCallBack(mSettingCallback);
        Log.d(TAG, "notifyDeviceTransferTerminated, info = " + info);
        mNotifyState = IconTransInformation.END_TRAN;
        BLEManager.setTranSportIconInformation(info);
    }

    private final SettingCallBack.ICallBack mSettingCallback = new SettingCallBack.ICallBack() {
        @Override
        public void onSuccess(SettingCallBack.SettingType p0, Object p1) {
            if (p0 == SettingCallBack.SettingType.ICON_INFORMATION_NOTICE) {
                if (mNotifyState == IconTransInformation.START_TRAN) {
                    mNotifyTimer.removeMessages(WHAT_NOTIFY);
                    Log.d(TAG, "notifyDeviceTransferStart success");
                    startTransfer();
                } else if (mNotifyState == IconTransInformation.END_TRAN) {
                    Log.d(TAG, "notifyDeviceTransferTerminated success");
                    handler.post(() -> fl_progress.setVisibility(View.GONE));
                }
            }
        }

        @Override
        public void onFailed(SettingCallBack.SettingType p0) {
            if (p0 == SettingCallBack.SettingType.ICON_INFORMATION_NOTICE) {
                if (mNotifyState == IconTransInformation.START_TRAN) {
                    Log.d(TAG, "notifyDeviceTransferStart failed");
                    mNotifyTimer.removeMessages(WHAT_NOTIFY);
                    notifyFailed();
                } else if (mNotifyState == IconTransInformation.END_TRAN) {
                    Log.d(TAG, "notifyDeviceTransferTerminated failed");
                    handler.post(() -> fl_progress.setVisibility(View.GONE));
                }
            }
        }

    };

    private void notifyFailed() {
        mNotifyTimer.post(new Runnable() {
            @Override
            public void run() {
                fl_progress.setVisibility(View.GONE);
                Toast.makeText(SportIconTransferActivity.this, "icon start transfer settings failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                if (index < taskList.size()) {
                    IconTranConfig config = new IconTranConfig();
                    IconTransTask task = taskList.get(index);
                    config.type = task.iconType;
                    config.index = task.sportType;
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
                            index++;
                            handler.sendEmptyMessage(1);
                            progress_bar.setProgress(index);
                        }

                        @Override
                        public void onFailed(IconTranConfig iconTranConfig) {
                            Log.e(TAG, "传输失败：" + iconTranConfig);
                            index++;
                            handler.sendEmptyMessage(1);
                        }

                        @Override
                        public void onBusy(IconTranConfig iconTranConfig) {
                            Log.e(TAG, "传输忙：" + iconTranConfig);
                        }

                        @Override
                        public String onHandlePicFile(IconTranConfig config, int width, int height) {
                            Log.e(TAG, "处理图片：" + config + ", width x height: " + width + " x " + height);
                            if (config != null) {
                                String dir = null;
                                switch (config.type) {
                                    case IconTranConfig.TYPE_SPORT_ANIMATION:
                                        dir = ANIM;
                                        break;
                                    case IconTranConfig.TYPE_SPORT_SMALL:
                                    case IconTranConfig.TYPE_SPORT_MIDDLE:
                                    case IconTranConfig.TYPE_SPORT_BIG:
                                        dir = NORMAL;
                                        break;
                                    default:
                                        dir = null;
                                        break;
                                }
                                if (dir != null) {
                                    String path = SportIconUtils.getResourceDir(SportIconTransferActivity.this).getAbsolutePath() + File.separator + dir + File.separator + Constant.PREFIX_MOTION_RESOURCE + config.index + PIC_SUFFIX;
                                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                                    if (bitmap == null) {
                                        return "";
                                    }

                                    int originalWidth = bitmap.getWidth();
                                    int originalHeight = bitmap.getHeight();
                                    if (originalWidth == 0) originalWidth = getDefaultWidthByType(config.index);
                                    if (originalHeight == 0) originalHeight = getDefaultHeightByType(config.index);
                                    String outFilePath = SportIconUtils.getSportIconTempDir(SportIconTransferActivity.this) + File.separator + width + "X" + height;
                                    File outFile = new File(outFilePath);
                                    if (!outFile.exists()) outFile.mkdirs();
                                    outFilePath += File.separator + Constant.PREFIX_MOTION_RESOURCE + config.index + PIC_SUFFIX;
                                    File file = new File(outFilePath);
                                    //图标存在，则复用，但是升级图标版本的时候需要删除缓存图标
                                    if (file.exists() && file.isFile()) {
                                        return outFilePath;
                                    }
                                    int outHeight = height;
                                    if (ANIM.equals(dir)) {
                                        float ratio = width / (originalWidth * 1.0f);
                                        outHeight = (int) (originalHeight * ratio);
                                    }
                                    bitmap = Bitmap.createScaledBitmap(bitmap, width, outHeight, true);
                                    saveBmp(bitmap, outFilePath);
                                    return outFilePath;
                                } else {
                                    return "";
                                }
                            } else {
                                return "";
                            }
                        }
                    });
                } else {
                    Log.d(TAG, "全部传输完成！");
                    if (isSupportSetTransferInfo()) {
                        notifyDeviceTransferTerminated();
                    } else {
                        handler.post(() -> fl_progress.setVisibility(View.GONE));
                    }
                }
            }
        }
    };

    private int getDefaultWidthByType(int sportType) {
        return 120;
    }

    private int getDefaultHeightByType(int sportType) {
        switch (sportType) {
            case MOTION_TYPE_52:
            case MOTION_TYPE_53:
                return 2880;
            default:
                return 2400;
        }
    }

    /**
     * @param sportType
     * @param taskList
     * @param iconFlag
     * @return 该运动类型已经传过的图标数量
     */
    private int createTaskAndAdd(int sportType, List<IconTransTask> taskList, int iconFlag) {
        Log.d(TAG, "运动：" + sportType + "图标任务创建: ");
        int transferredIconCount = 0;
        if (SportIconUtils.requireSmall(iconFlag)) {
            createAndAdd(sportType, taskList, IconTranConfig.TYPE_SPORT_SMALL);
            Log.d(TAG, "运动：" + sportType + " 的小图标还没有传输过！！！");
        } else {
            transferredIconCount++;
        }
        if (SportIconUtils.isSupportMiddleIcon()) {
            if (SportIconUtils.requireMiddle(iconFlag)) {
                createAndAdd(sportType, taskList, IconTranConfig.TYPE_SPORT_MIDDLE);
                Log.d(TAG, "运动：" + sportType + " 的中图标还没有传输过！！！");
            } else {
                transferredIconCount++;
            }
        }
        if (SportIconUtils.requireBig(iconFlag)) {
            createAndAdd(sportType, taskList, IconTranConfig.TYPE_SPORT_BIG);
            Log.d(TAG, "运动：" + sportType + " 的大图标还没有传输过！！！");
        } else {
            transferredIconCount++;
        }
        if (SportIconUtils.isSupportXIcon()) {
            if (SportIconUtils.requireX(iconFlag)) {
                createAndAdd(sportType, taskList, IconTranConfig.TYPE_SPORT_SMALL_SMALL);
                Log.d(TAG, "运动：" + sportType + " 的X图标还没有传输过！！！");
            } else {
                transferredIconCount++;
            }
        }

        if (transferredIconCount == SportIconUtils.getMaxIconCountForEachSport()) {
            Log.d(TAG, "运动：" + sportType + " 的图标已经传输过！！！");
        }
        return transferredIconCount;
    }

    /**
     *
     */
    private void createAndAdd(int sportType, List<IconTransTask> stateList, int iconType) {
//        if (iconType == IconTranConfig.TYPE_SPORT_SMALL) {
        //小图，如果存在动图则优先传动图
//            if (isSupportAnimIcon && mAnimationIconTypes ?.contains(type) == true){
//                moduleType = ModuleType.TYPE_SPORT_ANIMATION
//                CommonLogUtil.printAndSave("运动：${type} 存在动图，传输动图！")
//            }
//        }
        IconTransTask config = new IconTransTask(iconType, sportType);
        stateList.add(config);
    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.VH> {
        List<Sport100TypeItem> list;
        OnItemClickListener onClickListener;
        boolean added = false;

        public MyAdapter(List<Sport100TypeItem> list, boolean added, OnItemClickListener onClickListener) {
            this.list = list;
            this.added = added;
            this.onClickListener = onClickListener;
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sport_icon, null, false));
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            Sport100TypeItem sportType = list.get(position);
            holder.ivIcon.setImageResource(ResUtils.getMipmapResId("motion_" + sportType.type));
            holder.tvName.setText(ResUtils.getStringResId( "motion_" + sportType.type));
            holder.ivAdded.setImageResource(added ? R.drawable.delete : R.drawable.add);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class VH extends RecyclerView.ViewHolder {
            ImageView ivIcon;
            TextView tvName;
            ImageView ivAdded;

            public VH(@NonNull View itemView) {
                super(itemView);
                ivIcon = itemView.findViewById(R.id.ivIcon);
                tvName = itemView.findViewById(R.id.tvName);
                ivAdded = itemView.findViewById(R.id.cbBox);
                ivAdded.setOnClickListener(v -> {
                    if (onClickListener != null) {
                        onClickListener.onItemClick(getAdapterPosition());
                    }
                });
            }
        }
    }

    private class IconTransTask {
        /**
         * 图标类型，参考{@link
         * IconTranConfig#TYPE_SPORT_SMALL,IconTranConfig#TYPE_SPORT_MIDDLE,IconTranConfig#TYPE_SPORT_SMALL_SMALL,IconTranConfig#TYPE_SPORT_ANIMATION,IconTranConfig#TYPE_SPORT_BIG}
         */
        public int iconType;
        /**
         * 运动类型，参考{@link com.ido.ble.protocol.model.Sport100Type}
         */
        public int sportType;

        public IconTransTask(int iconType, int sportType) {
            this.iconType = iconType;
            this.sportType = sportType;
        }
    }

    private class SportType {
        public Sport100TypeItem item;
        public int checked;

        public SportType(Sport100TypeItem item, int checked) {
            this.item = item;
            this.checked = checked;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BLEManager.unregisterOperateCallBack(callback);
    }

}