package test.com.ido.set;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ido.ble.BLEManager;
import com.ido.ble.LocalDataManager;
import com.ido.ble.callback.OperateCallBack;
import com.ido.ble.protocol.model.FrequentContactsV3;

import java.util.ArrayList;

import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;

public class SetContactActivity extends BaseAutoConnectActivity {

    private TextView tvResult;
    private EditText name_et ,number_et;

    private OperateCallBack.ICallBack iCallBack = new OperateCallBack.ICallBack() {

        @Override
        public void onSetResult(OperateCallBack.OperateType type, boolean isSuccess) {
            if(type == OperateCallBack.OperateType.EMERGENCY_CONTACTS){
                tvResult.setText("EMERGENCY_CONTACTS set result:"+(isSuccess?"success":"failed"));
            }else if(type == OperateCallBack.OperateType.FREQUENT_CONTACTS){
                tvResult.setText("FREQUENT_CONTACTS set result:"+(isSuccess?"success":"failed"));
            }

        }

        @Override
        public void onAddResult(OperateCallBack.OperateType type, boolean isSuccess) {
            tvResult.setText("add result:"+(isSuccess?"success":"failed"));
        }

        @Override
        public void onDeleteResult(OperateCallBack.OperateType type, boolean isSuccess) {

        }

        @Override
        public void onModifyResult(OperateCallBack.OperateType type, boolean isSuccess) {

        }

        @Override
        public void onQueryResult(OperateCallBack.OperateType type, Object returnData) {
                 if(returnData!=null){
                     tvResult.setText(returnData.toString());
                 }else {
                     tvResult.setText("query result null");
                 }

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_contact);

        tvResult = (TextView) findViewById(R.id.tv_result);
        name_et = findViewById(R.id.contact_name);
        number_et = findViewById(R.id.contact_numbeer);

        BLEManager.registerOperateCallBack(iCallBack);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        BLEManager.unregisterOperateCallBack(iCallBack);
        Log.e("eee", "onDestroy");
    }
    public void addcontact(View v){
        String name = name_et.getText().toString();
        String number = number_et.getText().toString();
        if(TextUtils.isEmpty(name) || TextUtils.isEmpty(number)){
            Toast.makeText(this, "请输入联系人名称和号码", Toast.LENGTH_LONG).show();
            return;
        }
        FrequentContactsV3 frequentContactsV3 = new FrequentContactsV3();
        frequentContactsV3.name = name;
        frequentContactsV3.phone = number;
        ArrayList<FrequentContactsV3> list = new ArrayList<>();
        list.add(frequentContactsV3);
//        BLEManager.setFrequentContactsV3(list);
        BLEManager.addFrequentContactsV3(list);
        tvResult.setText("start setcontact...");
    }

    public void querycontact(View v){
        BLEManager.queryFrequentContactsV3();
        tvResult.setText("start querycontact...");
    }

    public void addemergencycontact(View v){
        if(!LocalDataManager.getSupportFunctionInfo().v3_support_set_get_emergency_connact){
            Toast.makeText(this, "设备不支持此功能", Toast.LENGTH_LONG).show();
            return;
        }
        String name = name_et.getText().toString();
        String number = number_et.getText().toString();
        if(TextUtils.isEmpty(name) || TextUtils.isEmpty(number)){
            Toast.makeText(this, "请输入联系人名称和号码", Toast.LENGTH_LONG).show();
            return;
        }
        FrequentContactsV3 frequentContactsV3 = new FrequentContactsV3();
        frequentContactsV3.name = name;
        frequentContactsV3.phone = number;
        ArrayList<FrequentContactsV3> list = new ArrayList<>();
        list.add(frequentContactsV3);
        BLEManager.setEmergencyContactsV3(list);
        tvResult.setText("start setEmergencyContactsV3...");
    }

    public void queryemergencycontact(View v){
        if(!LocalDataManager.getSupportFunctionInfo().v3_support_set_get_emergency_connact){
            Toast.makeText(this, "设备不支持此功能", Toast.LENGTH_LONG).show();
            return;
        }
        BLEManager.queryEmergencyContactsV3();
        tvResult.setText("start queryemergencycontact...");
    }

}
