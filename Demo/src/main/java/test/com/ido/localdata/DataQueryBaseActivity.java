package test.com.ido.localdata;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;

public abstract class DataQueryBaseActivity extends BaseAutoConnectActivity {

    private ListView listView;
    private TextView tvTotalInfo;
    private BaseAdapter baseAdapter;
    private DataQueryType dataQueryType;
    private DataItemQueryType dataItemQueryType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_query_base);
        
        initView();

        if (initPara()) {
            loadData();
        }else if (initItemPara()){
            loadItemData();
        }
    }

    protected void initView(){
        listView = (ListView) findViewById(R.id.data_query_base_list_view);
        tvTotalInfo = (TextView) findViewById(R.id.data_query_base_total_info_tv);
        baseAdapter = new BaseAdapter();
        listView.setAdapter(baseAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DataQueryBaseActivity.this.onItemClick(position);
            }
        });
    }



    protected boolean initPara(){
        dataQueryType = (DataQueryType) getIntent().getSerializableExtra(DataQueryType.INTENT_EXTRA_FLAG);
        if (dataQueryType == null){
            return false;
        }
        return true;
    }

    protected boolean initItemPara(){
        dataItemQueryType = (DataItemQueryType) getIntent().getSerializableExtra(DataItemQueryType.INTENT_EXTRA_FLAG);
        return dataItemQueryType != null;
    }

    protected void onItemClick(int position){

    }
    protected List<String> getData(DataQueryType dataQueryType){
        return null;
    }

    protected List<String> getData(DataItemQueryType dataItemQueryType){
        return null;
    }
    protected void loadData(){
        List<String> dataList = getData(dataQueryType);
        if (dataList != null){
            baseAdapter.resetData(dataList);
        }
    }
    protected void loadItemData(){
        List<String> dataList = getData(dataItemQueryType);
        if (dataList != null){
            baseAdapter.resetData(dataList);
        }
    }


    protected void setTotalInfo(String text){
        tvTotalInfo.setText(text);
    }

    protected void hideTotalInfoView(){
        tvTotalInfo.setVisibility(View.GONE);
    }

    class BaseAdapter extends android.widget.BaseAdapter{

        List<String> dataList;
        public BaseAdapter(){
            dataList = new ArrayList<>();
        }
        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public Object getItem(int position) {
            return dataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tvItem;
            if (convertView == null) {
                tvItem = new TextView(DataQueryBaseActivity.this);
            }else {
                tvItem = (TextView) convertView;
            }

            tvItem.setText(dataList.get(position));
            if (position % 2 == 0) {
                tvItem.setBackgroundColor(Color.GRAY);
                tvItem.setTextColor(Color.WHITE);
            }else {
                tvItem.setBackgroundColor(Color.WHITE);
                tvItem.setTextColor(Color.BLACK);
            }
            return tvItem;
        }

        public void resetData(List<String> dataList){
            this.dataList.clear();
            this.dataList.addAll(dataList);
            notifyDataSetChanged();
        }
    }
}
