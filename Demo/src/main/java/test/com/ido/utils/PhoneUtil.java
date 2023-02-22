package test.com.ido.utils;

import android.Manifest;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;

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
}
