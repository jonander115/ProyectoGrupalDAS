package com.example.proyectogrupaldas;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;

public class MesHistoricoAdapter extends BaseExpandableListAdapter {

    private final String usuario;
    private Context contexto;
    private LayoutInflater inflater;
    private ArrayList<String> listaMesesAños;
    private HashMap<String, ArrayList<String>> mapRutinas;




    public MesHistoricoAdapter(String usuario, Context pContexto, ArrayList<String> pListaMesesAños, HashMap<String, ArrayList<String>> pMapRutinas){
        this.usuario = usuario;
        contexto = pContexto;
        listaMesesAños = pListaMesesAños;
        mapRutinas = pMapRutinas;
    }


    @Override
    public int getGroupCount() {
        return listaMesesAños.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mapRutinas.get(listaMesesAños.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listaMesesAños.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mapRutinas.get(listaMesesAños.get(groupPosition)).get(childPosition);
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
        String textoMesAño = (String) getGroup(groupPosition);

        if (convertView == null){
            inflater = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.layout_meses, null);
        }

        //Recogemos el elemento de la vista
        TextView tv_MesAño = (TextView) convertView.findViewById(R.id.tv_MesAño);

        //Mostramos el mes y el año
        tv_MesAño.setText(textoMesAño);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String textoRutinaCompleto = (String) getChild(groupPosition, childPosition);

        String[] textoRutina = textoRutinaCompleto.split("/");

        if (convertView == null){
            inflater = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.layout_rutina, null);
        }

        //Recogemos el elemento de la vista
        TextView tv_NombreRutina = (TextView) convertView.findViewById(R.id.tv_NombreRutina);

        //Mostramos el nombre de la rutina
        tv_NombreRutina.setText(textoRutina[1]);

        //Listener para cuando el usuario haga click en la rutina
        tv_NombreRutina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Hay que abrir la actividad que muestra la información de la rutina, pasándole el usuario y el id de la rutina
                Intent intent = new Intent(v.getContext(), RutinaDeHistorico.class);
                intent.putExtra("usuario", usuario);
                intent.putExtra("idRutina",textoRutina[0]);
                v.getContext().startActivity(intent);
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


}
