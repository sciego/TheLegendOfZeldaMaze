package com.cfai.maze;

import AndroidMaze.BaseScales;
import AndroidMaze.IPopupWindowFactory;
import AndroidMaze.IPopupWindowFactoryClient;
import AndroidMaze.MazeGraphicsEngine;
import AndroidMaze.PlayerInputsFacade;
import AndroidMaze.PopupWindowFactory;
import AndroidMaze.SoundEngine;
import AndroidMaze.StatusBar;
import CFAIJoystick.IJoystickClient;
import CFAIJoystick.Joystick;
import CFAIJoystick.Move;
import Maze.IGameCallback;
import Maze.MazeCreator;
import Maze.MazeGameEngine;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

public class GameActivity extends Activity implements SensorEventListener, IJoystickClient, IGameCallback, IPopupWindowFactoryClient {
 
    // Fields   
    private MazeGameEngine _gameEngine;
    private PlayerInputsFacade _playerInputsFacade;
    private SensorManager _sensorManager;
    private Handler _handler;
    private Sensor _sensor;
    private WakeLock _wakeLock;
    private LinearLayout _mainLayout;
    private ImageButton _buttonAttack, _buttonGuard, _buttonMoveRight, _buttonMoveLeft, _buttonMoveUp, _buttonMoveDown;
    private boolean _layoutsCreated, _finish;
    private PopupWindowFactory _popupWindowFactory;
    private PopupWindow _pausePopupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // FULLSCREEN
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (MainMenuActivity.usingAccelerometer()){ // Accelerometer controls
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setContentView(R.layout.activity_game_portrait);
            _sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
            _sensor = _sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            _sensorManager.registerListener(this, _sensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
        else { // Landscape
            setLandscapeLayout();
        }
            
        // Common buttons
        _buttonAttack = (ImageButton) findViewById(R.id.ImageButton_Attack);
        _buttonAttack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _gameEngine.playerAttack();
            }
        });
        _buttonGuard = (ImageButton) findViewById(R.id.ImageButton_Guard);
        _buttonGuard.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    _gameEngine.playerGuard(true);
                else if (event.getAction() == MotionEvent.ACTION_UP)
                    _gameEngine.playerGuard(false);
                return false;
            }
        });

        // Prevent auto-lock
        PowerManager powerManager = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
        _wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");
        _wakeLock.acquire();
        
        _popupWindowFactory = new PopupWindowFactory(this, this);
        _pausePopupWindow = _popupWindowFactory.create("PAUSE", IPopupWindowFactory.Window.PAUSE);

        _handler = new Handler(){
            @Override
            public void handleMessage(android.os.Message msg){
                handleGameMessage(msg);
            }
        };

        _layoutsCreated = false;
        _mainLayout = (LinearLayout) findViewById(R.id.LinearLayout_Main);
        setGame();
        _playerInputsFacade = new PlayerInputsFacade(_gameEngine);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void setLandscapeLayout(){
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        if (MainMenuActivity.usingJoystick()){
            setContentView(R.layout.activity_game_landscape_joystick);
            LinearLayout layout = (LinearLayout) findViewById(R.id.LinearLayout_Joystick);
            layout.addView(new Joystick(this, this, R.drawable.joystick_ring, R.drawable.joystick_ball));
        } else {
            if (getResources().getBoolean(R.bool.isTablet)) {
                setContentView(R.layout.activity_game_landscape_arrows);
            } else {
                setContentView(R.layout.activity_game_landscape_arrows_pad);
            }
            _buttonMoveRight = (ImageButton) findViewById(R.id.ImageButton_MoveRight);
            _buttonMoveRight.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN)
                        _gameEngine.movePlayerRight(true);
                    else if (event.getAction() == MotionEvent.ACTION_UP)
                        _gameEngine.movePlayerRight(false);
                    return false;
                }
            });
            _buttonMoveLeft = (ImageButton) findViewById(R.id.ImageButton_MoveLeft);
            _buttonMoveLeft.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN)
                        _gameEngine.movePlayerLeft(true);
                    else if (event.getAction() == MotionEvent.ACTION_UP)
                        _gameEngine.movePlayerLeft(false);
                    return false;
                }
            });
            _buttonMoveUp = (ImageButton) findViewById(R.id.ImageButton_MoveUp);
            _buttonMoveUp.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN)
                        _gameEngine.movePlayerUp(true);
                    else if (event.getAction() == MotionEvent.ACTION_UP)
                        _gameEngine.movePlayerUp(false);
                    return false;
                }
            });
            _buttonMoveDown = (ImageButton) findViewById(R.id.ImageButton_MoveDown);
            _buttonMoveDown.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN)
                        _gameEngine.movePlayerDown(true);
                    else if (event.getAction() == MotionEvent.ACTION_UP)
                        _gameEngine.movePlayerDown(false);
                    return false;
                }
            });
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);
        
        if (!_layoutsCreated){
            RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.RelativeLayout_Controls_StatusBar);
            LinearLayout statusLayout = (LinearLayout) findViewById(R.id.LinearLayout_Status);
            int statusWidth = relativeLayout.getWidth();
            int buttonWidth = _buttonAttack.getWidth();
            statusWidth -= buttonWidth * 2;
            
            boolean isTablet = getResources().getBoolean(R.bool.isTablet);
            if (MainMenuActivity.usingArrows())
                statusWidth -= buttonWidth * 4;
            else if (MainMenuActivity.usingJoystick())
                statusWidth -= buttonWidth;

            statusLayout.getLayoutParams().width = statusWidth;
            _pausePopupWindow.setWidth(BaseScales.dpToPx(350));
            _layoutsCreated = true;
            _gameEngine.start();
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        _gameEngine.pause();
        _wakeLock.release();
        if (_finish){
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        if (_pausePopupWindow.isShowing() == false)
            _gameEngine.resume();
        _wakeLock.acquire();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        _wakeLock.release();
        _gameEngine.finish();
    }

    private void setGame(){
        MazeGraphicsEngine graphicsEngine = new MazeGraphicsEngine(this);
        StatusBar statusbar = new StatusBar(this);
        SoundEngine soundEngine = new SoundEngine(this);
        float[] lengths = {BaseScales.BOARD_WIDTH, BaseScales.BOARD_HEIGHT, BaseScales.WALL_THICKNESS, BaseScales.SQUARE_LENGTH, BaseScales.PLAYER_WIDTH, BaseScales.PLAYER_HEIGHT, BaseScales.MOBLIN_LENGTH};

        MazeCreator mc = new MazeCreator(lengths, graphicsEngine, statusbar, soundEngine, this);        
        _gameEngine = mc.getGameEngine();

        _mainLayout.addView(graphicsEngine);
        ((LinearLayout) findViewById(R.id.LinearLayout_Status)).addView(statusbar);
    
        graphicsEngine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GameActivity.this._gameEngine.isRunning()){
                    GameActivity.this.onPause();
                    GameActivity.this._pausePopupWindow.showAtLocation(GameActivity.this._buttonAttack, Gravity.CENTER, 0, 0);
                }
            }
        });
    }

    // Implemented methods
    @Override
    public void endGame(Message result){
        Intent intent = null;

        if (result == Message.WIN) { // Player won
            intent = new Intent(this, LeaderboardActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("time", _gameEngine.getTime());
            intent.putExtras(bundle);
            LeaderboardActivity.preparePopupWindows();
        }
        else {
            intent = new Intent(this, MainMenuActivity.class);
            _finish = true;
        }

        startActivity(intent);
        finish();
    }

    @Override
    public void notify(Message alert){
        if (alert == Message.MOBLIN_APPEARED){
            _handler.sendEmptyMessage(2);
        } else if (alert == Message.EXIT_APPEARED){
            _handler.sendEmptyMessage(3);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int moveX = (int) event.values[0];
        int moveY = (int) event.values[1];
        _playerInputsFacade.move(moveX, moveY);   
    }

    @Override
    public void move(Move direction){
        switch (direction){
            case RIGHT:
                _gameEngine.movePlayerRight(true);
                _gameEngine.movePlayerLeft(false);
                _gameEngine.movePlayerUp(false);
                _gameEngine.movePlayerDown(false);
                break;
            case LEFT:
                _gameEngine.movePlayerLeft(true);
                _gameEngine.movePlayerRight(false);
                _gameEngine.movePlayerUp(false);
                _gameEngine.movePlayerDown(false);
                break;
            case UP:
                _gameEngine.movePlayerUp(true);
                _gameEngine.movePlayerRight(false);
                _gameEngine.movePlayerLeft(false);
                _gameEngine.movePlayerDown(false);
                break;
            case DOWN:
                _gameEngine.movePlayerDown(true);
                _gameEngine.movePlayerRight(false);
                _gameEngine.movePlayerLeft(false);
                _gameEngine.movePlayerUp(false);
                break;
            case NONE:
                _gameEngine.movePlayerRight(false);
                _gameEngine.movePlayerLeft(false);
                _gameEngine.movePlayerUp(false);
                _gameEngine.movePlayerDown(false);
                break;
        }
    }

    @Override
    public void popupWindowEvent(Object arg){
        int value = Integer.valueOf(arg.toString());

        if (value == 2){
            _handler.sendEmptyMessage(1);
        }
        else if (value == 1) {
            _pausePopupWindow.dismiss();
            onResume();
        }
    }

    private void handleGameMessage(android.os.Message msg){
        PopupWindow alertPopupWindow = null;

        if (msg.what == 1){
            endGame(Message.LOSE);
        } else if (msg.what == 2){
            alertPopupWindow = _popupWindowFactory.create("HEY!", (String)getResources().getText(R.string.moblin_appeared));
        } else if (msg.what == 3){
            alertPopupWindow = _popupWindowFactory.create("HEY!", (String)getResources().getText(R.string.exit_available));
        }

        if (alertPopupWindow != null){
            try {
                Thread.sleep(1000);
            } catch (Exception e) { }
            alertPopupWindow.setWidth(_pausePopupWindow.getWidth());
            alertPopupWindow.showAtLocation(GameActivity.this._buttonAttack, Gravity.CENTER, 0, 0);
            MediaPlayer mp = MediaPlayer.create(this, R.raw.listen);
            mp.start();
         }
    }

}