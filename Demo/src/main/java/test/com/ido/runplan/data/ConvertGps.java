package test.com.ido.runplan.data;



import org.greenrobot.greendao.converter.PropertyConverter;

import test.com.ido.runplan.SportGps;
import test.com.ido.utils.GsonUtil;

/**
 * Copyright (c) 2019 深圳市爱都科技有限公司. All rights reserved.
 *
 * @Author: xyb
 * @CreateDate: 2020-04-24 10:36
 * @description
 */
public class ConvertGps implements PropertyConverter<SportGps,String> {

    @Override
    public SportGps convertToEntityProperty(String databaseValue) {
        SportGps result=null;
        try {
            result = GsonUtil.fromJson(databaseValue,SportGps.class);
        }catch (Exception e){

        }
        return result;
    }

    @Override
    public String convertToDatabaseValue(SportGps entityProperty) {
        String  result = "";
        try {
            result = GsonUtil.toJson(entityProperty);
        }catch (Exception e){
        }

        return result;
    }
}
