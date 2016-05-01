package AndroidMaze;

import java.util.ArrayList;
import java.util.List;

import Maze.IGraphicsEngine;
import Maze.Characters.Character.FACING;
import Maze.Characters.Character.State;
import Maze.Characters.Ganon;
import Maze.Characters.Ganon.Action;
import Maze.Characters.Ganon.Ball;
import Maze.Characters.Player;
import Maze.Objects.Wall;
import Maze.Processing.GanonFireAttackStrategy;
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

import com.cfai.maze.BossBattleActivity;
import com.cfai.maze.R;


public class BossBattleGraphicsEngine extends SurfaceView implements IGraphicsEngine, Runnable {
    // Fields
    private SurfaceHolder _holder;
    private Thread _thread;
    private List<Wall> _walls, _torches;
    private Player _player;
    private Ganon _ganon;
    private boolean _running, _hasStarted;
    private Paint _paint;
    private int _cameraWidth, _cameraHeight, _drawColor;
    private final int CAMERA_PADDING = 30;    
    private Canvas _canvas;
    private Bitmap _bitmap, _groundBitmap, _playerBitmap, _shieldBitmap, _heartBitmap, _ganonBitmap, _torchBitmap, _gameOverBitmap, _triforceBitmap;
    private Fractal _bounceBall;
    private Rect[] _playerRects;
    private Rect _camera;

    // Constructor
    public BossBattleGraphicsEngine(Context context) {
        super(context);
        
        // Engine variables
        _holder = getHolder();
        _running = false;
        _hasStarted = false;
        _thread = new Thread(this);
        _drawColor = 0;
        
        // Drawing resources
        Options opts = new Options();
        opts.inScaled = false;
        _playerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.link, opts);
        _shieldBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.shield, opts);
        _ganonBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ganon, opts);
        _torchBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.torch, opts);
        _gameOverBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.game_over, opts);
        _triforceBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.btriforce, opts);

        _bounceBall = new Fractal(255, 255, 167, 120, 90, 4, 10);
        _bounceBall.fromInsideToOutside(true);
        _paint = new Paint();
        
        // Camera
        _cameraWidth = BaseScales.CAMERA_WIDTH;
        _cameraHeight = BaseScales.CAMERA_HEIGHT;
        _cameraHeight /= 3;
        setRects();
    }    

    // Implementes methods
    public void setGameObjects(ArrayList<Object> gameObjects){
        _player = (Player) gameObjects.get(0);
        _ganon = (Ganon) gameObjects.get(1);
        _walls = (ArrayList<Wall>) gameObjects.get(2);
        _torches = (ArrayList<Wall>) gameObjects.get(3);
        
        _bitmap = Bitmap.createBitmap((int)BaseScales.BOSS_BOARD_WIDTH, (int)BaseScales.BOSS_BOARD_HEIGHT, Bitmap.Config.RGB_565);
        _canvas = new Canvas(_bitmap);
        _groundBitmap = getGroundBitmap();
        
        _camera.set((_bitmap.getWidth()/2)-(_cameraWidth/2), (_bitmap.getHeight()/2)-(_cameraHeight/2), (_bitmap.getWidth()/2)+(_cameraWidth/2), (_bitmap.getHeight()/2)+(_cameraHeight/2));
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
    }

    private Bitmap getGroundBitmap(){
        float width = BaseScales.BOSS_BOARD_WIDTH;
        float height = BaseScales.BOSS_BOARD_HEIGHT;
        Bitmap bitmap = Bitmap.createBitmap((int) width, (int) height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.rgb(50, 190, 50)); // Grass
        drawGround(canvas);
        drawWalls(canvas, 1);
        
        return bitmap;
    }

    private void drawGround(Canvas canvas){
        canvas.drawColor(Color.argb(255, 57, 57, 57));

        float radius = (_bitmap.getHeight()/2)-(_walls.get(0).getWidth()*2), cBorder = 4;
        float delta = radius / 5;
        float cx = _bitmap.getWidth()/2, cy = _bitmap.getHeight()/2;
        int border = Color.rgb(120, 120, 120);
        int circle = Color.rgb(85, 85, 85);

        _paint.setColor(border);
        canvas.drawCircle(cx, cy, radius, _paint);
        radius -= cBorder;
        _paint.setColor(circle);
        canvas.drawCircle(cx, cy, radius, _paint);
        radius -= delta;
        _paint.setColor(border);
        canvas.drawCircle(cx, cy, radius, _paint);
        radius -= cBorder;
        _paint.setColor(circle);
        canvas.drawCircle(cx, cy, radius, _paint);
        radius -= delta;
        _paint.setColor(border);
        canvas.drawCircle(cx, cy, radius, _paint);
        radius -= cBorder;
        _paint.setColor(circle);
        canvas.drawCircle(cx, cy, radius, _paint);
        
        canvas.drawBitmap(_triforceBitmap, new Rect(0, 0, _triforceBitmap.getWidth(), _triforceBitmap.getHeight()), new RectF(cx-(radius/2), cy-(radius/2), cx+(radius/2), cy+(radius/2)), null);
    }

    private void drawWalls(Canvas canvas, int floor){
        Wall w = null;
        RectF rectf = new RectF(0, 0, 0, 0);
        float fx, fy;
        
        for (int i = 0; i < _walls.size()-_torches.size(); i++){
            w = _walls.get(i);
            if (w.getFloor() == floor){
                // Border
                fx = w.getLocationX() + w.getWidth();
                fy = w.getLocationY() + w.getHeight();
                rectf.set(w.getLocationX(), w.getLocationY(), fx, fy);
                _paint.setColor(Color.argb(255, 0, 0, 0));
                canvas.drawRect(rectf, _paint);
                
                fx -= 2;
                fy -= 2;
                rectf.set(w.getLocationX()+2, w.getLocationY()+2, fx, fy);
                _paint.setColor(w.getColor());
                canvas.drawRect(rectf, _paint);
            }
        }
    }

    private void drawTorches(Canvas canvas){
        Rect src = new Rect(0, 0, 0, 0);
        RectF dst = new RectF(0, 0, 0, 0);
        float cx, cy;

        for (Wall t : _torches){
            if (t.getFrame() == 1){
                _paint.setColor(Color.argb(100, 238, 206, 1));
                src.set(0, 0, _torchBitmap.getWidth()/2, _torchBitmap.getHeight());
            }
            else {
                _paint.setColor(Color.argb(100, 255, 0, 0));
                src.set(_torchBitmap.getWidth()/2, 0, _torchBitmap.getWidth(), _torchBitmap.getHeight());
            }
            dst.set(t.getLocationX(), t.getLocationY()-t.getHeight(), t.getLocationX()+t.getWidth(), t.getLocationY()+t.getHeight());
            canvas.drawBitmap(_torchBitmap, src, dst, null);

            // Draw flame light.
            cx = t.getLocationX() + (t.getWidth()/2);
            cy = t.getLocationY() - (t.getHeight()/2);
            canvas.drawCircle(cx, cy, (t.getWidth()/2)+4, _paint);
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

    private void drawGanon(Canvas canvas){
        Rect src = new Rect(0, 0, 0, 0);
        RectF dst = new RectF(_ganon.getLocationX(), _ganon.getLocationY(), _ganon.getLocationX()+_ganon.getWidth(), _ganon.getLocationY()+_ganon.getHeight());
        
        if (_ganon.getAction() != Action.TELEPORT)
            src.set(0, 0, _ganonBitmap.getWidth()/2, _ganonBitmap.getHeight());
        else
            src.set(_ganonBitmap.getWidth()/2, 0, _ganonBitmap.getWidth(), _ganonBitmap.getHeight());
        canvas.drawBitmap(_ganonBitmap, src, dst, null);
    }

    private void drawGanonAction(Canvas canvas){
        float cx, cy;

        if (_ganon.getAction() == Action.ATTACK_BOUNCE){
            cx = _ganon.getLocationX() + (_ganon.getWidth()/2);
            cy = _ganon.getLocationY() + (_ganon.getHeight()/2);
            _bounceBall.drawCircle(canvas, cx, cy, _ganon.getWidth(), 2, 100);
        }
        else if (_ganon.getAction() == Action.ATTACK_FIREBALLS || _ganon.getStrategy() != null && _ganon.getStrategy().getClass() == GanonFireAttackStrategy.class){
            for (Ball b : _ganon.getFireBalls()){
                if (b.isEnabled() == false)
                    continue;
                    
                cx = b.getLocationX() + (b.getWidth()/2);
                cy = b.getLocationY() + (b.getHeight()/2);
                if (b.getFrame() == 1){
                    _paint.setColor(Color.RED);
                    canvas.drawCircle(cx, cy, b.getWidth()/2, _paint);
                    _paint.setColor(Color.rgb(242, 195, 5));
                    canvas.drawCircle(cx, cy, b.getWidth()/4, _paint);
                }
                else {
                    _paint.setColor(Color.rgb(242, 195, 5));
                    canvas.drawCircle(cx, cy, b.getWidth()/2, _paint);
                    _paint.setColor(Color.RED);
                    canvas.drawCircle(cx, cy, b.getWidth()/4, _paint);
                }
            }
        }
        else if (_ganon.getAction() == Action.ATTACK_THUNDERBALL){
            cx = _ganon.getThunderBall().getLocationX() + (_ganon.getThunderBall().getWidth()/2);
            cy = _ganon.getThunderBall().getLocationY() + (_ganon.getThunderBall().getHeight()/2);

            _paint.setColor(Color.argb(100, 255, 255, 0));
            canvas.drawCircle(cx, cy, (_ganon.getThunderBall().getWidth()/2)+2, _paint);
            _paint.setColor(Color.argb(100, 255, 255, 200));
            canvas.drawCircle(cx, cy, 3*(_ganon.getThunderBall().getWidth()/4), _paint);
            _paint.setColor(Color.argb(100, 252, 252, 122));
            canvas.drawCircle(cx, cy, _ganon.getThunderBall().getWidth()/4, _paint);
        }
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
        float bw = BaseScales.BOSS_BOARD_WIDTH, bh = BaseScales.BOSS_BOARD_HEIGHT;

        height = ((float)getHeight() / (float)getWidth()) * bw;
        width = ((float)_gameOverBitmap.getWidth() / (float)_gameOverBitmap.getHeight()) * height;
        x = (bw / 2) - (width / 2);
        dst.set(x, 0, x+width, height);
        _camera.set(0, 0, (int)bw, (int)height);
        
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
        _canvas.drawBitmap(_groundBitmap, src, dst, null);
        drawPlayer(_canvas);
        if (_ganon.getLife() > 0){
            drawGanon(_canvas);
            drawGanonAction(_canvas);
        }
        drawTorches(_canvas);
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
        Rect src = new Rect(0, 0, _bitmap.getWidth(), _bitmap.getHeight());
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

    // Unused methods
    public void drawExit() { }

}