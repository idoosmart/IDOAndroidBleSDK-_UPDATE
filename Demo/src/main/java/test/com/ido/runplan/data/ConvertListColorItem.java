package test.com.ido.runplan.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.List;

import test.com.ido.utils.GsonUtil;

/**
 * Copyright (c) 2019 深圳市爱都科技有限公司. All rights reserved.
 *
 * @Author: xyb
 * @CreateDate: 2020/12/27 15:04
 * @description
 */
public class ConvertListColorItem implements PropertyConverter<List<Integer>,String> {


    @Override
    public List<Integer> convertToEntityProperty(String databaseValue) {
        List<Integer> points = null;
        try {
            points = new Gson().fromJson(databaseValue, new TypeToken<List<Integer>>() {
            }.getType());
        } catch (Exception e) {
        }
        return points;
    }

    @Override
    public String convertToDatabaseValue(List<Integer> entityProperty) {
        String item = "";
        try {
            item = GsonUtil.toJson(entityProperty);
        } catch (Exception e) {

        }
        return item;
    }
}