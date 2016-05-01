package AndroidMaze;

import android.util.DisplayMetrics;

public final class BaseScales {
    public static final int CAMERA_WIDTH = 480;
    public static final int CAMERA_HEIGHT = 630;
    public static final float BOARD_WIDTH = 912;
    public static final float BOARD_HEIGHT = 1192;
    public static final float BOSS_BOARD_WIDTH = 520;
    public static final float BOSS_BOARD_HEIGHT = 296;
    public static final float WALL_THICKNESS = 16;
    public static final float SQUARE_LENGTH = 40;
    public static final float PLAYER_WIDTH = 18f;
    public static final float PLAYER_HEIGHT = 20f;
    public static final float MOBLIN_LENGTH = 23.5f;
    
    public static DisplayMetrics DISPLAY_METRICS;
    
    public static int dpToPx(int dp){
        if (DISPLAY_METRICS == null)
            return 0;
        int px = Math.round(dp * (DISPLAY_METRICS.xdpi / DisplayMetrics.DENSITY_DEFAULT));       
        return px;
    }

    public static int pxToDp(int px){
        if (DISPLAY_METRICS == null)
            return 0;
        int dp = Math.round(px / (DISPLAY_METRICS.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

}
