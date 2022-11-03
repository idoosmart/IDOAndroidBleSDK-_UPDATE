package test.com.ido.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import java.io.FileOutputStream;

/**
 * @author tianwei
 * @date 2022/11/1
 * @time 18:52
 * 用途:
 */
public class BitmapUtil {
    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable == null) return null;
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }


    /**
     * 转换为圆角图片
     *
     * @param source
     * @param recoup 补偿
     * @return
     */
    public static Bitmap transform2CycleBitmap(Bitmap source, float recoup) {
        if (source == null) {
            return null;
        }
        int x = source.getWidth();
        int y = source.getHeight();
        float radius = x >= y ? x / 2f : y / 2f;
        Bitmap bitmap = Bitmap.createBitmap(x, y, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);
        canvas.drawCircle(x / 2f, y / 2f, radius - recoup, paint);
        return bitmap;
    }

    /**
     * Bitmap 保存到sdcard
     *
     * @param b           bitmap 对象，
     * @param strFileName 文件路径
     */
    public static void saveBitmap(Bitmap b, String strFileName) {
        if (b == null || TextUtils.isEmpty(strFileName)) return;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(strFileName);
            if (null != fos) {
                b.compress(Bitmap.CompressFormat.PNG, 90, fos);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                    fos.flush();
                    fos = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }
}
