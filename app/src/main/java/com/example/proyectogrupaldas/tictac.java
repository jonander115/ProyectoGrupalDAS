package com.example.proyectogrupaldas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class tictac extends AppCompatActivity {
    private EditText min,seg;
    private Button start, stop;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;
    private final static String CHANNEL_ID = "NOTIFICACION";
    private final static int NOTIFICACION_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tictac);

        seg=findViewById(R.id.tic_Seg);
        min = findViewById(R.id.tic_min);
        start = (Button) findViewById(R.id.ticstartButton);
        stop = (Button) findViewById(R.id.ticStopButton);


        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {stopCountdown();}
        });
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCountdown();
            }
        });
    }

    private void startCountdown() {
        String minutos = min.getText().toString().trim();
        String segundos = seg.getText().toString().trim();


        long totalTimeInMillis = (Integer.parseInt(minutos) * 60 + Integer.parseInt(segundos)) * 1000;
        timeLeftInMillis = totalTimeInMillis;

        countDownTimer = new CountDownTimer(totalTimeInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountdownText();
            }

            @Override
            public void onFinish() {
                crearNotificacion();
            }
        }.start();
    }

    private void stopCountdown(){
        countDownTimer.cancel();
    }

    private void updateCountdownText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60 ;
        int seconds = (int) (timeLeftInMillis / 1000)% 60;

        min.setText(String.valueOf(minutes));
        seg.setText(String.valueOf(seconds));
        if(minutes==0 && seconds==0){
            crearNotificacion();
        }
    }
    @SuppressLint("MissingPermission")
    private void crearNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "Notificacion", NotificationManager.IMPORTANCE_DEFAULT );
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        builder.setSmallIcon(R.drawable.estadisticas);
        builder.setContentTitle("El tiempo ha terminado");
        builder.setContentText("Hora de volver a entrenar");
        builder.setColor(Color.YELLOW);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setLights(Color.YELLOW, 1000, 1000);
        builder.setDefaults(Notification.DEFAULT_SOUND);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat.notify(NOTIFICACION_ID, builder.build());
    }

}