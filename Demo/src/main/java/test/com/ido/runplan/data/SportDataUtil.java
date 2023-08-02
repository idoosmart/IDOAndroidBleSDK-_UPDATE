package test.com.ido.runplan.data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import test.com.ido.runplan.utils.StringUtil;
import test.com.ido.utils.DateUtil;

public class SportDataUtil {
    /**
     * 格式化速度
     *BigDecimal 防止丢精度
     * @param avgSpeed
     * @return
     */
    public static String formatAvgSpeed(float avgSpeed) {
        String speedStr = "0.0";
        if (avgSpeed > 0) {
            BigDecimal decimal = new BigDecimal(String.valueOf(avgSpeed));
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
            speedStr = decimalFormat.format(decimal.doubleValue());
        }
        return speedStr;
    }

    /**
     * 格式化配速
     *
     * @param
     * @return
     */
    public static String computeTimePace(int totalDistance, int totalSeconds) {
        String pace;
        float kmDistance, mileDistance;
        kmDistance = totalDistance / 1000f;
        //距离是4摄5入计算的所以超过5米才有效
        if (totalDistance >= 1) {
            pace = StringUtil.format("%.2f", ((totalSeconds / 60f) / kmDistance));
        } else {
            pace = StringUtil.format("%.2f", 0f);
        }
        pace = DateUtil.computeTimePace(pace);
        if ("00'00''".equals(pace)) {
            pace = "0";
        }
        return pace;
    }

    /**
     * 格式化速度
     *
     * @param
     * @return
     */
    public static String computeTimeSpeed(int totalDistance, int time) {
        String speed = "";
        float kmDistance = totalDistance / 1000f;

        if (totalDistance >= 1) {
            speed = StringUtil.format("%.1f", (kmDistance / (time / 3600f)));
        } else {
            speed = StringUtil.format("%.1f", 0f);
        }
        return speed;
    }

    public static String formatkm(Integer value){
        if(value == null){
            return "0";
        }
        double res = value / 1000.0;
        BigDecimal decimal = new BigDecimal(String.valueOf(res));
        DecimalFormat format = new DecimalFormat("#.##");
        format.setRoundingMode(RoundingMode.HALF_UP);
        return format.format(decimal.doubleValue());
    }
}
