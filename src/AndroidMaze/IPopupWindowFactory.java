package AndroidMaze;

import android.content.Context;
import android.view.View;
import android.widget.PopupWindow;

public interface IPopupWindowFactory {
    public enum Window {
        SETTINGS, ABOUT, PAUSE, LEADERBOARD_ENTRY
    }

    PopupWindow create(String title, Window window);
    PopupWindow create(String title, String message);
}
