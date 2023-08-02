package test.com.ido.runplan.utils;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.ido.ble.logs.LogTool;

import java.util.List;

import test.com.ido.exgdata.demo.DaoSession;
import test.com.ido.runplan.SportHealth;
import test.com.ido.runplan.SportHealthDao;
import test.com.ido.runplan.db.GreenDaoManager;

public class GreenDaoUtil {
    private static final String TAG = "GreenDaoUtil";
    public static DaoSession getDaoSession() {
        return GreenDaoManager.getInstance().getDaoSession();
    }
    /**
     * 获取活动数据
     *
     * @param dateTime 活动时间，格式yyyy-MM-dd HH:mm:ss
     */
    public static SportHealth getActivityData(long userId, String dateTime) {
        if (TextUtils.isEmpty(dateTime)) return null;
        long count = getDaoSession().getSportHealthDao().queryBuilder()
                .where(SportHealthDao.Properties.UserId.eq(userId),
                        SportHealthDao.Properties.DateTime.eq(dateTime))
                .count();
        if (count == 0) return null;
        return getDaoSession().getSportHealthDao().queryBuilder()
                .where(SportHealthDao.Properties.UserId.eq(userId),
                        SportHealthDao.Properties.DateTime.eq(dateTime))
                .limit(1).unique();
    }

    public static List<SportHealth> getAllActivityData(long userId){
        return getDaoSession().getSportHealthDao().queryBuilder()
                .where(SportHealthDao.Properties.UserId.eq(userId)).list();
    }


    /**
     * 保存活动数据
     *
     * @param health
     */
    public static void saveActivityData(@NonNull SportHealth health) {
        //health.setUserId(getUserId());
        LogTool.e(TAG, "saveActivityData: "
                + health.toString() + "时间戳" + health.getTimestamp());
        SportHealth activityData = getActivityData(health.getUserId(), health.getDateTime());
        if (activityData == null) {
            //手环或手表单独发起的运动
            //health.setSourceType(2);
            if (health.getSourceType() == 0) {
                health.setSourceType(SportHealth.DATA_FROM_DEVICE);
            }
            LogTool.e(TAG, "saveActivityData: null" + health.getStartTime());
            getDaoSession().insert(health);
        } else {
            //设置来源是1、手机单独发起2、还是设备单独发起 3、手机连接手环发起
            health.setSourceType(activityData.getSourceType());
            health.setActivityId(activityData.getActivityId());
            //设置目标（目标不覆盖，现在app和手环的目标不一样所以同步的时候取本地的目标）
            health.setTargetType(activityData.getTargetType());
            health.setTargetValue(activityData.getTargetValue());
            //开始时间不变
            health.setStartTime(activityData.getStartTime());
            if (!TextUtils.isEmpty(activityData.getIcon())) {
                health.setIcon(activityData.getIcon());
            }
            health.setAltitude_item(activityData.getAltitude_item());
            LogTool.e(TAG, "saveActivityData: 不等于null" + health.getStartTime());
            getDaoSession().update(health);
        }
    }
}
