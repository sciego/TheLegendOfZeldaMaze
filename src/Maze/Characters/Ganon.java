package Maze.Characters;

import java.util.Random;

import Maze.Objects.GameObject;
import Maze.Processing.CollitionsProcessor;
import Maze.Processing.IAIStrategy;

public class Ganon extends Character {
    public enum Action {
        NONE, ATTACK_BOUNCE, ATTACK_FIREBALLS, ATTACK_THUNDERBALL, TELEPORT 
    }

    // Fields
    private IAIStrategy _strategy;
    private Action _action;
    private float _initialX, _initialY;
    private Ball[] _fireBalls;
    private Ball _thunderBall;

    // Properties
    public Action getAction(){
        return _action;
    }
    public void setAction(Action action){
        _action = action;
    }
    public IAIStrategy getStrategy(){
        return _strategy;
    }
    public void setStrategy(IAIStrategy strategy){
        _strategy = strategy;
    }
    public Ball[] getFireBalls(){
        return _fireBalls;
    }
    public Ball getThunderBall(){
        return _thunderBall;
    }
    
    // Constructor
    public Ganon(float width, float height, float x, float y, int speed, int frames, int floor, CollitionsProcessor cp){
        super(width, height, x, y, speed, frames, floor, cp);
        setDirection(Character.FACING.SOUTH);
        _maxLife = _life = 6;
        _initialX = x;
        _initialY = y;
        createFireBalls();
        _thunderBall = new Ball(width/2, width/2, x+(width/4), (y+height)-(width/2), 0, 1, 0, 0);
        _action = Action.NONE;
    }

    // Methods
    public State operate(){
        return _strategy.operate();
    }

    public void teleport(){
        _x = _initialX;
        _y = _initialY;
    }

    public void teleport(float limitX, float limitY){
        float x = limitX / 4;
        float y = limitY / 4;
        int n = new Random().nextInt(4);

        if (n == 1)
            y *= 3;
        else if (n == 2)
            x *= 3;
        else if (n == 3){
            x *= 3;
            y *= 3;
        }

        _x = x;
        _y = y;
    }

    private void createFireBalls(){
        _fireBalls = new Ball[48];
        addBalls(16, 0, 0);
        addBalls(16, 16, Math.PI/4);
        addBalls(16, 32, Math.PI/3);
    }

    private void addBalls(int nBalls, int from, double offset){
        float length = 10f, x, y, r = _width/2, dx, dy; // dx & dy: distance will move.
        float cx = _x + (_width/2), cy = _y + (_height/2);
        double theta = (double) (Math.PI / (nBalls/2));

        for (int i = from; i < from+nBalls; i++){
            x = (float) Math.cos((theta * (i+1)) + offset);
            dx = x * getSpeed();
            x *= r + length; // length as padding/space between balls.
            x += cx;
            x -= length/2;

            y = (float) Math.sin((theta * (i+1)) + offset);
            dy = y * getSpeed();
            y *= r + length;
            y += cy;
            y -= length/2;
            
            _fireBalls[i] = new Ball(length, length, x, y, 0, 1, dx, dy);
            
        }
    }

    public class Ball extends GameObject {
        // Fields
        private boolean _enabled;
        private float _initialX, _initialY, _distanceX, _distanceY;
        private int _frame;

        public Ball(float width, float height, float x, float y, int color, int floor, float dx, float dy){
            super(width, height, x, y, color, floor);
            _initialX = x;
            _initialY = y;
            _distanceX = dx;
            _distanceY = dy;
            _frame = 1;
        }

        public float distanceX(){
            return _distanceX;
        }
        public float distanceY(){
            return _distanceY;
        }
        public void setDistanceX(float dx){
            _distanceX = dx;
        }
        public void setDistanceY(float dy){
            _distanceY = dy;
        }
        public boolean isEnabled(){
            return _enabled;
        }
        public void toggle(boolean value){
            _enabled = value;
        }
        public int getFrame(){
            return _frame;
        }
        public void setFrame(int value){
            _frame = value;
        }

        public void resetLocation(){
            _x = _initialX;
            _y = _initialY;
        }

    }

}
