package com.cfai.maze;

import AndroidMaze.BaseScales;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

public class MainMenuActivity extends Activity implements IPopupWindowFactoryClient {

    private FrameLayout _backgroundLayout;
    private LeaderboardManager _leaderboardManager;
    private String FILENAME = "leaderboard";
    private PopupWindow _settingsPopupWindow, _aboutPopupWindow;
    private Button _settingsButton, _aboutButton;
    private MediaPlayer _mediaPlayerBGM, _mediaPlayerStartGame, _mediaPlayerAbout;
    private static boolean USE_ACCELEROMETER, USE_JOYSTICK, USE_ARROWS = true;

    // Properties
    public static boolean usingAccelerometer(){
        return USE_ACCELEROMETER;
    }
    public static boolean usingArrows(){
        return USE_ARROWS;
    }
    public static boolean usingJoystick(){
        return USE_JOYSTICK;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.activity_main_menu);
        _backgroundLayout = (FrameLayout) findViewById(R.id.FrameLayout_Main);
        _backgroundLayout.getForeground().setAlpha(0);

        IPopupWindowFactory popupFactory = new PopupWindowFactory(this, this);
        _settingsPopupWindow = popupFactory.create("SETTINGS", IPopupWindowFactory.Window.SETTINGS);
        _aboutPopupWindow = popupFactory.create("ABOUT", "By Carlos Almonte");

        findViewById(R.id.Button_StartGame).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                MainMenuActivity.this._mediaPlayerStartGame.start();
                Intent intent = new Intent(MainMenuActivity.this, GameActivity.class);
                MainMenuActivity.this.startActivity(intent);
                MainMenuActivity.this.finish();
            }
        });
        Button bossBattleButton = (Button) findViewById(R.id.Button_BossBattle);
        bossBattleButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainMenuActivity.this, BossBattleActivity.class);
                MainMenuActivity.this.startActivity(intent);
                MainMenuActivity.this.finish();
            }
        });
        findViewById(R.id.Button_Leaderboard).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainMenuActivity.this, LeaderboardActivity.class);
                MainMenuActivity.this.startActivity(intent);
                MainMenuActivity.this.finish();
            }
        });
        _settingsButton = (Button) findViewById(R.id.Button_Settings);
        _settingsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                showSettings();
            }
        });
        findViewById(R.id.Button_How_To_Play).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainMenuActivity.this, HowToPlayActivity.class);
                MainMenuActivity.this.startActivity(intent);
                MainMenuActivity.this.finish();
            }
        });
        _aboutButton = (Button) findViewById(R.id.Button_About);
        _aboutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                showAboutPopup();
            }
        });

        BaseScales.DISPLAY_METRICS = getResources().getDisplayMetrics();
        _leaderboardManager = new LeaderboardManager(this, FILENAME);
        _leaderboardManager.loadRecords();
        _mediaPlayerBGM = MediaPlayer.create(this, R.raw.title);
        _mediaPlayerStartGame = MediaPlayer.create(this, R.raw.game_start);
        _mediaPlayerAbout = MediaPlayer.create(this, R.raw.hey);
        _mediaPlayerBGM.start();
        
        //if (_leaderboardManager.getRecords()[0][0] == null)
            //bossBattleButton.setEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    protected void onPause(){
        super.onPause();
        if (_mediaPlayerBGM.isPlaying())
            _mediaPlayerBGM.pause();
    }

    @Override
    protected void onResume(){
        super.onResume();
        if (!_mediaPlayerBGM.isPlaying())
            _mediaPlayerBGM.start();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if (_mediaPlayerBGM.isPlaying())
            _mediaPlayerBGM.stop();
        _mediaPlayerBGM.release();
        _mediaPlayerStartGame.release();
        _mediaPlayerAbout.release();
    }

    private void showSettings(){
        _backgroundLayout.getForeground().setAlpha(180);
        _settingsPopupWindow.showAtLocation(_settingsButton, Gravity.CENTER, 0, 0);
    }

    private void showAboutPopup(){
        _mediaPlayerAbout.start();
        _aboutPopupWindow.showAtLocation(_aboutButton, Gravity.CENTER, 0, 0);
    }

    // Implemented methods
    @Override
    public void popupWindowEvent(Object arg){
        int value = Integer.parseInt(arg.toString());
        if (value == 2){
            USE_ARROWS = true;
            USE_JOYSTICK = false;
            USE_ACCELEROMETER = false;
        }
        else if (value == 3){
            USE_ARROWS = false;
            USE_JOYSTICK = false;
            USE_ACCELEROMETER = true;
        }
        else if (value == 4){
            USE_ARROWS = false;
            USE_JOYSTICK = true;
            USE_ACCELEROMETER = false;
        }
        else if (value == 1) {
            _settingsPopupWindow.dismiss();
            _backgroundLayout.getForeground().setAlpha(0);
        }
    }

}
