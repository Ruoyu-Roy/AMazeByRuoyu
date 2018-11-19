package ruoyuli.cs301.cs.wm.edu.amazebyruoyu.gui;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import ruoyuli.cs301.cs.wm.edu.amazebyruoyu.R;

public class LosingActivity extends AppCompatActivity {
    TextView losing0;
    TextView losing1;
    TextView losing2;
    TextView losing3;
    TextView losing4;
    TextView losing5;
    Button back;
    MediaPlayer mediaPlayer = new MediaPlayer();

    private String drvalgo;
    private int shortestPath;
    private int userPath;
    private int energyConsump;
    private String LOG_V = "LosingActivity: ";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.losing_activity);
        setUpVariables();
        mediaPlayer = MediaPlayer.create(this, R.raw.losing_music);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        Intent preIntent = getIntent();
        drvalgo = preIntent.getStringExtra("drvalgo");
        shortestPath = preIntent.getIntExtra("shortestPath", 0);
        userPath = preIntent.getIntExtra("robotPath", 0);
        energyConsump = preIntent.getIntExtra("energy_consumption",0);
        setTextView();
    }

    private void setUpVariables(){
        losing0 = (TextView) findViewById(R.id.losing0);
        losing1 = (TextView) findViewById(R.id.losing1);
        losing2 = (TextView) findViewById(R.id.losing2);
        losing3 = (TextView) findViewById(R.id.losing3);
        losing4 = (TextView) findViewById(R.id.losing4);
        losing5 = (TextView) findViewById(R.id.losing5);
        back = (Button) findViewById(R.id.backl);
    }

    public void backButtonClicked(View view) {
        Log.v(LOG_V, "Go back to title screen.");
        Toast.makeText(getApplicationContext(), "Back to Menu", Toast.LENGTH_SHORT).show();
        mediaPlayer.stop();
        Intent intent = new Intent(this, AMazeActivity.class);
        startActivity(intent);
        finish();
    }

    private void setTextView(){
        losing2.setText("The Possible Shortest Path: " + Integer.toString(shortestPath));
        losing3.setText("Total path length: " + Integer.toString(userPath));
        losing4.setText("Energy Consumption: " + Integer.toString(energyConsump));
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
