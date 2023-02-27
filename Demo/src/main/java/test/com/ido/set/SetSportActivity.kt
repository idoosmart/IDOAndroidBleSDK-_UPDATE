package test.com.ido.set

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.ido.ble.LocalDataManager
import test.com.ido.R
import test.com.ido.connect.BaseAutoConnectActivity
import test.com.ido.file.transfer.SportIconTransferActivity

/**
 * @author tianwei
 * @date 2023/2/27
 * @time 9:45
 * 用途:
 */
class SetSportActivity : BaseAutoConnectActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_sport)
        val functionInfo = LocalDataManager.getSupportFunctionInfo()
        findViewById<Button>(R.id.btSportItemSet).visibility =
            if (functionInfo?.V3_set_20_base_sport_param_sort == true
            ) View.VISIBLE else View.GONE
    }

    fun btSportTypeSet(view: android.view.View) {
        startActivity(Intent(this, SportIconTransferActivity::class.java))
    }

    fun btSportItemSet(view: android.view.View) {
        startActivity(Intent(this, SetSportDataItemActivity::class.java))
    }
}