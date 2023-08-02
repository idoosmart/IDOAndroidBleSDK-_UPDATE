package test.com.ido.runplan.data;

/**
 * Copyright (c) 2019 深圳市爱都科技有限公司. All rights reserved.
 *
 * @Author: xyb
 * @CreateDate: 2020/8/31 10:45
 * @description
 */
public class SportItem {
    private int interval;
    private String items;

    public SportItem() {
    }

    public SportItem(String itmes) {
        this.items = itmes;
    }

    public String getItmes() {
        return items;
    }

    public void setItmes(String itmes) {
        this.items = itmes;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    @Override
    public String toString() {
        return "SportItem{" +
                "interval=" + interval +
                ", items='" + items + '\'' +
                '}';
    }
}
