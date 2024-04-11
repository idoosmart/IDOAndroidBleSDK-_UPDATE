package com.ido.jielidial.utils;

import java.util.HashMap;
import java.util.Map;

public class Constants {
    public static int TOP_LEFT = 100;
    public static int TOP_CENTER = 101;
    public static int TOP_RIGHT = 102;
    public static int CENTER_LEFT = 103;
    public static int CENTER_CENTER = 104;
    public static int CENTER_RIGHT = 105;
    public static int BOTTOM_LEFT = 106;
    public static int BOTTOM_CENTER = 107;
    public static int BOTTOM_RIGHT = 108;
    public static int FONT_TYPE_ONE = 109;
    public static int FONT_TYPE_TWO = 110;
    public static int FONT_TYPE_THREE = 111;
    public static int WIDTH = 240;
    public static int HEIGHT = 284;
    public static int PREVIEW_WIDTH = 164;
    public static int PREVIEW_HEIGHT = 191;

    public static Map<Integer, String> FILE_NAME = new HashMap<>();
    public static void init() {
        FILE_NAME.put(TOP_LEFT, "Top-Left");
        FILE_NAME.put(TOP_CENTER, "Top-Centre");
        FILE_NAME.put(TOP_RIGHT, "Top-Right");
        FILE_NAME.put(CENTER_LEFT, "Centre-Left");
        FILE_NAME.put(CENTER_CENTER, "Centre-Centre");
        FILE_NAME.put(CENTER_RIGHT, "Centre-Right");
        FILE_NAME.put(BOTTOM_LEFT, "Bottom-Left");
        FILE_NAME.put(BOTTOM_CENTER, "Bottom-Centre");
        FILE_NAME.put(BOTTOM_RIGHT, "Bottom-Right");
    }
    public static String getName(int placement) {
        if (FILE_NAME.containsKey(placement)) {
            return FILE_NAME.get(placement);
        }
        return String.valueOf(placement);
    }
}
