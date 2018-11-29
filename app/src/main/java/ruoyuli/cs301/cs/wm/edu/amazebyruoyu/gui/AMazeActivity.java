package ruoyuli.cs301.cs.wm.edu.amazebyruoyu.gui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;
import android.view.View;
import android.content.Intent;
import android.media.MediaPlayer;
//import android.widget.Toast;

import java.io.File;

import java.util.*;

import ruoyuli.cs301.cs.wm.edu.amazebyruoyu.R;
import ruoyuli.cs301.cs.wm.edu.amazebyruoyu.generation.*;

/**
 * A Maze Activity is the implementation java file for maze_activity xml file.
 * It takes care of the functionality of all elements in the menu screen
 *
 * @author ruoyuli
 */

public class AMazeActivity extends AppCompatActivity {
    //Basic Variables
    private SeekBar seekBar;
    private TextView difLevel;
    private Spinner gen_spinner;
    private Spinner driver_spinner;
    private MediaPlayer mediaPlayer;

    protected String DFS = "DFS",
            Prim = "Prim",
            Eller = "Eller",
            Manual = "Manual",
            WallF = "WallFollower",
            Wiz = "Wizard",
            Expl = "Explorer",
            Pledge = "Pledge";
    protected String LOG_V = "AMazeActivity";
    private boolean manual = false;

    /*
    Override the onCreate method in AppCompatActivity class. This is a method that is the main thread
    of this whole class. Everything we do or want to run is in this class.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maze_activity);
        initializeVariable();
        setOnSeekBarListener();
        spinnerSet();
        addOnSpinnerListener();
        mediaPlayer = MediaPlayer.create(this, R.raw.title_music2);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    /*
    Set up basic variables(Gui elements), connecting them with the responding parts in the responding
    xml file.
     */
    public void initializeVariable() {
        seekBar = (SeekBar) findViewById(R.id.seekBar1);
        difLevel = (TextView) findViewById(R.id.difLevel);
        gen_spinner = (Spinner) findViewById(R.id.gen_spinner);
        driver_spinner = (Spinner) findViewById(R.id.drv_spinner);
    }

    /*
    Takes care of the functionality of the seek bar of skill level.
     */
    public void setOnSeekBarListener() {
        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                difLevel.setText("Current Level:" + Integer.toString(progress) + "/15");
                Log.v(LOG_V, "Current level: " + progress);
                //Toast.makeText(getApplicationContext(), "Current level: " + progress, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    /*
    Set up the choices in each spinner.
     */
    private void spinnerSet() {
        List<String> gen_algo = new ArrayList<String>();
        gen_algo.add(DFS);
        gen_algo.add(Prim);
        gen_algo.add(Eller);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, gen_algo);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        gen_spinner.setAdapter(adapter);
        List<String> drv_algo = new ArrayList<String>();
        drv_algo.add(Manual);
        drv_algo.add(WallF);
        drv_algo.add(Wiz);
        drv_algo.add(Expl);
        drv_algo.add(Pledge);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, R.layout.spinner_item, drv_algo);
        adapter1.setDropDownViewResource(R.layout.spinner_item);
        driver_spinner.setAdapter(adapter1);
    }

    /*
    Takes care of the functionality of all spinner, like activity of the app when certain element is
    chosen.
     */
    private void addOnSpinnerListener() {
        gen_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.v(LOG_V, "Generation Algorithm Selected: " + gen_spinner.getSelectedItem().toString());
                //Toast.makeText(getApplicationContext(),"Generation Algorithm Selected: " + gen_spinner.getSelectedItem().toString()
                        //, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        driver_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.v(LOG_V, "Driver Algorithm Selected: " + driver_spinner.getSelectedItem().toString());
                //Toast.makeText(getApplicationContext(),
                  //      "Driver Algorithm Selected: " + driver_spinner.getSelectedItem().toString(),
                    //    Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /*
    Generating the new maze when user clicks the "New Maze" Button.
     */
    public void generateNewMaze(View view) {
        Log.v(LOG_V, "New Maze Button Clicked");
        //Toast.makeText(getApplicationContext(),
          //      "New Maze Button Clicked",
            //    Toast.LENGTH_SHORT).show();

        int skillLevel = seekBar.getProgress();
        String genAlgorithm = gen_spinner.getSelectedItem().toString();
        String drvAlgorithm = driver_spinner.getSelectedItem().toString();
        if (drvAlgorithm == "Manual") {
            manual = true;
        }
        Intent intent = new Intent(this, GeneratingActivity.class);

        intent.putExtra("newMaze", true);
        intent.putExtra("skillLevel", skillLevel);
        intent.putExtra("generateAlgorithm", genAlgorithm);
        intent.putExtra("driverAlgorithm", drvAlgorithm);
        intent.putExtra("manual", manual);

        mediaPlayer.stop();
        startActivity(intent);
        finish();
    }

    /*
    Load the former maze when user clicks the "Load Maze" button.
     */
    public void loadOldMaze(View view) {
        int skillLevel = seekBar.getProgress();
        String genAlgorithm = gen_spinner.getSelectedItem().toString();
        String drvAlgorithm = driver_spinner.getSelectedItem().toString();
        Log.v(LOG_V, "Old Maze Button Clicked: skillLevel: "+skillLevel+" generating algorithm: "
        +genAlgorithm);
        //Toast.makeText(getApplicationContext(),
          //      "Old Maze Button Clicked",
            //    Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, GeneratingActivity.class);
        intent.putExtra("newMaze", false);
        intent.putExtra("skillLevel", skillLevel);
        intent.putExtra("generateAlgorithm", genAlgorithm);
        intent.putExtra("driverAlgorithm", drvAlgorithm);

        mediaPlayer.stop();
        startActivity(intent);
        finish();
    }

    /*
    Resume the music when the app is visible.
     */
    @Override
    public void onResume() {
        mediaPlayer.start();
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

}
