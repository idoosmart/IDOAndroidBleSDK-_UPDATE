package test.com.ido.worldtime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;
import test.com.ido.utils.CalendarUtils;
import test.com.ido.utils.ExecutorDispatcher;
import test.com.ido.utils.OnItemClickListener;
import test.com.ido.utils.ResUtils;
import test.com.ido.utils.WorldTimeUtils;

public class WorldTimeChooseActivity extends BaseAutoConnectActivity {

    private static final int MAX_ITEMS = 10;
    private static final int MIN_ITEMS = 1;

    RecyclerView listview;
    WtAdapter adapter;

    private ArrayList<Integer> selectedIds = new ArrayList<>();
    private ArrayList<Integer> ids = new ArrayList<>();
    Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_world_time_choose);
        listview = findViewById(R.id.listview);
        adapter = new WtAdapter();
        adapter.onItemClickListener = position -> {
            int id = ids.get(position);
            if (selectedIds.contains(id)) {
                if (selectedIds.size() == MIN_ITEMS) {
                    showToast(String.format(ResUtils.getString(R.string.world_time_min_item), MIN_ITEMS));
                    return;
                }
                selectedIds.remove((Integer) id);
            } else {
                if (selectedIds.size() == MAX_ITEMS) {
                    showToast(String.format(ResUtils.getString(R.string.world_time_max_item), MAX_ITEMS));
                    return;
                }
                selectedIds.add(ids.get(position));
            }
            adapter.notifyDataSetChanged();
        };
        listview.setAdapter(adapter);
        listview.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        ArrayList<Integer> list = getIntent().getIntegerArrayListExtra("ids");
        selectedIds.addAll(list);
        loadData();
    }

    private void loadData() {
        int[] allIds = getResources().getIntArray(R.array.world_time_ids);
        for (int id : allIds) {
            ids.add(id);
        }
        adapter.notifyDataSetChanged();
        startTimer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    private void startTimer() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                ExecutorDispatcher.getInstance().dispatchOnMainThread(() -> adapter.notifyDataSetChanged());
            }
        }, 1000, 1000);
    }

    public void btConfirm(View view) {
        setResult(Activity.RESULT_OK, new Intent(this, WorldTimeActivity.class).putIntegerArrayListExtra("ids", selectedIds));
        finish();
    }

    class WtAdapter extends RecyclerView.Adapter<WtAdapter.VH> {

        OnItemClickListener onItemClickListener;

        @NonNull
        @Override
        public WtAdapter.VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new WtAdapter.VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wt, null, false));
        }

        @Override
        public void onBindViewHolder(@NonNull WtAdapter.VH holder, int position) {
            int id = ids.get(position);
            holder.tvName.setText(WorldTimeUtils.getCityName(id));
            holder.tvTime.setText(CalendarUtils.getTime(Calendar.getInstance(TimeZone.getTimeZone(WorldTimeUtils.getTimezone(id)))));
            holder.tvCountryName.setText(WorldTimeUtils.getCountryName(id));
            if (selectedIds.contains(id)) {
                holder.itemView.setBackgroundColor(getResources().getColor(R.color.light_grey));
            } else {
                holder.itemView.setBackgroundColor(getResources().getColor(R.color.base_color_text_gray));
            }
        }

        @Override
        public int getItemCount() {
            return ids.size();
        }

        class VH extends RecyclerView.ViewHolder {

            TextView tvName;
            TextView tvCountryName;
            TextView tvTime;

            public VH(@NonNull View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tvName);
                tvCountryName = itemView.findViewById(R.id.tvCountryName);
                tvTime = itemView.findViewById(R.id.tvTime);
                itemView.setOnClickListener(v -> {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(getAdapterPosition());
                    }
                });
            }
        }
    }
}