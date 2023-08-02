package test.com.ido.runplan.utils;

import com.ido.ble.logs.LogTool;

public class SportLogHelper {
    public static void saveSportLog(String tag,String message){
        LogTool.e(tag,message);
    }
}
