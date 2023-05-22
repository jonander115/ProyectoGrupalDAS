package com.example.proyectogrupaldas;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class EstadisticasFragment extends Fragment {
    private CuentaAtrasDialogo cuentaAtrasDialogo;
    private Button botoncito;
    private final static String CHANNEL_ID = "NOTIFICACION";
    private final static int NOTIFICACION_ID = 0;
    public EstadisticasFragment() {
        // Required empty public constructor
    }

     public static EstadisticasFragment newInstance(String param1, String param2) {
        EstadisticasFragment fragment = new EstadisticasFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // onSaveInstanceState(Bundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_estadisticas, container, false);

        botoncito= view.findViewById(R.id.esta_dialo);
        botoncito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CuentaAtrasDialogo cuentaAtrasDialogo=new CuentaAtrasDialogo();
                cuentaAtrasDialogo.show(getChildFragmentManager(), "cuentaAtras");
                crearNotificacion();
            }
        });

        return view;
    }


    @SuppressLint("MissingPermission")
    private void crearNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "Notificacion", NotificationManager.IMPORTANCE_DEFAULT );
            NotificationManager notificationManager = getContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_launcher_background);
        builder.setContentTitle("El tiempo ha terminado");
        builder.setContentText("Hora de volver a entrenar");
        builder.setColor(Color.YELLOW);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setLights(Color.YELLOW, 1000, 1000);
        builder.setDefaults(Notification.DEFAULT_SOUND);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getContext());
        notificationManagerCompat.notify(NOTIFICACION_ID, builder.build());
    }
}