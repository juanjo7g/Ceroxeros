package com.ceroxeros.view.activity;

import android.app.ProgressDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ceroxeros.helper.DBHelper;
import com.ceroxeros.modelo.Usuario;
import com.ceroxeros.rest.ServiceGenerator;
import com.ceroxeros.rest.services.UserService;
import com.ceroxeros.rest.model.User;
import com.google.gson.Gson;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.juan.electrocontrolapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class CrearUsuarioActivity extends AppCompatActivity {

    private EditText etNombreCompleto;
    private EditText etNombreUsuario;
    private EditText etCorreoElectronico;
    private EditText etContraseña;
    private EditText etContraseñaRep;

    private Button btnCrear;

    private DBHelper helper;
    private Dao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_usuario);

        ActionBar actionBar = ((AppCompatActivity) this).getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        }

        etNombreCompleto = (EditText) findViewById(R.id.etNombreCompleto);
        etNombreUsuario = (EditText) findViewById(R.id.etNombreUsuario);
        etCorreoElectronico = (EditText) findViewById(R.id.etCorreoElectronico);
        etContraseña = (EditText) findViewById(R.id.etFragmentIniciarSesionContraseña);
        etContraseñaRep = (EditText) findViewById(R.id.etContraseñaRep);

        btnCrear = (Button) findViewById(R.id.btnCrearUsuario);
        btnCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crearUsuario();
            }
        });
    }

    private void crearUsuario() {
        final ProgressDialog progressDialog = ProgressDialog.show(this, null, "Creando usuario...", true, false);
        User user = new User();
        UserService userService = ServiceGenerator.getUserService();
        final String[] bodyString = new String[1];
        user.setName(etNombreCompleto.getText().toString());
        user.setUsername(etNombreUsuario.getText().toString());
        user.setEmail(etCorreoElectronico.getText().toString());
        user.setPassword1(etContraseña.getText().toString());
        user.setPassword2(etContraseñaRep.getText().toString());

        // TODO: VALIDAR USUARIO
        userService.crearUsuario(user.getName(),
                user.getUsername(),
                user.getPassword1(),
                user.getPassword2(),
                user.getEmail(),
                new Callback<Response>() {
                    @Override
                    public void success(Response res, Response response) {
                        progressDialog.dismiss();
                        bodyString[0] = new String(((TypedByteArray) res.getBody()).getBytes());
                        try {
                            JSONObject resJson = new JSONObject(bodyString[0]);
                            if ((Boolean) resJson.get("success")) {
                                Toast.makeText(CrearUsuarioActivity.this, "JSON " + resJson.get("data") + "rES: " + response.getStatus(), Toast.LENGTH_SHORT).show();
                                guardarSesionUsuario((JSONObject) resJson.get("data"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        try {
                            progressDialog.dismiss();
                            bodyString[0] = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
                            //todo: Manejar error
                            Toast.makeText(CrearUsuarioActivity.this, "Res: " + bodyString[0], Toast.LENGTH_SHORT).show();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void guardarSesionUsuario(JSONObject userJson) {
        try {
            Gson gson = new Gson();
            User user = gson.fromJson(userJson.toString(), User.class);
            Toast.makeText(CrearUsuarioActivity.this, user.toString(), Toast.LENGTH_SHORT).show();
            Usuario usuario = new Usuario(user);
            dao = getHelper().getUsuarioDao();
            usuario.setIdLocal(1);
            dao.createOrUpdate(usuario);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public DBHelper getHelper() {
        if (helper == null) {
            helper = OpenHelperManager.getHelper(this, DBHelper.class);
        }
        return helper;
    }
}
