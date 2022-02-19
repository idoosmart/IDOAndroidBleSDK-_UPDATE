package test.com.ido.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Set;

public class CommonPreferences {
	protected SharedPreferences mSharePre;

    public void init(Context context, String name) {
        Log.i("CommonPreferences", "context  = :" + context + "   spName : " + name);
        this.mSharePre = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public boolean remove(String key) {
        return mSharePre.edit().remove(key).commit();
    }

    protected float getValue(String key, float defValue) {
        return this.mSharePre.getFloat(key, defValue);
    }

    protected int getValue(String key, int defValue) {
        return this.mSharePre.getInt(key, defValue);
    }

    protected long getValue(String key, long defValue) {
        return this.mSharePre.getLong(key, defValue);
    }

    protected String getValue(String key, String defValue) {
        return this.mSharePre.getString(key, defValue);
    }

    protected Set<String> getValue(String key, Set<String> defValue) {
        return this.mSharePre.getStringSet(key, defValue);
    }

    protected boolean getValue(String key, boolean defValue) {
        return this.mSharePre.getBoolean(key, defValue);
    }

    protected void setValue(String key, float defValue) {
        this.mSharePre.edit().putFloat(key, defValue).commit();
    }

    protected void setValue(String key, int defValue) {
        this.mSharePre.edit().putInt(key, defValue).commit();
    }

    protected void setValue(String key, long defValue) {
        this.mSharePre.edit().putLong(key, defValue).commit();
    }

    protected void setValue(String key, String defValue) {
        this.mSharePre.edit().putString(key, defValue).commit();
    }

    protected void setValue(String key, Set<String> defValue) {
        this.mSharePre.edit().putStringSet(key, defValue).commit();
    }

    protected void setValue(String key, boolean defValue) {
        this.mSharePre.edit().putBoolean(key, defValue).commit();
    }

    protected void clear(){
        this.mSharePre.edit().clear().commit();
    }
}