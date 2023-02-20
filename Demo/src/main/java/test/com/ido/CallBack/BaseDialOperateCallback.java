package test.com.ido.CallBack;

import com.ido.ble.watch.custom.callback.WatchPlateCallBack;
import com.ido.ble.watch.custom.model.DialPlateParam;
import com.ido.ble.watch.custom.model.WatchPlateFileInfo;
import com.ido.ble.watch.custom.model.WatchPlateScreenInfo;

/**
 * @Author: ym
 * @ClassName: BaseDialOperateCallback
 * @Description: 表盘操作的回调
 * @Package: com.ido.life.ble
 * @CreateDate: 2020/5/29 0029 17:49
 */
public class BaseDialOperateCallback implements WatchPlateCallBack.IOperateCallBack {
    @Override
    public void onGetPlateFileInfo(WatchPlateFileInfo watchPlateFileInfo) {

    }

    @Override
    public void onGetScreenInfo(WatchPlateScreenInfo watchPlateScreenInfo) {

    }

    @Override
    public void onGetCurrentPlate(String s) {

    }

    @Override
    public void onSetPlate(boolean b) {

    }

    @Override
    public void onDeletePlate(boolean b) {

    }

    @Override
    public void onGetDialPlateParam(DialPlateParam dialPlateParam) {

    }
}
