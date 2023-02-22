package test.com.ido.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lyw.
 *
 * @author: lyw
 * @package: com.id.app.DownloadManager.lib.utils
 * @description: ${TODO}{ 权限处理类}
 * @date: 2018/9/21 0021
 */
public class PermissionUtil {
    /**
     * 存储权限
     */
    public static final int CODE_REQUEST_STORAGE = 100;

    /**
     * 相机权限
     */
    public static final int CODE_REQUEST_CAMERA = 200;

    /**
     * 定位权限
     */
    public static final int CODE_REQUEST_LOCATION = 300;

    /**
     * 蓝牙权限
     */
    public static final int CODE_REQUEST_BLE = 400;

    /**
     * 通知权限
     */
    public static final int CODE_REQUEST_NOTIFICATION = 500;

    /**
     * 短信权限
     */
    public static final int CODE_REQUEST_SMS = 501;

    /**
     * 电话权限
     *
     * @return
     */
    public static final int CODE_REQUEST_PHONE = 502;

    /**
     * 震动权限
     */
    public static final int CODE_REQUEST_VIBRATE = 503;

    /**
     * 发短信权限
     */
    public static final int CODE_REQUEST_SEND_SMS=504;

    /**
     * 短信权限
     */
    public static final int CODE_REQUEST_READ_SEND_SMS=505;
    /**
     * 蓝牙扫描
     */
    public static final int CODE_REQUEST_BLE_SCAN = 506;

    /**
     *请求来电提醒权限
     */
    public static final int CODE_CALL_REMINDER=601;


    public static boolean isOverMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public void setRequestResult(RequestResult requestResult) {
        this.mRequestResult = requestResult;
    }

    private RequestResult mRequestResult;

    /**
     * 检查是否有权限
     *
     * @param permission
     * @return
     */
    @TargetApi(value = Build.VERSION_CODES.M)
    public static List<String> findDeniedPermissions(Context context, String... permission) {
        if (context == null) return null;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return new ArrayList<>();
        }
        List<String> denyPermissions = new ArrayList<>();
        for (String value : permission) {
            if (ContextCompat.checkSelfPermission(context, value) != PackageManager.PERMISSION_GRANTED) {
                denyPermissions.add(value);
            }
        }
        return denyPermissions;
    }

    /**
     * 检测权限，如果返回true,有权限 false 无权限
     *
     * @param permission 权限
     * @return true, 有权限 false 无权限
     */
    public static boolean checkSelfPermission(Context context, String... permission) {
        List<String> list = findDeniedPermissions(context, permission);
        return list == null || list.size() == 0;
    }

    /**
     * 申请权限
     *
     * @param object
     * @param requestCode
     * @param permissions
     */
    @TargetApi(value = Build.VERSION_CODES.M)
    public static void requestPermissions(Object object, int requestCode, String... permissions) {
        if (!(object instanceof Activity) && !(object instanceof Fragment)) return;
        if (!isOverMarshmallow()) {
            return;
        }
        List<String> deniedPermissions = null;
        if (object instanceof Activity) {
            Activity activity = (Activity) object;
            deniedPermissions = findDeniedPermissions(activity, permissions);
            if (deniedPermissions != null && deniedPermissions.size() > 0) {
                activity.requestPermissions(deniedPermissions.toArray(new String[deniedPermissions.size()]), requestCode);
            }
        } else if (object instanceof Fragment) {
            Fragment fragment = (Fragment) object;
            deniedPermissions = findDeniedPermissions(fragment.getContext(), permissions);
            if (deniedPermissions != null && deniedPermissions.size() > 0) {
                fragment.requestPermissions(deniedPermissions.toArray(new String[deniedPermissions.size()]), requestCode);
            }
        }
    }

    public static boolean grantedPermission(int[] grantResults) {
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (mRequestResult == null) return;
        List<String> deniedPermissions = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(permissions[i]);
            }
        }
        if (deniedPermissions.size() > 0) {
            mRequestResult.requestPermissionsFail(requestCode);
        } else {
            mRequestResult.requestPermissionsSuccess(requestCode);
        }
    }

    /**
     * 存储权限
     *
     * @return
     */
    public static String[] getStoragePermission() {
        return new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
    }

    /**
     * 相机权限
     *
     * @return
     */
    public static String[] getCameraPermission() {
        return new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
    }

    /**
     * 电话权限
     *
     * @return
     */
    public static String[] getPhonePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return new String[]{
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.ANSWER_PHONE_CALLS//9.0之后接听电话需要这个权限
            };
        } else {
            return new String[]{
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.READ_CALL_LOG
            };
        }
    }

    /**
     * 电话权限
     *
     * @return
     */
    public static String[] getOnlyPhonePermission() {
        return new String[]{
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_PHONE_STATE,
        };
    }

    /**
     * 读取联系人权限
     *
     * @return
     */
    public static String[] getOnlyContactPermission() {
        return new String[]{
                Manifest.permission.READ_CONTACTS,
        };
    }

    /**
     * 短信权限
     *
     * @return
     */
    public static String[] getSmsPermission() {
        return new String[]{
                Manifest.permission.SEND_SMS,
                Manifest.permission.RECEIVE_SMS
        };
    }

    /**
     * 读取短信权限
     *
     * @return
     */
    public static String[] getReadSmsPermission() {
        return new String[]{
                Manifest.permission.READ_SMS
        };
    }

    /**
     * 获取手机号
     * 发短信权限
     * @return
     */
    public static String[] getSendSmsPermission(){
        return new String[]{
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.SEND_SMS};
    }

    /**
     * 相机权限
     *
     * @return
     */
    public static String[] getOnlyCameraPermission() {
        return new String[]{
                Manifest.permission.CAMERA
        };
    }


    /**
     * 获取定位的权限
     */
    public static String[] getLocationPermission() {
        return new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
        };

    }

    /**
     * 后台定位权限
     *
     * @return
     */
    public static String[] getLocationBackGroundPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            };
        } else {
            return new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
            };
        }
    }

    /**
     * 蓝牙权限
     *
     * @return
     */
    public static String[] getBluetoothPermission() {
        return new String[]{
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };
    }

    public static String[] getBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return new String[]{
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
            };
        }
        return new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION
        };
    }

    /**
     * 蓝牙权限
     *
     * @return
     */
    public static String[] getOnlyBluetoothPermission() {
        return new String[]{
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
        };
    }

    /**
     * 震动权限
     */
    public static String[] getVibratePermission() {
        return new String[]{Manifest.permission.VIBRATE};
    }

    /**
     * 检查是否权限是否被用户拒绝并且选择了不再弹出
     */
    public static boolean isDeniedByNoAsk(Activity activity, String... permissions) {
        if (activity == null || permissions == null || permissions.length == 0) return false;
        for (String permission : permissions) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                return true;
            }
        }
        return false;
    }

    public interface RequestResult {
        void requestPermissionsSuccess(int requestCode);

        void requestPermissionsFail(int requestCode);
    }
}
