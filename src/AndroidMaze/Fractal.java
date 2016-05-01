package AndroidMaze;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Fractal {
    // Fields
    private int[] _colors;
    private int _frame;
    private Paint _paint;
    private boolean _insideToOutside;

    public Fractal(int fromR, int toR, int fromG, int toG, int fromB, int toB, int nColors){
        // Array length cannot be > the biggest difference of RGB ranges
        int nMaxColors = Math.max(fromR, toR) - Math.min(fromR, toR);
        nMaxColors = Math.max(nMaxColors, Math.max(fromG, toG) - Math.min(fromG, toG));
        nMaxColors = Math.max(nMaxColors, Math.max(fromB, toB) - Math.min(fromB, toB));
        if (nColors > nMaxColors || nColors == 0)
            nColors = nMaxColors;

        _colors = new int[nColors];
        int deltaR = (Math.max(fromR, toR) - Math.min(fromR, toR)) / nColors;
        int deltaG = (Math.max(fromG, toG) - Math.min(fromG, toG)) / nColors;
        int deltaB = (Math.max(fromB, toB) - Math.min(fromB, toB)) / nColors;
        
        int r = 0, g = 0, b = 0;
        for (int i = 0; i < nColors; i++){
            r = fromR - (deltaR * i);
            g = fromG - (deltaG * i);
            b = fromB - (deltaB * i);
            _colors[i] = Color.rgb(r, g, b);
        }

        _paint = new Paint();
        _frame = 0;
        _insideToOutside = false;
    }

    public void fromInsideToOutside(boolean value){
        _insideToOutside = value;
    }

    // 'ms' is the time in miliseconds that the colors will change.
    public void drawCircle(Canvas canvas, float cx, float cy, float radius, int ms){
        float delta = radius / _colors.length;
        int color = _frame / ms;

        for (int i = 0; i < _colors.length; i++){
            _paint.setColor(_colors[color]);
            canvas.drawCircle(cx, cy, radius, _paint);
            radius -= delta;
            
            if (_insideToOutside){
                color++;
                if (color == _colors.length)
                    color = 0;
            }
            else {
                color--;
                if (color == -1)
                    color = _colors.length - 1;
            }
        }

        _frame++;
        if (_frame == _colors.length * ms) // Reset if passed last frame's time
            _frame = 0;
    }

    public void drawCircle(Canvas canvas, float cx, float cy, float radius, int ms, int alpha){
        float delta = radius / _colors.length;
        int color = _frame / ms;

        for (int i = 0; i < _colors.length; i++){
            _paint.setColor(_colors[color]);
            _paint.setAlpha(alpha - ((_colors.length - i) * (alpha / _colors.length)));
            canvas.drawCircle(cx, cy, radius, _paint);
            radius -= delta;
            
            if (_insideToOutside){
                color++;
                if (color == _colors.length)
                    color = 0;
            }
            else {
                color--;
                if (color == -1)
                    color = _colors.length - 1;
            }
        }

        _frame++;
        if (_frame == _colors.length * ms) // Reset if passed last frame's time
            _frame = 0;
    }

}
