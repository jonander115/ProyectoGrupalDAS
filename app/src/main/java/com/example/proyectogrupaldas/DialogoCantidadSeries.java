package com.example.proyectogrupaldas;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DialogoCantidadSeries extends DialogFragment {
    private EditText peso, num_rep, notas;
    private String usuario, ejercicio, idRutina;


    @Nullable
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        if (getArguments() != null){
            usuario = getArguments().getString("usuario");
            ejercicio = getArguments().getString("ejercicio");
            idRutina = getArguments().getString("idRutina");
        }

        LayoutInflater inflater= getActivity().getLayoutInflater();
        View aspectoDialog=inflater.inflate(R.layout.dialogo_cantidad_series,null);
        builder.setView(aspectoDialog);
        builder.setTitle("Seleccione el numero de Series");

        peso= aspectoDialog.findViewById(R.id.campo_cant_peso);
        num_rep= aspectoDialog.findViewById(R.id.campo_cant_reps);
        notas= aspectoDialog.findViewById(R.id.campo_cant_notas);
        builder.setPositiveButton("Añadir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (peso.getText().toString() != null && num_rep.getText().toString() != null && Integer.parseInt(num_rep.getText().toString()) > 0) {
                    if (notas ==null){
                        notas.setText("sin notas");
                    }
                    ((RutinaIniciada) getActivity()).aniadirSerie(ejercicio, Integer.parseInt(peso.getText().toString()), Integer.parseInt(num_rep.getText().toString()), notas.getText().toString());

                }
                else{
                    Toast.makeText(getContext(), "Debes introducir un numero positivo", Toast.LENGTH_LONG).show();


                }
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
            }
        });
                return builder.create();
    }
}
