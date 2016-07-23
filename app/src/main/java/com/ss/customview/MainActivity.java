package com.ss.customview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.lpphan.rangeseekbar.RangeSeekBar;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Mainactivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        RangeSeekBar seekBar = (RangeSeekBar) findViewById(R.id.seekBar);
        seekBar.setTickCount(20);
//        seekBar.setThumbColor(Color.RED);
//        seekBar.setThumbNormalRadius(12);
//        seekBar.setThumbPressedRadius(16);
        seekBar.setLeftIndex(0);
        seekBar.setRightIndex(10);
        //set a listener when the index is changed.
        seekBar.setOnRangeBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangerListener() {
            @Override
            public void onIndexChange(RangeSeekBar rangeBar, int leftIndex, int rightIndex) {
                Log.d(TAG, "leftIndex: " + leftIndex);
                Log.d(TAG, "rightIndex: " + rightIndex);
            }
        });


    }

}
