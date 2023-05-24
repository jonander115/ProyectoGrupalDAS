package com.example.proyectogrupaldas;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class ServicioFirebase extends FirebaseMessagingService {

    public ServicioFirebase() {}

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getData().size() > 0){
            //Si el mensaje viene con datos
            //Programar la notificación

            //Definimos el Manager y el Builder
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "canalFirebase");

            //El Builder nos permite definir las características de la notificación
            //Recogemos el mensaje que ha sido enviado desde el servicio web
            builder.setSmallIcon(android.R.drawable.star_big_on)
                    .setContentTitle("Bienvenido")
                    .setContentText(remoteMessage.getData().get("mensaje"))
                    .setAutoCancel(true);

            //las versiones mayores que la Oreo necesitan un canal
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel canal = new NotificationChannel("canalFirebase", "CanalNotificacionFirebase", NotificationManager.IMPORTANCE_DEFAULT);

                canal.setDescription("Canal de mensajes de Firebase");
                canal.setLightColor(Color.MAGENTA);

                //Unimos el canal al manager
                manager.createNotificationChannel(canal);

                //El manager lanza la notificación
                manager.notify(123, builder.build());
            }

        }
        if (remoteMessage.getNotification() != null){
            //Si el mensaje es una notificación

            //(Solo he utilizado mensajes con datos)
            Log.d("firebase", "notificacion");

        }
    }
}
