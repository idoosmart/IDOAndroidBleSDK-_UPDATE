package test.com.ido.unbind;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.ido.ble.BLEManager;
import com.ido.ble.LocalDataManager;
import com.ido.ble.callback.UnbindCallBack;

import test.com.ido.R;
import test.com.ido.SplashActivity;
import test.com.ido.connect.BaseAutoConnectActivity;
import test.com.ido.utils.DataUtils;

public class UnbindActivity extends BaseAutoConnectActivity {

    UnbindCallBack.ICallBack iCallBack = new UnbindCallBack.ICallBack() {
        @Override
        public void onSuccess() {
            closeProgressDialog();
            //清空本地数据库
            LocalDataManager.deleteAllHealthDbData();

            DataUtils.getInstance().setIsFirst(true);
            Toast.makeText(UnbindActivity.this, R.string.unbind_tip_msg_ok, Toast.LENGTH_LONG).show();
            startActivity(new Intent(UnbindActivity.this, SplashActivity.class));
            finish();
            System.exit(0);
        }

        @Override
        public void onFailed() {
            closeProgressDialog();
            Toast.makeText(UnbindActivity.this, R.string.unbind_tip_msg_failed, Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unbind);
        BLEManager.registerUnbindCallBack(iCallBack);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BLEManager.unregisterUnbindCallBack(iCallBack);
    }

    public void unbind(View v){
        showProgressDialog("unbind ...");
        BLEManager.unbind();
    }

    public void forceUnbind(View v){
        showProgressDialog("unbind ...");
        BLEManager.forceUnbind();
    }
}
