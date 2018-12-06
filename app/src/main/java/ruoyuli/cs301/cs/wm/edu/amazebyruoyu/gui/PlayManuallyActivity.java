package ruoyuli.cs301.cs.wm.edu.amazebyruoyu.gui;

import android.content.Intent;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import ruoyuli.cs301.cs.wm.edu.amazebyruoyu.R;
import ruoyuli.cs301.cs.wm.edu.amazebyruoyu.generation.*;
import ruoyuli.cs301.cs.wm.edu.amazebyruoyu.generation.Constants.UserInput;

/**
 * Play Manually Activity is the implementation java file for manually_activity xml file.
 * It takes care of the functionality of all elements in the play manually screen
 *
 * @author ruoyuli
 */

public class PlayManuallyActivity extends AppCompatActivity {
    //Basic variables
    //Button go2win;
    ImageButton up;
    ImageButton left;
    ImageButton right;
    ToggleButton wall;
    ToggleButton map;
    ToggleButton clue;
    ImageButton size_up;
    ImageButton size_down;
    TextView compass;
    ImageView compassView;
    Vibrator vibrator;
    private ImageButton voiceB;
    private int shortestPath;
    private int userPath = 0;
    private String LOG_V = "PlayManuallyActivity";
    private String comp = "E";
    private MazeConfiguration mazeConfiguration;
    private MazePanel mazePanel;
    private StatePlaying statePlaying;
    private Robot robot;
    private RobotDriver driver;
    MediaPlayer mediaPlayer = new MediaPlayer();

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
        mediaPlayer = MediaPlayer.create(this, R.raw.playing_manually);
        mediaPlayer.setVolume(1.5f,1.5f);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        if (!DataHolder.voice) {
            voiceB.setImageResource(R.drawable.voiceoff);
            mediaPlayer.pause();
        }
        else {
            voiceB.setImageResource(R.drawable.voiceon);
        }
        mazeConfiguration = DataHolder.mazeConfiguration;
        robot = new BasicRobot();
        driver = new ManualDriver();
        statePlaying = new StatePlaying();
        statePlaying.setActivity(this);
        robot.setMaze(statePlaying);
        driver.setRobot(robot);
        statePlaying.setRobotAndDriver(robot, driver);
        statePlaying.setMazeConfiguration(mazeConfiguration);
        shortestPath = mazeConfiguration.getDistanceToExit(mazeConfiguration.getStartingPosition()[0],
                mazeConfiguration.getStartingPosition()[1]);
        statePlaying.start(mazePanel);
    }

    /*
    Set up basic variables(Gui elements), connecting them with the responding parts in the responding
    xml file.
     */
    private void setUpVariables() {
        up = (ImageButton) findViewById(R.id.upbutton);
        left = (ImageButton) findViewById(R.id.leftbutton);
        right = (ImageButton) findViewById(R.id.rightbutton);
        wall = (ToggleButton) findViewById(R.id.wallbuttonM);
        map = (ToggleButton) findViewById(R.id.mapbuttonM);
        clue = (ToggleButton) findViewById(R.id.cluebuttonM);
        size_up = (ImageButton) findViewById(R.id.size_up_map);
        size_down = (ImageButton) findViewById(R.id.size_down_map);
        mazePanel = (MazePanel) findViewById(R.id.mazePanel);
        compass = (TextView) findViewById(R.id.compass);
        compassView = (ImageView) findViewById(R.id.imageView);
        voiceB = (ImageButton) findViewById(R.id.voiceMan);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    }

    private void disableButtons() {
        up.setEnabled(false);
        left.setEnabled(false);
        right.setEnabled(false);
        wall.setEnabled(false);
        map.setEnabled(false);
        clue.setEnabled(false);
        size_up.setEnabled(false);
        size_down.setEnabled(false);
    }

    /*
    Enable user to go to winning screen by clicking go2Winning button.
     */
    public void toWin() {
        Log.v(LOG_V, "User wins the game, go to the winning screen.");
        vibrator.vibrate(VibrationEffect.createOneShot(50,3));
        disableButtons();
        mediaPlayer.stop();
        //Toast.makeText(getApplicationContext(), "To Win Screen", Toast.LENGTH_SHORT).show();
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
    public void setButtons() {
        size_up.setVisibility(View.INVISIBLE);
        size_down.setVisibility(View.INVISIBLE);
    }

    public void increasePath() {
        userPath++;
    }

    /*
    Enable user to move the robot by clicking responding direction button.
     */
    public void moveUp(View view) {
        Log.v(LOG_V, "MOVE_UP button clicked. User moves forward one step.");
        ((ManualDriver) driver).move();
        //Toast.makeText(getApplicationContext(), "Up", Toast.LENGTH_SHORT).show();
        userPath = robot.getOdometerReading();
    }

    /*
    Enable user to move the robot by clicking responding direction button.
     */
    public void moveRight(View view) {
        Log.v(LOG_V, "MOVE_RIGHT button clicked. User turns right.");
        ((ManualDriver) driver).rotate(Robot.Turn.RIGHT);
        //Toast.makeText(getApplicationContext(), "Right", Toast.LENGTH_SHORT).show();
        if (comp.equalsIgnoreCase("E")) {
            comp = "S";
        }
        else if (comp.equalsIgnoreCase("S")) {
            comp = "W";
        }
        else if (comp.equalsIgnoreCase("W")) {
            comp = "N";
        }
        else {
            comp = "E";
        }
        compass.setText(comp);
    }

    /*
    Enable user to move the robot by clicking responding direction button.
     */
    public void moveLeft(View view) {
        Log.v(LOG_V, "MOVE_LEFT button clicked. User turns left.");
        ((ManualDriver) driver).rotate(Robot.Turn.LEFT);
        //Toast.makeText(getApplicationContext(), "Left", Toast.LENGTH_SHORT).show();
        if (comp.equalsIgnoreCase("E")) {
            comp = "N";
        }
        else if (comp.equalsIgnoreCase("S")) {
            comp = "E";
        }
        else if (comp.equalsIgnoreCase("W")) {
            comp = "S";
        }
        else {
            comp = "W";
        }
        compass.setText(comp);
    }

    /*
    Enable user to increment or decrement the size of the toggle map.
     */
    public void sizeUp(View view) {
        Log.v(LOG_V, "Increment map size.");
        statePlaying.keyDown(UserInput.ZoomIn, 0, true);
        //Toast.makeText(getApplicationContext(), "Map Incre", Toast.LENGTH_SHORT).show();
    }

    /*
    Enable user to increment or decrement the size of the toggle map.
     */
    public void sizeDown(View view) {
        Log.v(LOG_V, "Decrement map size.");
        statePlaying.keyDown(UserInput.ZoomOut, 0, true);
        //Toast.makeText(getApplicationContext(), "Map Decre", Toast.LENGTH_SHORT).show();
    }

    /*
    This method enables user to get back to the menu screen by clicking the "back" button.
     */
    public void backButtonClicked(View view) {
        Log.v(LOG_V, "Go back to title screen.");
        //Toast.makeText(getApplicationContext(), "Back to Menu", Toast.LENGTH_SHORT).show();
        mediaPlayer.stop();
        Intent intent = new Intent(this, AMazeActivity.class);
        startActivity(intent);
        finish();
    }

    /*
    Set up the functionality of all the toggle button. Specifically, what will happen if the toggle
    is checked and what will happen if the toggle is unchecked.
     */
    public void setToggleButtons() {
        wall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.v(LOG_V, "Wall_Button clicked. Show the currently visible walls");
                    statePlaying.keyDown(UserInput.ToggleLocalMap, 0, true);
                    //Toast.makeText(getApplicationContext(), "Wall Button on", Toast.LENGTH_SHORT).show();
                } else {
                    Log.v(LOG_V, "Wall_Button unclicked. Hide the currently visible walls");
                    statePlaying.keyDown(UserInput.ToggleLocalMap, 0, true);
                    //Toast.makeText(getApplicationContext(), "Wall Button off", Toast.LENGTH_SHORT).show();
                }
            }
        });
        map.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.v(LOG_V, "Map_Button clicked. Show the map of the maze and show the size buttons");
                    statePlaying.keyDown(UserInput.ToggleLocalMap, 0, true);
                    statePlaying.keyDown(UserInput.ToggleFullMap, 0, true);
                    //Toast.makeText(getApplicationContext(), "Size Button Shown", Toast.LENGTH_SHORT).show();
                    size_up.setVisibility(View.VISIBLE);
                    size_down.setVisibility(View.VISIBLE);
                } else {
                    Log.v(LOG_V, "Map_Button unclicked. Hide the map of the maze and the size buttons");
                    statePlaying.keyDown(UserInput.ToggleFullMap, 0, true);
                    statePlaying.keyDown(UserInput.ToggleLocalMap, 0, true);
                    //Toast.makeText(getApplicationContext(), "Size Button hiden", Toast.LENGTH_SHORT).show();
                    setButtons();
                }
            }
        });
        clue.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.v(LOG_V, "Clue_Buttons clicked. Show the solution.");
                    statePlaying.keyDown(UserInput.ToggleSolution, 0, true);
                    //Toast.makeText(getApplicationContext(), "Clue Button on", Toast.LENGTH_SHORT).show();
                } else {
                    Log.v(LOG_V, "Clue_Button unclicked. Hide the solution");
                    statePlaying.keyDown(UserInput.ToggleSolution, 0, true);
                    //Toast.makeText(getApplicationContext(), "Clue Button off", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /*
    Resume the music when the app is visible.
     */
    @Override
    public void onResume() {
        if (DataHolder.voice && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
        super.onResume();
    }

    /*
    Stop the music when app is invisible.
     */
    @Override
    public void onPause() {
        super.onPause();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    public void voiceButton(View view) {
        if (DataHolder.voice) {
            DataHolder.voice = false;
            if (mediaPlayer.isPlaying()){
                mediaPlayer.pause();
                voiceB.setImageResource(R.drawable.voiceoff);
            }
        }
        else {
            DataHolder.voice = true;
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                voiceB.setImageResource(R.drawable.voiceon);
            }
        }
    }

}
