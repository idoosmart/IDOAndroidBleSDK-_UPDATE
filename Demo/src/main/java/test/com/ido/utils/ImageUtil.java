package test.com.ido.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Created by asus on 2016/10/26.
 */
public class ImageUtil {


    /**
     * 转换为圆角图片
     *
     * @param source
     * @param radius
     * @return
     */
    public static Bitmap transform2CornerBitmap(Bitmap source, float radius) {
        if (source == null) {
            return null;
        }
        int x = source.getWidth();
        int y = source.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(x, y, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);
        canvas.drawRoundRect(0, 0, x, y, radius, radius, paint);
        return bitmap;
    }

    /**
     * 保存用户设置的壁纸，无论登录与否都是保存在本地
     *
     * @param bitmap
     */
    public static boolean saveWallPaper(Bitmap bitmap, String filePath) {
        boolean saveSuccess;
        File imageFile;
        if (bitmap == null) {
            return false;
        }
        FileOutputStream fos = null;
        try {
            imageFile = new File(filePath);
            if (!imageFile.exists()) {
                imageFile.createNewFile();
            }
            fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            saveSuccess = true;
        } catch (IOException e) {
            e.printStackTrace();
            saveSuccess = false;
        }
        try {
            if (fos != null) {
                fos.flush();
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return saveSuccess;
    }


}
