package ruoyuli.cs301.cs.wm.edu.amazebyruoyu.gui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ToggleButton;

import ruoyuli.cs301.cs.wm.edu.amazebyruoyu.R;

public class PlayAnimationActivity extends AppCompatActivity {

    ToggleButton wall;
    ToggleButton map;
    ToggleButton clue;
    ImageButton size_up;
    ImageButton size_down;
    Button go2win;
    Button go2lose;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.animation_activity);
    }

    private void setUpVariables(){
        go2win = (Button) findViewById(R.id.go2win);
        go2lose = (Button) findViewById(R.id.go2lose);
        wall = (ToggleButton) findViewById(R.id.wallbuttonP);
        map = (ToggleButton) findViewById(R.id.mapbuttonP);
        clue = (ToggleButton) findViewById(R.id.cluebuttonP);
        size_up = (ImageButton) findViewById(R.id.size_up_map);
        size_down = (ImageButton) findViewById(R.id.size_down_map);
    }
}
