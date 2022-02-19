package test.com.ido.gps;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.ido.ble.LocalDataManager;
import com.ido.ble.gps.database.HealthGps;

import java.util.List;

import test.com.ido.R;
import test.com.ido.utils.ViewAdapter;
import test.com.ido.utils.ViewHolder;

public class BaiDuMapActivity extends Activity {

    private SDKReceiver mReceiver;

    /**
     * 构造广播监听类，监听 SDK key 验证以及网络异常广播
     */
    public class SDKReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String s = intent.getAction();
            if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
                Toast.makeText(BaiDuMapActivity.this, "apikey验证失败，地图功能无法正常使用", Toast.LENGTH_SHORT).show();
            } else if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK)) {
                Toast.makeText(BaiDuMapActivity.this, "apikey验证成功", Toast.LENGTH_SHORT).show();
            } else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
                Toast.makeText(BaiDuMapActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 展示列表
    private ListView mListView;
    private ViewAdapter<HealthGps> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bai_du_map);

        // apikey的授权需要一定的时间，在授权成功之前地图相关操作会出现异常；apikey授权成功后会发送广播通知，我们这里注册 SDK 广播监听者
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK);
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        mReceiver = new SDKReceiver();
        registerReceiver(mReceiver, iFilter);

        mListView = (ListView) findViewById(R.id.id_listview);
        List<HealthGps> list = LocalDataManager.getAllHealthGps();
        adapter = new ViewAdapter<HealthGps>(getApplicationContext(), list, R.layout.activity_baidu_map_item) {
            @Override
            public void convert(ViewHolder helper, HealthGps item) {
                helper.setString(R.id.id_sport_time, getString(R.string.sport_time, getTime(item)));
            }
        };
        mListView.setAdapter(adapter);

        /*点击事件*/
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HealthGps item = (HealthGps) adapterView.getItemAtPosition(i);
                Intent intent = new Intent();
                intent.putExtra("date", item.getDate());
                intent.setClass(getApplicationContext(), StaticMapActivity.class);
                startActivity(intent);
            }
        });

    }

    /**
     * 获取时间
     *
     * @param item
     * @return
     */
    private String getTime(HealthGps item) {
        return "" + String.format("%04d", item.getYear())
                + "/" + String.format("%02d", item.getMonth())
                + "/" + String.format("%02d", item.getDay())
                + "/" + String.format("%02d", item.getHour())
                + "/" + String.format("%02d", item.getMinute())
                + "/" + String.format("%02d", item.getSecond());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
