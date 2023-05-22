package com.example.proyectogrupaldas;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.DialogFragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CuentaAtrasDialogo extends DialogFragment {
    public boolean isDialogVisible = false;
    private EditText min,seg;
    private Button start, stop;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;
    private final static String CHANNEL_ID = "NOTIFICACION";
    private final static int NOTIFICACION_ID = 0;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());

        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
        builder1.setTitle("Temporizador");
        builder1.setMessage("Introduce el tiempo");

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_cuenta_atras_dialogo, null);
        builder.setView(dialogView);
        seg=dialogView.findViewById(R.id.tic_Seg);
        min = dialogView.findViewById(R.id.tic_min);
        start = (Button) dialogView.findViewById(R.id.ticstartButton);
        stop = (Button) dialogView.findViewById(R.id.ticStopButton);


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
        return builder.create();
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
            Toast.makeText(getContext(), "LLEGA", Toast.LENGTH_SHORT).show();
            crearNotificacion();
        }
    }
    @SuppressLint("MissingPermission")
    private void crearNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "Notificacion", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = requireContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_launcher_background);
        builder.setContentTitle("El tiempo ha terminado");
        builder.setContentText("Hora de volver a entrenar");
        builder.setColor(Color.YELLOW);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setLights(Color.YELLOW, 1000, 1000);
        builder.setDefaults(Notification.DEFAULT_SOUND);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(requireContext());
        notificationManagerCompat.notify(NOTIFICACION_ID, builder.build());
    }
}