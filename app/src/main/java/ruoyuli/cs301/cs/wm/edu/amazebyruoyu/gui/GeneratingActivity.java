package ruoyuli.cs301.cs.wm.edu.amazebyruoyu.gui;

import android.media.AsyncPlayer;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.os.Handler;
import android.media.MediaPlayer;
import android.content.Intent;
import android.widget.Toast;

import ruoyuli.cs301.cs.wm.edu.amazebyruoyu.R;

public class GeneratingActivity extends AppCompatActivity {
    // Basic variables
    private TextView catch_me;
    private TextView game_rule;
    private TextView murder;
    private TextView loading;
    private ProgressBar progressBar;
    private boolean newMaze;
    private int skillLevel;
    private String generateAlgorithm;
    private String driverAlgorithm;
    private Handler handler;
    private MediaPlayer mediaPlayer;
    private String LOG_V = "Generating Activity: ";
    private int progress = 0;
    private MyTask task = new MyTask();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generating_activity);
        mediaPlayer = MediaPlayer.create(this, R.raw.loading_music);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        task.resetTask();
        Intent previousIntent = getIntent();
        newMaze = previousIntent.getBooleanExtra("newMaze", true);
        generateAlgorithm = previousIntent.getStringExtra("generationAlgorithm");
        driverAlgorithm = previousIntent.getStringExtra("driverAlgorithm");
        if (newMaze) {
            skillLevel = previousIntent.getIntExtra("skillLevel", 0);
            setUpVariables();
            System.out.println(driverAlgorithm);
            task.execute();

        } else {
            skillLevel = previousIntent.getIntExtra("skillLevel", 0);
            setUpVariables();
            task.execute();
        }
    }

    private void setUpVariables() {
        catch_me = (TextView) findViewById(R.id.catch_me);
        game_rule = (TextView) findViewById(R.id.game_rule);
        murder = (TextView) findViewById(R.id.murder);
        loading = (TextView) findViewById(R.id.loading);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    public void newMaze(){
        Log.v(LOG_V, "Generating new maze (skill level: "+Integer.toString(skillLevel)+"; generating algorithm: "
        + generateAlgorithm + "; driver algorith: " + driverAlgorithm);

    }

    class MyTask extends AsyncTask<Void, Integer, Void> {
            @Override
            public Void doInBackground(Void... params) {
                while (progress < 100) {
                    try {
                        Thread.sleep(100);
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
                loading.setText("Loading: " + progress[0] + "%");

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
                Toast.makeText(getApplicationContext(), "Maze ready", Toast.LENGTH_SHORT).show();
                System.out.println(driverAlgorithm);
                switchToPlay();
            }

            public void resetTask(){
                task.cancel(true);
                task = new MyTask();
            }

        }


    public void backButtonClicked(View view) {
        Log.v(LOG_V, "Go back to title screen.");
        mediaPlayer.stop();
        task.resetTask();
        Toast.makeText(getApplicationContext(), "Back to Menu", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, AMazeActivity.class);
        startActivity(intent);
        finish();
    }

    public void switchToPlay(){
        if (driverAlgorithm.equals("Manual")) {
            Log.v(LOG_V, "Manual Driver implemented. User will control the robot");
            Intent i = new Intent(this, PlayManuallyActivity.class);
            mediaPlayer.stop();
            startActivity(i);
            finish();
        }
        else{
            Log.v(LOG_V,driverAlgorithm + " Driver implemented. Robot will go for exit itself");
            Intent i = new Intent(this, PlayAnimationActivity.class);
            i.putExtra("driverAlgorithm", driverAlgorithm);
            mediaPlayer.stop();
            startActivity(i);
            finish();
        }

    }

    @Override
    public void onResume() {
        mediaPlayer.start();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }
}
