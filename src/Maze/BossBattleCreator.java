package Maze;

import java.util.ArrayList;

import Maze.Characters.Character;
import Maze.Characters.Ganon;
import Maze.Characters.Ganon.Ball;
import Maze.Characters.Player;
import Maze.Objects.Square;
import Maze.Objects.Wall;
import Maze.Processing.CollitionsProcessor;
import android.graphics.Color;

public class BossBattleCreator {

    // Fields
    private final int _wallColor = Color.rgb(180, 0, 0);
    private float _boardWidth, _boardHeight, _wallThickness, _squareLength, _ganonLength;
    private CollitionsProcessor _collitionsProcessor;
    private BossBattleGameEngine _gameEngine;

    // Properties
    public BossBattleGameEngine getGameEngine(){
        return _gameEngine;
    }

    // Constructor
    public BossBattleCreator(float[] lengths, IGraphicsEngine ge, IGraphicsEngine sb, ISoundEngine se, IGameCallback cb){
        _boardWidth = lengths[0];
        _boardHeight = lengths[1];
        _wallThickness = lengths[2];
        _squareLength = lengths[3];
        float playerWidth = lengths[4];
        float playerHeight = lengths[5];
        _ganonLength = playerHeight * 2;

        ArrayList<Wall> walls = createWalls();
        ArrayList<Wall> torches = createTorches();
        addWallTypeObjectsToList(walls, torches);
        ArrayList<Square> squares = new ArrayList<Square>();
        ArrayList<Character> characters = new ArrayList<Character>();

        _collitionsProcessor = new CollitionsProcessor(characters, walls, squares);
        Player player = new Player(playerWidth, playerHeight, (_boardWidth/2)-(_ganonLength/2), (_boardHeight/4)*3, 2, 3, 1, _collitionsProcessor);
        Ganon ganon = new Ganon(_ganonLength, _ganonLength*0.65f, (_boardWidth/2)-(_ganonLength/2), (_boardHeight/2)-(_ganonLength/2), 2, 1, 1, _collitionsProcessor);
        characters.add(player);
        characters.add(ganon);

        ArrayList<Object> gameObjects = new ArrayList<Object>();
        gameObjects.add(player);
        gameObjects.add(ganon);
        gameObjects.add(walls);
        gameObjects.add(torches);
        
        ge.setGameObjects(gameObjects);
        sb.setGameObjects(gameObjects);
        _gameEngine = new BossBattleGameEngine(gameObjects, ge, sb, se, _collitionsProcessor, (int)lengths[0], (int)lengths[1], cb);
    }

    // Methods
    private float calculateX(int col){
        return (col*_squareLength) + ((col+1)*_wallThickness);
    }
    private float calculateY(int row){
        return (row*_squareLength) + ((row+1)*_wallThickness);
    }

    private void addWallTypeObjectsToList(ArrayList<Wall> walls, ArrayList<Wall> wallTypesList){
        for (Wall wt : wallTypesList){
            walls.add(wt);
        }
    }

    private ArrayList<Wall> createTorches(){
        ArrayList<Wall> torches = new ArrayList<Wall>();
        float width = _squareLength/2;
        float height = width;
        float offsetX = (_squareLength - width) / 2;
        
        torches.add(new Wall(width, height, calculateX(0)+offsetX, calculateY(0)+height, 0, 2, 1));
        torches.add(new Wall(width, height, calculateX(8)+offsetX, calculateY(0)+height, 0, 2, 1));
        torches.add(new Wall(width, height, calculateX(0)+offsetX, calculateY(4)+height, 0, 2, 1));
        torches.add(new Wall(width, height, calculateX(8)+offsetX, calculateY(4)+height, 0, 2, 1));

        torches.add(new Wall(width, height, calculateX(4)+offsetX, calculateY(0)+height, 0, 2, 1));
        torches.add(new Wall(width, height, calculateX(4)+offsetX, calculateY(4)+height, 0, 2, 1));
        
        torches.add(new Wall(width, height, calculateX(0)+offsetX, calculateY(2)+height, 0, 2, 1));
        torches.add(new Wall(width, height, calculateX(8)+offsetX, calculateY(2)+height, 0, 2, 1));
        
        return torches;
    }

    private ArrayList<Wall> createWalls(){
        ArrayList<Wall> walls = new ArrayList<Wall>();
        float x, y;
        int i, rows = 5, cols = 9;
        
        for (i = 0; i <= cols; i++){
            x = i * (_wallThickness + _squareLength);
            y = 0f;
            walls.add(new Wall(_wallThickness, _wallThickness, x, y, _wallColor, 1));
            x += _wallThickness;
            walls.add(new Wall(_squareLength, _wallThickness, x, y, _wallColor, 1));
            
            x -= _wallThickness;
            y = rows * (_wallThickness + _squareLength);
            walls.add(new Wall(_wallThickness, _wallThickness, x, y, _wallColor, 1));
            x += _wallThickness;
            walls.add(new Wall(_squareLength, _wallThickness, x, y, _wallColor, 1));
        }
        for (i = 0; i <= rows; i++){
            y = i * (_wallThickness + _squareLength);
            x = 0f;
            walls.add(new Wall(_wallThickness, _wallThickness, x, y, _wallColor, 1));
            y += _wallThickness;
            walls.add(new Wall(_wallThickness, _squareLength, x, y, _wallColor, 1));
            
            y -= _wallThickness;
            x = cols * (_wallThickness + _squareLength);
            walls.add(new Wall(_wallThickness, _wallThickness, x, y, _wallColor, 1));
            y += _wallThickness;
            walls.add(new Wall(_wallThickness, _squareLength, x, y, _wallColor, 1));
        }

        return walls;
    }
}