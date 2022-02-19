package test.com.ido.localdata;

import android.os.Bundle;

import com.ido.ble.LocalDataManager;
import com.ido.ble.data.manage.database.HealthSleepItem;

import java.util.ArrayList;
import java.util.List;

public class GetSleepItemDataActivity extends DataQueryBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected List<String> getData(DataItemQueryType dataItemQueryType) {
        List<String> resultList = new ArrayList<>();

        List<HealthSleepItem> healthSleepItemList = LocalDataManager.getHealthSleepItemByDay(dataItemQueryType.year,
                dataItemQueryType.month, dataItemQueryType.day);
        if (healthSleepItemList != null && healthSleepItemList.size() != 0){
            int count = 0;
            for (HealthSleepItem healthSleepItem : healthSleepItemList){
                if (healthSleepItem == null){
                    continue;
                }
                count ++;
                resultList.add(healthSleepItem.toString());
            }

            setTotalInfo("count = " + count);
        }

        return resultList;
    }
}
