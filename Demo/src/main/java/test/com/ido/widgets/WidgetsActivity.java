package test.com.ido.widgets;

import static com.ido.ble.protocol.model.SmallQuickModule.*;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.ido.ble.BLEManager;
import com.ido.ble.LocalDataManager;
import com.ido.ble.callback.OperateCallBack;
import com.ido.ble.protocol.model.SmallQuickModule;
import com.ido.ble.protocol.model.SupportFunctionInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import test.com.ido.CallBack.BaseOperateCallback;
import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;
import test.com.ido.utils.OnItemClickListener;

public class WidgetsActivity extends BaseAutoConnectActivity {
    private static final String TAG = "WidgetsActivity";
    private static final int BIG_SIZE = 1;
    private static final int SMALL_SIZE = 2;

    private static final int MENU_STEP = WIDGETS_TYPE_STEPS;
    private static final int MENU_HEART_RATE = WIDGETS_TYPE_HEART_RATE;
    private static final int MENU_SLEEP = WIDGETS_TYPE_SLEEP;
    private static final int MENU_ALARM = WIDGETS_TYPE_ALARM;
    private static final int MENU_MUSIC = WIDGETS_TYPE_MUSIC;
    private static final int MENU_TIMER = WIDGETS_TYPE_TIMER;
    private static final int MENU_WEATHER = WIDGETS_TYPE_WEATHER;
    private static final int MENU_PRESSURE = WIDGETS_TYPE_PRESSURE;
    private static final int MENU_DATA_THREE_CIRCLE = WIDGETS_TYPE_THREE_CIRCLE;
    private static final int MENU_TIME = WIDGETS_TYPE_WORLD_TIME;
    private static final int MENU_BLOOD_OXYGEN = WIDGETS_TYPE_SPO2;
    private static final int MENU_NOISE = WIDGETS_TYPE_NOISE; //环境音量
    private static final int MENU_BODY_TEMPERATURE = WIDGETS_TYPE_TEMPERATURE; //体温
    private static final int MENU_SPORTS = WIDGETS_TYPE_RECENT_ACTIVTY;//运动
    private static final int MENU_BATTERY_POWER = WIDGETS_TYPE_BATTERY;//电量
    private static final int MENU_REMINDERS = WIDGETS_TYPE_REMINDERS;//提醒事项
    private static final int MENU_PHONE = WIDGETS_TYPE_CONTACTS;//电话
    private static final int MENU_ALEXA = WIDGETS_TYPE_ALEXA;//Alexa

    TextView tvTips;
    RecyclerView listview;
    RecyclerView listview_unAdded;
    Adapter adapter;
    UnAddedAdapter unAddedAdapter;

    List<ShortcutAppData> addedList = new ArrayList<>();
    List<ShortcutAppData> unAddedList = new ArrayList<>();
    List<SmallQuickModule.SupportInfo> supportInfoList = new ArrayList<>();

    ItemTouchHelper mItemTouchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widgets);
        tvTips = findViewById(R.id.tvTips);
        listview = findViewById(R.id.listview);
        listview_unAdded = findViewById(R.id.listview_unAdded);
        SupportFunctionInfo func = LocalDataManager.getSupportFunctionInfo();
        if (func != null && func.V3_set_main_ui_sort) {
            GridLayoutManager layoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
            layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (addedList.get(position).size_type == BIG_SIZE) {
                        return 2;
                    }
                    return 1;
                }
            });
            listview.setLayoutManager(layoutManager);
            adapter = new Adapter();
            listview.setAdapter(adapter);
            BLEManager.registerOperateCallBack(mOperateCallback);
            BLEManager.querySmallQuickModule();
            showProgressDialog("加载中...");
            tvTips.setVisibility(View.GONE);
        } else {
            tvTips.setVisibility(View.VISIBLE);
        }
        mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int dragFlags;
                int swipeFlags;
                if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                    dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                    swipeFlags = 0;
                } else {
                    dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                    swipeFlags = 0;
                }
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();//得到拖动ViewHolder的position

                int toPosition = target.getAdapterPosition(); //得到目标ViewHolder的position

                if (fromPosition < toPosition) {
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(addedList, i, i + 1);
                    }
                } else {
                    int i = fromPosition;
                    int var7 = toPosition + 1;
                    if (fromPosition >= var7) {
                        while (true) {
                            Collections.swap(addedList, i, i - 1);
                            if (i == var7) {
                                break;
                            }
                            --i;
                        }
                    }
                }
                adapter.notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            }

            @Override
            public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
                super.onSelectedChanged(viewHolder, actionState);
                if (actionState != ItemTouchHelper.ACTION_STATE_IDLE && viewHolder instanceof Adapter.VH) {
                    ((Adapter.VH) viewHolder).rl.setBackgroundResource(R.color.black);
                }
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                if (viewHolder instanceof Adapter.VH) {
                    ((Adapter.VH) viewHolder).rl.setBackgroundResource(R.color.tips_grey_color);
                }
            }
        });
        mItemTouchHelper.attachToRecyclerView(listview);
        adapter.onItemClickListener = position -> {
            ShortcutAppData item = addedList.get(position);
            int index = item.showTypes.indexOf(item.size_type);
            item.size_type = item.showTypes.get(1 - index);
            adapter.notifyItemChanged(position);
        };
        adapter.onItemDeleteClickListener = position -> {
            ShortcutAppData item = addedList.remove(position);
            unAddedList.add(0, item);
            adapter.notifyDataSetChanged();
            unAddedAdapter.notifyDataSetChanged();
        };
        unAddedAdapter = new UnAddedAdapter();
        listview_unAdded.setAdapter(unAddedAdapter);
        unAddedAdapter.onItemClickListener = position -> {
            ShortcutAppData item = unAddedList.remove(position);
            addedList.add(item);
            unAddedAdapter.notifyDataSetChanged();
            adapter.notifyDataSetChanged();
        };
        listview_unAdded.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    private BaseOperateCallback mOperateCallback = new BaseOperateCallback() {
        @Override
        public void onSetResult(OperateCallBack.OperateType operateType, boolean b) {
            super.onSetResult(operateType, b);
            if (operateType == OperateCallBack.OperateType.SMALL_QUICK_MODULE_SORT) {
                showToast(b ? "设置成功" : "设置失败");
                closeProgressDialog();
            }
        }

        @Override
        public void onQueryResult(OperateCallBack.OperateType operateType, Object o) {
            super.onQueryResult(operateType, o);
            if (operateType == OperateCallBack.OperateType.SMALL_QUICK_MODULE_SORT) {
                SmallQuickModule.QueryResponse response = (SmallQuickModule.QueryResponse) o;
                addedList.clear();
                supportInfoList.clear();
                supportInfoList.addAll(response.support_items);
                for (SmallQuickModule module : response.items) {
                    ShortcutAppData data = new ShortcutAppData();
                    data.widgets_type = module.widgets_type;
                    data.size_type = module.size_type;
                    List<Integer> types = new ArrayList<>();
                    for (SmallQuickModule.SupportInfo supportInfo : supportInfoList) {
                        if (supportInfo.widgets_type == module.widgets_type) {
                            if (supportInfo.support_size_type == SupportInfo.SUPOORT_SIZE_TYPE_LARGE) {
                                types.add(BIG_SIZE);
                            } else if (supportInfo.support_size_type == SupportInfo.SUPOORT_SIZE_TYPE_LARGE_AND_SMALL) {
                                types.add(SMALL_SIZE);
                                types.add(BIG_SIZE);
                            } else {
                                types.add(SMALL_SIZE);
                            }
                        }
                    }
                    data.showTypes = types;
                    addedList.add(data);
                }
                adapter.notifyDataSetChanged();

                unAddedList.clear();
                for (SmallQuickModule.SupportInfo supportInfo : supportInfoList) {
                    boolean find = false;
                    for (ShortcutAppData module : addedList) {
                        if (module.widgets_type == supportInfo.widgets_type) {
                            find = true;
                            break;
                        }
                    }
                    if (!find) {
                        ShortcutAppData module = new ShortcutAppData();
                        module.widgets_type = supportInfo.widgets_type;
                        List<Integer> types = new ArrayList<>();
                        if (supportInfo.support_size_type == SupportInfo.SUPOORT_SIZE_TYPE_LARGE) {
                            types.add(BIG_SIZE);
                        } else if (supportInfo.support_size_type == SupportInfo.SUPOORT_SIZE_TYPE_LARGE_AND_SMALL) {
                            types.add(SMALL_SIZE);
                            types.add(BIG_SIZE);
                        } else {
                            types.add(SMALL_SIZE);
                        }
                        module.showTypes = types;
                        module.size_type = types.get(0);
                        unAddedList.add(module);
                    }
                }
                unAddedAdapter.notifyDataSetChanged();
                closeProgressDialog();
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        BLEManager.unregisterOperateCallBack(mOperateCallback);
    }

    public void btSubmit(View view) {
        SmallQuickModule lastItem = null;
        List<SmallQuickModule> result = new ArrayList<>();
        for (ShortcutAppData shortcutAppData : addedList) {
            SmallQuickModule item = new SmallQuickModule();
            item.size_type = shortcutAppData.size_type;
            item.widgets_type = shortcutAppData.widgets_type;
            if (lastItem != null) {//前一个元素
                boolean isItemBig = item.size_type == BIG_SIZE;
                boolean isLastItemBig = lastItem.size_type == BIG_SIZE;
                int lastItemY = lastItem.location_y;
                int lastItemX = lastItem.location_x;
                if (isLastItemBig || isItemBig) {//大号占一行
                    item.location_y = lastItemY + 1;
                    item.location_x = 1;
                } else {//小号
                    item.location_x = 2 - lastItemX + 1;
                    item.location_y = item.location_x == 1 ? lastItemY + 1 : lastItemY;
                }
            } else {//第一个元素
                item.location_y = 1;
                item.location_x = 1;
            }
            lastItem = item;
            result.add(item);
        }
        BLEManager.setSmallQuickModule(result);
        showProgressDialog("Set...");
    }

    class Adapter extends RecyclerView.Adapter<Adapter.VH> {

        OnItemClickListener onItemClickListener;
        OnItemClickListener onItemDeleteClickListener;


        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            if (viewType == BIG_SIZE) {
//                return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_big_widgets, null, false));
//            }
            return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_added_widgets, null, false));
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            ShortcutAppData data = addedList.get(position);
            Pair<Integer, Integer> pair = getShortcutAppInfo(data.widgets_type);
            holder.tvContent.setText(pair.first);
            holder.ivIcon.setImageResource(pair.second);
            holder.ivSwitch.setVisibility(data.showTypes.size() > 1 ? View.VISIBLE : View.GONE);
        }

        @Override
        public int getItemCount() {
            return addedList.size();
        }

        @Override
        public int getItemViewType(int position) {
            return addedList.get(position).size_type;
        }

        class VH extends RecyclerView.ViewHolder {
            RelativeLayout rl;
            TextView tvContent;
            ImageView ivIcon;
            ImageView ivSwitch;
            ImageView ivDelete;

            public VH(@NonNull View itemView) {
                super(itemView);
                rl = itemView.findViewById(R.id.rl);
                tvContent = itemView.findViewById(R.id.tvContent);
                ivIcon = itemView.findViewById(R.id.ivIcon);
                ivSwitch = itemView.findViewById(R.id.ivSwitch);
                ivDelete = itemView.findViewById(R.id.ivDelete);
                ivDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onItemDeleteClickListener != null) {
                            onItemDeleteClickListener.onItemClick(getAdapterPosition());
                        }
                    }
                });
                ivSwitch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onItemClickListener != null) {
                            onItemClickListener.onItemClick(getAdapterPosition());
                        }
                    }
                });
            }
        }
    }

    class UnAddedAdapter extends RecyclerView.Adapter<UnAddedAdapter.VH> {
        OnItemClickListener onItemClickListener;

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_unadded_widgets, null, false));
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            ShortcutAppData data = unAddedList.get(position);
            Pair<Integer, Integer> pair = getShortcutAppInfo(data.widgets_type);
            holder.tvContent.setText(pair.first);
            holder.ivIcon.setImageResource(pair.second);
        }

        @Override
        public int getItemCount() {
            return unAddedList.size();
        }

        class VH extends RecyclerView.ViewHolder {
            TextView tvContent;
            ImageView ivIcon;
            ImageView ivAdd;

            public VH(@NonNull View itemView) {
                super(itemView);
                tvContent = itemView.findViewById(R.id.tvContent);
                ivIcon = itemView.findViewById(R.id.ivIcon);
                ivAdd = itemView.findViewById(R.id.ivAdd);
                ivAdd.setOnClickListener(v -> {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(getAdapterPosition());
                    }
                });
            }
        }
    }

    Pair<Integer, Integer> getShortcutAppInfo(int appUnique) {
        Pair<Integer, Integer> pair;
        switch (appUnique) {
            case MENU_DATA_THREE_CIRCLE://运动三环
                pair = new Pair<>(R.string.shortcut_app_tricyclic, R.mipmap.icon_shortcut_app_tricyclic);
                break;
            case MENU_HEART_RATE://心率
                pair = new Pair<>(R.string.shortcut_app_heart_rate, R.mipmap.icon_shorcut_app_heart_list);
                break;
            case MENU_STEP://步数
                pair = new Pair<>(R.string.shortcut_app_steps, R.mipmap.icon_shorcut_app_walk_list);
                break;
            case MENU_PRESSURE://压力
                pair = new Pair<>(R.string.shortcut_app_spressure, R.mipmap.icon_shorcut_app_pressure_list);
                break;
            case MENU_NOISE://环境音量
                pair = new Pair<>(R.string.shortcut_app_noise, R.mipmap.icon_shorcut_app_noise_list);
                break;
            case MENU_BODY_TEMPERATURE://体温
                pair = new Pair<>(R.string.shortcut_app_body_temperature, R.mipmap.icon_tempretrue);
                break;
            case MENU_BLOOD_OXYGEN://血氧
                pair = new Pair<>(R.string.shortcut_app_blood_oxygen, R.mipmap.icon_shorcut_app_oxygen_list);
                break;
            case MENU_SLEEP://睡眠
                pair = new Pair<>(R.string.shortcut_app_sleep, R.mipmap.icon_shorcut_app_sleep_list);
                break;
            case MENU_SPORTS://运动
                pair = new Pair<>(R.string.shortcut_app_sports, R.mipmap.icon_shorcut_app_sport_list);
                break;
            case MENU_TIMER://计时器
                pair = new Pair<>(R.string.shortcut_app_timer, R.mipmap.icon_shorcut_app_timer_list);
                break;
            case MENU_BATTERY_POWER://电量
                pair = new Pair<>(R.string.shortcut_app_battery_power, R.mipmap.icon_shorcut_app_electricity_list);
                break;
            case MENU_WEATHER://天气
                pair = new Pair<>(R.string.shortcut_app_weather, R.mipmap.icon_shorcut_app_weather_list);
                break;
            case MENU_ALARM://闹钟
                pair = new Pair<>(R.string.shortcut_app_alarm_clock, R.mipmap.icon_shorcut_app_clock_list);
                break;
            case MENU_REMINDERS://提醒事项
                pair = new Pair<>(R.string.shortcut_app_reminders, R.mipmap.icon_shorcut_app_agenda_list);
                break;
            case MENU_MUSIC://音乐控制
                pair = new Pair<>(R.string.shortcut_app_music, R.mipmap.icon_shorcut_app_music_list);
                break;
            case MENU_PHONE://电话
                pair = new Pair<>(R.string.shortcut_app_phone, R.mipmap.icon_shorcut_app_phone_list);
                break;
            case MENU_ALEXA://Alexa
                pair = new Pair<>(R.string.shortcut_app_Alexa, R.mipmap.icon_shorcut_app_alexa_list);
                break;
            case MENU_TIME://世界时间
                pair = new Pair<>(R.string.shortcut_app_world_time, R.mipmap.icon_shorcut_app_world_time_list);
                break;
            default: {
                pair = new Pair<>(R.string.home_steps_tittle, R.mipmap.icon_shortcut_app_tricyclic);
            }
        }
        return pair;
    }

    class ShortcutAppData {
        public List<Integer> showTypes;
        public int size_type = 0;
        public int widgets_type = 0;
    }
}