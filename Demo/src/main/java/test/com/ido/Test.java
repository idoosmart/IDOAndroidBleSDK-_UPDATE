package test.com.ido;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.RequiresApi;

import com.ido.ble.bluetooth.DeviceConnectService;

/**
 * @author: zhouzj
 * @date: 2017/11/17 14:51
 */

public class Test {
    private static String sdfs;

    public void ontest(){
        sdfs = "";
        int i = 0;

        String ttt = "";
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void startService(Context context){
        final Intent intent = new Intent(context, TestService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

}
