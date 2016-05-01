package Maze.Characters;

import Maze.Processing.CollitionsProcessor;
import android.R.string;
import android.graphics.Color;

public class Player extends Character {
    
    // Fields
    private int _rupees;

    // Properties
    public int getRupees(){
        return _rupees;
    }
    public void setRupees(int amount){
        _rupees += amount;
    }
    public void increaseLife(int amount){
        _life += amount;
        if (_life > _maxLife)
            _life = _maxLife;
    }
    
    // Constructors
    public Player(float width, float height, float x, float y, int speed, int frames, int floor, CollitionsProcessor cp){
        super(width, height, x, y, speed, frames, floor, cp);
        setDirection(Character.FACING.SOUTH);
        _maxLife = _life = 6;
        _rupees = 0;
        _attackRange = 0.8f * width;
    }
    
}
