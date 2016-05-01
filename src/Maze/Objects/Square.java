package Maze.Objects;

public class Square extends GameObject {
    // Fields
    private Square _left, _right, _up, _down;

    // Properties
    public Square Left(){
        return _left;
    }
    public Square Right(){
        return  _right;
    }
    public Square Up(){
        return _up;
    }
    public Square Down(){
        return _down;
    }
    public void setRight(Square s){
        _right = s;
    }
    public void setLeft(Square s){
        _left = s;
    }
    public void setUp(Square s){
        _up = s;
    }
    public void setDown(Square s){
        _down = s;
    }

    // Constructors
    public Square(float w, float h, float x, float y, int f, int c){
        super(w, h, x, y, c, f);
    }
    

}
