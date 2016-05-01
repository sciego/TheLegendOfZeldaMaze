package AndroidMaze;

import java.util.ArrayList;

import Maze.IGraphicsEngine;
import Maze.MazeGameEngine;
import Maze.Characters.Character.FACING;
import Maze.Characters.Player;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;

import com.cfai.maze.R;

public class StatusBar extends SurfaceView implements IGraphicsEngine, Runnable {
    
    // Fields
    private Context _context;
    private Player _player;
    private SurfaceHolder _holder;
    private Thread _thread;
    private boolean _running, _hasStarted, _isTablet, _playerGotHit, _drawExit;
    private Paint _paint;
    private float _heartLength, _heartX, _statusX, _textY, _statusY; // Initial point for Hearts, Rupees, etc.
    private int _fontSize, _completeHearts, _emptyHearts;
    private Rect _src, _bmSrc;
    private RectF _dst, _bmDst;
    private Canvas _canvas;
    private Bitmap _bitmap, _heartsBitmap, _rupeeBitmap;
    
    // Constructor
    public StatusBar(Context context) {
        super(context);
        _context = context;
        _isTablet = context.getResources().getBoolean(R.bool.isTablet);
        LayoutParams lp = new LayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        setLayoutParams(lp);

        // Engine variables
        _holder = getHolder();
        _running = false;
        _hasStarted = false;
        _thread = new Thread(this);
               
        // Drawing resources
        _bitmap = Bitmap.createBitmap(230, 17, Bitmap.Config.RGB_565);
        _canvas = new Canvas(_bitmap);
        _heartsBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.hearts);
        _rupeeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.rupee);
        _paint = new Paint();
        _src = new Rect(0, 0, 0, 0);
        _dst = new RectF(0, 0, 0, 0);
        _bmSrc = new Rect(0, 0, 0, 0);
        _bmDst = new RectF(0, 0, 0, 0);
        
        // Drawing variables
        _statusX = 2f;
        _fontSize = 10;
        _heartLength = 15f;
        _textY = (_bitmap.getHeight() / 2) + 5;
        _statusY = _textY - ((_heartLength/4)*3);
        _paint.setTextSize(_fontSize);
        _paint.setColor(Color.WHITE);
    }

    // Implementes methods
    @Override
    public void setGameObjects(ArrayList<Object> gameObjects) {
        _player = (Player) gameObjects.get(0);
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

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);
        // Scale/adjust '_bitmap' to ImageView.
        float growth = (getWidth()-_bitmap.getWidth()) / (_bitmap.getWidth()/_bitmap.getHeight());
        if (getHeight()-_bitmap.getHeight() < growth)
            growth = getHeight()-_bitmap.getHeight();
        float dw = (_bitmap.getWidth()/_bitmap.getHeight()) * growth, dh = growth;
        _bmSrc.set(0, 0, _bitmap.getWidth(), _bitmap.getHeight());
        _bmDst.set((getWidth()/2)-((_bitmap.getWidth()+dw)/2), (getHeight()/2)-((_bitmap.getHeight()+dh)/2), (getWidth()/2)+((_bitmap.getWidth()+dw)/2), (getHeight()/2)+((_bitmap.getHeight()+dh)/2));
    }

    private void drawStatus(Canvas canvas){
        canvas.drawColor(Color.BLACK);
        
        // Draw life status
        canvas.drawText("LIFE " , _statusX, _textY, _paint);
        _completeHearts = _player.getLife() / 2;
        _emptyHearts = (_player.getMaxLife()/2) - _completeHearts;
        _src.set(0, 0, _heartsBitmap.getWidth()/3, _heartsBitmap.getHeight());
        _heartX = _statusX + (_fontSize*3);

        for (int i = 0; i < _completeHearts; i++){
            _dst.set(_heartX, _statusY, _heartX+_heartLength, _statusY+_heartLength);
            canvas.drawBitmap(_heartsBitmap, _src, _dst, null);
            if (i < _completeHearts-1)
                _heartX += _heartLength;
        }
        if (_player.getLife() % 2 != 0){
            _src.set(_heartsBitmap.getWidth()/3, 0, (_heartsBitmap.getWidth()/3)*2, _heartsBitmap.getHeight());
            if (_completeHearts != 0) // if half-heart is not the first to be drawn
                _heartX += _heartLength;
            _dst.set(_heartX, _statusY, _heartX+_heartLength, _statusY+_heartLength);
            canvas.drawBitmap(_heartsBitmap, _src, _dst, null);
            _emptyHearts--;
        }
        if (_emptyHearts > 0){
            _src.set((_heartsBitmap.getWidth()/3)*2, 0, _heartsBitmap.getWidth(), _heartsBitmap.getHeight());
            for (int i = 0; i < _emptyHearts; i++){
                if (_player.getLife() > 0 || i > 0)
                    _heartX += _heartLength;
                _dst.set(_heartX, _statusY, _heartX+_heartLength, _statusY+_heartLength);
                canvas.drawBitmap(_heartsBitmap, _src, _dst, null);
            }
        }

        // Draw Rupees count
        _src.set(0, 0, _rupeeBitmap.getWidth(), _rupeeBitmap.getHeight());
        _heartX += _heartLength * 2;
        _dst.set(_heartX, _statusY, _heartX+(2*(_heartLength/3)), _statusY+_heartLength);
        canvas.drawBitmap(_rupeeBitmap, _src, _dst, null);
        canvas.drawText("x "+String.valueOf(_player.getRupees()), _heartX+_heartLength, _textY, _paint);

        // Draw time elapsed
        String minutes = String.valueOf((MazeGameEngine.getTime() / 60));
        String seconds = String.valueOf((MazeGameEngine.getTime() % 60));
        canvas.drawText("TIME: " + minutes + "m " + seconds + "s", _heartX+(_heartLength*3), _textY, _paint);
    }

    @Override
    public void run() {
        Canvas canvas = null;
        Rect src = null;
        RectF dst = null;
        
        while(_hasStarted){
            if (!_running || !_holder.getSurface().isValid())
                continue;
            
            canvas = _holder.lockCanvas();
            drawStatus(_canvas);
            canvas.drawBitmap(_bitmap, _bmSrc, _bmDst, null);
            _holder.unlockCanvasAndPost(canvas);
            
            try {
                Thread.sleep(500);
            } catch (Exception e) { }
        }
    }

    // Unimplemented methods
    @Override
    public void drawColorScreen(int color){
        // TODO Auto-generated method stub
        
    }
    @Override
    public void drawExit() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setCamera(FACING direction) {
        // TODO Auto-generated method stub
        
    }

    
    
}