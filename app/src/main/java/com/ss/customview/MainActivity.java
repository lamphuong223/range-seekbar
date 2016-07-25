package com.ss.customview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.lpphan.rangeseekbar.RangeSeekBar;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView left = (TextView) findViewById(R.id.left);
        final TextView right = (TextView) findViewById(R.id.right);

        RangeSeekBar seekBar = (RangeSeekBar) findViewById(R.id.seekBar);
        seekBar.setOnRangeBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangerListener() {
            @Override
            public void onIndexChange(RangeSeekBar rangeBar, int leftIndex, int rightIndex) {
                Log.d(TAG, "leftIndex: " + leftIndex);
                Log.d(TAG, "rightIndex: " + rightIndex);
                right.setText(rightIndex+ "");
                left.setText(leftIndex + "");
            }
        });


    }

}
