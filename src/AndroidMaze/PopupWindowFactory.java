package AndroidMaze;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.cfai.maze.R;


public class PopupWindowFactory implements IPopupWindowFactory {
    
    private Context _context;
    private IPopupWindowFactoryClient _client;
    private int COLOR_MAINLAYOUT_BACKGROUND;
    private int COLOR_CHILDLAYOUT_BACKGROUND;
    private int COLOR_BUTTON;
    private int COLOR_TITLE;
    
    public PopupWindowFactory(Context context, IPopupWindowFactoryClient client){
        _context = context;
        _client = client;

        COLOR_MAINLAYOUT_BACKGROUND = context.getResources().getColor(R.color.mainlayout_background);
        COLOR_CHILDLAYOUT_BACKGROUND = context.getResources().getColor(R.color.childlayout_background);
        COLOR_BUTTON = context.getResources().getColor(R.color.button);
        COLOR_TITLE = context.getResources().getColor(R.color.title);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public PopupWindow create(String title, Window window){
        LinearLayout popupLayout = new LinearLayout(_context);
        popupLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        popupLayout.setOrientation(LinearLayout.VERTICAL);
        popupLayout.setPadding(20, 20, 20, 20);
        popupLayout.setBackgroundColor(COLOR_MAINLAYOUT_BACKGROUND);
        
        LinearLayout.LayoutParams lp;
        TextView tv2;
        Button b1;
        
        TextView tv1 = new TextView(_context);
        tv1.setText(title);
        tv1.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        tv1.setGravity(Gravity.CENTER);
        tv1.setTextSize(20);
        tv1.setTextColor(COLOR_TITLE);
        popupLayout.addView(tv1);

        switch (window){
            case SETTINGS:
                tv2 = new TextView(_context);
                tv2.setText("Control type:");
                tv2.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
                tv2.setGravity(Gravity.CENTER);
                tv2.setTextSize(15);
                tv2.setPadding(10, 10, 10, 10);
                tv2.setBackgroundColor(COLOR_CHILDLAYOUT_BACKGROUND);
                popupLayout.addView(tv2);

                RadioGroup rg = new RadioGroup(_context);
                rg.setBackgroundColor(COLOR_CHILDLAYOUT_BACKGROUND);
                
                RadioButton rb3 = new RadioButton(_context);
                rb3.setText("Arrow buttons");
                rb3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        _client.popupWindowEvent(2);
                    }
                });
                rg.addView(rb3);

                RadioButton rb1 = new RadioButton(_context);
                rb1.setText("Joystick");
                rb1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        _client.popupWindowEvent(4);
                    }
                });
                rg.addView(rb1);

                RadioButton rb2 = new RadioButton(_context);
                rb2.setText("Accelerometer (Hero mode)");
                rb2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        _client.popupWindowEvent(3);
                    }
                });
                rg.addView(rb2);
                popupLayout.addView(rg);

                b1 = new Button(_context);
                b1.setText("OK");
                lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 20, 0, 0);
                b1.setLayoutParams(lp);
                b1.setGravity(Gravity.CENTER);
                b1.setBackground(_context.getResources().getDrawable(R.drawable.maze_button));
                b1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v){
                        _client.popupWindowEvent(1);
                    }
                });
                popupLayout.addView(b1);
            break;
            case PAUSE:
                lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 20, 0, 0);
                
                b1 = new Button(_context);
                b1.setText("Continue");
                b1.setLayoutParams(lp);
                b1.setGravity(Gravity.CENTER);
                b1.setBackground(_context.getResources().getDrawable(R.drawable.maze_button));
                b1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v){
                        _client.popupWindowEvent(1);
                    }
                });
                popupLayout.addView(b1);
        
                Button b2 = new Button(_context);
                b2.setText("Back to Main Menu");
                b2.setLayoutParams(lp);
                b2.setGravity(Gravity.CENTER);
                b2.setBackground(_context.getResources().getDrawable(R.drawable.maze_button));
                b2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v){
                        _client.popupWindowEvent(2);
                    }
                });
                popupLayout.addView(b2);
            break;
            case LEADERBOARD_ENTRY:
                tv2 = new TextView(_context);
                tv2.setText("You got a top score!\n\nEnter your name:");
                tv2.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
                tv2.setGravity(Gravity.CENTER);
                tv2.setTextSize(15);
                tv2.setPadding(10, 10, 10, 10);
                tv2.setBackgroundColor(COLOR_CHILDLAYOUT_BACKGROUND);
                popupLayout.addView(tv2);

                final EditText et = new EditText(_context);
                et.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
                et.setBackgroundColor(Color.WHITE);
                popupLayout.addView(et);

                b1 = new Button(_context);
                b1.setText("OK");
                lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 20, 0, 0);
                b1.setLayoutParams(lp);
                b1.setGravity(Gravity.CENTER);
                b1.setBackground(_context.getResources().getDrawable(R.drawable.maze_button));
                b1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v){
                        String name = et.getText().toString();
                        _client.popupWindowEvent(name);
                    }
                });
                popupLayout.addView(b1);
            break;
        }

        PopupWindow popupWindow = new PopupWindow(popupLayout, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);

        return popupWindow;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public PopupWindow create(String title, String message){
        LinearLayout popupLayout = new LinearLayout(_context);
        popupLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        popupLayout.setOrientation(LinearLayout.VERTICAL);
        popupLayout.setPadding(20, 20, 20, 20);
        popupLayout.setBackgroundColor(COLOR_MAINLAYOUT_BACKGROUND);
        
        TextView tv1 = new TextView(_context);
        tv1.setText(title);
        tv1.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        tv1.setGravity(Gravity.CENTER);
        tv1.setTextSize(20);
        tv1.setTextColor(COLOR_TITLE);
        popupLayout.addView(tv1);

        TextView tv2 = new TextView(_context);
        tv2.setText(message);
        tv2.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        tv2.setGravity(Gravity.CENTER);
        tv2.setTextSize(15);
        tv2.setPadding(10, 10, 10, 10);
        tv2.setBackgroundColor(COLOR_CHILDLAYOUT_BACKGROUND);
        popupLayout.addView(tv2);

        final PopupWindow popupWindow = new PopupWindow(popupLayout, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);

        Button b = new Button(_context);
        b.setText("OK");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 20, 0, 0);
        b.setLayoutParams(lp);
        b.setGravity(Gravity.CENTER);
        b.setBackground(_context.getResources().getDrawable(R.drawable.maze_button));
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                popupWindow.dismiss();
                _client.popupWindowEvent(0);
            }
        });
        popupLayout.addView(b);

        return popupWindow;
    }

}
