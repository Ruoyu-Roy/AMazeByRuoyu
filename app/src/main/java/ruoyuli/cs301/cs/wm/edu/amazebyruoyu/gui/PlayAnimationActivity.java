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
import android.widget.Toast;
import android.widget.ToggleButton;

import ruoyuli.cs301.cs.wm.edu.amazebyruoyu.R;
import ruoyuli.cs301.cs.wm.edu.amazebyruoyu.generation.MazeConfiguration;
import ruoyuli.cs301.cs.wm.edu.amazebyruoyu.generation.MazePanel;
import ruoyuli.cs301.cs.wm.edu.amazebyruoyu.generation.Robot;
import ruoyuli.cs301.cs.wm.edu.amazebyruoyu.generation.RobotDriver;
import ruoyuli.cs301.cs.wm.edu.amazebyruoyu.generation.StatePlaying;

/**
 * Play Animation Activity is the implementation java file for animation_activity xml file.
 * It takes care of the functionality of all elements in the play animation screen
 *
 * @author ruoyuli
 */

public class PlayAnimationActivity extends AppCompatActivity {
    //Basic Variables
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

    private int energyReserve = 3000;
    private int shortestPath;
    private int pathLength;
    private String LOG_V = "PlayAnimationActivity";
    private boolean stopped = true;
    private boolean gameStop = false;
    private String driver;
    final private int TOTAL_ENERGY = 3000;

    private MazeConfiguration mazeConfiguration;
    private MazePanel mazePanel;
    private StatePlaying statePlaying;
    private Robot robot;
    private RobotDriver rdriver;


    /*
    Override the onCreate method in AppCompatActivity class. This is a method that is the main thread
    of this whole class. Everything we do or want to run is in this class.
     */
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

    /*
    Set up basic variables(Gui elements), connecting them with the responding parts in the responding
    xml file.
     */
    private void setUpVariables(){
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

    /*
    Enable user to go to winning screen by clicking go2Winning button.
     */
    public void toWin(){
        Log.v(LOG_V, "Robot wins the game, go to the winning screen.");
        Toast.makeText(getApplicationContext(), "To Win Screen", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, WinningActivity.class);
        intent.putExtra("energy_consumption", TOTAL_ENERGY-energyReserve);
        intent.putExtra("drvalgo", driver);
        intent.putExtra("shortestPath", shortestPath);
        intent.putExtra("robotPath", pathLength);
        startActivity(intent);
        finish();
    }

    /*
    Enable user to go to losing screen by clicking go2losing button.
     */
    public void toLose(){
        Log.v(LOG_V, "Robot fails the game, go to the losing screen.");
        Toast.makeText(getApplicationContext(), "To Lose Screen", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LosingActivity.class);
        intent.putExtra("energy_consumption", TOTAL_ENERGY-energyReserve);
        intent.putExtra("drvalgo", driver);
        intent.putExtra("shortestPath", shortestPath);
        intent.putExtra("robotPath", pathLength);
        intent.putExtra("stopped", stopped);
        startActivity(intent);
        finish();
    }

    /*
    Set the size_up and size_down button invisible. They will be visible if map button is checked.
     */
    public void setButtons(){
        size_up.setVisibility(View.INVISIBLE);
        size_down.setVisibility(View.INVISIBLE);
    }

    /*
    This method enables user to get back to the menu screen by clicking the "back" button.
     */
    public void backButtonClicked(View view) {
        Log.v(LOG_V, "Go back to title screen.");
        Toast.makeText(getApplicationContext(), "Back to Menu", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, AMazeActivity.class);
        startActivity(intent);
        finish();
    }

    /*
    Update the battery bar based on the info given.
     */
    public void updateBatteryBar(){
        energybar.setProgress(energyReserve);
    }

    /*
    Set up the functionality of all the toggle button. Specifically, what will happen if the toggle
    is checked and what will happen if the toggle is unchecked.
     */
    public void setToggleButtons(){
        wall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.v(LOG_V, "Wall_Button clicked. Show the currently visible walls");
                    Toast.makeText(getApplicationContext(), "Wall Button on", Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.v(LOG_V, "Wall_Button unclicked. Hide the currently visible walls");
                    Toast.makeText(getApplicationContext(), "Wall Button off", Toast.LENGTH_SHORT).show();
                }
            }
        });
        map.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.v(LOG_V, "Map_Button clicked. Show the map of the maze and show the size buttons");
                    Toast.makeText(getApplicationContext(), "Size Button Shown", Toast.LENGTH_SHORT).show();
                    size_up.setVisibility(View.VISIBLE);
                    size_down.setVisibility(View.VISIBLE);
                }
                else {
                    Log.v(LOG_V, "Map_Button unclicked. Hide the map of the maze and the size buttons");

                    Toast.makeText(getApplicationContext(), "Size Button Hiden", Toast.LENGTH_SHORT).show();
                    setButtons();
                }
            }
        });
        clue.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.v(LOG_V, "Clue_Buttons clicked. Show the solution.");
                    Toast.makeText(getApplicationContext(), "Clue Button on", Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.v(LOG_V, "Clue_Button unclicked. Hide the solution");
                    Toast.makeText(getApplicationContext(), "Clue Button off", Toast.LENGTH_SHORT).show();
                }
            }
        });
        pause.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    Log.v(LOG_V,"Pause button clicked. Pause the game.");
                    Toast.makeText(getApplicationContext(), "Game Paused", Toast.LENGTH_SHORT).show();
                    gameStop = true;
                }
                else{
                    Log.v(LOG_V, "Start button clicked. Start the game.");
                    Toast.makeText(getApplicationContext(), "Game Start", Toast.LENGTH_SHORT).show();
                    gameStop = false;
                }
            }
        });
    }

    /*
    Enable user to increment or decrement the size of the toggle map.
     */
    public void sizeUp(View view){
        Log.v(LOG_V, "Increment map size.");
        Toast.makeText(getApplicationContext(), "Map Inre", Toast.LENGTH_SHORT).show();
    }

    /*
    Enable user to increment or decrement the size of the toggle map.
     */
    public void sizeDown(View view){
        Log.v(LOG_V, "Decrement map size.");

        Toast.makeText(getApplicationContext(), "Map Decre", Toast.LENGTH_SHORT).show();
    }
}
