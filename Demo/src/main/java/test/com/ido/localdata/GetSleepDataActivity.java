package test.com.ido.localdata;

import android.content.Intent;

import com.ido.ble.LocalDataManager;
import com.ido.ble.data.manage.database.HealthSleep;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: zhouzj
 * @date: 2017/11/13 16:32
 */

public class GetSleepDataActivity extends DataQueryBaseActivity {
    private List<HealthSleep> healthSleepList = new ArrayList<>();

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
        HealthSleep healthSleep = healthSleepList.get(position);
        if (healthSleep != null){
            DataItemQueryType dataItemQueryType = new DataItemQueryType();
            dataItemQueryType.year = healthSleep.getYear();
            dataItemQueryType.month = healthSleep.getMonth();
            dataItemQueryType.day = healthSleep.getDay();

            Intent intent = new Intent();
            intent.setClass(this, GetSleepItemDataActivity.class);
            intent.putExtra(DataItemQueryType.INTENT_EXTRA_FLAG, dataItemQueryType);
            startActivity(intent);
        }
    }

    @Override
    protected List<String> getData(DataQueryType dataQueryType) {
        if (dataQueryType.dataType != DataQueryType.DATA_TYPE_SLEEP){
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
        if (healthSleepList.size() != 0) {
            TotalInfo totalInfo = new TotalInfo();
            for (HealthSleep healthSleep : healthSleepList) {
                if (healthSleep == null){
                    continue;
                }
                resultList.add(healthSleep.toString());
                totalInfo.mCount ++;
            }

            setTotalInfo(totalInfo.toString());
        }else {
            setTotalInfo("null");
        }
        return resultList;
    }

    private void getOndDayData(DataQueryType dataQueryType){
        healthSleepList.clear();
        if (dataQueryType.day == 0 && dataQueryType.month == 0){
            healthSleepList.addAll(LocalDataManager.getHealthSleepByYear(dataQueryType.year));
        }else if (dataQueryType.day == 0 && dataQueryType.month != 0){
            healthSleepList.addAll(LocalDataManager.getHealthSleepByMonth(dataQueryType.year, dataQueryType.month));
        }else if (dataQueryType.day != 0 && dataQueryType.month != 0){
            healthSleepList.add(LocalDataManager.getHealthSleepByDay(dataQueryType.year, dataQueryType.month, dataQueryType.day));
        }

    }

    private void getPeriodData(DataQueryType dataQueryType){
        healthSleepList.clear();
//        healthActivityList.addAll(LocalDataManager.getHealthActivityByDay(dataQueryType.year,
//                dataQueryType.month,
//                dataQueryType.day,
//                dataQueryType.endYear,
//                dataQueryType.endMonth,
//                dataQueryType.endDay));
    }

    private void getWeekData(DataQueryType dataQueryType){
        healthSleepList.clear();
        healthSleepList.addAll(LocalDataManager.getListHealthSleepByWeek(dataQueryType.week, 0));
    }
}
