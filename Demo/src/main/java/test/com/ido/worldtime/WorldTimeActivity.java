package test.com.ido.worldtime;

import static java.lang.Math.abs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ido.ble.BLEManager;
import com.ido.ble.callback.SettingCallBack;
import com.ido.ble.protocol.model.WorldTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;
import test.com.ido.utils.BigDecimalUtil;
import test.com.ido.utils.CalendarUtils;
import test.com.ido.utils.DataUtils;
import test.com.ido.utils.ExecutorDispatcher;
import test.com.ido.utils.ResUtils;
import test.com.ido.utils.WorldTimeUtils;

public class WorldTimeActivity extends BaseAutoConnectActivity {
    private static final String TAG = "WorldTimeActivity";

    private static final int NORTH = 1;
    private static final int SOUTH = 2;
    private static final int EAST = 1;
    private static final int WEST = 2;

    /**
     * 默认时钟id：London（148），New York（197），Tokyo（295）
     */
    private static final int[] DEFAULT_CITY_IDS = new int[]{148, 197, 295, 195};

    /**
     * 设置的世界时钟id
     */
    private ArrayList<Integer> ids = new ArrayList<>();

    RecyclerView listview;
    WtAdapter adapter;

    Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_world_time);
        listview = findViewById(R.id.listview);
        listview.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WtAdapter();
        listview.setAdapter(adapter);
        listview.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        loadData();
    }

    private void loadData() {
        ids.clear();
        List<Integer> setList = DataUtils.getInstance().getWorldTimeList();
        if (setList == null || setList.isEmpty()) {
            for (int id : DEFAULT_CITY_IDS) {
                ids.add(id);
            }
        } else {
            ids.addAll(setList);
        }
        adapter.notifyDataSetChanged();
        startTimer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
        BLEManager.unregisterSettingCallBack(mSettingCallBack);
    }

    private void startTimer() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                ExecutorDispatcher.getInstance().dispatchOnMainThread(() -> adapter.notifyDataSetChanged());
            }
        }, 1000, 1000);
    }

    public void btSubmit(View view) {
        List<WorldTime.Item> items = new ArrayList<>();
        for (int id : ids) {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(WorldTimeUtils.getTimezone(id)));
            Pair<Double, Double> latLon = WorldTimeUtils.getLatLon(id);

            //TODO 日出日落此处只是给出示例，具体的日出日落数据获取途径取决sdk使用者
            //TODO 日出日落此处只是给出示例，具体的日出日落数据获取途径取决sdk使用者
            //TODO 日出日落此处只是给出示例，具体的日出日落数据获取途径取决sdk使用者
            //TODO Here is just an example of sunrise and sunset. The specific way to obtain sunrise and sunset data depends on the SDK user.
            //TODO Here is just an example of sunrise and sunset. The specific way to obtain sunrise and sunset data depends on the SDK user.
            //TODO Here is just an example of sunrise and sunset. The specific way to obtain sunrise and sunset data depends on the SDK user.
            Pair<String, String> sunRise = WorldTimeUtils.getSunRiseTime(latLon.second, latLon.first, calendar);
            Pair<String, String> sunSet = WorldTimeUtils.getSunSetTime(latLon.second, latLon.first, calendar);
            WorldTime.Item item = new WorldTime.Item();
            item.id = id;
            item.city_name = ResUtils.getString(WorldTimeUtils.getCityName(id));
            try {
                item.latitude = (int) abs((BigDecimalUtil.round(latLon.first, 2) * 100));//保留两位小数*100
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            try {
                item.longitude = (int) abs((BigDecimalUtil.round(latLon.second, 2) * 100));//保留两位小数*100
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            item.latitude_flag = latLon.first >= 0 ? NORTH : SOUTH;
            item.longitude_flag = latLon.second >= 0 ? EAST : WEST;
            item.sunrise_hour = Integer.parseInt(sunRise.first);
            item.sunrise_min = Integer.parseInt(sunRise.second);
            item.sunset_hour = Integer.parseInt(sunSet.first);
            item.sunset_min = Integer.parseInt(sunSet.second);
            item.min_offset = CalendarUtils.getTimezoneOffsetInMin(TimeZone.getTimeZone(WorldTimeUtils.getTimezone(id)));
            items.add(item);
        }
        BLEManager.unregisterSettingCallBack(mSettingCallBack);
        BLEManager.registerSettingCallBack(mSettingCallBack);
        BLEManager.setWorldTime(items);
        showProgressDialog("set...");
    }

    private final SettingCallBack.ICallBack mSettingCallBack = new SettingCallBack.ICallBack() {
        @Override
        public void onSuccess(SettingCallBack.SettingType settingType, Object o) {
            if (settingType == SettingCallBack.SettingType.WORLD_TIME) {
                showToast(ResUtils.getString(R.string.set_tip_success));
                closeProgressDialog();
                DataUtils.getInstance().saveWorldTimeList(ids);
            }
        }

        @Override
        public void onFailed(SettingCallBack.SettingType settingType) {
            if (settingType == SettingCallBack.SettingType.WORLD_TIME) {
                showToast(ResUtils.getString(R.string.set_tip_failed));
            }
        }
    };

    public void btChooseWt(View view) {
        startActivityForResult(new Intent(this, WorldTimeChooseActivity.class).putIntegerArrayListExtra("ids", ids), 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {
            ids.clear();
            ArrayList<Integer> list = data.getIntegerArrayListExtra("ids");
            ids.addAll(list);
            adapter.notifyDataSetChanged();
        }
    }

    class WtAdapter extends RecyclerView.Adapter<WtAdapter.VH> {
        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wt, null, false));
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            int id = ids.get(position);
            holder.tvName.setText(WorldTimeUtils.getCityName(id));
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(WorldTimeUtils.getTimezone(id)));
            holder.tvTime.setText(CalendarUtils.getTime(calendar));
            holder.tvCountryName.setText(WorldTimeUtils.getCountryName(id));
            Pair<Double, Double> pair = WorldTimeUtils.getLatLon(id);
            holder.tvLatLon.setText("lat: " + pair.first + ", lon: " + pair.second);
        }

        @Override
        public int getItemCount() {
            return ids.size();
        }

        class VH extends RecyclerView.ViewHolder {

            TextView tvName;
            TextView tvCountryName;
            TextView tvTime;
            TextView tvSunrise;
            TextView tvLatLon;

            public VH(@NonNull View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tvName);
                tvCountryName = itemView.findViewById(R.id.tvCountryName);
                tvTime = itemView.findViewById(R.id.tvTime);
                tvSunrise = itemView.findViewById(R.id.tvSunrise);
                tvLatLon = itemView.findViewById(R.id.tvLatLon);
            }
        }
    }
}