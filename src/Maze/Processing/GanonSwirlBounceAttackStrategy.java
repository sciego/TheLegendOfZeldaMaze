package Maze.Processing;

import Maze.Characters.Character;
import Maze.Characters.Ganon;
import Maze.Objects.Wall;

public class GanonSwirlBounceAttackStrategy extends GanonBounceAttackStrategy {
    // Fields
    private float _radius, _theta, _centerX, _centerY;
    private int _transition;
    private final int TRANSITIONS = 32;

    // Construtor
    public GanonSwirlBounceAttackStrategy(Ganon seeker, Character target, CollitionsProcessor cp, int boardWidth, int boardHeight){
        super(seeker, target, cp);
        _centerX = boardWidth / 2;
        _centerY = boardHeight / 2;
        _radius = seeker.getWidth();
        _theta = (float) (Math.PI / (TRANSITIONS/2));
        _transition = 1;
    }

    // Methods
    @Override
    protected int move(){
        float x = _theta * _transition;
        x = (float) Math.cos((double) x);
        x *= _radius;
        x -= _cpu.getWidth() / 2;
        float y = _theta * _transition;
        y = (float) Math.sin((double) y);
        y *= _radius;
        y -= _cpu.getHeight() / 2;

        _cpu.setLocationX(_centerX + x);
        _cpu.setLocationY(_centerY + y);

        _transition++;
        if (_transition == TRANSITIONS){
            _transition = 1;
            _radius += _cpu.getWidth()/2;
        }

        Wall w = null;
        for (int i = 1; i < _collitionsProcessor.getWalls().size(); i++){
            w = _collitionsProcessor.getWall(i);
            if (_collitionsProcessor.gameObjectsIntersectedInX(_cpu, w) && _collitionsProcessor.gameObjectsIntersectedInY(_cpu, w))
                return i;
        }
    
        if (_collitionsProcessor.gameObjectsIntersectedInX(_target, _cpu) && _collitionsProcessor.gameObjectsIntersectedInY(_target, _cpu))
            return 0;

        return -1;
    }

}
