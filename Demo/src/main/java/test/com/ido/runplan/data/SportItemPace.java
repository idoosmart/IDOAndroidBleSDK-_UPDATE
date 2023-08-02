package test.com.ido.runplan.data;

/**
 * Copyright (c) 2019 深圳市爱都科技有限公司. All rights reserved.
 *
 * @Author: xyb
 * @CreateDate: 2020/9/10 12:14
 * @description
 */
public class SportItemPace {
    /**
     * 公里配速
     */
    private String metricItems;
    /**
     * 英里
     */
    private String britishItems;


    public SportItemPace() {

    }

    public SportItemPace(String metricItems, String britishItems) {
        this.metricItems = metricItems;
        this.britishItems = britishItems;
    }

    public String getMetricItems() {
        return metricItems;
    }

    public void setMetricItems(String metricItems) {
        this.metricItems = metricItems;
    }

    public String getBritishItems() {
        return britishItems;
    }

    public void setBritishItems(String britishItems) {
        this.britishItems = britishItems;
    }

    @Override
    public String toString() {
        return "SportItemPace{" +
                "metricItems='" + metricItems + '\'' +
                ", britishItems='" + britishItems + '\'' +
                '}';
    }
}
