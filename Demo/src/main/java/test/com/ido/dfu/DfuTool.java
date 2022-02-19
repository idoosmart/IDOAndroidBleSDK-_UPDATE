package test.com.ido.dfu;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.ido.ble.BLEManager;
import com.ido.ble.LocalDataManager;
import com.ido.ble.dfu.BleDFUConfig;
import com.ido.ble.dfu.BleDFUState;
import com.ido.ble.protocol.model.BasicInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class DfuTool {
    private static Handler handler = new Handler(Looper.getMainLooper());
    public static void startDFU(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                NewVersionCheckUtil.NewVersionResponse response = NewVersionCheckUtil.getLatestInfo();
                if (response == null || response.data == null) {
                    return;
                }

                Log.i(NewVersionCheckUtil.LOG_TAG, "get info success. " + response.toString());
                int deviceVersion = LocalDataManager.getBasicInfo().firmwareVersion;
                if (response.data.version <= deviceVersion){
                    return;
                }

                StringBuilder builder = new StringBuilder();
                final String dirPath = builder.append(Environment.getExternalStorageDirectory().getAbsolutePath()).
                        append(File.separator).
                        append("IDO_SDK_DEMO").
                        append(File.separator).
                        append("dfu").toString();
                final String fileName = "firmware.zip";
                if (!NewVersionCheckUtil.downloadFirmwarePackage(response.data.url, dirPath, fileName)){
                    return;
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        realDFU(dirPath + File.separator + fileName);
                    }
                });
            }
        }).start();



    }

    private static void realDFU(String filePath){
        BasicInfo basicInfo = LocalDataManager.getBasicInfo();
        BleDFUConfig bleDFUConfig = new BleDFUConfig();
        bleDFUConfig.setDeviceId(basicInfo.deivceId +"");
        bleDFUConfig.setFilePath(filePath);
        bleDFUConfig.setMacAddress(LocalDataManager.getLastConnectedDeviceInfo().mDeviceAddress);

        BLEManager.addDFUStateListener(new BleDFUState.IListener() {
            @Override
            public void onPrepare() {

            }

            @Override
            public void onDeviceInDFUMode() {

            }

            @Override
            public void onProgress(int progress) {

            }

            @Override
            public void onSuccess() {

            }

            @Override
            public void onSuccessAndNeedToPromptUser() {

            }

            @Override
            public void onFailed(BleDFUState.FailReason failReason) {

            }

            @Override
            public void onCanceled() {

            }

            @Override
            public void onRetry(int count) {

            }
        });
        BLEManager.startDFU(bleDFUConfig);
    }



    private static class NewVersionCheckUtil{
        public static final String LOG_TAG = "NewVersionCheckManager";
        private static final String URL = "http://veryfitproapi.veryfitplus.com/firmware/getLatestV2";

        /**
         * {"firmwareId": 6753,
         * "appVersionCode": 22,
         * "version": 39,
         * "os": 1,
         * "age": 2,
         * "gender": 1,
         * "mac": "C5:7D:03:B1:10:D7",
         * "mobileBrand": "phone",
         * "phoneModel": "苹果"
         * }
         */
        public static NewVersionResponse test(){
            BasicInfo basicInfo = LocalDataManager.getBasicInfo();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("firmwareId", "6753");
            jsonObject.put("version", "1");
            jsonObject.put("os",1);
            jsonObject.put("age",0);
            jsonObject.put("gender",0);
            jsonObject.put("mac","C5:7D:03:B1:10:D7");
            jsonObject.put("mobileBrand","");
            jsonObject.put("phoneModel","");
            Log.e(LOG_TAG, jsonObject.toJSONString());
            return request(jsonObject.toJSONString());
        }
        public static NewVersionResponse getLatestInfo(){
            BasicInfo basicInfo = LocalDataManager.getBasicInfo();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("firmwareId", basicInfo.deivceId);
            jsonObject.put("version", "1");
            jsonObject.put("os",1);
            jsonObject.put("age",0);
            jsonObject.put("gender",0);
            jsonObject.put("mac","");
            jsonObject.put("mobileBrand","");
            jsonObject.put("phoneModel","");
            Log.e(LOG_TAG, jsonObject.toJSONString());
            return request(jsonObject.toJSONString());
        }
        private static NewVersionResponse request(String jsonBody){
            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(JSON, jsonBody);

            OkHttpClient client = new OkHttpClient();

            try {
                Request request = new Request.Builder().
                        url(URL).
                        addHeader("content-type", "application/json").
                        post(body).
                        build();
                Call call = client.newCall(request);
                Response response = call.execute();
                if (response.isSuccessful()) {
                    String result =response.body().string();
                    return parse(result);
                }else{
                    Log.e(LOG_TAG, response.networkResponse().toString());
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, e.toString());
            }

            return null;
        }

        private static NewVersionResponse parse(String jsonResult){
            Log.i(LOG_TAG, jsonResult);
            try {
                return new Gson().fromJson(jsonResult, NewVersionResponse.class);
            }catch (Exception e){
                Log.e(LOG_TAG, e.toString());
            }

            return null;
        }


        public static class NewVersionResponse {
            public int resultCode;
            public String message;
            public NewVersionResponse.NewVersionInfo data;

            public static class NewVersionInfo{
                public String url;
                public boolean forceUpdate;
                public String descriptionChinese;
                public String descriptionEnglish;
                public boolean specialUpgrade;
                public int version;

                @Override
                public String toString() {
                    return "NewVersionInfo{" +
                            "url='" + url + '\'' +
                            ", forceUpdate=" + forceUpdate +
                            ", descriptionChinese='" + descriptionChinese + '\'' +
                            ", descriptionEnglish='" + descriptionEnglish + '\'' +
                            ", version=" + version +
                            '}';
                }
            }

            @Override
            public String toString() {
                return "NewVersionResponse{" +
                        "resultCode=" + resultCode +
                        ", message='" + message + '\'' +
                        ", data=" + data +
                        '}';
            }
        }


        public static boolean downloadFirmwarePackage(String downloadUrl, String fileDir, String fileName){
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
//                    .addHeader("Accept-Encoding", "gzip")
                    .url(downloadUrl)
                    .build();
            try {
                String path = fileDir  + File.separator+ fileName;
                File dir = new File(fileDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                Log.i(LOG_TAG, "download start");

                Call call = client.newCall(request);
                Response response = call.execute();
                if (response.isSuccessful()) {
                    ResponseBody body = response.body();
                    InputStream is = body.byteStream();
                    Log.i(LOG_TAG, "size=" + body.contentLength());
                    OutputStream fos = new FileOutputStream(path);
                    int len;
                    byte[] b = new byte[2048];
                    long sum = 0;
                    while ((len = is.read(b)) != -1)
                    {
                        sum += len;
                        Log.i(LOG_TAG, sum +"");
                        fos.write(b, 0, len);
                    }
                    fos.flush();
                    fos.close();
                    Log.i(LOG_TAG, "download success");
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(LOG_TAG, e.getMessage());
            }

            return false;
        }
    }
}
