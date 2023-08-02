package test.com.ido.runplan.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author: lyw
 * @Package: com.veryfit2hr.second.ui.main.timeaxis.model
 * @Description: ${TODO}(经纬度)
 * @Date: 2016/10/28 00:56
 */
public class LatLngBean implements Serializable, Cloneable, Parcelable {

    protected LatLngBean(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
        altitude = in.readDouble();
        isGps = in.readByte() != 0;
        currentTimeMillis = in.readString();
    }

    public static final Creator<LatLngBean> CREATOR = new Creator<LatLngBean>() {
        @Override
        public LatLngBean createFromParcel(Parcel in) {
            return new LatLngBean(in);
        }

        @Override
        public LatLngBean[] newArray(int size) {
            return new LatLngBean[size];
        }
    };

    @Override
    public LatLngBean clone() {
        try {
            return (LatLngBean) super.clone();
        } catch (Exception e) {

        }
        return null;

    }

    public double latitude;
    public double longitude;

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public double altitude;

    /**
     * 是否是标准的GPS坐标点
     * 如果是手环那边传过来的GPS坐标点，则是标准的GPS坐标点。
     * 如果是手机这边的坐标点，则是高德坐标点
     */
    public boolean isGps;

    /**
     * 当前时间点
     * yyyy-MM-dd HH:mm:ss
     */
    public String currentTimeMillis;

    /**
     * 当前点的颜色
     */
    private int color;


    public LatLngBean(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public LatLngBean() {

    }


    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


    public String getCurrentTimeMillis() {
        return currentTimeMillis;
    }

    public void setCurrentTimeMillis(String currentTimeMillis) {
        this.currentTimeMillis = currentTimeMillis;
    }


    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "LatLngBean{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", currentTimeMillis=" + currentTimeMillis +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeDouble(altitude);
        dest.writeByte((byte) (isGps ? 1 : 0));
        dest.writeString(currentTimeMillis);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LatLngBean that = (LatLngBean) o;
        return Double.compare(that.latitude, latitude) == 0 &&
                Double.compare(that.longitude, longitude) == 0 &&
                Double.compare(that.altitude, altitude) == 0 &&
                isGps == that.isGps &&
                Objects.equals(currentTimeMillis, that.currentTimeMillis);
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude, altitude, isGps, currentTimeMillis);
    }
}