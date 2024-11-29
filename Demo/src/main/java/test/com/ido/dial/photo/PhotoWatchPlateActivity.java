package test.com.ido.dial.photo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ido.ble.BLEManager;
import com.ido.ble.LocalDataManager;
import com.ido.ble.vendor.dial.photo.IPhotoDial;
import com.ido.ble.vendor.dial.photo.PhotoDialConfig;
import com.ido.ble.vendor.dial.photo.PhotoDialPresetConfig;
import com.ido.ble.vendor.dial.photo.glacier;
import com.ido.ble.vendor.dial.photo.harpoon;
import com.ido.ble.watch.custom.callback.WatchPlateCallBack;
import com.ido.ble.watch.custom.model.DialPlateParam;
import com.ido.ble.watch.custom.model.WatchPlateFileInfo;
import com.ido.ble.watch.custom.model.WatchPlateScreenInfo;
import com.ido.life.constants.WallpaperDialConstants;
import com.ido.life.util.WallpaperDialManager;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;
import test.com.ido.log.LogPathImpl;
import test.com.ido.utils.BitmapUtil;
import test.com.ido.utils.DataUtils;
import test.com.ido.utils.FileUtil;
import test.com.ido.utils.GetFilePathFromUri;
import test.com.ido.utils.GsonUtil;
import test.com.ido.utils.ImageUtil;
import test.com.ido.utils.OnItemClickListener;
import test.com.ido.utils.ResourceUtil;

public class PhotoWatchPlateActivity extends BaseAutoConnectActivity {
    private static final String TAG = PhotoWatchPlateActivity.class.getSimpleName();
    private FrameLayout fl_cw;
    private ImageView mIvDialTime, iv_cw, mIvDialFunction;
    private LinearLayout llDialWidget;
    private RecyclerView color1_rv, color2_rv;
    private RecyclerView rv_time_location, rv_func;
    private TextView tvFilePath, tvState, tvOperateTv, tvUniqueID, tvPhoto, tv_progress, tvColor2;
    private EditText et_cw_file,et_cw_img,et_cw_color;
    private ProgressBar progress_bar;

    WatchPlateScreenInfo mScreenInfo;
    private List<String> colorList = new ArrayList<>();
    private List<Integer> mLocationValues = new ArrayList<>();
    private List<Integer> functions = new ArrayList<>();
    private PhotoDialPresetConfig presetConfig;
    private int selectedLocation;
    private int selectedTimeColorIndex, selectedFunctionColorIndex;
    private ColorAdapter color1Adapter, color2Adapter;
    private TimeLocationAdapter mTimeLocationAdapter;

    private boolean mFunctionShow = true;
    private boolean isSupportFunctionSet;
    private int mFunction = WallpaperDialConstants.WidgetFunction.WEEK_DATE;
    private FunctionAdapter funcAdapter;
    private String filePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_watch_plate);
        BLEManager.getBasicInfo();
        initView();
    }

    private void initView() {
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

    private void initTimeLocationView() {
        mTimeLocationAdapter = new TimeLocationAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv_time_location.setLayoutManager(layoutManager);
        rv_time_location.setAdapter(mTimeLocationAdapter);
    }

    private static final int SELECT_CW_FILE_REQ = 11;
    private static final int SELECT_CW_IMAGE_REQ = 21;
    private static final int SELECT_CW_VIDEO_REQ = 22;
    private static final int CROP_CW_VIDEO_REQ = 23;
    private String cwFilePath;
    private String cwImgPath;
    private String cwImgPath_crop = LogPathImpl.getInstance().getPicPath() + "cw_input.png";
    private String cwImgPath_show = LogPathImpl.getInstance().getPicPath() + System.currentTimeMillis() + "_cw_show.png";
    private String cwName = "custom4";
    private String cwVideoPath = "";

    public void selectCwFile(View view) {
        openFileChooser(SELECT_CW_FILE_REQ);
    }

    private void openFileChooser(int code) {
        final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/zip");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            // file browser has been found on the device
            startActivityForResult(intent, code);
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case SELECT_CW_FILE_REQ:
                // and read new one
                processCwFile(data);
                break;
            case SELECT_CW_IMAGE_REQ:
                processCwImage(data);
                break;
        }
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
            presetConfig = BLEManager.preparePhotoDialConfigSync(cwName, cwFilePath);
            if (presetConfig == null) {
                Toast.makeText(this, "parse dial package failed", Toast.LENGTH_LONG).show();
                return;
            }
            Log.d(TAG, "processCwFile: " + presetConfig);
//            //取颜色
            getColors();
//            //取位置
            selectedLocation = presetConfig.getPosition();
            List<Integer> locations = presetConfig.getSupportPositions();
            if (locations != null && !locations.isEmpty()) {
                mLocationValues.addAll(locations);
            }
            selectedTimeColorIndex = colorList.indexOf(presetConfig.getTimeColor());
            color1Adapter.selectedColorIndex = selectedTimeColorIndex;
            isSupportFunctionSet = presetConfig.getSupportFunctionType();//1: 支持，0:不支持
            if (isSupportFunctionSet && presetConfig.getSupportFunctionTypeList() != null && !presetConfig.getSupportFunctionTypeList().isEmpty()) {
                functions.addAll(presetConfig.getSupportFunctionTypeList());
                funcAdapter.notifyDataSetChanged();
                mFunction = presetConfig.getDefaultFunctionType();
                selectedFunctionColorIndex = colorList.indexOf(presetConfig.getDefaultFunctionWidgetColor());
                color2Adapter.selectedColorIndex = selectedFunctionColorIndex;
                color2Adapter.notifyDataSetChanged();
                color2_rv.setVisibility(View.VISIBLE);
                tvColor2.setVisibility(View.VISIBLE);
            }
            updateWidget();
            color1Adapter.notifyDataSetChanged();
            iv_cw.setImageURI(Uri.fromFile(new File(presetConfig.getBg())));
            updateColor();
        }
    }

    private void processCwImage(Intent data) {
        final Uri cw_image_url = data.getData();

        cwImgPath = GetFilePathFromUri.getFileAbsolutePath(this, cw_image_url);
        et_cw_img.setText(cwImgPath);
        Bitmap cw_wallPaper = BitmapFactory.decodeFile(cwImgPath);
        cwImgPath_show = LogPathImpl.getInstance().getPicPath() + System.currentTimeMillis() + "_cw_show.png";
        if (mScreenInfo != null && mScreenInfo.width > 0) {//Crop the watch screen size picture
            ImageUtil.saveWallPaper(Bitmap.createScaledBitmap(cw_wallPaper, mScreenInfo.width, mScreenInfo.height, true), cwImgPath_crop);
            if (isSiCheDevice()) {
                ImageUtil.saveWallPaper(ImageUtil.transform2CornerBitmap(Bitmap.createScaledBitmap(cw_wallPaper, mScreenInfo.width,
                        mScreenInfo.height, true), presetConfig.getPreviewRadius()), cwImgPath_show);
            } else {
                ImageUtil.saveWallPaper(ImageUtil.transform2CornerBitmap(Bitmap.createScaledBitmap(cw_wallPaper, mScreenInfo.width,
                        mScreenInfo.height, true), presetConfig.getPreviewRadius()), cwImgPath_show);
            }
        } else {
            Toast.makeText(this, "please CALL BLEManager.getWatchPlateScreenInfo();", Toast.LENGTH_LONG).show();
        }
        iv_cw.setImageURI(Uri.fromFile(new File(cwImgPath_show)));

        Log.d(TAG, "cwImgPath_show: " + cwImgPath_show);
        cwVideoPath = null;
    }


    private void getColors() {
        List<String> colors = presetConfig.getTimeColorList();
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

    public List<String> getColorList() {
        List<String> mColorList = new ArrayList<>();
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
        return mColorList;
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
            holder.tvFun.setText(WallpaperDialManager.getFunctionName(functions.get(position)));
            holder.ivSelector.setVisibility(functions.get(position) == mFunction ? View.VISIBLE : View.GONE);
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
                        mFunction = functions.get(getAdapterPosition());
                        funcAdapter.notifyDataSetChanged();
                        updateWidgetType();
                    }
                });
            }
        }
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
            DrawableCompat.setTint(drawable, mLocationValues.get(position) == selectedLocation ?
                    ResourceUtil.getColor(R.color.color_FF4A00) : ResourceUtil.getColor(R.color.translate));
            holder.lay_location.setBackground(drawable);
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) holder.tv_time.getLayoutParams();
            lp.gravity = WallpaperDialManager.getGravityByLocation(mLocationValues.get(position));
            holder.tv_time.setLayoutParams(lp);
            if (mLocationValues.get(position) == WallpaperDialConstants.WidgetLocation.CENTER_55) {
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

        int timeResId = selectedLocation == WallpaperDialConstants.WidgetLocation.CENTER_55 ? R.mipmap.icon_wallpager_dial_time_center :
                R.mipmap.icon_wallpaper_dial_time;
        mIvDialTime.setImageResource(timeResId);
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
     * 设置表盘控件位置
     */
    private void changeDialWidgetLocation(@WallpaperDialConstants.WidgetLocation int location) {
        int gravity = WallpaperDialManager.getGravityByLocation(location);
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

    private void updateColor() {
        mIvDialTime.setColorFilter(Color.parseColor(colorList.get(selectedTimeColorIndex)));
        mIvDialFunction.setColorFilter(Color.parseColor(colorList.get(selectedFunctionColorIndex)));
    }

    public void setCw(View view) {
            PhotoDialConfig config = new PhotoDialConfig();
            config.setDialName(cwName);
            File zoomBitmapBgFile = new File(cwImgPath_crop);
            config.setBgPicPath(zoomBitmapBgFile.getAbsolutePath());
            File zoomBitmapFile = new File(cwImgPath_show);
            BitmapUtil.savePngBitmap(BitmapUtil.view2BitmapBlackBg(fl_cw, fl_cw.getWidth(), fl_cw.getHeight()), zoomBitmapFile, false);
            config.setPreviewPicPath(zoomBitmapFile.getAbsolutePath());
            config.setPosition(selectedLocation);
            config.setShowFunction(mFunction);
            config.setTimeColor(colorList.get(selectedTimeColorIndex));
            config.setFunctionColor(colorList.get(selectedFunctionColorIndex));
            tv_progress.setText("start");
            BLEManager.startInstallPhotoWatchface(config, new glacier() {
                @Override
                public void onProgress(int progress) {
                    runOnUiThread(() -> {
                        tv_progress.setText("" + progress);
                        progress_bar.setProgress(progress);
                    });
                }

                @Override
                public void onSuccess() {
                    runOnUiThread(() -> tv_progress.setText("success"));
                }

                @Override
                public void onFailed(int errCode, @NotNull Object errMsg) {
                    runOnUiThread(() -> tv_progress.setText("failed: errCode = " + errCode + ", errMsg = " + errMsg));
                }
            });
    }

    private boolean isJieLi() {
        Log.d(TAG, "isJieLi: " + LocalDataManager.getBasicInfo().platform);
        if (LocalDataManager.getBasicInfo().platform == 96) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 打开文件管理选择照片
     */
    public void selectCwImage(View view) {
        BLEManager.getWatchPlateScreenInfo();

        openImageChooser(SELECT_CW_IMAGE_REQ);
    }

    public void selectCwVideo(View view) {
        BLEManager.getWatchPlateScreenInfo();

//        openVideoChooser(SELECT_CW_VIDEO_REQ);
    }

    /**
     * 打开文件管理选择照片
     */
    private void openImageChooser(int code) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"), code);
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, code);
        }
    }
    private boolean isSiCheDevice() {
        return BLEManager.isSichePlatform();
    }

    public void cancelInstall(View view) {
        BLEManager.cancelPhotoDialInstall();
    }
}