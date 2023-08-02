package test.com.ido.runplan;
/**
 * Copyright (c) 2019 深圳市爱都科技有限公司. All rights reserved.
 * @Author: xyb
 * @CreateDate: 2020-05-04 18:18
 * @description 意见反馈实体类
 */
public class SportGps {
    private int interval;
    private String items;

    public SportGps() {

    }

    /**
     * gps 对象
     * @param interval
     * @param items
     */
    public SportGps(int interval, String items) {
        this.interval = interval;
        this.items = items;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "Gps{" +
                "interval=" + interval +
                ", items='" + items + '\'' +
                '}';
    }
}
