package test.com.ido.gps;

import com.ido.ble.LocalDataManager;
import com.ido.ble.common.TimeUtil;
//import com.ido.ble.gps.database.GpsDataManager;
import com.ido.ble.gps.database.HealthGps;
import com.ido.ble.gps.database.HealthGpsItem;
import com.ido.ble.gps.model.GpsDataReply;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by zhouzj on 2018/7/21.
 */

public class TestGpsDataUtil implements Runnable{
    // 分隔符
    private static final String separators = ",";
    private static final String LONGITUDE_SYMBOL = "E";
    private static final String LATITUDE_SYMBOL = "N";


    @Override
    public void run() {
        moNiData();
    }

    public void moNiData() {
        // json格式转换
//        try {
//            GpsDataReply replyData = new GpsDataReply();
////                    replyData = moNiHealthGpsReply(replyData);
//            replyData = moNiHealthGpsReply2(replyData);
//            if (replyData.getYear() != 0 && (replyData.getItems() != null && replyData.getItems().size() > 0)) {
////                LogUtil.writeSyncLog("同步GPS健康数据replyData===" + replyData.toString());
//                showMessage("同步GPS健康数据replyData===" + replyData.toString());
//
//                // 汇总数据
//                HealthGps mHealthGps = new HealthGps();
//                mHealthGps.setDId(getBindId());
//                mHealthGps.setYear(replyData.getYear());
//                mHealthGps.setMonth(replyData.getMonth());
//                mHealthGps.setDay(replyData.getDay());
//                mHealthGps.setHour(replyData.getHour());
//                mHealthGps.setMinute(replyData.getMinute());
//                mHealthGps.setSecond(replyData.getSecond());
//                mHealthGps.setData_interval(replyData.getData_interval());
//                long date = TimeUtil.dateToStamp(replyData.getYear(), replyData.getMonth() - 1, replyData.getDay(), replyData.getHour(), replyData.getMinute(), replyData.getSecond());
//                mHealthGps.setDate(date);
////                LogUtil.writeSyncLog("同步GPS健康数据mHealthGps===" + mHealthGps.toString());
//                showMessage("同步GPS健康数据mHealthGps===" + mHealthGps.toString());
//                // 添加汇总数据
//                GpsDataManager.addHealthGps(mHealthGps);
//
//                // 详情数据
//                showMessage("同步GPS健康数据replyData.getItems()===>" + replyData.getItems());
//                for (String item : replyData.getItems()) {
//                    showMessage("同步GPS健康数据replyData.getItem===>" + item);
//                    if (item.contains(separators)) {
//                        String[] gpsItems = item.split(separators);
//                        showMessage("同步GPS健康数据转换之前,gpsItems[0]==" + gpsItems[0] + ",gpsItems[1]==" + gpsItems[1]);
//                        double longitude = getCoordinate(gpsItems[0], LONGITUDE_SYMBOL);
//                        double latitude = getCoordinate(gpsItems[1], LATITUDE_SYMBOL);
//                        showMessage("同步GPS健康数据转换之后,longitude==" + longitude + ",latitude==" + latitude);
//                        addHealthGpsItem(longitude, latitude, date);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            showMessage("同步GPS健康数据===>json格式转换出错===>" + e.getMessage());
//        } finally {
//            // 转发到主线程
//
//        }
    }


    /**
     * 获取一个坐标
     *
     * @param coordinateStr 坐标字符串
     * @param symbolStr     符号字符串
     * @return
     */
    private double getCoordinate(String coordinateStr, String symbolStr) {
        if (coordinateStr.equalsIgnoreCase("0.0E") || coordinateStr.equalsIgnoreCase("0.0N")) {
            return 0;
        }
        // 获取经度分割下标
        int index = coordinateStr.indexOf(".") - 2;
        // 度
        int degree = Integer.parseInt(coordinateStr.substring(0, index));
        // 分
        double minute = Double.parseDouble(coordinateStr.substring(index, coordinateStr.length() - 1));
        // 坐标
        double m = minute / 60;
        showMessage("获取一个坐标===>>> minute = " + minute + ", m = " + m);
        double coordinate = degree + m;
        // 正数、负数
        boolean symbol = coordinateStr.substring(coordinateStr.length() - 1).equalsIgnoreCase(symbolStr) ? true : false;
        if (!symbol) {
            coordinate = -coordinate;
        }
        return coordinate;
    }

    /**
     * 模拟数据
     *
     * @param replyData
     * @return
     */
    private GpsDataReply moNiHealthGpsReply2(GpsDataReply replyData) {
        List<String> items = new ArrayList<>();
//        String[] strings = MoNiData.getFirst();
        String[] strings = TestGpsDataUtil.getSecond();
        for (int i = 0; i < strings.length; i++) {
            String item = strings[i];
            showMessage("同步GPS健康数据item===>" + item);
            items.add(item);
        }
        replyData.setYear(2017);
        replyData.setMonth(11);
        replyData.setDay(2);
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (Calendar.AM_PM == Calendar.PM) {
            hour += 12;
        }
        replyData.setHour(hour);
        replyData.setMinute(Calendar.getInstance().get(Calendar.MINUTE));
        replyData.setSecond(Calendar.getInstance().get(Calendar.SECOND));
        replyData.setItems(items);
        replyData.setData_interval(2);
        return replyData;
    }

    // 打印信息
    void showMessage(Object msg) {
    }


    /**
     * 添加详情数据
     *
     * @param longitude 经度
     * @param latitude  纬度
     * @param date      添加时间
     */
    private void addHealthGpsItem(double longitude, double latitude, long date) {
//        HealthGpsItem mHealthGpsItem = new HealthGpsItem();
//        mHealthGpsItem.setDId(getBindId());
//        mHealthGpsItem.setDate(date);
//        mHealthGpsItem.setLongitude(longitude);
//        mHealthGpsItem.setLatitude(latitude);
////        LogUtil.writeSyncLog("同步GPS健康数据mHealthGpsItem===" + mHealthGpsItem.toString());
//        showMessage("同步GPS健康数据mHealthGpsItem===" + mHealthGpsItem.toString());
//        // 添加详情数据
//        GpsDataManager.addHealthGpsItem(mHealthGpsItem);
    }

    public static String[] getFirst() {
        return new String[]{
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "11358.5781E,2241.3370N",
                "11358.5745E,2241.3541N",
                "11358.5726E,2241.3617N",
                "11358.5705E,2241.3641N",
                "11358.5711E,2241.3580N",
                "11358.5715E,2241.3536N",
                "11358.5733E,2241.3454N",
                "11358.5739E,2241.3420N",
                "11358.5742E,2241.3431N",
                "11358.5746E,2241.3435N",
                "11358.5759E,2241.3413N",
                "11358.5775E,2241.3395N",
                "11358.5792E,2241.3366N",
                "11358.5809E,2241.3344N",
                "11358.5827E,2241.3322N",
                "11358.5849E,2241.3314N",
                "11358.5868E,2241.3323N",
                "11358.5886E,2241.3311N",
                "11358.5905E,2241.3323N",
                "11358.5920E,2241.3308N",
                "11358.5937E,2241.3317N",
                "11358.5955E,2241.3326N",
                "11358.5968E,2241.3336N",
                "11358.5979E,2241.3324N",
                "11358.5993E,2241.3320N",
                "11358.6006E,2241.3317N",
                "11358.6021E,2241.3326N",
                "11358.6035E,2241.3334N",
                "11358.6051E,2241.3319N",
                "11358.6066E,2241.3310N",
                "11358.6081E,2241.3320N",
                "11358.6096E,2241.3324N",
                "11358.6110E,2241.3326N",
                "11358.6125E,2241.3325N",
                "11358.6138E,2241.3327N",
                "11358.6150E,2241.3321N",
                "11358.6165E,2241.3318N",
                "11358.6178E,2241.3323N",
                "11358.6193E,2241.3326N",
                "11358.6205E,2241.3341N",
                "11358.6218E,2241.3346N",
                "11358.6230E,2241.3362N",
                "11358.6242E,2241.3374N",
                "11358.6252E,2241.3376N",
                "11358.6259E,2241.3376N",
                "11358.6261E,2241.3377N",
                "11358.6265E,2241.3375N",
                "11358.6270E,2241.3371N",
                "11358.6276E,2241.3360N",
                "11358.6282E,2241.3357N",
                "11358.6287E,2241.3361N",
                "11358.6293E,2241.3370N",
                "11358.6300E,2241.3376N",
                "11358.6300E,2241.3385N",
                "11358.6298E,2241.3387N",
                "11358.6292E,2241.3386N",
                "11358.6292E,2241.3375N",
                "11358.6291E,2241.3352N",
                "11358.6292E,2241.3339N",
                "11358.6283E,2241.3312N",
                "11358.6269E,2241.3292N",
                "11358.6252E,2241.3281N",
                "11358.6242E,2241.3259N",
                "11358.6236E,2241.3240N",
                "11358.6227E,2241.3236N",
                "11358.6218E,2241.3223N",
                "11358.6205E,2241.3220N",
                "11358.6191E,2241.3222N",
                "11358.6177E,2241.3211N",
                "11358.6163E,2241.3203N",
                "11358.6149E,2241.3187N",
                "11358.6136E,2241.3184N",
                "11358.6123E,2241.3186N",
                "11358.6111E,2241.3189N",
                "11358.6099E,2241.3178N",
                "11358.6086E,2241.3175N",
                "11358.6073E,2241.3176N",
                "11358.6058E,2241.3176N",
                "11358.6044E,2241.3175N",
                "11358.6028E,2241.3174N",
                "11358.6010E,2241.3173N",
                "11358.5996E,2241.3174N",
                "11358.5980E,2241.3181N",
                "11358.5963E,2241.3186N",
                "11358.5948E,2241.3198N",
                "11358.5930E,2241.3206N",
                "11358.5914E,2241.3210N",
                "11358.5897E,2241.3211N",
                "11358.5881E,2241.3213N",
                "11358.5865E,2241.3212N",
                "11358.5850E,2241.3214N",
                "11358.5834E,2241.3213N",
                "11358.5819E,2241.3209N",
                "11358.5801E,2241.3204N",
                "11358.5784E,2241.3200N",
                "11358.5770E,2241.3194N",
                "11358.5760E,2241.3190N",
                "11358.5752E,2241.3188N",
                "11358.5744E,2241.3193N",
                "11358.5737E,2241.3196N",
                "11358.5745E,2241.3196N",
                "11358.5744E,2241.3201N",
                "11358.5744E,2241.3211N",
                "11358.5743E,2241.3234N",
                "11358.5741E,2241.3256N",
                "11358.5738E,2241.3277N",
                "11358.5744E,2241.3294N",
                "11358.5750E,2241.3303N"
        };
    }

    public static String[] getSecond() {
        return new String[]{
                "11358.5750E,2241.3303N",
                "11358.5750E,2241.3303N",
                "11358.5750E,2241.3303N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "11401.2320E,2240.523N",
                "11401.2285E,2240.560N",
                "11401.2310E,2240.552N",
                "11401.2315E,2240.557N",
                "11401.2248E,2240.581N",
                "11401.2167E,2240.607N",
                "11401.1998E,2240.626N",
                "11401.1933E,2240.631N",
                "11401.1972E,2240.648N",
                "11401.1989E,2240.664N",
                "11401.1975E,2240.688N",
                "11401.1990E,2240.682N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "11358.9832E,2238.3356N",
                "11358.9824E,2238.3700N",
                "11358.9811E,2238.3820N",
                "11358.9803E,2238.3741N",
                "11358.9788E,2238.3729N",
                "11358.9767E,2238.3693N",
                "11358.9742E,2238.3635N",
                "11358.9722E,2238.3605N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "0.0E,0.0N",
                "11358.5437E,2241.3273N",
                "11358.5411E,2241.3354N",
                "11358.5397E,2241.3284N",
                "11358.5392E,2241.3276N",
                "11358.5381E,2241.3279N",
                "11358.5367E,2241.3281N",
                "11358.5355E,2241.3279N",
                "11358.5342E,2241.3302N",
                "11358.5328E,2241.3322N",
                "11358.5314E,2241.3340N",
                "11358.5307E,2241.3367N",
                "11358.5299E,2241.3383N",
                "11358.5284E,2241.3394N",
                "11358.5279E,2241.3396N",
                "11358.5276E,2241.3390N",
                "11358.5276E,2241.3379N",
                "11358.5274E,2241.3367N",
                "11358.5271E,2241.3355N",
                "11358.5266E,2241.3340N",
                "11358.5273E,2241.3326N",
                "11358.5278E,2241.3317N",
                "11358.5285E,2241.3308N",
                "11358.5288E,2241.3299N",
                "11358.5292E,2241.3287N",
                "11358.5298E,2241.3272N",
                "11358.5300E,2241.3262N",
                "11358.5314E,2241.3251N",
                "11358.5327E,2241.3241N",
                "11358.5339E,2241.3234N",
                "11358.5345E,2241.3225N",
                "11358.5358E,2241.3222N",
                "11358.5373E,2241.3218N",
                "11358.5387E,2241.3219N",
                "11358.5403E,2241.3221N",
                "11358.5419E,2241.3224N",
                "11358.5436E,2241.3217N",
                "11358.5453E,2241.3220N",
                "11358.5468E,2241.3218N",
                "11358.5485E,2241.3217N",
                "11358.5501E,2241.3222N",
                "11358.5517E,2241.3230N",
                "11358.5532E,2241.3231N",
                "11358.5547E,2241.3233N",
                "11358.5561E,2241.3237N",
                "11358.5578E,2241.3250N",
                "11358.5596E,2241.3260N",
                "11358.5613E,2241.3268N",
                "11358.5630E,2241.3274N",
                "11358.5646E,2241.3281N",
                "11358.5662E,2241.3286N",
                "11358.5675E,2241.3291N",
                "11358.5686E,2241.3294N",
                "11358.5699E,2241.3277N",
                "11358.5711E,2241.3264N",
                "11358.5723E,2241.3258N",
                "11358.5734E,2241.3260N",
                "11358.5742E,2241.3259N",
                "11358.5746E,2241.3267N",
                "11358.5744E,2241.3283N",
                "11358.5740E,2241.3295N",
                "11358.5733E,2241.3296N",
                "11358.5728E,2241.3300N",
                "11358.5709E,2241.3299N",
                "11358.5708E,2241.3309N",
                "11358.5707E,2241.3320N",
                "11358.5700E,2241.3323N",
                "11358.5698E,2241.3334N",
                "11358.5693E,2241.3339N",
                "11358.5692E,2241.3344N",
                "11358.5693E,2241.3354N",
                "11358.5698E,2241.3364N",
                "11358.5705E,2241.3375N",
                "11358.5710E,2241.3390N",
                "11358.5715E,2241.3404N",
                "11358.5716E,2241.3416N",
                "11358.5717E,2241.3416N",
                "11358.5719E,2241.3422N",
                "11358.5720E,2241.3432N"
        };
    }

    // 获取当前绑定设备的id
    protected long getBindId() {
        return LocalDataManager.getLastConnectedDeviceInfo().mDeviceId;
    }
}
