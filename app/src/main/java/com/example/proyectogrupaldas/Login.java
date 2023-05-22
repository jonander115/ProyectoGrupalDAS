package com.example.proyectogrupaldas;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.scottyab.aescrypt.AESCrypt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    private final Activity activity=this;
    private Context context=this;
    private ArrayList<String> lineas=new ArrayList<>();

    private RequestQueue rq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //se obtienen los elementos de la vista
        EditText usuario = findViewById(R.id.is_t_usuario);
        EditText contrasenia = findViewById(R.id.is_t_contrasenia);
        Button iniciars = findViewById(R.id.is_b_iniciarsesion);
        Button registrarse = findViewById(R.id.is_b_registrarse);

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
                                Intent intent = new Intent(Login.this, PerfilUsuario.class);
                                intent.putExtra("usuario", usuario.getText().toString().trim());
                                startActivity(intent);
                                //Intent intent = new Intent(Login.this, Rutinas.class);
                                //intent.putExtra("id", usuario.getText().toString().trim());
                                //Login.this.startActivity(intent);
                                //se manda una notificación aleatoria de las posibles cmo un tutorial para el usuario
                                //finish();

                                //rq.cancelAll("login");
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


}