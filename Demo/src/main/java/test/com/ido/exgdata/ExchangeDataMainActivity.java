package test.com.ido.exgdata;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;

public class ExchangeDataMainActivity extends BaseAutoConnectActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_data_main);
    }

    public void appExchange(View v){
        startActivity(new Intent(this, AppExchangeActivity.class));
    }

    public void deviceExchange(View v){
        startActivity(new Intent(this, DeviceExchangeActivity.class));
    }
}
