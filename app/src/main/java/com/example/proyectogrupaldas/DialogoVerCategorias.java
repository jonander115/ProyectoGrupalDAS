package com.example.proyectogrupaldas;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

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

public class DialogoVerCategorias extends DialogFragment {
    private RequestQueue rq;
    private String usuario;
    private String idrutina;
    ListView lv;

    private Context context;

    public DialogoVerCategorias(Context con) {
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
        }

        //Definimos un builder para construir el diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Elige una categoria");

        //Mediante un LayoutInflater hacemos que la vista del diálogo sea un layout que hemos diseñado en un xml
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View aspectoDialog = inflater.inflate(R.layout.dialogo_elegircategoria_aniadir, null);
        builder.setView(aspectoDialog);
        lv = aspectoDialog.findViewById(R.id.rut_ejercicios);

        //se obtienen las categorias mediante una llamada a la bd
        obtenerCategorias();

        //con este boton se crea una categoria nueva, y es necesario con ello aniadir un ejercicio tambien
        builder.setPositiveButton("Crear categoría", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DialogoNuevaCategoria dialogo = new DialogoNuevaCategoria(context);
                Bundle args = new Bundle();
                args.putString("usuario", usuario);
                args.putString("idrutina", idrutina);
                dialogo.setArguments(args);
                dialogo.show(((Rutina) getActivity()).getSupportFragmentManager(), "dialogo_nueva_categoria");
                obtenerCategorias();
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

    //se hace una llamada a la base de datos para coger las categorias de los ejercicios por defecto, y de los ejercicios creados por el usuario
    public void obtenerCategorias(){
        StringRequest sr = new StringRequest(Request.Method.POST, "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/jwojciechowska001/WEB/entrega3/obtenerelementos.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("respuesta",response);

                //si la respuesta esta vacia imprime mensaje
                if(response.equals("null")){
                    Log.d("respuesta","no hay rutinas para ver");
                }
                else{
                    //si no esta vacia se cogen las categorias
                    ArrayList<String> categorias = new ArrayList<String>();

                    //se obtiene el json en formato string que se vuelve a pasar a array de json
                    try {
                        JSONArray jsona = new JSONArray(response);
                        //para cada elemento del array que es un json

                        for (int i = 0; i < jsona.length(); i++)
                        {
                            JSONObject json = jsona.getJSONObject(i);
                            //se obtienen los datos del json y se setean para poder verse
                            categorias.add(json.getString("Categoria"));
                        }

                    }catch (Exception e){
                        //no se hace nada en caso de excepcion
                    }

                    //mediante un adapter de listview se muestra cada categoria como un elemento del listview
                    ArrayAdapter a = new ArrayAdapter<String>(((Rutina) getActivity()).getApplicationContext(), android.R.layout.simple_list_item_1, categorias);
                    lv.setAdapter(a);
                    //si se actualizan los datos notificarlo para que se muestren
                    a.notifyDataSetChanged();

                    rq.cancelAll("categorias");

                    //si se pulsa una categoria del listview se obtendran los ejercicios de esa categoria
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int p, long id) {
                            DialogoVerEjercicios dialogo = new DialogoVerEjercicios(context);
                            Bundle args = new Bundle();
                            // se pasan todos los datos necesarios
                            args.putString("usuario", usuario);
                            args.putString("idrutina", idrutina);
                            args.putString("categoria", categorias.get(p));
                            dialogo.setArguments(args);
                            dialogo.show(((Rutina) getActivity()).getSupportFragmentManager(), "dialogo_elegirejercicio_aniadir");
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
                parametros.put("opcion", "obcategorias");
                parametros.put("usuario", usuario);

                return parametros;
            }
        };

        //se envia la solicitud con los parametros
        rq = Volley.newRequestQueue(((Rutina) getActivity()).getApplicationContext());
        sr.setTag("categorias");
        rq.add(sr);
    }
}
