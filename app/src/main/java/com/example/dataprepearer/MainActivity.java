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
import java.util.concurrent.atomic.AtomicBoolean;


public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    CameraBridgeViewBase cameraBridgeViewBase;
    private SurfaceView surfaceView;
    private View dotView, lamp;
    private TextView textView, progressX, progressY;
    private Button play_pause, lock_bounds;
    private static final int PERMISSION_REQUEST_CODE = 200;
    public float x;
    public float y;
    public static Mat frame;
    public int Ymin = 800;
    public int Ymax = 2200;
    public int Xmin = 300;
    public int Xmax = 1080;
    public FrameLayout layout;
    public boolean isPaused = false;
    private AtomicBoolean isLocked = new AtomicBoolean(false);
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
            lock_bounds = findViewById(R.id.lock_bounds);


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

                    if(isLocked.get() == false) {
                        x = random.nextFloat() * 1080;
                        y = random.nextFloat() * 2200;

                        position = "x: " + x + " y:" + y;
                        textView.setText(position);
                        textView.setTextColor(Color.BLACK);
                        dotView.setX(x);
                        dotView.setY(y);

                    }else if (isLocked.get() == true & x == 0 || y == 0){
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
                        textView.setText(position);
                        textView.setTextColor(Color.BLUE);
                        dotView.setX(x);
                        dotView.setY(y);
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

                if(isLocked.get() == false) {
                    isLocked.set(true);
                    lamp.setBackgroundColor(Color.GREEN);

                }else{
                    isLocked.set(false);
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

        @Override
        protected void onPause() {
            super.onPause();
        }

        @Override
        protected void onResume() {
            super.onResume();
        }
}

