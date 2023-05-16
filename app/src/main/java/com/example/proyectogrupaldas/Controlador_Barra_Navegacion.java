package com.example.proyectogrupaldas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.proyectogrupaldas.databinding.ActivityControladorBarraNavegacionBinding;
import com.google.android.material.navigation.NavigationBarView;

import android.os.Bundle;
import android.view.MenuItem;

public class Controlador_Barra_Navegacion extends AppCompatActivity {

    private String lastFragment;

    ActivityControladorBarraNavegacionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            lastFragment = savedInstanceState.getString("fragment_act");
            if (lastFragment != null) {
                restoreLastFragment();
            } else {
                replaceFragment(new RutinasFragment());
            }
        } else {
            replaceFragment(new RutinasFragment());
        }

        binding = ActivityControladorBarraNavegacionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.navView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navigation_registro:;
                        lastFragment = "registro";
                        replaceFragment(new RegistroFragment());
                        break;
                    case R.id.navigation_rutinas:;
                        lastFragment = "rutinas";
                        replaceFragment(new RutinasFragment());
                        break;
                    case R.id.navigation_estadisticas:;
                        lastFragment = "estadisticas";
                        replaceFragment(new EstadisticasFragment());
                        break;
                }
                return true;
            }
        });
        BottomNavigationView navigationView = findViewById(R.id.nav_view);


       }
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager= getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }
    private void restoreLastFragment() {
        switch (lastFragment) {
            case "registro":
                replaceFragment(new RutinasFragment());
                break;
            case "rutinas":
                replaceFragment(new RegistroFragment());
                break;
            case "estadisticas":
                replaceFragment(new EstadisticasFragment());
                break;
        }

    }
}