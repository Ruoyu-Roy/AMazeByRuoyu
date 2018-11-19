package ruoyuli.cs301.cs.wm.edu.amazebyruoyu.gui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.media.MediaPlayer;
import android.widget.Toast;


import ruoyuli.cs301.cs.wm.edu.amazebyruoyu.R;

public class WinningActivity extends AppCompatActivity {
    TextView win0;
    TextView win1;
    TextView win2;
    TextView win3;
    TextView win4;
    TextView win5;
    Button back;
    MediaPlayer mediaPlayer = new MediaPlayer();

    private String drvalgo;
    private int shortestPath;
    private int userPath;
    private int energyConsump;
    private String LOG_V = "WinningActivity: ";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.winning_activity);
        setUpVariables();
        mediaPlayer = MediaPlayer.create(this, R.raw.winning_music);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        Intent preIntent = getIntent();
        drvalgo = preIntent.getStringExtra("drvalgo");
        shortestPath = preIntent.getIntExtra("shortestPath", 30);
        if (drvalgo.equals("Manual")){
            userPath = preIntent.getIntExtra("userPath", 0);
            win4.setVisibility(View.GONE);
        }
        else{
            userPath = preIntent.getIntExtra("robotPath", 0);
            energyConsump = preIntent.getIntExtra("energy_consumption",0);
        }
        setTextView();
    }

    private void setUpVariables(){
        win0 = (TextView) findViewById(R.id.win0);
        win1 = (TextView) findViewById(R.id.win1);
        win2 = (TextView) findViewById(R.id.win2);
        win3 = (TextView) findViewById(R.id.win3);
        win4 = (TextView) findViewById(R.id.win4);
        win5 = (TextView) findViewById(R.id.win5);
        back = (Button) findViewById(R.id.backw);
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
        win2.setText("The Possible Shortest Path: " + Integer.toString(shortestPath));
        win3.setText("You use " + Integer.toString(userPath) + " steps to win");
        win4.setText("Energy Consumption: " + Integer.toString(energyConsump));
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
