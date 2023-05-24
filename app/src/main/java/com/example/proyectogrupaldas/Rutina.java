package com.example.proyectogrupaldas;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Rutina extends AppCompatActivity {
    private RequestQueue rq;
    private Context context=this;
    private String usuario;
    private String idrutina, nombreRutina;
    private ArrayAdapter a;

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
        setContentView(R.layout.activity_rutina);

        //obtener elementos de la vista
        TextView nombre=findViewById(R.id.rut_nombre);

        nombre.setText(getIntent().getStringExtra("nombre"));
        usuario=getIntent().getStringExtra("usuario");
        idrutina=getIntent().getStringExtra("id");

         nombreRutina = getIntent().getStringExtra("nombre");

        Button aniadir=findViewById(R.id.rut_guardar);
        Button ordenar=findViewById(R.id.rut_ordenar);
        Button iniciarEntrenamiento = findViewById(R.id.rut_iniciar);

        //boton para aniadir un ejercicio a la rutina
        aniadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //se genera un dialogo al que se le pasan los datos necesarios para aniadir luego un ejercicio
                DialogoVerCategorias dialogo = new DialogoVerCategorias();
                Bundle args = new Bundle();
                args.putString("usuario", usuario);
                args.putString("idrutina", idrutina);
                dialogo.setArguments(args);
                dialogo.show(getSupportFragmentManager(), "dialogo_elegircategoria_aniadir");
            }
        });

        //se puede ordenar los ejercicios de la rutina pulsandolo
        ordenar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Cuando se pulsa una playlist se acceda a su contenido, que se muestra en otra actividad
                Intent i = new Intent (Rutina.this, Ordenar.class);
                //Enviamos el nombre de usuario y el nombre de la playlist a esta nueva actividad
                i.putExtra("idrutina", idrutina);
                i.putExtra("usuario", usuario);
                i.putExtra("nombre", getIntent().getStringExtra("nombre"));
                startActivityForResult(i, 1);
            }
        });

        //boton para iniciar el entrenamiento
        iniciarEntrenamiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertarRutinaIniciada(nombreRutina);
            }


        });

        //actualizamos los ejercicios de la rutina
        actualizarLista();
    }

    //se hace una llamada a la base de datos para obtener los ejercicios de la rutina
    public void actualizarLista(){
        StringRequest sr = new StringRequest(Request.Method.POST, "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/jwojciechowska001/WEB/entrega3/obtenerejerciciosrutina.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //si la respuesta esta vacia imprime mensaje
                if(response.equals("null")){
                    Toast.makeText(getApplicationContext(), "No hay ejercicios para mostrar", Toast.LENGTH_SHORT).show();
                }
                else{
                    //se crean arraylist para guardar luego los nombres y el orden de los ejercicios
                    ArrayList<String> nombres = new ArrayList<String>();
                    ArrayList<String> orden = new ArrayList<String>();

                    //se obtiene el json en formato string que se vuelve a pasar a array de json
                    try {
                        JSONArray jsona = new JSONArray(response);
                        //para cada elemento del array que es un json

                        for (int i = 0; i < jsona.length(); i++)
                        {
                            JSONObject json = jsona.getJSONObject(i);
                            //se obtienen los datos del json y se setean para poder verse
                            nombres.add(json.getString("NombreEjercicio"));
                            orden.add(json.getString("Orden"));
                        }

                    }catch (Exception e){
                        //no se hace nada si ocurre una excepcion
                    }

                    //se genera un adapter para que cada elemento (ejercicio) obtenido sea un elemento del arraylist
                    a = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, nombres);
                    ListView lv = (ListView) findViewById(R.id.rut_ejercicios);
                    lv.setFocusable(false);
                    lv.setAdapter(a);
                    //se llama para actualizar los datos una vez se modifica los elementos en el adapter
                    a.notifyDataSetChanged();

                    rq.cancelAll("ejs");

                    //si se mantiene pulsado largo se puede eliminar el ejercicio pulsado
                    lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, int p, long id) {
                            dialogoEliminar(nombres.get(p),orden.get(p));
                            return false;
                        }
                    });

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //si ha habido algun error con la solicitud
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //se pasan todos los parametros necesarios en la solicitud
                HashMap<String, String> parametros = new HashMap<String, String>();
                parametros.put("usuario", usuario);
                parametros.put("rutina", idrutina);

                return parametros;
            }};

        //se envia la solicitud con los parametros
        rq = Volley.newRequestQueue(context);
        sr.setTag("ejs");
        rq.add(sr);
    }

    private void dialogoEliminar(String nombre, String orden){
        //crear el dialogo
        AlertDialog.Builder ad=new AlertDialog.Builder(this);
        ad.setTitle("Eliminar ejercicio");
        ad.setMessage("¿Estás seguro de que quieres eliminar este ejercicio?");
        ad.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            //si se pulsa que si se elimina la pelicula
            @Override
            public void onClick(DialogInterface dialog, int which) {
                eliminarEjercicio(nombre,orden);
                actualizarLista();
            }
        });
        //si se pulsa que no nos mantenemos en la actividad
        ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //no hace nada
            }
        });
        ad.create().show();
    }

    private void eliminarEjercicio(String nombre, String orden){
        StringRequest sr = new StringRequest(Request.Method.POST, "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/jwojciechowska001/WEB/entrega3/eliminarejercicio.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //si la respuesta esta vacia imprime mensaje
                if(response.equals("1")){
                    Toast.makeText(getApplicationContext(), "Se ha eliminado el ejercicio", Toast.LENGTH_SHORT).show();
                }
                //mostrar lista con los nuevos elementos
                actualizarLista();
                rq.cancelAll("eliminarejercicio");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //si ha habido algun error con la solicitud
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //se pasan todos los parametros necesarios en la solicitud
                HashMap<String, String> parametros = new HashMap<String, String>();
                parametros.put("idrutina", idrutina);
                parametros.put("usuario", usuario);
                parametros.put("nombreejercicio", nombre);
                parametros.put("orden", orden);

                return parametros;
            }};

        //se envia la solicitud con los parametros
        rq = Volley.newRequestQueue(context);
        sr.setTag("eliminarejercicio");
        rq.add(sr);
    }

    //para actualizar los ejercios volviendo de una rutina concreta
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==1){
            actualizarLista();
        }
    }


    private void insertarRutinaIniciada(String nombreRutina) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        //Url del servicio web en el servidor
        String url = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/jwojciechowska001/WEB/entrega3/rutinaIniciada.php";

        //Solicitud
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Procesar la respuesta del servidor
                Toast.makeText(getApplicationContext(), "Rutina comenzada", Toast.LENGTH_LONG).show();

                //La respuesta es el id de la nueva rutina, lo enviamos a la nueva actividad
                Intent i = new Intent (Rutina.this, RutinaIniciada.class);
                i.putExtra("idRutinaPlantilla", idrutina); //id de la rutina plantilla
                i.putExtra("idRutina", response); //id de la nueva rutina insertada
                i.putExtra("usuario", usuario);
                i.putExtra("nombreRutina", nombreRutina);
                startActivity(i);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Manejar error de la solicitud
                Toast.makeText(getApplicationContext(), "Error al crear la rutina", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                // Agregar los parámetros necesarios
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String horaInicio = now.format(formatter);
                params.put("accion", "insertarRutina");
                params.put("fechaHoraInicio",horaInicio);
                params.put("usuario", usuario);
                params.put("nombreRutina",nombreRutina);
                params.put("idRutina",idrutina);

                return params;
            }
        };

        //Encolar la solicitud
        queue.add(stringRequest);
    }

}