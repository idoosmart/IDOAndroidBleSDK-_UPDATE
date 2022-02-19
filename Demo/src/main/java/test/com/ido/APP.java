package test.com.ido;


import android.app.Application;
import android.content.Context;
import android.os.StrictMode;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.facebook.stetho.Stetho;
import com.ido.ble.BLEManager;
import com.tencent.bugly.crashreport.CrashReport;

import test.com.ido.crash.CrashHandler;


/**
 * Created by Zhouzj on 2017/9/29.
 * 
 */

public class APP extends Application {
    private static Application application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        BLEManager.onApplicationCreate(this);

        CrashHandler.getInstance().init(this);

        // 添加facebookc插件
        Stetho.initializeWithDefaults(this);

        //百度地图
        SDKInitializer.initialize(this);
        SDKInitializer.setCoordType(CoordType.BD09LL);

        CrashReport.initCrashReport(getApplicationContext(), "1ddf6b5084", true);
        if (!BuildConfig.DEBUG) {
            CrashReport.setUserSceneTag(getApplicationContext(), 178865);
        }

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();


    }

    public static Context getAppContext(){
        return application.getApplicationContext();
    }

}
