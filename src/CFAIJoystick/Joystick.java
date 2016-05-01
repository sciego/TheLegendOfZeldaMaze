package CFAIJoystick;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class Joystick extends LinearLayout {
    private IJoystickClient _client;
    private ImageView _ball;
    private float _initialX, _initialY;
    private int _prevX, _prevY;
    private boolean _moveX, _moveY;
    
    public Joystick(Context context, IJoystickClient client, int resid_bg, int resid_ball){
        super(context);
        _client = client;
        this.setBackgroundResource(resid_bg);
        _ball = new ImageView(context);
        _ball.setBackgroundResource(resid_ball);
        addView(_ball);
        
        this.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                move(v, event);
                return true;
            }
        });

        _prevX = -1;
        _prevY = -1;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);
        
        _ball.getLayoutParams().width = w / 2;
        _ball.getLayoutParams().height = w / 2;
        _initialX = w / 4;
        _initialY = w / 4;
        _ball.setX(_initialX);
        _ball.setY(_initialY);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void move(View v, MotionEvent event){
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                _prevX = (int)event.getX();
                _prevY = (int)event.getY();
                break;
            case MotionEvent.ACTION_UP:
                _ball.setX(_initialX);
                _ball.setY(_initialY);
                _prevX = -1;
                _prevY = -1;
                _moveX = false;
                _moveY = false;
                break;
             case MotionEvent.ACTION_MOVE:
                dragView(v, _ball, event);
                break;
        }

        
        // Move command
        int x = (int)event.getX();
        int y = (int)event.getY();
        int mw = v.getWidth()/2;
        int mh = v.getHeight()/2;
        if (_moveX && _moveY){ // Ball is in a corner
            // Will only move in one direction
            if (x > mw && y < mh)
                _client.move(Move.RIGHT);
            else if (x > mw && y > mh)
                _client.move(Move.DOWN);
            else if (x < mw && y > mh)
                _client.move(Move.LEFT);
            else
                _client.move(Move.UP);
        } else if (_moveX){
            if (x > mw)
                _client.move(Move.RIGHT);
            else
                _client.move(Move.LEFT);
        } else if (_moveY){
            if (y > mh)
                _client.move(Move.DOWN);
            else
                _client.move(Move.UP);
        } else {
            _client.move(Move.NONE);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void dragView(View area, View v, MotionEvent event){
        int ex = (int)event.getX();
        int ey = (int)event.getY();
        int bx = (int)v.getX();
        int by = (int)v.getY();
        int bw = v.getWidth();
        int bh = v.getHeight();
        
        if (ex >= bx && ex <= bx + bw
            && ey >= by && ey <= by + bh){
            int newX, newY;
            newX = bx + (ex - _prevX);
            newY = by + (ey - _prevY);
            _moveX = true;
            _moveY = true;

            if (newX < 0)
                newX = 0;
            else if (newX + bw > area.getWidth())
                newX = area.getWidth() - bw;
            else if (newX >= bw/4 && newX + bw <= (bw*2) - bw/4)
                _moveX = false;
            if (newY < 0)
                newY = 0;
            else if (newY + bh > getHeight())
                newY = area.getHeight() - bh;
            else if (newY >= bh/4 && newY + bh <= (bh*2) - bh/4)
                _moveY = false;

            v.setX((float)newX);
            v.setY((float)newY);

            _prevX = ex;
            _prevY = ey;
        }
        
    }

}
