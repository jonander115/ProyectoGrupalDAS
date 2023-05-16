package com.example.proyectogrupaldas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rutina);

        TextView nombre=findViewById(R.id.rut_nombre);

        nombre.setText(getIntent().getStringExtra("nombre"));
        String usuario=getIntent().getStringExtra("usuario");
        String idrutina=getIntent().getStringExtra("id");

        StringRequest sr = new StringRequest(Request.Method.POST, "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/jwojciechowska001/WEB/entrega3/obtenerejerciciosrutina.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //si la respuesta esta vacia imprime mensaje
                if(response.equals("null")){
                    Toast.makeText(getApplicationContext(), "No hay ejercicios para mostrar", Toast.LENGTH_SHORT).show();
                }
                else{
                    ArrayList<String> nombres = new ArrayList<String>();

                    //se obtiene el json en formato string que se vuelve a pasar a array de json
                    try {
                        JSONArray jsona = new JSONArray(response);
                        //para cada elemento del array que es un json

                        for (int i = 0; i < jsona.length(); i++)
                        {
                            JSONObject json = jsona.getJSONObject(i);
                            //se obtienen los datos del json y se setean para poder verse
                            nombres.add(json.getString("NombreEjercicio"));
                        }

                    }catch (Exception e){

                    }

                    ArrayAdapter a = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, nombres);
                    ListView lv = (ListView) findViewById(R.id.rut_lv);
                    lv.setAdapter(a);

                    rq.cancelAll("ejs");

                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int p, long id) {
                            //DE MOMENTO NO SE QUE TIENE QUE HACER
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
}