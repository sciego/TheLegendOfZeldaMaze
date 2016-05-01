package Maze.Processing;

import Maze.Characters.Character;
import Maze.Characters.Moblin;
import Maze.Characters.Character.FACING;
import Maze.Characters.Character.State;

public class MoblinSeekStrategy implements IAIStrategy{

    // Fields
    private Moblin _cpu;
    private Character _target;
    private CollitionsProcessor _collitionsProcessor;
    private float _gotoX, _gotoY;
    private boolean _lockX, _lockY, _switchedDirection;
    private int _direction; // 0: None, 1: North, 2: South, 3: East, 4: West

    // Constructors
    public MoblinSeekStrategy(Moblin seeker, Character target, CollitionsProcessor cp){
        _cpu = seeker;
        _target = target;
        _collitionsProcessor = cp;
        
        _direction = 0;
        _gotoX = 0;
        _gotoY = 0;
        _lockX = false;
        _lockY = false;
        _switchedDirection = false;
    }

    // Implemented methods
    @Override
    public State operate(){
        moveCPU();
        int direction = 0;
        if ((direction = _collitionsProcessor.targetInAttackRange(_cpu, _target)) > 0){
            if (direction == 1)
                _cpu.setDirection(FACING.NORTH);
            else if (direction == 2)
                _cpu.setDirection(FACING.SOUTH);
            else if (direction == 3)
                _cpu.setDirection(FACING.EAST);
            else
                _cpu.setDirection(FACING.WEST);
            _cpu.changeState(State.FIGHTING);
        }
        return _cpu.state();
    }

    // Methods
    private void moveCPU(){
        int move = seekTarget();
        if (move == 0){ // No target found
            _cpu.changeState(State.STANDING);
            return;
        }

        int collided = -1;
        _cpu.changeState(State.MOVING);
        switch (move){
            case 1:
            {
                if (_cpu.isFacing() != FACING.NORTH)
                    _cpu.setDirection(FACING.NORTH);
                else
                    collided = _cpu.move(FACING.NORTH);
                break;
            }
            case 2:
            {
                if (_cpu.isFacing() != FACING.SOUTH)
                    _cpu.setDirection(FACING.SOUTH);
                else
                    collided = _cpu.move(FACING.SOUTH);
                break;
            }
            case 3:
            {
                if (_cpu.isFacing() != FACING.EAST)
                    _cpu.setDirection(FACING.EAST);
                else
                    collided = _cpu.move(FACING.EAST);
                break;
            }
            case 4:
            {
                if (_cpu.isFacing() != FACING.WEST)
                    _cpu.setDirection(FACING.WEST);   
                else 
                    collided = _cpu.move(FACING.WEST);
                break;
            }
        } 

        if (collided != -1) // Clashed with GameObject
            _direction = 0;
    }

    // 0: no Player found
    // 1: found Player to the North
    // 2: found Player to the South
    // 3: found Player to the East
    // 4: found Player to the West
    private int findTarget(){
        if (_collitionsProcessor.gameObjectReachable(_cpu, _target, FACING.NORTH))
            return 1;
        else if (_collitionsProcessor.gameObjectReachable(_cpu, _target, FACING.SOUTH))
            return 2;
        else if (_collitionsProcessor.gameObjectReachable(_cpu, _target, FACING.EAST))
            return 3;
        else if (_collitionsProcessor.gameObjectReachable(_cpu, _target, FACING.WEST))
            return 4;

        return 0;
    }

    private int seekTarget(){
        if (_direction == 0){
            _direction = findTarget();
            if (_direction != 0){
                _gotoX = _target.getLocationX();
                _gotoY = _target.getLocationY();
                _lockX = false;
                _lockY = false;
                _switchedDirection = false;
            }
        }
        else if (_direction == 1 || _direction == 2){
            if (_lockY == false){ // Could change targeted location in Y
                if (_gotoY != _target.getLocationY())
                    _gotoY = _target.getLocationY();
                else if (_collitionsProcessor.gameObjectsIntersectedInX(_target, _cpu) == false)
                    _lockY = true;
            }
            else if (_direction == 1 && _cpu.getLocationY() <= _gotoY || _direction == 2 && _cpu.getLocationY() >= _gotoY){
                if (_switchedDirection) // If lost target twice.
                    return _direction = 0;
                else if (_target.getLocationX() > _cpu.getLocationX())
                    _direction = 3;
                else
                    _direction = 4;
                _gotoX = _target.getLocationX();
                _lockX = true;
                _switchedDirection = true;
            }
        }
        else {
            if (_lockX == false){ // Could change targeted location in X
                if (_gotoX != _target.getLocationX())
                    _gotoX = _target.getLocationX();
                else if (_collitionsProcessor.gameObjectsIntersectedInY(_target, _cpu) == false)
                    _lockX = true;
            }
            else if (_direction == 3 && _cpu.getLocationX() >= _gotoX || _direction == 4 && _cpu.getLocationX() <= _gotoX){
                if (_switchedDirection) // If lost target twice.
                    return _direction = 0;
                else if (_target.getLocationY() < _cpu.getLocationY())
                    _direction = 1;
                else
                    _direction = 2;
                _gotoY = _target.getLocationY();
                _lockY = true;
                _switchedDirection = true;

            }
            
        }

        return _direction;
    }

}
