package com.example.dataprepearer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.util.Random;


public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    CameraBridgeViewBase cameraBridgeViewBase;
    private SurfaceView surfaceView;
    private View dotView, lamp;
    private TextView textView, statusView;
    private Button play_pause, lock_bounds;
    private static final int PERMISSION_REQUEST_CODE = 200;
    public float x;
    public float y;
    public static Mat frame;
    public int Ymin = 800;
    public int Ymax = 2200;
    public int Xmin = 300;
    public int Xmax = 1080;
    public static int lock_status = 0;
    public FrameLayout layout;
    public boolean isPaused = true;
    public boolean isLocked = false;
    Random random = new Random();
    public String position = "";


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            dotView = findViewById(R.id.dotView);
            lamp = findViewById(R.id.lamp);
            textView = findViewById(R.id.position);
            play_pause = findViewById(R.id.button_pause_start);
            play_pause.getBackground().setAlpha(225);
            lock_bounds = findViewById(R.id.lock_bounds);
            lock_bounds.getBackground().setAlpha(225);
            statusView = findViewById(R.id.lock_stat);



            checkPermission();

            cameraBridgeViewBase = findViewById(R.id.camera_view);
            cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
            cameraBridgeViewBase.setCvCameraViewListener(this);
            layout = findViewById(R.id.layout);
            cameraBridgeViewBase.setLayoutParams(new FrameLayout.LayoutParams(1, 1));

            if (OpenCVLoader.initDebug()) {
                try {
                    cameraBridgeViewBase.enableView();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            handler.post(runnableCode);
            stathandler.post(statHandler);
            play_pausehandler.post(play_pauseHandler);
        }
            private void checkPermission () {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    // Пермишены не предоставлены, делаем запрос.
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                }  // Пермишены уже предоставлены, можно продолжать работу.

            }

            public void onRequestPermissionsResult ( int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults){
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                if (requestCode == PERMISSION_REQUEST_CODE) {
                    if (grantResults.length > 0) {
                        boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                        boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                        if (cameraAccepted && writeStorageAccepted) {
                            //moveDotAndTakePicture(frame);
                        } else {
                            //Toast.makeText(this, "Permission Denied, You cannot access camera and write storage.", Toast.LENGTH_SHORT).show();
                            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                checkPermission();
                            } else if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                checkPermission();
                            }
                        }
                    }
                }
            }
            private void moveDotAndTakePicture(Mat frame) throws InterruptedException {
                // Move the dot to a random position
                if (frame != null) {

                    if(lock_status == 0) {
                        x = random.nextFloat() * 1080;
                        y = random.nextFloat() * 2200;

                        position = "x: " + x + " y:" + y;
                        setPosition(position, dotView, textView, x, y);

                    }else if (lock_status == 1){
                        int Xlock = random.nextInt(2);
                        int Ylock = random.nextInt(2);

                        if(Xlock == 0){
                            x = random.nextFloat() + random.nextInt(150);
                        } else if (Xlock == 1) {
                            x = random.nextFloat() + random.nextInt((Xmax - Xmin) + 1) + Xmin;
                        }

                        if(Ylock == 0){
                            y = random.nextFloat() + random.nextInt(220);
                        } else if (Ylock == 1) {
                            y = random.nextFloat() + random.nextInt((Ymax - Ymin) + 1) + Ymin;
                        }
                        position = "x: " + x + " y:" + y;
                        setPosition(position, dotView, textView, x, y);
                    }else if(lock_status == 2){
                        x = random.nextFloat() + random.nextInt(150);
                        y = random.nextFloat() + random.nextInt(150);
                        position = "x: " + x + " y:" + y;
                        setPosition(position, dotView, textView, x, y);
                    }else if(lock_status == 3){
                        x = random.nextFloat() + random.nextInt((1076 - 150) + 1) + 150;
                        y = random.nextFloat() + random.nextInt((2198 - 200) + 1) + 200;
                        position = "x: " + x + " y:" + y;
                        setPosition(position, dotView, textView, x, y);
                    }else if(lock_status == 4){
                        x = random.nextFloat() + random.nextInt(150);
                        y = random.nextFloat() + random.nextInt((2198 - 200)+ 1) + 200;
                        position = "x: " + x + " y:" + y;
                        setPosition(position, dotView, textView, x, y);
                    }else if(lock_status == 5){
                        x = random.nextFloat() + random.nextInt((1076 - 150) + 1) + 150;
                        y = random.nextFloat() + random.nextInt(200);
                        position = "x: " + x + " y:" + y;
                        setPosition(position, dotView, textView, x, y);
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //Core.rotate(frame, frame, Core.ROTATE_90_CLOCKWISE);
                    Imgcodecs.imwrite(getExternalFilesDir(null) + ":" + x + "_" + y + ".jpg", frame);
                    x = 0;
                    y = 0;
                }
            }

            public void setPosition(String position, View view, TextView textView, float x, float y){
                position = "x: " + x + " y:" + y;
                textView.setText(position);
                textView.setTextColor(Color.GREEN);
                dotView.setX(x);
                dotView.setY(y);
            }

            public void togglePause(View view) {

                if (isPaused) {
                    onResume();
                    play_pause.setText("PAUSE");
                    isPaused = false;
                } else {
                    textView.setText("PAUSE");
                    play_pause.setText("PLAY");
                    isPaused = true;
                    onPause();
                }
            }
            public void toggleLockBounds(View view){
                String stat;
                if(!isLocked || lock_status <= 5){
                    lamp.setBackgroundColor(Color.GREEN);

                    if(lock_status < 5){
                        lock_status++;
                        stat = "" + lock_status;
                        statusView.setText(stat);
                    }else {
                        lock_status = 0;
                        stat = "" + lock_status;
                        statusView.setText(stat);
                        isLocked = true;
                    }
                }else if(isLocked == true & lock_status == 0){
                    isLocked = false;
                    lamp.setBackgroundColor(Color.GRAY);
                }
            }

            @Override
            public void onCameraViewStarted(int width, int height) {

            }

            @Override
            public void onCameraViewStopped() throws InterruptedException {
                handler.removeCallbacks(runnableCode);
                if (cameraBridgeViewBase != null)
                    cameraBridgeViewBase.disableView();
            }

            @Override
            public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

                Mat tempMat = new Mat();
                frame = inputFrame.rgba();
                tempMat = frame;
                Core.rotate(tempMat, frame, Core.ROTATE_180);
                Core.flip(frame, frame, 0);

                return frame;
            }


        static final Handler handler = new Handler();
        static final Handler stathandler = new Handler();
        static final Handler play_pausehandler = new Handler();
        private final Runnable runnableCode = new Runnable() {
            @Override
            public void run() {
                try {
                    if (!isPaused) {
                        moveDotAndTakePicture(frame);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                handler.postDelayed((Runnable) this, 2500);
            }
        };
        private final Runnable statHandler = new Runnable() {
            @Override
            public void run() {
                toggleLockBounds(lamp);
            }
        };

        private final Runnable play_pauseHandler = new Runnable() {
            @Override
            public void run() {
                togglePause(textView);
            }
        };

        @Override
        protected void onPause() {
            super.onPause();
        }

        @Override
        protected void onResume() {
            super.onResume();
        }
}

