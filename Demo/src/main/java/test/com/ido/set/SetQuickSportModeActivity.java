package test.com.ido.set;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.ido.ble.BLEManager;
import com.ido.ble.callback.SettingCallBack;
import com.ido.ble.protocol.model.QuickSportMode;
import com.ido.ble.protocol.model.SportModeSortV3;
import com.ido.ble.protocol.model.SportType;

import java.util.ArrayList;

import test.com.ido.R;
import test.com.ido.connect.BaseAutoConnectActivity;

public class SetQuickSportModeActivity extends BaseAutoConnectActivity {

    CheckBox sport_type0_walk;
    CheckBox sport_type0_run;
    CheckBox sport_type0_by_bike;
    CheckBox sport_type0_on_foot;
    CheckBox sport_type0_swim;
    CheckBox sport_type0_mountain_climbing;
    CheckBox sport_type0_badminton;
    CheckBox sport_type0_other;

    CheckBox sport_type1_fitness;
    CheckBox sport_type1_spinning;
    CheckBox sport_type1_ellipsoid;
    CheckBox sport_type1_treadmill;
    CheckBox sport_type1_sit_up;
    CheckBox sport_type1_push_up;
    CheckBox sport_type1_dumbbell;
    CheckBox sport_type1_weightlifting;

    CheckBox sport_type2_bodybuilding_exercise;
    CheckBox sport_type2_yoga;
    CheckBox sport_type2_rope_skipping;
    CheckBox sport_type2_table_tennis;
    CheckBox sport_type2_basketball;
    CheckBox sport_type2_footballl;
    CheckBox sport_type2_volleyball;
    CheckBox sport_type2_tennis;

    CheckBox sport_type3_golf;
    CheckBox sport_type3_baseball;
    CheckBox sport_type3_skiing;
    CheckBox sport_type3_roller_skating;
    CheckBox sport_type3_dance;

    private SettingCallBack.ICallBack iCallBack = new SettingCallBack.ICallBack() {
        @Override
        public void onSuccess(SettingCallBack.SettingType type, Object returnData) {
            Toast.makeText(SetQuickSportModeActivity.this, R.string.set_para_tip_msg_ok, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailed(SettingCallBack.SettingType type) {
            Toast.makeText(SetQuickSportModeActivity.this, R.string.set_para_tip_msg_failed, Toast.LENGTH_SHORT).show();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_quick_sport_mode);

        initView();
        BLEManager.registerSettingCallBack(iCallBack);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BLEManager.unregisterSettingCallBack(iCallBack);
    }

    private void initView() {
        sport_type0_walk = (CheckBox)findViewById(R.id.set_para_quick_sport_mode_walk);
        sport_type0_run = (CheckBox)findViewById(R.id.set_para_quick_sport_mode_run);
        sport_type0_by_bike = (CheckBox)findViewById(R.id.set_para_quick_sport_mode_bike);
        sport_type0_on_foot = (CheckBox)findViewById(R.id.set_para_quick_sport_mode_foot);
        sport_type0_swim = (CheckBox)findViewById(R.id.set_para_quick_sport_mode_swim);
        sport_type0_mountain_climbing = (CheckBox)findViewById(R.id.set_para_quick_sport_mode_mountain);
        sport_type0_badminton = (CheckBox)findViewById(R.id.set_para_quick_sport_mode_badminton);
        sport_type0_other = (CheckBox)findViewById(R.id.set_para_quick_sport_mode_other);

        sport_type1_fitness = (CheckBox)findViewById(R.id.set_para_quick_sport_mode_fitness);
        sport_type1_spinning = (CheckBox)findViewById(R.id.set_para_quick_sport_mode_spinning);
        sport_type1_ellipsoid = (CheckBox)findViewById(R.id.set_para_quick_sport_mode_ellipsoid);
        sport_type1_treadmill = (CheckBox)findViewById(R.id.set_para_quick_sport_mode_treadmill);
        sport_type1_sit_up = (CheckBox)findViewById(R.id.set_para_quick_sport_mode_sit_up);
        sport_type1_push_up = (CheckBox)findViewById(R.id.set_para_quick_sport_mode_push_up);
        sport_type1_dumbbell = (CheckBox)findViewById(R.id.set_para_quick_sport_mode_dumbbell);
        sport_type1_weightlifting = (CheckBox)findViewById(R.id.set_para_quick_sport_mode_weightlifting);
        sport_type2_bodybuilding_exercise = (CheckBox)findViewById(R.id.set_para_quick_sport_mode_bodybuilding_exercise);
        sport_type2_yoga = (CheckBox)findViewById(R.id.set_para_quick_sport_mode_yoga);
        sport_type2_rope_skipping = (CheckBox)findViewById(R.id.set_para_quick_sport_mode_rope_skipping);
        sport_type2_table_tennis = (CheckBox)findViewById(R.id.set_para_quick_sport_mode_table_tennis);
        sport_type2_basketball = (CheckBox)findViewById(R.id.set_para_quick_sport_mode_basketball);
        sport_type2_footballl = (CheckBox)findViewById(R.id.set_para_quick_sport_mode_football);
        sport_type2_volleyball = (CheckBox)findViewById(R.id.set_para_quick_sport_mode_volleyball);
        sport_type2_tennis = (CheckBox)findViewById(R.id.set_para_quick_sport_mode_tennis);

        sport_type3_golf = (CheckBox)findViewById(R.id.set_para_quick_sport_mode_golf);
        sport_type3_baseball = (CheckBox)findViewById(R.id.set_para_quick_sport_mode_baseball);
        sport_type3_skiing = (CheckBox)findViewById(R.id.set_para_quick_sport_mode_rope_skiing);
        sport_type3_roller_skating = (CheckBox)findViewById(R.id.set_para_quick_sport_mode_roller_skating);
        sport_type3_dance = (CheckBox)findViewById(R.id.set_para_quick_sport_mode_dance);
    }


    public void toSetQuickSportMode(View v){

        QuickSportMode quickSportMode = new QuickSportMode();
        quickSportMode.sport_type0_walk = sport_type0_walk.isChecked();
        quickSportMode.sport_type0_run = sport_type0_run.isChecked();
        quickSportMode.sport_type0_by_bike = sport_type0_by_bike.isChecked();
        quickSportMode.sport_type0_on_foot = sport_type0_on_foot.isChecked();
        quickSportMode.sport_type0_swim = sport_type0_swim.isChecked();
        quickSportMode.sport_type0_mountain_climbing = sport_type0_mountain_climbing.isChecked();
        quickSportMode.sport_type0_badminton = sport_type0_badminton.isChecked();
        quickSportMode.sport_type0_other = sport_type0_other.isChecked();

        quickSportMode.sport_type1_fitness = sport_type1_fitness.isChecked();
        quickSportMode.sport_type1_spinning = sport_type1_spinning.isChecked();
        quickSportMode.sport_type1_ellipsoid = sport_type1_ellipsoid.isChecked();
        quickSportMode.sport_type1_treadmill = sport_type1_treadmill.isChecked();
        quickSportMode.sport_type1_sit_up = sport_type1_sit_up.isChecked();
        quickSportMode.sport_type1_push_up = sport_type1_push_up.isChecked();
        quickSportMode.sport_type1_dumbbell = sport_type1_dumbbell.isChecked();
        quickSportMode.sport_type1_weightlifting = sport_type1_weightlifting.isChecked();

        quickSportMode.sport_type2_bodybuilding_exercise = sport_type2_bodybuilding_exercise.isChecked();
        quickSportMode.sport_type2_yoga = sport_type2_yoga.isChecked();
        quickSportMode.sport_type2_rope_skipping = sport_type2_rope_skipping.isChecked();
        quickSportMode.sport_type2_table_tennis = sport_type2_table_tennis.isChecked();
        quickSportMode.sport_type2_basketball = sport_type2_basketball.isChecked();
        quickSportMode.sport_type2_footballl = sport_type2_footballl.isChecked();
        quickSportMode.sport_type2_volleyball = sport_type2_volleyball.isChecked();
        quickSportMode.sport_type2_tennis = sport_type2_tennis.isChecked();

        quickSportMode.sport_type3_golf = sport_type3_golf.isChecked();
        quickSportMode.sport_type3_baseball = sport_type3_baseball.isChecked();
        quickSportMode.sport_type3_skiing = sport_type3_skiing.isChecked();
        quickSportMode.sport_type3_roller_skating = sport_type3_roller_skating.isChecked();
        quickSportMode.sport_type3_dance = sport_type3_dance.isChecked();

//        BLEManager.setQuickSportMode(quickSportMode);

        SportModeSortV3 sportModeSortV3 = new SportModeSortV3();
        sportModeSortV3.num = 2;
        sportModeSortV3.item = new ArrayList<>();

        SportModeSortV3.SportModeSortItemV3 itemV3 = new SportModeSortV3.SportModeSortItemV3();
        itemV3.index =1;
        itemV3.type = SportType.SPORT_TYPE_SWIM;
        sportModeSortV3.item.add(itemV3);

        SportModeSortV3.SportModeSortItemV3 itemV31 = new SportModeSortV3.SportModeSortItemV3();
        itemV31.index =2;
        itemV31.type = SportType.SPORT_TYPE_WALK;
        sportModeSortV3.item.add(itemV31);

        BLEManager.setSportModeSortInfoV3(sportModeSortV3);
    }
}
