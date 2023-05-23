package com.example.proyectogrupaldas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MenuEstadisticas extends AppCompatActivity {

    String usuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean tema = prefs.getBoolean("tema",true);
        if(tema) {
            setTheme(R.style.TemaClaro);
        }
        else{
            setTheme(R.style.TemaOscuro);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_estadisticas);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            usuario = extras.getString("usuario");
        }

        ListView listView = findViewById(R.id.listaMenuEstadisticas);
        ArrayList<String> listaOpciones = new ArrayList<>();
        listaOpciones.add("Rutinas");
        listaOpciones.add("Ejercicios");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaOpciones);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Acciones a realizar cuando se hace clic en un elemento de la lista
                String opcionSeleccionada = listaOpciones.get(position);
                if(opcionSeleccionada.equals("Rutinas")){
                    Intent intent = new Intent(MenuEstadisticas.this, RutinasEstadistica.class);
                    intent.putExtra("usuario", usuario);
                    startActivity(intent);

                }else{
                    Intent intent = new Intent(MenuEstadisticas.this, EjerciciosEstadisticas.class);
                    intent.putExtra("usuario", usuario);
                    startActivity(intent);
                }
                finish();
            }});

    }
}