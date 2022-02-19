package test.com.ido.localdata;

import android.os.Bundle;

import com.ido.ble.LocalDataManager;
import com.ido.ble.data.manage.database.HealthSportItem;

import java.util.ArrayList;
import java.util.List;

public class GetSportItemDataActivity extends DataQueryBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected List<String> getData(DataItemQueryType dataItemQueryType) {
        List<String> resultList = new ArrayList<>();

        List<HealthSportItem> healthSportItemList = LocalDataManager.getHealthSportItemByDay(dataItemQueryType.year,
                dataItemQueryType.month, dataItemQueryType.day);
        if (healthSportItemList != null && healthSportItemList.size() != 0){
            int count = 0;
            for (HealthSportItem healthSportItem : healthSportItemList){
                if (healthSportItem == null){
                    continue;
                }
                count ++;
                resultList.add(healthSportItem.toString());
            }

            setTotalInfo("count = " + count);
        }

        return resultList;
    }
}
