package Maze.Characters;

import Maze.Objects.GameObject;

public class Deku extends Character{

    // Fields
    private Nut _nut;
    private float _initialNutX, _initialNutY;
    private boolean _readyToShoot;
    private short _reloadTime;
    private final short MAX_RELOAD_TIME = 10;

    // Properties
    public Nut getNut(){
        return _nut;
    }
    public boolean readyToShoot(){
        return _readyToShoot;
    }

    public void setNutX(float x){
        _nut.setLocationX(x);
    }
    public void setNutY(float y){
        _nut.setLocationY(y);
    }

    // Constructor
    public Deku(float length, float x, float y, int speed, int floor, Character.FACING direction){
        super(length, length, x+2, y+2, speed, 2, floor, null);
        _facing = direction;
        _state = State.ATTACKING;

        float diameter = 0; // For the Nut
        if (direction == FACING.NORTH || direction == FACING.SOUTH){
            _width -= (0.15f * _width);
            diameter = _width / 3;
            x += _width / 2;
            x -= diameter / 2;
            x += 2;
        }
        else {
            _height -= (0.15f * _height);
            diameter = _height / 3;
            y += _height / 2;
            y -= diameter / 2;
            y += 2;
        }

        // Rellocate the Nut at the Deku's mouth:
        if (direction == Character.FACING.NORTH)
            y -= diameter;
        else if (direction == Character.FACING.SOUTH)
            y += _height;
        else if (direction == Character.FACING.WEST)
            x -= diameter;
        else
            x += _width;
        
        _nut = new Nut(diameter, x, y, floor);
        _initialNutX = _nut.getLocationX();
        _initialNutY = _nut.getLocationY();
        _reloadTime = MAX_RELOAD_TIME;
    }

    // Methods
    public void shoot(){
        if (_reloadTime > 0)
            _reloadTime--;
        else
            _readyToShoot = false; // Shooting the Nut (is moving)
    }

    public void reload(){
        _reloadTime = MAX_RELOAD_TIME;
        _nut.setLocationX(_initialNutX);
        _nut.setLocationY(_initialNutY);
        _readyToShoot = true;
    }

    public boolean checkForTarget(Character target){
        float padding = 12f; // Space betweeen wall and Deku

        switch (_facing){
            case NORTH:
                if (target.getLocationY() + target.getHeight() >= _y - _height && target.getLocationY() + target.getHeight() <= _y + _height+padding)
                    return true;
                break;
            case SOUTH:
                if (target.getLocationY() <= _y + (_height * 2) && target.getLocationY() >= _y-padding)
                    return true;
                break;
            case EAST:
                if (target.getLocationX() <= _x + (_width * 2) && target.getLocationX() >= _x-padding)
                    return true;
                break;
            case WEST:
                if (target.getLocationX() + target.getWidth() >= _x - _width && target.getLocationX() + target.getWidth() <= _x + _width+padding)
                    return true;
                break;
        }

        return false;
    }

    public class Nut extends GameObject {

        // Constructor
        private Nut(float diameter, float x, float y, int floor){
            super(diameter, diameter, x, y, 0, floor);
        }
        
    }

}
