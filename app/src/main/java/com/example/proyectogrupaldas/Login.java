package com.example.proyectogrupaldas;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.scottyab.aescrypt.AESCrypt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    private final Activity activity=this;
    private Context context=this;
    private ArrayList<String> lineas=new ArrayList<>();
    private final int CODIGO_DE_PERMISO_NOTIFICACIONES = 10;

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
        setContentView(R.layout.activity_login);

        //se obtienen los elementos de la vista
        EditText usuario = findViewById(R.id.is_t_usuario);
        EditText contrasenia = findViewById(R.id.is_t_contrasenia);
        Button iniciars = findViewById(R.id.is_b_iniciarsesion);
        Button registrarse = findViewById(R.id.is_b_registrarse);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            //Comprobamos si el permiso de notificaciones está concedido
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {

                //El permiso no está concedido, lo pedimos
                String[] permisos = new String[]{android.Manifest.permission.POST_NOTIFICATIONS};
                ActivityCompat.requestPermissions(this, permisos, CODIGO_DE_PERMISO_NOTIFICACIONES);
            }
        }

        iniciars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //si alguno de los campos es distinto del vacio
                if(!usuario.getText().toString().trim().equals("") || !contrasenia.getText().toString().trim().equals("")) {
                    //se encripta la contrasenia

                    /*Basado en el código extraído de Stack Overflow
                     Pregunta: https://stackoverflow.com/questions/41223937/how-can-i-encrypte-my-password-android-studio
                     Respuesta: https://stackoverflow.com/a/60652350
                     Modificado para cambiar la password de encriptado
                     */


                    String  cencriptada="";
                    //se encripta la contrasenia introducida para ver si coincide con la encriptada en la base de datos
                    try {
                        cencriptada = AESCrypt.encrypt("DASGRUPAL", contrasenia.getText().toString().trim());
                    }catch (Exception e){
                        //no hace nada
                    }
                    String finalCencriptada = cencriptada;

                    //se hace una peticion POST al servidor para registrar un usuario
                    StringRequest sr = new StringRequest(Request.Method.POST, "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/jwojciechowska001/WEB/entrega3/login.php", new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            String respuesta=response.toString();

                            //si la respuesta esta vacia imprime mensaje
                            if(respuesta.isEmpty()){
                                Toast.makeText(context, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                //si no esta vacia significa que existe el usuario con ese usuario y contrasenia y se abre la siguiente actividad
                                //Intent intent = new Intent(Login.this, PerfilUsuario.class);
                                //intent.putExtra("usuario", usuario.getText().toString().trim());
                                //startActivity(intent);
                                Intent intent = new Intent(Login.this, Controlador_Barra_Navegacion.class);
                                intent.putExtra("usuario", usuario.getText().toString().trim());
                                guardarToken();
                                Login.this.startActivity(intent);
                                //se manda una notificación aleatoria de las posibles cmo un tutorial para el usuario
                                finish();

                                rq.cancelAll("login");
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //si ha habido algun error con la solicitud
                            Toast.makeText(context, "Se ha producido un error", Toast.LENGTH_SHORT).show();
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            //se pasan todos los parametros necesarios en la solicitud
                            HashMap<String, String> parametros = new HashMap<String, String>();
                            parametros.put("usuario", usuario.getText().toString().trim());
                            parametros.put("password", finalCencriptada);

                            return parametros;
                        }
                    };

                    //se envia la solicitud con los parametros
                    rq = Volley.newRequestQueue(context);
                    sr.setTag("login");
                    rq.add(sr);
                }
            }
        });

        registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //se abre la actividad de registro
                Intent intent=new Intent(Login.this, Registro.class);
                intent.putExtra("usuario", usuario.getText().toString().trim());
                activity.startActivityForResult(intent, 1);
            }
        });
    }

    //metodo para que se actualice la bd con el usuario que se ha registrado nuevo y así no de error la aplicación
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==1){
            recreate();
        }
    }

    private void guardarToken(){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()){
                    task.getException();
                    return;
                }
                else{
                    String token = task.getResult();
                    Log.d("token",token);
                    subirtoken(token);
                }
            }
        });
    }

    private void subirtoken(String token){
        //Se lanza una notificación de bienvenida mediante Firebase
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        //Url del servicio web en el servidor
        String url = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/jwojciechowska001/WEB/entrega3/aniadirtoken.php";

        //Solicitud
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Procesar la respuesta del servidor
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Manejar error de la solicitud
                Log.d("firebase","error de servicio firebase");
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                // Agregar los parámetros necesarios para la solicitud
                params.put("token",token);
                return params;
            }
        };
        //Encolar la solicitud
        queue.add(stringRequest);
    }

    //Método que sobreescribimos para gestionar la decisión del usuario tras responder al diálogo de los permisos
    //Los permisos de notificaciones solo se piden si la versión es mayor o igual a Android 13
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode) {

            case CODIGO_DE_PERMISO_NOTIFICACIONES: {
                // Si la petición se cancela, granResults estará vacío
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permiso concedido, se pueden recibir notificaciones
                } else {
                    // PERMISO DENEGADO
                    Toast.makeText(getApplicationContext(), "No se pueden recibir notificaciones", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }


}