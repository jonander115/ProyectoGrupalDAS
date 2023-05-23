package com.example.proyectogrupaldas;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class EstadisticasFragment extends Fragment {

    String usuario = "joni";

    public EstadisticasFragment() {
        // Required empty public constructor
    }


    public static EstadisticasFragment newInstance(String param1, String param2) {
        EstadisticasFragment fragment = new EstadisticasFragment();
        Bundle args = new Bundle();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_estadisticas, container, false);

        Bundle extras = getArguments();
        if (extras != null) {
            usuario = extras.getString("usuario");
        }

        ListView listView = view.findViewById(R.id.listaMenuEstadisticas);
        ArrayList<String> listaOpciones = new ArrayList<>();
        listaOpciones.add("Rutinas");
        listaOpciones.add("Ejercicios");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, listaOpciones);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Acciones a realizar cuando se hace clic en un elemento de la lista
                String opcionSeleccionada = listaOpciones.get(position);
                if (opcionSeleccionada.equals("Rutinas")) {
                    Intent intent = new Intent(requireContext(), RutinasEstadistica.class);
                    intent.putExtra("usuario", usuario);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(requireContext(), EjerciciosEstadisticas.class);
                    intent.putExtra("usuario", usuario);
                    startActivity(intent);
                }
                requireActivity().finish();
            }
        });

        return view;
    }
}