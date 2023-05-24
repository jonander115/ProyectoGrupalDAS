package com.example.proyectogrupaldas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class Ordenar extends AppCompatActivity {
    private Context context=this;
    private Activity activity=this;
    private ListView lv;
    private TextView nombre;
    private Button guardar;
    private String idrutina, usuario;
    private OrdenarAdapter a;
    private RequestQueue rq;

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
        setContentView(R.layout.activity_ordenar);

        //obtener elementos de la vista
        lv=findViewById(R.id.ord_ejercicios);
        nombre=findViewById(R.id.ord_nombre);
        idrutina=getIntent().getStringExtra("idrutina");
        usuario=getIntent().getStringExtra("usuario");
        nombre.setText(getIntent().getStringExtra("nombre"));

        //se obtienen los ejercicios de la rutina que se quieren ordenar de la bd
        obtenerEjercicios();
    }

    //se cogen de la base de datos los ejercicios de la rutina anteriormente seleccionada
    public void obtenerEjercicios(){
        StringRequest sr = new StringRequest(Request.Method.POST, "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/jwojciechowska001/WEB/entrega3/obtenerelementos.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //se crear arraylist para guardar los nombres y el orden de los ejercicios
                ArrayList<String> nombres = new ArrayList<String>();
                ArrayList<String> orden = new ArrayList<String>();

                //si la respuesta esta vacia imprime mensaje
                if(response.equals("null")){
                    Toast.makeText(getApplicationContext(), "No hay ejercicios para mostrar", Toast.LENGTH_SHORT).show();
                }
                else{

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
                        //en caso de excepcion no se hace nada
                    }
                    //se crea un adapter personalizado para el listview para que cada ejercicio sea un elemento del listview
                    a = new OrdenarAdapter(activity,context, idrutina, usuario, nombres, orden);
                    ListView lv =findViewById(R.id.ord_ejercicios);
                    lv.setAdapter(a);
                    //si se modifican datos actualizar el listview
                    a.notifyDataSetChanged();

                    rq.cancelAll("ejs");

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
                parametros.put("opcion", "obejerciciosrutina");
                parametros.put("usuario", usuario);
                parametros.put("rutina", idrutina);
                return parametros;
            }};

        //se envia la solicitud con los parametros
        rq = Volley.newRequestQueue(context);
        sr.setTag("ejs");
        rq.add(sr);
    }

}