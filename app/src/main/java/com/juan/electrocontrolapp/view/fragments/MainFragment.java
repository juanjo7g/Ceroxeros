package com.juan.electrocontrolapp.view.fragments;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import com.juan.electrocontrolapp.R;
import com.juan.electrocontrolapp.view.activity.MainActivity;

import java.io.IOException;

public class MainFragment extends Fragment {

    private FloatingActionButton fabModoA;
    private FloatingActionButton fabModoB;
    private FloatingActionButton fabModoC;
    private FloatingActionButton fabAumentarIntensidad;
    private FloatingActionButton fabDisminuirIntensidad;
    private SeekBar sbIntensidad;

    private String modo;
    private String intensidad;

    private MainActivity mainActivity;

    public MainFragment() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mainActivity = (MainActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        fabModoA = (FloatingActionButton) rootView.findViewById(R.id.fabModoA);
        fabModoB = (FloatingActionButton) rootView.findViewById(R.id.fabModoB);
        fabModoC = (FloatingActionButton) rootView.findViewById(R.id.fabModoC);
        fabAumentarIntensidad = (FloatingActionButton) rootView.findViewById(R.id.fabAumentarIntensidad);
        fabDisminuirIntensidad = (FloatingActionButton) rootView.findViewById(R.id.fabDisminuirIntensidad);
        sbIntensidad = (SeekBar) rootView.findViewById(R.id.sbIntensidad);

        fabModoA.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(rootView.getContext(), R.color.colorAccent)));
        fabModoB.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(rootView.getContext(), R.color.colorSecondaryText)));
        fabModoC.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(rootView.getContext(), R.color.colorSecondaryText)));

        modo = "A";
        intensidad = "45";

        fabModoA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activarModoA();
            }
        });
        fabModoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activarModoB();
            }
        });
        fabModoC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activarModoC();
            }
        });

        fabAumentarIntensidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aumentarIntensidad();
            }
        });
        fabDisminuirIntensidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disminuirIntensidad();
            }
        });

        // No permite modificar el seekbar al tocarlo
        sbIntensidad.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        return rootView;
    }

    private void activarModoA() {

        fabModoA.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorAccent)));
        fabModoB.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorSecondaryText)));
        fabModoC.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorSecondaryText)));

        modo = "A";
        mostrarMensajeConfiguracionActual();

        enviarStringBluetooth(modo);
    }

    private void activarModoB() {
        fabModoA.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorSecondaryText)));
        fabModoB.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorAccent)));
        fabModoC.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorSecondaryText)));

        modo = "B";
        mostrarMensajeConfiguracionActual();

        enviarStringBluetooth(modo);
    }

    private void activarModoC() {
        fabModoA.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorSecondaryText)));
        fabModoB.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorSecondaryText)));
        fabModoC.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorAccent)));

        modo = "C";
        mostrarMensajeConfiguracionActual();

        enviarStringBluetooth(modo);
    }


    private void aumentarIntensidad() {
        if (Integer.parseInt(intensidad) < 100) {
            intensidad = String.valueOf(Integer.parseInt(intensidad) + 5);
            sbIntensidad.setProgress(Integer.parseInt(intensidad));
        }
        actualizaColores();
        mostrarMensajeConfiguracionActual();

        enviarStringBluetooth("+");
    }


    private void disminuirIntensidad() {
        if (Integer.parseInt(intensidad) > 0) {
            intensidad = String.valueOf(Integer.parseInt(intensidad) - 5);
            sbIntensidad.setProgress(Integer.parseInt(intensidad));
        }
        actualizaColores();
        mostrarMensajeConfiguracionActual();
        enviarStringBluetooth("-");
    }

    private void actualizaColores() {
        int intensidadInt = Integer.parseInt(intensidad);

        if (intensidadInt + 5 >= 80) {
            fabAumentarIntensidad.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorRed)));
        }
        if (intensidadInt + 5 >= 5 && intensidadInt + 5 <= 75) {
            fabAumentarIntensidad.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorOrange)));
        }
        if (intensidadInt + 5 <= 45) {
            fabAumentarIntensidad.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorGreen)));
        }

        if (intensidadInt - 5 >= 80) {
            fabDisminuirIntensidad.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorRed)));
        }
        if (intensidadInt - 5 >= 5 && intensidadInt - 5 <= 75) {
            fabDisminuirIntensidad.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorOrange)));
        }
        if (intensidadInt - 5 <= 45) {
            fabDisminuirIntensidad.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorGreen)));
        }
    }

    public void mostrarMensajeConfiguracionActual() {
        Float intensidadF = Float.parseFloat(intensidad) / 10;
        Toast.makeText(getContext(), "Modo: " + modo + " Intensidad: " + intensidadF, Toast.LENGTH_SHORT).show();
    }

    private void enviarStringBluetooth(String s) {
        BluetoothSocket btSocket = mainActivity.getBtSocket();
        if (btSocket != null && s != null) {
            try {
                btSocket.getOutputStream().write(s.getBytes());
            } catch (IOException e) {
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
