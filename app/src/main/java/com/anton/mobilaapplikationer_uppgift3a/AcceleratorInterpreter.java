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

public class AcceleratorInterpreter {

    private MainActivity activity;
    private TextView output;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private float last_x, last_y, last_z;
    private long lastUpdate;
    private float accel;
    private long timeShaking = 0;
    private int shakeCount = 0;

    private float SHAKE_THESHOLD = 5f;
    private float ACCEL_FILTER = 0.8f;
    private float NOISE_FILTER = 0.4f;

    private float[] gravity = {0f,0f,0f};
    private float[] linearAccel = {0f,0f,0f};

    private int tilt = 0;

    public AcceleratorInterpreter(MainActivity activity, TextView output){
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

            calculateTilt(event);

            detectShake(event);

            updateUI();
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    /**
     * Low pass filter to
     * @param event
     */
    private void calculateTilt(SensorEvent event){
        float x = last_x * NOISE_FILTER  + (1.0f - NOISE_FILTER) * event.values[0];
        float y = last_y * NOISE_FILTER  + (1.0f - NOISE_FILTER) * event.values[1];
        float z = last_z * NOISE_FILTER  + (1.0f - NOISE_FILTER) * event.values[2];

        tilt = (int)Math.toDegrees(Math.atan(z/y));

        last_x = x;
        last_y = y;
        last_z = z;
    }

    /**
     * NOTE: The shake count is needed to take direction changes into consideration.
     * @param event
     */
    private void detectShake(SensorEvent event){
        float delta = System.currentTimeMillis() - lastUpdate;
        lastUpdate = System.currentTimeMillis();

        gravity[0] = ACCEL_FILTER * gravity[0] + (1f- ACCEL_FILTER) * event.values[0];
        gravity[1] = ACCEL_FILTER * gravity[1] + (1f- ACCEL_FILTER) * event.values[1];
        gravity[2] = ACCEL_FILTER * gravity[2] + (1f- ACCEL_FILTER) * event.values[2];

        linearAccel[0] = event.values[0] - gravity[0];
        linearAccel[1] = event.values[1] - gravity[1];
        linearAccel[2] = event.values[2] - gravity[2];

        accel = (float) Math.sqrt(
                linearAccel[0] * linearAccel[0]
                        + linearAccel[1] *linearAccel[1]
                        + linearAccel[2] * linearAccel[2]
        );

        if(accel > SHAKE_THESHOLD){
            //the phone is accelerating faster than set threshold.
            timeShaking+=delta;
            shakeCount++;
        }else if(shakeCount == 0){
            //we've had 2 signal changes where the phone is not shaking.
            timeShaking = 0;
        }else{
            //we've had 1 signal change where phone is not shaking.
            //could be a turn.
            shakeCount = 0;
        }
    }

    private void updateUI(){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(timeShaking > 1000f)
                    output.setTextColor(Color.RED);
                else
                    output.setTextColor(Color.BLACK);

                output.setText(tilt +"°");
            }
        });
    }


    public void pause(){
        sensorManager.unregisterListener(sensorEventListener);
    }

    public void resume(){
        sensorManager.registerListener(sensorEventListener, accelerometer, 5);
    }
}
