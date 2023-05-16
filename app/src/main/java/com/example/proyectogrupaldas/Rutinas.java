package com.example.proyectogrupaldas;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Rutinas extends AppCompatActivity {
    private RequestQueue rq;

    private Context context=this;
    private String usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rutinas);

        usuario= getIntent().getStringExtra("id");
        Button aniadir= findViewById(R.id.ru_aniadir);
        aniadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogoNuevaRutina dialogo = new DialogoNuevaRutina();
                Bundle args = new Bundle();
                args.putString("usuario", usuario);
                dialogo.setArguments(args);
                dialogo.show(getSupportFragmentManager(), "dialogo_nueva_rutina");
            }
        });

        actualizarLista();
    }

    public void crearRutina(String nombre){
        StringRequest sr = new StringRequest(Request.Method.POST, "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/jwojciechowska001/WEB/entrega3/aniadirrutina.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //si la respuesta esta vacia imprime mensaje
                if(response.equals("0")){
                    Toast.makeText(getApplicationContext(), "Esa rutina ya existe", Toast.LENGTH_SHORT).show();
                }
                else if(response.equals("1")){
                    Toast.makeText(getApplicationContext(), "Se ha añadido la rutina", Toast.LENGTH_SHORT).show();
                    actualizarLista();
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
                parametros.put("nombre", nombre);

                return parametros;
            }};

        //se envia la solicitud con los parametros
        rq = Volley.newRequestQueue(context);
        sr.setTag("ejs");
        rq.add(sr);
    }

    private void actualizarLista(){
        StringRequest sr = new StringRequest(Request.Method.POST, "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/jwojciechowska001/WEB/entrega3/obtenerrutinas.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("respuesta",response);

                //si la respuesta esta vacia imprime mensaje
                if(response.equals("null")){
                    Toast.makeText(getApplicationContext(), "No hay rutinas para mostrar", Toast.LENGTH_SHORT).show();
                }
                else{
                    ArrayList<String> rutinas = new ArrayList<String>();
                    ArrayList<String> ids = new ArrayList<String>();

                    //se obtiene el json en formato string que se vuelve a pasar a array de json
                    try {
                        JSONArray jsona = new JSONArray(response);
                        //para cada elemento del array que es un json

                        for (int i = 0; i < jsona.length(); i++)
                        {
                            JSONObject json = jsona.getJSONObject(i);
                            //se obtienen los datos del json y se setean para poder verse
                            ids.add(json.getString("IDRutina"));
                            rutinas.add(json.getString("Nombre"));
                        }

                    }catch (Exception e){

                    }

                    ArrayAdapter a = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, rutinas);
                    ListView lv = (ListView) findViewById(R.id.rut_lv);
                    lv.setAdapter(a);

                    rq.cancelAll("rutinas");

                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int p, long id) {
                            //Cuando se pulsa una playlist se acceda a su contenido, que se muestra en otra actividad
                            Intent i = new Intent (Rutinas.this, Rutina.class);
                            //Enviamos el nombre de usuario y el nombre de la playlist a esta nueva actividad
                            i.putExtra("id", ids.get(p));
                            i.putExtra("nombre", rutinas.get(p));
                            i.putExtra("usuario", usuario);
                            startActivity(i);
                        }
                    });

                    lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, int p, long id) {
                            dialogoEliminar(ids.get(p));

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

                return parametros;
            }
        };

        //se envia la solicitud con los parametros
        rq = Volley.newRequestQueue(context);
        sr.setTag("rutinas");
        rq.add(sr);
    }

    private void eliminarRutina(String id){
        StringRequest sr = new StringRequest(Request.Method.POST, "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/jwojciechowska001/WEB/entrega3/eliminarrutina.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //si la respuesta esta vacia imprime mensaje
                if(response.equals("1")){
                    Toast.makeText(getApplicationContext(), "Se ha eliminado la rutina", Toast.LENGTH_SHORT).show();
                }
                actualizarLista();
                rq.cancelAll("eliminarrutina");
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
                parametros.put("id", id);

                return parametros;
            }};

        //se envia la solicitud con los parametros
        rq = Volley.newRequestQueue(context);
        sr.setTag("eliminarrutina");
        rq.add(sr);
    }

    private void dialogoEliminar(String id){

        AlertDialog.Builder ad=new AlertDialog.Builder(this);
        ad.setTitle("Eliminar rutina");
        ad.setMessage("¿Estás seguro de que quieres eliminar esta rutina?");
        ad.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            //si se pulsa que si se elimina la pelicula
            @Override
            public void onClick(DialogInterface dialog, int which) {
                eliminarRutina(id);
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

}