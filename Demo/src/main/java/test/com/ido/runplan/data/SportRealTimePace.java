package test.com.ido.runplan.data;

/**
 * @author xyb
 * @date 2021-09-14 15:53
 * @description: 实时配速的值
 */
public class SportRealTimePace {

    private String realTimeSpace;

    public SportRealTimePace() {

    }

    public SportRealTimePace(String realTimeSpace) {
        this.realTimeSpace = realTimeSpace;
    }

    public String getRealTimeSpace() {
        return realTimeSpace;
    }

    public void setRealTimeSpace(String realTimeSpace) {
        this.realTimeSpace = realTimeSpace;
    }

    @Override
    public String toString() {
        return "SportRealTimePace{" +
                "realTimeSpace='" + realTimeSpace + '\'' +
                '}';
    }
}
