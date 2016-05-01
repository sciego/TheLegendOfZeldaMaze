package com.cfai.maze;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class HowToPlayActivity extends Activity {

    private MediaPlayer _mediaPlayerBGM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Fullscreem
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.activity_how_to_play);

        ((Button) findViewById(R.id.Button_Back)).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(HowToPlayActivity.this, MainMenuActivity.class);
                HowToPlayActivity.this.startActivity(intent);
                HowToPlayActivity.this.finish();
            }
        });

        _mediaPlayerBGM = MediaPlayer.create(this, R.raw.htp_bgm);
        _mediaPlayerBGM.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.how_to_play, menu);
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
    }

}
