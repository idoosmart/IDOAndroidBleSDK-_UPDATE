package test.com.ido.utils;

import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Created by asus on 2016/10/26.
 */
public class ImageUtil {
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
