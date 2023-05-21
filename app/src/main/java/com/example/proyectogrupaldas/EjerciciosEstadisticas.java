package com.example.proyectogrupaldas;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class EjerciciosEstadisticas extends AppCompatActivity {

    DatePickerDialog datePickerDialog, datePickerDialog2;
    Button dataButton, dataButton2;
    String fechaIni, fechaFin, usuario, fechaIniGiro, fechaFinGiro ;

    Integer selectedPosCat, selectedPosEjer;
    Spinner spinnerCategorias, spinnerEjercicios;
    EditText pesoMedio, mediaRepes, seriesMedias;

    ArrayList<String> listaRutinas;
    ArrayList<String> listaEjers, listaCat;
    private int year, month, day;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ejercicios_estadisticas);


        usuario = getIntent().getExtras().getString("usuario");
        Toast.makeText(getApplicationContext(), "Seleccionaste: " + usuario, Toast.LENGTH_SHORT).show();

        spinnerCategorias =findViewById(R.id.spinner_categorias);

        spinnerEjercicios =findViewById(R.id.spinner_ejercicios);

        initDataPicker();
        dataButton = findViewById(R.id.pickerFechaIni);

        initDataPicker2();
        dataButton2 = findViewById(R.id.pickerFechaFin);

        pesoMedio = findViewById(R.id.pesoMedio);
        mediaRepes = findViewById(R.id.repMedias);
        seriesMedias = findViewById(R.id.SeriesMedias);


        if (savedInstanceState != null) {
            selectedPosCat = savedInstanceState.getInt("CategoriaElegida");
            selectedPosEjer = savedInstanceState.getInt("EjerElegido");
            dataButton.setText(savedInstanceState.getString("dataButton"));
            fechaIni = savedInstanceState.getString("dataButton");
            dataButton2.setText(savedInstanceState.getString("dataButton2"));
            fechaFin = savedInstanceState.getString("dataButton2");
            listaRutinas = savedInstanceState.getStringArrayList("listaRutinas");

            LinearLayout container = findViewById(R.id.listadoRutinasLL);
            //Guardamos los datos para cada diario
            //Nombre, fecha ini, hora ini, fecha fin, hora fin
            for (int i = 0; i < listaRutinas.size(); i++) {
                    EditText textView = new EditText(EjerciciosEstadisticas.this);
                    textView.setEnabled(false);
                    textView.setText(listaRutinas.get(i));
                    container.addView(textView);
            }
            container.post(new Runnable() {
                @Override
                public void run() {
                    // Ajustar el tamaño del contenedor al contenido
                    container.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    container.requestLayout();
                }
            });

        }

        cargarCatEjers(spinnerCategorias,spinnerEjercicios);



    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        int selectedPosition = spinnerCategorias.getSelectedItemPosition();
        int selectedPosition2 = spinnerEjercicios.getSelectedItemPosition();
        Log.d("selectedPos", String.valueOf(selectedPosition));
        savedInstanceState.putInt("CategoriaElegida", selectedPosition);
        savedInstanceState.putInt("EjerElegido", selectedPosition2);
        savedInstanceState.putCharSequence("dataButton", dataButton.getText());
        savedInstanceState.putCharSequence("dataButton2", dataButton2.getText());
        savedInstanceState.putStringArrayList("listaRutinas", listaRutinas);


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
        RequestQueue rq = Volley.newRequestQueue(EjerciciosEstadisticas.this);

        //Definimos la URL a la que se va a hacer peticiones
        StringRequest sr = new StringRequest(Request.Method.POST, "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/jwojciechowska001/WEB/entrega3/estadisticas.php", new Response.Listener<String>() {
            @Override //En caso de exito
            public void onResponse(String response) {

                Log.d("respuesta", response);

                ArrayList<String> listaDatos = new ArrayList<String>();

                try {
                    //{"idRutina":"4","peso":"3","nombreRutina":"Tren Superior","numRepes":"3"}
                    //Convertimos la respuesta en JSON para poder recorrerla
                    JSONArray json = new JSONArray(response);

                    ArrayList<ArrayList<String>> listaListas = new ArrayList<ArrayList<String>>();
                    ArrayList<Integer> listaPesos = new ArrayList<>();
                    ArrayList<Integer> listaRepes = new ArrayList<>();
                    ArrayList<Integer> listaSeries = new ArrayList<>();
                    listaRutinas = new ArrayList<>();
                    String rutina;
                    listaDatos.add("Elige una rutina");
                    LinearLayout container = findViewById(R.id.listadoRutinasLL);
                    //Guardamos los datos para cada diario
                    //Nombre, fecha ini, hora ini, fecha fin, hora fin
                    for (int i = 0; i < json.length(); i++) {

                        listaPesos.add(Integer.valueOf(json.getJSONObject(i).getString("peso")));
                        listaRepes.add(Integer.valueOf(json.getJSONObject(i).getString("numRepes")));
                        listaSeries.add(Integer.valueOf(json.getJSONObject(i).getString("idRutina")));
                        rutina = json.getJSONObject(i).getString("nombreRutina");
                        if(!listaRutinas.contains(rutina)){
                            listaRutinas.add(rutina);
                            EditText textView = new EditText(EjerciciosEstadisticas.this);
                            textView.setEnabled(false);
                            textView.setText(rutina);
                            container.addView(textView);
                        }
                    }
                    container.post(new Runnable() {
                        @Override
                        public void run() {
                            // Ajustar el tamaño del contenedor al contenido
                            container.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                            container.requestLayout();
                        }
                    });
                    float media = listaPesos.stream().mapToInt(Integer::intValue).sum();
                    pesoMedio.setText(String.valueOf(media/listaPesos.size()) + " Kg");

                    media = listaRepes.stream().mapToInt(Integer::intValue).sum();
                    mediaRepes.setText(String.valueOf(media/listaRepes.size()) + " repeticiones");

                    double mediaSeries = listaSeries.stream()
                            .collect(Collectors.groupingBy(Integer::intValue, Collectors.counting()))
                            .values().stream()
                            .mapToLong(Long::intValue)
                            .average()
                            .orElse(0);

                    seriesMedias.setText(String.valueOf(mediaSeries) + " series");

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
                Toast.makeText(EjerciciosEstadisticas.this, "Se ha producido un error", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Definicion de los parametros necesarios para realizar la peticion
                String ejer = (String) spinnerEjercicios.getSelectedItem();
                HashMap<String, String> parametros = new HashMap<String, String>();
                parametros.put("usuario", usuario);
                parametros.put("ejercicio", ejer);
                parametros.put("tarea", "estadisticasEjers");
                parametros.put("fechaIni", fechaIni);
                parametros.put("fechaFin", fechaFin);
                return parametros;
            }
        };

        //Enviamos la peticion
        rq = Volley.newRequestQueue(EjerciciosEstadisticas.this);
        rq.add(sr);

    }


    private void cargarCatEjers(Spinner spinner, Spinner spinnerEjer) {

        //Documentacion de volley : https://google.github.io/volley/


        //Inicializamos la cola de peticiones
        RequestQueue rq = Volley.newRequestQueue(EjerciciosEstadisticas.this);

        //Definimos la URL a la que se va a hacer peticiones
        StringRequest sr = new StringRequest(Request.Method.POST, "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/jwojciechowska001/WEB/entrega3/estadisticas.php", new Response.Listener<String>() {
            @Override //En caso de exito
            public void onResponse(String response) {

                listaCat = new ArrayList<String>();
                listaEjers = new ArrayList<String>();
                Log.d("respuesta", response);

                try {
                    //Convertimos la respuesta en JSON para poder recorrerla
                    JSONArray json = new JSONArray(response);


                    listaCat.add("Elige una categoria");
                    System.out.print(json);
                    String nombreCat = "";
                    String nombreEjer = "";
                    //Guardamos los datos para cada diario
                    for (int i = 0; i < json.length(); i++) {
                        nombreCat = json.getJSONObject(i).getString("categoria");
                        nombreEjer = json.getJSONObject(i).getString("nombreEjer");
                        if (!listaCat.contains(nombreCat)){
                            listaCat.add(nombreCat);
                        }

                        if (!listaEjers.contains(nombreEjer)){
                            listaEjers.add(nombreEjer);
                        }

                    }


                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(EjerciciosEstadisticas.this, android.R.layout.simple_spinner_item, listaCat);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);

                    if(selectedPosCat != null){
                        spinner.setSelection(selectedPosCat);
                    }


                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            // Obtén el valor seleccionado en el primer spinner
                            String opcionSeleccionada = (String) parent.getItemAtPosition(position);

                            // Obtén los datos para el segundo spinner basados en la opción seleccionada
                            ArrayList<String> opcionesSpinner2 = null;
                            try {
                                opcionesSpinner2 = obtenerOpcionesSpinnerEjers(opcionSeleccionada, json);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }


                            ArrayAdapter<String> adapter2 = new ArrayAdapter<>(EjerciciosEstadisticas.this, android.R.layout.simple_spinner_item, opcionesSpinner2);
                            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerEjer.setAdapter(adapter2);

                            if(selectedPosEjer != null){
                                spinnerEjer.setSelection(selectedPosEjer);
                            }

                        }

                        private ArrayList<String> obtenerOpcionesSpinnerEjers(String opcionSeleccionada, JSONArray listaDatos) throws JSONException {

                            String ejer = "";
                            listaEjers = new ArrayList<String>();
                            for (int i = 0; i < listaDatos.length(); i++) {
                                ejer = listaDatos.getJSONObject(i).getString("nombreEjer");
                                if (!listaEjers.contains(ejer) && listaDatos.getJSONObject(i).getString("categoria").equals(opcionSeleccionada)){
                                    listaEjers.add(ejer);
                                }
                            }
                            return listaEjers;
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            // No se seleccionó ninguna opción en el primer spinner
                        }
                    });



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
                Toast.makeText(EjerciciosEstadisticas.this, "Se ha producido un error", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Definicion de los parametros necesarios para realizar la peticion

                HashMap<String, String> parametros = new HashMap<String, String>();
                parametros.put("usuario", usuario);
                parametros.put("tarea", "spinnerEjer");
                return parametros;
            }
        };

        //Enviamos la peticion
        rq = Volley.newRequestQueue(EjerciciosEstadisticas.this);
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