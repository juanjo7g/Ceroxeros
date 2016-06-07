package com.ceroxeros.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.ceroxeros.helper.DBHelper;
import com.ceroxeros.modelo.Configuracion;
import com.ceroxeros.rest.ServiceGenerator;
import com.ceroxeros.rest.model.Configuration;
import com.ceroxeros.rest.services.ConfigurationService;
import com.ceroxeros.util.Utility;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.Date;
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
    private DBHelper helper = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Utility.isOnline()) {
            Toast.makeText(context, "Cambio.....", Toast.LENGTH_SHORT).show();
            this.context = context;
            new TaskGuardarConfiguracion().execute();
        }
    }

    private DBHelper getHelper() {
        if (helper == null) {
            helper = OpenHelperManager.getHelper(context, DBHelper.class);
        }
        return helper;
    }

    private class TaskGuardarConfiguracion extends AsyncTask<Void, Void, Void> {
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
}
