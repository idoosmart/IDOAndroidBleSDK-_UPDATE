package com.ido.life.constants

import androidx.annotation.IntDef

/**
 * @author tianwei
 * @date 2021/10/20
 * @time 10:54
 * 用途:
 */
class WallpaperDialConstants {
    /**
     * @author tianwei
     * @date 2021/10/15
     * @time 17:57
     * 用途:壁纸表盘功能表
     */
    @Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD, AnnotationTarget.FUNCTION)
    @IntDef(
        WidgetFunction.WEEK_DATE,
        WidgetFunction.STEP,
        WidgetFunction.DISTANCE,
        WidgetFunction.CALORIE,
        WidgetFunction.HEART_RATE,
        WidgetFunction.BATTERY
    )
    annotation class WidgetFunction {
        /**
         * 1星期/日期2步数3距离4卡路里5心率6电量
         */
        companion object {
            const val WEEK_DATE = 1
            const val STEP = 2
            const val DISTANCE = 3
            const val CALORIE = 4
            const val HEART_RATE = 5
            const val BATTERY = 6
        }
    }

    /**
     * @author tianwei
     * @date 2021/10/15
     * @time 17:57
     * 用途:壁纸表盘组件显示位置
     */
    @Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD, AnnotationTarget.FUNCTION)
    @IntDef(
        WidgetLocation.INVALID,
        WidgetLocation.LEFT_TOP,
        WidgetLocation.LEFT_CENTER,
        WidgetLocation.LEFT_BOTTOM,
        WidgetLocation.RIGHT_TOP,
        WidgetLocation.RIGHT_CENTER,
        WidgetLocation.RIGHT_BOTTOM,
        WidgetLocation.CENTER_TOP,
        WidgetLocation.CENTER,
        WidgetLocation.CENTER_BOTTOM
    )
    annotation class WidgetLocation {
        /**
         * 0无效1表盘（上左）参考九宫格2表盘（上中）3表盘（上右)4表盘（中左）5表盘（中中）6表盘（中右）7表盘（下左）8表盘（下中）9表盘（下右）
         */
        companion object {
            const val INVALID = 0
            const val LEFT_TOP = 1
            const val LEFT_CENTER = 4
            const val LEFT_BOTTOM = 7
            const val RIGHT_TOP = 3
            const val RIGHT_CENTER = 6
            const val RIGHT_BOTTOM = 9
            const val CENTER_TOP = 2
            const val CENTER = 5
            const val CENTER_BOTTOM = 8
        }
    }


    /**
     * @author tianwei
     * @date 2021/10/15
     * @time 17:57
     * 用途:壁纸表盘组件显示状态
     */
    @Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD, AnnotationTarget.FUNCTION)
    @IntDef(
        WidgetStatus.SHOW,
        WidgetStatus.HIDE
    )
    annotation class WidgetStatus {
        /**
         * 0全部显示，1隐藏子控件（图标和数字）
         */
        companion object {
            const val SHOW = 0
            const val HIDE = 1
        }
    }

    /**
     * @author tianwei
     * @date 2021/10/20
     * @time 11:01
     * 用途:壁纸表盘组件
     */
    enum class WidgetColor(val color: String) {
        COLOR_BFA8FF("#BFA8FF"),
        COLOR_FF3333("#FF3333"),
        COLOR_826F60("#826F60"),
        COLOR_FF8542("#FF8542"),
        COLOR_5CA4AC("#5CA4AC"),
        COLOR_7DC498("#7DC498"),
        COLOR_4694FF("#4694FF"),
        COLOR_4C68BE("#4C68BE"),
        COLOR_718B83("#718B83"),
        COLOR_B4C21D("#B4C21D"),
        COLOR_CC8A89("#CC8A89"),
        COLOR_FFD508("#FFD508"),
        COLOR_FDF19F("#FDF19F"),
        COLOR_FFE0BF("#FFE0BF"),
        COLOR_C4DAE9("#C4DAE9"),
        COLOR_F2F2F2("#F2F2F2")
    }

    /**
     * @author tianwei
     * @date 2021/10/15
     * @time 17:57
     * 用途:壁纸表盘安装状态
     */
    @Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD, AnnotationTarget.FUNCTION)
    @IntDef(
        InstallStatus.DEFAULT,
        InstallStatus.INSTALLING,
        InstallStatus.INSTALL_FAILED,
        InstallStatus.INSTALL_SUCCESS,
        InstallStatus.INSTALLED_ALREADY,
        InstallStatus.DOWNLOADING,
        InstallStatus.DOWNLOAD_SUCCESS,
        InstallStatus.DOWNLOAD_FAILED,
        InstallStatus.DISK_DEFRAG
    )
    annotation class InstallStatus {
        companion object {
            const val DEFAULT = 0
            const val INSTALLING = 1
            const val INSTALL_FAILED = 2
            const val INSTALL_SUCCESS = 3
            const val INSTALLED_ALREADY = 4
            const val DOWNLOADING = 5
            const val DOWNLOAD_SUCCESS = 6
            const val DOWNLOAD_FAILED = 7
            const val DISK_DEFRAG = 8//磁盘整理
        }
    }

}