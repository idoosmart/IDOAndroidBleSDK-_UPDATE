package test.com.ido.notice;

import static android.Manifest.permission.ANSWER_PHONE_CALLS;
import static android.content.Context.TELEPHONY_SERVICE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import test.com.ido.APP;
import test.com.ido.utils.PermissionUtil;

/**
 * @author: sslong
 * @package: com.veryfit2hr.second.common.utils
 * @description: ${TODO}{一句话描述该类的作用}
 * @date: 2016/12/15 17:38
 */
public class PhoneUtil {

    public static String TAG = PhoneUtil.class.getSimpleName();

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
    public static List<PhoneDto> getPhoneSorted() {
        List<PhoneDto> phoneDtos = new ArrayList<>();
        if (PermissionUtil.checkSelfPermission(APP.getAppContext().getApplicationContext(), Manifest.permission.READ_CONTACTS)) {
            ContentResolver cr = APP.getAppContext().getApplicationContext().getContentResolver();
            Cursor cursor = cr.query(phoneUri, new String[]{NUM, NAME}, null, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_LAST_UPDATED_TIMESTAMP + " DESC");
            while (cursor.moveToNext()) {
                PhoneDto phoneDto = new PhoneDto(cursor.getString(cursor.getColumnIndex(NAME)),
                        cursor.getString(cursor.getColumnIndex(NUM)), false, false);
                //过滤重复
                if (!phoneDtos.contains(phoneDto)) {
                    phoneDtos.add(phoneDto);
                }
            }
        }
        return phoneDtos;
    }
}
