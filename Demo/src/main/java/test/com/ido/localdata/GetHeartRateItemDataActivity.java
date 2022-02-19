package test.com.ido.localdata;

import android.os.Bundle;

import com.ido.ble.LocalDataManager;
import com.ido.ble.data.manage.database.HealthHeartRateItem;

import java.util.ArrayList;
import java.util.List;

public class GetHeartRateItemDataActivity extends DataQueryBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected List<String> getData(DataItemQueryType dataItemQueryType) {
        List<String> resultList = new ArrayList<>();

        List<HealthHeartRateItem> healthHeartRateItemList = LocalDataManager.getHealthHeartRateItemByDay(dataItemQueryType.year,
                dataItemQueryType.month, dataItemQueryType.day);
        if (healthHeartRateItemList != null && healthHeartRateItemList.size() != 0){
            int count = 0;
            for (HealthHeartRateItem healthSleepItem : healthHeartRateItemList){
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
