package test.com.ido.runplan.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.greendao.database.EncryptedDatabase;

import java.util.List;

public interface StandardDatabaseInterface {
	
	/**
	 *获取数据库管理对象
	 */
	SQLiteDatabase getSQLiteDatabase();
	
	/**
	 *获取数据库管理对象
	 */
	EncryptedDatabase getEncryptedDatabase();
	
	/**
	 *查询表中所有数据
	 */
	Cursor query(String tableName);

	/**
	 *查询表中的数据
	 */
	Cursor query(String table, String[] columns, String selection,
				 String[] selectionArgs, String groupBy, String having,
				 String orderBy);
	
	/**
	 *删除表
	 */
	boolean dropTable(String tableName,boolean ifExists);
	
	/**
	 * 执行Sql语句
	 */
	boolean execSql(String sql);
	
	/**
	 *开始交易
	 */
	void beginTransaction();
	
	/**
	 *设置交易成功
	 */
	void setTransactionSuccessful();
	
	/**
	 *结束交易
	 */
	void endTransaction();
	
	/**
	 *向表中添加数据
	 */
	long insert(String table, String nullColumnHack, ContentValues values);

	/**
	 *向表中添加数据
	 */
	void insert(String table, String nullColumnHack, List<ContentValues> valuesList);
	
	/**
	 *删除数据
	 */
	int delete(String table, String whereClause, String[] whereArgs);
}
