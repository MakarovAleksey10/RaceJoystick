package com.example.joystickserver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private double angle = 0.0;
    private int tik = 40;
    private boolean stop = false, run = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startServer();
    }

    private void startServer() {
        setButtons();
        getSensorInfo();
        new Thread() {
            @Override
            public void run() {
                try {
                    ServerSocket server = new ServerSocket(8080);
                    print("Server Started...");
                    Socket sock = server.accept();
                    print("Accepted...");
                    BufferedReader br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
                    print(br.readLine());
                    String text = "<button>Start</button>\r\n";
                    bw.write("HTTP/1.1 200 OK\r\nContent-Type: text/html\r\n\r\n" + text);
                    bw.flush();
                    while (true) {
                        bw.write(angle + " " + (stop ? 1 : 2) + " " + (run ? 1 : 2) + "\r\n");
                        bw.flush();
                        Thread.sleep(tik);
                    }
                } catch(Exception e) {
                    print("Server error: " + e.getMessage());
                }
            }
        }.start();
    }

    private void print(String t) {
        TextView tv = findViewById(R.id.textView);
        String text = (String) tv.getText();
        //tv.setText(text + t + "\n");
        tv.setText(t);
    }

    private void getSensorInfo() {
        try {
            SensorManager manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            Sensor sensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            manager.registerListener(new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent sensorEvent) {
                    angle = sensorEvent.values[1];
                }
                @Override
                public void onAccuracyChanged(Sensor sensor, int i) {
                }
            }, sensor, 1);
        } catch (Exception e) {
            print("Sensor error: " + e.getMessage());
        }
    }

    private void setButtons() {
        findViewById(R.id.button).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    stop = true;
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    stop = false;
                }
                return false;
            }
        });
        findViewById(R.id.button2).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    run = true;
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    run = false;
                }
                return false;
            }
        });
    }
}
