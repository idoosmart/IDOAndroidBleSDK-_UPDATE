package test.com.ido.runplan;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ido.ble.logs.LogTool;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;
import test.com.ido.runplan.data.SportDataUtil;
import test.com.ido.runplan.page.HealthManagerActivity;
import test.com.ido.runplan.utils.AGException;
import test.com.ido.runplan.utils.GreenDaoUtil;
import test.com.ido.runplan.utils.IOkHttpCallBack;
import test.com.ido.runplan.utils.OkHttpUtil;
import test.com.ido.runplan.utils.RunTimeUtil;
import test.com.ido.runplan.utils.SPUtils;
import test.com.ido.utils.TimeUtil;
import test.com.ido.utils.ViewHolder;

public class RunPlanActivity extends BaseAutoConnectActivity implements View.OnClickListener {
    private static final String TAG = "RunPlanActivity";
    ListView recycler;
    MyAdapter mAdapter;
    List<SportHealth> mData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_plan);
        recycler = findViewById(R.id.recycler);
        mAdapter = new MyAdapter(this,mData);
        recycler.setAdapter(mAdapter);
        findViewById(R.id.btn_run).setOnClickListener(this);
        findViewById(R.id.btn_refresh).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initData(){
        mData.clear();
        mData.addAll(GreenDaoUtil.getAllActivityData(RunTimeUtil.getInstance().getUserId()));
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_run){
            String COMMON_RUN_PLAN_MANAGER_URL_NEW = "https://%d-healthmanage.idoocloud.com/running";
            HealthManagerActivity.Companion.startActivity(this,
                    COMMON_RUN_PLAN_MANAGER_URL_NEW.replace("%d","cn"),
                    Constants.INTENT_FROM_RUN_PLAN);
        } else if(v.getId() == R.id.btn_refresh){
            OkHttpUtil.getInstance().getAccessToken(new IOkHttpCallBack<String>() {
                @Override
                public void success(String s) {
                    recycler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jsonObject = new JSONObject(s);
                                String token = jsonObject.getString("result");
                                LogTool.e(TAG,token);
                                if(!TextUtils.isEmpty(token)){
                                    Toast.makeText(RunPlanActivity.this,"刷新成功",Toast.LENGTH_SHORT).show();
                                    SPUtils.put(Constants.TOKEN,token);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

                @Override
                public void fail(AGException e) {
                    LogTool.e(TAG,e.toString());
                }
            });
        }

    }

    static class MyAdapter extends ViewAdapter<SportHealth>{

        public MyAdapter(Context context, List<SportHealth> datas) {
            super(context, datas, R.layout.item_run_plan);
        }

        @Override
        public void convert(ViewHolder helper, SportHealth item) {
            super.convert(helper, item);
            TextView tv_date_time = helper.getView(R.id.tv_date_time);
            TextView tv_sport_duration = helper.getView(R.id.tv_sport_duration);
            TextView tv_sport_distance = helper.getView(R.id.tv_sport_distance);
            tv_date_time.setText(item.getDateTime());
            tv_sport_duration.setText("耗时："+ TimeUtil.second2Minute(item.getTotalSeconds()));
            tv_sport_distance.setText(SportDataUtil.formatkm(item.getDistance())+"km");
        }
    }
}
