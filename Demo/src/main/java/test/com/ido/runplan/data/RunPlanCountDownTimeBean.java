package test.com.ido.runplan.data;

public class RunPlanCountDownTimeBean {
    ////运动倒计时（注：递减）， 或课程结束后计时（（注：递增））   action_type = 1—5时递减  ， action_type = 6递增
    //    uint8_t count_hour;
    //    uint8_t count_minute;
    //    uint8_t count_second;
    int count_hour;
    int count_minute;
    int count_second;

    public int getKm_speed() {
        return km_speed;
    }

    public void setKm_speed(int km_speed) {
        this.km_speed = km_speed;
    }

    int km_speed;
    public int getCount_hour() {
        return count_hour;
    }

    public void setCount_hour(int count_hour) {
        this.count_hour = count_hour;
    }

    public int getCount_minute() {
        return count_minute;
    }

    public void setCount_minute(int count_minute) {
        this.count_minute = count_minute;
    }

    public int getCount_second() {
        return count_second;
    }

    public void setCount_second(int count_second) {
        this.count_second = count_second;
    }
}
