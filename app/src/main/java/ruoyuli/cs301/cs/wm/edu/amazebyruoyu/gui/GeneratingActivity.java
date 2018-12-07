package ruoyuli.cs301.cs.wm.edu.amazebyruoyu.gui;

import android.media.AsyncPlayer;
import android.os.AsyncTask;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.os.Handler;
import android.media.MediaPlayer;
import android.content.Intent;
import android.widget.Toast;

import java.io.File;

import ruoyuli.cs301.cs.wm.edu.amazebyruoyu.R;
import ruoyuli.cs301.cs.wm.edu.amazebyruoyu.generation.*;

/**
 * Generating Activity is the implementation java file for generating_activity xml file.
 * It takes care of the functionality of all elements in the generating screen
 *
 * Bonus: This activity contains 1. a overall music controller which can turns on/off music.
 * 2. A vibration that will happens when goes to the next activity.
 * 3. A mediaplayer that plays the background music
 * 4. A background picture.
 *
 * @author ruoyuli
 */

public class GeneratingActivity extends AppCompatActivity implements Order{
    // Basic variables
    private TextView catch_me;
    private TextView game_rule;
    private TextView murder;
    private TextView loading;
    private ProgressBar progressBar;
    private ImageButton voiceB;
    private boolean newMaze;
    private int skillLevel;
    private String generateAlgorithm;
    private String driverAlgorithm;
    private MediaPlayer mediaPlayer;
    private String LOG_V = "Generating Activity: ";
    private int progress = 0;
    private MyTask task = new MyTask();
    private Builder builder; // selected maze generation algorithm
    private boolean perfect = false;
    private Handler handler = new Handler();
    private MazeFactory mazeFactory = new MazeFactory();
    Vibrator vibrator;

    /*
    Override the onCreate method in AppCompatActivity class. This is a method that is the main thread
    of this whole class. Everything we do or want to run is in this class.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generating_activity);
        mediaPlayer = MediaPlayer.create(this, R.raw.loading_music);
        mediaPlayer.setVolume(2.0f,2.0f);
        mediaPlayer.setLooping(true);
        //task.resetTask();
        Intent previousIntent = getIntent();
        newMaze = previousIntent.getBooleanExtra("newMaze", true);
        generateAlgorithm = previousIntent.getStringExtra("generateAlgorithm");
        driverAlgorithm = previousIntent.getStringExtra("driverAlgorithm");
        skillLevel = previousIntent.getIntExtra("skillLevel", 0);
        DataHolder.skillLevel = this.skillLevel;
        setUpVariables();
        mediaPlayer.start();
        if (!DataHolder.voice) {
            voiceB.setImageResource(R.drawable.voiceoff);
            mediaPlayer.pause();
        }
        else {
            voiceB.setImageResource(R.drawable.voiceon);
        }
        if (newMaze) {
            setBuilder();
            buildNew();
            //task.execute();

        } else {
            loadMaze();
            //task.execute();
        }
    }

    /*
    Set up basic variables(Gui elements), connecting them with the responding parts in the responding
    xml file.
     */
    private void setUpVariables() {
        catch_me = (TextView) findViewById(R.id.catch_me);
        game_rule = (TextView) findViewById(R.id.game_rule);
        murder = (TextView) findViewById(R.id.murder);
        loading = (TextView) findViewById(R.id.loading);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        voiceB = (ImageButton) findViewById(R.id.voiceGen);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    }

    /*
    Log info when the newMaze generating is done.
     */
    public void newMaze(){
        Log.v(LOG_V, "Generating new maze (skill level: "+Integer.toString(skillLevel)+"; generating algorithm: "
        + generateAlgorithm + "; driver algorith: " + driverAlgorithm);
    }

    @Override
    public int getSkillLevel() {
        return skillLevel;
    }

    @Override
    public Builder getBuilder() {
        return builder;
    }

    @Override
    public boolean isPerfect() {
        return false;
    }

    public void setBuilder() {
        if (generateAlgorithm.equalsIgnoreCase("DFS"))
            builder = Builder.DFS;
        else if (generateAlgorithm.equalsIgnoreCase("Prim"))
            builder = Builder.Prim;
        else if (generateAlgorithm.equalsIgnoreCase("Eller"))
            builder = Builder.Eller;
        DataHolder.builder = this.builder;
    }

    @Override
    public void deliver(MazeConfiguration mazeConfig) {
        DataHolder.mazeConfiguration = mazeConfig;
        if (skillLevel <= 3) {
            String filename = "maze" + Integer.toString(skillLevel);
            writeMazeFile(filename, mazeConfig);
        }
        switchToPlay();
    }

    public void writeMazeFile(String string, MazeConfiguration mazeConfig) {
        File file = new File(getApplicationContext().getFilesDir(), string);
        MazeFileWriter mazeFileWriter = new MazeFileWriter();
        MazeFileWriter.store(string, mazeConfig.getWidth(), mazeConfig.getHeight(),
                0 ,0,
                mazeConfig.getRootnode(),
                mazeConfig.getMazecells(),
                mazeConfig.getMazedists().getAllDistanceValues(),
                mazeConfig.getStartingPosition()[0],
                mazeConfig.getStartingPosition()[1],
                DataHolder.context);
    }

    public void loadMaze() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String filename = "maze" + Integer.toString(skillLevel);
                MazeFileReader reader = new MazeFileReader(filename, DataHolder.context);
                if (reader.fileExist) {
                    DataHolder.mazeConfiguration = reader.getMazeConfiguration();
                    task.execute();
                }
                else {
                    loading.setText("No maze at this level stored, Click back button.");
                    game_rule.setText("No maze at this level stored, Click back button. Can only store maze at level 0-3");
                    return;
                }
            }
        }).start();
    }

    @Override
    public void updateProgress(int percentage) {
        if (this.progress < percentage && percentage <= 100) {
            this.progress = percentage;
            this.handler.post(new Runnable() {
                @Override
                public void run() {
                    progressBar.setProgress(progress);
                    loading.setText("Loading: " + progress + "%");
                    Log.v(LOG_V, "Progress: " + progress);
                }
            });
        }
    }

    public void buildNew() {
        mazeFactory.order(this);
    }

    /*
    Create a background thread by AsyncTask to mimic the generating process
     */

    class MyTask extends AsyncTask<Void, Integer, Void> {
            @Override
            public Void doInBackground(Void... params) {
                while (progress < 100) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.v(LOG_V, "Progress: " + progress);
                    publishProgress(progress);
                    progress++;
                    if (isCancelled())
                        break;
                }
                return null;
            }

            @Override
            public void onProgressUpdate(Integer... progress) {
                super.onProgressUpdate(progress);
                progressBar.setProgress(progress[0]);
                loading.setText("Loading old maze: " + progress[0] + "%");

            }

            @Override
            public void onPreExecute() {
                progress = 0;
                progressBar.setProgress(0);
            }

            @Override
            public void onPostExecute(Void result) {
                super.onPostExecute(result);
                progressBar.setProgress(100);
                loading.setText("Murder Happens!");
                newMaze();
                Toast.makeText(getApplicationContext(), "Maze ready", Toast.LENGTH_SHORT).show();
                System.out.println(driverAlgorithm);
                switchToPlay();
            }

            public void resetTask(){
                task.cancel(true);
                task = new MyTask();
            }

        }

    /*
    This method enables user to get back to the menu screen by clicking the "back" button.
     */
    public void backButtonClicked(View view) {
        Log.v(LOG_V, "Go back to title screen.");
        mediaPlayer.stop();
        //task.resetTask();
        //Toast.makeText(getApplicationContext(), "Back to Menu", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, AMazeActivity.class);
        startActivity(intent);
        finish();
    }

    /*
    Enable the app automatically goes to play screen when the generating process reaches 100%
     */
    public void switchToPlay(){
        if (driverAlgorithm.equals("Manual")) {
            Log.v(LOG_V, "Manual Driver implemented. User will control the robot");
            Intent i = new Intent(this, PlayManuallyActivity.class);
            vibrator.vibrate(VibrationEffect.createOneShot(50,3));
            mediaPlayer.stop();
            startActivity(i);
            finish();
        }
        else{
            Log.v(LOG_V,driverAlgorithm + " Driver implemented. Robot will go for exit itself");
            Intent i = new Intent(this, PlayAnimationActivity.class);
            i.putExtra("driverAlgorithm", driverAlgorithm);
            vibrator.vibrate(VibrationEffect.createOneShot(50,3));
            mediaPlayer.stop();
            startActivity(i);
            finish();
        }

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
                Log.v(LOG_V, "Music off");
                mediaPlayer.pause();
                voiceB.setImageResource(R.drawable.voiceoff);
            }
        }
        else {
            DataHolder.voice = true;
            if (!mediaPlayer.isPlaying()) {
                Log.v(LOG_V, "Music on");
                mediaPlayer.start();
                voiceB.setImageResource(R.drawable.voiceon);
            }
        }
    }
}
