package test.com.ido.dfu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ido.ble.BLEManager;
import com.ido.ble.callback.EnterDfuModeCallback;

import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;
import test.com.ido.file.transfer.FileTransferActivity;

public class MainUpgradeActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_upgrade);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void realtekUpgrade(View view){
        Intent intent =  new Intent(this, MainDfuActivity.class);
        startActivity(intent);
    }
    public void nodicUpgrade(View view){
        Intent intent =  new Intent(this, MainDfuActivity.class);
        intent.putExtra("nordic",true);
        startActivity(intent);
    }

    public void ApolloUpgrade(View view){
        /**
         * Reference document transfer
         */
        startActivity(new Intent(this, FileTransferActivity.class));
    }

}
