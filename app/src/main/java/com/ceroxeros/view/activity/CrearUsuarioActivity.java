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

import com.ceroxeros.rest.ServiceGenerator;
import com.ceroxeros.rest.UserService;
import com.ceroxeros.rest.model.User;
import com.juan.electrocontrolapp.R;

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
        etContraseña = (EditText) findViewById(R.id.etContraseña);
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
                    public void success(Response user, Response response) {
                        progressDialog.dismiss();
                        bodyString[0] = new String(((TypedByteArray) user.getBody()).getBytes());
                        Toast.makeText(CrearUsuarioActivity.this, "JSON " + bodyString[0] + "rES: " + response.getStatus(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        progressDialog.dismiss();
                        bodyString[0] = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
                        Toast.makeText(CrearUsuarioActivity.this, "Res: " + bodyString[0], Toast.LENGTH_SHORT).show();
                    }
                });
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
}
