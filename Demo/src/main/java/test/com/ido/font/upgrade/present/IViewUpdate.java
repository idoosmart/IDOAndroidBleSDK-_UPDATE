package test.com.ido.font.upgrade.present;

public interface IViewUpdate {
    void onUpdateFontFileStart();
    void onUpdateFontFileProgress(int progress);
    void onUpdateFontFileSuccess();
    void onUpdateFontFileFailed();

    void onUpdateBinFileStart();
    void onUpdateBinFileProgress(int progress);
    void onUpdateBinFileSuccess();
    void onUpdateBinFileFailed();

    void onUpdateOtaFileStart();
    void onUpdateOtaFileProgress(int progress);
    void onUpdateOtaFileSuccess();
    void onUpdateOtaFileFailed();

    void onTaskFailed();
    void onTaskSuccess();
    void onTaskStart();

    void onLostTime(long second);
}
