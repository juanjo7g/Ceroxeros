package com.ceroxeros.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.ceroxeros.modelo.Dispositivo;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.ceroxeros.modelo.Configuracion;
import com.ceroxeros.modelo.Usuario;

import java.sql.SQLException;

/**
 * Created by Juan on 03/05/2016.
 */
public class DBHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "ceroxeros.db";
    private static final int DATABASE_VERSION = 1;

    private Dao<Configuracion, Integer> configuracionDao;
    private Dao<Usuario, Integer> usuarioDao;
    private Dao<Dispositivo, Integer> dispositivoDao;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Configuracion.class);
            TableUtils.createTable(connectionSource, Usuario.class);
            TableUtils.createTable(connectionSource, Dispositivo.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        onCreate(database, connectionSource);
    }

    public Dao<Configuracion, Integer> getConfiguracionDao() throws SQLException {
        if (configuracionDao == null) {
            configuracionDao = getDao(Configuracion.class);
        }
        return configuracionDao;
    }

    public Dao<Usuario, Integer> getUsuarioDao() throws SQLException {
        if (usuarioDao == null) {
            usuarioDao = getDao(Usuario.class);
        }
        return usuarioDao;
    }

    public Dao<Dispositivo, Integer> getDispositivoDao() throws SQLException {
        if (dispositivoDao == null) {
            dispositivoDao = getDao(Dispositivo.class);
        }
        return dispositivoDao;
    }

    @Override
    public void close() {
        super.close();
        configuracionDao = null;
        usuarioDao = null;
        dispositivoDao = null;
    }
}
