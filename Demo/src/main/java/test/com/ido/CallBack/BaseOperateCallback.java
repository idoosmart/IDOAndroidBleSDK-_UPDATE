package test.com.ido.CallBack;

import com.ido.ble.callback.OperateCallBack;

/**
 * @author tianwei
 * @date 2022/11/3
 * @time 11:08
 * 用途:
 */
public class BaseOperateCallback implements OperateCallBack.ICallBack {

    @Override
    public void onSetResult(OperateCallBack.OperateType operateType, boolean b) {

    }

    @Override
    public void onAddResult(OperateCallBack.OperateType operateType, boolean b) {

    }

    @Override
    public void onDeleteResult(OperateCallBack.OperateType operateType, boolean b) {

    }

    @Override
    public void onModifyResult(OperateCallBack.OperateType operateType, boolean b) {

    }

    @Override
    public void onQueryResult(OperateCallBack.OperateType operateType, Object o) {

    }
}
