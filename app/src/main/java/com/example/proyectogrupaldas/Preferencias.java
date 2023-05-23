package com.example.proyectogrupaldas;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceFragmentCompat;

import com.example.proyectogrupaldas.Login;
import com.example.proyectogrupaldas.R;


import java.util.Locale;
//Clase que se encarga de gestionar las preferencias del usuario.
public class Preferencias extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener{
    @Override
    public void onCreatePreferences (Bundle savedInstanceState, String rootKey){
        addPreferencesFromResource(R.xml.pref_config);
    }

    //Metodo llamado automaticamente cuando se realiza un cambio en las preferencias
    @Override
    public void onSharedPreferenceChanged (SharedPreferences sharedPreferences, String s){
        switch (s) {
            //Si se ha cambiado de tema
            case "tema" :
                //Se finaliza la actividad para crearla de nuevo ocn el tema nuevo
                getActivity().finish();
                startActivity(getActivity().getIntent());

            default:
                break;
        }
    }

    //Metodo para recuperar las preferencias en caso de que se destruya la actividad
    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    //Metodo para recuperar las preferencias en caso de que la actividad pase a segundo plano
    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

}
