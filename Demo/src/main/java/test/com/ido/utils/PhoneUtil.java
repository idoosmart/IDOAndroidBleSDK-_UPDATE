package test.com.ido.utils;

import static android.Manifest.permission.ANSWER_PHONE_CALLS;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import test.com.ido.APP;
import test.com.ido.model.ContactBean;

/**
 * @author tianwei
 * @date 2023/2/22
 * @time 14:17
 * 用途:
 */
public class PhoneUtil {
    //联系人提供者的uri
    // 号码
    public final static String NUM = ContactsContract.CommonDataKinds.Phone.NUMBER;
    // 联系人姓名
    public final static String NAME = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;

    private static Uri phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

    /**
     * 获取联系人
     *
     * @return
     */
    public static List<ContactBean> getPhoneSorted() {
        List<ContactBean> phoneDtos = new ArrayList<>();
        if (PermissionUtil.checkSelfPermission(APP.getAppContext(), Manifest.permission.READ_CONTACTS)) {
            ContentResolver cr = APP.getAppContext().getContentResolver();
            Cursor cursor = cr.query(phoneUri, new String[]{NUM, NAME}, null, null, ContactsContract.CommonDataKinds.Phone.CONTACT_LAST_UPDATED_TIMESTAMP + " DESC");
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(NAME));
                String phoneNum = cursor.getString(cursor.getColumnIndex(NUM));
                ContactBean phoneDto = new ContactBean(name, phoneNum);
                if (TextUtils.isEmpty(phoneNum)) continue;
                //过滤重复
                if (!phoneDtos.contains(phoneDto)) {
                    phoneDtos.add(phoneDto);
                }
            }
        }
        return phoneDtos;
    }

    /**
     * 挂断电话
     *
     * @param context
     */
    public static void endCall(Context context) {
        try {
            Object telephonyObject = getTelephonyObject(context);
            if (null != telephonyObject) {
                Class telephonyClass = telephonyObject.getClass();
                Method endCallMethod = telephonyClass.getMethod("endCall");
                endCallMethod.setAccessible(true);
                endCallMethod.invoke(telephonyObject);
            } else {
                endCall2(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
            endCall2(context);
        }
    }

    public static void endCall2(Context context) {
        if (context==null)return;
        try {
            if (Build.VERSION.SDK_INT >= 28) {//28
                TelecomManager manager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
                if (manager != null && PermissionUtil.checkSelfPermission(context,ANSWER_PHONE_CALLS)) {
                    // complains required API app requires API level 28 (current min is 21).
                    manager.endCall();
                    return;

                }
            }
        } catch (Exception e) {
        }
        endCall3(context);

    }
    /**
     * 接听电话
     *
     * @param context
     */
    public static void answerRingingCall(Context context) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {  //2.3或2.3以上系统
//            answerRingingCallWithBroadcast(context);
//        } else {
        answerRingingCallWithReflect(context);
//        }
    }
    /**
     * 通过反射调用的方法，接听电话，该方法只在android 2.3之前的系统上有效。
     * Neither user 11164 nor current process has android.permission.MODIFY_PHONE_STATE.
     *
     * @param context
     */
    public static void answerRingingCallWithReflect(Context context) {
        try {
            Object telephonyObject = getTelephonyObject(context);
            if (null != telephonyObject) {
                Class telephonyClass = telephonyObject.getClass();
                Method endCallMethod = telephonyClass.getMethod("answerRingingCall");
                endCallMethod.setAccessible(true);
                endCallMethod.invoke(telephonyObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static void endCall3(Context context) {
        Object o = getTelephonyObject(context);
        Class clazz = null;
        if (null != o) {
            clazz = o.getClass();
        }
        if (clazz == null) {
            return;
        }
        Method method;
        try {
            method = clazz.getMethod("endCallForSubscriber", int.class);
            method.setAccessible(true);

            for (int i = 0; i < 20; i++) {
                boolean endCall = (boolean) method.invoke(o, i);
                if (endCall) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                method = clazz.getMethod("endCallForSubscriber", long.class);
                method.setAccessible(true);

                for (int i = 0; i < 20; i++) {
                    boolean endCall = (boolean) method.invoke(o, i);
                    if (endCall) {
                        break;
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }

    }

    public static Object getTelephonyObject(Context context) {
        try {
            // 获取 TelephonyManager 实例
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            // 获取 getITelephony 方法
            Class<?> telephonyManagerClass = Class.forName(telephonyManager.getClass().getName());
            Method getITelephonyMethod = telephonyManagerClass.getDeclaredMethod("getITelephony");

            // 设置访问权限
            getITelephonyMethod.setAccessible(true);

            // 调用 getITelephony 方法，获取 ITelephony 对象
            Object iTelephonyObject = getITelephonyMethod.invoke(telephonyManager);

            return iTelephonyObject;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
//    private static Object getTelephonyObject(Context context) {
//        Object telephonyObject = null;
//        try {
//            // 初始化iTelephony
//            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
//            // Will be used to invoke hidden methods with reflection
//            // Get the current object implementing ITelephony interface
//            Class telManager = telephonyManager.getClass();
//            Method getITelephony =telManager.getDeclaredMethod("getITelephony",String.class, Class[].class);
//            getITelephony.setAccessible(true);
//            telephonyObject = getITelephony.invoke(telephonyManager);
//        } catch (SecurityException e) {
//            e.printStackTrace();
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        }
//        return telephonyObject;
//    }
}
