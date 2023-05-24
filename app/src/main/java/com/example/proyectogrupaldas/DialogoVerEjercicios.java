package com.example.proyectogrupaldas;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.preference.PreferenceManager;

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

public class DialogoVerEjercicios extends DialogFragment {

    private RequestQueue rq;
    private String usuario, idrutina, categoria;
    ListView lv;
    private Context context;

    public DialogoVerEjercicios(Context con) {
        this.context=con;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        super.onCreateDialog(savedInstanceState);

        //Recogemos los elementos que ha recibido el diálogo
        if (getArguments() != null){
            usuario = getArguments().getString("usuario");
            idrutina = getArguments().getString("idrutina");
            categoria = getArguments().getString("categoria");
        }

        //Definimos un builder para construir el diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Elige un ejercicio");


        //Mediante un LayoutInflater hacemos que la vista del diálogo sea un layout que hemos diseñado en un xml
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View aspectoDialog = inflater.inflate(R.layout.dialogo_elegircategoria_aniadir, null);
        builder.setView(aspectoDialog);
        lv = aspectoDialog.findViewById(R.id.rut_ejercicios);

        //obtener ejercicios de la bd
        obtenerEjercicios();

        //se crea un ejercicio nuevo con la categoria en la que ya se esta
        builder.setPositiveButton("Crear ejercicio", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DialogoNuevaCategoria dialogo = new DialogoNuevaCategoria(context);
                Bundle args = new Bundle();
                args.putString("usuario", usuario);
                args.putString("categoria", categoria);
                args.putString("idrutina", idrutina);
                dialogo.setArguments(args);
                dialogo.show(((Rutina) getActivity()).getSupportFragmentManager(), "dialogo_nueva_categoria");
            }
        });

        //Opción de cancelar, que cierra el diálogo
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
            }
        });


        return builder.create();
    }

    //se hace la llamada a la base de datos para obtener los ejercicios de la categoria anteriormente seleccionada
    private void obtenerEjercicios(){
        StringRequest sr = new StringRequest(Request.Method.POST, "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/jwojciechowska001/WEB/entrega3/obtenerelementos.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("respuesta",response);

                //si la respuesta esta vacia imprime mensaje
                if(response.equals("null")){
                    Log.d("respuesta","no hay rutinas para ver");
                }
                else{
                    ArrayList<String> ejercicios = new ArrayList<String>();

                    //se obtiene el json en formato string que se vuelve a pasar a array de json
                    try {
                        JSONArray jsona = new JSONArray(response);
                        //para cada elemento del array que es un json

                        for (int i = 0; i < jsona.length(); i++)
                        {
                            JSONObject json = jsona.getJSONObject(i);
                            //se obtienen los datos del json y se setean para poder verse
                            ejercicios.add(json.getString("Ejercicio"));
                        }

                    }catch (Exception e){
                        //no se hace nada en caso de excepcion
                    }

                    //se crea un adapter para el listview y poder añadir los ejercicios como elementos
                    ArrayAdapter a = new ArrayAdapter<String>(((Rutina) getActivity()).getApplicationContext(), android.R.layout.simple_list_item_1, ejercicios);
                    lv.setAdapter(a);
                    a.notifyDataSetChanged();

                    rq.cancelAll("rutinas");

                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int p, long id) {
                            //si se pulsa sobre un ejercicio este se aniade a la rutina
                            aniadirEjercicio(ejercicios.get(p));
                            ((Rutina) getActivity()).actualizarLista();
                            dismiss();
                        }
                    });
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //si ha habido algun error con la solicitud
                Log.d("error", "error al obtener categorias");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //se pasan todos los parametros necesarios en la solicitud
                HashMap<String, String> parametros = new HashMap<String, String>();
                parametros.put("opcion", "obejercicioscategoria");
                parametros.put("categoria", categoria);
                parametros.put("usuario", usuario);

                return parametros;
            }
        };

        //se envia la solicitud con los parametros
        rq = Volley.newRequestQueue(((Rutina) getActivity()).getApplicationContext());
        sr.setTag("rutinas");
        rq.add(sr);
    }

    //se aniade el ejercicio a la rutina
    private void aniadirEjercicio(String nombre){
        StringRequest sr = new StringRequest(Request.Method.POST, "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/jwojciechowska001/WEB/entrega3/rutinasaniadirordenar.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("respuesta",response);
                //al ser un insert no se hace nada con la respuesta
                if (response.equals("noPosible")){
                    Toast.makeText(context,"El ejercicio ya está en la rutina",Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //si ha habido algun error con la solicitud
                Log.d("respuesta", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //se pasan todos los parametros necesarios en la solicitud
                HashMap<String, String> parametros = new HashMap<String, String>();
                parametros.put("opcion", "anejerciciorutina");
                parametros.put("idrutina", idrutina);
                parametros.put("usuario", usuario);
                parametros.put("nombre", nombre);

                return parametros;
            }
        };

        //se envia la solicitud con los parametros
        rq = Volley.newRequestQueue(((Rutina) getActivity()).getApplicationContext());
        sr.setTag("rutinas");
        rq.add(sr);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }
}
