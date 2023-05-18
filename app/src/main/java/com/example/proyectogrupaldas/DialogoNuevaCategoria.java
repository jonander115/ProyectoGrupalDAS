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

        //Definimos un builder para construir el diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        if (getArguments() != null){
            usuario = getArguments().getString("usuario");
            titulo = getArguments().getString("categoria");
            idrutina = getArguments().getString("idrutina");
        }

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

        categoria=aspectoDialog.findViewById(R.id.cat_cat);
        nombre=aspectoDialog.findViewById(R.id.cat_ej);
        categoria.setText(titulo);


        //Opción de crear playlist, que añade la playlist y cierra el diálogo
        builder.setPositiveButton("Añadir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                aniadircategoriayej();
                ((Rutina) getActivity()).actualizarLista();
                //dismiss();
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

    private void aniadircategoriayej(){
        StringRequest sr = new StringRequest(Request.Method.POST, "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/jwojciechowska001/WEB/entrega3/aniadircategoriayejercicio.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("respuesta",response);

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
