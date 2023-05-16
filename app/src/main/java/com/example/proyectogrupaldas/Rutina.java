package com.example.proyectogrupaldas;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class Rutina extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rutina);

        TextView id=findViewById(R.id.rut_id);
        TextView nombre=findViewById(R.id.rut_nombre);

        id.setText(getIntent().getStringExtra("id"));
        nombre.setText(getIntent().getStringExtra("nombre"));

    }
}