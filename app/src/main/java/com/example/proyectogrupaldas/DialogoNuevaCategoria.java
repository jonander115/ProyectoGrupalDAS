package com.example.proyectogrupaldas;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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

public class DialogoNuevaCategoria extends DialogFragment {
    private RequestQueue rq;
    private EditText categoria,nombre;
    private String usuario, titulo, idrutina;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        //Definimos un builder para construir el dialogo
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //obtener los datos pasados al dialogo
        if (getArguments() != null){
            usuario = getArguments().getString("usuario");
            titulo = getArguments().getString("categoria");
            idrutina = getArguments().getString("idrutina");
        }

        //definir titulo para el dialogo
        //si no se pasa titulo significa que se quiere crear una categoria nueva
        //si se pasa titulo significa que se ha elegido una categoria y se crea un ejercicio
        if (titulo == null){
            builder.setTitle("Es necesario crear un ejercicio para poder crear la categoría");
        }
        else{
            builder.setTitle("Escribe el nombre del ejercicio");
        }

        //Mediante un LayoutInflater hacemos que la vista del diálogo sea un layout que hemos diseñado en un xml
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View aspectoDialog = inflater.inflate(R.layout.dialogo_ejercicio_nuevo, null);
        builder.setView(aspectoDialog);

        //obtener elementos de la vista
        categoria=aspectoDialog.findViewById(R.id.cat_cat);
        nombre=aspectoDialog.findViewById(R.id.cat_ej);
        categoria.setText(titulo);


        //boton añadir para terminar de aniadir el ejercicio
        builder.setPositiveButton("Añadir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (categoria.getText().toString().length() == 0 || nombre.getText().toString().length() == 0) {
                    Toast.makeText(getContext(), "Debes introducir datos para poder añadir un ejercicio", Toast.LENGTH_LONG).show();
                }
                else{
                    aniadircategoriayej();
                    //actualizar la lista con el ejercicio nuevo aniadido
                    ((Rutina) getActivity()).actualizarLista();
                    //dismiss();
                }
            }
        });

        //cancelar cierra el dialogo
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
            }
        });

        return builder.create();
    }

    //se aniade el ejercicio con su correspondiente categoria, independientemente de si la categoria se ha creado o seleccionado
    private void aniadircategoriayej(){
        StringRequest sr = new StringRequest(Request.Method.POST, "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/jwojciechowska001/WEB/entrega3/aniadircategoriayejercicio.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("respuesta",response);
                //como es un insert en la db, no se hace nada con la respuesta
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
                parametros.put("categoria", categoria.getText().toString());
                parametros.put("ejercicio", nombre.getText().toString());
                parametros.put("usuario", usuario);
                parametros.put("idrutina", idrutina);
                return parametros;
            }
        };

        //se envia la solicitud con los parametros
        rq = Volley.newRequestQueue(((Rutina) getActivity()).getApplicationContext());
        sr.setTag("categorias");
        rq.add(sr);
    }
}
