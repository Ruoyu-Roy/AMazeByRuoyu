package ruoyuli.cs301.cs.wm.edu.amazebyruoyu.gui;

import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ToggleButton;

import ruoyuli.cs301.cs.wm.edu.amazebyruoyu.R;

public class PlayManuallyActivity extends AppCompatActivity {
    //Basic variables
    Button go2win;
    ImageButton up;
    ImageButton left;
    ImageButton right;
    ToggleButton wall;
    ToggleButton map;
    ToggleButton clue;
    ImageButton size_up;
    ImageButton size_down;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manually_activity);
        setUpVariables();
    }

    private void setUpVariables(){
        go2win = (Button) findViewById(R.id.go2win);
        up = (ImageButton) findViewById(R.id.upbutton);
        left = (ImageButton) findViewById(R.id.leftbutton);
        right = (ImageButton) findViewById(R.id.rightbutton);
        wall = (ToggleButton) findViewById(R.id.wallbuttonM);
        map = (ToggleButton) findViewById(R.id.mapbuttonM);
        clue = (ToggleButton) findViewById(R.id.cluebuttonM);
        size_up = (ImageButton) findViewById(R.id.size_up_map);
        size_down = (ImageButton) findViewById(R.id.size_down_map);
    }

}
