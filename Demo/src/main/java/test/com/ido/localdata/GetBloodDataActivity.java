package test.com.ido.localdata;

import android.content.Intent;

import com.ido.ble.LocalDataManager;
import com.ido.ble.data.manage.database.HealthBloodPressed;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: zhouzj
 * @date: 2017/11/13 16:33
 */

public class GetBloodDataActivity extends DataQueryBaseActivity {
    List<HealthBloodPressed> healthBloodPressedList = new ArrayList<>();
    static class TotalInfo{
        public int mCount = 0;

        @Override
        public String toString() {
            return "{" +
                    "mCount=" + mCount +
                    '}';
        }
    }

    @Override
    protected void onItemClick(int position) {
        HealthBloodPressed healthBloodPressed = healthBloodPressedList.get(position);
        if (healthBloodPressed != null){
            DataItemQueryType dataItemQueryType = new DataItemQueryType();
            dataItemQueryType.year = healthBloodPressed.getYear();
            dataItemQueryType.month = healthBloodPressed.getMonth();
            dataItemQueryType.day = healthBloodPressed.getDay();

            Intent intent = new Intent();
            intent.setClass(this, GetBloodItemDataActivity.class);
            intent.putExtra(DataItemQueryType.INTENT_EXTRA_FLAG, dataItemQueryType);
            startActivity(intent);
        }
    }

    @Override
    protected List<String> getData(DataQueryType dataQueryType) {
        if (dataQueryType.dataType != DataQueryType.DATA_TYPE_BLOOD){
            return null;
        }

        return getRealData(dataQueryType);

    }

    private List<String> getRealData(DataQueryType dataQueryType){
        if (dataQueryType.queryType == DataQueryType.QUERY_TYPE_ONE_DAY){
            getOndDayData(dataQueryType);
        }else if (dataQueryType.queryType == DataQueryType.QUERY_TYPE_PERIOD){
            getPeriodData(dataQueryType);
        }else if (dataQueryType.queryType == DataQueryType.QUERY_TYPE_WEEK){
            getWeekData(dataQueryType);
        }

        List<String> resultList = new ArrayList<>();
        if (healthBloodPressedList.size() != 0) {
            TotalInfo totalInfo = new TotalInfo();
            for (HealthBloodPressed healthBloodPressed : healthBloodPressedList) {
                if (healthBloodPressed == null){
                    continue;
                }
                resultList.add(healthBloodPressed.toString());
                totalInfo.mCount ++;
            }

            setTotalInfo(totalInfo.toString());
        }else {
            setTotalInfo("null");
        }
        return resultList;
    }

    private void getOndDayData(DataQueryType dataQueryType){
        healthBloodPressedList.clear();
        if (dataQueryType.day == 0 && dataQueryType.month == 0){
            healthBloodPressedList.addAll(LocalDataManager.getHealthBloodPressedByYear(dataQueryType.year));
        }else if (dataQueryType.day == 0 && dataQueryType.month != 0){
            healthBloodPressedList.addAll(LocalDataManager.getHealthBloodPressedByMonth(dataQueryType.year, dataQueryType.month));
        }else if (dataQueryType.day != 0 && dataQueryType.month != 0){
            healthBloodPressedList.add(LocalDataManager.getHealthBloodPressedByDay(dataQueryType.year, dataQueryType.month, dataQueryType.day));
        }

    }

    private void getPeriodData(DataQueryType dataQueryType){
        healthBloodPressedList.clear();
//        healthActivityList.addAll(LocalDataManager.getHealthActivityByDay(dataQueryType.year,
//                dataQueryType.month,
//                dataQueryType.day,
//                dataQueryType.endYear,
//                dataQueryType.endMonth,
//                dataQueryType.endDay));
    }

    private void getWeekData(DataQueryType dataQueryType){
        healthBloodPressedList.clear();
        healthBloodPressedList.addAll(LocalDataManager.getHealthBloodPressedByWeek(dataQueryType.week, 0));
    }
}
