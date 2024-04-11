package com.ido.jielidial.model;

/**
 * @author tianwei
 * @date 2024/3/18
 * @time 11:34
 * 用途:
 */
public class DialConfig {
    //目标文件路径
    private String targetFilePath;
    //背景图路径
    private String bgPath;
    //预览图路径
    private String previewPath;
    //时间组件颜色
    private int timeColor;

    public String getTargetFilePath() {
        return targetFilePath;
    }

    public void setTargetFilePath(String targetFilePath) {
        this.targetFilePath = targetFilePath;
    }

    public String getBgPath() {
        return bgPath;
    }

    public void setBgPath(String bgPath) {
        this.bgPath = bgPath;
    }

    public String getPreviewPath() {
        return previewPath;
    }

    public void setPreviewPath(String previewPath) {
        this.previewPath = previewPath;
    }

    public int getTimeColor() {
        return timeColor;
    }

    public void setTimeColor(int timeColor) {
        this.timeColor = timeColor;
    }
}
