package com.demo.candy.wavedemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button mBtnStart;
    private WaveHelper mWaveHelper;
    private WaveView mWaveView;
    private Button mBtnStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnStart = (Button)findViewById(R.id.btn_start);
        mWaveView = (WaveView)findViewById(R.id.wave);
        mBtnStop = (Button)findViewById(R.id.btn_stop);
        mWaveHelper = new WaveHelper(mWaveView);

        mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWaveHelper.start();
            }
        });

        mBtnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWaveHelper.stop();
            }
        });
    }
}
