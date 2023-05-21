package com.example.proyectogrupaldas;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Locale;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.sql.DataTruncation;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class RutinasEstadistica extends AppCompatActivity {

    DatePickerDialog datePickerDialog, datePickerDialog2;
    Button dataButton, dataButton2;
    String fechaIni, fechaFin, usuario;
    Integer selectedPosRut;
    Spinner spinnerRutinas;
    EditText numRep, tiempoMedio, diaComun;

    private int year, month, day;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rutinas_estadistica);

        usuario = getIntent().getExtras().getString("usuario");
        Toast.makeText(getApplicationContext(), "Seleccionaste: " + usuario, Toast.LENGTH_SHORT).show();
        spinnerRutinas=findViewById(R.id.spinner_rutinas);
        cargarRutinas(spinnerRutinas);

        initDataPicker();
        dataButton = findViewById(R.id.datePickerButton2);

        initDataPicker2();
        dataButton2 = findViewById(R.id.datePickerButton);

        numRep = findViewById(R.id.NumeroRepeticionesRutina);
        tiempoMedio = findViewById(R.id.EstadMinsMedioRut);
        diaComun = findViewById(R.id.EstadDiaMasComun);

        if (savedInstanceState != null) {
            selectedPosRut = savedInstanceState.getInt("CategoriaElegida");

            dataButton.setText(savedInstanceState.getString("dataButton"));
            fechaIni = savedInstanceState.getString("dataButton");
            dataButton2.setText(savedInstanceState.getString("dataButton2"));
            fechaFin = savedInstanceState.getString("dataButton2");

        }
    }

    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        int selectedPosition = spinnerRutinas.getSelectedItemPosition();
        Log.d("selectedPos", String.valueOf(selectedPosition));
        savedInstanceState.putCharSequence("dataButton", dataButton.getText());
        savedInstanceState.putCharSequence("dataButton2", dataButton2.getText());
        savedInstanceState.putInt("RutinaElegida", selectedPosition);

    }

    private void initDataPicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                String date = makeDateString(i2, i1+1, i);
                dataButton.setText(date);
                fechaIni = date;
            }
        };

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);


        datePickerDialog2 = new DatePickerDialog(this,R.style.DatePickerDialogMaterialStyle, dateSetListener, year, month,day);
        datePickerDialog2.getDatePicker().setMaxDate(System.currentTimeMillis());
    }

    private void initDataPicker2() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                String date = makeDateString(i2,i1+1, i);
                dataButton2.setText(date);
                fechaFin = date;

            }
        };

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);


        datePickerDialog = new DatePickerDialog(this,R.style.DatePickerDialogMaterialStyle, dateSetListener, year, month,day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
    }

    private String makeDateString(int day, int month, int year) {

        return day + "/" + month + "/" + year;
    }

    public void openDatePicker(View view){

        datePickerDialog.show();
    }

    public void openDatePicker2(View view){

        datePickerDialog2.show();
    }

    public void cargarEstadisticas(View view) {

        //Documentacion de volley : https://google.github.io/volley/


        //Inicializamos la cola de peticiones
        RequestQueue rq = Volley.newRequestQueue(RutinasEstadistica.this);

        //Definimos la URL a la que se va a hacer peticiones
        StringRequest sr = new StringRequest(Request.Method.POST, "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/jwojciechowska001/WEB/entrega3/estadisticas.php", new Response.Listener<String>() {
            @Override //En caso de exito
            public void onResponse(String response) {

                Log.d("respuesta", response);
                ArrayList<String> listaDatos = new ArrayList<String>();
                try {
                    //[{"nombre":"Rutina Pierna","inicio":"2023-05-16 13:08:00","fin":"2023-05-16 14:18:20"}]
                    //Convertimos la respuesta en JSON para poder recorrerla
                    JSONArray json = new JSONArray(response);

                    ArrayList<ArrayList<String>> listaListas = new ArrayList<ArrayList<String>>();
                    ArrayList<Integer> listaMinutos = new ArrayList<>();
                    ArrayList<String> listaDias = new ArrayList<String>();
                    listaDatos.add("Elige una rutina");

                    //Guardamos los datos para cada diario
                    //Nombre, fecha ini, hora ini, fecha fin, hora fin
                    for (int i = 0; i < json.length(); i++) {

                        String[] parts = json.getJSONObject(i).getString("inicio").split(" ");
                        String date = parts[0];
                        String time = parts[1];

                        parts = json.getJSONObject(i).getString("fin").split(" ");
                        String date2 = parts[0];
                        String time2 = parts[1];

                        LocalTime hora1 = LocalTime.parse(time);
                        LocalTime hora2 = LocalTime.parse(time2);

                        System.out.println("hora1: " + hora1);
                        System.out.println("hora2: " + hora2);
                        long minutos = ChronoUnit.MINUTES.between(hora1, hora2);
                        listaMinutos.add((int) minutos);

                        DateTimeFormatter formateador = DateTimeFormatter.ofPattern("EEEE", new Locale("es", "ES"));
                        String diaSemana = formateador.format(LocalDate.parse(date));

                        listaDias.add(diaSemana);

                    }

                    numRep.setText("Numero de veces que has hecho la rutina: " + String.valueOf(json.length()));
                    int suma = listaMinutos.stream().mapToInt(Integer::intValue).sum();
                    tiempoMedio.setText("Tiempo medio:" + String.valueOf(suma/listaMinutos.size()) + "min");

                    String valorMasRepetido = listaDias.stream()
                            .collect(Collectors.groupingBy(e -> e, Collectors.counting()))
                            .entrySet()
                            .stream()
                            .max(Comparator.comparingLong(e -> e.getValue()))
                            .map(e -> e.getKey())
                            .orElse(null);

                    Log.d("dias", valorMasRepetido);
                    diaComun.setText("Dia mas comun: " + String.valueOf(valorMasRepetido));

                } catch(JSONException e){
                    System.out.print("Estoy dentro del catch");
                }catch(Exception e) {
                    System.out.print("Ha ocurrido un error: " + e.getMessage());
                }

            }
        }, new Response.ErrorListener() {
            @Override //En caso de error
            public void onErrorResponse(VolleyError error) {
                //Notificamos al usuario
                Toast.makeText(RutinasEstadistica.this, "Se ha producido un error", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Definicion de los parametros necesarios para realizar la peticion

                HashMap<String, String> parametros = new HashMap<String, String>();
                parametros.put("usuario", usuario);
                parametros.put("tarea", "estadisticas");
                parametros.put("rutina", (String) spinnerRutinas.getSelectedItem());
                parametros.put("fechaIni", fechaIni);
                parametros.put("fechaFin", fechaFin);
                return parametros;
            }
        };

        //Enviamos la peticion
        rq = Volley.newRequestQueue(RutinasEstadistica.this);
        rq.add(sr);

    }
    private void cargarRutinas(Spinner spinnerRutinas) {

        //Documentacion de volley : https://google.github.io/volley/

        //Inicializamos la cola de peticiones
        RequestQueue rq = Volley.newRequestQueue(RutinasEstadistica.this);

        //Definimos la URL a la que se va a hacer peticiones
        StringRequest sr = new StringRequest(Request.Method.POST, "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/jwojciechowska001/WEB/entrega3/estadisticas.php", new Response.Listener<String>() {
            @Override //En caso de exito
            public void onResponse(String response) {

                String respuesta=response;
                ArrayList<String> listaDatos = new ArrayList<String>();
                Log.d("respuesta", respuesta);
                try {
                    //Convertimos la respuesta en JSON para poder recorrerla
                    JSONArray json = new JSONArray(respuesta);

                    listaDatos.add("Elige una rutina");
                    System.out.print(json);
                    //Guardamos los datos para cada diario
                    for (int i = 0; i < json.length(); i++) {

                        listaDatos.add(json.getJSONObject(i).getString("nombre"));


                    }


                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(RutinasEstadistica.this, android.R.layout.simple_spinner_item, listaDatos);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerRutinas.setAdapter(adapter);


                } catch(JSONException e){
                    System.out.print("Estoy dentro del catch");
                }catch(Exception e) {
                    System.out.print("Ha ocurrido un error: " + e.getMessage());
                }


            }
        }, new Response.ErrorListener() {
            @Override //En caso de error
            public void onErrorResponse(VolleyError error) {
                //Notificamos al usuario
                Toast.makeText(RutinasEstadistica.this, "Se ha producido un error", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Definicion de los parametros necesarios para realizar la peticion

                HashMap<String, String> parametros = new HashMap<String, String>();
                parametros.put("usuario", usuario);
                parametros.put("tarea", "listadoRutinas");
                return parametros;
            }
        };

        //Enviamos la peticion
        rq = Volley.newRequestQueue(RutinasEstadistica.this);
        rq.add(sr);

    }

    public void onBackPressed() {
        //Volvemos a la lista de diarios
        Intent intent = new Intent(this, MenuEstadisticas.class);
        intent.putExtra("usuario", usuario);
        startActivity(intent);
        finish();
    }
}