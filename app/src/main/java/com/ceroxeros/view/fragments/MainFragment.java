package com.ceroxeros.view.fragments;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import com.ceroxeros.modelo.Configuracion;
import com.ceroxeros.rest.ServiceGenerator;
import com.ceroxeros.rest.model.Configuration;
import com.ceroxeros.rest.services.ConfigurationService;
import com.j256.ormlite.dao.Dao;
import com.juan.electrocontrolapp.R;
import com.ceroxeros.view.activity.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class MainFragment extends Fragment {

    private FloatingActionButton fabModoA;
    private FloatingActionButton fabModoB;
    private FloatingActionButton fabModoC;
    private FloatingActionButton fabAumentarIntensidad;
    private FloatingActionButton fabDisminuirIntensidad;
    private SeekBar sbIntensidad;

    private MainActivity mainActivity;

    private Boolean favorito;

    private Dao daoConfiguracion;
    private Dao daoUsuario;

    private Configuracion configuracion;

    private MenuItem menuItemFavorito;

    public MainFragment() {
        configuracion = new Configuracion();
        configuracion.setIdLocal(-1);
        favorito = Boolean.FALSE;
    }

    public MainFragment(Configuracion configuracion) {
        this.configuracion = configuracion;
        configuracion.setIdLocal(0);
        favorito = Boolean.FALSE;
    }

    public MainFragment(int id) {
        configuracion = new Configuracion();
        configuracion.setIdLocal(id);
        favorito = Boolean.TRUE;
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

        fabModoA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (configuracion.getIdLocal() > 0) {
                    mainActivity.inicializarMainFragment(configuracion);
                }
                activarModoA();
            }
        });
        fabModoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (configuracion.getIdLocal() > 0) {
                    mainActivity.inicializarMainFragment(configuracion);
                }
                activarModoB();
            }
        });
        fabModoC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (configuracion.getIdLocal() > 0) {
                    mainActivity.inicializarMainFragment(configuracion);
                }
                activarModoC();
            }
        });

        fabAumentarIntensidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (configuracion.getIdLocal() > 0) {
                    mainActivity.inicializarMainFragment(configuracion);
                }
                aumentarIntensidad();
            }
        });
        fabDisminuirIntensidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (configuracion.getIdLocal() > 0) {
                    mainActivity.inicializarMainFragment(configuracion);
                }
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
        setHasOptionsMenu(true);

        if (configuracion.getIdLocal() == -1) {
            try {
                daoConfiguracion = mainActivity.getHelper().getConfiguracionDao();
                configuracion = (Configuracion) daoConfiguracion.queryForId(-1);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (configuracion == null) {
                configuracion = new Configuracion();
                configuracion.setIdLocal(-1);
                configuracion.setModo("A");
                configuracion.setIntensidad((float) 0);
            }
        }
        if (configuracion.getIdLocal() > 0) {
            try {
                daoConfiguracion = mainActivity.getHelper().getConfiguracionDao();
                configuracion = (Configuracion) daoConfiguracion.queryForId(configuracion.getIdLocal());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        inicializarConfiguracion();
        return rootView;
    }

    private void inicializarConfiguracion() {
        if (configuracion != null && configuracion.getModo() != null) {
            switch (configuracion.getModo().toLowerCase()) {
                case "a":
                    activarModoA();
                    break;
                case "b":
                    activarModoB();
                    break;
                case "c":
                    activarModoC();
                    break;
            }
            sbIntensidad.setProgress(configuracion.getIntensidad().intValue());
            actualizaColores();
            enviarIntensidadBluetooth(configuracion.getIntensidad());
        }
    }

    private void activarModoA() {

        fabModoA.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorAccent)));
        fabModoB.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorSecondaryText)));
        fabModoC.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorSecondaryText)));

        configuracion.setModo("A");
        //mostrarMensajeConfiguracionActual();
        guardarUltimaConfiguracion();
        enviarModoBluetooth(configuracion.getModo());
    }

    private void activarModoB() {
        fabModoA.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorSecondaryText)));
        fabModoB.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorAccent)));
        fabModoC.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorSecondaryText)));

        configuracion.setModo("B");
        //mostrarMensajeConfiguracionActual();
        guardarUltimaConfiguracion();
        enviarModoBluetooth(configuracion.getModo());
    }

    private void activarModoC() {
        fabModoA.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorSecondaryText)));
        fabModoB.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorSecondaryText)));
        fabModoC.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorAccent)));

        configuracion.setModo("C");
        //mostrarMensajeConfiguracionActual();
        guardarUltimaConfiguracion();
        enviarModoBluetooth(configuracion.getModo());
    }


    private void aumentarIntensidad() {
        if (configuracion.getIntensidad().intValue() < 100) {
            configuracion.setIntensidad(configuracion.getIntensidad() + 10);
            sbIntensidad.setProgress(configuracion.getIntensidad().intValue());
        }
        actualizaColores();
        //mostrarMensajeConfiguracionActual();
        guardarUltimaConfiguracion();
        enviarIntensidadBluetooth(configuracion.getIntensidad());
    }


    private void disminuirIntensidad() {
        if (configuracion.getIntensidad().intValue() > 0) {
            configuracion.setIntensidad(configuracion.getIntensidad() - 10);
            sbIntensidad.setProgress(configuracion.getIntensidad().intValue());
        }
        actualizaColores();
//        mostrarMensajeConfiguracionActual();
        guardarUltimaConfiguracion();
        enviarIntensidadBluetooth(configuracion.getIntensidad());
    }

    private void guardarUltimaConfiguracion() {
        Configuracion ultimaConfiguracion = null;
        try {
            ultimaConfiguracion = new Configuracion();
            ultimaConfiguracion.setModo(configuracion.getModo());
            ultimaConfiguracion.setIntensidad(configuracion.getIntensidad());
            ultimaConfiguracion.setIdLocal(-1);
            daoConfiguracion = mainActivity.getHelper().getConfiguracionDao();
            daoConfiguracion.createOrUpdate(ultimaConfiguracion);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void actualizaColores() {
        int intensidadInt = configuracion.getIntensidad().intValue();
        Drawable thumb;

        if (intensidadInt >= 80) {
            thumb = mainActivity.getResources().getDrawable(R.drawable.thumb_red);
        } else if (intensidadInt <= 50) {
            thumb = mainActivity.getResources().getDrawable(R.drawable.thumb_green);
        } else {
            thumb = mainActivity.getResources().getDrawable(R.drawable.thumb_orange);
        }
        sbIntensidad.setThumb(thumb);

        if (intensidadInt + 5 >= 80) {
            fabAumentarIntensidad.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorRed)));
        }
        if (intensidadInt + 5 >= 5 && intensidadInt + 5 <= 75) {
            fabAumentarIntensidad.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorOrange)));
        }
        if (intensidadInt + 5 <= 50) {
            fabAumentarIntensidad.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorGreen)));
        }

        if (intensidadInt - 5 >= 80) {
            fabDisminuirIntensidad.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorRed)));
        }
        if (intensidadInt - 5 >= 5 && intensidadInt - 5 <= 75) {
            fabDisminuirIntensidad.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorOrange)));
        }
        if (intensidadInt - 5 <= 50) {
            fabDisminuirIntensidad.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorGreen)));
        }
    }

    public void mostrarMensajeConfiguracionActual() {
        int intensidadF = (int) (configuracion.getIntensidad() / 10);
        if (getView() != null) {
            Snackbar.make(getView(), "Modo: " + configuracion.getModo() + " Intensidad: " + intensidadF,
                    Snackbar.LENGTH_SHORT).show();
        }
    }

    private void enviarModoBluetooth(String modo) {
        BluetoothSocket btSocket = mainActivity.getBtSocket();
        if (btSocket != null && modo != null) {
            try {
                modo = modo.toLowerCase();
                btSocket.getOutputStream().write(modo.getBytes());
                mostrarMensajeConfiguracionActual();
            } catch (IOException e) {
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void enviarIntensidadBluetooth(Float intensidad) {
        BluetoothSocket btSocket = mainActivity.getBtSocket();
        int intensidadInt = (int) (intensidad / 10);
        String intensidadStr;
        if (intensidadInt == 0) {
            intensidadStr = "p";
        } else if (intensidadInt == 10) {
            intensidadStr = "m";
        } else {
            intensidadStr = intensidadInt + "";
        }
        if (btSocket != null) {
            try {
                intensidadStr = intensidadStr.toLowerCase();
                btSocket.getOutputStream().write(intensidadStr.getBytes());
                mostrarMensajeConfiguracionActual();
            } catch (IOException e) {
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_activity_main, menu);
        menuItemFavorito = menu.findItem(R.id.action_favorito);
        if (favorito) {
            menuItemFavorito.setIcon(R.drawable.ic_star_white_24dp);
        } else {
            menuItemFavorito.setIcon(R.drawable.ic_star_border_white_24dp);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_favorito) {
            if (favorito) {
                new TaskEliminarConfiguracion().execute();
                item.setIcon(getResources().getDrawable(R.drawable.ic_star_border_white_24dp));
                favorito = Boolean.FALSE;
            } else {
                new TaskGuardarConfiguracion().execute();
                item.setIcon(getResources().getDrawable(R.drawable.ic_star_white_24dp));
                favorito = Boolean.TRUE;
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class TaskGuardarConfiguracion extends AsyncTask<Void, Void, Void> {
        Configuracion configuracionAGuardar = null;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                daoConfiguracion = mainActivity.getHelper().getConfiguracionDao();
                configuracionAGuardar = new Configuracion();
                configuracionAGuardar.setIntensidad(configuracion.getIntensidad());
                configuracionAGuardar.setModo(configuracion.getModo());
                configuracionAGuardar.setFechaCreacion(new Date());
                daoConfiguracion.create(configuracionAGuardar);
                if (mainActivity.getUsuarioActual() != null) {
                    final String[] bodyString = new String[1];
                    final int idConfiguracion = configuracionAGuardar.getIdLocal();
                    configuracionAGuardar.setUsuario(mainActivity.getUsuarioActual());
                    ConfigurationService configurationService = ServiceGenerator.getConfigurationService();
                    final Configuration configuration = new Configuration(configuracionAGuardar);
                    configurationService.crearConfiguracion("Configuracion " + idConfiguracion,
                            configuration.getMode(),
                            configuration.getIntensity(),
                            mainActivity.getUsuarioActual().getToken(),
                            new Callback<Response>() {
                                @Override
                                public void success(Response res, Response response) {
                                    bodyString[0] = new String(((TypedByteArray) res.getBody()).getBytes());
                                    try {
                                        JSONObject resJson = new JSONObject(bodyString[0]);
                                        if ((Boolean) resJson.get("success")) {
                                            Toast.makeText(mainActivity, "JSON " + bodyString[0], Toast.LENGTH_SHORT).show();
                                            configuracionAGuardar.setSincronizado(true);
                                            try {
                                                daoConfiguracion = mainActivity.getHelper().getConfiguracionDao();
                                                daoConfiguracion.update(configuracionAGuardar);
                                            } catch (SQLException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    try {
                                        bodyString[0] = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
                                        Toast.makeText(getActivity(), "Res: " + bodyString[0], Toast.LENGTH_SHORT).show();
                                        configuracion.setSincronizado(false);
                                        daoConfiguracion = mainActivity.getHelper().getConfiguracionDao();
                                        daoConfiguracion.update(configuracionAGuardar);
                                    } catch (SQLException | NullPointerException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            int idConfiguracion = configuracionAGuardar.getIdLocal();
            mainActivity.agregarConfiguracionFavoritaAlMenuLateral(configuracionAGuardar, true);
            mostrarMensajeToast("Configuración " + idConfiguracion + " guardada.");
        }
    }

    private class TaskEliminarConfiguracion extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                daoConfiguracion = mainActivity.getHelper().getConfiguracionDao();
                configuracion.setEliminado(true);
                configuracion.setSincronizado(false);
                daoConfiguracion.update(configuracion);
                if (mainActivity.getUsuarioActual() != null) {
                    final String[] bodyString = new String[1];
                    final String idConfiguracion = configuracion.getIdRemoto();
                    ConfigurationService configurationService = ServiceGenerator.getConfigurationService();
                    configurationService.eliminarConfiguracion(idConfiguracion,
                            mainActivity.getUsuarioActual().getToken(),
                            new Callback<Response>() {
                                @Override
                                public void success(Response res, Response response) {
                                    bodyString[0] = new String(((TypedByteArray) res.getBody()).getBytes());
                                    try {
                                        JSONObject resJson = new JSONObject(bodyString[0]);
                                        if ((Boolean) resJson.get("success")) {
                                            Toast.makeText(mainActivity, "JSON " + bodyString[0], Toast.LENGTH_SHORT).show();
                                            configuracion.setSincronizado(true);
                                            try {
                                                daoConfiguracion = mainActivity.getHelper().getConfiguracionDao();
                                                daoConfiguracion.update(configuracion);
                                            } catch (SQLException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    try {
                                        bodyString[0] = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
                                        Toast.makeText(getActivity(), "Res: " + bodyString[0], Toast.LENGTH_SHORT).show();
                                        configuracion.setSincronizado(false);
                                        daoConfiguracion = mainActivity.getHelper().getConfiguracionDao();
                                        daoConfiguracion.update(configuracion);
                                    } catch (SQLException | NullPointerException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mainActivity.eliminaraConfiguracionFavoritaMenuLateral(configuracion.getIdLocal());
            mostrarMensajeToast("Configuración " + configuracion.getIdLocal() + " eliminada.");
        }
    }

    void mostrarMensaje(String s) {
//        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
        if (getView() != null) {
            Snackbar.make(getView(), s, Snackbar.LENGTH_LONG).show();
        }
    }

    void mostrarMensajeToast(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }

}
