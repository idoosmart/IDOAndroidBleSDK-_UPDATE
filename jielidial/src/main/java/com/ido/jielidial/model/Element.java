package com.ido.jielidial.model;

import androidx.annotation.NonNull;

import java.util.List;

public class Element {
    private int offset;
    private int index;
    private int width;
    private int height;
    private int x = 0;
    private int y = 0;
    private byte imageCount = 1;
    private int type;
    private int hasAlpha = 1;
    private byte anchor = 9;
    private byte blackTransparent = 1;
    private int compression;
    private byte leftOffset = 0;
    private int size;
    private int fontType;
    private List<ImageData> dataList;
    private byte[] rawData;

    public static class ImageData {
        private byte[] data;
        private int size;

        public ImageData(byte[] data) {
            this.data = data;
        }

        public ImageData(byte[] data, int size) {
            this.data = data;
            this.size = size;
        }

        public byte[] getData() {
            return data;
        }

        public void setData(byte[] data) {
            this.data = data;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public byte getImageCount() {
        return imageCount;
    }

    public void setImageCount(byte imageCount) {
        this.imageCount = imageCount;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getHasAlpha() {
        return hasAlpha;
    }

    public void setHasAlpha(int hasAlpha) {
        this.hasAlpha = hasAlpha;
    }

    public byte getAnchor() {
        return anchor;
    }

    public void setAnchor(byte anchor) {
        this.anchor = anchor;
    }

    public byte getBlackTransparent() {
        return blackTransparent;
    }

    public void setBlackTransparent(byte blackTransparent) {
        this.blackTransparent = blackTransparent;
    }

    public int getCompression() {
        return compression;
    }

    public void setCompression(int compression) {
        this.compression = compression;
    }

    public byte getLeftOffset() {
        return leftOffset;
    }

    public void setLeftOffset(byte leftOffset) {
        this.leftOffset = leftOffset;
    }

    public List<ImageData> getDataList() {
        return dataList;
    }

    public void setDataList(List<ImageData> dataList) {
        this.dataList = dataList;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getFontType() {
        return fontType;
    }

    public void setFontType(int fontType) {
        this.fontType = fontType;
    }

    public byte[] getRawData() {
        return rawData;
    }

    public void setRawData(byte[] rawData) {
        this.rawData = rawData;
    }

    @NonNull
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
