package test.com.ido.localdata;

import com.ido.ble.LocalDataManager;
import com.ido.ble.data.manage.database.HealthActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: zhouzj
 * @date: 2017/11/13 16:30
 */

public class GetActivityDataActivity extends DataQueryBaseActivity{
    private List<HealthActivity> healthActivityList = new ArrayList<>();

    static class TotalInfo{
        public int mCount = 0;
        public int mTotalStep = 0;
        public int mTotalDistance = 0;
        public int mTotalCalories = 0;

        @Override
        public String toString() {
            return "{" +
                    "mCount=" + mCount +
                    ", mTotalStep=" + mTotalStep +
                    ", mTotalDistance=" + mTotalDistance +
                    ", mTotalCalories=" + mTotalCalories +
                    '}';
        }
    }

    @Override
    protected void onItemClick(int position) {

    }

    @Override
    protected List<String> getData(DataQueryType dataQueryType) {
        if (dataQueryType.dataType != DataQueryType.DATA_TYPE_ACTIVITY){
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
        if (healthActivityList.size() != 0) {
            TotalInfo totalInfo = new TotalInfo();
            for (HealthActivity healthActivity : healthActivityList) {
                if (healthActivity == null){
                    continue;
                }
                resultList.add(healthActivity.toString());
                totalInfo.mCount ++;
                totalInfo.mTotalStep += healthActivity.getStep();
                totalInfo.mTotalCalories += healthActivity.getCalories();
                totalInfo.mTotalDistance += healthActivity.getDistance();
            }

            setTotalInfo(totalInfo.toString());
        }else {
            setTotalInfo("null");
        }
        return resultList;
    }

    private void getOndDayData(DataQueryType dataQueryType){
        healthActivityList.clear();
        if (dataQueryType.day == 0 && dataQueryType.month == 0){
            healthActivityList.addAll(LocalDataManager.getHealthActivityByYear(dataQueryType.year));
        }else if (dataQueryType.day == 0 && dataQueryType.month != 0){
            healthActivityList.addAll(LocalDataManager.getHealthActivityByMonth(dataQueryType.year, dataQueryType.month));
        }else if (dataQueryType.day != 0 && dataQueryType.month != 0){
            healthActivityList.addAll(LocalDataManager.getHealthActivityByDay(dataQueryType.year, dataQueryType.month, dataQueryType.day));
        }

    }

    private void getPeriodData(DataQueryType dataQueryType){
        healthActivityList.clear();
//        healthActivityList.addAll(LocalDataManager.getHealthActivityByDay(dataQueryType.year,
//                dataQueryType.month,
//                dataQueryType.day,
//                dataQueryType.endYear,
//                dataQueryType.endMonth,
//                dataQueryType.endDay));
    }

    private void getWeekData(DataQueryType dataQueryType){
        healthActivityList.clear();
        healthActivityList.addAll(LocalDataManager.getHealthActivityByWeek(dataQueryType.week, 0));
    }
}
