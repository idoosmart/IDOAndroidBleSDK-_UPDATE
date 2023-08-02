package test.com.ido.runplan.utils;
import android.os.Handler;
import android.os.Looper;

public class ThreadUtil {
//    private static Handler handler=new Handler(Looper.getMainLooper());
    public static void runOnMainThread(Runnable runnable) {
        Handler handler=new Handler(Looper.getMainLooper());
        if(!(Thread.currentThread() == Looper.getMainLooper().getThread())){
            handler.post(runnable);
        }else{
          runnable.run();
        }
    }
    public static void delayTask(Runnable task, int delay){
        Handler handler=new Handler(Looper.getMainLooper());
        handler.postDelayed(task,delay);
    }
}