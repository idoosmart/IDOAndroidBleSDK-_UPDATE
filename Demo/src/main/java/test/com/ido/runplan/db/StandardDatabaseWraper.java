package test.com.ido.runplan.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import org.greenrobot.greendao.database.EncryptedDatabase;

import java.util.List;

/**
 * 用于数据库升级
 */
public class StandardDatabaseWraper implements StandardDatabaseInterface {
    private EncryptedDatabase mEncryptedDatabase;
    private SQLiteDatabase mSQLiteDatabase;

    public StandardDatabaseWraper(EncryptedDatabase encryptedDatabase) {
        mEncryptedDatabase = encryptedDatabase;
    }

    public StandardDatabaseWraper(SQLiteDatabase SQLiteDatabase) {
        mSQLiteDatabase = SQLiteDatabase;
    }

    /**
     * 获取数据库管理对象
     */
    public SQLiteDatabase getSQLiteDatabase() {
        return mSQLiteDatabase;
    }

    /**
     * 获取数据库管理对象
     */
    public EncryptedDatabase getEncryptedDatabase() {
        return mEncryptedDatabase;
    }

    /**
     * 查询表中所有数据
     */
    @Override
    public Cursor query(String tableName) {
        if (TextUtils.isEmpty(tableName)) return null;
        if (mEncryptedDatabase != null) {
            return mEncryptedDatabase.getSQLiteDatabase().query(tableName, null, null, null, null, null, null);
        }
        if (mSQLiteDatabase != null) {
            return mSQLiteDatabase.query(tableName, null, null, null, null, null, null);
        }
        return null;
    }

    @Override
    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        if (TextUtils.isEmpty(table)) return null;
        if (mEncryptedDatabase != null) {
            return mEncryptedDatabase.getSQLiteDatabase().query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
        }
        if (mSQLiteDatabase != null) {
            return mSQLiteDatabase.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
        }
        return null;
    }

    /**
     * 删除表
     */
    @Override
    public boolean dropTable(String tableName, boolean ifExists) {
        if (TextUtils.isEmpty(tableName)) return false;
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"" + tableName + "\"";
        if (mEncryptedDatabase != null) {
            mEncryptedDatabase.getSQLiteDatabase().execSQL(sql);
        }
        if (mSQLiteDatabase != null) {
            mSQLiteDatabase.execSQL(sql);
        }
        return true;
    }

    /**
     * 执行Sql语句
     */
    @Override
    public boolean execSql(String sql) {
        if (TextUtils.isEmpty(sql)) return false;
        if (mEncryptedDatabase != null) {
            mEncryptedDatabase.getSQLiteDatabase().execSQL(sql);
        }
        if (mSQLiteDatabase != null) {
            mSQLiteDatabase.execSQL(sql);
        }
        return true;
    }

    /**
     * 开始交易
     */
    @Override
    public void beginTransaction() {
        if (mEncryptedDatabase != null) {
            mEncryptedDatabase.beginTransaction();
        }
        if (mSQLiteDatabase != null) {
            mSQLiteDatabase.beginTransaction();
        }
    }

    /**
     * 设置交易成功
     */
    @Override
    public void setTransactionSuccessful() {
        if (mEncryptedDatabase != null) {
            mEncryptedDatabase.setTransactionSuccessful();
        }
        if (mSQLiteDatabase != null) {
            mSQLiteDatabase.setTransactionSuccessful();
        }
    }

    /**
     * 结束交易
     */
    @Override
    public void endTransaction() {
        if (mEncryptedDatabase != null) {
            mEncryptedDatabase.endTransaction();
        }
        if (mSQLiteDatabase != null) {
            mSQLiteDatabase.endTransaction();
        }
    }

    /**
     * 向表中添加数据
     */
    @Override
    public long insert(String table, String nullColumnHack, ContentValues values) {
        if (mEncryptedDatabase != null) {
            return mEncryptedDatabase.getSQLiteDatabase().insert(table, nullColumnHack, values);
        }
        if (mSQLiteDatabase != null) {
            return mSQLiteDatabase.insert(table, nullColumnHack, values);
        }
        return -1;
    }

    @Override
    public void insert(String table, String nullColumnHack, List<ContentValues> valuesList) {
        if (valuesList == null || valuesList.size() == 0) return;
        for (ContentValues values : valuesList) {
            if (mEncryptedDatabase != null) {
                mEncryptedDatabase.getSQLiteDatabase().insert(table, nullColumnHack, values);
            }
            if (mSQLiteDatabase != null) {
                mSQLiteDatabase.insert(table, nullColumnHack, values);
            }
        }
    }

    /**
     * 删除数据
     */
    @Override
    public int delete(String table, String whereClause, String[] whereArgs) {
        if (TextUtils.isEmpty(table)) return 0;
        if (mEncryptedDatabase != null) {
            return mEncryptedDatabase.getSQLiteDatabase().delete(table, whereClause, whereArgs);
        }
        if (mSQLiteDatabase != null) {
            return mSQLiteDatabase.delete(table, whereClause, whereArgs);
        }
        return 0;
    }
}
