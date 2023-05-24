package com.example.proyectogrupaldas;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

    private ContextThemeWrapper contextThemeWrapper;
    private RequestQueue rq;
    private Context context;
    private String usuario;
    private View view;

    public RutinasFragment() {
        // Required empty public constructor
    }


    public static RutinasFragment newInstance(String param1, String param2) {
        RutinasFragment fragment = new RutinasFragment();
        Bundle args = new Bundle();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater,container, savedInstanceState);

        context = requireContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean tema = prefs.getBoolean("tema",true);
        if(tema) {
            contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.FragmentTheme);
        }
        else{
            contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.FragmentTheme2);
        }


        // Infla el diseño utilizando el ContextThemeWrapper
        LayoutInflater themedInflater = inflater.cloneInContext(contextThemeWrapper);
        view = themedInflater.inflate(R.layout.fragment_rutinas, container, false);

        //super.onCreateView(inflater,container,savedInstanceState);
        //View view = inflater.inflate(R.layout.fragment_rutinas, container, false);

        if (getArguments()!=null) {
            usuario = getArguments().getString("usuario");
        }
        Button aniadir = view.findViewById(R.id.ru_aniadir);
        aniadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogoNuevaRutina dialogo = new DialogoNuevaRutina();
                Bundle args = new Bundle();
                args.putString("usuario", usuario);
                dialogo.setArguments(args);
                dialogo.show(getChildFragmentManager(), "dialogo_nueva_rutina");
            }
        });

        actualizarLista();

        return view; }
    public void crearRutina(String nombre) {
        StringRequest sr = new StringRequest(Request.Method.POST, "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/jwojciechowska001/WEB/entrega3/aniadirrutina.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Si la respuesta está vacía imprime un mensaje
                if (response.equals("0")) {
                    Toast.makeText(context, "Esa rutina ya existe", Toast.LENGTH_SHORT).show();
                } else if (response.equals("1")) {
                    Toast.makeText(context, "Se ha añadido la rutina", Toast.LENGTH_SHORT).show();
                    actualizarLista();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Si ha habido algún error con la solicitud
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Se pasan todos los parámetros necesarios en la solicitud
                HashMap<String, String> parametros = new HashMap<>();
                parametros.put("usuario", usuario);
                parametros.put("nombre", nombre);

                return parametros;
            }
        };

        // Se envía la solicitud con los parámetros
        rq = Volley.newRequestQueue(context);
        sr.setTag("ejs");
        rq.add(sr);
    }
    private void actualizarLista() {
        StringRequest sr = new StringRequest(Request.Method.POST, "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/jwojciechowska001/WEB/entrega3/obtenerrutinas.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("respuesta", response);

                // Si la respuesta está vacía imprime un mensaje
                if (response.equals("null")) {
                    Toast.makeText(context, "No hay rutinas para mostrar", Toast.LENGTH_SHORT).show();
                } else {
                    ArrayList<String> rutinas = new ArrayList<>();
                    ArrayList<String> ids = new ArrayList<>();

                    // Se obtiene el JSON en formato String y se vuelve a pasar a un array de JSON
                    try {
                        JSONArray jsona = new JSONArray(response);
                        // Para cada elemento del array que es un JSON
                        for (int i = 0; i < jsona.length(); i++) {
                            JSONObject json = jsona.getJSONObject(i);
                            // Se obtienen los datos del JSON y se configuran para poder verse
                            ids.add(json.getString("IDRutina"));
                            rutinas.add(json.getString("Nombre"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, rutinas);
                    //ListView lv = (ListView) findViewById(R.id.rut_ejercicios);

                    ListView listView = view.findViewById(R.id.rut_ejercicios);
                    listView.setAdapter(adapter);

                    rq.cancelAll("rutinas");

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int p, long id) {
                            // Cuando se pulsa una rutina se accede a su contenido, que se muestra en otra actividad
                            Intent intent = new Intent(requireContext(), Rutina.class);
                            // Enviamos el nombre de usuario y el nombre de la rutina a esta nueva actividad
                            intent.putExtra("id", ids.get(p));
                            intent.putExtra("nombre", rutinas.get(p));
                            intent.putExtra("usuario", usuario);
                            startActivity(intent);
                        }
                    });

                    listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
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
                // Si ha habido algún error con la solicitud
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Se pasan todos los parámetros necesarios en la solicitud
                HashMap<String, String> parametros = new HashMap<>();
                parametros.put("usuario", usuario);

                return parametros;
            }
        };

        // Se envía la solicitud con los parámetros
        rq = Volley.newRequestQueue(context);
        sr.setTag("rutinas");
        rq.add(sr);
    }
    private void eliminarRutina(String id) {
        StringRequest sr = new StringRequest(Request.Method.POST, "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/jwojciechowska001/WEB/entrega3/eliminarrutina.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Si la respuesta está vacía imprime un mensaje
                if (response.equals("1")) {
                    Toast.makeText(context, "Se ha eliminado la rutina", Toast.LENGTH_SHORT).show();
                }
                actualizarLista();
                rq.cancelAll("eliminarrutina");
            } }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Si ha habido algún error con la solicitud
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Se pasan todos los parámetros necesarios en la solicitud
                HashMap<String, String> parametros = new HashMap<>();
                parametros.put("id", id);

                return parametros;
            }
        };

        // Se envía la solicitud con los parámetros
        rq = Volley.newRequestQueue(context);
        sr.setTag("eliminarrutina");
        rq.add(sr);
    }
    private void dialogoEliminar(String id) {
        AlertDialog.Builder ad = new AlertDialog.Builder(requireContext());
        ad.setTitle("Eliminar rutina");
        ad.setMessage("¿Estás seguro de que quieres eliminar esta rutina?");
        ad.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            // Si se pulsa "Sí", se elimina la rutina
            @Override
            public void onClick(DialogInterface dialog, int which) {
                eliminarRutina(id);
            }
        });
        // Si se pulsa "No", no se hace nada
        ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // No hace nada
            }
        });
        ad.create().show();
    }



}

