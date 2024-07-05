package com.ido.jielidial.utils

import android.content.Context
import android.graphics.*
import com.ido.jielidial.R
import java.io.FileInputStream

internal object ImageUtils {
    /**
     * 把颜色替换成主题色，不改变图片的透明度
     */
    fun replaceColorPix(themeColor: Int, src: Bitmap): Bitmap {
        val width = src.width
        val height = src.height
        val colors = IntArray(width * height)
        val red = Color.red(themeColor)
        val green = Color.green(themeColor)
        val blue = Color.blue(themeColor)
        for (i in 0 until width) {
            for (j in 0 until height) {
                val color = src.getPixel(i, j)
                val alpha = Color.alpha(color)
                if (alpha > 0) {
                    colors[j * width + i] = Color.argb(alpha, red, green, blue)
                }
            }
        }
        return Bitmap.createBitmap(
            colors, width, height,
            src.config
        )
    }

    /**
     * 获取带alpha的RGB565图片数据，每行对齐
     */
    fun getBitmapAlphaPix(bit: Bitmap): ByteArray {
        val width = bit.width
        val height = bit.height
        var lineSize = width * 3
        lineSize = (lineSize + 3) / 4 * 4 // 每行对齐
        val data = ByteArray(lineSize * height)
        for (h in 0 until height) {
            for (w in 0 until width) {
                val clr = bit.getPixel(w, h)
                val alpha = clr and -0x1000000 shr 24
                val red = clr and 0x00ff0000 shr 16
                val green = clr and 0x0000ff00 shr 8
                val blue = clr and 0x000000ff
                val color = (red shr 3 shl 11) + (green shr 2 shl 5) + (blue shr 3)
                data[lineSize * h + w * 3] = alpha.toByte()
                data[lineSize * h + w * 3 + 1] = (color shr 8 and 0xff).toByte()
                data[lineSize * h + w * 3 + 2] = (color and 0xff).toByte()
            }
        }
        return data
    }

    /**
     * 根据所选的元素，重新画一张图片
     */
    fun composeImage(bgPath: String?, previewPath: String?, color: Int): Bitmap? {
        if (bgPath.isNullOrEmpty() && previewPath.isNullOrEmpty()) {
            return null
        }

        val srcBitmap = if (bgPath.isNullOrEmpty()) {
            throw IllegalArgumentException("background image path can't be null")
        } else {
            val fis = FileInputStream(bgPath)
            val options = BitmapFactory.Options()
            options.inScaled = false
            BitmapFactory.decodeFileDescriptor(fis.fd, null, options)
        }
        val cache = Bitmap.createBitmap(
            srcBitmap.width,
            srcBitmap.height,
            Bitmap.Config.ARGB_8888
        )

        val paint = Paint()
        paint.isDither = true
        val canvas = Canvas(cache)
        canvas.drawBitmap(srcBitmap, 0f, 0f, null)

        val options: BitmapFactory.Options = BitmapFactory.Options()
        options.inScaled = false
        val bitmap = BitmapFactory.decodeFile(previewPath, options)
        val colorBitmap = replaceColorPix(color, bitmap)
        canvas.drawBitmap(colorBitmap, 0f, 0f, null)

        srcBitmap.recycle()
        return cache
    }

    /**
     * 宽高分别缩放
     *
     * @param bitmap     加载的图片
     * @param widthSize  缩放之后的图片宽度.
     * @param heightSize 缩放之后的图片高度.
     */
    fun scaleImage(bitmap: Bitmap, widthSize: Int, heightSize: Int): Bitmap {
        val bmpW = bitmap.width
        val bmpH = bitmap.height
        val scaleW = widthSize.toFloat() / bmpW
        val scaleH = heightSize.toFloat() / bmpH
        val matrix = Matrix()
        matrix.postScale(scaleW, scaleH)
        return Bitmap.createBitmap(bitmap, 0, 0, bmpW, bmpH, matrix, true)
    }

    fun getRoundCornerBitmap(bitmap: Bitmap, roundPx: Int): Bitmap {
        return try {
            // 其原理就是：先建立一个与图片大小相同的透明的Bitmap画板
            // 然后在画板上画出一个想要的形状的区域。
            // 最后把源图片帖上。
            val width = bitmap.width
            val height = bitmap.height
            val paintingBoard = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(paintingBoard)
            canvas.drawFilter =
                PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
            canvas.drawARGB(
                Color.TRANSPARENT,
                Color.TRANSPARENT,
                Color.TRANSPARENT,
                Color.TRANSPARENT
            )
            val paint = Paint()
            paint.isAntiAlias = true
            paint.color = Color.RED

            //画出4个圆角
            val rectF = RectF(0f, 0f, width.toFloat(), height.toFloat())
            canvas.drawRoundRect(rectF, roundPx.toFloat(), roundPx.toFloat(), paint)
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)

            //帖子图
            val src = Rect(0, 0, width, height)
            canvas.drawBitmap(bitmap, src, src, paint)
            paintingBoard
        } catch (exp: Exception) {
            bitmap
        }
    }
}