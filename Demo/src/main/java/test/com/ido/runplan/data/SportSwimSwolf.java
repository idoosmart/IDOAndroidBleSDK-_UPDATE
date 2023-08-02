package test.com.ido.runplan.data;

/**
 * Copyright (c) 2019 深圳市爱都科技有限公司. All rights reserved.
 * @Author: xyb
 * @CreateDate: 2020/9/9 14:14
 * @description
 */
public class SportSwimSwolf {
    /**
     * 间隔
     */
    private int interval;
    /**
     * 游泳图表数据
     */
    private String items;




    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    @Override
    public String toString() {
        return "SportSwimSwolf{" +
                "interval=" + interval +
                ", items=" + items +
                '}';
    }



}
