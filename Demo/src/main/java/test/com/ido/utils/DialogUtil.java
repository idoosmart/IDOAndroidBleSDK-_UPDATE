package test.com.ido.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import test.com.ido.R;


/**
 * @author: sslong
 * @package: com.veryfit.
 * @description: ${TODO}{一句话描述该类的作用}
 * @date: 2016/5/18 15:58
 */
public class DialogUtil {
    public static final int CAMERA = 1;
    public static final int PHOTOZOOM = 2;
    public static final int PHOTORESOULT = 3;
    /*拍照*/
    public static String photoTemp = "/temp.png";
    public static String photoPath = "/avatar.jpg";
    private static Toast toast;
    private static AlertDialog waitDialog;

    public static void showToast(Context context, String msg) {

        if (context == null) {
            return;
        }

        if (toast == null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
        } else {
            toast.setText(msg);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.show();
    }

    public static void showToast(Context context, int res) {
        if (context == null) {
            return;
        }
        String msg = context.getString(res);
        showToast(context, msg);
    }

    public static void showWaitDialog(Activity activity, String msg) {

        waitDialog = new ProgressDialog(activity);
        waitDialog.setMessage(msg);
        waitDialog.setCancelable(false);
        waitDialog.show();

    }

    public static void updateWaitDialog(String msg) {
        if (waitDialog != null && waitDialog.isShowing()) {
            waitDialog.setMessage(msg);
        }
    }


    public static void closeAlertDialog() {
        if (waitDialog != null && waitDialog.isShowing()) {
            waitDialog.dismiss();
            waitDialog = null;
        }
    }



    /**
     * @param context
     * @return true activity not distory
     */
    public static boolean checkActivityisDestroyed(Context context) {
//        boolean isActivity=context instanceof Activity;
        if (context == null) return false;
        if (!(context instanceof Activity)) {
//            throw  new IllegalArgumentException("context is not activity");
            return false;
        }
        Activity activity = (Activity) context;

        if (activity.isDestroyed()) {
//            throw  new IllegalArgumentException("activity is Destroyed");
            return false;
        }
        return true;
    }



    public interface OnWheelSelectorListener {
        public void onWheelSelect(Object obj);
    }


    public interface OnWheelSelectorBackListener {
        public void onWheelSelect(Object obj, Boolean b);
    }

    public interface OnWheelExitListener {
        public void OnWheelExit();

        public void OnWheelGoOn();
    }

    public interface OnWheelTimeOutListener {
        public void OnYes();

        public void OnNo();
    }

    public interface OnWheelChangeingListener {
        public void OnYes();

        public void OnNo();
    }

    public interface OnWheelDisConnectListener {
        public void OnYes();

        public void OnNo();
    }

    public interface OnUnitSetSelectorListener {
        public void onWheelSelect(Object obj1, Object obj2);
    }

    public interface OnUnBoundDeviceListener {
        public void onUnBoundDevice();
    }

    public interface OnNormalDialogListener {
        public void OnYes(int[] auth_code);

        public void OnNo();
    }

    /**
     * 常见的对话框
     *
     * @param activity
     * @param titleStr
     * @param leftStr
     * @param listener
     */
    public static Dialog showNormalDialog(final Activity activity, String titleStr, String leftStr, String rightStr, final OnNormalDialogListener listener) {
        final Dialog dialog = new Dialog(activity, R.style.dialog);
        dialog.setContentView(R.layout.dialog_normal);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().width = (int) (activity.getWindowManager().getDefaultDisplay().getWidth() * 0.9f);
        TextView title = (TextView) dialog.findViewById(R.id.title);
        final TextView left = (TextView) dialog.findViewById(R.id.left);
        TextView right = (TextView) dialog.findViewById(R.id.right);
        final EditText mBindCode = (EditText) dialog.findViewById(R.id.id_bind_code);

        title.setText(titleStr);
        left.setText(leftStr);
        right.setText(rightStr);

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.OnNo();
                    dialog.dismiss();
                }
            }
        });

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = mBindCode.getText().toString().trim();
                if (TextUtils.isEmpty(str)) {
                    showToast(v.getContext(), "绑定码不能为空");
                    return;
                }
                int length = str.length();
                if (length == 0) {
                    length = 1;
                }
                int code = Integer.parseInt(str);
                int[] auth_code = new int[length];
                for (int i = 0; i < length; i++) {
                    auth_code[i] = code / getBaseCode(length - i) % 10;
                }
                if (listener != null) {
                    listener.OnYes(auth_code);
                    dialog.dismiss();
                }
            }
        });

        dialog.show();

        return dialog;
    }

    /**
     * 常见的对话框
     *
     * @param activity
     * @param titleStr
     * @param leftStr
     * @param listener
     */
    public static Dialog showDialog(final Activity activity, String leftStr, String titleStr, String rightStr, final OnWheelChangeingListener listener) {
        final Dialog dialog = new Dialog(activity, R.style.dialog);
        dialog.setContentView(R.layout.dialog_common);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().width = (int) (activity.getWindowManager().getDefaultDisplay().getWidth() * 0.9f);
        TextView title = (TextView) dialog.findViewById(R.id.title);
        TextView left = (TextView) dialog.findViewById(R.id.left);
        TextView right = (TextView) dialog.findViewById(R.id.right);

        title.setText(titleStr);
        left.setText(leftStr);
        right.setText(rightStr);

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.OnYes();
                    dialog.dismiss();
                }
            }
        });

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.OnNo();
                    dialog.dismiss();
                }
            }
        });

        dialog.show();

        return dialog;
    }

    private static int getBaseCode(int length) {
        int baseCode = 10;
        switch (length) {
            case 1:
                baseCode = 1;
                break;
            case 2:
                baseCode = 10;
                break;
            case 3:
                baseCode = 100;
                break;
            case 4:
                baseCode = 1000;
                break;
            case 5:
                baseCode = 10000;
                break;
            case 6:
                baseCode = 100000;
                break;
            case 7:
                baseCode = 1000000;
                break;
            case 8:
                baseCode = 10000000;
                break;
        }
        return baseCode;
    }

}
