package Maze;

import java.util.ArrayList;
import java.util.Random;

import Maze.Characters.Character;
import Maze.Characters.Player;
import Maze.Characters.Character.FACING;
import Maze.Characters.Character.State;
import Maze.IGameCallback.Message;
import Maze.ISoundEngine.Sound;
import Maze.Objects.Square;
import Maze.Objects.Wall;
import Maze.Processing.CollitionsProcessor;

public abstract class GameEngine {
    
    // Fields
    protected IGameCallback _callback;
    protected CollitionsProcessor _collitionsProcessor;
    protected Player _player;
    protected IGraphicsEngine _graphicsEngine, _statusBar;
    protected ISoundEngine _soundEngine;
    protected Thread _playerThread, _playerStateThread;
    protected boolean _running, _hasStarted, _movePlayerRight, _movePlayerLeft, _movePlayerUp, _movePlayerDown, _playerGuard, _playerCanAttack;
    protected int _playerStepCount, _gameOverScreenTime;
    protected final int MAX_STEP_COUNT = 8;
    protected Random _random;

    // Constructors
    public GameEngine(Player player, IGraphicsEngine ge, IGraphicsEngine sb, ISoundEngine se, CollitionsProcessor cp, IGameCallback callback){
        _callback = callback;
        // Game objects
        _player = player;
        _graphicsEngine = ge;
        _statusBar = sb;
        _soundEngine = se;
        _collitionsProcessor = cp;

        // Engine variables
        _playerThread = new Thread(new Runnable(){
            @Override
            public void run(){
                checkInputs();
            }
        });
        _playerStateThread = new Thread(new Runnable(){
            @Override
            public void run(){
                while (_hasStarted){
                    if (!_running)
                        continue;
                    if (_player.state() == State.ATTACKING){
                        try {
                            Thread.sleep(600);
                        } catch (Exception e) { }
                        _player.changeState(State.STANDING);
                    }
                }
            }
        });
        
        _hasStarted = false;
        _running = false;
        _movePlayerRight = false;
        _movePlayerLeft = false;
        _movePlayerUp = false;
        _movePlayerDown = false;
        _playerGuard = false;
        _playerCanAttack = true;
        _playerStepCount = 0;
        _random = new Random();
    }

    // Properties
    public boolean isRunning(){
        return _running;
    }
    public boolean movingPlayerRight(){
        return _movePlayerRight;
    }
    public boolean movingPlayerLeft(){
        return _movePlayerLeft;
    }
    public boolean movingPlayerUp(){
        return _movePlayerUp;
    }
    public boolean movingPlayerDown(){
        return _movePlayerDown;
    }

    // Methods
    protected void checkInputs(){
        while (_hasStarted){
            if (!_running)
                continue;

            if (_playerGuard){
                if (_player.state() != State.GUARDING);
                    _player.changeState(State.GUARDING);
                continue;
            }

            if (_movePlayerRight)
                movePlayerRight();
            else if (_movePlayerLeft)
                movePlayerLeft();
            else if (_movePlayerUp)
                movePlayerUp();
            else if (_movePlayerDown)
                movePlayerDown();
            
            if (_player.state() != State.STANDING && _player.state() != State.ATTACKING)
                _player.changeState(State.STANDING);

            try {
                Thread.sleep(20);
            } catch (Exception e) { }
        }
    }

    protected boolean start(){
        if (!_hasStarted){ // To prevent thread issues
            _hasStarted = true;
            _running = true;
            _playerThread.start();
            _playerStateThread.start();
            _soundEngine.playSound(Sound.BGM_DEFAULT);
            _graphicsEngine.start();
            _statusBar.start();
            return false;
        }
        return true;
    }

    public void pause(){
        _running = false;
        _graphicsEngine.pause();
        _statusBar.pause();
        _soundEngine.pause();
    }
    
    public void resume(){
        if (_player.getLife() > 0)
            _running = true;
        _graphicsEngine.resume();
        _statusBar.resume();
        _soundEngine.resume();
    }

    public void finish(){
        _running = false;
        _hasStarted = false;
        _graphicsEngine.finish();
        _statusBar.finish();
        _soundEngine.finish();
    }

    // PLAYER'S INPUTS
    protected boolean movePlayerRight(){
        if (_player.state() != State.STANDING || _player.getLife() == 0)
            return false;
        
        if (_player.isFacing() != FACING.EAST){
            _player.setDirection(FACING.EAST);
            return false;
        }

        _player.changeState(State.MOVING);
        
        int i = _player.move(FACING.EAST);
        
        if (i != -1){
            _playerCanAttack = false;
            return false;
        }
        else {
            _playerCanAttack = true;
            return true;
        }
    }
    public void movePlayerRight(boolean move){
        _movePlayerRight = move;
    }

    protected boolean movePlayerLeft(){
        if (_player.state() != State.STANDING || _player.getLife() == 0)
            return false;
        
        if (_player.isFacing() != FACING.WEST){
            _player.setDirection(FACING.WEST);
            return false;
        }

        _player.changeState(State.MOVING);
        
        int i = _player.move(FACING.WEST);

        if (i != -1){
            _playerCanAttack = false;
            return false;
        }
        else {
            _playerCanAttack = true;
            return true;
        }
    }
    public void movePlayerLeft(boolean move){
        _movePlayerLeft = move;
    }

    protected boolean movePlayerUp(){
        if (_player.state() != State.STANDING || _player.getLife() == 0)
            return false;
        
        if (_player.isFacing() != FACING.NORTH){
            _player.setDirection(FACING.NORTH);
            return false;
        }

        _player.changeState(State.MOVING); // Lock movement
        
        int i = _player.move(FACING.NORTH);
        
        if (i != -1){
            _playerCanAttack = false;
            return false;
        }
        else {
            _playerCanAttack = true;
            return true;
        }   
    }
    public void movePlayerUp(boolean move){
        _movePlayerUp = move;
    }

    protected boolean movePlayerDown(){
        if (_player.state() != State.STANDING || _player.getLife() == 0)
            return false;
        
        if (_player.isFacing() != FACING.SOUTH){
            _player.setDirection(FACING.SOUTH);
            return false;
        }

        _player.changeState(State.MOVING);
        
        int i = _player.move(FACING.SOUTH);
        
        if (i != -1){
            _playerCanAttack = false;
            return false;
        }
        else {
            _playerCanAttack = true;
            return true;
        }
    }
    public void movePlayerDown(boolean move){
        _movePlayerDown = move;
    }

    protected void playerAttack(){
        if (_running && _player.state() == State.STANDING){
            _player.changeState(State.ATTACKING);
            _soundEngine.playSound(Sound.values()[_random.nextInt(3)]);
        }
    }

    public void playerGuard(boolean guard){
        _playerGuard = guard;
    }

    // PLAYER'S PROCESSING
    protected boolean playerIsVulnerable(FACING from){
        if (_player.state() != State.GUARDING)
            return true;

        switch (_player.isFacing()){
            case NORTH:
                if (from == FACING.SOUTH)
                    return false;
                break;
            case SOUTH:
                if (from == FACING.NORTH)
                    return false;
                break;
            case EAST:
                if (from == FACING.WEST)
                    return false;
                break;
            case WEST:
                if (from == FACING.EAST)
                    return false;
                break;
        }

        return true;
    }

    protected void decreasePlayerLife(int damage){
        _soundEngine.playSound(Sound.LINK_HURT);
        _graphicsEngine.drawColorScreen(1);
        _player.decreaseLife(damage);
        if (_player.getLife() == 0){
            _running = false;
            _soundEngine.stopSound(Sound.BGM_DEFAULT);
            _soundEngine.stopSound(Sound.BGM_BATTLE);
            _soundEngine.stopSound(Sound.BGM_GANON);
            _soundEngine.playSound(Sound.LINK_KILLED);
            _soundEngine.playSound(Sound.GAME_OVER);
            try {
                Thread.sleep(3000);
            } catch (Exception e) { }
            _soundEngine.stopSound(Sound.GAME_OVER);
            _callback.endGame(Message.LOSE);
        }
    }

    // MISC
    protected void enemyGotHurt(Character enemy){
        enemy.changeState(State.HURT);
        _soundEngine.playSound(Sound.SWORD);
        if (enemy.getLife() > enemy.getMaxLife()/2)
            _graphicsEngine.drawColorScreen(2);
        else if (enemy.getLife() > 0)
            _graphicsEngine.drawColorScreen(3);
    }

    
}