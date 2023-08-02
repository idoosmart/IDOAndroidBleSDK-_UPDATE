package test.com.ido.runplan.db;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


import com.ido.ble.logs.LogTool;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.EncryptedDatabase;
import org.greenrobot.greendao.query.QueryBuilder;

import java.io.File;

import test.com.ido.exgdata.demo.DaoMaster;
import test.com.ido.exgdata.demo.DaoSession;
import test.com.ido.utils.DatabaseUpgradeUtil;


/**
 * @Author: ym
 * @ClassName: GreenDaoManager
 * @Description: 数据库管理类
 * @Package: com.ido.life.database
 * @CreateDate: 2020/4/30 0030 12:00
 */
public class GreenDaoManager {
    public static final String DB_NAME = "demo_fit.db";//数据库名称
    @SuppressLint("StaticFieldLeak")
    private final static GreenDaoManager mGreenDaoManager = new GreenDaoManager();//多线程访问
    private DaoMaster.DevOpenHelper mHelper;
    private static DaoMaster mDaoMaster;
    private static DaoSession mDaoSession;
    private Context context;

    /**
     * 使用单例模式获得操作数据库的对象
     */
    public static GreenDaoManager getInstance() {
        return mGreenDaoManager;
    }

    /**
     * 初始化Context对象
     */
    public void init(Context context) {
        this.context = context;
    }

    /**
     * 判断数据库是否存在，如果不存在则创建
     */
    private DaoMaster getDaoMaster() {
        if (null == mDaoMaster) {
            mHelper = new DaoMaster.DevOpenHelper(context, DB_NAME, null) {
                @Override
                public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                    if (!processDataBaseUpgrade(new StandardDatabaseWraper(db), oldVersion, newVersion)) {
                        super.onUpgrade(db, oldVersion, newVersion);
                    }
                }

                @Override
                public void onUpgrade(Database db, int oldVersion, int newVersion) {
                    if (db instanceof EncryptedDatabase &&
                            processDataBaseUpgrade(new StandardDatabaseWraper((EncryptedDatabase) db), oldVersion, newVersion)) {
                    } else {
                        super.onUpgrade(db, oldVersion, newVersion);
                    }
                }
            };
            mDaoMaster = new DaoMaster(mHelper.getWritableDb());
        }
        return mDaoMaster;
    }

    /**
     * 处理数据库升级
     */
    private boolean processDataBaseUpgrade(StandardDatabaseWraper databaseWrapper, int oldVersion, int newVersion) {
        LogTool.e("DB",
                "开始处理数据库升级oldVersion=" + oldVersion + ",newVersion=" + newVersion);
        boolean success = false;
        try {
            success = DatabaseUpgradeUtil.upgradeDatabase(databaseWrapper, oldVersion, newVersion);
        } catch (Exception e) {
            e.printStackTrace();
            LogTool.e("DB",
                    "数据库升级失败：" + e);
        }
        return success;
    }



    /**
     * 完成对数据库的增删查找
     */
    public DaoSession getDaoSession() {
        if (null == mDaoSession) {
            synchronized (GreenDaoManager.this) {
                if (null == mDaoMaster) {
                    synchronized (GreenDaoManager.this) {
                        mDaoMaster = getDaoMaster();
                    }
                }
                mDaoSession = mDaoMaster.newSession();
            }
        }
        return mDaoSession;
    }

    /**
     * 设置debug模式开启或关闭，默认关闭
     */
    public void setDebug(boolean flag) {
        QueryBuilder.LOG_SQL = flag;
        QueryBuilder.LOG_VALUES = flag;
    }

    /**
     * 关闭数据库
     */
    public void closeDataBase() {
        closeHelper();
        closeDaoSession();
    }

    public void closeDaoSession() {
        if (null != mDaoSession) {
            mDaoSession.clear();
            mDaoSession = null;
        }
    }

    public void closeHelper() {
        if (mHelper != null) {
            mHelper.close();
            mHelper = null;
        }
    }
}
