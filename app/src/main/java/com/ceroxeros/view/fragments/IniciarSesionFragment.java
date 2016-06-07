package com.ceroxeros.view.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ceroxeros.modelo.Configuracion;
import com.ceroxeros.modelo.Usuario;
import com.ceroxeros.rest.ServiceGenerator;
import com.ceroxeros.rest.model.Configuration;
import com.ceroxeros.rest.model.User;
import com.ceroxeros.rest.services.ConfigurationService;
import com.ceroxeros.rest.services.UserService;
import com.ceroxeros.view.activity.CrearUsuarioActivity;
import com.ceroxeros.view.activity.MainActivity;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;
import com.juan.electrocontrolapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class IniciarSesionFragment extends Fragment {

    private LoginButton loginButtonFacebook;
    private CallbackManager callbackManager;

    private Button buttonCrearUsuario;
    private EditText etNombreUsuario;
    private EditText etContraseña;
    private Button buttonIniciar;

    private MainActivity mainActivity;

    private Dao dao;
    private Dao daoConfiguracion;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mainActivity = (MainActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_iniciar_sesion, container, false);

        /** Inicio de sesion con Facebook. */

        loginButtonFacebook = (LoginButton) view.findViewById(R.id.btnLoginFB);
        loginButtonFacebook.setFragment(this);

        loginButtonFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            String texto = "";

            @Override
            public void onSuccess(LoginResult loginResult) {

                texto = texto +
                        "User ID: "
                        + loginResult.getAccessToken().getUserId()
                        + "\n" +
                        "Auth Token: "
                        + loginResult.getAccessToken().getToken()
                        + "\n" +
                        "Token Duration: "
                        + loginResult.getAccessToken().getExpires()
                        + "\n";

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                if (response.getError() != null) {

                                } else {
                                    texto = texto +
                                            "Nombre: " + object.optString("first_name") + " " + object.optString("last_name")
                                            + "\n" +
                                            "Email: " + object.optString("email") + "\n";
                                    System.out.println(texto);
                                    Toast.makeText(getActivity(), texto, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "first_name,last_name,email");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });


        buttonCrearUsuario = (Button) view.findViewById(R.id.buttonCrearUsuario);
        buttonCrearUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Todo: validar que tenga conexion a internet
                Intent i = new Intent(getActivity(), CrearUsuarioActivity.class);
                startActivity(i);
            }
        });
        etNombreUsuario = (EditText) view.findViewById(R.id.etFragmentIniciarSesionUsuario);
        etContraseña = (EditText) view.findViewById(R.id.etFragmentIniciarSesionContraseña);
        buttonIniciar = (Button) view.findViewById(R.id.btnFragmentIniciarSesionIniciar);
        buttonIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarSesion();
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Facebook.
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void iniciarSesion() {
        final ProgressDialog progressDialog = ProgressDialog.show(this.getActivity(), null, "Iniciando sesion...", true, false);
        User user = new User();
        UserService userService = ServiceGenerator.getUserService();
        final String[] bodyString = new String[2];
        user.setUsername(etNombreUsuario.getText().toString());
        user.setPassword(etContraseña.getText().toString());

        userService.iniciarSesion(user.getUsername(),
                user.getPassword(),
                new Callback<Response>() {
                    @Override
                    public void success(Response user, Response response) {
                        bodyString[0] = new String(((TypedByteArray) user.getBody()).getBytes());
                        try {
                            final JSONObject resJson = new JSONObject(bodyString[0]);
                            Toast.makeText(getActivity(), "JSON " + resJson + "rES: " + response.getStatus(), Toast.LENGTH_SHORT).show();
                            guardarSesionUsuario((JSONObject) resJson);
                            progressDialog.setMessage("Cargando configuraciones favoritas");
                            ConfigurationService configurationService = ServiceGenerator.getConfigurationService();
                            String token = resJson.getString("token");
                            configurationService.obtenerConfiguracion(token, new Callback<Response>() {
                                @Override
                                public void success(Response response, Response response2) {
                                    bodyString[1] = new String(((TypedByteArray) response.getBody()).getBytes());
                                    final JSONArray resArrayJson;
                                    try {
                                        resArrayJson = new JSONArray(bodyString[1]);
                                        Toast.makeText(getActivity(), "JSON " + resArrayJson + "rES: " + response2.getStatus(), Toast.LENGTH_SHORT).show();
                                        guardarConfiguraciones((JSONArray) resArrayJson);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    progressDialog.dismiss();
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    progressDialog.dismiss();
                                }
                            });

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
                            Toast.makeText(getActivity(), "Res: " + bodyString[0], Toast.LENGTH_SHORT).show();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void guardarConfiguraciones(JSONArray resArrayJson) {
        List<Configuracion> listaConfiguraciones;
        Configuracion configuracion;
        Configuration configuration;
        Gson gson;
        try {
            daoConfiguracion = mainActivity.getHelper().getConfiguracionDao();
            listaConfiguraciones = daoConfiguracion.queryForAll();
            for (int i = 0; i < listaConfiguraciones.size(); i++) {
                configuracion = listaConfiguraciones.get(i);
                daoConfiguracion.delete(configuracion);
                mainActivity.eliminaraConfiguracionFavoritaMenuLateral(configuracion.getIdLocal());
            }
            gson = new Gson();
            for (int i = 0; i < resArrayJson.length(); i++) {
                configuration = gson.fromJson(resArrayJson.get(i).toString(), Configuration.class);
                configuracion = new Configuracion(configuration);
                configuracion.setUsuario(mainActivity.getUsuarioActual());
                daoConfiguracion.create(configuracion);
                mainActivity.agregarConfiguracionFavoritaAlMenuLateral(configuracion, false);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void guardarSesionUsuario(JSONObject userJson) {
        try {
            Gson gson = new Gson();
            User user = gson.fromJson(userJson.toString(), User.class);
            Toast.makeText(getActivity(), user.toString(), Toast.LENGTH_SHORT).show();
            Usuario usuario = new Usuario(user);
            dao = mainActivity.getHelper().getUsuarioDao();
            usuario.setIdLocal(1);
            dao.createOrUpdate(usuario);
            mainActivity.actualizarSesionMenuLateral();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
