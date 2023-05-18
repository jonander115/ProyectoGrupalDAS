package com.example.proyectogrupaldas;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Rutina extends AppCompatActivity {
    private RequestQueue rq;
    private Context context=this;
    private String usuario;
    private String idrutina;
    private ArrayAdapter a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rutina);

        TextView nombre=findViewById(R.id.rut_nombre);

        nombre.setText(getIntent().getStringExtra("nombre"));
        usuario=getIntent().getStringExtra("usuario");
        idrutina=getIntent().getStringExtra("id");

        Button aniadir=findViewById(R.id.rut_aniadir);

        aniadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        DialogoVerCategorias dialogo = new DialogoVerCategorias();
                        Bundle args = new Bundle();
                        args.putString("usuario", usuario);
                        args.putString("idrutina", idrutina);
                        dialogo.setArguments(args);
                        dialogo.show(getSupportFragmentManager(), "dialogo_elegircategoria_aniadir");
            }
        });
        actualizarLista();
    }

    public void actualizarLista(){
        StringRequest sr = new StringRequest(Request.Method.POST, "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/jwojciechowska001/WEB/entrega3/obtenerejerciciosrutina.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //si la respuesta esta vacia imprime mensaje
                if(response.equals("null")){
                    Toast.makeText(getApplicationContext(), "No hay ejercicios para mostrar", Toast.LENGTH_SHORT).show();
                }
                else{
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

                    }

                    a = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, nombres);
                    ListView lv = (ListView) findViewById(R.id.d_categorias);
                    lv.setAdapter(a);
                    a.notifyDataSetChanged();

                    rq.cancelAll("ejs");

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

}