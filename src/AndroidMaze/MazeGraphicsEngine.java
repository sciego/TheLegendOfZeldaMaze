package AndroidMaze;

import java.util.ArrayList;
import java.util.List;

import Maze.IGraphicsEngine;
import Maze.Characters.Character;
import Maze.Characters.Character.FACING;
import Maze.Characters.Character.State;
import Maze.Characters.Deku;
import Maze.Characters.Moblin;
import Maze.Characters.Player;
import Maze.Objects.Square;
import Maze.Objects.Wall;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.cfai.maze.GameActivity;
import com.cfai.maze.MainMenuActivity;
import com.cfai.maze.R;

public class MazeGraphicsEngine extends SurfaceView implements IGraphicsEngine, Runnable {
    
    // Fields
    private List<Wall> _walls;
    private List<Square> _squares;
    private List<Deku> _dekus;
    private List<Wall> _rupees, _bushes, _hearts;
    private Player _player;
    private Moblin _moblin;
    private SurfaceHolder _holder;
    private Thread _thread;
    private boolean _running, _hasStarted, _drawExit;
    private Paint _paint;
    private int _cameraWidth, _cameraHeight, _exitFrame, _drawColor;
    private final int CAMERA_PADDING = 30;    
    private Canvas _canvas;
    private Bitmap _bitmap, _mazeBitmap, _bridgesBitmap, _playerBitmap, _shieldBitmap, _dekuBitmap, _dekuFlowerBitmap, _moblinBitmap, _rupeeBitmap, _bushBitmap, _heartBitmap, _gameOverBitmap;
    private Fractal _exit;
    private Rect[] _playerRects, _moblinRects;
    private Rect _camera;

    // Constructor
    public MazeGraphicsEngine(Context context) {
        super(context);
        
        // Engine variables
        _holder = getHolder();
        _running = false;
        _hasStarted = false;
        _thread = new Thread(this);
        _drawColor = 0;
        _exitFrame = 0;

        // Drawing resources
        Options opts = new Options();
        opts.inScaled = false;
        _playerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.link, opts);
        _shieldBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.shield, opts);
        _moblinBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.moblin, opts);
        _dekuBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.deku, opts);
        _dekuFlowerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.deku_flower, opts);
        _rupeeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.rupee, opts);
        _bushBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bush, opts);
        _heartBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.hearts, opts);
        _gameOverBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.game_over, opts);
        _exit = new Fractal(250, 10, 250, 115, 255, 255, 20);
        _paint = new Paint();
        
        // Camera
        _cameraWidth = BaseScales.CAMERA_WIDTH;
        _cameraHeight = BaseScales.CAMERA_HEIGHT;
        if (MainMenuActivity.usingAccelerometer()){
            _cameraWidth /= 2;
            _cameraHeight /= 21;
            _cameraHeight *= 13;
        }
        else
            _cameraHeight /= 3;
        setRects();
    }

    // Implementes methods
    public void setGameObjects(ArrayList<Object> gameObjects){
        _player = (Player) gameObjects.get(0);
        _moblin = (Moblin) ((ArrayList<Character>) gameObjects.get(1)).get(1);
        _walls = (ArrayList<Wall>) gameObjects.get(2);
        _squares = (ArrayList<Square>) gameObjects.get(3);
        _dekus = (ArrayList<Deku>) gameObjects.get(4);
        _rupees = (ArrayList<Wall>) gameObjects.get(5);
        _bushes = (ArrayList<Wall>) gameObjects.get(6);
        _hearts = (ArrayList<Wall>) gameObjects.get(7);
        
        _mazeBitmap = getMazeBitmap();
        _bridgesBitmap = getBridgesBitmap();
        _bitmap = Bitmap.createBitmap(_mazeBitmap.getWidth(), _mazeBitmap.getHeight(), Bitmap.Config.RGB_565);
        _canvas = new Canvas(_bitmap);
    }

    public void start(){
        _running = true;
        if (!_hasStarted){
            _hasStarted = true;
            _thread.start();
        }
    }
    public void pause(){
        _running = false;
    }
    public void resume(){
        _running = true;
    }
    public void finish(){
        _running = false;
        _hasStarted = false;
    }

    public void drawColorScreen(int color){
        _drawColor = color;
    }
    public void drawExit(){
        _drawExit = true;
    }

    // Methods
    private void setRects(){
        // Camera
        _camera = new Rect(0, 0, _cameraWidth, _cameraHeight);

        // Player
        _playerRects = new Rect[17];
        _playerRects[0] = new Rect(0, 0, 19, 23);
        _playerRects[1] = new Rect(19, 0, 37, 23);
        _playerRects[2] = new Rect(37, 0, 55, 23);
        _playerRects[4] = new Rect(0, 24, 19, 45);
        _playerRects[5] = new Rect(19, 24, 38, 45);
        _playerRects[6] = new Rect(38, 24, 57, 45);
        _playerRects[8] = new Rect(0, 49, 17, 70);
        _playerRects[9] = new Rect(17, 49, 37, 70);
        _playerRects[10] = new Rect(37, 49, 59, 70);
        _playerRects[12] = new Rect(0, 73, 17, 94);
        _playerRects[13] = new Rect(17, 73, 37, 94);
        _playerRects[14] = new Rect(37, 73, 59, 94);
        _playerRects[3] = new Rect(0, 98, 19, 134);
        _playerRects[7] = new Rect(20, 98, 38, 130);
        _playerRects[15] = new Rect(38, 98, 73, 121);
        _playerRects[11] = new Rect(75, 98, 112, 121);
        _playerRects[16] = new Rect(0, 0, _shieldBitmap.getWidth(), _shieldBitmap.getHeight());

        // Moblin
        _moblinRects = new Rect[12];
        _moblinRects[0] = new Rect(0, 0, 27, 34);
        _moblinRects[1] = new Rect(30, 0, 56, 34);
        _moblinRects[2] = new Rect(55, 0, 82, 34);
        _moblinRects[3] = new Rect(0, 38, 28, 70);
        _moblinRects[4] = new Rect(31, 36, 55, 71);
        _moblinRects[5] = new Rect(55, 38, 82, 71);
        _moblinRects[6] = new Rect(0, 72, 37, 102);
        _moblinRects[7] = new Rect(38, 75, 76, 102);
        _moblinRects[8] = new Rect(80, 73, 119, 100);
        _moblinRects[9] = new Rect(0, 106, 36, 136);
        _moblinRects[10] = new Rect(39, 107, 77, 136);
        _moblinRects[11] = new Rect(81, 108, 124, 134);
    }

    private Bitmap getMazeBitmap(){
        float width = BaseScales.BOARD_WIDTH;
        float height = BaseScales.BOARD_HEIGHT;
        Bitmap bitmap = Bitmap.createBitmap((int) width, (int) height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.rgb(50, 190, 50)); // Grass
        //canvas.drawColor(Color.BLACK);
        drawWalls(canvas, 1);
        
        return bitmap;
    }

    private Bitmap getBridgesBitmap(){
        float width = BaseScales.BOARD_WIDTH;
        float height = BaseScales.BOARD_HEIGHT;
        Bitmap bitmap = Bitmap.createBitmap((int) width, (int) height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawWalls(canvas, 2);
        drawBridges(canvas);
        
        return bitmap;
    }

    private void drawWalls(Canvas canvas, int floor){
        Wall w = null;
        RectF rectf = new RectF(0, 0, 0, 0);
        float fx, fy;
        
        for (int i = 0; i < _walls.size()-_bushes.size(); i++){
            w = _walls.get(i);
            if (w.getFloor() == floor){
                // Border
                fx = w.getLocationX() + w.getWidth();
                fy = w.getLocationY() + w.getHeight();
                rectf.set(w.getLocationX(), w.getLocationY(), fx, fy);
                _paint.setColor(Color.BLACK);
                canvas.drawRect(rectf, _paint);
                
                fx -= 2;
                fy -= 2;
                rectf.set(w.getLocationX()+2, w.getLocationY()+2, fx, fy);
                _paint.setColor(w.getColor());
                canvas.drawRect(rectf, _paint);
            }
        }
    }

    private void drawPlayer(Canvas canvas){
        RectF dst = new RectF(_player.getLocationX(), _player.getLocationY(), _player.getLocationX() + _player.getWidth(), _player.getLocationY() + _player.getHeight());
        int rectIndex = 0;

        if (_player.isFacing() == FACING.NORTH)
            rectIndex = 4;
        else if (_player.isFacing() == FACING.WEST)
            rectIndex = 8;
        else if (_player.isFacing() == FACING.EAST)
            rectIndex = 12;

        if (_player.state() == State.STANDING || _player.state() == State.MOVING)
            rectIndex += _player.getFrame()-1;
        else if (_player.state() == State.ATTACKING){
            rectIndex += 3;
            if (rectIndex == 3) // Facing south
                dst.set(_player.getLocationX(), _player.getLocationY(), _player.getLocationX()+_player.getWidth(), _player.getLocationY()+_player.getHeight()+_player.getAttackRange());
            else if (rectIndex == 7) // Facing north
                dst.set(_player.getLocationX(), _player.getLocationY()-_player.getAttackRange(), _player.getLocationX()+_player.getWidth(), _player.getLocationY()+_player.getHeight());
            else if (rectIndex == 11) // Facing west
                dst.set(_player.getLocationX()-_player.getAttackRange(), _player.getLocationY(), _player.getLocationX()+_player.getWidth(), _player.getLocationY()+_player.getHeight());
            else // Facing east
                dst.set(_player.getLocationX(), _player.getLocationY(), _player.getLocationX()+_player.getWidth()+_player.getAttackRange(), _player.getLocationY()+_player.getHeight());
        }

        if (_player.state() != State.GUARDING)
            canvas.drawBitmap(_playerBitmap, _playerRects[rectIndex], dst, null);
        else {
            rectIndex++;
            _paint.setColor(Color.WHITE);
            float shieldThickness = 2f, shieldLength = _player.getWidth() / 2, shieldLocation = _player.getLocationY() + (_player.getHeight()/4);;
            RectF sdst = new RectF(0, 0, 0, 0);

            if (_player.isFacing() == FACING.NORTH){
                shieldLocation = _player.getLocationX() + (_player.getWidth()/4);
                sdst.set(shieldLocation, _player.getLocationY()-shieldThickness, shieldLocation+shieldLength,  _player.getLocationY());
            }
            else if (_player.isFacing() == FACING.EAST)
                sdst.set(_player.getLocationX()+_player.getWidth(), shieldLocation, _player.getLocationX()+_player.getWidth()+shieldThickness, shieldLocation+shieldLength);
            else if (_player.isFacing() == FACING.WEST)
                sdst.set(_player.getLocationX()-shieldThickness, shieldLocation, _player.getLocationX(), shieldLocation+shieldLength);
            
            if (_player.isFacing() == FACING.SOUTH){
                sdst.set(_player.getLocationX(), shieldLocation+(shieldLength/2), _player.getLocationX()+(_player.getWidth()/2), _player.getLocationY()+_player.getHeight());
                canvas.drawBitmap(_playerBitmap, _playerRects[rectIndex], dst, null);
                canvas.drawBitmap(_shieldBitmap, _playerRects[16], sdst, null);
            }
            else {
                canvas.drawRect(sdst, _paint);
                canvas.drawBitmap(_playerBitmap, _playerRects[rectIndex], dst, null);
            }
        }
    }

    private void drawMoblin(Canvas canvas){
        RectF dst = new RectF(0, 0, 0, 0);
        int rectIndex = 0; // South as default

        if (_moblin.isFacing() == FACING.NORTH || _moblin.isFacing() == FACING.SOUTH){
            if (_moblin.isFacing() == FACING.NORTH)
                rectIndex = 3;
            
            if (_moblin.state() == State.ATTACKING){
                if (_moblin.isFacing() == FACING.SOUTH)
                    dst.set(_moblin.getLocationX(), _moblin.getLocationY(), _moblin.getLocationX()+_moblin.getWidth(), _moblin.getLocationY()+_moblin.getHeight()+_moblin.getAttackRange());
                else
                    dst.set(_moblin.getLocationX(), _moblin.getLocationY()-_moblin.getAttackRange(), _moblin.getLocationX()+_moblin.getWidth(), _moblin.getLocationY()+_moblin.getHeight());
                
                rectIndex++;
            }
            else {
                dst.set(_moblin.getLocationX(), _moblin.getLocationY()-(_moblin.getAttackRange()/2), _moblin.getLocationX()+_moblin.getWidth(), _moblin.getLocationY()+_moblin.getHeight()+(_moblin.getAttackRange()/2));
                if (_moblin.getFrame() == 2)
                    rectIndex += 2;
            }
        }
        else { // It's  East or West
            if (_moblin.isFacing() == FACING.WEST)
                rectIndex = 6;
            else
                rectIndex = 9;
            if (_moblin.state() == State.ATTACKING)
                rectIndex += 2;
            else
                rectIndex += _moblin.getFrame() - 1;

            if (_moblin.getFrame() == 1 && _moblin.state() != State.ATTACKING)
                dst.set(_moblin.getLocationX()-(_moblin.getAttackRange()/2), _moblin.getLocationY(), _moblin.getLocationX()+_moblin.getWidth()+(_moblin.getAttackRange()/2), _moblin.getLocationY()+_moblin.getHeight());
            else if (_moblin.isFacing() == FACING.WEST && _moblin.state() != State.ATTACKING || _moblin.isFacing() == FACING.EAST && _moblin.state() == State.ATTACKING)
                dst.set(_moblin.getLocationX(), _moblin.getLocationY(), _moblin.getLocationX()+_moblin.getWidth()+_moblin.getAttackRange(), _moblin.getLocationY()+_moblin.getHeight());
            else
                dst.set(_moblin.getLocationX()-_moblin.getAttackRange(), _moblin.getLocationY(), _moblin.getLocationX()+_moblin.getWidth(), _moblin.getLocationY()+_moblin.getHeight());
        }

        canvas.drawBitmap(_moblinBitmap, _moblinRects[rectIndex], dst, null);
        
        // Sword blocking effect
        if (_moblin.state() == State.GUARDING){
            float cx, cy, r = _moblin.getWidth()/2;
            Paint paint = new Paint();
            paint.setColor(Color.argb(127, 255, 255, 255));

            if (_moblin.isFacing() == FACING.NORTH){
                cx = _moblin.getLocationX() + (_moblin.getWidth()/2);
                cy = _moblin.getLocationY();
            }
            else if (_moblin.isFacing() == FACING.SOUTH){
                cx = _moblin.getLocationX() + (_moblin.getWidth()/2);
                cy = _moblin.getLocationY() + _moblin.getHeight();
            }
            else if (_moblin.isFacing() == FACING.EAST){
                cx = _moblin.getLocationX() + _moblin.getWidth();
                cy = _moblin.getLocationY() + (_moblin.getHeight()/2);
            }
            else {
                cx = _moblin.getLocationX();
                cy = _moblin.getLocationY() + (_moblin.getHeight()/2);
            }
            
            canvas.drawCircle(cx, cy, r, paint);
            r -= r / 2;
            paint.setColor(Color.argb(50, 255, 255, 255));
            canvas.drawCircle(cx, cy, r, paint);
        }
    }

    private void drawDekus(Canvas canvas){
        Rect src = new Rect(0, 0, 0, 0);
        RectF dst = new RectF(0, 0, 0, 0);
        float cx = 0, cy = 0, radius = _dekus.get(0).getNut().getWidth() / 2;
        
        for (Deku d : _dekus){
            // Draw dekunut
            if (d.readyToShoot() == false){
                cx = d.getNut().getLocationX() + (d.getNut().getWidth()/2);
                cy = d.getNut().getLocationY() + (d.getNut().getWidth()/2);
                _paint.setColor(Color.BLACK);
                canvas.drawCircle(cx, cy, radius+1, _paint); // Border
                _paint.setColor(Color.YELLOW);
                canvas.drawCircle(cx, cy, radius, _paint);
                _paint.setColor(Color.RED);
                canvas.drawCircle(cx, cy, (radius/4)*3, _paint);
            }

            // Draw the actual Deku
            dst.set(d.getLocationX(), d.getLocationY(), d.getLocationX()+d.getWidth(), d.getLocationY()+d.getHeight());
            if (d.state() != State.GUARDING){
                if (d.isFacing() == FACING.SOUTH)
                    src.set(0, 0, 15, 20);
                else if (d.isFacing() == FACING.NORTH)
                    src.set(0, 20, 15, 40);
                else if (d.isFacing() == FACING.EAST)
                    src.set(0, 40, 20, 55);
                else
                    src.set(0, 55, 20, 70);
                canvas.drawBitmap(_dekuBitmap, src, dst, null);
            }
            else {
                if (d.isFacing() == FACING.SOUTH)
                    src.set(0, 0, 247, 204);
                else if (d.isFacing() == FACING.NORTH)
                    src.set(247, 0, 494, 204);
                else if (d.isFacing() == FACING.EAST)
                    src.set(0, 204, 204, 451);
                else
                    src.set(204, 204, 408, 451);
                canvas.drawBitmap(_dekuFlowerBitmap, src, dst, null);
            }
            
        }
    }

    private void drawRupees(Canvas canvas){
        Rect src = new Rect(0, 0, _rupeeBitmap.getWidth(), _rupeeBitmap.getHeight());
        RectF dst = new RectF(0, 0, 0, 0);

        // A Rupee can be null by the time the iteration tries to draw it.
        try {
            for (Wall r : _rupees){
                dst.set(r.getLocationX(), r.getLocationY(), r.getLocationX()+r.getWidth(), r.getLocationY()+r.getHeight());
                canvas.drawBitmap(_rupeeBitmap, src, dst, null);
            }
        } catch (Exception e) { }
    }

    private void drawHearts(Canvas canvas){
        Rect src = new Rect(0, 0, _heartBitmap.getWidth()/3, _heartBitmap.getHeight());
        RectF dst = new RectF(0, 0, 0, 0);

        // A Heart can be null by the time the iteration tries to draw it.
        try {
            for (Wall h : _hearts){
                dst.set(h.getLocationX(), h.getLocationY(), h.getLocationX()+h.getWidth(), h.getLocationY()+h.getHeight());
                canvas.drawBitmap(_heartBitmap, src, dst, null);
            }
        } catch (Exception e) { }
    }

    private void drawBushes(Canvas canvas){
        Rect src = new Rect(0, 0, _bushBitmap.getWidth(), _bushBitmap.getHeight());
        RectF dst = new RectF(0, 0, 0, 0);

        // A Bush can be null by the time the iteration tries to draw it.
        try {
            for (Wall b : _bushes){
                dst.set(b.getLocationX(), b.getLocationY(), b.getLocationX()+b.getWidth(), b.getLocationY()+b.getHeight());
                canvas.drawBitmap(_bushBitmap, src, dst, null);
            }
        } catch (Exception e) { }
    }

    private void drawBridges(Canvas canvas){
        RectF rectf = new RectF(0, 0, 0, 0);
        float fx, fy;
        int borderColor = Color.rgb(135, 135, 0);
        
        for (Square s : _squares){
            fx = s.getLocationX() + s.getWidth();
            fy = s.getLocationY() + s.getHeight();

            // Draw the actual brige:
            if (s.Up() != null){
                // Border
                rectf.set(s.getLocationX(), s.Up().getLocationY()+s.Up().getHeight(), fx, s.getLocationY());
                _paint.setColor(borderColor);
                canvas.drawRect(rectf, _paint);
                // Wood
                rectf.set(s.getLocationX()+1, s.Up().getLocationY()+s.Up().getHeight()+1, fx-1, s.getLocationY()-1);
                _paint.setColor(s.getColor());
                canvas.drawRect(rectf, _paint);
            }
            if (s.Down() != null){
                // Border
                rectf.set(s.getLocationX(), s.getLocationY()+s.getHeight(), fx, s.Down().getLocationY());
                _paint.setColor(borderColor);
                canvas.drawRect(rectf, _paint);
                // Wood
                rectf.set(s.getLocationX()+1, s.getLocationY()+s.getHeight()+1, fx-1, s.Down().getLocationY()-1);
                _paint.setColor(s.getColor());
                canvas.drawRect(rectf, _paint);
            }
            if (s.Right() != null){
                // Border
                rectf.set(s.getLocationX()+s.getWidth(), s.getLocationY(), s.Right().getLocationX(), fy);
                _paint.setColor(borderColor);
                canvas.drawRect(rectf, _paint);
                // Wood
                rectf.set(s.getLocationX()+s.getWidth()+1, s.getLocationY()+1, s.Right().getLocationX()-1, fy-1);
                _paint.setColor(s.getColor());
                canvas.drawRect(rectf, _paint);
            }
            if (s.Left() != null){
                // Border
                rectf.set(s.Left().getLocationX()+s.Left().getWidth(), s.getLocationY(), s.getLocationX(), fy);
                _paint.setColor(borderColor);
                canvas.drawRect(rectf, _paint);
                // Wood
                rectf.set(s.Left().getLocationX()+s.Left().getWidth()+1, s.getLocationY()+1, s.getLocationX()-1, fy-1);
                _paint.setColor(s.getColor());
                canvas.drawRect(rectf, _paint);
            }

            // Draw Square/Tile
            if (s.getFloor() > 1){
                rectf.set(s.getLocationX(), s.getLocationY(), fx, fy);
                _paint.setColor(s.getColor());
                canvas.drawRect(rectf, _paint);
            }
            
        }

        for (Square s : _squares){
            if (s.getFloor() > 1)
                continue;

            if (s.Down() != null)
                drawStairs(canvas, s, 1);
            if (s.Up() != null)
                drawStairs(canvas, s, 2);
            if (s.Left() != null)
                drawStairs(canvas, s, 3);
            if (s.Right() != null)
                drawStairs(canvas, s, 4);
        }
    }

    private void drawStairs(Canvas canvas, Square square, int type){
        int nStairs = 4, i;
        float x, y, fx, fy;
        float stairLength = _walls.get(0).getWidth() / 3;
        RectF dst = new RectF(0, 0, 0, 0);
        _paint.setColor(square.getColor());

        if (type == 1 || type == 2){ // Vertical
            x = square.getLocationX();
            fx = x + square.getWidth();
            // Stairs length
            if (type == 1){
                y = square.getLocationY() + square.getHeight();
                fy = y + _walls.get(0).getWidth() + ((nStairs - 3) * stairLength);
                dst.set(x, y, fx, fy);
                fy = y;
            } else {
                fy = square.getLocationY();
                y = fy - _walls.get(0).getWidth() - ((nStairs - 3) * stairLength);;
                dst.set(x, y, fx, fy);
                y = fy;
            }
            canvas.drawRect(dst, _paint);

            // Stairs' border
            _paint.setColor(Color.BLACK);
            canvas.drawLine(x, y, fx, fy, _paint);
            for (i = 0; i < nStairs; i++){
                if (type == 1)
                    fy = y += stairLength;
                else
                    fy = y -= stairLength;
                canvas.drawLine(x, y, fx, fy, _paint);
            }    
        } else { // Horizontal
            y = square.getLocationY();
            fy = y + square.getHeight();
            // Stairs length
            if (type == 3){
                fx = square.getLocationX();
                x = fx - _walls.get(0).getWidth() - ((nStairs - 3) * stairLength);
                dst.set(x, y, fx, fy);
                x = fx;
            } else {
                x = square.getLocationX() + square.getWidth();
                fx = x + _walls.get(0).getWidth() + ((nStairs - 3) * stairLength);
                dst.set(x, y, fx, fy);
                fx = x;
            }
            canvas.drawRect(dst, _paint);

            // Stairs' border
            _paint.setColor(Color.BLACK);
            canvas.drawLine(x, y, fx, fy, _paint);
            for (i = 0; i < nStairs; i++){
                if (type == 3)
                    fx = x -= stairLength;
                else
                    fx = x += stairLength;
                canvas.drawLine(x, y, fx, fy, _paint);
            }
        }
    }

    private void drawExit(Canvas canvas){
        Square exit = _squares.get(_squares.size()-1);
        float cx = exit.getLocationX() + (exit.getWidth()/2);
        float cy = exit.getLocationY() + (exit.getHeight()/2);
        float radius = exit.getWidth() / 2;
        _exit.drawCircle(canvas, cx, cy, radius, 2);
    }

    private void drawColorScreen(Canvas canvas){
        if (_drawColor == 1)
            canvas.drawColor(Color.RED);
        else if (_drawColor == 2)
            canvas.drawColor(Color.GREEN);
        else if (_drawColor == 3)
            canvas.drawColor(Color.YELLOW);
        else
            canvas.drawColor(Color.rgb(204, 153, 255));

        try {
            Thread.sleep(125);
        } catch (Exception e) { }
        finally {
            _drawColor = 0;
        }
    }

    private void drawGameOverScreen(Canvas canvas){
        Rect src = new Rect(0, 0, _gameOverBitmap.getWidth(), _gameOverBitmap.getHeight());
        RectF dst = new RectF(0, 0, 0, 0);
        float x, y, width, height;
        float bw = BaseScales.BOARD_WIDTH, bh = BaseScales.BOARD_HEIGHT;

        if (MainMenuActivity.usingAccelerometer()){
            width = bw;
            height = ((float)_gameOverBitmap.getHeight() / (float)_gameOverBitmap.getWidth()) * bw;
            y = (bh / 2) - (height / 2);
            dst.set(0, y, width, y+height);
            _camera.set(0, 0, (int)bw, (int)bh);
        }
        else {
            height = ((float)getHeight() / (float)getWidth()) * bw;
            width = ((float)_gameOverBitmap.getWidth() / (float)_gameOverBitmap.getHeight()) * height;
            x = (bw / 2) - (width / 2);
            dst.set(x, 0, x+width, height);
            _camera.set(0, 0, (int)bw, (int)height);
        }
        
        canvas.drawBitmap(_gameOverBitmap, src, dst, null);
    }

    private void drawOnBitmap(Rect src, RectF dst){
        if (_drawColor > 0){
            drawColorScreen(_canvas);
            return;
        }
        if (_player.getLife() == 0){
            _canvas.drawColor(Color.BLACK); // Background
            drawGameOverScreen(_canvas);
            return;
        }
        // 1st floor:
        _canvas.drawBitmap(_mazeBitmap, src, dst, null);
        drawDekus(_canvas);
        drawRupees(_canvas);
        drawHearts(_canvas);
        drawBushes(_canvas);
        if (_drawExit)
            drawExit(_canvas);
        if (_player.getFloor() == 1)
            drawPlayer(_canvas);
        
        // 2nd floor:
        _canvas.drawBitmap(_bridgesBitmap, src, dst, null);
        if (_player.getFloor() == 2)
            drawPlayer(_canvas);
        if (_moblin.getLife() > 0)
            drawMoblin(_canvas);

    }

    public void setCamera(FACING direction){
        int left = _camera.left, top = _camera.top, right = _camera.right, bottom = _camera.bottom; 
        int playerRange;
        
        switch (direction){
            case NORTH:
            {
                playerRange = (int) _player.getLocationY() - CAMERA_PADDING;
                if (top > 0 && playerRange < top){
                    top -= _player.getSpeed();
                    bottom -= _player.getSpeed();
                    if (top < 0){
                        top = 0;
                        bottom = _cameraHeight;
                    }
                }
                break;
            }
            case SOUTH:
            {
                playerRange = ((int) (_player.getLocationY() + _player.getHeight())) + CAMERA_PADDING;
                if (bottom < _bitmap.getHeight() && playerRange > bottom){
                    bottom += _player.getSpeed();
                    top += _player.getSpeed();
                    if (bottom > _bitmap.getHeight()){
                        bottom = _bitmap.getHeight();
                        top = bottom - _cameraHeight;
                    }
                }
                break;
            }
            case EAST:
            {
                playerRange = ((int) (_player.getLocationX() + _player.getWidth())) + CAMERA_PADDING;
                if (right < _bitmap.getWidth() && playerRange > right){
                    right += _player.getSpeed();
                    left += _player.getSpeed();
                    if (right > _bitmap.getWidth()){
                        right = _bitmap.getWidth();
                        left = right - _cameraWidth;
                    }
                }
                break;
            }
            case WEST:
            {
                playerRange = (int) _player.getLocationX() - CAMERA_PADDING;
                if (left > 0 && playerRange < left){
                    left -= _player.getSpeed();
                    right -= _player.getSpeed();
                    if (left < 0){
                        left = 0;
                        right = _cameraWidth;
                    }
                }
                break;
            }
        }

        _camera.set(left, top, right, bottom);
    }

    @Override
    public void run() {
        Rect src = new Rect(0, 0, _mazeBitmap.getWidth(), _mazeBitmap.getHeight());
        RectF dst = new RectF(0, 0, getWidth(), getHeight()), bmpDst = new RectF(0, 0, _bitmap.getWidth(), _bitmap.getHeight());
        Canvas canvas = null;

        while (_hasStarted){
            if (!_running || !_holder.getSurface().isValid())
                continue;
            
            canvas = _holder.lockCanvas();
            drawOnBitmap(src, bmpDst);
            canvas.drawBitmap(_bitmap, _camera, dst, null);
            _holder.unlockCanvasAndPost(canvas);
            
        }
    }
    
}