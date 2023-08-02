package test.com.ido.runplan.data;



import org.greenrobot.greendao.converter.PropertyConverter;

import test.com.ido.utils.GsonUtil;

/**
 * Copyright (c) 2019 深圳市爱都科技有限公司. All rights reserved.
 *
 * @Author: xyb
 * @CreateDate: 2020/8/31 10:39
 * @description
 */
public class ConvertRealPaceItem implements PropertyConverter<SportRealTimePace,String> {
    @Override
    public SportRealTimePace convertToEntityProperty(String databaseValue) {
        SportRealTimePace sportItemPace = null;
        sportItemPace = GsonUtil.fromJson(databaseValue, SportRealTimePace.class);
        try {
            sportItemPace = GsonUtil.fromJson(databaseValue, SportRealTimePace.class);
        }catch (Exception e){

        }
        return sportItemPace;
    }

    @Override
    public String convertToDatabaseValue(SportRealTimePace entityProperty) {
        String item = "";
        try{
            item = GsonUtil.toJson(entityProperty);
        }catch (Exception e){

        }
        return item;
    }
}
