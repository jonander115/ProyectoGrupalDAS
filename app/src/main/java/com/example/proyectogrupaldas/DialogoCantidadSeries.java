package com.example.proyectogrupaldas;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DialogoCantidadSeries extends DialogFragment {
    private EditText peso, num_rep, notas;
    private String usuario, ejercicioSeleccionado, idRutina;
    private ArrayList<String> listaEjercicios;
    private  Spinner spinnerEjercicio;

    @Nullable
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        if (getArguments() != null){
            usuario = getArguments().getString("usuario");
            idRutina = getArguments().getString("idRutina");
        }

        LayoutInflater inflater= getActivity().getLayoutInflater();
        View aspectoDialog=inflater.inflate(R.layout.dialogo_cantidad_series,null);
        builder.setView(aspectoDialog);
        builder.setTitle("Seleccione el numero de Series");

        peso= aspectoDialog.findViewById(R.id.campo_cant_peso);
        num_rep= aspectoDialog.findViewById(R.id.campo_cant_reps);
        notas= aspectoDialog.findViewById(R.id.campo_cant_notas);

        spinnerEjercicio = aspectoDialog.findViewById(R.id.spinner_ejercicio);

        rellenarSpinnerEjercicios();


        builder.setPositiveButton("Añadir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String textPeso = peso.getText().toString();
                String textNumRep = num_rep.getText().toString();
                String textoNotas = notas.getText().toString();

                if (textPeso.equals("")){
                    peso.setText("0");
                }
                if (textNumRep.equals("")){
                    num_rep.setText("0");
                }
                if (textoNotas.equals("")){
                    notas.setText("-");
                }

                if (Integer.parseInt(num_rep.getText().toString()) >= 0 && Integer.parseInt(peso.getText().toString()) >= 0) {
                    if (!ejercicioSeleccionado.equals("")){

                        //Añadir serie al ejercicio
                        ((RutinaIniciada) getActivity()).aniadirSerie(ejercicioSeleccionado, Integer.parseInt(peso.getText().toString()), Integer.parseInt(num_rep.getText().toString()), notas.getText().toString());


                    }
                    else{
                        Toast.makeText(getContext(), "Debes introducir un ejercicio", Toast.LENGTH_LONG).show();
                    }


                }
                else{
                    Toast.makeText(getContext(), "Debes introducir un número positivo en peso y repeticiones", Toast.LENGTH_LONG).show();

                }
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
            }
        });
                return builder.create();
    }


    //Método para mostrar todos los ejercicios de la rutina en el desplegable
    private void rellenarSpinnerEjercicios(){
        //Utilizamos un servicio web alojado en el servidor

        //Crear la cola de solicitudes
        RequestQueue queue = Volley.newRequestQueue(getContext());

        //Url del servicio web en el servidor
        String url = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/jwojciechowska001/WEB/entrega3/obtenerRutinasHistorico.php";

        //Solicitud
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Procesar la respuesta del servidor
                try {

                    //El resultado es un array
                    JSONArray jsonArray = new JSONArray(response);

                    //Variables en las que se almacenará la información a mostrar, que se mandará al adaptador
                    listaEjercicios = new ArrayList<>(); //Lista de nombres de los ejercicios que el usuario ha realizado en la rutina

                    //Por cada ejercicio
                    for(int i = 0; i < jsonArray.length(); i++) {
                        String nombreEjercicio = jsonArray.getJSONObject(i).getString("NombreEjercicio");
                        //String orden = jsonArray.getJSONObject(i).getString("Orden");

                        //Actualizamos la lista de los ejercicios
                        listaEjercicios.add(nombreEjercicio);

                    }

                    //Le pasamos a la vista los datos a mostrar mediante el adaptador
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, listaEjercicios);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerEjercicio.setAdapter(adapter);

                    spinnerEjercicio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            ejercicioSeleccionado = (String) parent.getItemAtPosition(position);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            // Acciones adicionales cuando no se selecciona ningún ejercicio
                        }
                    });


                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Manejar error de la solicitud
                Toast.makeText(getContext(), "Error al obtener los nombres de los ejercicios", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                // Agregar los parámetros necesarios
                params.put("accion","obtenerEjerciciosDeRutina");
                params.put("usuario", usuario);
                params.put("idRutina", idRutina);

                return params;
            }
        };

        //Encolar la solicitud
        queue.add(stringRequest);
    }



}
