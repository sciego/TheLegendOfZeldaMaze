package Maze.Processing;

import java.util.Random;

import Maze.Characters.Character;
import Maze.Characters.Character.FACING;
import Maze.Characters.Character.State;
import Maze.Characters.Ganon;

public class GanonBounceAttackStrategy implements IAIStrategy {
    // Fields
    protected Ganon _cpu;
    protected Character _target;
    protected CollitionsProcessor _collitionsProcessor;
    protected boolean _movePosX, _movePosY;
    
    // Constructor
    public GanonBounceAttackStrategy(Ganon seeker, Character target, CollitionsProcessor cp){
        _cpu = seeker;
        _target = target;
        _collitionsProcessor = cp;
        
        Random r = new Random();
        if (r.nextInt(2) == 1)
            _movePosX = true;
        if (r.nextInt(2) == 1)
            _movePosY = true;
    }

    // Implemented methods
    public State operate(){
        int i = move();
        if (i == 0) // Character list index #0 (Player).
            return State.HURT;
        else if (i > 0)
            return State.GUARDING;
        return _cpu.state();
    }

    // Methods
    protected int move(){
        int i = -1;

        if (_movePosX){
            if ((i = _cpu.move(FACING.EAST)) > -1)
                _movePosX = false;
        }
        else {
            if ((i = _cpu.move(FACING.WEST)) > -1)
                _movePosX = true;
        }
        if (_movePosY){
            if ((i = _cpu.move(FACING.SOUTH)) > -1)
                _movePosY = false;
        }
        else {
            if ((i = _cpu.move(FACING.NORTH)) > -1)
                _movePosY = true;
        }

        return i;
    }
    
}
