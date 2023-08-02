package test.com.ido.runplan.page;

import com.ido.ble.LocalDataManager;
import com.ido.ble.protocol.model.SupportFunctionInfo;

public class BaseCmdPresenter {
    public SupportFunctionInfo getSupportFunctionInfo() {
        SupportFunctionInfo functionInfo = LocalDataManager.getSupportFunctionInfo();
        return functionInfo == null ? new SupportFunctionInfo() : functionInfo;
    }
}
