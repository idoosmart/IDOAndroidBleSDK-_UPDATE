package test.com.ido.localdata;

import android.content.Intent;

import com.ido.ble.LocalDataManager;
import com.ido.ble.data.manage.database.HealthHeartRate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: zhouzj
 * @date: 2017/11/13 16:32
 */

public class GetHeartRateActivity extends DataQueryBaseActivity {
    List<HealthHeartRate> healthHeartRateList = new ArrayList<>();
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
        HealthHeartRate healthHeartRate = healthHeartRateList.get(position);
        if (healthHeartRate != null){
            DataItemQueryType dataItemQueryType = new DataItemQueryType();
            dataItemQueryType.year = healthHeartRate.getYear();
            dataItemQueryType.month = healthHeartRate.getMonth();
            dataItemQueryType.day = healthHeartRate.getDay();

            Intent intent = new Intent();
            intent.setClass(this, GetHeartRateItemDataActivity.class);
            intent.putExtra(DataItemQueryType.INTENT_EXTRA_FLAG, dataItemQueryType);
            startActivity(intent);
        }
    }

    @Override
    protected List<String> getData(DataQueryType dataQueryType) {
        if (dataQueryType.dataType != DataQueryType.DATA_TYPE_HEART_RATE){
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
        if (healthHeartRateList.size() != 0) {
            TotalInfo totalInfo = new TotalInfo();
            for (HealthHeartRate healthHeartRate : healthHeartRateList) {
                if (healthHeartRate == null){
                    continue;
                }
                resultList.add(healthHeartRate.toString());
                totalInfo.mCount ++;
            }

            setTotalInfo(totalInfo.toString());
        }else {
            setTotalInfo("null");
        }
        return resultList;
    }

    private void getOndDayData(DataQueryType dataQueryType){
        healthHeartRateList.clear();
        if (dataQueryType.day == 0 && dataQueryType.month == 0){
            healthHeartRateList.addAll(LocalDataManager.getHealthHeartRateByYear(dataQueryType.year));
        }else if (dataQueryType.day == 0 && dataQueryType.month != 0){
            healthHeartRateList.addAll(LocalDataManager.getHealthHeartRateByMonth(dataQueryType.year, dataQueryType.month));
        }else if (dataQueryType.day != 0 && dataQueryType.month != 0){
            healthHeartRateList.add(LocalDataManager.getHealthHeartRateByDay(dataQueryType.year, dataQueryType.month, dataQueryType.day));
        }

    }

    private void getPeriodData(DataQueryType dataQueryType){
        healthHeartRateList.clear();
//        healthActivityList.addAll(LocalDataManager.getHealthActivityByDay(dataQueryType.year,
//                dataQueryType.month,
//                dataQueryType.day,
//                dataQueryType.endYear,
//                dataQueryType.endMonth,
//                dataQueryType.endDay));
    }

    private void getWeekData(DataQueryType dataQueryType){
        healthHeartRateList.clear();
        healthHeartRateList.addAll(LocalDataManager.getHealthHeartRateByWeek(dataQueryType.week, 0));
    }
}
