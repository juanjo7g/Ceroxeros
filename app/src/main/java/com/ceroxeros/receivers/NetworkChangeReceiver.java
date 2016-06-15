package com.ceroxeros.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.ceroxeros.helper.DBHelper;
import com.ceroxeros.modelo.Configuracion;
import com.ceroxeros.modelo.Usuario;
import com.ceroxeros.rest.ServiceGenerator;
import com.ceroxeros.rest.model.Configuration;
import com.ceroxeros.rest.services.ConfigurationService;
import com.ceroxeros.util.Utility;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by juan on 6/06/16.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {
    private Context context = null;
    private Dao daoConfiguracion = null;
    private Dao daoUsuario;
    private DBHelper helper = null;
    private Usuario usuarioActual;


    @Override
    public void onReceive(Context context, Intent intent) {
        if (Utility.isOnline()) {
//            Toast.makeText(context, "Cambio.....", Toast.LENGTH_SHORT).show();
            this.context = context;
            new TaskSincronizarConfiguraciones().execute();
        }
    }

    private DBHelper getHelper() {
        if (helper == null) {
            helper = OpenHelperManager.getHelper(context, DBHelper.class);
        }
        return helper;
    }

    private class TaskSincronizarConfiguraciones extends AsyncTask<Void, Void, Void> {
        List<Configuracion> listaConfiguracionesASincronizar = null;
        Configuracion configuracionASincronizar = null;
        Configuration configurationToSync = null;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                daoConfiguracion = getHelper().getConfiguracionDao();
                listaConfiguracionesASincronizar = daoConfiguracion.queryForAll();
                for (int i = 0; i < listaConfiguracionesASincronizar.size(); i++) {
                    configuracionASincronizar = listaConfiguracionesASincronizar.get(i);
                    if (!configuracionASincronizar.getSincronizado()
                            && getUsuarioActual() != null) {
                        if (!configuracionASincronizar.getEliminado()) {
                            guardarConfiguracion(configuracionASincronizar);
                        }
                        if (configuracionASincronizar.getEliminado()) {
                            eliminarConfiguracion(configuracionASincronizar);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
        }
    }

    private void guardarConfiguracion(final Configuracion configuracionASincronizar) {
        final String[] bodyString = new String[1];
        ConfigurationService configurationService = ServiceGenerator.getConfigurationService();
        Configuration configuration = new Configuration(configuracionASincronizar);
        configurationService.crearConfiguracion("Configuración " + configuracionASincronizar.getIdLocal(),
                configuration.getMode(),
                configuration.getIntensity(),
                usuarioActual.getToken(),
                new Callback<Response>() {
                    @Override
                    public void success(Response res, Response response) {
                        bodyString[0] = new String(((TypedByteArray) res.getBody()).getBytes());
                        try {
                            JSONObject resJson = new JSONObject(bodyString[0]);
                            if ((Boolean) resJson.get("success")) {
                                configuracionASincronizar.setSincronizado(true);
                                daoConfiguracion.update(configuracionASincronizar);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                    }
                });
    }

    private void eliminarConfiguracion(final Configuracion configuracionASincronizar) {
        final String[] bodyString = new String[1];
        ConfigurationService configurationService = ServiceGenerator.getConfigurationService();
        Configuration configuration = new Configuration(configuracionASincronizar);
        configurationService.eliminarConfiguracion(configuration.get_id(),
                usuarioActual.getToken(),
                new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {
                        bodyString[0] = new String(((TypedByteArray) response.getBody()).getBytes());
                        try {
                            JSONObject resJson = new JSONObject(bodyString[0]);
                            if ((Boolean) resJson.get("success")) {
                                configuracionASincronizar.setSincronizado(true);
                                daoConfiguracion.update(configuracionASincronizar);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                    }
                });
    }

    public Usuario getUsuarioActual() {
        try {
            daoUsuario = getHelper().getUsuarioDao();
            usuarioActual = (Usuario) daoUsuario.queryForId(1);
            if (usuarioActual != null && usuarioActual.getToken() != null) {
                return usuarioActual;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
