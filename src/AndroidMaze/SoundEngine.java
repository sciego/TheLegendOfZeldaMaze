package AndroidMaze;

import Maze.ISoundEngine;
import android.content.Context;
import android.media.MediaPlayer;

import com.cfai.maze.R;

public class SoundEngine implements ISoundEngine {
    // Fields
    private MediaPlayer[] _mediaPlayers;
    private int _currentBGM;

    // Constructor
    public SoundEngine(Context context){
        _mediaPlayers = new MediaPlayer[Sound.values().length];
        _mediaPlayers[0] = MediaPlayer.create(context, R.raw.link_sword1);
        _mediaPlayers[1] = MediaPlayer.create(context, R.raw.link_sword2);
        _mediaPlayers[2] = MediaPlayer.create(context, R.raw.link_sword3);
        _mediaPlayers[3] = MediaPlayer.create(context, R.raw.link_shield);
        _mediaPlayers[4] = MediaPlayer.create(context, R.raw.link_hurt);
        _mediaPlayers[5] = MediaPlayer.create(context, R.raw.link_killed);
        _mediaPlayers[6] = MediaPlayer.create(context, R.raw.sword);
        _mediaPlayers[7] = MediaPlayer.create(context, R.raw.rupee);
        _mediaPlayers[8] = MediaPlayer.create(context, R.raw.cut_grass);
        _mediaPlayers[9] = MediaPlayer.create(context, R.raw.secret);
        _mediaPlayers[10] = MediaPlayer.create(context, R.raw.deku_nut);
        _mediaPlayers[11] = MediaPlayer.create(context, R.raw.enemy_pawn);
        _mediaPlayers[12] = MediaPlayer.create(context, R.raw.enemy_guard);
        _mediaPlayers[13] = MediaPlayer.create(context, R.raw.enemy_killed);
        _mediaPlayers[14] = MediaPlayer.create(context, R.raw.game_bgm);
        _mediaPlayers[15] = MediaPlayer.create(context, R.raw.battle);
        _mediaPlayers[16] = MediaPlayer.create(context, R.raw.game_over);
        _mediaPlayers[17] = MediaPlayer.create(context, R.raw.walk_grass);
        _mediaPlayers[18] = MediaPlayer.create(context, R.raw.walk_bridge);
        _mediaPlayers[19] = MediaPlayer.create(context, R.raw.heart);
        _mediaPlayers[20] = MediaPlayer.create(context, R.raw.ganon_bgm);
        _mediaPlayers[21] = MediaPlayer.create(context, R.raw.ganon_fireballs);
        _mediaPlayers[22] = MediaPlayer.create(context, R.raw.ganon_ball);
        _mediaPlayers[23] = MediaPlayer.create(context, R.raw.ganon_ball_hit);
        _mediaPlayers[24] = MediaPlayer.create(context, R.raw.ganon_bounce);
        _mediaPlayers[25] = MediaPlayer.create(context, R.raw.ganon_teleport);        
        _mediaPlayers[26] = MediaPlayer.create(context, R.raw.ganon_kill);
        _mediaPlayers[27] = MediaPlayer.create(context, R.raw.ending);

        _mediaPlayers[14].setLooping(true);
        _mediaPlayers[15].setLooping(true);
        _mediaPlayers[20].setLooping(true);

        _currentBGM = -1;
    }
    
    // Implementes Methods
    @Override
    public void playSound(Sound sound){
        int index = 0;
        for (int i = 0; i < Sound.values().length; i++){
            if (sound == Sound.values()[i]){
                index = i;
                break;
            }
        }

        _mediaPlayers[index].start();
        if (_mediaPlayers[index].isLooping())
            _currentBGM = index;
    }

    @Override
    public void stopSound(Sound sound){
        int index = 0;
        for (int i = 0; i < Sound.values().length; i++){
            if (sound == Sound.values()[i]){
                index = i;
                break;
            }
        }

        if (_mediaPlayers[index].isLooping())
            _mediaPlayers[index].pause();
        else
            _mediaPlayers[index].stop();
    }
 
    public void pause(){
        for (MediaPlayer mp : _mediaPlayers){
            if (mp.isPlaying() && mp.isLooping())
                mp.pause();
        }
    }
    
    public void resume(){
        for (int i = 0; i < _mediaPlayers.length; i++){
            if (_currentBGM == i)
                _mediaPlayers[i].start();
        }
    }

    public void finish(){
        for (MediaPlayer mp : _mediaPlayers){
            try {
                mp.release();
            } catch (Exception e) { }
        }
    }
    
    
}