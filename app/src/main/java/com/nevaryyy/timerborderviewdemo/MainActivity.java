package com.nevaryyy.timerborderviewdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private TimerBorderView timerBorderView;
    private Button startButton;
    private Button pauseButton;
    private Button resumeButton;
    private Button cancelButton;
    private Button clearButton;
    private Button showButton;
    private Button hideButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timerBorderView = (TimerBorderView) findViewById(R.id.tbv_main);
        startButton = (Button) findViewById(R.id.btn_start);
        pauseButton = (Button) findViewById(R.id.btn_pause);
        resumeButton = (Button) findViewById(R.id.btn_resume);
        cancelButton = (Button) findViewById(R.id.btn_cancel);
        clearButton = (Button) findViewById(R.id.btn_clear);
        showButton = (Button) findViewById(R.id.btn_show);
        hideButton = (Button) findViewById(R.id.btn_hide);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timerBorderView.startCountdown(15, TimeUnit.SECONDS, new MyTimerListener());
            }
        });
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timerBorderView.pause();
            }
        });
        resumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timerBorderView.resume();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timerBorderView.cancel();
            }
        });
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timerBorderView.clear();
            }
        });
        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timerBorderView.show();
            }
        });
        hideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timerBorderView.hide();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerBorderView.clear();
    }

    private class MyTimerListener extends TimerBorderView.TimerAdapter {
        @Override
        public void onStart() {
            super.onStart();
            Log.d(TAG, "on start");
        }

        @Override
        public void onPause() {
            super.onPause();
            Log.d(TAG, "on pause");
        }

        @Override
        public void onResume() {
            super.onResume();
            Log.d(TAG, "on resume");
        }

        @Override
        public void onCancel() {
            super.onCancel();
            Log.d(TAG, "on cancel");
        }

        @Override
        public void onFinish() {
            super.onFinish();
            Log.d(TAG, "on finish");
        }

        @Override
        public void onRepeat() {
            super.onRepeat();
            Log.d(TAG, "on repeat");
        }
    }
}
