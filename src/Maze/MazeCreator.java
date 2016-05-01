package Maze;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Maze.Characters.Character;
import Maze.Characters.Character.FACING;
import Maze.Characters.Deku;
import Maze.Characters.Moblin;
import Maze.Characters.Player;
import Maze.Objects.Square;
import Maze.Objects.Wall;
import Maze.Processing.MoblinSeekStrategy;
import Maze.Processing.CollitionsProcessor;
import Maze.Processing.IAIStrategy;
import android.graphics.Color;

public class MazeCreator {

    // Fields
    private final int _f1Color = Color.rgb(0, 102, 0), _f2Color = Color.rgb(135, 135, 0), _squareColor = Color.rgb(153, 153, 0);
    private float _boardWidth, _boardHeight, _wallThickness, _squareLength, _moblinLength;
    private CollitionsProcessor _collitionsProcessor;
    private MazeGameEngine _gameEngine;

    // Properties
    public MazeGameEngine getGameEngine(){
        return _gameEngine;
    }

    // Constructor
    public MazeCreator(float[] lengths, IGraphicsEngine ge, IGraphicsEngine sb, ISoundEngine se, IGameCallback cb){
        _boardWidth = lengths[0];
        _boardHeight = lengths[1];
        _wallThickness = lengths[2];
        _squareLength = lengths[3];
        float playerWidth = lengths[4];
        float playerHeight = lengths[5];
        _moblinLength = lengths[6];
        
        ArrayList<Wall> walls = new ArrayList<Wall>();
        ArrayList<Square> squares = new ArrayList<Square>();
        createBoard(walls, squares);
        ArrayList<Deku> dekus = createDekus();
        ArrayList<Wall> rupees = createRupees();
        ArrayList<Wall> bushes = createBushes();
        ArrayList<Wall> hearts = createHearts(bushes);
        addWallTypeObjectsToList(walls, bushes);
        ArrayList<Character> characters = new ArrayList<Character>();

        _collitionsProcessor = new CollitionsProcessor(characters, walls, squares);
        Player player = new Player(playerWidth, playerHeight, _wallThickness+1, _wallThickness+1, 2, 3, 1, _collitionsProcessor);
        characters.add(player);
        characters.add(createMoblin(1, 10, player));

        ArrayList<Object> gameObjects = new ArrayList<Object>();
        gameObjects.add(player);
        gameObjects.add(characters);
        gameObjects.add(walls);
        gameObjects.add(squares);
        gameObjects.add(dekus);
        gameObjects.add(rupees);
        gameObjects.add(bushes);
        gameObjects.add(hearts);
        
        ge.setGameObjects(gameObjects);
        sb.setGameObjects(gameObjects);
        _gameEngine = new MazeGameEngine(gameObjects, ge, sb, se, _collitionsProcessor, cb);
    }

    // Methods
    private float calculateX(int col){
        return (col*_squareLength) + ((col+1)*_wallThickness);
    }
    private float calculateY(int row){
        return (row*_squareLength) + ((row+1)*_wallThickness);
    }

    private Moblin createMoblin(int row, int col, Character target){
        float offset = (_squareLength - _moblinLength) / 2;
        Moblin moblin = new Moblin(_moblinLength, _moblinLength, calculateX(col)+offset, calculateY(row)+offset, 2, 2, 1, _collitionsProcessor);
        moblin.setStrategy(new MoblinSeekStrategy(moblin, target, _collitionsProcessor));

        return moblin;
    }

    private ArrayList<Deku> createDekus(){
        ArrayList<Deku> dekus = new ArrayList<Deku>();
        float length = _moblinLength;
        float offset = (_squareLength - length) / 2;
        int speed = 6;

        dekus.add(new Deku(length, calculateX(0)+offset, calculateY(9)+offset, speed, 1, FACING.NORTH));
        dekus.add(new Deku(length, calculateX(8)+offset, calculateY(2)+offset, speed, 1, FACING.SOUTH));
        dekus.add(new Deku(length, calculateX(10)+offset, calculateY(20)+offset, speed, 1, FACING.WEST));
        dekus.add(new Deku(length, calculateX(15)+offset, calculateY(14)+offset, speed, 1, FACING.WEST));
        dekus.add(new Deku(length, calculateX(4)+offset, calculateY(9)+offset, speed, 1, FACING.EAST));

        return dekus;
    }

    private ArrayList<Wall> createRupees(){
        ArrayList<Wall> rupees = new ArrayList<Wall>();
        float height = _wallThickness;
        float width = height / 2;
        float offsetX = (_squareLength - width) / 2;
        float offsetY = (_squareLength - height) / 2;

        rupees.add(new Wall(width, height, calculateX(5)+offsetX, calculateY(3)+offsetY, 0, 1));
        rupees.add(new Wall(width, height, calculateX(13)+offsetX, calculateY(18)+offsetY, 0, 1));
        rupees.add(new Wall(width, height, calculateX(13)+offsetX, calculateY(6)+offsetY, 0, 1));
        rupees.add(new Wall(width, height, calculateX(2)+offsetX, calculateY(8)+offsetY, 0, 1));
        rupees.add(new Wall(width, height, calculateX(0)+offsetX, calculateY(12)+offsetY, 0, 1));
        rupees.add(new Wall(width, height, calculateX(14)+offsetX, calculateY(12)+offsetY, 0, 1));
        rupees.add(new Wall(width, height, calculateX(11)+offsetX, calculateY(13)+offsetY, 0, 1));
        rupees.add(new Wall(width, height, calculateX(7)+offsetX, calculateY(17)+offsetY, 0, 1));

        // Random location Rupee:
        Random r = new Random();
        int n = r.nextInt(3), rx, ry = 0;
        if (n == 1){
            rx = 11;
            ry = 1;
        } else if (n == 2){
            rx = 9;
        } else {
            rx = 15;
        }
        rupees.add(new Wall(width, height, calculateX(rx)+offsetX, calculateY(ry)+offsetY, 0, 1));

        return rupees;
    }

    private ArrayList<Wall> createHearts(ArrayList<Wall> bushes){
        ArrayList<Wall> hearts = new ArrayList<Wall>();
        float length = _squareLength/3;
        Random r = new Random();
        int n = r.nextInt(bushes.size()/2);
        float offset = (bushes.get(n).getWidth() - length) / 2;
        
        hearts.add(new Wall(length, length, bushes.get(n).getLocationX()+offset, bushes.get(n).getLocationY()+offset, 0, 1));
        n = r.nextInt(bushes.size());
        hearts.add(new Wall(length, length, bushes.get(n).getLocationX()+offset, bushes.get(n).getLocationY()+offset, 0, 1));
        
        return hearts;
    }

    private void addWallTypeObjectsToList(ArrayList<Wall> walls, ArrayList<Wall> wallTypesList){
        for (Wall wt : wallTypesList){
            walls.add(wt);
        }
    }

    private ArrayList<Wall> createBushes(){
        ArrayList<Wall> bushes = new ArrayList<Wall>();
        float width = 3 * (_squareLength/4);
        float height = width;
        float offsetX = (_squareLength - width) / 2;
        float offsetY = offsetX;

        bushes.add(new Wall(width, height, calculateX(9)+offsetX, calculateY(0)+offsetY, 0, 1));
        bushes.add(new Wall(width, height, calculateX(11)+offsetX, calculateY(1)+offsetY, 0, 1));
        bushes.add(new Wall(width, height, calculateX(15)+offsetX, calculateY(0)+offsetY, 0, 1));
        bushes.add(new Wall(width, height, calculateX(3)+offsetX, calculateY(3)+offsetY, 0, 1));
        bushes.add(new Wall(width, height, calculateX(14)+offsetX, calculateY(8)+offsetY, 0, 1));
        bushes.add(new Wall(width, height, calculateX(9)+offsetX, calculateY(4)+offsetY, 0, 1));
        bushes.add(new Wall(width, height, calculateX(15)+offsetX, calculateY(5)+offsetY, 0, 1));
        bushes.add(new Wall(width, height, calculateX(6)+offsetX, calculateY(6)+offsetY, 0, 1));
        bushes.add(new Wall(width, height, calculateX(12)+offsetX, calculateY(8)+offsetY, 0, 1));
        bushes.add(new Wall(width, height, calculateX(12)+offsetX, calculateY(4)+offsetY, 0, 1));
        bushes.add(new Wall(width, height, calculateX(9)+offsetX, calculateY(2)+offsetY, 0, 1));
        bushes.add(new Wall(width, height, calculateX(10)+offsetX, calculateY(8)+offsetY, 0, 1));
        bushes.add(new Wall(width, height, calculateX(1)+offsetX, calculateY(6)+offsetY, 0, 1));
        bushes.add(new Wall(width, height, calculateX(2)+offsetX, calculateY(13)+offsetY, 0, 1));
        bushes.add(new Wall(width, height, calculateX(6)+offsetX, calculateY(12)+offsetY, 0, 1));
        bushes.add(new Wall(width, height, calculateX(12)+offsetX, calculateY(11)+offsetY, 0, 1));
        bushes.add(new Wall(width, height, calculateX(8)+offsetX, calculateY(18)+offsetY, 0, 1));
        bushes.add(new Wall(width, height, calculateX(1)+offsetX, calculateY(15)+offsetY, 0, 1));
        bushes.add(new Wall(width, height, calculateX(8)+offsetX, calculateY(17)+offsetY, 0, 1));

        return bushes;
    }

    private void createBoard(ArrayList<Wall> walls, ArrayList<Square> squares){
        int i, j;
        float x, y;

        float wt = _wallThickness;
        float sl = _squareLength;
        float bw = _boardWidth;
        float bh = _boardHeight;

        // Set the corner Walls:
        Wall[][] cornerWalls = new Wall[22][17];
        y = 0;
        for (i = 0; i <= 21; i++){
            x = 0;
            for (j = 0; j <= 16; j++){
                cornerWalls[i][j] = new Wall(wt, wt, x, y, _f1Color, 1);
                walls.add(cornerWalls[i][j]);
                x += wt + sl;
            }
            y += wt + sl;
        }

        // Set horizontal Walls:
        boolean[][] hWalls = getHorizontalWallsLocations();
        y = 0;
        for (i = 0; i <= 21; i++){
            x = wt;
            for (j = 0; j <= 15; j++){
                if (hWalls[i][j])
                    walls.add(new Wall(sl, wt, x, y, _f1Color, 1));
                x += sl + wt;
            }
            y += wt + sl;
        }

        boolean[][] vWalls = getVerticalWallsLocations();
        y = wt;
        for (i = 0; i <= 20; i++){
            x = 0;
            for (j = 0; j <= 16; j++){
                if (vWalls[i][j])
                    walls.add(new Wall(wt, sl, x, y, _f1Color, 1));
                x += wt + sl;
            }
            y += sl + wt;
        }

        // Set bridges:
        squares.add(createSquare(cornerWalls[0][1], wt, 2));
        createBridge(squares, 2, cornerWalls, 1, 1, 2, 2, wt);
        squares.add(createSquare(cornerWalls[2][0], wt, 1));
        squares.get(0).setDown(squares.get(1));
        squares.get(1).setUp(squares.get(0));
        squares.get(2).setLeft(squares.get(3));
        squares.get(3).setRight(squares.get(2));
        
        createBridge(squares, 1, cornerWalls, 0, 2, 2, 2, wt);
        squares.add(createSquare(cornerWalls[0][4], wt, 1));
        squares.get(0).setRight(squares.get(4));
        squares.get(4).setLeft(squares.get(0));
        squares.get(5).setRight(squares.get(6));
        squares.get(6).setLeft(squares.get(5));
        
        createBridge(squares, 1, cornerWalls, 10, 0, 4, 2, wt);
        squares.add(createSquare(cornerWalls[11][0], wt, 1));
        squares.add(createSquare(cornerWalls[11][3], wt, 1));
        squares.get(7).setDown(squares.get(11));
        squares.get(11).setUp(squares.get(7));
        squares.get(10).setDown(squares.get(12));
        squares.get(12).setUp(squares.get(10));
        
        createBridge(squares, 1, cornerWalls, 15, 12, 2, 2, wt);
        squares.add(createSquare(cornerWalls[15][11], wt, 1));
        squares.add(createSquare(cornerWalls[14][13], wt, 1));
        squares.get(13).setLeft(squares.get(15));
        squares.get(15).setRight(squares.get(13));
        squares.get(14).setUp(squares.get(16));
        squares.get(16).setDown(squares.get(14));
        
        createBridge(squares, 2, cornerWalls, 6, 9, 7, 2, wt);
        squares.add(createSquare(cornerWalls[6][10], wt, 1));
        squares.get(17).setRight(squares.get(24));
        squares.get(24).setLeft(squares.get(17));
        squares.add(createSquare(cornerWalls[12][10], wt, 1));
        squares.get(23).setRight(squares.get(25));
        squares.get(25).setLeft(squares.get(23));
        
        createBridge(squares, 2, cornerWalls, 15, 6, 4, 2, wt);
        squares.add(createSquare(cornerWalls[15][5], wt, 2));
        squares.add(createSquare(cornerWalls[18][5], wt, 2));
        squares.add(createSquare(cornerWalls[15][4], wt, 1));
        squares.add(createSquare(cornerWalls[18][4], wt, 1));
        squares.add(createSquare(cornerWalls[15][7], wt, 1));
        squares.get(26).setLeft(squares.get(30));
        squares.get(30).setRight(squares.get(26));
        squares.get(29).setLeft(squares.get(31));
        squares.get(31).setRight(squares.get(29));
        squares.get(30).setLeft(squares.get(32));
        squares.get(32).setRight(squares.get(30));
        squares.get(31).setLeft(squares.get(33));
        squares.get(33).setRight(squares.get(31));
        squares.get(26).setRight(squares.get(34));
        squares.get(34).setLeft(squares.get(26));
        
        // Create 2nd floor Walls:
        for (Square s : squares){
            if (s.getFloor() > 1){
                if (s.Right() == null)
                    walls.add(new Wall(wt, sl+(wt*2), s.getLocationX()+sl, s.getLocationY()-wt, _f2Color, s.getFloor()));
                if (s.Left() == null)
                    walls.add(new Wall(wt, sl+(wt*2), s.getLocationX()-wt, s.getLocationY()-wt, _f2Color, s.getFloor()));
                if (s.Up() == null)
                    walls.add(new Wall(sl+(wt*2), wt, s.getLocationX()-wt, s.getLocationY()-wt, _f2Color, s.getFloor()));
                if (s.Down() == null)
                    walls.add(new Wall(sl+(wt*2), wt, s.getLocationX()-wt, s.getLocationY()+sl, _f2Color, s.getFloor()));
                
                if (s.Down() != null && s.Down().getFloor() < s.getFloor() && s.Right() != null && s.Right().getFloor() == s.getFloor() && s.Right().Down() != null && s.Right().Down().getFloor() < s.getFloor())
                    walls.add(new Wall(wt, wt, s.getLocationX()+sl, s.getLocationY()+sl, _f2Color, s.getFloor()));
                if (s.Up() != null && s.Up().getFloor() < s.getFloor() && s.Right() != null && s.Right().getFloor() == s.getFloor() && s.Right().Up() != null && s.Right().Up().getFloor() < s.getFloor())
                    walls.add(new Wall(wt, wt, s.getLocationX()+sl, s.getLocationY()-wt, _f2Color, s.getFloor()));
                if (s.Left() != null && s.Left().getFloor() < s.getFloor() && s.Down() != null && s.Down().getFloor() == s.getFloor() && s.Down().Left() != null && s.Down().Left().getFloor() < s.getFloor())
                    walls.add(new Wall(wt, wt, s.getLocationX()-wt, s.getLocationY()+sl, _f2Color, s.getFloor()));
                if (s.Right() != null && s.Right().getFloor() < s.getFloor() && s.Down() != null && s.Down().getFloor() == s.getFloor() && s.Down().Right() != null && s.Down().Right().getFloor() < s.getFloor())
                    walls.add(new Wall(wt, wt, s.getLocationX()+sl, s.getLocationY()+sl, _f2Color, s.getFloor()));
            }
        }

        // Create exit
        Square exit = createSquare(cornerWalls[20][15], wt, 1);
        squares.add(exit);
    }

    private boolean[][] getHorizontalWallsLocations(){
        boolean[][] indexes = new boolean[22][16];

        int i;
        for (i = 0; i <= 15; i++)
            indexes[0][i] = true;
        for (i = 1; i <= 4; i++)
            indexes[1][i] = true;
        indexes[1][6] = true;
        indexes[1][7] = true;
        indexes[1][8] = true;
        indexes[1][10] = true;
        indexes[1][12] = true;
        indexes[2][0] = true;
        indexes[2][1] = true;
        for (i = 3; i <= 11; i++)
            indexes[2][i] = true;
        indexes[2][13] = true;
        indexes[3][1] = true;
        indexes[3][4] = true;
        indexes[3][5] = true;
        indexes[3][6] = true;
        indexes[3][9] = true;
        indexes[3][10] = true;
        indexes[3][13] = true;
        indexes[3][15] = true;
        indexes[4][3] = true;
        indexes[4][6] = true;
        indexes[4][7] = true;
        indexes[4][9] = true;
        indexes[4][10] = true;
        indexes[4][12] = true;
        indexes[4][14] = true;
        for (i = 2; i <= 6; i++)
            indexes[5][i] = true;
        indexes[5][9] = true;
        indexes[5][11] = true;
        indexes[5][12] = true;
        indexes[6][0] = true;
        indexes[6][1] = true;
        indexes[6][5] = true;
        indexes[6][6] = true;
        indexes[6][7] = true;
        indexes[6][9] = true;
        indexes[6][10] = true;
        indexes[6][11] = true;
        indexes[6][13] = true;
        indexes[6][14] = true;
        indexes[6][15] = true;
        indexes[7][1] = true;
        indexes[7][2] = true;
        indexes[7][4] = true;
        indexes[7][7] = true;
        indexes[7][9] = true;
        indexes[7][10] = true;
        indexes[7][12] = true;
        indexes[7][13] = true;
        indexes[7][14] = true;
        indexes[8][1] = true;
        indexes[9][4] = true;
        indexes[8][14] = true;
        indexes[8][15] = true;
        indexes[9][2] = true;
        indexes[9][5] = true;
        indexes[9][6] = true;
        indexes[9][10] = true;
        indexes[9][12] = true;
        indexes[9][14] = true;
        indexes[10][0] = true;
        indexes[10][3] = true;
        indexes[10][4] = true;
        indexes[10][6] = true;
        indexes[10][13] = true;
        for (i = 5; i <= 8; i++)
            indexes[11][i] = true;
        indexes[11][12] = true;
        indexes[11][14] = true;
        for (i = 0; i <= 3; i++)
            indexes[12][i] = true;
        indexes[12][6] = true;
        indexes[12][10] = true;
        indexes[12][11] = true;
        indexes[13][2] = true;
        indexes[13][6] = true;
        indexes[13][7] = true;
        indexes[13][9] = true;
        indexes[13][10] = true;
        indexes[13][14] = true;
        indexes[14][2] = true;
        indexes[14][4] = true;
        indexes[14][5] = true;
        indexes[14][9] = true;
        indexes[14][11] = true;
        indexes[14][13] = true;
        indexes[14][15] = true;
        indexes[15][1] = true;
        for (i = 3; i <= 15; i++)
            indexes[15][i] = true;
        indexes[16][1] = true;
        indexes[16][2] = true;
        indexes[16][3] = true;
        indexes[16][5] = true;
        indexes[16][6] = true;
        indexes[16][7] = true;
        indexes[16][10] = true;
        indexes[16][13] = true;
        indexes[16][14] = true;
        indexes[17][0] = true;
        indexes[17][1] = true;
        //indexes[17][5] = true;
        indexes[17][7] = true;
        indexes[17][8] = true;
        indexes[17][12] = true;
        indexes[17][13] = true;
        indexes[18][4] = true;
        indexes[18][5] = true;
        indexes[18][8] = true;
        for (i = 11; i <= 14; i++)
            indexes[18][i] = true;
        indexes[19][3] = true;
        indexes[19][5] = true;
        indexes[19][6] = true;
        for (i = 11; i <= 14; i++)
            indexes[19][i] = true;
        for (i = 1; i <= 5; i++)
            indexes[20][i] = true;
        for (i = 7; i <= 10; i++)
            indexes[20][i] = true;
        indexes[20][11] = true;
        indexes[20][14] = true;
        indexes[20][15] = true;
        for (i = 0; i <= 15; i++)
            indexes[21][i] = true;

        return indexes;
    }

    private boolean[][] getVerticalWallsLocations(){
        boolean[][] indexes = new boolean[21][17];

        int i;
        for (i = 0; i <= 20; i++)
            indexes[i][0] = true;
        indexes[0][1] = true;
        indexes[0][10] = true;
        indexes[0][15] = true;
        indexes[0][16] = true;
        indexes[1][5] = true;
        indexes[1][11] = true;
        indexes[1][12] = true;
        indexes[1][14] = true;
        indexes[1][15] = true;
        indexes[2][2] = true;
        indexes[2][3] = true;
        indexes[2][8] = true;
        indexes[2][9] = true;
        indexes[2][12] = true;
        indexes[2][14] = true;
        indexes[3][1] = true;
        for (i = 1; i <= 6; i++)
            indexes[3][i] = true;
        indexes[4][1] = true;
        indexes[4][2] = true;
        indexes[4][8] = true;
        indexes[4][9] = true;
        indexes[4][13] = true;
        indexes[4][14] = true;
        indexes[5][15] = true;
        indexes[6][1] = true;
        indexes[6][3] = true;
        indexes[6][4] = true;
        indexes[6][6] = true;
        indexes[6][i] = true;
        indexes[6][9] = true;
        indexes[6][12] = true;
        indexes[6][13] = true;
        indexes[7][3] = true;
        for (i = 5; i <= 9; i++)
            indexes[7][i] = true;
        indexes[7][13] = true;
        for (i = 2; i <= 5; i++)
            indexes[8][i] = true;
        for (i = 8; i <= 14; i++)
            indexes[8][i] = true;
        indexes[9][1] = true;
        indexes[9][2] = true;
        indexes[9][4] = true;
        indexes[9][13] = true;
        indexes[9][15] = true;
        for (i = 1; i <= 4; i++)
            indexes[10][i] = true;
        for (i = 7; i <= 12; i++)
            indexes[10][i] = true;
        indexes[10][14] = true;
        indexes[11][2] = true;
        indexes[11][4] = true;
        indexes[11][5] = true;
        indexes[11][9] = true;
        indexes[11][10] = true;
        indexes[11][12] = true;
        indexes[11][13] = true;
        indexes[11][15] = true;
        indexes[12][1] = true;
        indexes[12][4] = true;
        indexes[12][5] = true;
        indexes[12][6] = true;
        indexes[12][8] = true;
        indexes[12][9] = true;
        for (i = 12; i <= 15; i++)
            indexes[12][i] = true;
        indexes[13][1] = true;
        indexes[13][3] = true;
        indexes[13][8] = true;
        for (i = 10; i <= 14; i++)
            indexes[13][i] = true;
        indexes[14][1] = true;
        indexes[14][3] = true;
        indexes[14][5] = true;
        indexes[14][7] = true;
        indexes[14][8] = true;
        indexes[15][2] = true;
        indexes[15][11] = true;
        indexes[15][12] = true;
        indexes[16][4] = true;
        indexes[16][5] = true;
        for (i = 9; i <= 12; i++)
            indexes[16][i] = true;
        indexes[16][15] = true;
        indexes[17][2] = true;
        indexes[17][3] = true;
        indexes[17][4] = true;
        indexes[17][6] = true;
        indexes[17][7] = true;
        indexes[17][8] = true;
        indexes[17][10] = true;
        indexes[17][11] = true;
        indexes[17][15] = true;
        indexes[18][1] = true;
        for (i = 3; i <= 10; i++)
            indexes[18][i] = true;
        indexes[18][6] = false;
        indexes[18][14] = true;
        indexes[19][1] = true;
        indexes[19][2] = true;
        indexes[19][6] = true;
        indexes[19][8] = true;
        indexes[19][11] = true;
        indexes[19][13] = true;
        indexes[20][11] = true;
        for (i = 0; i <= 20; i++)
            indexes[i][16] = true;

        return indexes;
    }

    // A corner Wall is needed to get it's location
    private Square createSquare(Wall w, float wt, int floor){
        float x = w.getLocationX() + wt; // Wall X+thickness
        float y = w.getLocationY() + wt;

        return new Square(_squareLength, _squareLength, x, y, floor, _squareColor);
    }

    // Orientation = 1: horizontal, 2: vertical
    // "length" argument stands for how many blocks will be built
    private void createBridge(List<Square> squares, int orientation, Wall[][] cWalls, int fromX, int fromY, int length, int floor, float wt){
        int buildPoint = squares.size();
        Square s = null;

        if (orientation == 1){
            for (int i = 0; i < length; i++){
                s = createSquare(cWalls[fromX][fromY+i], wt, floor);
                if (i > 0){
                    squares.get((buildPoint+i)-1).setRight(s);
                    s.setLeft(squares.get((buildPoint+i)-1));
                }
                squares.add(s);
            }
        }
        else {
            for (int i = 0; i < length; i++){
                s = createSquare(cWalls[fromX+i][fromY], wt, floor);
                if (i > 0){ // Bind with previous Square
                    squares.get((buildPoint+i)-1).setDown(s);
                    s.setUp(squares.get((buildPoint+i)-1));
                }
                squares.add(s);
            }
        }
    }
    
}
