package ruoyuli.cs301.cs.wm.edu.amazebyruoyu.gui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.*;

import ruoyuli.cs301.cs.wm.edu.amazebyruoyu.R;

/**
 *
 *
 * @author ruoyuli
 */

public class AMazeActivity extends AppCompatActivity {

    private SeekBar seekBar;
    private TextView difLevel;
    private Spinner gen_spinner;
    private Spinner driver_spinner;

    protected String DFS = "DFS",
                     Prim = "Prim's",
                     Eller = "Eller's",
                     Manual = "Manual",
                     WallF = "WallFollower",
                     Wiz = "Wizard",
                     Expl = "Explorer",
                     Pledge = "Pledge";


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maze_activity);
        initializeVariable();
        setOnSeekBarListener();
        spinnerSet();
    }

    public void initializeVariable(){
        seekBar = (SeekBar) findViewById(R.id.seekBar1);
        difLevel = (TextView) findViewById(R.id.difLevel);
        gen_spinner = (Spinner) findViewById(R.id.gen_spinner);
        driver_spinner = (Spinner) findViewById(R.id.drv_spinner);
    }

    public void setOnSeekBarListener(){
        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                difLevel.setText("Current Level:" + Integer.toString(progress) + "/15");
                Log.v("AMazeActivity", "Current level: "+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void spinnerSet(){
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
}
