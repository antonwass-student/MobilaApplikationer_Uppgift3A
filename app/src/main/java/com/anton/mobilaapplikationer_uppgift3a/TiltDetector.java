package com.anton.mobilaapplikationer_uppgift3a;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;

/**
 * Created by Anton on 2016-12-04.
 */

public class TiltDetector {

    private MainActivity activity;
    private TextView output;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private float last_x, last_y, last_z;
    private long lastUpdate;
    private float speed;
    private long timeShaking = 0;

    private float SHAKE_THESHOLD = 40f;
    private float SPEED_FILTER = 0.1f;
    private float NOISE_FILTER = 0.6f;

    private int tilt = 0;

    public TiltDetector(MainActivity activity, TextView output){
        this.output = output;
        this.activity = activity;
        sensorManager =
                (SensorManager) activity.getSystemService(activity.getApplicationContext().SENSOR_SERVICE);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if(accelerometer != null){
            //we got an accelerometer
            sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }else{
            //we got no accelerometer
        }
    }

    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {


            float x = last_x * NOISE_FILTER  + (1.0f - NOISE_FILTER) * event.values[0];
            float y = last_y * NOISE_FILTER  + (1.0f - NOISE_FILTER) * event.values[1];
            float z = last_z * NOISE_FILTER  + (1.0f - NOISE_FILTER) * event.values[2];


            long currentTime = System.currentTimeMillis();

            if(currentTime - lastUpdate > 100){
                long delta = (currentTime - lastUpdate);
                lastUpdate = currentTime;

                float newSpeed = Math.abs(x + y + z - last_x - last_y - last_z)/delta * 10000;
                speed = SPEED_FILTER * speed + (1.0f- SPEED_FILTER) * newSpeed;

                if(speed > SHAKE_THESHOLD){
                    timeShaking+=delta;
                }else{
                    timeShaking = 0;
                }

                tilt = (int)Math.toDegrees(Math.atan(z/y));

                last_x = x;
                last_y = y;
                last_z = z;



                updateUI();
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private void updateUI(){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(timeShaking > 1000f)
                    output.setTextColor(Color.RED);
                else
                    output.setTextColor(Color.BLACK);

                /*
                output.setText("Speed:\n" + String.format("%.2f", speed));
                output.append("\nX:" + String.format("%.2f", last_x));
                output.append("\nY:" + String.format("%.2f", last_z));
                output.append("\nZ:" + String.format("%.2f", last_y));
                */
                output.setText("Speed:" + String.format("%.2f\n", speed));
                output.append(tilt +"°");
            }
        });
    }


    public void pause(){
        sensorManager.unregisterListener(sensorEventListener);
    }

    public void resume(){
        //sensorManager.registerListener(sensorEventListener, accelerometer, 5);
    }
}
