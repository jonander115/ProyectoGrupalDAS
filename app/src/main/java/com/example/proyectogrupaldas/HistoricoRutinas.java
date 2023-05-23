package com.example.proyectogrupaldas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ExpandableListView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HistoricoRutinas extends AppCompatActivity {

    private String usuario;
    private ExpandableListView listViewHistorico;
    private ArrayList<String> listaMesesAños;
    private HashMap<String, ArrayList<String>> mapRutinas;

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
        setContentView(R.layout.activity_historico_rutinas);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            usuario = extras.getString("usuario");
        }

        mostrarHistorico();

    }


    //Método para mostrar el histórico de rutinas del usuario
    private void mostrarHistorico(){
        //Recogemos los datos a mostrar desde la base de datos

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
                    listaMesesAños = new ArrayList<>(); //Lista de pares mes-año donde el usuario tiene registradas rutinas de entrenamiento
                    mapRutinas = new HashMap<>(); //Relación entre las rutinas y los meses donde han sido realizadas

                    //Por cada par mes-año
                    for(int i = 0; i < jsonArray.length(); i++)
                    {
                        String mesAño = jsonArray.getJSONObject(i).getString("MesAnio");
                        String[] partes = mesAño.split("-");
                        String numMes = partes[0];
                        String año = partes[1];

                        String mes="";

                        switch (numMes){
                            case "01":
                                mes="Enero";
                                break;
                            case "02":
                                mes="Febrero";
                                break;
                            case "03":
                                mes="Marzo";
                                break;
                            case "04":
                                mes="Abril";
                                break;
                            case "05":
                                mes="Mayo";
                                break;
                            case "06":
                                mes="Junio";
                                break;
                            case "07":
                                mes="Julio";
                                break;
                            case "08":
                                mes="Agosto";
                                break;
                            case "09":
                                mes="Septiembre";
                                break;
                            case "10":
                                mes="Octubre";
                                break;
                            case "11":
                                mes="Noviembre";
                                break;
                            case "12":
                                mes="Diciembre";
                                break;
                        }

                        //Construimos el texto que se mostrará en el histórico por cada mes de la lista
                        String textoMesAño = mes + " de " + año;

                        //Actualizamos la lista de los meses
                        listaMesesAños.add(textoMesAño);

                        //Ahora hay que obtener las rutinas que el usuario ha realizado durante ese mes, y actualizar el HashMap
                        // que relaciona las rutinas con los meses donde aparecen
                        obtenerRutinas(numMes,año, i);

                    }


                    //Le pasamos a la vista los datos a mostrar mediante el adaptador
                    listViewHistorico = (ExpandableListView) findViewById(R.id.listViewHistoricoRutinas);
                    MesHistoricoAdapter adapter = new MesHistoricoAdapter(usuario, getApplicationContext(), listaMesesAños, mapRutinas);
                    listViewHistorico.setAdapter(adapter);


                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Manejar error de la solicitud
                Toast.makeText(getApplicationContext(), "Error al obtener los meses", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                // Agregar los parámetros necesarios
                params.put("accion","obtenerFechasDelHistorico");
                params.put("usuario", usuario);

                return params;
            }
        };

        //Encolar la solicitud
        queue.add(stringRequest);

    }


    //Método para obtener las rutinas que se han realizado en un mes
    private void obtenerRutinas(String numMes, String año, int posMesAñoEnLista){
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

                    ArrayList<String> listaRutinasDelMes = new ArrayList<>();

                    //Por cada rutina del mes
                    for(int i = 0; i < jsonArray.length(); i++)
                    {
                        String idRutina = jsonArray.getJSONObject(i).getString("IDRutina");
                        String nombreRutina = jsonArray.getJSONObject(i).getString("Nombre");
                        String dia = jsonArray.getJSONObject(i).getString("Dia");


                        //Añadimos a la lista de rutinas del mes la información de la rutina
                        listaRutinasDelMes.add(idRutina + "/" + nombreRutina + " - Día " + dia);

                    }

                    //Añadimos al HashMap la información recogida sobre la relación entre los meses y las rutinas
                    mapRutinas.put(listaMesesAños.get(posMesAñoEnLista),listaRutinasDelMes);

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Manejar error de la solicitud
                Toast.makeText(getApplicationContext(), "Error al obtener las rutinas", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                // Agregar los parámetros necesarios
                params.put("accion","obtenerRutinasDelMes");
                params.put("usuario", usuario);
                params.put("numMes", numMes);
                params.put("numAnio", año);

                return params;
            }
        };

        //Encolar la solicitud
        queue.add(stringRequest);
    }


}