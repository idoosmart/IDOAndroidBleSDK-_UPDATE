package com.ido.jielidial.utils

import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
import com.ido.jielidial.model.Element
import com.ido.jielidial.model.Element.ImageData
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.OutputStream

internal object CustomDialUtils {

    private const val PREVIEW = 0x01
    private const val BACKGROUND = 0x02
    private const val YEAR = 0x06
    private const val MONTH = 0x07
    private const val DAY = 0x08
    private const val HOUR = 0x09
    private const val MINUTE = 0x0A
    private const val AM_PM = 0x0C
    private const val DELIMITER = 0x16
    private const val TIME_DELIMITER = 0x17
    private const val DATE_DELIMITER = 0x18
    private const val WEEK = 0x0D
    private var mContentColor: Int = Color.parseColor("#ffffff")

    /**
     * 生成自定义表盘文件，数据采用大端模式
     *
     * @param dialFilePath 表盘文件保存路径
     * @param bgPath 背景图片路径
     * @param previewPath 预览图图片路径，覆盖在背景图上方，透明只带时间组件
     * @param color 字体颜色
     * @param baseBinPath 基础bin包文件路径
     */
    @JvmStatic
    fun toDialFile(
        dialFilePath: String, bgPath: String?, previewPath: String?,
        color: Int, baseBinPath: String
    ): Boolean {
        mContentColor = color
        val dataMap = parseBaseBin(baseBinPath) ?: return false
        var stream: OutputStream? = null
        try {
//            val imageTotalCount = dataMap.values.stream().mapToInt { v -> v.imageCount.toInt() }.sum() + 2 // 2 是预览图和背景图片
            val imageTotalCount = dataMap.values.sumOf { it.imageCount.toInt() } + 2 // 2 是预览图和背景图片
            val elementCount = dataMap.size + 2 // 2 是预览图和背景图片
            stream = FileOutputStream(dialFilePath)
            val header = ByteArray(4)
            val count = intToBytes(imageTotalCount, 2)
            header[0] = count[1]  // 图片总数量
            header[1] = count[0]  // 图片总数量
            header[2] = elementCount.toByte()   // 元素数量 没有上/下午这个元素
            header[3] = 2   // 输出格式
            var index = 0
            stream.write(header)
            // 图片数据之前的所有数据大小，即图片数据在bin文件中的起始偏移位置
            // 头的长度4字节，9个元素，每个元素的数据占20个字节，51张图片，每张图片的size占4个字节
            val sizeList = ArrayList<Int>(imageTotalCount)
            var imageBufferOffset = header.size + 20 * elementCount + imageTotalCount * 4

            val thumbBean = getPreviewDataBean(previewPath, bgPath, color)
            thumbBean.type = PREVIEW
            thumbBean.offset = imageBufferOffset
            thumbBean.index = index
            thumbBean.anchor = 0
            thumbBean.compression = 0
            sizeList.add(thumbBean.size)
            writeElement(stream, thumbBean)
            index += thumbBean.imageCount
            imageBufferOffset += thumbBean.size * thumbBean.imageCount

            val backgroundBean = getDataBean(bgPath)
            backgroundBean.type = BACKGROUND
            backgroundBean.offset = imageBufferOffset
            backgroundBean.index = index
            backgroundBean.compression = 0
            sizeList.add(backgroundBean.size)
            writeElement(stream, backgroundBean)
            index += backgroundBean.imageCount
            imageBufferOffset += backgroundBean.size * backgroundBean.imageCount


            val hourBean =
                if (dataMap.containsKey(HOUR)) dataMap[HOUR]//getDataBean(context, true, bean.font, "time/", 10)
                else null
            hourBean?.let {
                it.offset = imageBufferOffset
                it.index = index
                it.compression = 0
                for (i in 0 until it.imageCount) {
                    sizeList.add(it.size)
                }
                writeElement(stream, it)
                index += it.imageCount
                imageBufferOffset += it.size * it.imageCount
            }

            val minuteBean =
                if (dataMap.containsKey(MINUTE)) dataMap[MINUTE]//getDataBean(context, true, bean.font, "time/", 10)
                else null
            minuteBean?.let {
                it.offset = imageBufferOffset
                it.index = index
                it.compression = 0
                for (i in 0 until it.imageCount) {
                    sizeList.add(it.size)
                }
                writeElement(stream, it)
                index += it.imageCount
                imageBufferOffset += it.size * it.imageCount
            }

            val weekBean =
                if (dataMap.containsKey(WEEK)) dataMap[WEEK]//getDataBean(context, true, bean.font, "week/", 7)
                else null
            weekBean?.let {
                it.offset = imageBufferOffset
                it.index = index
                it.compression = 0
                for (i in 0 until it.imageCount) {
                    sizeList.add(it.size)
                }
                writeElement(stream, it)
                index += it.imageCount
                imageBufferOffset += it.size * it.imageCount
            }

            val dayBean =
                if (dataMap.containsKey(DAY)) dataMap[DAY]//getDataBean(context, true, bean.font, "date/", 10)
                else null
            dayBean?.let {
                it.offset = imageBufferOffset
                it.index = index
                it.compression = 0
                for (i in 0 until it.imageCount) {
                    sizeList.add(it.size)
                }
                writeElement(stream, it)
                index += it.imageCount
                imageBufferOffset += it.size * it.imageCount
            }

            val dateSeparatorBean =
                if (dataMap.containsKey(DATE_DELIMITER)) dataMap[DATE_DELIMITER]//getDataBean(context, true, bean.font, "date_separator/", 1)
                else null
            dateSeparatorBean?.let {
                it.offset = imageBufferOffset
                it.index = index
                it.compression = 0
                for (i in 0 until it.imageCount) {
                    sizeList.add(it.size)
                }
                writeElement(stream, it)
                index += it.imageCount
                imageBufferOffset += it.size * it.imageCount
            }

            val monthBean =
                if (dataMap.containsKey(MONTH)) dataMap[MONTH]//getDataBean(context, true, bean.font, "date/", 10)
                else null
            monthBean?.let {
                it.offset = imageBufferOffset
                it.index = index
                it.compression = 0
                for (i in 0 until it.imageCount) {
                    sizeList.add(it.size)
                }
                writeElement(stream, it)
                index += it.imageCount
                imageBufferOffset += it.size * it.imageCount
            }

            val timeSeparatorBean =
                if (dataMap.containsKey(TIME_DELIMITER)) dataMap[TIME_DELIMITER]//getDataBean(context, true, bean.font, "time_separator/", 1)
                else null
            timeSeparatorBean?.let {
                it.offset = imageBufferOffset
                it.index = index
                it.compression = 0
                for (i in 0 until it.imageCount) {
                    sizeList.add(it.size)
                }
                writeElement(stream, it)
                index += it.imageCount
                imageBufferOffset += it.size * it.imageCount
            }

            val halfDayBean = if (dataMap.containsKey(AM_PM)) dataMap[AM_PM] else null
            halfDayBean?.let {
                it.offset = imageBufferOffset
                it.index = index
                it.compression = 0
                for (i in 0 until it.imageCount) {
                    sizeList.add(it.size)
                }
                writeElement(stream, it)
                index += it.imageCount
                imageBufferOffset += it.size * it.imageCount
            }

            // 图片sizes（每张图片的size）
            writeImageSize(stream, sizeList)
            // 图片数据
            stream.write(thumbBean.dataList[0].data)
            stream.write(backgroundBean.dataList[0].data)
            if (hourBean != null) {
                for (n in 0 until hourBean.imageCount) {
                    stream.write(hourBean.dataList[n].data)
                }
            }
            if (minuteBean != null) {
                for (n in 0 until minuteBean.imageCount) {
                    stream.write(minuteBean.dataList[n].data)
                }
            }
            if (weekBean != null) {
                for (n in 0 until weekBean.imageCount) {
                    stream.write(weekBean.dataList[n].data)
                }
            }
            if (dayBean != null) {
                for (n in 0 until dayBean.imageCount) {
                    stream.write(dayBean.dataList[n].data)
                }
            }
            if (dateSeparatorBean != null) {
                stream.write(dateSeparatorBean.dataList[0].data)
            }
            if (monthBean != null) {
                for (n in 0 until monthBean.imageCount) {
                    stream.write(monthBean.dataList[n].data)
                }
            }
            if (timeSeparatorBean != null) {
                stream.write(timeSeparatorBean.dataList[0].data)
            }
            if (halfDayBean != null) {
                for (n in 0 until halfDayBean.imageCount) {
                    stream.write(halfDayBean.dataList[n].data)
                }
            }
            Log.i("CustomDialUtils", "自定义表盘生成完成，${dialFilePath}")
            return true
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            Log.i("CustomDialUtils", "自定义表盘生成失败，${e.message}")
        } finally {
            try {
                stream?.close()
            } catch (_: Exception) {
            }
        }
        return false
    }

    private fun writeImageSize(s: FileOutputStream, list: ArrayList<Int>) {
        val size = list.size
        val sizeList = ByteArray(size * 4)
        var i = 0
        for (n in 0 until size) {
            // 大端模式
            val sizeByt = intToBytes(list[n], 4)
            sizeList[i++] = sizeByt[3]
            sizeList[i++] = sizeByt[2]
            sizeList[i++] = sizeByt[1]
            sizeList[i++] = sizeByt[0]
        }
        s.write(sizeList)
    }

    private fun writeElement(s: FileOutputStream, bean: Element) {
        val elementType = ByteArray(20)
        val offset = intToBytes(bean.offset, 4)
        elementType[0] = offset[3]
        elementType[1] = offset[2]
        elementType[2] = offset[1]
        elementType[3] = offset[0]
        val index = intToBytes(bean.index, 2)
        elementType[4] = index[1]
        elementType[5] = index[0]
        val w = intToBytes(bean.width, 2)
        elementType[6] = w[1]
        elementType[7] = w[0]
        val h = intToBytes(bean.height, 2)
        elementType[8] = h[1]
        elementType[9] = h[0]
        val x = intToBytes(bean.x, 2)
        elementType[10] = x[1]
        elementType[11] = x[0]
        val y = intToBytes(bean.y, 2)
        elementType[12] = y[1]
        elementType[13] = y[0]
        elementType[14] = bean.imageCount
        elementType[15] = (bean.type or (bean.hasAlpha shl 7)).toByte()
        elementType[16] = bean.anchor
        elementType[17] = bean.blackTransparent
        elementType[18] = (bean.compression shl 1).toByte()
        elementType[19] = bean.leftOffset
        s.write(elementType)
    }

    private fun getDataBean(filePath: String?): Element {
        val element = Element()
        element.imageCount = 1
        element.dataList = ArrayList<Element.ImageData>()
        val bitmap = if (filePath.isNullOrEmpty()) {
            throw IllegalArgumentException("background image path can't be null")
        } else {
            val fis = FileInputStream(filePath)
            val options = BitmapFactory.Options()
            options.inScaled = false
            BitmapFactory.decodeFileDescriptor(fis.fd, null, options)
        }
        element.width = bitmap.width
        element.height = bitmap.height
        val data = ImageUtils.getBitmapAlphaPix(bitmap)
        element.size = data.size
        val image = ImageData(data)
        element.dataList.add(image)
        bitmap.recycle()

        return element
    }

    private fun getPreviewDataBean(
        previewPath: String?, bgPath: String?, color: Int
    ): Element {
        if (bgPath.isNullOrEmpty()) {
            throw IllegalArgumentException("background image path can't be null")
        }
        val element = Element()
        element.imageCount = 1
        element.dataList = ArrayList<ImageData>()

        val bitmap = ImageUtils.composeImage(bgPath, previewPath, color)
        bitmap?.let {
            val scaleBitmap =
                ImageUtils.scaleImage(it, Constants.PREVIEW_WIDTH, Constants.PREVIEW_HEIGHT)
            val roundBitmap = ImageUtils.getRoundCornerBitmap(scaleBitmap, 12)
            element.width = roundBitmap.width
            element.height = roundBitmap.height
            val roundData = ImageUtils.getBitmapAlphaPix(roundBitmap)
            element.size = roundData.size
            val image = ImageData(roundData)
            element.dataList.add(image)
            it.recycle()
            scaleBitmap.recycle()
            roundBitmap.recycle()
        }
        return element
    }

    private fun intToBytes(from: Int, len: Int): ByteArray {
        val to = ByteArray(len)
        var iMove = len - 1
        var iTo = 0
        while (iMove >= 0) {
            to[iTo] = (from shr 8 * iMove and 0xFF).toByte()
            --iMove
            ++iTo
        }
        return to
    }

    private fun parseBaseBin(baseBinFilePath: String): Map<Int, Element>? {
        var stream: FileInputStream? = null
        try {
            stream = FileInputStream(baseBinFilePath)
            val twoBytes = ByteArray(2)
            val fourBytes = ByteArray(4)
            val flag = ByteArray(100)

            stream.read(flag)

            // 时间数字图片
            val hourModel = Element()
            val minuteModel = Element()
            hourModel.type = HOUR
            minuteModel.type = MINUTE

            stream.read(twoBytes)
            hourModel.width = convertTwoUnSignInt(twoBytes)
            minuteModel.width = convertTwoUnSignInt(twoBytes)

            stream.read(twoBytes)
            hourModel.height = convertTwoUnSignInt(twoBytes)
            minuteModel.height = convertTwoUnSignInt(twoBytes)

            hourModel.dataList = mutableListOf<ImageData>()
            minuteModel.dataList = mutableListOf<ImageData>()
            hourModel.imageCount = 10
            minuteModel.imageCount = 10
            for (n in 0 until 10) {
                stream.read(fourBytes)
                val imgSize = convertFourUnSignLong(fourBytes).toInt()
                val rawData = ByteArray(imgSize)
                stream.read(rawData)
                if (n == 0) {
                    hourModel.rawData = rawData
                } else if (n == 7) {
                    minuteModel.rawData = rawData
                }
                val bitmap = BitmapFactory.decodeByteArray(rawData, 0, imgSize)
                bitmap?.let {
                    val dest = ImageUtils.replaceColorPix(mContentColor, it)
                    val data = ImageUtils.getBitmapAlphaPix(dest)
                    hourModel.size = data.size
                    minuteModel.size = data.size
                    hourModel.dataList.add(ImageData(data))
                    minuteModel.dataList.add(ImageData(data))
                    it.recycle()
                }
            }

            // 时间分隔符图片
            val timeSeparatorModel = Element()
            timeSeparatorModel.type = DELIMITER
            stream.read(twoBytes)
            timeSeparatorModel.width = convertTwoUnSignInt(twoBytes)
            stream.read(twoBytes)
            timeSeparatorModel.height = convertTwoUnSignInt(twoBytes)
            stream.read(fourBytes)
            var imgSize = convertFourUnSignLong(fourBytes).toInt()
            timeSeparatorModel.dataList = mutableListOf<ImageData>()
            timeSeparatorModel.imageCount = 1
            val rawData = ByteArray(imgSize)
            stream.read(rawData)
            timeSeparatorModel.rawData = rawData
            val bitmap = BitmapFactory.decodeByteArray(rawData, 0, imgSize)
            bitmap?.let {
                val dest = ImageUtils.replaceColorPix(mContentColor, it)
                val data = ImageUtils.getBitmapAlphaPix(dest)
                timeSeparatorModel.size = data.size
                timeSeparatorModel.dataList.add(ImageData(data))
                it.recycle()
            }

            // 日期数字图片
            val dayModel = Element()
            val monthModel = Element()
            dayModel.type = DAY
            monthModel.type = MONTH
            stream.read(twoBytes)
            dayModel.width = convertTwoUnSignInt(twoBytes)
            monthModel.width = convertTwoUnSignInt(twoBytes)
            stream.read(twoBytes)
            dayModel.height = convertTwoUnSignInt(twoBytes)
            monthModel.height = convertTwoUnSignInt(twoBytes)

            dayModel.dataList = mutableListOf<ImageData>()
            monthModel.dataList = mutableListOf<ImageData>()
            dayModel.imageCount = 10
            monthModel.imageCount = 10
            for (n in 0 until 10) {
                stream.read(fourBytes)
                val size = convertFourUnSignLong(fourBytes).toInt()
                val dayRawData = ByteArray(size)
                stream.read(dayRawData)
                if (n == 0) {
                    monthModel.rawData = dayRawData
                } else if (n == 7) {
                    dayModel.rawData = dayRawData
                }
                val dayBitmap = BitmapFactory.decodeByteArray(dayRawData, 0, size)
                dayBitmap?.let {
                    val dest = ImageUtils.replaceColorPix(mContentColor, it)
                    val dayData = ImageUtils.getBitmapAlphaPix(dest)
                    dayModel.size = dayData.size
                    monthModel.size = dayData.size
                    dayModel.dataList.add(ImageData(dayData))
                    monthModel.dataList.add(ImageData(dayData))
                    it.recycle()
                }
            }

            // 日期分隔符图片
            val dateSeparatorModel = Element()
            dateSeparatorModel.type = DELIMITER
            stream.read(twoBytes)
            dateSeparatorModel.width = convertTwoUnSignInt(twoBytes)
            stream.read(twoBytes)
            dateSeparatorModel.height = convertTwoUnSignInt(twoBytes)
            stream.read(fourBytes)
            imgSize = convertFourUnSignLong(fourBytes).toInt()
            dateSeparatorModel.dataList = mutableListOf<ImageData>()
            dateSeparatorModel.imageCount = 1
            val dateSeparatorData = ByteArray(imgSize)
            stream.read(dateSeparatorData)
            dateSeparatorModel.rawData = dateSeparatorData
            val dateBitmap = BitmapFactory.decodeByteArray(dateSeparatorData, 0, imgSize)
            dateBitmap?.let {
                val dest = ImageUtils.replaceColorPix(mContentColor, it)
                val data = ImageUtils.getBitmapAlphaPix(dest)
                dateSeparatorModel.size = data.size
                dateSeparatorModel.dataList.add(ImageData(data))
                it.recycle()
            }

            // 星期图片
            val weekModel = Element()
            weekModel.type = WEEK
            stream.read(twoBytes)
            weekModel.width = convertTwoUnSignInt(twoBytes)
            stream.read(twoBytes)
            weekModel.height = convertTwoUnSignInt(twoBytes)

            weekModel.dataList = mutableListOf<ImageData>()
            weekModel.imageCount = 7
            for (n in 0 until 7) {
                stream.read(fourBytes)
                imgSize = convertFourUnSignLong(fourBytes).toInt()
                val weekRawData = ByteArray(imgSize)
                stream.read(weekRawData)
                if (n == 0) {
                    weekModel.rawData = weekRawData
                }
                val weekBitmap = BitmapFactory.decodeByteArray(weekRawData, 0, imgSize)
                weekBitmap?.let {
                    val dest = ImageUtils.replaceColorPix(mContentColor, it)
                    val weekData = ImageUtils.getBitmapAlphaPix(dest)
                    weekModel.size = weekData.size
                    weekModel.dataList.add(ImageData(weekData))
                    it.recycle()
                }
            }

            // 上/下午图片
            val halfDayModel = Element()
            halfDayModel.type = AM_PM
            stream.read(twoBytes)
            halfDayModel.width = convertTwoUnSignInt(twoBytes)
            stream.read(twoBytes)
            halfDayModel.height = convertTwoUnSignInt(twoBytes)

            halfDayModel.dataList = mutableListOf<ImageData>()
            halfDayModel.imageCount = 2
            for (n in 0 until 2) {
                stream.read(fourBytes)
                imgSize = convertFourUnSignLong(fourBytes).toInt()
                val halfDayRawData = ByteArray(imgSize)
                stream.read(halfDayRawData)
                if (n == 0) {
                    halfDayModel.rawData = halfDayRawData
                }
                val halfDayBitmap = BitmapFactory.decodeByteArray(halfDayRawData, 0, imgSize)
                halfDayBitmap?.let {
                    val dest = ImageUtils.replaceColorPix(mContentColor, it)
                    val halfDayData = ImageUtils.getBitmapAlphaPix(dest)
                    halfDayModel.size = halfDayData.size
                    halfDayModel.dataList.add(ImageData(halfDayData))
                    it.recycle()
                }
            }

            stream.read(twoBytes)
            hourModel.x = convertTwoUnSignInt(twoBytes)
            stream.read(twoBytes)
            hourModel.y = convertTwoUnSignInt(twoBytes)

            stream.read(twoBytes)
            timeSeparatorModel.x = convertTwoUnSignInt(twoBytes)
            stream.read(twoBytes)
            timeSeparatorModel.y = convertTwoUnSignInt(twoBytes)

            stream.read(twoBytes)
            minuteModel.x = convertTwoUnSignInt(twoBytes)
            stream.read(twoBytes)
            minuteModel.y = convertTwoUnSignInt(twoBytes)

            stream.read(twoBytes)
            monthModel.x = convertTwoUnSignInt(twoBytes)
            stream.read(twoBytes)
            monthModel.y = convertTwoUnSignInt(twoBytes)

            stream.read(twoBytes)
            dateSeparatorModel.x = convertTwoUnSignInt(twoBytes)
            stream.read(twoBytes)
            dateSeparatorModel.y = convertTwoUnSignInt(twoBytes)

            stream.read(twoBytes)
            dayModel.x = convertTwoUnSignInt(twoBytes)
            stream.read(twoBytes)
            dayModel.y = convertTwoUnSignInt(twoBytes)

            stream.read(twoBytes)
            weekModel.x = convertTwoUnSignInt(twoBytes)
            stream.read(twoBytes)
            weekModel.y = convertTwoUnSignInt(twoBytes)

            stream.read(twoBytes)
            halfDayModel.x = convertTwoUnSignInt(twoBytes)
            stream.read(twoBytes)
            halfDayModel.y = convertTwoUnSignInt(twoBytes)

            stream.close()

            val dataMap = HashMap<Int, Element>()
            if (hourModel.x != -1 && hourModel.y != -1) {
                dataMap[HOUR] = hourModel
            }
            if (minuteModel.x != -1 && minuteModel.y != -1) {
                dataMap[MINUTE] = minuteModel
            }
            if (timeSeparatorModel.x != -1 && timeSeparatorModel.y != -1) {
                dataMap[TIME_DELIMITER] = timeSeparatorModel
            }
            if (dayModel.x != -1 && dayModel.y != -1) {
                dataMap[DAY] = dayModel
            }
            if (monthModel.x != -1 && monthModel.y != -1) {
                dataMap[MONTH] = monthModel
            }
            if (dateSeparatorModel.x != -1 && dateSeparatorModel.y != -1) {
                dataMap[DATE_DELIMITER] = dateSeparatorModel
            }
            if (weekModel.x != -1 && weekModel.y != -1) {
                dataMap[WEEK] = weekModel
            }
            if (halfDayModel.x != -1 && halfDayModel.y != -1) {
                dataMap[AM_PM] = halfDayModel
            }
            return dataMap
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                stream?.close()
            } catch (_: Exception) {
            }
        }
        return null
    }

    private fun convertTwoUnSignInt(byteArray: ByteArray): Int =
        (byteArray[1].toInt() and 0xFF) shl 8 or (byteArray[0].toInt() and 0xFF)

    private fun convertFourUnSignLong(byteArray: ByteArray): Long =
        ((byteArray[3].toInt() and 0xFF) shl 24 or (byteArray[2].toInt() and 0xFF) shl 16 or (byteArray[1].toInt() and 0xFF) shl 8 or (byteArray[0].toInt() and 0xFF)).toLong()
}