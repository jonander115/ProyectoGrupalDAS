package com.example.proyectogrupaldas;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrdenarAdapter extends ArrayAdapter {
    private final Activity c;
    private final Context contextc;
    private String rutina, usuario;
    private ArrayList<String> nombres=new ArrayList<>();
    private ArrayList<String> orden=new ArrayList<>();
    private LayoutInflater inflater;
    private RequestQueue rq;

    public OrdenarAdapter(Activity context,Context con, String r, String u, ArrayList<String> nom, ArrayList<String> ord){
        super(context, R.layout.lv_reorganizar, nom);

        this.c=context;
        this.contextc=con;
        this.rutina=r;
        this.usuario=u;
        this.nombres=nom;
        this.orden=ord;
        inflater = context.getLayoutInflater();
    }

    @Override
    public View getView(int p, View view, ViewGroup parent) {
        view = inflater.inflate(R.layout.lv_reorganizar, null);

        TextView ejercicio=view.findViewById(R.id.ejercicio);
        Button arriba=view.findViewById(R.id.arriba);
        Button abajo=view.findViewById(R.id.abajo);

        if (p==0){
            arriba.setVisibility(View.INVISIBLE);
        }

        if (p==nombres.size()-1){
            abajo.setVisibility(View.INVISIBLE);
        }
        Log.d("respuesta", Integer.toString(nombres.size()));

        ejercicio.setText(nombres.get(p));

        arriba.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("respuesta", "arriba");
                intercambiarOrden(p,p-1);
            }
        });

        abajo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("respuesta", "abajo");
                intercambiarOrden(p,p+1);
            }
        });

        return view;
    }

    private void intercambiarOrden(int actual, int nuevo){
        StringRequest sr = new StringRequest(Request.Method.POST, "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/jwojciechowska001/WEB/entrega3/ordenar.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String respuesta=response.toString();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //si ha habido algun error con la solicitud
                Log.d("respuesta", "se ha producido un error");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //se pasan todos los parametros necesarios en la solicitud
                HashMap<String, String> parametros = new HashMap<String, String>();
                parametros.put("usuario", usuario);
                parametros.put("idrutina", rutina);
                parametros.put("nombreactual", nombres.get(actual));
                parametros.put("nombreotro", nombres.get(nuevo));
                parametros.put("ordenactual", orden.get(actual));
                parametros.put("ordenotro", orden.get(nuevo));

                return parametros;
            }
        };

        //se envia la solicitud con los parametros
        rq = Volley.newRequestQueue(contextc);
        sr.setTag("login");
        rq.add(sr);
    }
}
