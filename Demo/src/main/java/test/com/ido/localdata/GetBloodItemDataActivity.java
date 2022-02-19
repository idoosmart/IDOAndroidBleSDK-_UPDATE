package test.com.ido.localdata;

import android.os.Bundle;

import com.ido.ble.LocalDataManager;
import com.ido.ble.data.manage.database.HealthBloodPressedItem;

import java.util.ArrayList;
import java.util.List;

public class GetBloodItemDataActivity extends DataQueryBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected List<String> getData(DataItemQueryType dataItemQueryType) {
        List<String> resultList = new ArrayList<>();

        List<HealthBloodPressedItem> healthBloodPressedItemList = LocalDataManager.getHealthBloodPressedItemByDay(dataItemQueryType.year,
                dataItemQueryType.month, dataItemQueryType.day);
        if (healthBloodPressedItemList != null && healthBloodPressedItemList.size() != 0){
            int count = 0;
            for (HealthBloodPressedItem healthBloodPressedItem : healthBloodPressedItemList){
                if (healthBloodPressedItem == null){
                    continue;
                }
                count ++;
                resultList.add(healthBloodPressedItem.toString());
            }

            setTotalInfo("count = " + count);
        }

        return resultList;
    }
}
