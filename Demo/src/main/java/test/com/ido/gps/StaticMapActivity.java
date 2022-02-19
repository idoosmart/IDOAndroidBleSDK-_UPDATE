package test.com.ido.gps;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.DistanceUtil;
import com.ido.ble.LocalDataManager;
import com.ido.ble.gps.database.HealthGpsItem;

import java.util.ArrayList;
import java.util.List;

import test.com.ido.R;

public class StaticMapActivity extends Activity {

    // 定位相关
    BitmapDescriptor mCurrentMarker;
    MapView mMapView;
    BaiduMap mBaiduMap;
    Polyline mPolyline;
    LatLng target;
    List<LatLng> latLngs = new ArrayList<>();

    // 开始和结束的位置
    private BitmapDescriptor startBD;
    private BitmapDescriptor finishBD;

    // 标记
    private Marker mMarkerA;
    private Marker mMarkerB;
    private InfoWindow mInfoWindow;

    /*数据*/
    private List<HealthGpsItem> healthGpsItems;
    // 缩放级别
    private float level;
    // 从上个页面传过来的时间
    private long date;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_static_map);

        // 获取从页面传过来的值
        date = (long) getIntent().getSerializableExtra("date");

        // 地图初始化
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);

        // 获取图片
        startBD = BitmapDescriptorFactory
                .fromResource(R.drawable.ic_me_history_startpoint);
        finishBD = BitmapDescriptorFactory
                .fromResource(R.drawable.ic_me_history_finishpoint);

        new Thread(new Runnable() {
            @Override
            public void run() {
                // 将google地图的wgs84坐标转化为百度地图坐标
                coordinateConvert();

                // 设置缩放动画
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().target(target).zoom(level).build()));

                if (latLngs != null && latLngs.size() > 0) {
                    //添加起点图层
                    MarkerOptions oStart = new MarkerOptions();
                    LatLng startLatLng = null;
                    for (LatLng latLng : latLngs) {
                        if (latLng.latitude != 0 || latLng.longitude != 0) {
                            startLatLng = latLng;
                            break;
                        }
                    }
                    // 判断是否有效
                    if (startLatLng == null) {
                        return;
                    }
                    // 画图层
                    oStart.position(startLatLng);//覆盖物位置点，第一个点为起点
                    oStart.icon(startBD);//设置覆盖物图片
                    oStart.zIndex(1);//设置覆盖物Index
                    mMarkerA = (Marker) (mBaiduMap.addOverlay(oStart)); //在地图上添加此图层

                    //添加终点图层
                    LatLng endLatLng = null;
                    for (int i = latLngs.size() - 1; i >= 0; i--) {
                        LatLng latLng = latLngs.get(i);
                        if (latLng.latitude != 0 || latLng.longitude != 0) {
                            endLatLng = latLng;
                            break;
                        }
                    }
                    MarkerOptions oFinish = new MarkerOptions();
                    oFinish.position(endLatLng);
                    oFinish.icon(finishBD);
                    oFinish.zIndex(2);
                    mMarkerB = (Marker) (mBaiduMap.addOverlay(oFinish));

                    // 画线段
                    List<List<LatLng>> tempLatLngList = new ArrayList<>();
                    tempLatLngList.add(new ArrayList<LatLng>());
                    int index = 0;
                    for (int i = 0; i < latLngs.size(); i++) {
                        LatLng latLng = latLngs.get(i);
                        List<LatLng> tempLatLngs = tempLatLngList.get(index);
                        if (latLng.latitude == 0 && latLng.longitude == 0) {
                            if (tempLatLngs != null && tempLatLngs.size() > 0) {
                                tempLatLngList.add(tempLatLngs);
                                index += 1;
                                tempLatLngList.add(new ArrayList<LatLng>());
                            }
                        } else {
                            tempLatLngs.add(latLng);
                            if (i == latLngs.size() - 1) {
                                if (tempLatLngs != null && tempLatLngs.size() > 0) {
                                    tempLatLngList.add(tempLatLngs);
                                }
                            }
                        }
                    }

                    if (tempLatLngList != null && tempLatLngList.size() > 0) {
                        for (int i = 0; i < tempLatLngList.size(); i++) {
                            if (tempLatLngList.get(i) != null && tempLatLngList.get(i).size() > 1) {
                                OverlayOptions ooPolyline = new PolylineOptions().width(13).color(0xAAFF0000).points(tempLatLngList.get(i));
                                mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);
                                mPolyline.setZIndex(3 + i);
                            }
                        }
                    }

                }
            }
        }).start();

        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            public boolean onMarkerClick(final Marker marker) {

                if (marker.getZIndex() == mMarkerA.getZIndex()) {//如果是起始点图层
                    TextView textView = new TextView(getApplicationContext());
                    textView.setText("起点");
                    textView.setTextColor(Color.BLACK);
                    textView.setGravity(Gravity.CENTER);
                    textView.setBackgroundResource(R.drawable.popup);

                    //设置信息窗口点击回调
                    InfoWindow.OnInfoWindowClickListener listener = new InfoWindow.OnInfoWindowClickListener() {
                        public void onInfoWindowClick() {
                            Toast.makeText(getApplicationContext(), "这里是起点", Toast.LENGTH_SHORT).show();
                            mBaiduMap.hideInfoWindow();//隐藏信息窗口
                        }
                    };
                    LatLng latLng = marker.getPosition();//信息窗口显示的位置点
                    /**
                     * 通过传入的 bitmap descriptor 构造一个 InfoWindow
                     * bd - 展示的bitmap
                     position - InfoWindow显示的位置点
                     yOffset - 信息窗口会与图层图标重叠，设置Y轴偏移量可以解决
                     listener - 点击监听者
                     */
                    mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(textView), latLng, -47, listener);
                    mBaiduMap.showInfoWindow(mInfoWindow);//显示信息窗口

                } else if (marker.getZIndex() == mMarkerB.getZIndex()) {//如果是终点图层
                    Button button = new Button(getApplicationContext());
                    button.setText("终点");
                    button.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Toast.makeText(getApplicationContext(), "这里是终点", Toast.LENGTH_SHORT).show();
                            mBaiduMap.hideInfoWindow();
                        }
                    });
                    LatLng latLng = marker.getPosition();
                    /**
                     * 通过传入的 view 构造一个 InfoWindow, 此时只是利用该view生成一个Bitmap绘制在地图中，监听事件由自己实现。
                     view - 展示的 view
                     position - 显示的地理位置
                     yOffset - Y轴偏移量
                     */
                    mInfoWindow = new InfoWindow(button, latLng, -47);
                    mBaiduMap.showInfoWindow(mInfoWindow);
                }
                return true;
            }
        });

        mBaiduMap.setOnPolylineClickListener(new BaiduMap.OnPolylineClickListener() {
            @Override
            public boolean onPolylineClick(Polyline polyline) {
                if (polyline.getZIndex() == mPolyline.getZIndex()) {
                    Toast.makeText(getApplicationContext(), "点数：" + polyline.getPoints().size() + ",width:" + polyline.getWidth(), Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

    }

    /**
     * 将google地图的wgs84坐标转化为百度地图坐标
     */
    private void coordinateConvert() {
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
        // 从数据库中获取百度地图GPS轨迹

        healthGpsItems = LocalDataManager.getHealthGpsItemByDate(date);
        if (healthGpsItems != null && healthGpsItems.size() > 0) {
            for (int i = 0; i < healthGpsItems.size(); i++) {
                HealthGpsItem item = healthGpsItems.get(i);
                LatLng sourceLatLng = new LatLng(item.getLatitude(), item.getLongitude());
                if (item.getLatitude() != 0 || item.getLongitude() != 0) {
                    converter.coord(sourceLatLng);
                    LatLng desLatLng = converter.convert();
                    latLngs.add(desLatLng);
                }
            }
        }
        this.level = getLevel(latLngs);
        this.target = getCenterLatLng(latLngs);
    }

    /**
     * 计算中心的坐标
     *
     * @param latLngs
     * @return
     */
    private LatLng getCenterLatLng(List<LatLng> latLngs) {
        if (latLngs != null && latLngs.size() > 0) {
            LatLng[] lats = getLatLng(latLngs);
            double latitude = (lats[0].latitude + lats[1].latitude) / 2;
            double longitude = (lats[0].longitude + lats[1].longitude) / 2;
            return new LatLng(latitude, longitude);
        }
        return new LatLng(0, 0);
    }

    /**
     * 计算缩放级别
     *
     * @param latLngs
     * @return
     */
    private float getLevel(List<LatLng> latLngs) {
        float level = 18;
        if (latLngs != null && latLngs.size() > 0) {
            LatLng[] lats = getLatLng(latLngs);
            int distance = getDistance(lats);
            for (int i = 0; i < getDistances().size(); i++) {
                if (getDistances().get(i) > distance) {
                    level = 23 - i;
                    break;
                }
            }
        }
        return level;
    }

    /**
     * 距离
     *
     * @return
     */
    private List<Integer> getDistances() {
        List<Integer> dis = new ArrayList<>();
        dis.add(10);
        dis.add(20);
        dis.add(50);
        dis.add(100);
        dis.add(200);
        dis.add(500);
        dis.add(1000);
        dis.add(2000);
        dis.add(5000);
        dis.add(10000);
        dis.add(20000);
        dis.add(25000);
        dis.add(50000);
        dis.add(100000);
        dis.add(200000);
        dis.add(500000);
        dis.add(1000000);
        dis.add(2000000);
        return dis;
    }

    /**
     * 计算距离
     *
     * @param latLngs
     * @return
     */
    private int getDistance(LatLng[] latLngs) {
        return (int) DistanceUtil.getDistance(latLngs[0], latLngs[1]);
    }

    /**
     * 获取经度和纬度
     *
     * @param latLngs
     * @return
     */
    private LatLng[] getLatLng(List<LatLng> latLngs) {
        if (latLngs != null && latLngs.size() > 0) {
            LatLng startLatLng = latLngs.get(0);
            double maxLatitude = startLatLng.latitude;
            double minLatitude = startLatLng.latitude;
            double maxLongitude = startLatLng.longitude;
            double minLongitude = startLatLng.longitude;
            for (LatLng lat : latLngs) {
                if (lat.latitude > maxLatitude) {
                    maxLatitude = lat.latitude;
                }
                if (lat.latitude <= minLatitude) {
                    minLatitude = lat.latitude;
                }
                if (lat.longitude > maxLongitude) {
                    maxLongitude = lat.longitude;
                }
                if (lat.longitude <= minLongitude) {
                    minLongitude = lat.longitude;
                }
            }
            return new LatLng[]{new LatLng(maxLatitude, maxLongitude), new LatLng(minLatitude, minLongitude)};
        }
        return new LatLng[]{new LatLng(0, 0), new LatLng(0, 0)};
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
        // 为系统的方向传感器注册监听器
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.getMap().clear();
        mMapView.onDestroy();
        mMapView = null;
        startBD.recycle();
        finishBD.recycle();
        super.onDestroy();
    }
}
