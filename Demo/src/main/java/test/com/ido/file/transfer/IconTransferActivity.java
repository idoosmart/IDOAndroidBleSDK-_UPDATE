package test.com.ido.file.transfer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ido.ble.LocalDataManager;
import com.ido.ble.protocol.model.SupportFunctionInfo;

import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;

public class IconTransferActivity extends BaseAutoConnectActivity {

    Button btNoticeIconTrans;
    Button btSportIconTrans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_icon_transfer);
        btNoticeIconTrans = findViewById(R.id.btNoticeIconTrans);
        btSportIconTrans = findViewById(R.id.btSportIconTrans);
        SupportFunctionInfo func = LocalDataManager.getSupportFunctionInfo();
        if (func.V3_support_set_v3_notify_add_app_name) {
            btNoticeIconTrans.setVisibility(View.VISIBLE);
        } else {
            btNoticeIconTrans.setVisibility(View.GONE);
        }
//        if (func.V3_set_100_sport_sort) {
//            btSportIconTrans.setVisibility(View.VISIBLE);
//        } else {
//            btSportIconTrans.setVisibility(View.GONE);
//        }

    }

    public void btNoticeIconTrans(View view) {
        startActivity(new Intent(this, NotificationIconTransferActivity.class));
    }

    public void btSportIconTrans(View view) {
        startActivity(new Intent(this, SportIconTransferActivity.class));
    }
}