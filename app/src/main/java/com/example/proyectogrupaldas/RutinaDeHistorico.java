package com.example.proyectogrupaldas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ExpandableListView;
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
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RutinaDeHistorico extends AppCompatActivity {

    private String usuario;
    private String idRutina;
    private TextView tv_NombreDeRutina;
    private TextView tv_FechaInicio;
    private TextView tv_HoraInicio;
    private TextView tv_FechaFin;
    private TextView tv_HoraFin;
    private ExpandableListView listViewEjercicios;
    private ArrayList<String> listaEjercicios;
    private HashMap<String, ArrayList<String>> mapSeries;

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
        setContentView(R.layout.activity_rutina_de_historico);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            usuario = extras.getString("usuario");
            idRutina = extras.getString("idRutina");
        }

        //Recogemos los elementos de la vista
        tv_NombreDeRutina = findViewById(R.id.tv_NombreDeRutina);
        tv_FechaInicio = findViewById(R.id.tv_FechaInicio);
        tv_HoraInicio = findViewById(R.id.tv_HoraInicio);
        tv_FechaFin = findViewById(R.id.tv_FechaFin);
        tv_HoraFin = findViewById(R.id.tv_HoraFin);

        mostrarDatosDeRutina();
        mostrarDatosDeEjercicios();
    }

    //Método para recoger la información de la rutina de la base de datos y mostrarla al usuario
    private void mostrarDatosDeRutina(){
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

                    //El resultado es un JSON
                    JSONParser parser = new JSONParser();
                    JSONObject json = (JSONObject) parser.parse(response);

                    //Extraemos el resultado
                    String nombre = (String) json.get("Nombre");
                    String fechaHoraInicio = (String) json.get("FechaHoraInicio");
                    String fechaHoraFinal = (String) json.get("FechaHoraFinal");

                    //Mostramos la información
                    tv_NombreDeRutina.setText(nombre);

                    String[] fInicio = fechaHoraInicio.split(" ");
                    tv_FechaInicio.setText(fInicio[0]);
                    tv_HoraInicio.setText(fInicio[1]);

                    if (fechaHoraFinal.split(" ").length==0){
                        tv_FechaFin.setText("-En progreso-");
                        tv_HoraFin.setText("-En progreso-");
                    }
                    else{
                        String[] fFinal = fechaHoraFinal.split(" ");
                        tv_FechaFin.setText(fFinal[0]);
                        tv_HoraFin.setText(fFinal[1]);
                    }
/*
                    if (fFinal[0].equals("null")){
                        tv_FechaFin.setText("-En progreso-");
                    }
                    else{
                        tv_FechaFin.setText(fFinal[0]);
                    }
                    if (fFinal[1].equals("null")){
                        tv_HoraFin.setText("-En progreso-");
                    }
                    else{
                        tv_HoraFin.setText(fFinal[1]);
                    }

 */

                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Manejar error de la solicitud
                Toast.makeText(getApplicationContext(), "Error al obtener los datos de la rutina", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                // Agregar los parámetros necesarios
                params.put("accion","obtenerFechasDeRutina");
                params.put("usuario", usuario);
                params.put("idRutina", idRutina);

                return params;
            }
        };

        //Encolar la solicitud
        queue.add(stringRequest);
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
                    listViewEjercicios = (ExpandableListView) findViewById(R.id.listViewEjerciciosRutina);
                    EjerciciosRutinaAdapter adapter = new EjerciciosRutinaAdapter(usuario, getApplicationContext(), listaEjercicios, mapSeries);
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


    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("tv_NombreDeRutina",tv_NombreDeRutina.getText().toString());
        savedInstanceState.putString("tv_FechaInicio",tv_FechaInicio.getText().toString());
        savedInstanceState.putString("tv_HoraInicio",tv_HoraInicio.getText().toString());
        savedInstanceState.putString("tv_FechaFin",tv_FechaFin.getText().toString());
        savedInstanceState.putString("tv_HoraFin",tv_HoraFin.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        tv_NombreDeRutina.setText(savedInstanceState.getString("tv_NombreDeRutina"));
        tv_FechaInicio.setText(savedInstanceState.getString("tv_FechaInicio"));
        tv_HoraInicio.setText(savedInstanceState.getString("tv_HoraInicio"));
        tv_FechaFin.setText(savedInstanceState.getString("tv_FechaFin"));
        tv_HoraFin.setText(savedInstanceState.getString("tv_HoraFin"));
    }
}