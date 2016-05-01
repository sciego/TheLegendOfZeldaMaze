package Maze.Objects;

public abstract class GameObject {
    // Fields
    protected float _x;
    protected float _y;
    protected float _width;
    protected float _height;
    private int _color;
    protected int _floor;

    // Properties
    public float getLocationX(){
        return _x;
    }
    public float getLocationY(){
        return _y;
    }
    public void setLocationX(float x){
        _x = x;
    }
    public void setLocationY(float y){
        _y = y;
    }
    public float getWidth(){
        return _width;
    }
    public float getHeight(){
        return _height;
    }
    public int getColor(){
        return _color;
    }
    public int getFloor(){
        return _floor;
    }

    // Constructors
    protected GameObject(float w, float h, float x, float y, int color, int floor){
        _width = w;
        _height = h;
        _x = x;
        _y = y;
        _color = color;
        _floor = floor;
    }

}