package com.ceroxeros.view.activity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ceroxeros.modelo.Configuracion;
import com.ceroxeros.modelo.Dispositivo;
import com.ceroxeros.modelo.Usuario;
import com.ceroxeros.util.Utility;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.juan.electrocontrolapp.R;
import com.ceroxeros.helper.DBHelper;
import com.ceroxeros.view.fragments.IniciarSesionFragment;
import com.ceroxeros.view.fragments.MainFragment;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private MenuItem menuItemBotonBluetooth;

    private String address = null;
    private ProgressDialog progress;
    private BluetoothAdapter myBluetooth = null;
    private BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    //SPP UUID. Look for it
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private DBHelper helper;

    private Dao daoConfiguracion;
    private Dao daoUsuario;
    private Dao daoDispositivo;

    private Menu menu;
    private SubMenu subMenuConfiguracionesFavoritas;
    private MenuItem itemConfiguracionesFavoritas;
    private NavigationView navigationView;

    private Snackbar snackbarConexionBT;

    private Usuario usuarioActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (drawer != null) {
            drawer.setDrawerListener(toggle);
        }
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            menu = navigationView.getMenu();
        }

        snackbarConexionBT = Snackbar.make(navigationView, R.string.mensaje_sin_dispositivo_conectado, Snackbar.LENGTH_INDEFINITE);
        snackbarConexionBT.show();
        inicializarMenuLateral(navigationView);
        inicializarMainFragment();
        inicializarConexion();

        FacebookSdk.sdkInitialize(this.getApplicationContext());
    }

    private void inicializarMenuLateral(NavigationView navigationView) {
        if (navigationView != null) {
            subMenuConfiguracionesFavoritas = menu.addSubMenu(R.id.group_configuraciones_favoritas, Menu.NONE, 2, R.string.titulo_configuraciones_favoritas);
            subMenuConfiguracionesFavoritas.setGroupCheckable(R.id.group_configuraciones_favoritas, Boolean.TRUE, Boolean.TRUE);
            navigationView.setNavigationItemSelectedListener(this);
            new TaskCargarConfiguraciones().execute();
            actualizarSesionMenuLateral();
        }
    }

    private void inicializarMainFragment() {
        Fragment fragment = new MainFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
        menu.setGroupCheckable(R.id.group_configuraciones_favoritas, Boolean.TRUE, Boolean.TRUE);
        menu.getItem(0).setChecked(Boolean.TRUE);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
        }
        if (btSocket == null) {
            snackbarConexionBT.show();
        }
    }

    public void inicializarMainFragment(Configuracion configuracion) {
        Fragment fragment = null;
        fragment = new MainFragment(configuracion);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
        menu.setGroupCheckable(R.id.group_configuraciones_favoritas, Boolean.TRUE, Boolean.TRUE);
        menu.getItem(0).setChecked(Boolean.TRUE);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
        }
        if (btSocket == null) {
            snackbarConexionBT.show();
        }
    }


    private void inicializarMainFragment(int id) {
        Fragment fragment = null;
        fragment = new MainFragment(id);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();

        menu.setGroupCheckable(R.id.group_configuraciones_favoritas, Boolean.TRUE, Boolean.TRUE);
        menu.getItem(0).setChecked(Boolean.FALSE);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(menu.findItem(id).getTitle());
        }
        if (btSocket == null) {
            snackbarConexionBT.show();
        }
    }

    private void inicializarConexion() {
        Dispositivo dispositivo = null;
        List<Dispositivo> listaDispositivos = null;
        String mac = null;
        try {
            daoDispositivo = getHelper().getDispositivoDao();
            dispositivo = (Dispositivo) daoDispositivo.queryForId(1);
            if (dispositivo != null) {
                mac = dispositivo.getMac();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (mac != null) {
            address = mac;
            //todo: iniciar conexion automaticamente al abrir la app
            // new ConnectBT().execute();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();
        menuItemBotonBluetooth = item;

        if (id == R.id.action_bluetooth) {
            Intent i = new Intent(MainActivity.this, BluetoothActivity.class);
            startActivityForResult(i, 1);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        Fragment fragment = null;
        menu.setGroupCheckable(R.id.group_configuraciones_favoritas, Boolean.TRUE, Boolean.TRUE);
        item.setChecked(true);

        if (id == R.id.nav_configuracion_actual) {
            fragment = new MainFragment();
        } else if (id == R.id.nav_iniciar_sesion) {
            if (Utility.isOnline()) {
                fragment = new IniciarSesionFragment();
            } else {
                Toast.makeText(MainActivity.this, getString(R.string.mensaje_error_requiere_internet), Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_cerrar_sesion) {
            fragment = new MainFragment();
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(R.string.app_name);
            }
            cerrarSesion();
        } else {
            fragment = new MainFragment(id);
        }
        if (fragment != null) {
            if (getSupportActionBar() != null) {
                if (id == R.id.nav_cerrar_sesion) {
                    getSupportActionBar().setTitle(R.string.app_name);
                } else {
                    getSupportActionBar().setTitle(item.getTitle());
                }
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    private void cerrarSesion() {
        List<Configuracion> listaConfiguraciones = null;
        Configuracion config;
        try {
            daoUsuario = getHelper().getUsuarioDao();
            daoConfiguracion = getHelper().getConfiguracionDao();

            listaConfiguraciones = daoConfiguracion.queryForAll();
            for (int i = 0; i < listaConfiguraciones.size(); i++) {
                config = listaConfiguraciones.get(i);
                daoConfiguracion.delete(config);
                eliminaraConfiguracionFavoritaMenuLateral(config.getIdLocal());
            }
            daoConfiguracion.updateRaw("delete from 'configuracion';");
            daoConfiguracion.updateRaw("delete from sqlite_sequence where name='configuracion';");

            Usuario usuario = getUsuarioActual();
            if (usuario != null) {
                usuario.setToken(null);
                daoUsuario.update(usuario);
                actualizarSesionMenuLateral();
            }
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            if (accessToken != null) {
                LoginManager.getInstance().logOut();
            }
            mostrarAlertDialog(getString(R.string.informacion), getString(R.string.mensaje_sesion_cerrada));
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data != null && data.getExtras() != null && data.getExtras().get("address") != null) {
            Toast.makeText(MainActivity.this, "Mac " + data.getExtras().get("address"), Toast.LENGTH_SHORT).show();
            address = data.getExtras().get("address") + "";
            new ConnectBT().execute(); //Call the class to connect
        } else {
//            Toast.makeText(MainActivity.this, "No llego mac", Toast.LENGTH_SHORT).show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void agregarConfiguracionFavoritaAlMenuLateral(Configuracion configuracion, Boolean inserta) {
        int id = configuracion.getIdLocal();
        if (id != -1 && !configuracion.getEliminado()) {
            itemConfiguracionesFavoritas = subMenuConfiguracionesFavoritas
                    .add(R.id.group_configuraciones_favoritas, id, 2, "Fav " + id);
            itemConfiguracionesFavoritas.setIcon(R.drawable.ic_star_black_24dp);
            subMenuConfiguracionesFavoritas.setGroupCheckable(R.id.group_configuraciones_favoritas, Boolean.TRUE, Boolean.TRUE);
            if (inserta) {
                inicializarMainFragment(configuracion.getIdLocal());
            }
        }

    }


    public void eliminaraConfiguracionFavoritaMenuLateral(int idLocal) {
        subMenuConfiguracionesFavoritas.removeItem(idLocal);
        inicializarMainFragment();
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true;
        private Dispositivo dispositivoActual = null;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(MainActivity.this, getString(R.string.mensaje_progreso_conectando_dispositivo),
                    getString(R.string.mensaje_por_favor_espere));
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try {
                if (btSocket == null || !isBtConnected) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            } catch (IOException e) {
                ConnectSuccess = false;//if the try failed, you can check the exception here
                Log.e("Error conexión", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                btSocket = null;
                Toast.makeText(getApplicationContext(), "Falló la conexión", Toast.LENGTH_SHORT).show();
                snackbarConexionBT.show();
                if (menuItemBotonBluetooth != null) {
                    menuItemBotonBluetooth.setIcon(getResources().getDrawable(R.drawable.ic_bluetooth_disabled_white_24dp));
                }
            } else {
                Toast.makeText(getApplicationContext(), "Conectado con éxito", Toast.LENGTH_SHORT).show();
                isBtConnected = true;
                try {
                    daoDispositivo = getHelper().getDispositivoDao();
                    dispositivoActual = new Dispositivo();
                    dispositivoActual.setMac(address);
                    dispositivoActual.setUsuario(null);
                    dispositivoActual.setIdLocal(1);
                    daoDispositivo.createOrUpdate(dispositivoActual);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                snackbarConexionBT.dismiss();
                menuItemBotonBluetooth.setIcon(getResources().getDrawable(R.drawable.ic_bluetooth_connected_white_24dp));
            }
            progress.dismiss();
        }
    }

    public BluetoothSocket getBtSocket() {
        return btSocket;
    }

    public DBHelper getHelper() {
        if (helper == null) {
            helper = OpenHelperManager.getHelper(this, DBHelper.class);
        }
        return helper;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (helper != null) {
            OpenHelperManager.releaseHelper();
            helper = null;
            if (btSocket != null) {
                try {
                    btSocket.close();
                    btSocket = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class TaskCargarConfiguraciones extends AsyncTask<Void, Void, Void> {
        List<Configuracion> listaConfiguraciones = null;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                daoConfiguracion = getHelper().getConfiguracionDao();
                listaConfiguraciones = new ArrayList<>();
                listaConfiguraciones = daoConfiguracion.queryForAll();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Configuracion configuracion = null;
            if (listaConfiguraciones != null && listaConfiguraciones.size() != 0) {
                for (int i = 0; i < listaConfiguraciones.size(); i++) {
                    configuracion = listaConfiguraciones.get(i);
                    agregarConfiguracionFavoritaAlMenuLateral(configuracion, false);
                }
            }
        }
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

    public void actualizarSesionMenuLateral() {
        View headerView = navigationView.getHeaderView(0);
        TextView tvNombreNavHeader = (TextView) headerView.findViewById(R.id.textViewNombreNavHeader);
        TextView tvEmailNavHeader = (TextView) headerView.findViewById(R.id.textViewEmailEmailNavHeader);
        if (getUsuarioActual() != null) {
            menu.findItem(R.id.nav_iniciar_sesion).setVisible(false);
            menu.findItem(R.id.nav_cerrar_sesion).setVisible(true);
            tvNombreNavHeader.setText(usuarioActual.getNombreCompleto());
            tvEmailNavHeader.setText(usuarioActual.getCorreo());
        } else {
            menu.findItem(R.id.nav_iniciar_sesion).setVisible(true);
            menu.findItem(R.id.nav_cerrar_sesion).setVisible(false);
            tvNombreNavHeader.setText("Usuario Invitado - Inicie Sesión");
            tvEmailNavHeader.setText("");
        }
    }

    private void mostrarAlertDialog(String t, String s) {
        new AlertDialog.Builder(this)
                .setTitle(t)
                .setMessage(s)
                .setPositiveButton(getString(R.string.boton_aceptar), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }
}
