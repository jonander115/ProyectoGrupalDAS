package com.example.proyectogrupaldas;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class Rutina extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rutina);

        TextView nombre=findViewById(R.id.rut_nombre);
        
        nombre.setText(getIntent().getStringExtra("nombre"));

    }
}