package test.com.ido.utils;



import com.ido.ble.logs.LogTool;

import java.util.HashMap;
import java.util.Map;

import test.com.ido.runplan.db.StandardDatabaseWraper;

/**
 * 数据库升级策略,只需要填写上一个版本的数据到这个版本的数据处理逻辑
 */
public class DatabaseUpgradeUtil {
    private static Map<String, UpgrateDataBase> mUpgradeMap = new HashMap<>();

    /**
     * 如果数据库升级请在这里注册
     */
    static {

    }

    public static boolean upgradeDatabase(StandardDatabaseWraper db, int oldVersion, int newVersion) {
        if (db == null) return false;
        if (oldVersion >= newVersion) return true;
        boolean result = true;
        while (oldVersion < newVersion) {
            String dataKey = getDatabaseUpgradeKey(oldVersion, ++oldVersion);
            if (mUpgradeMap.containsKey(dataKey)) {
                LogTool.e("DB",
                        "数据库开始升级:" + dataKey);
                result = mUpgradeMap.get(dataKey).processUpgrate(db);
                if (!result) {
                    LogTool.e("DB",
                            "数据库升级失败:" + dataKey);
                    break;
                } else {
                    LogTool.e("DB",
                            "数据库升级成功:" + dataKey);
                }
            }
        }
        return result;
    }

    /**
     * 获取数据库升级Key
     */
    private static String getDatabaseUpgradeKey(int oldVersion, int newVersion) {
        return oldVersion + "-" + newVersion;
    }

    public interface UpgrateDataBase {
        boolean processUpgrate(StandardDatabaseWraper db);
    }
}
