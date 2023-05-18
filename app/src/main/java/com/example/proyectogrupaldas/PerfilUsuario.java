package com.example.proyectogrupaldas;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
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

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PerfilUsuario extends AppCompatActivity {
    private final int CODIGO_DE_PERMISOS_SACARFOTO = 1;
    private int CODIGO_FOTO_ARCHIVO = 2;
    private final int CODIGO_DE_PERMISO_LECTURA_GALERIA = 3;
    private String usuario;
    private EditText et_EmailPerfil;
    private ImageView fotoPerfil;
    private Uri uriFoto;
    private String fotoen64 = "";
    private String fotoGiro = "";


    //Se recoge la foto hecha con la cámara y se pone en el ImageView correspondiente
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODIGO_FOTO_ARCHIVO && resultCode == RESULT_OK) {
            //Tenemos la uri de la foto en el atributo uriFoto
            Log.d("fotoPerfil", "URI: " + uriFoto);

            //Convertimos la uri en Bitmap
            Bitmap bitmapFoto = null;
            try {
                bitmapFoto = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uriFoto);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            //Ajustamos la foto al ImageView
            Bitmap bitmapredimensionado = ajustarAImageView(bitmapFoto);

            //Ponemos la foto en el ImageView
            fotoPerfil.setImageBitmap(bitmapredimensionado);

            //Convertimos el Bitmap en un String en Base64 (que será lo que se suba a la base de datos cuando el usuario guarde los cambios)
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmapredimensionado.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] fototransformada = stream.toByteArray();
            fotoen64 = Base64.encodeToString(fototransformada, Base64.DEFAULT);

            //Ahora se podría lanzar un Broadcast para actualizar el repositorio multimedia donde se ha almacenado la foto sin tener que reiniciar el dispositivo
            //Pero, como es un directorio privado de la aplicación y no va a ser accedido por otras aplicaciones, no tiene sentido hacerlo

        }
        else {
            Log.d("fotoPerfil", "Problema con foto");
        }
    }



    //Launcher que recoge la imagen elegida de la galería y la pone en el ImageView correspondiente
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
        if (uri != null) {

            Log.d("fotoPerfil", "URI elegida: " + uri);

            //Convertimos la uri en Bitmap
            Bitmap bitmapFoto = null;
            try {
                bitmapFoto = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            //Ajustamos la imagen al ImageView
            Bitmap bitmapredimensionado = ajustarAImageView(bitmapFoto);

            //Ponemos la foto en el ImageView
            fotoPerfil.setImageBitmap(bitmapredimensionado);

            //Convertimos el bitmap en un String en Base64 (que será lo que se suba a la base de datos cuando el usuario guarde los cambios)
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmapredimensionado.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] fototransformada = stream.toByteArray();
            fotoen64 = Base64.encodeToString(fototransformada, Base64.DEFAULT);

        } else {
            Log.d("fotoPerfil", "No se ha elegido imagen");
        }
    });




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);

        //Recogemos el usuario
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            usuario = extras.getString("usuario");
        }

        //Recogemos los elementos de la vista
        et_EmailPerfil = (EditText) findViewById(R.id.et_EmailPerfil);
        fotoPerfil = (ImageView) findViewById(R.id.fotoPerfil);

        //Para mostrar sus datos al usuario usamos un servicio web al que accedemos con la librería Volley

        //Crear la cola de solicitudes
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        //Url del servicio web en el servidor
        String url = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/jwojciechowska001/WEB/entrega3/perfilUsuario.php";

        //Solicitud
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Procesar la respuesta del servidor

                try {
                    //La respuesta es un JSON
                    JSONParser parser = new JSONParser();
                    JSONObject json = (JSONObject) parser.parse(response);

                    //Mostramos al usuario sus datos

                    et_EmailPerfil.setText((String) json.get("email"));

                    String tieneFoto = (String) json.get("tieneFoto");

                    if (tieneFoto.equals("1")){
                        fotoen64 = (String) json.get("foto");
                    }

                    String fotoAPoner;

                    //Es posible que el usuario haya sacado una nueva foto con la cámara o haya elegido una foto de la galería,
                    // y que esa imagen no se haya subido aún a la base de datos porque el usuario no ha pulsado en "Guardar cambios".
                    //Si el usuario girara el móvil antes de subir los cambios a la base de datos, le aparecería la imagen que
                    // tenía en la base de datos y se perdería la nueva. Por eso controlamos que las fotos sean distintas.
                    if (fotoGiro!="" && fotoen64 != fotoGiro) {
                        fotoen64 = fotoGiro;
                    }
                    fotoAPoner = fotoen64;

                    if (tieneFoto.equals("0") || fotoAPoner.equals("")){
                        //El usuario no tiene foto de perfil, ponemos la imagen predefinida
                        fotoPerfil.setImageResource(R.drawable.imagenusuario);
                    }
                    else{
                        //Decodificamos la foto
                        byte[] imagenByteArray = Base64.decode(fotoAPoner, Base64.DEFAULT);
                        Bitmap imagenBitmap = BitmapFactory.decodeByteArray(imagenByteArray, 0, imagenByteArray.length);

                        //Ajustamos la foto al ImageView
                        Bitmap bitmapredimensionado = ajustarAImageView(imagenBitmap);

                        //Mostramos la foto en el ImageView
                        fotoPerfil.setImageBitmap(bitmapredimensionado);

                        //Guardamos el String en Base64 de la imagen para subirlo al servidor cuando el usuario guarde los cambios
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        imagenBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] fototransformada = stream.toByteArray();
                        fotoen64 = Base64.encodeToString(fototransformada, Base64.DEFAULT);

                    }

                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Manejar error de la solicitud
                Toast.makeText(getApplicationContext(), "Error al recoger los datos", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //Agregar los parámetros necesarios
                params.put("accion", "select");
                params.put("usuario", usuario);

                return params;
            }
        };

        //Encolar la solicitud
        queue.add(stringRequest);

    }


    //Método para actualizar los datos del usuario en la base de datos
    public void onClick_GuardarCambios(View v){

        //Usamos la librería Volley

        //Crear la cola de solicitudes
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        //Url del servicio web en el servidor
        String url = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/jwojciechowska001/WEB/entrega3/perfilUsuario.php";

        //Solicitud
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Procesar la respuesta del servidor

                //La respuesta es un String
                if (response.equals("usuarioModificado")){
                    Toast.makeText(getApplicationContext(), "Cambios guardados", Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Manejar error de la solicitud
                Toast.makeText(getApplicationContext(), "Error al guardar los cambios", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                // Agregar los parámetros necesarios
                params.put("accion", "update");
                params.put("usuario", usuario);
                params.put("email", et_EmailPerfil.getText().toString());
                params.put("foto", fotoen64);

                return params;
            }
        };

        //Encolar la solicitud
        queue.add(stringRequest);

    }



    private void lanzarIntentFoto(){
        //Antes de lanzar el Intent hay que preparar el fichero donde se guardará la foto
        //Utilizamos un FileProvider (definido en res/xml/file_provider.xml)
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String nombrefichero = "IMG_" + timeStamp + "_";
        File directorio = this.getFilesDir(); //Directorio donde almacenar la imagen, definido en el FileProvider
        //El directorio es: data/data/com.example.proyectogrupaldas/files
        //Se trata de un directorio privado de la aplicación

        File ficheroFoto = null;
        uriFoto = null;
        try{
            ficheroFoto = File.createTempFile(nombrefichero, ".jpg", directorio);
            uriFoto = FileProvider.getUriForFile(this, "com.example.proyectogrupaldas.provider", ficheroFoto);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Lanzamos el Intent para hacer la foto
        Intent intentFoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentFoto.putExtra(MediaStore.EXTRA_OUTPUT, uriFoto); //Le mandamos al Intent la uri del fichero donde se almacenará la foto
        startActivityForResult(intentFoto,CODIGO_FOTO_ARCHIVO);
    }



    private void lanzarIntentGaleria(){
        //Construimos el builder para lanzar la selección de imagen

        //Puede que Android Studio indique un error en la siguiente línea, pero el código funciona
        ActivityResultContracts.PickVisualMedia.VisualMediaType mediaType = (ActivityResultContracts.PickVisualMedia.VisualMediaType) ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE;
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(mediaType)
                .build());
    }



    //Método que sobreescribimos para gestionar la decisión del usuario tras responder al diálogo de los permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode){
            case CODIGO_DE_PERMISOS_SACARFOTO: {
                // Si la petición no se cancela, granResults no está vacío
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    lanzarIntentFoto();
                }
                else {
                    // PERMISO DENEGADO
                    Toast.makeText(getApplicationContext(), "No se puede ejecutar la funcionalidad", Toast.LENGTH_LONG).show();
                }
            }
            case CODIGO_DE_PERMISO_LECTURA_GALERIA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    lanzarIntentGaleria();
                }
                else {
                    Toast.makeText(getApplicationContext(), "No se puede ejecutar la funcionalidad", Toast.LENGTH_LONG).show();
                }
            }

        }
    }



    //Método para abrir la cámara y sacar una foto que será la foto de perfil del usuario
    public void onClick_FotoCamara(View v){
        //Necesitamos permisos de cámara y de escritura en galería
        if ( (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) ) {
            //Si ambos permisos están concedidos
            lanzarIntentFoto();
        }
        else{ //Si algún permiso, o los dos, no están concedidos
            //Pedimos los permisos
            String[] permisos = new String[] {android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
            ActivityCompat.requestPermissions(this,permisos, CODIGO_DE_PERMISOS_SACARFOTO);
        }
    }


    //Método para elegir una foto de la galería que será la foto de perfil del usuario
    public void onClick_FotoGaleria(View v){
        //Comprobamos si el permiso de lectura de galería está concedido
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //El permiso no está concedido, lo pedimos
            String[] permisos = new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this,permisos, CODIGO_DE_PERMISO_LECTURA_GALERIA);
        }
        else{
            lanzarIntentGaleria();
        }
    }



    //Método para ajustar el tamaño de la foto al tamaño del ImageView
    private Bitmap ajustarAImageView(Bitmap bitmapFoto){
        //Recogemos ancho y alto del ImageView y de la imagen
        int anchoDestino = fotoPerfil.getWidth();
        int altoDestino = fotoPerfil.getHeight();
        int anchoImagen = bitmapFoto.getWidth();
        int altoImagen = bitmapFoto.getHeight();

        //Calculamos ancho y alto finales
        float ratioImagen = (float) anchoImagen / (float) altoImagen;
        float ratioDestino = (float) anchoDestino / (float) altoDestino;
        int anchoFinal = anchoDestino;
        int altoFinal = altoDestino;
        if (ratioDestino > ratioImagen) {
            anchoFinal = (int) ((float)altoDestino * ratioImagen);
        } else {
            altoFinal = (int) ((float)anchoDestino / ratioImagen);
        }

        //Escalamos la foto al tamaño del ImageView
        Bitmap bitmapredimensionado = Bitmap.createScaledBitmap(bitmapFoto,anchoFinal,altoFinal,true);

        return bitmapredimensionado;
    }


    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        //Es posible que el usuario haya hecho una foto o haya elegido una foto de la galería y que gire el móvil antes de guardar los cambios.
        //Como después del giro se ejecuta el onCreate, se perdería la nueva foto y se mostraría la que se recoge de la base de datos.
        //Por ello, lo que debemos guardar en el Bundle es el contenido que hay actualmente en el ImageView, que no tiene por qué
        // coincidir con el que se ha recogido de la base de datos

        Drawable drawable = fotoPerfil.getDrawable();
        Bitmap bitmap;

        if (drawable instanceof BitmapDrawable){

            //Obtenemos el bitmap de la foto
            bitmap = ((BitmapDrawable) drawable).getBitmap();

            //Guardamos el String en Base64 de la foto en el atributo "fotoGiro"
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] fototransformada = stream.toByteArray();
            fotoGiro = Base64.encodeToString(fototransformada, Base64.DEFAULT);

            savedInstanceState.putByteArray("foto", fototransformada);
        }

    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        byte[] byteArrayFotoGiro = savedInstanceState.getByteArray("foto");

        //Si hay un valor es porque el usuario ha establecido una foto de perfil
        if (byteArrayFotoGiro!=null){

            //Decodificamos la imagen
            Bitmap imagenBitmap = BitmapFactory.decodeByteArray(byteArrayFotoGiro, 0, byteArrayFotoGiro.length);

            //Mostramos la imagen en el ImageView
            fotoPerfil.setImageBitmap(imagenBitmap);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imagenBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] fototransformada = stream.toByteArray();
            fotoGiro = Base64.encodeToString(fototransformada, Base64.DEFAULT);

        }
    }
}