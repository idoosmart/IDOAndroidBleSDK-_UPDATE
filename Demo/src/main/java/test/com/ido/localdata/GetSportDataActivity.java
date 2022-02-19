package test.com.ido.localdata;

import android.content.Intent;

import com.ido.ble.LocalDataManager;
import com.ido.ble.data.manage.database.HealthSport;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: zhouzj
 * @date: 2017/11/13 16:31
 */

public class GetSportDataActivity extends DataQueryBaseActivity {
    private List<HealthSport> healthSportList = new ArrayList<>();


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
        HealthSport healthSport = healthSportList.get(position);
        if (healthSport != null){
            DataItemQueryType dataItemQueryType = new DataItemQueryType();
            dataItemQueryType.year = healthSport.getYear();
            dataItemQueryType.month = healthSport.getMonth();
            dataItemQueryType.day = healthSport.getDay();

            Intent intent = new Intent();
            intent.setClass(this, GetSportItemDataActivity.class);
            intent.putExtra(DataItemQueryType.INTENT_EXTRA_FLAG, dataItemQueryType);
            startActivity(intent);
        }
    }

    @Override
    protected List<String> getData(DataQueryType dataQueryType) {
        if (dataQueryType.dataType != DataQueryType.DATA_TYPE_SPORT){
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
      if (healthSportList.size() != 0) {
          TotalInfo totalInfo = new TotalInfo();
          for (HealthSport healthSport : healthSportList) {
              if (healthSport == null){
                  continue;
              }
              resultList.add(healthSport.toString());
              totalInfo.mCount ++;
              totalInfo.mTotalCalories += healthSport.getTotalCalory();
              totalInfo.mTotalDistance += healthSport.getTotalDistance();
              totalInfo.mTotalStep += healthSport.getTotalStepCount();
          }

          setTotalInfo(totalInfo.toString());
      }else {
          setTotalInfo("null");
      }
      return resultList;
    }

    private void getOndDayData(DataQueryType dataQueryType){
        healthSportList.clear();
        if (dataQueryType.day == 0 && dataQueryType.month == 0){
            healthSportList.addAll(LocalDataManager.getHealthSportByYear(dataQueryType.year));
        }else if (dataQueryType.day == 0 && dataQueryType.month != 0){
            healthSportList.addAll(LocalDataManager.getHealthSportByMonth(dataQueryType.year, dataQueryType.month));
        }else if (dataQueryType.day != 0 && dataQueryType.month != 0){
            healthSportList.add(LocalDataManager.getHealthSportByDay(dataQueryType.year, dataQueryType.month, dataQueryType.day));
        }

    }

    private void getPeriodData(DataQueryType dataQueryType){
        healthSportList.clear();
//        healthSportList.addAll(LocalDataManager.getHealthSportByDay(dataQueryType.year,
//                dataQueryType.month,
//                dataQueryType.day,
//                dataQueryType.endYear,
//                dataQueryType.endMonth,
//                dataQueryType.endDay));
    }

    private void getWeekData(DataQueryType dataQueryType){
        healthSportList.clear();
        healthSportList.addAll(LocalDataManager.getHealthSportByWeek(dataQueryType.week, 0));
    }
}
