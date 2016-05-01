package Maze.Processing;

import java.util.Random;

import Maze.Characters.Character;
import Maze.Characters.Character.FACING;
import Maze.Characters.Character.State;
import Maze.Characters.Ganon;
import Maze.Objects.Wall;

public class GanonThunderAttackStrategy implements IAIStrategy {
    // Fields
    private Ganon _cpu;
    private Character _target;
    private CollitionsProcessor _collitionsProcessor;
    private boolean _movingToTarget;
    private int _counters;
    private static boolean _playerCanCounter;

    // Properties
    public static void playerCanCounter(boolean can){
        _playerCanCounter = can;
    }
    public static boolean playerCanCounter(){
        return _playerCanCounter;
    }

    // Constructor
    public GanonThunderAttackStrategy(Ganon seeker, Character target, CollitionsProcessor cp){
        _cpu = seeker;
        _target = target;
        _collitionsProcessor = cp;
        _cpu.getThunderBall().resetLocation();
        setMoveDistances();
        _movingToTarget = true;
        
        // Set the number of times the CPU will counter the Ball.
        Random r = new Random();
        _counters = 4 + r.nextInt(3);
    }

    // Implemented methods
    public State operate(){
        moveBall();

        if (checkForWalls())
            return State.STANDING;
        else if (_movingToTarget){
            if (checkTargetCounter()){
                changeBallDirection(false);
                return State.GUARDING;
            }
            else if (_collitionsProcessor.gameObjectsIntersectedInX(_cpu.getThunderBall(), _target) && _collitionsProcessor.gameObjectsIntersectedInY(_cpu.getThunderBall(), _target))
                return State.HURT;   
        }
        else if (!_movingToTarget){
            checkForCPU();
        }
        
        return _cpu.state();
    }

    // Methods
    private void setMoveDistances(){
        float x = Math.max(_cpu.getLocationX(), _target.getLocationX()) - Math.min(_cpu.getLocationX(), _target.getLocationX());
        float y = Math.max(_cpu.getLocationY(), _target.getLocationY()) - Math.min(_cpu.getLocationY(), _target.getLocationY());
        float hip = (float) (Math.sqrt(Math.pow((double)x, 2) + Math.pow((double)y, 2)));
        float dx = (x/hip) * _cpu.getSpeed();
        float dy = (y/hip) * _cpu.getSpeed();

        if (_target.getLocationX() < _cpu.getLocationX())
            dx *= -1;
        if (_target.getLocationY() < _cpu.getLocationY())
            dy *= -1;

        _cpu.getThunderBall().setDistanceX(dx);
        _cpu.getThunderBall().setDistanceY(dy);
    }

    private void changeBallDirection(boolean movingToTarget){
        _cpu.getThunderBall().setDistanceX(_cpu.getThunderBall().distanceX() * -1);
        _cpu.getThunderBall().setDistanceY(_cpu.getThunderBall().distanceY() * -1);
        _movingToTarget = movingToTarget;
    }

    private void moveBall(){
        _cpu.getThunderBall().setLocationX(_cpu.getThunderBall().getLocationX() + _cpu.getThunderBall().distanceX());
        _cpu.getThunderBall().setLocationY(_cpu.getThunderBall().getLocationY() + _cpu.getThunderBall().distanceY());
    }

    private boolean checkForWalls(){
        for (Wall w : _collitionsProcessor.getWalls()){
            if (_collitionsProcessor.gameObjectsIntersectedInX(_cpu.getThunderBall(), w) && _collitionsProcessor.gameObjectsIntersectedInY(_cpu.getThunderBall(), w))
                return true;
        }
        return false;
    }

    private boolean checkTargetCounter(){
            if (!_playerCanCounter)
                return false;

            boolean canCounter = false;
            int direction = _collitionsProcessor.targetInAttackRange(_target, _cpu.getThunderBall());  

            if (direction == 1 && _target.isFacing() == FACING.NORTH)
                canCounter = true;
            else if (direction == 2 && _target.isFacing() == FACING.SOUTH)
                canCounter = true;
            else if (direction == 3 && _target.isFacing() == FACING.EAST)
                canCounter = true;
            else if (direction == 4 && _target.isFacing() == FACING.WEST)
                canCounter = true; 

            if (canCounter)
                _playerCanCounter = false;

            return canCounter; 
    }

    private void checkForCPU(){
        if (_collitionsProcessor.gameObjectsIntersectedInX(_cpu.getThunderBall(), _cpu) && _collitionsProcessor.gameObjectsIntersectedInY(_cpu.getThunderBall(), _cpu)){
            if (_counters > 0){
                _cpu.changeState(State.GUARDING);
                changeBallDirection(true);
                _counters--;
            }
            else {
                _cpu.changeState(State.HURT);
            }
        }
    }
    
}
