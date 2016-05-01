package Maze.Objects;

import android.graphics.Color;

public class Wall extends GameObject {
    private boolean _enabled;
    private int _frame, _totalFrames;

    // Properties
    public int getFrame(){
        return _frame;
    }
    public void setFrame(int f){
        _frame = f;
    }
    public int getTotalFrames(){
        return _totalFrames;
    }
    public boolean isEnabled(){
        return _enabled;
    }
    public void disable(){
        _enabled = false;
    }

    // Constructors
    public Wall(float w, float h, float x, float y, int color, int floor){
        super(w, h, x, y, color, floor);
        _enabled = true;
    }

    public Wall(float w, float h, float x, float y, int color, int frames, int floor){
        super(w, h, x, y, color, floor);
        _enabled = true;
        _frame = 1;
        _totalFrames = frames;
    }
    
}
