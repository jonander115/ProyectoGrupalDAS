package com.example.proyectogrupaldas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RutinaIniciada extends AppCompatActivity {

    private String usuario, nombre;
    private String idRutina;
    private TextView nombreRutinaIniciada;
    private ExpandableListView listViewEjercicios;
    private ArrayList<String> listaEjercicios;
    private HashMap<String, ArrayList<String>> mapSeries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rutina_iniciada);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            usuario = extras.getString("usuario");
            idRutina = extras.getString("idrutina");
            nombre = extras.getString("nombre");
        }

        //Recogemos los elementos de la vista
        nombreRutinaIniciada = findViewById(R.id.nombreRutinaIniciada);

        mostrarDatosDeEjercicios();
    }







    //Método para mostrar los ejercicios de los que está compuesta la rutina
    private void mostrarDatosDeEjercicios(){
        //Utilizamos un servicio web alojado en el servidor

        //Crear la cola de solicitudes
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        //Url del servicio web en el servidor
        String url = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/jwojciechowska001/WEB/entrega3/obtenerRutinasHistorico.php";

        //Solicitud
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Procesar la respuesta del servidor tras haberse recogido la imagen
                try {

                    //El resultado es un array
                    JSONArray jsonArray = new JSONArray(response);

                    //Variables en las que se almacenará la información a mostrar, que se mandará al adaptador
                    listaEjercicios = new ArrayList<>(); //Lista de nombres de los ejercicios que el usuario ha realizado en la rutina
                    mapSeries = new HashMap<>(); //Relación entre las series y sus respectivos ejercicios

                    //Por cada ejercicio
                    for(int i = 0; i < jsonArray.length(); i++) {
                        String nombreEjercicio = jsonArray.getJSONObject(i).getString("NombreEjercicio");
                        String orden = jsonArray.getJSONObject(i).getString("Orden");

                        //Actualizamos la lista de los ejercicios
                        listaEjercicios.add(nombreEjercicio);

                        //Ahora hay que obtener las series, y actualizar el HashMap que relaciona las series con los ejercicios
                        obtenerSeriesDeEjercicio(nombreEjercicio, orden, i);

                    }

                    //Le pasamos a la vista los datos a mostrar mediante el adaptador
                    listViewEjercicios = (ExpandableListView) findViewById(R.id.listaEjerciciosSeriesRutinaIniciada);
                    RutinaIniciadaAdapter adapter = new RutinaIniciadaAdapter(usuario, RutinaIniciada.this, listaEjercicios, mapSeries);
                    listViewEjercicios.setAdapter(adapter);


                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Manejar error de la solicitud
                Toast.makeText(getApplicationContext(), "Error al obtener los nombres de los ejercicios", Toast.LENGTH_LONG).show();
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


    //Método para obtener la información de las series de un ejercicio
    private void obtenerSeriesDeEjercicio(String nombreEjercicio, String orden, int posEjercicioEnLista){
        //Utilizamos un servicio web alojado en el servidor

        //Crear la cola de solicitudes
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        //Url del servicio web en el servidor
        String url = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/jwojciechowska001/WEB/entrega3/obtenerRutinasHistorico.php";

        //Solicitud
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Procesar la respuesta del servidor tras haberse recogido la imagen
                try {

                    //El resultado es un array
                    JSONArray jsonArray = new JSONArray(response);

                    ArrayList<String> listaSeriesDelEjercicio = new ArrayList<>();

                    //Contador para el número de serie
                    int cont = 0;

                    //Por cada serie del ejercicio
                    for(int i = 0; i < jsonArray.length(); i++)
                    {
                        cont++;
                        String peso = jsonArray.getJSONObject(i).getString("Peso");
                        String numRepeticiones = jsonArray.getJSONObject(i).getString("NumRepeticiones");
                        String notas = jsonArray.getJSONObject(i).getString("Notas");


                        //Añadimos a la lista de ejercicios la información de la serie
                        listaSeriesDelEjercicio.add(cont + "/" + peso + "/" + numRepeticiones + "/" + notas);

                    }

                    //Añadimos al HashMap la información recogida sobre la relación entre los ejercicios y las series
                    mapSeries.put(listaEjercicios.get(posEjercicioEnLista),listaSeriesDelEjercicio);

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Manejar error de la solicitud
                Toast.makeText(getApplicationContext(), "Error al obtener las series", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                // Agregar los parámetros necesarios
                params.put("accion","obtenerSeriesDeEjercicio");
                params.put("usuario", usuario);
                params.put("idRutina", idRutina);
                params.put("nombreEjercicio", nombreEjercicio);
                params.put("orden", orden);

                return params;
            }
        };

        //Encolar la solicitud
        queue.add(stringRequest);

    }
    public void aniadirSerie(int peso, int reps, String notas, String nombre) {
        StringRequest sr = new StringRequest(Request.Method.POST, "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/jwojciechowska001/WEB/entrega3/aniadirserie.php", new Response.Listener<String>() {
            public void onResponse(String response) {
                // Si la respuesta está vacía imprime un mensaje
                if (response.equals("0")) {
                    //  Toast.makeText(context, "Esa rutina ya existe", Toast.LENGTH_SHORT).show();
                } else if (response.equals("1")) {
                    Toast.makeText(getApplicationContext(), "Se ha añadido la serie", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Si ha habido algún error con la solicitud
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Se pasan todos los parámetros necesarios en la solicitud
                HashMap<String, String> parametros = new HashMap<>();
                parametros.put("usuario", usuario);
                parametros.put("nombre",nombre);
                parametros.put("peso", String.valueOf(peso));
                parametros.put("repeticiones", String.valueOf(reps));
                parametros.put("notas", notas);

                return parametros;
            }
        };

        // Se envía la solicitud con los parámetros
        RequestQueue rq = Volley.newRequestQueue(getApplicationContext());
        sr.setTag("ejs");
        rq.add(sr);
    }






    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("tv_NombreDeRutina",nombreRutinaIniciada.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        nombreRutinaIniciada.setText(savedInstanceState.getString("tv_NombreDeRutina"));
    }

}