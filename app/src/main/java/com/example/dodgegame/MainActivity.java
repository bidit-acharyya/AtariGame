package com.example.dodgegame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    GameSurface gameSurface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameSurface = new GameSurface(this);
        setContentView(gameSurface);

        x = findViewById(R.id.textView);
        y = findViewById(R.id.textView2);
        z = findViewById(R.id.textView3);

        SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometerSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sm.registerListener(this, accelerometerSensor, sm.SENSOR_DELAY_NORMAL);
    }
    TextView x, y, z;


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        x.setText(sensorEvent.values[0] + " ");
        y.setText(sensorEvent.values[1] + " ");
        z.setText(sensorEvent.values[2] + " ");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        gameSurface.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameSurface.resume();
    }

    public class GameSurface extends SurfaceView implements Runnable, SensorEventListener {

        Thread gameThread;
        SurfaceHolder holder;
        volatile boolean running = false;
        Bitmap ball;
        Bitmap background;
        int ballX = 0;
        int x = 200;

        Paint paintProperty;
        int screenWidth;
        int screenHeight;

        public GameSurface(Context context){
            super(context);
            holder = getHolder();
            ball = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
            background = BitmapFactory.decodeResource(getResources(), R.drawable.space);
            Display screenDisplay = getWindowManager().getDefaultDisplay();
            Point sizeOfScreen = new Point();
            screenDisplay.getSize(sizeOfScreen);
            screenWidth = sizeOfScreen.x;
            screenHeight = sizeOfScreen.y;
            paintProperty = new Paint();
        }

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }

        @Override
        public void run() {
            Canvas canvas = null;
            Drawable d = getResources().getDrawable(R.drawable.space, null);

            int flip = 1;
            while(running){
                if(holder.getSurface().isValid() == false)
                    continue;
                canvas = holder.lockCanvas(null);
                d.setBounds(getLeft(), getTop(), getRight(), getBottom());
                d.draw(canvas);

                canvas.drawBitmap(ball, (screenWidth/2)- (ball.getWidth()/2)+ballX, (screenHeight/2) - ball.getHeight(), null);

                Log.d("Ball&ScreenWidthFlip", ballX + " " + screenWidth + " " + flip);
                if(ballX == screenWidth/2 - ball.getWidth()/2 || ballX == -1*screenWidth/2+ball.getWidth()/2){
                    flip *= -1;
                }

                ballX += flip;
                holder.unlockCanvasAndPost(canvas);
            }
        }

        public void resume(){
            running = true;
            gameThread = new Thread(this);
            gameThread.start();
        }

        public void pause(){
            running = false;
            while(true){
                try {
                    gameThread.join();
                }
                catch (InterruptedException e){

                }
            }
        }
    }


}