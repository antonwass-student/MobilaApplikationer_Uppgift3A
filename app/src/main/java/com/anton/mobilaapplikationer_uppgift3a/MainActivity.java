package com.anton.mobilaapplikationer_uppgift3a;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    AcceleratorInterpreter acceleratorInterpreter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = (TextView) findViewById(R.id.output_text);
        acceleratorInterpreter = new AcceleratorInterpreter(this, textView);
    }


    @Override
    protected void onPause() {
        super.onPause();

        acceleratorInterpreter.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        acceleratorInterpreter.resume();
    }
}
