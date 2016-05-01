package Maze.Processing;

import Maze.Characters.Character;
import Maze.Characters.Character.State;
import Maze.Characters.Ganon;
import Maze.Characters.Ganon.Ball;
import Maze.Objects.Wall;

public class GanonFireAttackStrategy implements IAIStrategy {
    // Fields
    private Ganon _cpu;
    private Character _target;
    private CollitionsProcessor _collitionsProcessor;
    
    // Constructor
    public GanonFireAttackStrategy(Ganon seeker, Character target, CollitionsProcessor cp){
        _cpu = seeker;
        _target = target;
        _collitionsProcessor = cp;
        setFireBalls();
    }

    // Implemented methods
    public State operate(){
        moveFireBalls();
        if (targetGotHit())
            return State.HURT;
        else if (allFireBallsDisabled())
            return State.STANDING;
        return _cpu.state();
    }

    // Methods
    private boolean allFireBallsDisabled(){
        for (Ball b : _cpu.getFireBalls()){
            if (b.isEnabled())
                return false;
        }
        return true;
    }

    private void disableFireBalls(){
        for (Ball b : _cpu.getFireBalls())
            b.toggle(false);
    }

    private void setFireBalls(){
        int i;
        for (Ball b : _cpu.getFireBalls())
            b.resetLocation();
        for (i = 0; i < 16; i++)
            _cpu.getFireBalls()[i].toggle(true);
        for (i = 16; i < 28; i++)
            _cpu.getFireBalls()[i].toggle(false);
        for (i = 28; i < 36; i++)
            _cpu.getFireBalls()[i].toggle(false);
    }

    private void moveFireBalls(){
        for (Ball b : _cpu.getFireBalls()){
            if (b.isEnabled() == false)
                continue;

            b.setLocationX(b.getLocationX() + b.distanceX());
            b.setLocationY(b.getLocationY() + b.distanceY());
            if (b.getFrame() == 1)
                b.setFrame(2);
            else
                b.setFrame(1);
        }
        checkForCollitions();
    }

    private boolean targetGotHit(){
        for (Ball b : _cpu.getFireBalls()){
            if (b.isEnabled() && _collitionsProcessor.characterCollidedWithGameObject(_target, b)){
                b.toggle(false);
                return true;
            }
        }
        return false;
    }

    private void checkForCollitions(){
        for (Ball b : _cpu.getFireBalls()){
            if (b.isEnabled() == false)
                continue;

            for (Wall w : _collitionsProcessor.getWalls()){
                if (_collitionsProcessor.gameObjectsIntersectedInX(b, w) && _collitionsProcessor.gameObjectsIntersectedInY(b, w)){
                    b.toggle(false);
                    break;
                }
            }
        }
    }
    
}
