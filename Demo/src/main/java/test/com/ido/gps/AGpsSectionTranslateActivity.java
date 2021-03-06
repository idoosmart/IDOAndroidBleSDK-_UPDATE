package test.com.ido.gps;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.ido.ble.BLEManager;
import com.ido.ble.gps.callback.GpsCallBack;
import com.ido.ble.gps.model.ConfigGPS;
import com.ido.ble.gps.model.ConnParam;
import com.ido.ble.gps.model.ConnParamReply;
import com.ido.ble.gps.model.ControlGps;
import com.ido.ble.gps.model.ControlGpsReply;
import com.ido.ble.gps.model.GPSInfo;
import com.ido.ble.gps.model.GpsHotStartParam;
import com.ido.ble.gps.model.GpsStatus;
import com.veryfit.multi.nativeprotocol.Protocol;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;

public class AGpsSectionTranslateActivity extends BaseAutoConnectActivity {
    private TextView tvGPSInfo, tvAGpsFilePath, aGpsTransState, tvGpsSetPara;
    private EditText etYear, etMonth, etDay, etHour, etMin, etSecond, etStartMode, etOperatorMode, etCycleMs, etGpsMode, etPRN;
    private String filePath;
    private ProgressBar progressBar;

    private EditText hotStartPara_ETtcxoOffset, hotStartPara_ETlongitude, hotStartPara_ETlatitude,hotStartPara_ETaltitude;
    private TextView hotStartPara_TVgetPara;

    public LocationClient mLocationClient ;
    private BaiduLocationListener mBaiduLocationListener = new BaiduLocationListener();
    private class BaiduLocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            double longitude = bdLocation.getLongitude();
            double latitude =  bdLocation.getLatitude();
            double altitude = bdLocation.getAltitude();

            hotStartPara_ETlongitude.setText("" + longitude);
            hotStartPara_ETlatitude.setText("" + latitude);

            if (altitude == 4.9E-324){
                hotStartPara_ETaltitude.setText("0");
            }else {
                hotStartPara_ETaltitude.setText("" + altitude);
            }
        }
    }

    /**
     * ??????
     */
    private void startLocate() {
        mLocationClient = new LocationClient(getApplicationContext());     //??????LocationClient???
        mLocationClient.registerLocationListener(mBaiduLocationListener);    //??????????????????

        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors
        );//?????????????????????????????????????????????????????????????????????????????????
//        option.setCoorType("bd09ll");//???????????????gcj02???????????????????????????????????????
        int span = 1000;
        option.setScanSpan(span);//???????????????0???????????????????????????????????????????????????????????????????????????1000ms???????????????
//        option.setIsNeedAddress(true);//?????????????????????????????????????????????????????????
        option.setOpenGps(true);//???????????????false,??????????????????gps
//        option.setLocationNotify(true);//???????????????false??????????????????GPS???????????????1S/1???????????????GPS??????
//        option.setIsNeedLocationDescribe(true);//???????????????false??????????????????????????????????????????????????????BDLocation.getLocationDescribe?????????????????????????????????????????????????????????
//        option.setIsNeedLocationPoiList(true);//???????????????false?????????????????????POI??????????????????BDLocation.getPoiList?????????
//        option.setIgnoreKillProcess(false);//???????????????true?????????SDK???????????????SERVICE?????????????????????????????????????????????stop?????????????????????????????????????????????
//        option.SetIgnoreCacheException(false);//???????????????false?????????????????????CRASH?????????????????????
//        option.setEnableSimulateGps(false);//???????????????false???????????????????????????GPS???????????????????????????
        mLocationClient.setLocOption(option);
        //????????????
        mLocationClient.start();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agps_section_translate);

        BLEManager.registerDeviceReplySetGpsCallBack(deviceReplySetGpsCallBack);
        BLEManager.registerGetGpsInfoCallBack(getGpsInfoCallBack);
        BLEManager.registerTranAgpsFileCallBack(tranAgpsFileCallBack);

        tvGPSInfo = (TextView) findViewById(R.id.gps_info_tv);
        progressBar = (ProgressBar) findViewById(R.id.agps_file_progress);
        tvAGpsFilePath = (TextView) findViewById(R.id.agps_file_path);
        aGpsTransState = (TextView) findViewById(R.id.agps_trans_complete_state);
        tvGpsSetPara = (TextView) findViewById(R.id.gps_set_para);

        etYear = (EditText) findViewById(R.id.gps_year);
        etMonth = (EditText) findViewById(R.id.gps_month);
        etDay = (EditText) findViewById(R.id.gps_day);
        etHour = (EditText) findViewById(R.id.gps_hour);
        etMin = (EditText) findViewById(R.id.gps_min);
        etSecond = (EditText) findViewById(R.id.gps_second);
        etStartMode = (EditText) findViewById(R.id.gps_start_mode);
        etOperatorMode = (EditText) findViewById(R.id.gps_operator_mode);
        etCycleMs = (EditText) findViewById(R.id.gps_cycle_ms);
        etGpsMode = (EditText) findViewById(R.id.gps_gps_mode);

        etConnMode = (EditText) findViewById(R.id.agps_connect_mode_et);
        etConnInterval = (EditText) findViewById(R.id.agps_connect_interval_et);
        tvConnResult = (TextView) findViewById(R.id.apgs_connect_mode_result_tv);
        etPRN = (EditText) findViewById(R.id.agps_trans_set_prn_et);

        etControlOrQueryOprate = (EditText) findViewById(R.id.agps_control_or_query_oprate_et);
        etControlOrQueryType = (EditText) findViewById(R.id.agps_control_type_et);
        tvControlOrQueryResult = (TextView) findViewById(R.id.agps_control_or_query_result_tv);

        tvAGpsFilePath.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                selectAGpsFile();
                return true;
            }
        });

        hotStartPara_ETtcxoOffset = (EditText) findViewById(R.id.tcxo_offset_et);
        hotStartPara_ETlongitude = (EditText) findViewById(R.id.longitude_et);
        hotStartPara_ETlatitude = (EditText) findViewById(R.id.latitude_et);
        hotStartPara_ETaltitude = (EditText) findViewById(R.id.altitude_et);
        hotStartPara_TVgetPara = (TextView) findViewById(R.id.get_gps_hot_para_tv);

        startLocate();
    }

    public void setGpsHotStartPara(View view){
        GpsHotStartParam hotStartParam = new GpsHotStartParam();
        hotStartParam.setTcxo_offset(Integer.parseInt(hotStartPara_ETtcxoOffset.getText().toString()));
        hotStartParam.setLongitude(Double.parseDouble(hotStartPara_ETlongitude.getText().toString()));
        hotStartParam.setLatitude(Double.parseDouble(hotStartPara_ETlatitude.getText().toString()));
        hotStartParam.setAltitude(Double.parseDouble(hotStartPara_ETaltitude.getText().toString()));
        BLEManager.setGpsHotPara(hotStartParam);
    }

    public void getGpsHotStartPara(View view){
        BLEManager.getGpsHotPara();
    }

    private void selectAGpsFile() {
        openFileChooser();
    }


    public void getGPSInfo(View view) {
        BLEManager.getGpsInfo();
    }

    public void setGPSConfig(View view) {
        ConfigGPS configGPS = new ConfigGPS();

        configGPS.startMode = Integer.parseInt(etStartMode.getText().toString());
        configGPS.operationMode = Integer.parseInt(etOperatorMode.getText().toString());
        configGPS.cycleMS = Integer.parseInt(etCycleMs.getText().toString());
        configGPS.gnsValue = Integer.parseInt(etGpsMode.getText().toString());

        tvGpsSetPara.setText(configGPS.toString());
        BLEManager.setGpsParas(configGPS);
    }

    public void startTransAGpsFile(View view) {
        Protocol.getInstance().tranDataSetPRN(Integer.parseInt(etPRN.getText().toString()));


        byte[] aGpsData = getBytes(filePath);
        if (aGpsData != null && aGpsData.length > 0) {

        } else {
            Log.e("AGPS", "aGpsData is null");
        }

        Protocol.getInstance().tranDataStart();
    }

    public void stopTransAGpsFile(View view) {
        Protocol.getInstance().tranDataStop();
    }

    public void continueTransAGpsFile(View view) {
        Protocol.getInstance().tranDataContinue();
    }

    /**
     * ????????????????????????aGps??????
     *
     */
//    public void checkAGpsUpdate(View view) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                // ?????????????????????
//                if (!NetWorkUtil.isNetWorkConnected(getApplicationContext())) {
//                    return;
//                }
//
//                OkHttpUtil.getInstance().checkAgpsFile(new IOkHttpCallBack<String>() {
//                    @Override
//                    public void success(String s) {
//                        String dir = Environment.getExternalStorageDirectory() + File.separator + "veryfit2.2" + File.separator + "agps";
//                        String fileNameList = "cep_pak.bin";
//                        File file = new File(dir, fileNameList);
//                        String checkCode = AppSharedPreferencesUtils.getInstance().getApsCheckCode();
//                        if (!TextUtils.isEmpty(s) && s.equals(checkCode) && file.exists()) {
//                            showMsg("??????AGPS???????????????");
//                            return;
//                        }
//
//                        // ???????????????
//                        AppSharedPreferencesUtils.getInstance().setApsCheckCode(s);
//                        // ??????Agps??????
//                        OkHttpUtil.getInstance().downLoadAgpsFile(dir, fileNameList, new IOkHttpCallBack<String>() {
//                            @Override
//                            public void success(String s) {
//                                showMsg("??????Agps???????????? , " + s);
//                            }
//
//                            @Override
//                            public void fail(AGException e) {
//                                showMsg("??????Agps???????????? , " + e.getMessage());
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void fail(AGException e) {
//                        showMsg("??????AGPS???????????? , " + e.getMessage());
//                    }
//                });
//            }
//        }).start();
//    }

    private void showMsg(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * ??????/??????GPs
     *
     * @param view
     */
    private EditText etControlOrQueryOprate, etControlOrQueryType;
    private TextView tvControlOrQueryResult;

    public void controlOrQueryGps(View view) {
        tvControlOrQueryResult.setText("");
        ControlGps controlGps = new ControlGps();
        controlGps.operate = Integer.parseInt(etControlOrQueryOprate.getText().toString());
        controlGps.type = Integer.parseInt(etControlOrQueryType.getText().toString());

        tvControlOrQueryResult.append(controlGps.toString());
        BLEManager.setControlGps(controlGps);
    }

    /**
     * ????????????
     *
     * @param view
     */
    private EditText etConnMode, etConnInterval;
    private TextView tvConnResult;

    public void connectMode(View view) {
        tvConnResult.setText("");
        ConnParam connParam = new ConnParam();
        connParam.mode = Integer.parseInt(etConnMode.getText().toString());
        connParam.modifyConnParam = Integer.parseInt(etConnInterval.getText().toString());

        tvConnResult.append(connParam.toString());
        BLEManager.setConnParam(connParam);
    }

    @Override
    protected void onDestroy() {
        BLEManager.unregisterDeviceReplySetGpsCallBack(deviceReplySetGpsCallBack);
        BLEManager.unregisterGetGpsInfoCallBack(getGpsInfoCallBack);
        BLEManager.unregisterTranAgpsFileCallBack(tranAgpsFileCallBack);
        super.onDestroy();
        mLocationClient.unRegisterLocationListener(mBaiduLocationListener);
        mLocationClient.stop();
    }

    private GpsCallBack.IDeviceReplySetGpsCallBack deviceReplySetGpsCallBack = new GpsCallBack.IDeviceReplySetGpsCallBack() {
        @Override
        public void onSetHotStartGpsPara(boolean isSuccess) {
            String msg = isSuccess ? "???????????????????????????":"???????????????????????????";
            Toast.makeText(AGpsSectionTranslateActivity.this, msg, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onSetConnParam(ConnParamReply connParamReply) {
            tvConnResult.append("\n??????:\n");
            if (connParamReply != null) {
                tvConnResult.append(connParamReply.toString());
            } else {
                tvConnResult.append("????????????");
            }
        }

        @Override
        public void onControlGps(ControlGpsReply controlGpsReply) {
            tvControlOrQueryResult.append("\n??????:\n");
            if (controlGpsReply != null) {
                tvControlOrQueryResult.append(controlGpsReply.toString());
            } else {
                tvControlOrQueryResult.append("????????????");
            }
        }

        @Override
        public void onSetConfigGps(boolean isSuccess) {
            if (isSuccess) {
                Toast.makeText(AGpsSectionTranslateActivity.this, "??????GPS????????????", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(AGpsSectionTranslateActivity.this, "??????GPS????????????", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private GpsCallBack.IGetGpsInfoCallBack getGpsInfoCallBack = new GpsCallBack.IGetGpsInfoCallBack() {
        @Override
        public void onGetGpsInfo(GPSInfo gpsInfo) {
            if (gpsInfo != null) {
                tvGPSInfo.setText(gpsInfo.toString());
            } else {
                tvGPSInfo.setText("????????????");
            }
        }

        @Override
        public void onGetHotStartGpsPara(GpsHotStartParam param) {
            if (param != null) {
                hotStartPara_TVgetPara.setText(param.toString());
            }else {
                hotStartPara_TVgetPara.setText("get failed");
            }
        }

        @Override
        public void onGetGpsStatus(GpsStatus gpsStatus) {

        }
    };

    private GpsCallBack.ITranAgpsFileCallBack tranAgpsFileCallBack = new GpsCallBack.ITranAgpsFileCallBack() {
        @Override
        public void onProgress(int progress) {
            progressBar.setProgress(progress);
            aGpsTransState.setText("" + progress);
        }

        @Override
        public void onFinish() {
            aGpsTransState.setText("onFinish");
        }

        @Override
        public void onFailed(int error) {
            aGpsTransState.setText("error = " + error);
        }

        @Override
        public void onFailed(int error, Object value) {

        }
    };


    /**
     * ?????????????????????byte??????
     */
    public static byte[] getBytes(String filePath) {
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream((int) file.length());
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    private static final int SELECT_FILE_REQ = 1;

    private void openFileChooser() {
        final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/bin");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            // file browser has been found on the device
            startActivityForResult(intent, SELECT_FILE_REQ);
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {
            case SELECT_FILE_REQ: {
                // and read new one
                final Uri uri = data.getData();
            /*
             * The URI returned from application may be in 'file' or 'content' schema. 'File' schema allows us to create a File object and read details from if
			 * directly. Data from 'Content' schema must be read by Content Provider. To do that we are using a Loader.
			 */
                if (uri.getScheme().equals("file")) {
                    // the direct path to the file has been returned
                    final String path = uri.getPath();
                    filePath = path;
                    tvAGpsFilePath.setText(path);
                }
            }

        }
    }

}
