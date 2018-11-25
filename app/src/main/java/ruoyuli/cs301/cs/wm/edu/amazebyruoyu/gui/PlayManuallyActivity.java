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
import android.widget.Toast;
import android.widget.ToggleButton;

import ruoyuli.cs301.cs.wm.edu.amazebyruoyu.R;

/**
 * Play Manually Activity is the implementation java file for manually_activity xml file.
 * It takes care of the functionality of all elements in the play manually screen
 *
 * @author ruoyuli
 */

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

    /*
    Override the onCreate method in AppCompatActivity class. This is a method that is the main thread
    of this whole class. Everything we do or want to run is in this class.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manually_activity);
        setUpVariables();
        setToggleButtons();
        setButtons();
    }

    /*
    Set up basic variables(Gui elements), connecting them with the responding parts in the responding
    xml file.
     */
    private void setUpVariables(){
        up = (ImageButton) findViewById(R.id.upbutton);
        left = (ImageButton) findViewById(R.id.leftbutton);
        right = (ImageButton) findViewById(R.id.rightbutton);
        wall = (ToggleButton) findViewById(R.id.wallbuttonM);
        map = (ToggleButton) findViewById(R.id.mapbuttonM);
        clue = (ToggleButton) findViewById(R.id.cluebuttonM);
        size_up = (ImageButton) findViewById(R.id.size_up_map);
        size_down = (ImageButton) findViewById(R.id.size_down_map);
    }

    /*
    Enable user to go to winning screen by clicking go2Winning button.
     */
    public void toWin(View view){
        Log.v(LOG_V, "User wins the game, go to the winning screen.");
        Toast.makeText(getApplicationContext(), "To Win Screen", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, WinningActivity.class);
        intent.putExtra("drvalgo", "Manual");
        intent.putExtra("shortestPath", shortestPath);
        intent.putExtra("userPath", userPath);
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
    Enable user to move the robot by clicking responding direction button.
     */
    public void moveUp(View view){
        Log.v(LOG_V, "MOVE_UP button clicked. User moves forward one step.");
        Toast.makeText(getApplicationContext(), "Up", Toast.LENGTH_SHORT).show();
        userPath++;
    }

    /*
    Enable user to move the robot by clicking responding direction button.
     */
    public void moveRight(View view){
        Log.v(LOG_V, "MOVE_RIGHT button clicked. User turns right.");
        Toast.makeText(getApplicationContext(), "Right", Toast.LENGTH_SHORT).show();
    }

    /*
    Enable user to move the robot by clicking responding direction button.
     */
    public void moveLeft(View view){
        Log.v(LOG_V, "MOVE_LEFT button clicked. User turns left.");
        Toast.makeText(getApplicationContext(), "Left", Toast.LENGTH_SHORT).show();
    }

    /*
    Enable user to increment or decrement the size of the toggle map.
     */
    public void sizeUp(View view){
        Log.v(LOG_V, "Increment map size.");
        Toast.makeText(getApplicationContext(), "Map Incre", Toast.LENGTH_SHORT).show();
    }

    /*
    Enable user to increment or decrement the size of the toggle map.
     */
    public void sizeDown(View view){
        Log.v(LOG_V, "Decrement map size.");
        Toast.makeText(getApplicationContext(), "Map Decre", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getApplicationContext(), "Size Button hiden", Toast.LENGTH_SHORT).show();
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
    }

}
