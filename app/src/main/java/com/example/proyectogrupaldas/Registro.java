package com.example.proyectogrupaldas;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.scottyab.aescrypt.AESCrypt;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Registro extends AppCompatActivity {

    private Context context = this;
    private Activity activity = this;

    private RequestQueue rq;
    private EditText usuario, email, contrasenia;
    private ImageView vistaimagen;
    private Uri imagen;
    private Bitmap bimagen;
    private String b64;
    private String nombreimagen;

    private static final int IMAGE_CODE=112;
    private static final int PHOTO_CODE=111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        //obtener datos de la vista
        usuario = findViewById(R.id.r_tv_usuario);
        contrasenia = findViewById(R.id.r_t_contrasenia);
        email = findViewById(R.id.r_t_email);
        vistaimagen = findViewById(R.id.r_iv_previafoto);

        //boton elegir imagen de la galeria
        Button elegirfoto = findViewById(R.id.r_b_elegirfoto);
        elegirfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mirar si la version es mayor o igual a marshmallow
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //si lo es mirar si se han dado los permisos de lectura
                    if (ActivityCompat.checkSelfPermission(Registro.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        //si no hay permisos de lectura darselos
                        ActivityCompat.requestPermissions(Registro.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, IMAGE_CODE);
                    } else {
                        //si ya hay permisos
                        elegirfoto();
                    }
                } else {
                    //si la version es menor a marshmallow
                    elegirfoto();
                }
            }
        });

        //boton sacar foto con la camara
        Button sacarfoto = findViewById(R.id.r_b_sacarfoto);
        sacarfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //se ha de mirar primero si la version es igual o superior a la marshmallow
                //si es igual o superior
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //si no hay permisos para acceder a la cámara, o escribir
                    if (ActivityCompat.checkSelfPermission(Registro.this,android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(Registro.this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        //dar permisos de camara y escritura
                        ActivityCompat.requestPermissions(Registro.this,new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PHOTO_CODE);
                    } else {
                        //si ya hay permisos se abre la camara
                        abrirCamara();
                    }
                }
                //si la version es menor a la marshmallow
                else {
                    abrirCamara();
                }
            }
        });

        Button boton = findViewById(R.id.r_b_registrarse);
        //pulsar para registrarse
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //si hay algun campo vacio no se deja registrar y manda un mensaje
                if (usuario.getText().toString().trim().equals("") || contrasenia.getText().toString().trim().equals("") || email.getText().toString().trim().equals("")) {
                    Toast.makeText(context, "Por favor introduce todos tus datos, serán obligatorios para el registro.", Toast.LENGTH_SHORT).show();
                } else {
                        //miramos si el email es valido mediante regex
                        java.util.regex.Pattern rp = java.util.regex.Pattern.compile("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$");
                        java.util.regex.Matcher rm = rp.matcher(email.getText().toString().trim());

                        //si el email es valido (ej: gym@gmail.com)
                        if (rm.matches()) {
                            //se mira si el usuario con ese nombre ya existe

                            //si el usuario existe se mira que haya una imagen elegida o sacada
                            if (imagen != null) {
                                //para la imagen de uri a bitmap
                                try {
                                    bimagen = MediaStore.Images.Media.getBitmap(getContentResolver(), imagen);
                                } catch (IOException e) {
                                    //no hace nada
                                }
                            }
                            else{
                                bimagen = BitmapFactory.decodeResource(context.getResources(), R.drawable.imagenusuario);
                            }

                            //convertir el bitmap de la imagen en base64
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bimagen.compress(Bitmap.CompressFormat.PNG, 100, baos);
                            byte[] b = baos.toByteArray();
                            b64 = Base64.encodeToString(b, Base64.DEFAULT);

                                //encriptar contrasenia

                                    /*Basado en el código extraído de Stack Overflow
                                     Pregunta: https://stackoverflow.com/questions/41223937/how-can-i-encrypte-my-password-android-studio
                                     Respuesta: https://stackoverflow.com/a/60652350
                                     Modificado para cambiar la password de encriptado
                                     */
                                String  cencriptada="";
                                //se encripta la contasenia
                                try {
                                    cencriptada = AESCrypt.encrypt("DASGRUPAL", contrasenia.getText().toString().trim());
                                }catch (Exception e){
                                    //no hace nada
                                }
                                String finalCencriptada = cencriptada;

                                //se hace una peticion POST al servidor para registrar un usuario
                                StringRequest sr = new StringRequest(Request.Method.POST, "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/jwojciechowska001/WEB/entrega3/registro.php", new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        if (!response.equals("sucess")) {
                                            //si la solicitud se ha procesado correctamente
                                            finish();
                                            rq.cancelAll("registro");
                                        }
                                    }
                                }, new Response.ErrorListener() {

                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        //si ha habido algun error con la solicitud
                                        Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                                        //Toast.makeText(context, "No se ha podido registrar el usuario", Toast.LENGTH_SHORT).show();
                                    }
                                }) {
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        //se pasan todos los parametros necesarios en la solicitud
                                        HashMap<String, String> parametros = new HashMap<String, String>();
                                        parametros.put("usuario", usuario.getText().toString().trim());
                                        parametros.put("password", finalCencriptada);
                                        parametros.put("email", email.getText().toString().trim());
                                        parametros.put("foto", b64);

                                        return parametros;
                                    }
                                };

                                //se envia la solicitud con los parametros
                                rq = Volley.newRequestQueue(context);
                                sr.setTag("registro");
                                rq.add(sr);

                        } else {
                            //mensaje para email incorrecto
                            Toast.makeText(context, "Por favor, introduce un email válido.", Toast.LENGTH_SHORT).show();
                        }
                }
            }
        });
    }

    //metodo para cuando se usa la camara
    private void abrirCamara() {
        ContentValues cv = new ContentValues();
        //informacion de la imagen
        cv.put(MediaStore.Images.Media.TITLE, "Nueva Imagen");
        cv.put(MediaStore.Images.Media.DESCRIPTION, "Nueva Imagen sacada con la cámara");
        //uri de la imagen
        imagen = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
        //crear intent para la camara
        Intent camarai = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camarai.putExtra(MediaStore.EXTRA_OUTPUT, imagen);
        startActivityForResult(camarai, 1111);
    }

    //metodo para cuando se elige foto de la galeria
    private void elegirfoto() {
        //crear intent para la galeria
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1112);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //si se esta sacando una foto
        if(requestCode==PHOTO_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //se han dado los permisos necesarios
                abrirCamara();
            } else {
                //no se han dado los permisos necesarios
                Toast.makeText(this, "No se han aceptado los permisos", Toast.LENGTH_SHORT).show();
            }
        }
        //si se esta eligiendo una foto de la galeria
        if(requestCode==IMAGE_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //se han dado los permisos necesarios
                elegirfoto();
            } else {
                //no se han dado los permisos necesarios
                Toast.makeText(this, "No se han aceptado los permisos", Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //cuando ya se ha sacado la foto
        if (resultCode == RESULT_OK && requestCode == 1111) {
            //ver vista previa de la imagen
            vistaimagen.setImageURI(imagen);
            //guardar la imagen en bitmap para luego subirla a la bd

            //encontrar directorio de la galeria
            File directorio = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

            //crear nombre de la foto sacada
            String tiempo = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            nombreimagen = "IMG_" + tiempo + ".png";
            File imagenfinal = new File(directorio, nombreimagen);

            try {
                //guardar la foto en un file y enviarla a la galeria
                FileOutputStream fos = new FileOutputStream(imagenfinal);
                bimagen.compress(Bitmap.CompressFormat.PNG, 100, fos);

                //ByteArrayOutputStream baos = new ByteArrayOutputStream();
                //byte[] b = baos.toByteArray();
                //b64 = Base64.encodeToString(b, Base64.DEFAULT);
                fos.flush();
                fos.close();
                //crear intent para que guarde la informacion de la imagen
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.fromFile(imagenfinal));
                sendBroadcast(intent);

                Toast.makeText(this, "Se ha guardado la imagen en la galería", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                //no hace nada
            }

        }

        //cuando ya se ha elegido la foto de la galeria
        if (resultCode == RESULT_OK && requestCode == 1112) {
            //visualizar la imagen
            imagen = data.getData();
            vistaimagen.setImageURI(imagen);

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //si la uri de imagen no esta vacia se guarda
        if(imagen != null && !imagen.equals(Uri.EMPTY)) {
            outState.putParcelable("imagen", imagen);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //se recupera la uri y se setea de nuevo para verse
        imagen=savedInstanceState.getParcelable("imagen");
        vistaimagen.setImageURI(savedInstanceState.getParcelable("imagen"));
    }
}