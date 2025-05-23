package test.com.ido.set;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.stetho.common.LogUtil;
import com.ido.ble.BLEManager;
import com.ido.ble.LocalDataManager;
import com.ido.ble.file.transfer.FileTransferConfig;
import com.ido.ble.file.transfer.IFileTransferListener;
import com.ido.ble.watch.custom.WatchPlateSetConfig;
import com.ido.ble.watch.custom.callback.WatchPlateCallBack;
import com.ido.ble.watch.custom.model.DialPlateParam;
import com.ido.ble.watch.custom.model.WatchPlateFileInfo;
import com.ido.ble.watch.custom.model.WatchPlateScreenInfo;
import com.ido.jielidial.JieliDialMaker;
import com.ido.jielidial.model.JieliDialConfig;
import com.ido.life.constants.WallpaperDialConstants;
import com.ido.life.util.BackgroundType;
import com.ido.life.util.WallpaperDialManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import test.com.ido.CallBack.BaseDialOperateCallback;
import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;
import test.com.ido.localdata.Wallpaper;
import test.com.ido.log.LogPathImpl;
import test.com.ido.model.CwdAppBean;
import test.com.ido.model.CwdDialConfigBean;
import test.com.ido.model.CwdIwfBean;
import test.com.ido.model.DialDataBean;
import test.com.ido.model.IwfItemType;
import test.com.ido.utils.BitmapUtil;
import test.com.ido.utils.DataUtils;
import test.com.ido.utils.FileUtil;
import test.com.ido.utils.GetFilePathFromUri;
import test.com.ido.utils.GsonUtil;
import test.com.ido.utils.ImageUtil;
import test.com.ido.utils.ListUtils;
import test.com.ido.utils.OnItemClickListener;
import test.com.ido.utils.ResourceUtil;
import test.com.ido.utils.ZipUtils;

public class WatchPlateActivity extends BaseAutoConnectActivity {
    private static final String TAG = "Davy";
    private String filePath;
    private TextView tvFilePath, tvState, tvOperateTv, tvUniqueID, tvPhoto, tv_progress, tvColor2;
    private EditText et_cw_file, et_cw_img, et_cw_color;
    private ImageView mIvDialTime, iv_cw, mIvDialFunction;
    private RecyclerView color1_rv, color2_rv;
    private RecyclerView rv_time_location, rv_func;
    private LinearLayout llDialWidget;
    private ProgressBar progress_bar;
    private FrameLayout fl_cw;
    WatchPlateScreenInfo mScreenInfo;
    private CwdAppBean app;
    private CwdIwfBean iwf;
    private CwdDialConfigBean dial_config;

    private List<String> colorList = new ArrayList<>();
    private int selectedTimeColorIndex, selectedFunctionColorIndex;
    private ColorAdapter color1Adapter, color2Adapter;
    private FunctionAdapter funcAdapter;
    private int selectedLocation;
    private List<Integer> mLocationValues = new ArrayList<>();
    private TimeLocationAdapter mTimeLocationAdapter;
    private int[] mWidgetRules = new int[]{RelativeLayout.ALIGN_END};
    private int mFunction = WallpaperDialConstants.WidgetFunction.WEEK_DATE;

    private boolean mFunctionShow = true;
    private boolean isSupportFunctionSet;


    private List<CwdAppBean.Function> functions = new ArrayList<>();
    private String packTempCwdPackage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_plate);
        BLEManager.getBasicInfo();

        llDialWidget = findViewById(R.id.ll_dial_widget);
        rv_time_location = findViewById(R.id.rv_time_location);
        rv_func = findViewById(R.id.rv_func);
        tv_progress = findViewById(R.id.tv_progress);
        progress_bar = findViewById(R.id.progress_bar);
        fl_cw = findViewById(R.id.fl_cw);
        color1_rv = findViewById(R.id.recyclerview);
        color2_rv = findViewById(R.id.color2_rv);
        mIvDialTime = findViewById(R.id.iv_dial_time);
        mIvDialFunction = findViewById(R.id.iv_dial_function);
        iv_cw = findViewById(R.id.iv_cw);
        et_cw_color = findViewById(R.id.et_cw_color);
        et_cw_file = findViewById(R.id.et_cw_file);
        et_cw_img = findViewById(R.id.et_cw_img);
        tvFilePath = findViewById(R.id.file_path_tv);
        tvState = findViewById(R.id.state_tv);
        tvOperateTv = findViewById(R.id.operate_info_tv);
        tvUniqueID = findViewById(R.id.unique_id_tv);
        tvPhoto = findViewById(R.id.photo_tv);
        tvColor2 = findViewById(R.id.tvColor2);


        BLEManager.getFunctionTables();
        BLEManager.registerWatchOperateCallBack(iOperateCallBack);
        //根据Mac地址区分不同设备的表盘存储文件路径
        cwDir = getFilesDir().getPath() + "/wallpaper/" + BLEManager.getBtMacAddress() + "/";
        Log.d(TAG, "onCreate: " + WallpaperDialManager.getDeviceMac());
        BLEManager.getWatchPlateScreenInfo();
        filePath = DataUtils.getInstance().getFilePath();
        if (TextUtils.isEmpty(filePath)) {
            tvFilePath.setText("please select watch file(.zip)");
        } else {
            tvFilePath.setText(filePath);
        }
        initColorView();
        initTimeLocationView();
        initFuncView();
    }


    private void sortTimeFunctionView() {
        llDialWidget.removeView(mIvDialTime);
        if (selectedLocation == WallpaperDialConstants.WidgetLocation.CENTER) {
            llDialWidget.addView(mIvDialTime, 0);
        } else {
            llDialWidget.addView(mIvDialTime);
        }
    }


    /**
     * 初始化颜色选择列表
     */
    private void initColorView() {
        color1Adapter = new ColorAdapter();
        color1Adapter.onItemClickListener = position -> {
            selectedTimeColorIndex = position;
            color1Adapter.selectedColorIndex = selectedTimeColorIndex;
            color1Adapter.notifyDataSetChanged();
            updateColor();
        };
        color1_rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        color1_rv.setAdapter(color1Adapter);

        color2Adapter = new ColorAdapter();
        color2Adapter.onItemClickListener = position -> {
            selectedFunctionColorIndex = position;
            color2Adapter.selectedColorIndex = selectedFunctionColorIndex;
            color2Adapter.notifyDataSetChanged();
            updateColor();
        };
        color2_rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        color2_rv.setAdapter(color2Adapter);
    }

    private void initFuncView() {
        funcAdapter = new FunctionAdapter();
        LinearLayoutManager lm = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv_func.setLayoutManager(lm);
        rv_func.setAdapter(funcAdapter);
    }


    private class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.VH> {
        OnItemClickListener onItemClickListener;
        int selectedColorIndex;

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_color, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            holder.view.setBackgroundColor(Color.parseColor(colorList.get(position)));
            holder.ivSelector.setVisibility(position == selectedColorIndex ? View.VISIBLE : View.GONE);
        }

        @Override
        public int getItemCount() {
            return colorList.size();
        }

        class VH extends RecyclerView.ViewHolder {
            View view;
            ImageView ivSelector;

            public VH(@NonNull View itemView) {
                super(itemView);
                view = itemView.findViewById(R.id.vColor);
                ivSelector = itemView.findViewById(R.id.ivColorSelector);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onItemClickListener != null) {
                            onItemClickListener.onItemClick(getAdapterPosition());
                        }
                    }
                });
            }
        }
    }

    private class FunctionAdapter extends RecyclerView.Adapter<FunctionAdapter.VH> {
        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_func, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            holder.tvFun.setText(functions.get(position).getName());
            holder.ivSelector.setVisibility(functions.get(position).getFunction() == mFunction ? View.VISIBLE : View.GONE);
        }

        @Override
        public int getItemCount() {
            return functions.size();
        }

        class VH extends RecyclerView.ViewHolder {
            TextView tvFun;
            ImageView ivSelector;

            public VH(@NonNull View itemView) {
                super(itemView);
                tvFun = itemView.findViewById(R.id.tvFun);
                ivSelector = itemView.findViewById(R.id.ivColorSelector);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mFunction = functions.get(getAdapterPosition()).getFunction();
                        funcAdapter.notifyDataSetChanged();
                        updateWidgetType();
                    }
                });
            }
        }
    }

    private void initTimeLocationView() {
        mTimeLocationAdapter = new TimeLocationAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv_time_location.setLayoutManager(layoutManager);
        rv_time_location.setAdapter(mTimeLocationAdapter);
    }

    private class TimeLocationAdapter extends RecyclerView.Adapter<TimeLocationAdapter.VH> {
        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dial_time_location, parent, false));
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            Drawable bg = ResourceUtil.getDrawable(R.drawable.bg_e2e3ea_10_corner);
            holder.ivWallpaperDial.setBackground(bg);
            Drawable drawable = ResourceUtil.getDrawable(R.drawable.bg_usage_dial);
            DrawableCompat.setTint(drawable, mLocationValues.get(position) == selectedLocation ? ResourceUtil.getColor(R.color.color_FF4A00) : ResourceUtil.getColor(R.color.translate));
            holder.lay_location.setBackground(drawable);
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) holder.tv_time.getLayoutParams();
            lp.gravity = WallpaperDialManager.getGravityByLocation(mLocationValues.get(position));
            holder.tv_time.setLayoutParams(lp);
            if (mLocationValues.get(position) == WallpaperDialConstants.WidgetLocation.CENTER) {
                holder.tv_time.setText("10\n08");
            } else {
                holder.tv_time.setText("10:08");
            }
        }

        @Override
        public int getItemCount() {
            return mLocationValues.size();
        }

        private class VH extends RecyclerView.ViewHolder {
            FrameLayout lay_location;
            TextView tv_time;
            View ivWallpaperDial;

            public VH(@NonNull View itemView) {
                super(itemView);
                lay_location = itemView.findViewById(R.id.lay_location);
                tv_time = itemView.findViewById(R.id.tv_time);
                ivWallpaperDial = itemView.findViewById(R.id.ivWallpaperDial);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (selectedLocation != mLocationValues.get(getAdapterPosition())) {
                            selectedLocation = mLocationValues.get(getAdapterPosition());
                            updateWidget();
                        }
                    }
                });
            }
        }
    }

    private void updateWidget() {
        if (mTimeLocationAdapter != null) {
            mTimeLocationAdapter.notifyDataSetChanged();
        }
        changeDialWidgetLocation(selectedLocation);
        if (isSupportFunctionSet) {
            updateWidgetType();
        }
    }

    private void updateWidgetType() {
        sortTimeFunctionView();
        mIvDialFunction.setVisibility(mFunctionShow ? View.VISIBLE : View.GONE);
        mIvDialFunction.setImageResource(WallpaperDialManager.getFunctionIcon(mFunction));

        int timeResId = selectedLocation == WallpaperDialConstants.WidgetLocation.CENTER ? R.mipmap.icon_wallpager_dial_time_center : R.mipmap.icon_wallpaper_dial_time;
        mIvDialTime.setImageResource(timeResId);
    }

    /**
     * 设置表盘控件位置
     */
    private void changeDialWidgetLocation(@WallpaperDialConstants.WidgetLocation int location) {
        int gravity = WallpaperDialManager.getLayoutGravityByLocation(location);
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) llDialWidget.getLayoutParams();
        Log.e("Davy", "changeDialWidgetLocation: " + location + ", gravity: " + gravity);
        lp.gravity = gravity;
        llDialWidget.setLayoutParams(lp);
        int childGravity = Gravity.CENTER;
        if (location == WallpaperDialConstants.WidgetLocation.LEFT_BOTTOM || location == WallpaperDialConstants.WidgetLocation.LEFT_TOP || location == WallpaperDialConstants.WidgetLocation.LEFT_CENTER) {
            childGravity = Gravity.LEFT;
        } else if (location == WallpaperDialConstants.WidgetLocation.RIGHT_BOTTOM || location == WallpaperDialConstants.WidgetLocation.RIGHT_TOP || location == WallpaperDialConstants.WidgetLocation.RIGHT_CENTER) {
            childGravity = Gravity.RIGHT;
        }
        llDialWidget.setGravity(childGravity);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BLEManager.unregisterWatchOperateCallBack(iOperateCallBack);
    }

    /**
     * 打开文件管理选择文件
     */
    public void selectFile(View view) {
        openFileChooser(SELECT_FILE_REQ);
    }

    public void startSet(View view) {
        WatchPlateSetConfig config = new WatchPlateSetConfig();
        File file = new File(filePath);
        config.uniqueID = file.getName().replace(".zip", "");
        config.filePath = filePath;
        config.isOnlyTranslateWatchFile = false;
        config.stateListener = new WatchPlateCallBack.IAutoSetPlateCallBack() {
            @Override
            public void onStart() {
                tvState.setText("start");
            }

            @Override
            public void onProgress(int progress) {
                tvState.setText("progress = " + progress);
            }

            @Override
            public void onSuccess() {
                tvState.setText("success");
            }

            @Override
            public void onFailed() {
                tvState.setText("failed");
            }
        };
        BLEManager.startSetPlateFileToWatch(config);
    }


    private static final int SELECT_FILE_REQ = 1;
    private static final int SELECT_IMAGE_REQ = 2;
    private String photoInputPath = "";
    private String photoInputPath_crop = LogPathImpl.getInstance().getPicPath() + "input.png";

    /**
     * 打开文件管理选择文件
     */
    private void openFileChooser(int code) {
        final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/zip");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            // file browser has been found on the device
            startActivityForResult(intent, code);
        }
    }

    /**
     * 打开文件管理选择照片
     */
    private void openImageChooser(int code) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"),
                    code);
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, code);
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case SELECT_FILE_REQ:
                // and read new one
                final Uri uri = data.getData();
                /*
                 * The URI returned from application may be in 'file' or 'content' schema. 'File' schema allows us to create a File object and read details from if
                 * directly. Data from 'Content' schema must be read by Content Provider. To do that we are using a Loader.
                 */

                if (uri.getScheme().equals("file") || uri.getScheme().equals("content")) {
                    // the direct path to the file has been returned
                    //  final String path = uri.getPath();
                    String path = GetFilePathFromUri.getFileAbsolutePath(this, uri);
                    filePath = path;
                    tvFilePath.setText(path);
                    DataUtils.getInstance().saveFilePath(path);
                }
                break;
            case SELECT_CW_FILE_REQ:
                // and read new one
                processCwFile(data);
                break;
            case SELECT_CW_IMAGE_REQ:
                processCwImage(data);
                break;
            case SELECT_IMAGE_REQ:
                final Uri image_url = data.getData();
                BLEManager.getWatchPlateScreenInfo();//获取屏幕信息
                photoInputPath = GetFilePathFromUri.getFileAbsolutePath(this, image_url);
                tvPhoto.setText(photoInputPath);
                Bitmap wallPaper = BitmapFactory.decodeFile(photoInputPath);
                if (mScreenInfo != null && mScreenInfo.width > 0) {//Crop the watch screen size picture
                    ImageUtil.saveWallPaper(Bitmap.createScaledBitmap(wallPaper, mScreenInfo.width, mScreenInfo.height, true), photoInputPath_crop);
                } else {
                    Toast.makeText(this, "please CALL BLEManager.getWatchPlateScreenInfo();", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    WatchPlateCallBack.IOperateCallBack iOperateCallBack = new WatchPlateCallBack.IOperateCallBack() {
//        @Override
//        public void onGetPlateList(List<String> uniqueIDList) {
//            tvOperateTv.setText(GsonUtil.toJson(uniqueIDList));
//        }

        @Override
        public void onGetPlateFileInfo(WatchPlateFileInfo watchPlateFileInfo) {
            if (watchPlateFileInfo == null || watchPlateFileInfo.fileNameList == null) {
                return;
            }

            Log.d(TAG, "onGetPlateFileInfo: " + watchPlateFileInfo);
            tvOperateTv.setText(watchPlateFileInfo.toString());
        }

        @Override
        public void onGetScreenInfo(WatchPlateScreenInfo screenInfo) {
            tvOperateTv.setText(GsonUtil.toJson(screenInfo));
            mScreenInfo = screenInfo;
        }

        @Override
        public void onGetCurrentPlate(String uniqueID) {
            tvOperateTv.setText("当前使用的表盘：" + uniqueID);
        }

        @Override
        public void onSetPlate(boolean isSuccess) {
            tvOperateTv.setText(isSuccess ? "设置成功" : "设置失败");
        }

        @Override
        public void onDeletePlate(boolean isSuccess) {
            tvOperateTv.setText(isSuccess ? "删除成功" : "删除失败");
        }

        @Override
        public void onGetDialPlateParam(DialPlateParam dialPlateParam) {

        }
    };

    public void getPlateList(View view) {
        BLEManager.getWatchPlateList();
        if (LocalDataManager.getSupportFunctionInfo().V3_get_watch_list_new) {  //根据功能表盘判断，调用不同的方法
            BLEManager.getDialPlateParam();
        } else {
            BLEManager.getWatchPlateList();
        }
    }

    public void getScreenInfo(View view) {
        BLEManager.getWatchPlateScreenInfo();
    }

    public void getCurrentPlate(View view) {
        BLEManager.getCurrentWatchPlate();
    }


    public void deletePlate(View view) {
        //the name must contain .iwf ,example "watch.iwf"
        BLEManager.deleteWatchPlate(tvUniqueID.getText().toString());
    }


    public void setPlate(View view) {
        BLEManager.setWatchPlate(tvUniqueID.getText().toString());
    }

    public void selectImage(View view) {
        openImageChooser(SELECT_IMAGE_REQ);
    }

    /**
     * 设置壁纸表盘
     */
    public void setPhotoWatch(View view) {
        if (TextUtils.isEmpty(photoInputPath_crop)) {
            Toast.makeText(this, "please select imamge", Toast.LENGTH_LONG).show();
            return;
        }
        String outfile = LogPathImpl.getInstance().getPicPath() + "output.png";
        Wallpaper wallpaper = new Wallpaper();
        wallpaper.setFileName(photoInputPath_crop);// 裁剪的图片存储的地方
        wallpaper.setSaveFileName(outfile);
        wallpaper.setFormat(5);
        String json = GsonUtil.toJson(wallpaper);
        BLEManager.setParaToDeviceByTypeAndJson(5500, json);//请求制作照片
        Log.e("ddd", outfile);
        //制作完成，传输壁纸文件到设备
        FileTransferConfig config1 = FileTransferConfig.getDefaultTransPictureConfig(outfile + ".lz", new IFileTransferListener() {
            @Override
            public void onStart() {
                Log.e(TAG, "开始传输");
            }

            @Override
            public void onProgress(int progress) {
                Log.e(TAG, progress + "watch file");
            }

            @Override
            public void onSuccess() {
                Log.e(TAG, "success");
            }

            @Override
            public void onFailed(String errorMsg) {
                Log.e(TAG, "onFailed: " + errorMsg);
            }
        });
        BLEManager.startTranCommonFile(config1);
    }

    private static final int SELECT_CW_FILE_REQ = 11;
    private static final int SELECT_CW_IMAGE_REQ = 21;
    private String cwFilePath;
    private String cwImgPath;
    private String cwImgPath_crop = LogPathImpl.getInstance().getPicPath() + "cw_input.png";
    private String cwImgPath_show = LogPathImpl.getInstance().getPicPath() + System.currentTimeMillis() + "_cw_show.png";
    private String cwName = "custom4";
    private String cwDir;
    private String cwTmpDir;
    private String cwDialDir;

    public void selectCwFile(View view) {
        openFileChooser(SELECT_CW_FILE_REQ);
    }

    public void transTepm(View view) {


    }

    /**
     * 获取选择的表盘文件
     */
    private void processCwFile(Intent data) {
        final Uri cw_uri = data.getData();
        initConfig();
        if (cw_uri.getScheme().equals("file") || cw_uri.getScheme().equals("content")) {
            String path = GetFilePathFromUri.getFileAbsolutePath(this, cw_uri);
            cwFilePath = path;
            et_cw_file.setText(path);
            cwName = FileUtil.getNoSuffixFileNameFromPath(cwFilePath);

            cwDialDir = cwDir + cwName + "/";
            cwTmpDir = cwDialDir + "tmp/";

            try {
                File file = new File(cwDialDir);
                if (file.exists()) {
                    FileUtil.deleteDirectory(file);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            //解压zip文件
            ZipUtils.unpackCopyZip(cwDir, cwFilePath);
            //xxx/wallpaper/custom1/custom.zip
            ZipUtils.unpackCopyZip(cwTmpDir, cwDialDir + cwName + ".zip");

            Log.d(TAG, "processCwFile: cwDialDir：" + cwDialDir);
            Log.d(TAG, "processCwFile: cwTmpDir：" + cwTmpDir);
            Log.d(TAG, "processCwFile: cwName：" + cwName);
            Log.d(TAG, "processCwFile: cwDialDir + cwName + \".zip\"：" + cwDialDir + cwName + ".zip");
            Log.d(TAG, "processCwFile: cwDir：" + cwDir);
            Log.d(TAG, "processCwFile: cwFilePath：" + cwFilePath);


            app = getCwdAppBean();
            if (isSiCheDevice()) {
                dial_config = getTempCwdDialConfigBean();
            } else {
                iwf = getCwdIwfBean();
            }
            Log.d(TAG, "processCwFile: " + dial_config + "   " + getTempCwdDialConfigBean());
            Log.d(TAG, "processCwFile: " + iwf + getCwdIwfBean());
            //取颜色
            getColors();
            //取位置
            selectedLocation = app.getSelect().getTimeFuncLocation();
            List<Integer> locations = app.getLocationValues();
            if (locations != null && !locations.isEmpty()) {
                mLocationValues.addAll(locations);
            }
            selectedTimeColorIndex = app.getSelect().getTimeColorIndex();
            color1Adapter.selectedColorIndex = selectedTimeColorIndex;
            isSupportFunctionSet = app.getFunction_support() == 1 || isSupportFunction();//1: 支持，0:不支持
            if (isSupportFunctionSet && app.getFunction_list() != null && !app.getFunction_list().isEmpty()) {
                functions.addAll(app.getFunction_list());
                funcAdapter.notifyDataSetChanged();
                if (app.getSelect().getFunction() != null) {
                    mFunction = app.getSelect().getFunction().get(0);
                }
                selectedFunctionColorIndex = app.getSelect().getFuncColorIndex();
                color2Adapter.selectedColorIndex = selectedFunctionColorIndex;
                color2Adapter.notifyDataSetChanged();
                color2_rv.setVisibility(View.VISIBLE);
                tvColor2.setVisibility(View.VISIBLE);
            }
            updateWidget();
            color1Adapter.notifyDataSetChanged();
            File previewImgFile = new File(cwDialDir + "images/bg.png");
            iv_cw.setImageURI(Uri.fromFile(previewImgFile));
            updateColor();
            Log.e("Davy", "app = " + GsonUtil.toJson(app) + ", iwf = " + GsonUtil.toJson(iwf));
        }
    }

    /**
     * 获取颜色列表
     */
    private void getColors() {
        List<String> colors = app.getColors();
        if (colors != null && !colors.isEmpty()) {
            colorList.addAll(colors);
        } else {
            colorList = getColorList();
        }

        if (colorList.isEmpty()) {
            colorList = getColorList();
        }
    }

    private void initConfig() {
        mLocationValues.clear();
        colorList.clear();
        functions.clear();
        tvColor2.setVisibility(View.GONE);
        color2_rv.setVisibility(View.GONE);
    }


    private boolean isSupportFunction() {
        return app != null && app.getFunction_support_new() == 1;
    }

    /**
     * 更新颜色
     */
    private void updateColor() {
        mIvDialTime.setColorFilter(Color.parseColor(colorList.get(selectedTimeColorIndex)));
        mIvDialFunction.setColorFilter(Color.parseColor(colorList.get(selectedFunctionColorIndex)));
    }


    /**
     * 获取选择的照片文件
     */
    private void processCwImage(Intent data) {
        final Uri cw_image_url = data.getData();

        CwdAppBean cwdAppBean = getCwdAppBean();
        cwImgPath = GetFilePathFromUri.getFileAbsolutePath(this, cw_image_url);
        et_cw_img.setText(cwImgPath);
        Bitmap cw_wallPaper = BitmapFactory.decodeFile(cwImgPath);
        cwImgPath_show = LogPathImpl.getInstance().getPicPath() + System.currentTimeMillis() + "_cw_show.png";
        if (mScreenInfo != null && mScreenInfo.width > 0) {//Crop the watch screen size picture
            ImageUtil.saveWallPaper(Bitmap.createScaledBitmap(cw_wallPaper, mScreenInfo.width, mScreenInfo.height, true), cwImgPath_crop);
            if (isSiCheDevice()) {
                ImageUtil.saveWallPaper(ImageUtil.transform2CornerBitmap(Bitmap.createScaledBitmap(cw_wallPaper, mScreenInfo.width, mScreenInfo.height, true), cwdAppBean.getPreviewRadius()), cwImgPath_show);
            } else {
                ImageUtil.saveWallPaper(ImageUtil.transform2CornerBitmap(Bitmap.createScaledBitmap(cw_wallPaper, mScreenInfo.width, mScreenInfo.height, true), cwdAppBean.getPreviewRadius()), cwImgPath_show);
            }
        } else {
            Toast.makeText(this, "please CALL BLEManager.getWatchPlateScreenInfo();", Toast.LENGTH_LONG).show();
        }
        iv_cw.setImageURI(Uri.fromFile(new File(cwImgPath_show)));

        Log.d(TAG, "cwImgPath_show: " + cwImgPath_show);

    }

    /**
     * 打开文件管理选择照片
     */
    public void selectCwImage(View view) {
        BLEManager.getWatchPlateScreenInfo();

        openImageChooser(SELECT_CW_IMAGE_REQ);
    }


    private boolean isJieLi() {
        Log.d(TAG, "isJieLi: " + LocalDataManager.getBasicInfo().platform);
        if (LocalDataManager.getBasicInfo().platform == 96) {
            return true;
        } else {
            return false;
        }
    }

    final Handler mMainHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
        }
    };

    public void setCw(View view) {
        if (isJieLi()) {
            //TODO 拦截重复调用
            //
            new Thread(new Runnable() {
                @Override
                public void run() {
                    JieliDialConfig config = new JieliDialConfig();
                    config.setBgPath(TextUtils.isEmpty(et_cw_img.getText().toString().trim()) ? cwDialDir + "images/bg.png" : cwImgPath_show);
                    Log.d(TAG, "run: " + cwImgPath_crop);
                    config.setPreviewPath(cwTmpDir + "preview_" + selectedLocation + ".png");
                    Log.d(TAG, "run: " + Color.parseColor(colorList.get(selectedTimeColorIndex)));
                    config.setTimeColor(Color.parseColor(colorList.get(selectedTimeColorIndex)));
                    config.setBaseBinPath(cwTmpDir + "base_" + selectedLocation + ".bin");
                    config.setTargetFilePath(cwDialDir + "jl_platform_dail.iwf.lz");
                    if (new JieliDialMaker().makePhotoDial(config)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.e(TAG, "制作完成：" + config.getTargetFilePath());
                                FileTransferConfig config1 = FileTransferConfig.getDefaultTransPictureConfig(config.getTargetFilePath(), new IFileTransferListener() {
                                    @Override
                                    public void onStart() {
                                    }
                                    @Override
                                    public void onProgress(int progress) {
                                        Log.e("ddd", progress + "watch file");
                                    }
                                    @Override
                                    public void onSuccess() {
                                        Log.e("ddd", "success");
                                    }
                                    @Override
                                    public void onFailed(String errorMsg) {
                                        Log.e("ddd onFailed", errorMsg + "watch file");
                                    }
                                });
                                config1.firmwareSpecName = "jl_platform_dail.iwf.lz";
                                BLEManager.startTranCommonFile(config1);
                            }
                        });
                    }
                }
            }).start();
        } else {
            //替换背景图和预览图
            replacePreviewImage();
            //替换时间颜色
            replaceTempCwdConfig();
            //如果当前表盘已经安装过，则需要删除，可通过表盘列表查询
            boolean isAlreadyInstalled = true;
            if (isAlreadyInstalled) {
                deleteCw();
            } else {
                packAndTransfer();
            }
        }
    }

    /**
     * 删除当前表盘，如果已安装了
     */
    private void deleteCw() {
        String dialName;
        Log.d(TAG, "deleteCw: " + isSiCheDevice());
        if (isSiCheDevice()) {
            dialName = dial_config.getName() + ".watch";
        } else {
            dialName = iwf.getName() + ".iwf";
        }
        Log.e("Davy", "deleteCwd：" + dialName);
        BLEManager.unregisterWatchOperateCallBack(mDialOperateCallback);
        BLEManager.registerWatchOperateCallBack(mDialOperateCallback);
        BLEManager.deleteWatchPlate(dialName);
    }

    private BaseDialOperateCallback mDialOperateCallback = new BaseDialOperateCallback() {
        @Override
        public void onDeletePlate(boolean b) {
            super.onDeletePlate(b);
            BLEManager.unregisterWatchOperateCallBack(mDialOperateCallback);
            packAndTransfer();
        }

        @Override
        public void onGetDialPlateParam(DialPlateParam dialPlateParam) {
            super.onGetDialPlateParam(dialPlateParam);

        }
    };


    private void packAndTransfer() {
        //获取表盘压缩包路径
        if (isSiCheDevice()) {
            packTempCwdPackage = packTempCwdPackageBySiChe(cwName);
        } else {
            packTempCwdPackage = packTempCwdPackage();
        }
        //set
        WatchPlateSetConfig config = new WatchPlateSetConfig();
        //选择已经打包好的表盘压缩文件路径
        if (!packTempCwdPackage.isEmpty()) {
            if (isSiCheDevice()) {
                config.filePath = packTempCwdPackage;
                config.uniqueID = dial_config.getName();
            } else {
                config.filePath = packTempCwdPackage;
                config.uniqueID = iwf.getName();
            }
        }
        Log.d(TAG, "packAndTransfer: packTempCwdPackage：" + packTempCwdPackage);
        progress_bar.setProgress(0);
        config.stateListener = new WatchPlateCallBack.IAutoSetPlateCallBack() {
            @Override
            public void onStart() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_progress.setText("start");
                    }
                });

            }

            @Override
            public void onProgress(int i) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_progress.setText("" + i);
                        progress_bar.setProgress(i);
                    }
                });

            }

            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_progress.setText("success");
                    }
                });
            }

            @Override
            public void onFailed() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_progress.setText("failed");
                    }
                });
            }
        };
        config.errorCallback = new WatchPlateCallBack.ISetPlatErrorCallback() {
            @Override
            public void onFailed(int i) {
                tv_progress.setText("failed, code = " + i);
                Log.e(TAG, "failed, code = " + i);

            }
        };
        config.isOnlyTranslateWatchFile = false;
        BLEManager.startSetPlateFileToWatch(config);
    }

    private CwdAppBean.Location getTimeFuncLocation() {
        List<CwdAppBean.Location> locations = app.getLocations();
        if (ListUtils.INSTANCE.isNotEmpty(locations)) {
            for (CwdAppBean.Location location : locations) {
                if (location.getType() == selectedLocation) {
                    return location;
                }
            }
        }
        return null;
    }


    /**
     * 配置是否发生变化
     */
    private void replaceTempCwdConfig() {

        boolean hasChanged = false;
        if (isSiCheDevice()) {
            CwdDialConfigBean cwdDialConfigBean = getTempCwdDialConfigBean();
            if (cwdDialConfigBean != null && ListUtils.INSTANCE.isNotEmpty(cwdDialConfigBean.getDial_data())) {
                List<DialDataBean> items = cwdDialConfigBean.getDial_data();
                int[] rgb = WallpaperDialManager.hex2RGB(colorList.get(selectedTimeColorIndex));
                if (items.size() > 2) {
                    DialDataBean item = items.get(2);
                    if (rgb != null) {
                        item.setR(rgb[0]);
                        item.setG(rgb[1]);
                        item.setB(rgb[2]);
                    }
                    CwdAppBean.Location location = getTimeFuncLocation();
                    if (location != null) {
                        item.setX(location.getWeek().get(0));
                        item.setY(location.getWeek().get(1));
                    }
                    hasChanged = true;
                }
                if (items.size() > 3) {
                    DialDataBean item = items.get(3);
                    if (rgb != null) {
                        item.setR(rgb[0]);
                        item.setG(rgb[1]);
                        item.setB(rgb[2]);
                    }
                    CwdAppBean.Location location = getTimeFuncLocation();
                    if (location != null) {
                        item.setX(location.getTime().get(0));
                        item.setY(location.getTime().get(1));
                    }
                    hasChanged = true;
                }
            }
            if (hasChanged) {
                boolean b = saveTempDialConfigIwf(cwdDialConfigBean);
                Log.d(TAG, "replaceTempCwdConfig: " + b);
            }
        } else {
            CwdIwfBean mCwdIwfBean = iwf;
            if (mCwdIwfBean != null && !mCwdIwfBean.getItem().isEmpty()) { //其他表盘
                List<CwdIwfBean.Item> items = mCwdIwfBean.getItem();
                CwdAppBean.Location location = app.findLocation(selectedLocation);
                Log.d(TAG, "replaceTempCwdConfig: location: " + location + "   " + selectedLocation);
                CwdAppBean.Function mSelectedFunction = app.findFunction(mFunction);
                String selectedTimeColor = WallpaperDialManager.colorTo16(colorList.get(selectedTimeColorIndex));
                String selectedFuncColor = WallpaperDialManager.colorTo16(colorList.get(selectedFunctionColorIndex));
                if (isSupportFunctionSet) {
                    //找到坐标
                    //处理功能
                    CwdAppBean.Location.FunctionCoordinate functionCoordinate = null;
                    if (location != null && mSelectedFunction != null) {
                        List<CwdAppBean.Location.FunctionCoordinate> functionCoordinates = location.getFunction_coordinate();
                        if (functionCoordinates != null) {
                            for (CwdAppBean.Location.FunctionCoordinate coordinate : functionCoordinates) {
                                if (coordinate.getFunction() == mFunction) {
                                    functionCoordinate = coordinate;
                                    break;
                                }
                            }
                        }
                    }
                    //如果功能未变
//                if (mFunction == app.getSelect().getSelectFunction()) {
//                    Log.d(TAG, "未更换功能！");
//                    if (selectedLocation != app.getSelect().getTimeFuncLocation()) {
//                    }
//                } else {
                    Log.d(TAG, "处理前: " + GsonUtil.toJson(mCwdIwfBean));
                    //删除不要的配置，添加用户选择的配置
                    Iterator<CwdIwfBean.Item> it = items.iterator();
                    while (it.hasNext()) {
                        CwdIwfBean.Item item = it.next();
                        boolean itemRemoved = false;
                        //删除功能组件
                        for (CwdAppBean.Function function : functions) {
                            if (function.findItem(item.getType(), item.getWidget()) != null) {
                                it.remove();
                                itemRemoved = true;
                                break;
                            }
                        }

                        if (itemRemoved) {
                            continue;
                        }

                        //删除时间组件，在时间组件总表则删除后添加用户添加的
                        if (app.findTimeWidgetItem(item.getType(), item.getWidget()) != null) {
                            it.remove();
                        }
                    }
                    Log.d(TAG, "删除功能后: " + GsonUtil.toJson(mCwdIwfBean));
                    if (functionCoordinate != null) {
                        List<CwdAppBean.Function.Item> newFunctionItems = mSelectedFunction.getItem();
                        for (CwdAppBean.Function.Item newFunctionItem : newFunctionItems) {
                            CwdAppBean.Location.FunctionCoordinate.Item item = functionCoordinate.findItem(newFunctionItem.getType(), newFunctionItem.getWidget());
                            if (item != null && item.getCoordinate().size() == 4) {
                                List<Integer> coordinate = item.getCoordinate();
                                CwdIwfBean.Item newItem = new CwdIwfBean.Item(newFunctionItem.getWidget(), newFunctionItem.getType(), coordinate.get(0), coordinate.get(1), coordinate.get(2), coordinate.get(3), newFunctionItem.getBg(), newFunctionItem.getAlign(), selectedFuncColor, selectedFuncColor, "", newFunctionItem.getFont(), newFunctionItem.getFontnum());
                                items.add(newItem);
                            }
                        }
                    }
//                }
                    //处理时间组件
                    List<CwdIwfBean.Item> timeWidget = null;
                    if (location != null) {
                        timeWidget = location.getTime_widget();
                        Log.d(TAG, "时间组件跟随位置变化：" + timeWidget);
                    }

                    if (timeWidget == null || timeWidget.isEmpty()) {
                        Log.d(TAG, "时间组件未配置！");
                    } else {
                        for (CwdIwfBean.Item item : timeWidget) {
                            item.setFgcolor(selectedTimeColor);
                            items.add(item);
                        }
                    }
                    //改变时间组件位置
                    for (CwdIwfBean.Item item : items) {
                        if (IwfItemType.ICON.equals(item.getType())) {
                        } else if (IwfItemType.TIME.equals(item.getType())) {
                            item.setFgcolor(selectedTimeColor);
                            if (location != null) {
                                item.setX(location.getTime().get(0));
                                item.setY(location.getTime().get(1));
                            }
                        } else if (IwfItemType.DAY.equals(item.getType())) {
                            item.setFgcolor(selectedTimeColor);
                            if (location != null) {
                                item.setX(location.getDay().get(0));
                                item.setY(location.getDay().get(1));
                            }
                        } else if (IwfItemType.WEEK.equals(item.getType())) {
                            item.setFgcolor(selectedTimeColor);
                            if (location != null) {
                                item.setX(location.getWeek().get(0));
                                item.setY(location.getWeek().get(1));
                            }
                        }
                    }
                    Log.d(TAG, "处理后: " + GsonUtil.toJson(mCwdIwfBean));
                } else {
                    //无功能编辑设备只更改时间组件位置
                    for (CwdIwfBean.Item item : items) {
                        if (IwfItemType.ICON.equals(item.getType())) {
                        } else if (IwfItemType.TIME.equals(item.getType())) {
                            item.setFgcolor(selectedTimeColor);
                            if (location != null) {
                                item.setX(location.getTime().get(0));
                                item.setY(location.getTime().get(1));
                            }
                        } else if (IwfItemType.DAY.equals(item.getType())) {
                            item.setFgcolor(selectedTimeColor);
                            if (location != null) {
                                item.setX(location.getDay().get(0));
                                item.setY(location.getDay().get(1));
                            }
                        } else if (IwfItemType.WEEK.equals(item.getType())) {
                            item.setFgcolor(selectedTimeColor);
                            if (location != null) {
                                item.setX(location.getWeek().get(0));
                                item.setY(location.getWeek().get(1));
                            }
                        }
                    }
                }
                saveTempCwdIwf(mCwdIwfBean);
            }
        }
    }


    /**
     * 替换背景壁纸
     */
    private void replaceBgImage() {
        boolean isUseBmpBg = true;
        String bgName = WallpaperDialManager.TEMP_BG_BMP;
        if (iwf != null && !iwf.getItem().isEmpty()) {
            List<CwdIwfBean.Item> items = iwf.getItem();
            for (CwdIwfBean.Item item : items) {
                if (IwfItemType.ICON.equals(item.getType())) {
                    String bg = item.getBg();
                    if (!TextUtils.isEmpty(bg)) {
                        bgName = bg;
                        isUseBmpBg = bg.toLowerCase().endsWith(BackgroundType.BMP);
                    }
                    break;
                }
            }
        }
        String des = cwTmpDir + bgName;
        Log.e(TAG, "replaceBgImage: " + des);
        WallpaperDialManager.replaceCwrBgImageWithTemp(cwImgPath_crop, des, isUseBmpBg);
    }


    private void replacePreviewImage() {
        String previewName = WallpaperDialManager.TEMP_PREVIEW_IMAGE_BMP;
        String tempPreviewPath;
        boolean isUsePreviewBmp = false;
        if (isSiCheDevice()) {
            tempPreviewPath = cwTmpDir  + previewName;

        } else {
            if (iwf != null) {
                String preview = iwf.getPreview();
                if (!TextUtils.isEmpty(preview)) {
                    previewName = preview;
                    isUsePreviewBmp = previewName.toLowerCase().endsWith(BackgroundType.BMP);
                }
            }
            tempPreviewPath = cwDialDir + "tmp/" + previewName;

        }


        Pair<Integer, Integer> size = WallpaperDialManager.getImageSize(tempPreviewPath);
        if (size == null) {
            size = new Pair<>(fl_cw.getWidth(), fl_cw.getHeight());
        }
        Log.d(TAG, "preview size = " + size);
        Log.d(TAG, "tempPreviewPath：临时表盘包预览图路径： " + tempPreviewPath);
        File bitmapFile = new File(tempPreviewPath);
        int width = mScreenInfo != null ? mScreenInfo.width : fl_cw.getWidth();
        int height = mScreenInfo != null ? mScreenInfo.height : fl_cw.getHeight();
        //存储预览图原始大小，透明背景
        BitmapUtil.savePngBitmap(BitmapUtil.zoomImgWithBorder(BitmapUtil.view2BitmapWithAlpha(fl_cw, fl_cw.getWidth(), fl_cw.getHeight()), width, height), bitmapFile, false);
        //存储按照固件预览图大小缩放的预览图，用于生产bmp
        //存储按照固件预览图大小缩放的预览图，用于生产bmp(previewView 可能会绘制边框，用做固件预览图)
        File zoomBitmapFile = new File(cwImgPath_show);
        //bitmap写入到文件。
        BitmapUtil.savePngBitmap(BitmapUtil.zoomImgWithBorder(BitmapUtil.view2BitmapBlackBg(fl_cw, fl_cw.getWidth(), fl_cw.getHeight()), size.first, size.second), zoomBitmapFile, false);
        //替换预览图
        if (isSiCheDevice()) {
            //将png转化为bin，
            FileUtil.savePngByBin(zoomBitmapFile.getAbsolutePath(), cwTmpDir + WallpaperDialManager.TEMP_PREVIEW_IMAGE_BIN);
        } else {
            //转换bmp
            WallpaperDialManager.replaceCwrPreviewImageWithTemp(zoomBitmapFile.getAbsolutePath(), cwTmpDir + previewName, isUsePreviewBmp);
        }
        Log.d(TAG, "replacePreviewImage: zoomBitmapFile Path:" + zoomBitmapFile.getAbsolutePath());
        //替换背景图片
        if (isSiCheDevice()) {
            //获取选择的背景图
            File zoomBitmapBgFile = new File(cwImgPath_crop);
            FileUtil.savePngByBin(zoomBitmapBgFile.getAbsolutePath(), cwTmpDir + WallpaperDialManager.TEMP_BACKGROUND_BIN);
        } else {
            replaceBgImage();
        }

    }

    /**
     * 是否是思澈设备
     */
    private boolean isSiCheDevice() {
        return BLEManager.isSichePlatform();
    }

    /**
     * 解析app.json
     */
    private CwdAppBean getCwdAppBean() {
        try {
            String json = FileUtil.readStringFromFile(cwDialDir + "app.json");
            LogUtil.d("json = " + json);
            if (!TextUtils.isEmpty(json)) {
                return GsonUtil.fromJson(json, CwdAppBean.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析临时表盘包iwf.json
     *
     * @return
     */
    private CwdIwfBean getCwdIwfBean() {
        try {
            String json = FileUtil.readStringFromFile(cwTmpDir + "iwf.json");
            if (!TextUtils.isEmpty(json)) {
                return GsonUtil.fromJson(json, CwdIwfBean.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean saveTempCwdIwf(CwdIwfBean data) {
        try {
            return FileUtil.writeStringToFile(cwTmpDir + "iwf.json", GsonUtil.toJson(data));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 压缩临时壁纸表盘包到临时目录（思澈平台）
     */
    private String packTempCwdPackageBySiChe(String otaFaceName) {
        try {
            String tempZipPath = cwTmpDir  + "custom1.watch";
            FileUtil.deleteFile(tempZipPath);
            int result = BLEManager.mkSifliDial(cwTmpDir);
            return result == 0 ? tempZipPath : "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 解析临时表盘包dial_config.json(思澈平台)
     */
    private CwdDialConfigBean getTempCwdDialConfigBean() {
        try {
            String json = FileUtil.readStringFromFile(cwTmpDir + "dial_config.json");
            if (!TextUtils.isEmpty(json)) {
                return GsonUtil.fromJson(json, CwdDialConfigBean.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 压缩临时壁纸表盘包到临时目录
     */
    private String packTempCwdPackage() {
        String tempZipPath = cwDialDir + "zip/tmp.zip";
        //pack
        try {
            //压缩
            ZipUtils.zip(
                    cwTmpDir,
                    cwDialDir + "zip/",
                    "tmp.zip"
            );
            return tempZipPath;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "packAndTransfer: " + e.getMessage());
        }
        return "";
    }


    private boolean saveTempDialConfigIwf(CwdDialConfigBean data) {
        try {
            return FileUtil.writeStringToFile(cwTmpDir + "dial_config.json", GsonUtil.toJson(data));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 颜色列表
     */
    public List<String> getColorList() {
        List<String> mColorList = new ArrayList<>();
        if (!isJieLi()){
            mColorList.add("#F2F2F2");
            mColorList.add("#000000");
            mColorList.add("#FC1E58");
            mColorList.add("#FF9501");
            mColorList.add("#0091FF");
            mColorList.add("#44D7B6");
        }else {
            mColorList.add("#ffffff");
            mColorList.add("#000000");
            mColorList.add("#de4371");
            mColorList.add("#de4343");
            mColorList.add("#de7143");
            mColorList.add("#dba85c");
            mColorList.add("#dbcf60");
            mColorList.add("#b7c96b");
            mColorList.add("#a8e36d");
            mColorList.add("#85e36d");
            mColorList.add("#6de379");
            mColorList.add("#6de39c");
            mColorList.add("#6de3c0");
            mColorList.add("#dba85c");
        }


        return mColorList;
    }
}
