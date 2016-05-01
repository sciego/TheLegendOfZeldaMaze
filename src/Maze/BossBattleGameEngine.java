package Maze;

import java.util.ArrayList;

import Maze.IGameCallback.Message;
import Maze.ISoundEngine.Sound;
import Maze.Characters.Character;
import Maze.Characters.Character.FACING;
import Maze.Characters.Character.State;
import Maze.Characters.Ganon;
import Maze.Characters.Ganon.Action;
import Maze.Characters.Player;
import Maze.Objects.Wall;
import Maze.Processing.CollitionsProcessor;
import Maze.Processing.GanonBounceAttackStrategy;
import Maze.Processing.GanonFireAttackStrategy;
import Maze.Processing.GanonThunderAttackStrategy;

public class BossBattleGameEngine extends GameEngine {
    // Fields
    private Ganon _ganon;
    private Action _ganonAction;
    private ArrayList<Wall> _torches;
    private Thread _battleInputs, _ganonThread, _ganonActionThread, _ganonTeleportThread, _animationsThread;
    private int _boardWidth, _boardHeight, _ganonStepCount, _ganonActionCounter, _bounceTime;
    private boolean _ganonDodgePlayerAttack, _navyHint;

    // Constructors
    public BossBattleGameEngine(ArrayList<Object> gameObjects, IGraphicsEngine ge, IGraphicsEngine sb, ISoundEngine se, CollitionsProcessor cp, int bWidth, int bHeight,IGameCallback callback){
        super((Player) gameObjects.get(0), ge, sb, se, cp, callback);

        // Game objects
        _ganon = (Ganon) gameObjects.get(1);
        _torches = (ArrayList<Wall>) gameObjects.get(3);
        
        // Engine variables
        _battleInputs = new Thread(new Runnable(){
            @Override
            public void run(){
                while(_hasStarted){
                    if (!_running)
                        continue;
                    
                    if (GanonThunderAttackStrategy.playerCanCounter()){
                        try {
                            Thread.sleep(100);
                        } catch (Exception e) { }
                        GanonThunderAttackStrategy.playerCanCounter(false);
                    }
                }
            }
        });
        _ganonThread = new Thread(new Runnable(){
            @Override
            public void run(){
                BossBattleGameEngine.this._callback.notify(Message.GANON_APPEARED);
                while(_hasStarted && _ganon.getLife() > 0){
                    if (!_running)
                        continue;
                    operateGanon();
                }
            }
        });
        _ganonActionThread = new Thread(new Runnable(){
            @Override
            public void run(){
                ganonAction();
            }
        });
        _ganonTeleportThread = new Thread(new Runnable(){
            @Override
            public void run(){
                while (_hasStarted && _ganon.getLife() > 0){
                    if (!_running || !_ganonDodgePlayerAttack)
                        continue;

                    _ganonAction = _ganon.getAction();
                    _ganon.setAction(Action.TELEPORT);
                    _soundEngine.playSound(Sound.GANON_TELEPORT);
                    try {
                        Thread.sleep(800);
                    } catch (Exception e) { }
                    _soundEngine.playSound(Sound.GANON_TELEPORT);
                    _ganon.teleport((float)_boardWidth, (float)_boardHeight);
                    try {
                        Thread.sleep(800);
                    } catch (Exception e) { }
                    _soundEngine.playSound(Sound.GANON_TELEPORT);
                    _ganon.teleport();
                    try {
                        Thread.sleep(800);
                    } catch (Exception e) { }
                
                    _ganon.setAction(Action.NONE);
                    _ganonAction = null;
                    _ganonDodgePlayerAttack = false;
                }
            }
        });
        _animationsThread = new Thread(new Runnable(){
            @Override
            public void run(){
                animations();
            }
        });
        _ganonStepCount = 0;
        _ganonActionCounter = -1;
        _boardWidth = bWidth;
        _boardHeight = bHeight;
        _ganonAction = null;
        _ganonDodgePlayerAttack = false;
        _player.setDirection(FACING.NORTH);
    }

    @Override
    public boolean start(){
        boolean hs = super.start();
        if (!hs){
            _soundEngine.stopSound(Sound.BGM_DEFAULT);
            _soundEngine.playSound(Sound.BGM_GANON);
            _battleInputs.start();
            _ganonThread.start();
            _ganonActionThread.start();
            _ganonTeleportThread.start();
            _animationsThread.start();
        }
        return hs;
    }

    // Methods
    private void animations(){
        while (_hasStarted){
            if(!_running)
                continue;

            try {
                Thread.sleep(250);
            } catch (Exception e) { }

            for (Wall t : _torches){
                if (t.getFrame() == 1)
                    t.setFrame(2);
                else
                    t.setFrame(1);
            }
        }
    }

    private void ganonAction(){
        while (_hasStarted && _ganon.getLife() > 0){
            if (!_running || _ganon.getAction() == Action.NONE)
                continue;
            
            if (_ganon.getAction() == Action.ATTACK_FIREBALLS && _ganonActionCounter > -1){
                try {
                    Thread.sleep(1500);
                } catch (Exception e) { }
                
                if (_ganonDodgePlayerAttack)
                   continue;

                _ganonActionCounter++;
                if (_ganonActionCounter == 1){
                    _soundEngine.playSound(Sound.GANON_ATTACK_FIRE);
                    for (int i = 16; i < 32; i++)
                        _ganon.getFireBalls()[i].toggle(true);
                }
                else if (_ganonActionCounter == 2){
                    _soundEngine.playSound(Sound.GANON_ATTACK_FIRE);
                    for (int i = 28; i < 48; i++)
                        _ganon.getFireBalls()[i].toggle(true);
                }
                else
                    _ganonActionCounter = -1;
            }
            else if (_ganon.getAction() == Action.ATTACK_BOUNCE){
                try {
                    Thread.sleep(1000);
                } catch (Exception e) { }
                _ganonActionCounter++;
                if (_ganonActionCounter == _bounceTime){
                    _soundEngine.playSound(Sound.GANON_TELEPORT);
                    _ganon.setAction(Action.TELEPORT);
                    try {
                        Thread.sleep(800);
                    } catch (Exception e) { }
                    _ganon.teleport();
                    _soundEngine.playSound(Sound.GANON_TELEPORT);
                    try {
                        Thread.sleep(800);
                    } catch (Exception e) { }
                    _ganon.changeState(State.STANDING);
                    _ganon.setAction(Action.NONE);
                }
            }
        }
    }

    // PLAYER'S INPUTS
    @Override
    protected boolean movePlayerRight(){
        boolean moved = super.movePlayerRight();
        if (moved){
            _playerStepCount++;
            if (_playerStepCount == MAX_STEP_COUNT){ // Change frame
                _player.setFrame((_player.getFrame() % _player.getTotalFrames()) + 1);
                _playerStepCount = 0;
            }
            _graphicsEngine.setCamera(FACING.EAST);
        }
        
        return moved;
    }

    @Override
    protected boolean movePlayerLeft(){
        boolean moved = super.movePlayerLeft();
        if (moved){
            _playerStepCount++;
            if (_playerStepCount == MAX_STEP_COUNT) { // Change frame
                _player.setFrame((_player.getFrame() % _player.getTotalFrames()) + 1);
                _playerStepCount = 0;
            }
            _graphicsEngine.setCamera(FACING.WEST);
        }
        
        return moved;
    }

    @Override
    protected boolean movePlayerUp(){
        boolean moved = super.movePlayerUp();

        if (moved){
            _playerStepCount++;
            if (_playerStepCount == MAX_STEP_COUNT) { // Change frame
                _player.setFrame((_player.getFrame() % _player.getTotalFrames()) + 1);
                _playerStepCount = 0;
            }
            _graphicsEngine.setCamera(FACING.NORTH);
        }
     
        return moved;  
    }

    @Override
    protected boolean movePlayerDown(){
        boolean moved = super.movePlayerDown();

        if (moved){
            _playerStepCount++;
            if (_playerStepCount == MAX_STEP_COUNT){ // Change frame
                _player.setFrame((_player.getFrame() % _player.getTotalFrames()) + 1);
                _playerStepCount = 0;
            }
            _graphicsEngine.setCamera(FACING.SOUTH);
        }

        return moved;
    }

    @Override
    public void playerAttack(){
        int direction = _collitionsProcessor.targetInAttackRange(_player, _ganon);
        
        if (direction == -1 && !_playerCanAttack)
            return;
        if (_running && _player.state() == State.STANDING){
            super.playerAttack();
            GanonThunderAttackStrategy.playerCanCounter(true);
            
            if (_ganon.getAction() != Action.NONE && _ganon.getAction() != Action.ATTACK_FIREBALLS)
                return;

            if (direction > 0 && _player.isFacing() == FACING.values()[direction-1]){
                _ganonDodgePlayerAttack = true;
            }
        }
    }

    // MISC
    @Override
    protected void enemyGotHurt(Character enemy){
        super.enemyGotHurt(enemy);
        if (enemy.getLife() == 0) {
            _collitionsProcessor.getCharacters().remove(_ganon);
            _soundEngine.stopSound(Sound.BGM_GANON);
            _soundEngine.playSound(Sound.GANON_KILLED);
            for (int i = 0; i < 3; i++){
                _graphicsEngine.drawColorScreen(4);
                try {
                    Thread.sleep(375);
                } catch (Exception e) { }
            }
            
            try {
                Thread.sleep(1000);
            } catch (Exception e) { }
            _callback.notify(Message.GANON_DEFEATED);
        }
    }

    // GANON'S PROCESSING
    private void setGanonStrategy(){
        switch (_ganon.getAction()){
            case ATTACK_BOUNCE:
            {
                _ganon.setStrategy(new GanonBounceAttackStrategy(_ganon, _player, _collitionsProcessor));
                //_ganon.setStrategy(new GanonSwirlBounceAttackStrategy(_ganon, _player, _collitionsProcessor, _boardWidth, _boardHeight));
                _bounceTime = 7 + _random.nextInt(5);
                break;
            }
            case ATTACK_FIREBALLS:
            {
                _ganon.setStrategy(new GanonFireAttackStrategy(_ganon, _player, _collitionsProcessor));
                _soundEngine.playSound(Sound.GANON_ATTACK_FIRE);
                break;
            }
            case ATTACK_THUNDERBALL:
            {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) { }
                _ganon.setStrategy(new GanonThunderAttackStrategy(_ganon, _player, _collitionsProcessor));
                _soundEngine.playSound(Sound.GANON_ATTACK_BALL);
                break;
            }
        }
        _ganonActionCounter = 0;
    }

    private void operateGanon(){
        int ms = 0;

        if (_ganon.state() == State.STANDING){
            _ganon.changeState(State.ATTACKING);
            ms = 1000;
        }
        else {
            if (_ganon.getAction() == Action.NONE){
                int n = _random.nextInt(3);
                if (n == 1)
                    _ganon.setAction(Action.ATTACK_BOUNCE);
                else if (n == 2)
                    _ganon.setAction(Action.ATTACK_FIREBALLS);
                else
                    _ganon.setAction(Action.ATTACK_THUNDERBALL);
                setGanonStrategy();
            }

            State state = null;
            if (_ganon.getAction() != Action.TELEPORT || _ganonAction == Action.ATTACK_FIREBALLS)
                state = _ganon.operate();
            
            if (_ganon.getAction() == Action.ATTACK_THUNDERBALL){
                ms = 10;
                if (state == State.GUARDING){ // Ganon or Player hit the Ball.
                    _soundEngine.playSound(Sound.GANON_ATTACK_BALL);
                    if (_ganon.state() == State.GUARDING)
                        _ganon.changeState(State.ATTACKING);
                }
                else if (state == State.HURT || state == State.STANDING){
                    _soundEngine.playSound(Sound.GANON_ATTACK_BALL_HIT);
                    if (_ganon.state() == State.HURT){
                        _ganon.decreaseLife(1);
                        enemyGotHurt(_ganon);
                    }
                    else if (state == State.HURT){
                        if (_ganon.state() == State.ATTACKING || _ganon.state() == State.GUARDING){
                            decreasePlayerLife(1);
                            if (!_navyHint){
                                _callback.notify(Message.NAVY_HINT);
                                _navyHint = true;
                            }
                        }
                    }
                    _ganon.changeState(State.STANDING);
                    _ganon.setAction(Action.NONE);
                    _ganon.getThunderBall().resetLocation();
                }
            }
            else {
                if (_ganon.getAction() == Action.ATTACK_BOUNCE){
                    ms = 10;
                    if (state == State.GUARDING){
                        _soundEngine.playSound(Sound.GANON_ATTACK_BOUNCE);
                    }
                }
                else
                    ms = 35;
                if (state == State.STANDING){
                    _ganon.changeState(State.STANDING);
                    _ganon.setAction(Action.NONE);
                }
                if (state == State.HURT)
                    decreasePlayerLife(1);
            }
        }

        try {
            Thread.sleep(ms);
        } catch (Exception e) { }
    }
    

}
