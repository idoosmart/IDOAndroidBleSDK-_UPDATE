package test.com.ido.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import test.com.ido.R;

/**
 * @author tianwei
 * @date 2022/11/1
 * @time 18:52
 * 用途:
 */
public class BitmapUtil {
    // 压缩图片大小k
    public static final int IMAGE_SIZE = 25;

    /**
     * bitmap写入到文件。
     * <p>该方法适用于写入小文件，因为这是一次性的</p>
     *
     * @param file  文件实例
     * @param image 写入图片
     * @param file  文件实例
     * @return file存在则删除，写入新的内容
     */
    public static void savePngBitmap(Bitmap image, File file, boolean isCompressBit) {
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            if (isCompressBit) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.PNG, 100, baos);
                if (baos.toByteArray().length > IMAGE_SIZE * 2) {//图片小于50，不进行压缩。
                    int options = 100;
                    while (baos.toByteArray().length / 1024 > IMAGE_SIZE) { //图片很大，质量最低压倒40%。
                        baos.reset();//重置baos即清空baos
                        image.compress(Bitmap.CompressFormat.PNG, options, baos);
                        options -= 10;
                    }
                }
                ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
                Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
                if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                    out.flush();
                    out.close();
                }
            } else {
                if (image.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                    out.flush();
                    out.close();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static Bitmap view2BitmapBlackBg(final View view, int width, int height) {
        if (view == null) return null;
        Bitmap ret = Bitmap.createBitmap(width,
                height,
                Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(ret);
        canvas.drawColor(Color.BLACK);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        view.draw(canvas);
        return ret;
    }

    public static Bitmap view2BitmapWithAlpha(final View view, int width, int height) {
        if (view == null) return null;
        Bitmap ret = Bitmap.createBitmap(width,
                height,
                Bitmap.Config.ARGB_8888);
        ret.eraseColor(Color.argb(0, 0, 0, 0));
        Canvas canvas = new Canvas(ret);
        canvas.drawColor(Color.TRANSPARENT);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        view.draw(canvas);
        return ret;
    }

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

    public static Bitmap transformCycleBitmap(Bitmap originalBitmap) {
        // 加载原始图片
// 创建一个空白的Bitmap作为目标，大小为圆形区域的直径
        int diameter = Math.min(originalBitmap.getWidth(), originalBitmap.getHeight());
        Bitmap circularBitmap = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888);

// 创建一个Canvas对象，并将其与目标Bitmap关联
        Canvas canvas = new Canvas(circularBitmap);

// 创建一个Paint对象，并设置抗锯齿属性
        Paint paint = new Paint();
        paint.setAntiAlias(true);

// 计算圆形区域的半径
        float radius = diameter / 2f;

// 在Canvas上绘制圆形区域
        canvas.drawCircle(radius, radius, radius, paint);

// 创建一个BitmapShader，并将原始图片作为源
        Shader shader = new BitmapShader(originalBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

// 将Shader设置给Paint对象
        paint.setShader(shader);

// 设置Xfermode为SRC_IN，以保留交集部分
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

// 绘制圆形图片
        canvas.drawBitmap(originalBitmap, 0, 0, paint);
        return circularBitmap;
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
