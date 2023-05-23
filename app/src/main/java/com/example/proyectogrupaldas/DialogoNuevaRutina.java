package com.example.proyectogrupaldas;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DialogoNuevaRutina extends DialogFragment {

    private String usuario;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        //Recogemos los elementos que ha recibido el diálogo
        if (getArguments() != null){
            usuario = getArguments().getString("usuario");
        }

        //Definimos un builder para construir el diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Nueva rutina");

        //Mediante un LayoutInflater hacemos que la vista del diálogo sea un layout que hemos diseñado en un xml
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View aspectoDialog = inflater.inflate(R.layout.dialogo_nueva_rutina, null);
        builder.setView(aspectoDialog);

        //Opción de crear playlist, que añade la playlist y cierra el diálogo
        builder.setPositiveButton("Crear rutina", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //Recogemos el texto del EditText donde el usuario ha escrito el nombre de la playlist
                EditText nombreet = (EditText) aspectoDialog.findViewById(R.id.dnr_nombre);

                //Comprobamos que el nombre de la playlist no está en blanco
                if (nombreet.getText().toString().length() != 0){

                    String nombre = nombreet.getText().toString();

                    ((RutinasFragment) getParentFragment()).crearRutina(nombre);

                    dismiss();
                }
                else{
                    Toast.makeText(getContext(), "Por favor, introduce un nombre para la rutina", Toast.LENGTH_LONG).show();
                }

            }
        });

        //Opción de cancelar, que cierra el diálogo
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
            }
        });


        return builder.create();
    }

}
