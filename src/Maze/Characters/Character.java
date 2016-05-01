package Maze.Characters;

import Maze.Objects.GameObject;
import Maze.Processing.CollitionsProcessor;

public abstract class Character extends GameObject{
    
    // Fields
    protected int _life, _maxLife;
    protected float _attackRange;
    private int _speed, _frame, _totalFrames;
    private CollitionsProcessor _collitionsProcessor;
    protected FACING _facing;
    protected State _state;

    public enum FACING{
        NORTH, SOUTH, EAST, WEST
    }
    public enum State{
        STANDING, MOVING, FIGHTING, ATTACKING, GUARDING, HURT
    }
    
    // Propterties
    public int getSpeed(){
        return _speed;
    }
    public void setSpeed(int speed){
        _speed = speed;
    }
    public int getFrame(){
        return _frame;
    }
    public void setFrame(int f){
        _frame = f;
    }
    public int getTotalFrames(){
        return _totalFrames;
    }
    public void setFloor(int floor){
        _floor = floor;
    }
    public FACING isFacing(){
        return _facing;
    }
    public void setDirection(FACING facing){
            _facing = facing;
    }
    public State state(){
        return _state;
    }
    public void changeState(State state){
        _state = state;
    }
    public int getLife(){
        return _life;
    }
    public void decreaseLife(int damage){
        if (damage > 0)
            _life -= damage;
    }
    public int getMaxLife(){
        return _maxLife;
    }
    public float getAttackRange(){
        return _attackRange;
    }
    
    // Constructors
    protected Character(float width, float height, float x, float y, int speed, int frames, int floor, CollitionsProcessor cp){
        super(width, height, x, y, 0, floor);
        _speed = speed;
        _totalFrames = frames;
        _frame = 1;
        _state = State.STANDING;
        _attackRange = 0;
        _collitionsProcessor = cp;
    }

    // Methods
    public int move(FACING direction){
        CollitionsProcessor cp = _collitionsProcessor; // Shorter name
        float newLocation;
        int i = -1;

        switch (direction){
            case NORTH:
            {
                newLocation = _y - _speed;
                if (cp.checkForBridges(this, FACING.NORTH, newLocation) == false || _floor > 1){
                    i = cp.checkForWalls(this, FACING.NORTH, newLocation);
                    if (i != -1)
                        newLocation = cp.getWall(i).getLocationY() + cp.getWall(i).getHeight();
                    else {
                        i = cp.checkForCharacters(this, FACING.NORTH, newLocation);
                        if (i != -1)
                            newLocation = cp.getCharacter(i).getLocationY() + cp.getCharacter(i).getHeight();
                    }
                }
                _y = newLocation;
                break;
            }
            case SOUTH:
            {
                newLocation = _y + _speed;
                if (cp.checkForBridges(this, FACING.SOUTH, newLocation) == false || _floor > 1){
                    i = cp.checkForWalls(this, FACING.SOUTH, newLocation);
                    if (i != -1)
                        newLocation = cp.getWall(i).getLocationY() - _height;
                    else {
                        i = cp.checkForCharacters(this, FACING.SOUTH, newLocation);
                        if (i != -1)
                            newLocation = cp.getCharacter(i).getLocationY() - _height;
                    }
                }
                _y = newLocation;
                break;
            }
            case EAST:
            {
                newLocation = _x + _speed;
                if (cp.checkForBridges(this, FACING.EAST, newLocation) == false || _floor > 1){
                    i = cp.checkForWalls(this, FACING.EAST, newLocation);
                    if (i != -1)
                        newLocation = cp.getWall(i).getLocationX() - _width;
                    else {
                        i = cp.checkForCharacters(this, FACING.EAST, newLocation);
                        if (i != -1)
                            newLocation = cp.getCharacter(i).getLocationX() - _width;
                    }
                }
                _x = newLocation;
                break;
            }
            case WEST:
            {
                newLocation = _x - _speed;
                if (cp.checkForBridges(this, FACING.WEST, newLocation) == false || _floor > 1){
                    i = cp.checkForWalls(this, FACING.WEST, newLocation);
                    if (i != -1)
                        newLocation = cp.getWall(i).getLocationX() + cp.getWall(i).getWidth();
                    else {
                        i = cp.checkForCharacters(this, FACING.WEST, newLocation);
                        if (i != -1)
                            newLocation = cp.getCharacter(i).getLocationX() + cp.getCharacter(i).getWidth();
                    }
                }
                _x = newLocation;
                break;
            }
        }

        return i;
    }
    
}
