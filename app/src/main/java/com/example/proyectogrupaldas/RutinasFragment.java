package com.example.proyectogrupaldas;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

public class RutinasFragment extends Fragment {
    private RequestQueue rq;
    private Context context;

    public RutinasFragment() {
        // Required empty public constructor
    }


    public static RutinasFragment newInstance(String param1, String param2) {
        RutinasFragment fragment = new RutinasFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_rutinas, container, false);

        context = getActivity();

        String usuario = getActivity().getIntent().getStringExtra("id");

        StringRequest sr = new StringRequest(Request.Method.POST, "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/jwojciechowska001/WEB/entrega3/obtenerrutinas.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String respuesta = response.toString();

                //si la respuesta esta vacia imprime mensaje
                if (respuesta.isEmpty()) {
                    Toast.makeText(context, "No hay rutinas para mostrar", Toast.LENGTH_SHORT).show();
                } else {
                    ArrayList<String> rutinas = new ArrayList<String>();
                    ArrayList<String> ids = new ArrayList<String>();

                    //se obtiene el json en formato string que se vuelve a pasar a array de json
                    try {
                        JSONArray jsona = new JSONArray(respuesta);
                        //para cada elemento del array que es un json

                        for (int i = 0; i < jsona.length(); i++) {
                            JSONObject json = jsona.getJSONObject(i);
                            //se obtienen los datos del json y se setean para poder verse
                            ids.add(json.getString("IDRutina"));
                            rutinas.add(json.getString("Nombre"));
                        }

                    } catch (Exception e) {
                        Toast.makeText(context, "Se ha producido un error", Toast.LENGTH_SHORT).show();
                    }

                    ArrayAdapter a = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, rutinas);
                    ListView lv = (ListView) view.findViewById(R.id.ru_lv);
                    lv.setAdapter(a);

                    rq.cancelAll("rutinas");

                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int p, long id) {
                            //Cuando se pulsa una playlist se acceda a su contenido, que se muestra en otra actividad
                            Intent i = new Intent(getActivity().getApplicationContext(), Rutina.class);
                            //Enviamos el nombre de usuario y el nombre de la playlist a esta nueva actividad
                            i.putExtra("id", ids.get(p));
                            i.putExtra("nombre", rutinas.get(p));
                            startActivity(i);
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

        return view;
    }
}
