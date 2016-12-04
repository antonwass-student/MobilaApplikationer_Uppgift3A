package com.anton.mobilaapplikationer_uppgift3a;

import android.hardware.Sensor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    TiltDetector tiltDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = (TextView) findViewById(R.id.output_text);
        tiltDetector = new TiltDetector(this, textView);
    }


    @Override
    protected void onPause() {
        super.onPause();

        tiltDetector.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        tiltDetector.resume();
    }
}
