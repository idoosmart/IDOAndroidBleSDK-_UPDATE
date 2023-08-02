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
public class ConvertPaceItem implements PropertyConverter<SportItemPace,String> {
    @Override
    public SportItemPace convertToEntityProperty(String databaseValue) {
        SportItemPace sportItemPace = null;
        sportItemPace = GsonUtil.fromJson(databaseValue,SportItemPace.class);
        try {
            sportItemPace = GsonUtil.fromJson(databaseValue,SportItemPace.class);
        }catch (Exception e){

        }
        return sportItemPace;
    }

    @Override
    public String convertToDatabaseValue(SportItemPace entityProperty) {
        String item = "";
        try{
            item = GsonUtil.toJson(entityProperty);
        }catch (Exception e){

        }
        return item;
    }
}
