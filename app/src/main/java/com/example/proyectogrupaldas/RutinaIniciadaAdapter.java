package com.example.proyectogrupaldas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class RutinaIniciadaAdapter extends BaseExpandableListAdapter {

    private final String usuario;
    private Context contexto;
    private LayoutInflater inflater;
    private ArrayList<String> listaEjercicios;
    private HashMap<String, ArrayList<String>> mapSeries;

    public RutinaIniciadaAdapter(String usuario, Context pContexto, ArrayList<String> pListaEjercicios, HashMap<String, ArrayList<String>> pMapSeries) {
        this.usuario = usuario;
        contexto = pContexto;
        listaEjercicios = pListaEjercicios;
        mapSeries = pMapSeries;
    }
    @Override
    public int getGroupCount() {
        return listaEjercicios.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mapSeries.get(listaEjercicios.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listaEjercicios.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mapSeries.get(listaEjercicios.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String textoNombreEjercicio = (String) getGroup(groupPosition);

        if (convertView == null){
            inflater = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.layout_ejercicio_rutina_iniciada, null);
        }

        //Recogemos los elementos de la vista
        TextView tv_EjercicioDeRutinaIniciada = (TextView) convertView.findViewById(R.id.tv_EjercicioDeRutinaIniciada);
        Button bt_AniadirSerie = (Button) convertView.findViewById(R.id.bt_AniadirSerie);

        //Mostramos el nombre del ejercicio
        tv_EjercicioDeRutinaIniciada.setText(textoNombreEjercicio);

        //Listener para abrir el diálogo que permite añadir una nueva serie del ejercicio
        bt_AniadirSerie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //abrir dialogode javi
            }
        });

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String textoSerieCompleto = (String) getChild(groupPosition, childPosition);

        String[] textoSerie = textoSerieCompleto.split("/");

        if (convertView == null){
            inflater = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.layout_serie, null);
        }

        //Recogemos los elementos de la vista
        TextView tv_NumSerie = (TextView) convertView.findViewById(R.id.tv_NumSerie);
        TextView tv_Peso = (TextView) convertView.findViewById(R.id.tv_Peso);
        TextView tv_Repeticiones = (TextView) convertView.findViewById(R.id.tv_Repeticiones);
        TextView tv_Notas = (TextView) convertView.findViewById(R.id.tv_Notas);

        //Mostramos los datos de la serie
        tv_NumSerie.setText(textoSerie[0]);

        if (!textoSerie[1].equals("null")){
            tv_Peso.setText(textoSerie[1]);
        }
        else{
            tv_Peso.setText("-");
        }

        if (!textoSerie[2].equals("null")){
            tv_Repeticiones.setText(textoSerie[2]);
        }
        else{
            tv_Repeticiones.setText("-");
        }

        if (!textoSerie[3].equals("null")){
            tv_Notas.setText(textoSerie[3]);
        }
        else{
            tv_Notas.setText("-");
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
