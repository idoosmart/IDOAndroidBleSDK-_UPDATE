package test.com.ido.runplan.data;

import test.com.ido.runplan.SportHealth;
import test.com.ido.runplan.utils.GreenDaoUtil;
import test.com.ido.utils.DateUtil;

public class SportHealthDataManager {
    /**
     *
     *  保存用户的运动数据
     * @param sportHealth
     */
    public static void addDataFromApp(SportHealth sportHealth) {
        //开始时间，格式yyyy-MM-dd HH:mm:ss
        sportHealth.setTimestamp(DateUtil.parse(sportHealth.getEndTime(),DateUtil.DATE_FORMAT_YMDHms).getTime());
        GreenDaoUtil.saveActivityData(sportHealth);
    }
}
