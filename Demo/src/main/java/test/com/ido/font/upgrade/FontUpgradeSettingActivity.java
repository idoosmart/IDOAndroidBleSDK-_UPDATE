package test.com.ido.font.upgrade;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.veryfit.multi.nativeprotocol.Protocol;

import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;
import test.com.ido.utils.DataUtils;

public class FontUpgradeSettingActivity extends BaseAutoConnectActivity {

    private EditText etNrfPRN, etBinPRN, etSoLibMtuSend, etSoLibMtuRec;
    private RadioGroup rgSentBytes, rgRceiveBytes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_font_upgrade_setting);

        etNrfPRN = findViewById(R.id.font_setting_dfu_number_of_packets_et);
        etBinPRN = findViewById(R.id.font_setting_bin_file_prn_et);

        etSoLibMtuRec = findViewById(R.id.font_setting_mtu_receive);
        etSoLibMtuSend = findViewById(R.id.font_setting_mtu_send);


        rgRceiveBytes = findViewById(R.id.font_receive_bytes_rg);
        rgSentBytes = findViewById(R.id.font_send_bytes_rg);


        etNrfPRN.setText(DataUtils.getInstance().getFontUpgradeNRFPRN() +"");
        etBinPRN.setText(DataUtils.getInstance().getFontUpgradeBinPRN() + "");
    }



    public void saveParas(View view){

        if (TextUtils.isDigitsOnly(etNrfPRN.getText().toString()) && !TextUtils.isEmpty(etNrfPRN.getText().toString())) {
            int prn = Integer.parseInt(etNrfPRN.getText().toString());
            DataUtils.getInstance().saveFontUpgradeNRFPRN(prn);
        }

        if (TextUtils.isDigitsOnly(etBinPRN.getText().toString()) && !TextUtils.isEmpty(etBinPRN.getText().toString())) {
            int prn = Integer.parseInt(etBinPRN.getText().toString());
            DataUtils.getInstance().saveFontUpgradeBinPRN(prn);
        }

        if (TextUtils.isDigitsOnly(etSoLibMtuSend.getText().toString()) && !TextUtils.isEmpty(etSoLibMtuRec.getText().toString())) {
            int send = Integer.parseInt(etSoLibMtuSend.getText().toString());
            int recev = Integer.parseInt(etSoLibMtuRec.getText().toString());
            Protocol.getInstance().setMtu(send, recev);
        }


        finish();
    }
}
