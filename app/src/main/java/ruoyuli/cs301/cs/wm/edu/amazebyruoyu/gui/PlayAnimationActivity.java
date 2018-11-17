package ruoyuli.cs301.cs.wm.edu.amazebyruoyu.gui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import ruoyuli.cs301.cs.wm.edu.amazebyruoyu.R;

public class PlayAnimationActivity extends AppCompatActivity {

    ToggleButton wall;
    ToggleButton map;
    ToggleButton clue;
    ToggleButton pause;
    ImageButton size_up;
    ImageButton size_down;
    Button go2win;
    Button go2lose;
    TextView energy;
    ProgressBar energybar;
    Button back;

    private int energyReserve = 2500;
    private int shortestPath = 30;
    private int pathLength = 300;
    private String LOG_V = "PlayAnimationActivity";
    private boolean stopped = true;
    private boolean gameStop = false;
    private String driver;
    final private int TOTAL_ENERGY = 3000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.animation_activity);
        Intent preIntent = getIntent();
        driver = preIntent.getStringExtra("driverAlgorithm");
        setUpVariables();
        energybar.setMax(3000);
        energybar.setProgress(3000);
        setButtons();
        setToggleButtons();
    }

    private void setUpVariables(){
        go2win = (Button) findViewById(R.id.go2win);
        go2lose = (Button) findViewById(R.id.go2lose);
        back = (Button) findViewById(R.id.backP);
        energy = (TextView) findViewById(R.id.energy);
        energybar = (ProgressBar) findViewById(R.id.progressBar2);
        wall = (ToggleButton) findViewById(R.id.wallbuttonP);
        map = (ToggleButton) findViewById(R.id.mapbuttonP);
        clue = (ToggleButton) findViewById(R.id.cluebuttonP);
        pause = (ToggleButton) findViewById(R.id.pausetoggle);
        size_up = (ImageButton) findViewById(R.id.size_up_map);
        size_down = (ImageButton) findViewById(R.id.size_down_map);
    }

    public void toWin(View view){
        Log.v(LOG_V, "Robot wins the game, go to the winning screen.");
        Intent intent = new Intent(this, WinningActivity.class);
        intent.putExtra("energy_consumption", TOTAL_ENERGY-energyReserve);
        intent.putExtra("drvalgo", driver);
        intent.putExtra("shortestPath", shortestPath);
        intent.putExtra("robotPath", pathLength);
        startActivity(intent);
        finish();
    }

    public void toLose(View view){
        Log.v(LOG_V, "Robot fails the game, go to the losing screen.");
        Intent intent = new Intent(this, LosingActivity.class);
        intent.putExtra("energy_consumption", TOTAL_ENERGY-energyReserve);
        intent.putExtra("drvalgo", driver);
        intent.putExtra("shortestPath", shortestPath);
        intent.putExtra("robotPath", pathLength);
        intent.putExtra("stopped", stopped);
        startActivity(intent);
        finish();
    }

    public void setButtons(){
        size_up.setVisibility(View.INVISIBLE);
        size_down.setVisibility(View.INVISIBLE);
    }

    public void backButtonClicked(View view) {
        Log.v(LOG_V, "Go back to title screen.");
        Intent intent = new Intent(this, AMazeActivity.class);
        startActivity(intent);
        finish();
    }

    private void updateBatteryBar(){
        energybar.setProgress(energyReserve);
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
        pause.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    Log.v(LOG_V,"Pause button clicked. Pause the game.");
                    gameStop = true;
                }
                else{
                    Log.v(LOG_V, "Start button clicked. Start the game.");
                    gameStop = false;
                }
            }
        });
    }

    public void sizeUp(View view){
        Log.v(LOG_V, "Increment map size.");
    }

    public void sizeDown(View view){
        Log.v(LOG_V, "Decrement map size.");
    }
}
