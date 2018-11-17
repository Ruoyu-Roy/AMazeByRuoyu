package ruoyuli.cs301.cs.wm.edu.amazebyruoyu.gui;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
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
    private int shortestPath = 30;
    private int userPath = 300;
    private String LOG_V = "PlayManuallyActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manually_activity);
        setUpVariables();
        setToggleButtons();
        setButtons();
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

    public void toWin(View view){
        Log.v(LOG_V, "User wins the game, go to the winning screen.");
        Intent intent = new Intent(this, WinningActivity.class);
        intent.putExtra("drvalgo", "Manual");
        intent.putExtra("shortestPath", shortestPath);
        intent.putExtra("userPath", userPath);
        startActivity(intent);
        finish();
    }

    public void setButtons(){
        size_up.setVisibility(View.INVISIBLE);
        size_down.setVisibility(View.INVISIBLE);
    }

    public void moveUp(View view){
        Log.v(LOG_V, "MOVE_UP button clicked. User moves forward one step.");
        userPath++;
    }

    public void moveRight(View view){
        Log.v(LOG_V, "MOVE_RIGHT button clicked. User turns right.");
    }

    public void moveLeft(View view){
        Log.v(LOG_V, "MOVE_LEFT button clicked. User turns left.");
    }

    public void sizeUp(View view){
        Log.v(LOG_V, "Increment map size.");
    }

    public void sizeDown(View view){
        Log.v(LOG_V, "Decrement map size.");
    }

    public void backButtonClicked(View view) {
        Log.v(LOG_V, "Go back to title screen.");
        Intent intent = new Intent(this, AMazeActivity.class);
        startActivity(intent);
        finish();
    }

    public void setToggleButtons(){
        wall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.v(LOG_V, "Wall_Button clicked. Show the currently visible walls");
                }
                else {
                   Log.v(LOG_V, "Wall_Button unclicked. Hide the currently visible walls");
                }
            }
        });
        map.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.v(LOG_V, "Map_Button clicked. Show the map of the maze and show the size buttons");
                    size_up.setVisibility(View.VISIBLE);
                    size_down.setVisibility(View.VISIBLE);
                }
                else {
                    Log.v(LOG_V, "Map_Button unclicked. Hide the map of the maze and the size buttons");
                    setButtons();
                }
            }
        });
        clue.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.v(LOG_V, "Clue_Buttons clicked. Show the solution.");
                }
                else {
                    Log.v(LOG_V, "Clue_Button unclicked. Hide the solution");
                }
            }
        });
    }

}
