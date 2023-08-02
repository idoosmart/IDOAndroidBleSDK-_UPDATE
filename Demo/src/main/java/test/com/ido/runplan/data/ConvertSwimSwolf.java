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
public class ConvertSwimSwolf implements PropertyConverter<SportSwimSwolf,String> {
    @Override
    public SportSwimSwolf convertToEntityProperty(String databaseValue) {
        SportSwimSwolf sportSwimSwolf = null;
        try {
            sportSwimSwolf = GsonUtil.fromJson(databaseValue,SportSwimSwolf.class);
        }catch (Exception e){

        }
        return sportSwimSwolf;
    }

    @Override
    public String convertToDatabaseValue(SportSwimSwolf entityProperty) {
        String item = "";
        try{
            item = GsonUtil.toJson(entityProperty);
        }catch (Exception e){

        }
        return item;
    }

    /*@Override
    public SportItem convertToEntityProperty(String databaseValue) {
        SportItem sportItem = null;
        try{
            sportItem = GsonUtil.fromJson(databaseValue,SportItem.class);
        }catch (Exception e){
        }
        return sportItem;
    }

    @Override
    public String convertToDatabaseValue(SportItem entityProperty) {
        String item = "";
        try{
            item = GsonUtil.toJson(entityProperty);
        }catch (Exception e){

        }
        return item;
    }*/
}
