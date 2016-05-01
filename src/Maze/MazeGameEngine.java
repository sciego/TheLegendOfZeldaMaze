package Maze;

import java.util.ArrayList;
import java.util.Random;

import Maze.Characters.Character;
import Maze.Characters.Deku;
import Maze.Characters.Moblin;
import Maze.Characters.Player;
import Maze.Characters.Character.FACING;
import Maze.Characters.Character.State;
import Maze.IGameCallback.Message;
import Maze.ISoundEngine.Sound;
import Maze.Objects.Square;
import Maze.Objects.Wall;
import Maze.Processing.MoblinFightStrategy;
import Maze.Processing.MoblinSeekStrategy;
import Maze.Processing.CollitionsProcessor;

public class MazeGameEngine extends GameEngine {
    
    // Fields
    private ArrayList<Wall> _walls, _rupees, _bushes, _hearts;
    private ArrayList<Square> _squares;
    private ArrayList<Deku> _dekus;
    private ArrayList<Character> _characters;
    private Moblin _moblin;
    private Thread _dekusThread, _moblinThread, _timerThread;
    private boolean _battle, _exitIsAvailable;
    private int _moblinStepCount;
    private static int _time; // Seconds

    // Constructors
    public MazeGameEngine(ArrayList<Object> gameObjects, IGraphicsEngine ge, IGraphicsEngine sb, ISoundEngine se, CollitionsProcessor cp, IGameCallback callback){
        super((Player) gameObjects.get(0), ge, sb, se, cp, callback);

        // Game objects
        _characters = (ArrayList<Character>) gameObjects.get(1);
        _moblin = (Moblin) _characters.get(1);
        _characters.remove(1);
        _walls = (ArrayList<Wall>) gameObjects.get(2);
        _squares = (ArrayList<Square>) gameObjects.get(3);
        _dekus = (ArrayList<Deku>) gameObjects.get(4);
        _rupees = (ArrayList<Wall>) gameObjects.get(5);
        _bushes = (ArrayList<Wall>) gameObjects.get(6);
        _hearts = (ArrayList<Wall>) gameObjects.get(7);
       
        // Engine variables
        _dekusThread = new Thread(new Runnable(){
            @Override
            public void run(){
                dekusProcessing();
            }
        });
        _moblinThread = new Thread(new Runnable(){
            @Override
            public void run(){
                moblinProcessing();
            }
        });
        _timerThread = new Thread(new Runnable(){
            @Override
            public void run(){
                oneSecondDelays();
            }
        });
        
        _time = 0; // Static
        _battle = false;
        _exitIsAvailable = false;
        _moblinStepCount = 0;
    }

    // Properties
    public static int getTime(){
        return _time;
    }

    // Methods
    private void dekusProcessing(){
        while (_hasStarted){
            if (!_running)
                continue;

            try {
                operateDekus();
                Thread.sleep(50);
            } catch (Exception e) { }
            
        }
    }

    private void moblinProcessing(){
        while (_hasStarted){
            if (!_running)
                continue;

            try {
                operateMoblin();
                Thread.sleep(50);
            } catch (Exception e) { }
            
        }
    }

    private void oneSecondDelays(){
        _gameOverScreenTime = 0;

        while (_hasStarted){
            try {
                Thread.sleep(1000);
            } catch (Exception e) { }

            if (!_running)
                continue;

            if (_time < Integer.MAX_VALUE)
                _time++;
            
        }
    }

    @Override
    public boolean start(){
        boolean hs = super.start();
        if (!hs){ // To prevent thread issues
            _dekusThread.start();
            _moblinThread.start();
            _timerThread.start();
        }
        return hs;
    }
    
    // PLAYER'S INPUTS
    @Override
    protected boolean movePlayerRight(){
        boolean moved = super.movePlayerRight();
        
        if (moved){
            afterPlayerMoved();
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
            afterPlayerMoved();
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
            afterPlayerMoved();
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
            afterPlayerMoved();
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
        if (_running && _player.state() == State.STANDING){
            if (checkForPlayerTargets() || checkForBushes() || _playerCanAttack)
                super.playerAttack();
        }
    }

    // PLAYER'S PROCESSING
    private void afterPlayerMoved(){
        playWalkSound(_player);
        checkForRupees();
        checkForHearts();
        checkForExit();
    }

    private void checkForRupees(){
        for (int i = 0; i < _rupees.size(); i++){
            if (_collitionsProcessor.characterCollidedWithGameObject(_player, _rupees.get(i))){
                _soundEngine.playSound(Sound.RUPEE);
                _player.setRupees(1);
                _rupees.remove(i);

                if (_rupees.size() == 0){ // If no rupees left, open the exit/portal
                    _soundEngine.playSound(Sound.ENEMY_PAWN);
                    _moblin.pawn(8);
                    _characters.add(_moblin);
                    _callback.notify(Message.MOBLIN_APPEARED);
                }
                break;
            }
        }

    }

    private void checkForHearts(){
        for (int i = 0; i < _hearts.size(); i++){
            if (_collitionsProcessor.characterCollidedWithGameObject(_player, _hearts.get(i))){
                _soundEngine.playSound(Sound.HEART);
                _player.increaseLife(2);
                _hearts.remove(i);
                break;
            }
        }
    }

    private boolean checkForBushes(){
        for (Wall b : _bushes){
            int bushDirection = _collitionsProcessor.targetInAttackRange(_player, b);
            if (bushDirection > 0 && FACING.values()[bushDirection-1] == _player.isFacing()){
                _soundEngine.playSound(Sound.CUT_BUSH);
                _bushes.remove(b);
                b.disable();
                return true;
            }
        }

        return false;
    }

    private void checkForExit(){
        Square exit = _squares.get(_squares.size()-1);

        if (_exitIsAvailable && _collitionsProcessor.characterCollidedWithGameObject(_player, exit)){
            pause();
            try {
                Thread.sleep(1000);
            } catch (Exception e) { }
            _callback.endGame(Message.WIN);
        }
    }

    private boolean checkForPlayerTargets(){
        for (Character e : _characters){
            int enemyDirection = _collitionsProcessor.targetInAttackRange(_player, e);
            if (_player != e && enemyDirection > 0 && FACING.values()[enemyDirection-1] == _player.isFacing()){
                if (_random.nextInt(3) == 2){
                    e.decreaseLife(1);
                    enemyGotHurt(e);
                }
                else {
                    e.changeState(State.GUARDING);
                    _soundEngine.playSound(Sound.ENEMY_GUARD);
                }
                return true;
            }
        }

        return false;
    }

    private void playWalkSound(Character character){
        if (character.getFloor() == 1)
            _soundEngine.playSound(Sound.WALK_GRASS);
        else
            _soundEngine.playSound(Sound.WALK_BRIDGE);
    }

    // MISC
    @Override
    protected void enemyGotHurt(Character enemy){
        super.enemyGotHurt(enemy);
        if (enemy.getLife() == 0) {
            _characters.remove(enemy);
            if (_characters.size() > 1) // There's still enemies left (besides Player)
                return;
            _soundEngine.stopSound(Sound.BGM_BATTLE);
            _soundEngine.playSound(Sound.ENEMY_KILLED);
            for (int i = 0; i < 3; i++){
                _graphicsEngine.drawColorScreen(4);
                try {
                    Thread.sleep(375);
                } catch (Exception e) { }
            }
            _exitIsAvailable = true;
            _graphicsEngine.drawExit();
            _soundEngine.playSound(Sound.SECRET);
            _soundEngine.playSound(Sound.BGM_DEFAULT);
            _callback.notify(Message.EXIT_APPEARED);
        }
    }

    // CPU'S PROCESSING
    private boolean moveDekuNut(Deku deku){
        boolean collided = false;
        float newLocation = 0;
        int wallIndex = -1;

        switch (deku.isFacing()){
            case NORTH:
            {
                newLocation = deku.getNut().getLocationY() - deku.getSpeed();
                wallIndex = _collitionsProcessor.checkForWalls(deku.getNut(), FACING.NORTH, newLocation);
                if (wallIndex > -1){
                    deku.setNutY(_walls.get(wallIndex).getLocationY() + _walls.get(wallIndex).getHeight());
                    collided = true;
                }
                else
                    deku.setNutY(newLocation);
                break;
            }
            case SOUTH:
            {
                newLocation = deku.getNut().getLocationY() + deku.getSpeed();
                wallIndex = _collitionsProcessor.checkForWalls(deku.getNut(), FACING.SOUTH, newLocation);
                if (wallIndex > -1){
                    deku.setNutY(_walls.get(wallIndex).getLocationY() - deku.getNut().getHeight());
                    collided = true;
                }
                else
                    deku.setNutY(newLocation);
                break;
            }
            case EAST:
            {
                newLocation = deku.getNut().getLocationX() + deku.getSpeed();
                wallIndex = _collitionsProcessor.checkForWalls(deku.getNut(), FACING.EAST, newLocation);
                if (wallIndex > -1){
                    deku.setNutX(_walls.get(wallIndex).getLocationX() - deku.getNut().getWidth());
                    collided = true;
                }
                else
                    deku.setNutX(newLocation);
                break;
            }
            case WEST:
            {
                newLocation = deku.getNut().getLocationX() - deku.getSpeed();
                wallIndex = _collitionsProcessor.checkForWalls(deku.getNut(), FACING.WEST, newLocation);
                if (wallIndex > -1){
                    deku.setNutX(_walls.get(wallIndex).getLocationX() + _walls.get(wallIndex).getWidth());
                    collided = true;
                }
                else
                    deku.setNutX(newLocation);
                break;
            }
        }

        return collided;
    }

    private void operateDekus(){
        for (Deku d : _dekus){
            if (d.isFacing() == FACING.NORTH || d.isFacing() == FACING.SOUTH){
                if (_collitionsProcessor.gameObjectsIntersectedInX(d, _player) && d.checkForTarget(_player))
                    d.changeState(State.GUARDING);
                else
                    d.changeState(State.ATTACKING);
            }
            else if (_collitionsProcessor.gameObjectsIntersectedInY(d, _player) && d.checkForTarget(_player))
                d.changeState(State.GUARDING);
            else
                d.changeState(State.ATTACKING);

            if (d.readyToShoot() == false){ // Nut is moving (already thrown)
                if (moveDekuNut(d)){ // If true, it collided with a GameObject
                    _soundEngine.playSound(Sound.DEKU_NUT);
                    d.reload();
                }
                else if (_collitionsProcessor.characterCollidedWithGameObject(_player, d.getNut())){
                    if (playerIsVulnerable(d.isFacing()))
                        decreasePlayerLife(1);
                    else
                        _soundEngine.playSound(Sound.LINK_SHIELD);
                    d.reload();
                }
                else if (_moblin.getLife() > 0 && _collitionsProcessor.characterCollidedWithGameObject(_moblin, d.getNut())){
                    _moblin.changeState(State.HURT);
                    _moblin.decreaseLife(1);
                    enemyGotHurt(_moblin);
                    d.reload();
                }
            }
            else if (d.state() == State.ATTACKING)
                d.shoot();
        }
    }

    private void operateMoblin(){
        if (_moblin.getLife() == 0)
            return;

        State was = _moblin.state();
        State now = _moblin.operate();
        
        if (!_battle){
            if (now == State.MOVING || now == State.FIGHTING){
                _battle = true;
                _soundEngine.stopSound(Sound.BGM_DEFAULT);
                _soundEngine.playSound(Sound.BGM_BATTLE);
            }
        }

        if (now == State.STANDING || now == State.MOVING){
            if (was == State.FIGHTING)
                _moblin.setStrategy(new MoblinSeekStrategy(_moblin, _player, _collitionsProcessor));
            else if (now == State.MOVING){
                _moblinStepCount++;
                if (_moblinStepCount == MAX_STEP_COUNT){
                    playWalkSound(_moblin);
                    if (_moblin.getFrame() == 1)
                        _moblin.setFrame(2);
                    else
                        _moblin.setFrame(1);
                    _moblinStepCount = 0;
                }
            }
        }
        else {
            if (was == State.STANDING || was == State.MOVING)
                _moblin.setStrategy(new MoblinFightStrategy(_moblin, _player, _collitionsProcessor));
            if (now == State.ATTACKING){
                if (playerIsVulnerable(_moblin.isFacing()))
                    decreasePlayerLife(1);
                else
                    _soundEngine.playSound(Sound.LINK_SHIELD);
            }
            if (now != State.FIGHTING){
                try {
                    Thread.sleep(1000);
                    _moblin.changeState(State.FIGHTING);
                } catch (Exception e) { }
                _moblin.changeState(State.FIGHTING);
            }
        }

    }

}