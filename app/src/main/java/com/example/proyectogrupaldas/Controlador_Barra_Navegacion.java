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
import android.util.Log;
import android.view.MenuItem;

public class Controlador_Barra_Navegacion extends AppCompatActivity {

    private String lastFragment, usuario;
    private Fragment rt;
    ActivityControladorBarraNavegacionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            usuario = extras.getString("usuario");
        }
        if (savedInstanceState != null) {
            lastFragment = savedInstanceState.getString("fragment_act");
            if (lastFragment != null) {
                //restoreLastFragment();
                Log.d("usuario",usuario);

            } else {
                RutinasFragment rt = new RutinasFragment();
                Bundle args = new Bundle();
                args.putString("usuario", usuario);
                Log.d("usuario",usuario);
                rt.setArguments(args);
                replaceFragment(rt);
            }
        } else {
            RutinasFragment rt = new RutinasFragment();
            Bundle args = new Bundle();
            args.putString("usuario", usuario);
            Log.d("usuario",usuario);


            rt.setArguments(args);
            replaceFragment(rt);
        }

        binding = ActivityControladorBarraNavegacionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.navView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Bundle args;
                switch (item.getItemId()){
                    case R.id.navigation_historico:;
                        HistoricoRutinasFragment rt =new HistoricoRutinasFragment(getApplicationContext());
                        args = new Bundle();
                        args.putString("usuario", usuario);
                        rt.setArguments(args);
                        lastFragment = "historico";
                        replaceFragment(rt);
                        break;
                    case R.id.navigation_rutinas:;
                        RutinasFragment r1 = new RutinasFragment();
                        args = new Bundle();
                        args.putString("usuario", usuario);
                        r1.setArguments(args);
                        lastFragment = "rutinas";
                        replaceFragment(r1);

                        break;
                    case R.id.navigation_estadisticas:;
                        EstadisticasFragment r2 =new EstadisticasFragment();
                        args = new Bundle();
                        args.putString("usuario", usuario);
                        r2.setArguments(args);

                        lastFragment = "estadisticas";
                        replaceFragment(r2);
                        break;
                    case R.id.navigation_perfil:;
                        PerfilFragment r3 = new PerfilFragment();
                        args = new Bundle();
                        args.putString("usuario", usuario);
                        r3.setArguments(args);
                        lastFragment = "perfil";
                        replaceFragment(r3);

                        break;
                }
                return true;
            }
        });
    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("fragment_act", lastFragment);
    }
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager= getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }
//    private void restoreLastFragment() {
//        Bundle args;
//        switch (lastFragment) {
//            case "rutinas":
//                RutinasFragment r1 = new RutinasFragment();
//                 args = new Bundle();
//                args.putString("usuario", usuario);
//                r1.setArguments(args);
//                lastFragment = "rutinas";
//                replaceFragment(r1);
//
//                break;
//            case "historico":
//                HistoricoRutinasFragment rt =new HistoricoRutinasFragment(getApplicationContext());
//                args = new Bundle();
//                args.putString("usuario", usuario);
//                rt.setArguments(args);
//                lastFragment = "historico";
//                replaceFragment(rt);
//                break;
//
//            case "estadisticas":
//                EstadisticasFragment r2 =new EstadisticasFragment();
//                args = new Bundle();
//                args.putString("usuario", usuario);
//                r2.setArguments(args);
//
//                lastFragment = "estadisticas";
//                replaceFragment(r2);
//                break;
//            case "perfil":
//                PerfilFragment r3 = new PerfilFragment();
//                args = new Bundle();
//                args.putString("usuario", usuario);
//                r3.setArguments(args);
//                lastFragment = "perfil";
//                replaceFragment(r3);
//
//                break;
//        }
//
//    }
}