package com.example.proyectogrupaldas;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DialogoEliminarEjercicios extends DialogFragment {

    private String usuario;
    private String[] listaNombresEjercicios;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        //Recogemos los elementos que ha recibido el diálogo
        if (getArguments() != null){
            usuario = getArguments().getString("usuario");
            listaNombresEjercicios = getArguments().getStringArray("listaNombresEjercicios");
        }

        //Definimos un builder para construir el diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Selecciona los ejercicios creados por ti que quieras eliminar");

        final ArrayList<Integer> opcionesElegidas = new ArrayList<Integer>();

        if (listaNombresEjercicios.length == 0){
            builder.setMessage("- No hay ningún ejercicio creado por ti -");
        }
        else{
            builder.setMultiChoiceItems(listaNombresEjercicios, null, new DialogInterface.OnMultiChoiceClickListener(){

                @Override
                public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                    if (b == true){
                        opcionesElegidas.add(i);
                    }
                    else if (opcionesElegidas.contains(i)){
                        opcionesElegidas.remove(Integer.valueOf(i));
                    }
                }

            });


        }

        //Opción de Aceptar, que borra los ejercicios seleccionados y cierra el diálogo
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (opcionesElegidas.size() == 0){
                    Toast.makeText(getContext(), "No se ha eliminado ningún ejercicio", Toast.LENGTH_LONG).show();
                }
                else{

                    //Recogemos y eliminamos los ejercicios seleccionados
                    for (int elem=0; elem<opcionesElegidas.size(); elem++){

                        int numeroOpcionElegida = opcionesElegidas.get(elem);
                        String nombreEjercicioABorrar = listaNombresEjercicios[numeroOpcionElegida];

                        //Eliminamos el ejercicio
                        FragmentManager fragmentManager = getChildFragmentManager();
                        PerfilFragment fragment = (PerfilFragment) fragmentManager.findFragmentByTag("");
                        if (fragment != null){
                            fragment.eliminarEjercicio(nombreEjercicioABorrar);
                        }

                    }

                }

                dismiss();
            }
        });


        //Opción de Cancelar, que cierra el diálogo
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
            }
        });


        return builder.create();
    }


}
