package com.cfai.maze;

import AndroidMaze.IPopupWindowFactory;
import AndroidMaze.IPopupWindowFactoryClient;
import AndroidMaze.LeaderboardManager;
import AndroidMaze.PopupWindowFactory;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class LeaderboardActivity extends Activity implements IPopupWindowFactoryClient{

    private LeaderboardManager _leaderboardManager;
    private String FILENAME = "leaderboard";
    private int _time, _place;
    private FrameLayout _backgroundLayout;
    private PopupWindow _popup;
    private Button _backButton;
    private MediaPlayer _mediaPlayer;
    private static boolean _popupShowed = false; // Popup will open once, to prevent multiple entries of same time record

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Fullscreem
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_leaderboard);
        _backgroundLayout = (FrameLayout) findViewById(R.id.FrameLayout_Main);
        _backgroundLayout.getForeground().setAlpha(0);

        _leaderboardManager = new LeaderboardManager(this, FILENAME);
        _leaderboardManager.loadRecords();
        setTable();
        
        _backButton = (Button) findViewById(R.id.Button_Back);
        _backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(LeaderboardActivity.this, MainMenuActivity.class);
                LeaderboardActivity.this.startActivity(intent);
                LeaderboardActivity.this.finish();
            }
        });

        _mediaPlayer = MediaPlayer.create(this, R.raw.game_won);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.leaderboard, menu);
        return true;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);
        
        if (_popupShowed || getIntent().getExtras() == null)
            return;
        
        _time = getIntent().getExtras().getInt("time");
        _place = newRecord(_time);
        if (_place != -1){
            _mediaPlayer.start();
            showPopup();
        }
        _popupShowed = true;
    }

    public static void preparePopupWindows(){
        _popupShowed = false;
    }

    private void setTable(){
        String[][] records = _leaderboardManager.getRecords();
        TextView hPlace = (TextView) findViewById(R.id.TextView_Place);
        TextView hName = (TextView) findViewById(R.id.TextView_Name);
        TextView hTime = (TextView) findViewById(R.id.TextView_Time);

        TableLayout table = (TableLayout) findViewById(R.id.TableLayout_Records);
        table.removeAllViews();
        TableRow tr;
        TextView place, name, time;
        String placeString, formatedTime;
        int seconds;

        for (int i = 0; i < records.length; i++){
            if (records[i][0] == null)
                break;

            tr = new TableRow(this);
            tr.setPadding(10, 0, 10, 0);

            place = new TextView(this);
            placeString = String.valueOf(i+1);
            if (i+1 == 1)
                placeString += "st";
            else if (i+1 == 2)
                placeString += "nd";
            else if (i+1 == 3)
                placeString += "rd";
            else
                placeString += "th";
            place.setText(placeString);
            place.setLayoutParams(hPlace.getLayoutParams());
            tr.addView(place);

            name = new TextView(this);
            name.setText(records[i][0]);
            name.setGravity(Gravity.CENTER);
            name.setLayoutParams(hName.getLayoutParams());
            tr.addView(name);
            
            time = new TextView(this);
            seconds = Integer.parseInt(records[i][1]);
            formatedTime = String.valueOf((seconds/60)) + "m "; // Minutes
            formatedTime += String.valueOf((seconds % 60)) + "s";
            time.setText(formatedTime);
            time.setLayoutParams(hTime.getLayoutParams());
            tr.addView(time);

            tr.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            table.addView(tr);
        }
    }

    // Returns the Place for the new time, -1 if there's non.
    private int newRecord(int time){
        String[][] records = _leaderboardManager.getRecords();

        for (int i = 0; i < records.length; i++){
            if (records[i][0] == null)
                return i;
        }

        if (time < Integer.parseInt(records[records.length-1][1])) // Better than the worst time.
            return records.length-1;

        return -1;
    }

    private void submitNewRecord(String name){
        if (name.length() == 0)
            return;

        String row = name;
        row += " " + String.valueOf(_time);
        _leaderboardManager.addRecord(_place, row);
        _leaderboardManager.saveRecords();
        _leaderboardManager.sortRecords();
        setTable();
        _backgroundLayout.getForeground().setAlpha(0);
        _popup.dismiss();
    }

    private void showPopup(){        
        PopupWindowFactory popupFactory = new PopupWindowFactory(this, this);
        _popup = popupFactory.create("CONGRATULATIONS", IPopupWindowFactory.Window.LEADERBOARD_ENTRY);
        _backgroundLayout.getForeground().setAlpha(180);
        _popup.showAtLocation(_backButton, Gravity.CENTER, 0, 0);
    }

    // Implemented methods
    @Override
    public void popupWindowEvent(Object arg){
        submitNewRecord(arg.toString());
    }

}

