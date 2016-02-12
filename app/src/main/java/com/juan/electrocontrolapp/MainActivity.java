package com.juan.electrocontrolapp;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FloatingActionButton fabModoA;
    private FloatingActionButton fabModoB;
    private FloatingActionButton fabModoC;
    private FloatingActionButton fabAumentarIntensidad;
    private FloatingActionButton fabDisminuirIntensidad;
    private SeekBar sbIntensidad;
    private String modo;
    private String intensidad;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        fabModoA = (FloatingActionButton) findViewById(R.id.fabModoA);
        fabModoB = (FloatingActionButton) findViewById(R.id.fabModoB);
        fabModoC = (FloatingActionButton) findViewById(R.id.fabModoC);
        fabAumentarIntensidad = (FloatingActionButton) findViewById(R.id.fabAumentarIntensidad);
        fabDisminuirIntensidad = (FloatingActionButton) findViewById(R.id.fabDisminuirIntensidad);
        sbIntensidad = (SeekBar) findViewById(R.id.sbIntensidad);

        fabModoA.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorAccent)));
        fabModoB.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorSecondaryText)));
        fabModoC.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorSecondaryText)));

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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    private void activarModoA() {

        fabModoA.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorAccent)));
        fabModoB.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorSecondaryText)));
        fabModoC.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorSecondaryText)));

        modo = "A";
        mostrarMensajeConfiguracionActual();
    }

    private void activarModoB() {
        fabModoA.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorSecondaryText)));
        fabModoB.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorAccent)));
        fabModoC.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorSecondaryText)));

        modo = "B";
        mostrarMensajeConfiguracionActual();
    }

    private void activarModoC() {
        fabModoA.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorSecondaryText)));
        fabModoB.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorSecondaryText)));
        fabModoC.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorAccent)));

        modo = "C";
        mostrarMensajeConfiguracionActual();
    }


    private void aumentarIntensidad() {
        if (Integer.parseInt(intensidad) < 100) {
            intensidad = String.valueOf(Integer.parseInt(intensidad) + 5);
            sbIntensidad.setProgress(Integer.parseInt(intensidad));
        }
        actualizaColores();
        mostrarMensajeConfiguracionActual();
    }

    private void disminuirIntensidad() {
        if (Integer.parseInt(intensidad) > 0) {
            intensidad = String.valueOf(Integer.parseInt(intensidad) - 5);
            sbIntensidad.setProgress(Integer.parseInt(intensidad));
        }
        actualizaColores();
        mostrarMensajeConfiguracionActual();
    }

    private void actualizaColores() {
        int intensidadInt = Integer.parseInt(intensidad);

        if (intensidadInt + 5 >= 80) {
            fabAumentarIntensidad.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorRed)));
        }
        if (intensidadInt + 5 >= 5 && intensidadInt + 5 <= 75) {
            fabAumentarIntensidad.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorOrange)));
        }
        if (intensidadInt + 5 <= 45) {
            fabAumentarIntensidad.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorGreen)));
        }

        if (intensidadInt - 5 >= 80) {
            fabDisminuirIntensidad.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorRed)));
        }
        if (intensidadInt - 5 >= 5 && intensidadInt - 5 <= 75) {
            fabDisminuirIntensidad.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorOrange)));
        }
        if (intensidadInt - 5 <= 45) {
            fabDisminuirIntensidad.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorGreen)));
        }
    }


    public void mostrarMensajeConfiguracionActual() {
//        Toast.makeText(MainActivity.this, "Modo: " + modo + " Intensidad: " + intensidad, Toast.LENGTH_SHORT).show();
        Float intensidadF = Float.parseFloat(intensidad) / 10;
        Snackbar.make(findViewById(android.R.id.content), "Modo: " + modo + " Intensidad: " + intensidadF, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_bluetooth) {
            new AlertDialog.Builder(this)
                    .setTitle("Configuración conexión bluetooth")
                    .setMessage("Seleccionar dispositivo bluetooth.......")
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            item.setIcon(R.drawable.ic_bluetooth_connected_white_24dp);
                            Toast.makeText(MainActivity.this, "Dispositivo conectado con éxito.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setIcon(R.drawable.ic_info_outline_black_24dp)
                    .show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
