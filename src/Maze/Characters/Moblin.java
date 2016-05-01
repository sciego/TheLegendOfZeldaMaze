package Maze.Characters;

import Maze.Processing.CollitionsProcessor;
import Maze.Processing.IAIStrategy;

public class Moblin extends Character {
    // Fields
    private IAIStrategy _strategy;

    // Properties
    public void setStrategy(IAIStrategy strategy){
        _strategy = strategy;
    }
    
    // Constructor
    public Moblin(float width, float height, float x, float y, int speed, int frames, int floor, CollitionsProcessor cp){
        super(width, height, x, y, speed, frames, floor, cp);
        setDirection(Character.FACING.WEST);
        _maxLife = _life = 0; // So it can be pawned.
        _attackRange = 0.7f * width;
    }

    // Methods
    public State operate(){
        return _strategy.operate();
    }

    public void pawn(int life){
        _maxLife = _life = life;
    }

}
