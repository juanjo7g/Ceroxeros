package com.ceroxeros.view.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import com.ceroxeros.util.Utility;
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

            @Override
            public void onSuccess(final LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                if (response.getError() != null) {

                                } else {
                                    iniciarSesionFb(loginResult.getAccessToken().getUserId(),
                                            loginResult.getAccessToken().getToken(),
                                            object.optString("first_name") + " " + object.optString("last_name"),
                                            object.optString("email"));
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
                if (!Utility.isOnline()) {
                    mostrarToast(mainActivity.getString(R.string.mensaje_error_requiere_internet));
                } else {
                    Intent i = new Intent(getActivity(), CrearUsuarioActivity.class);
                    startActivity(i);
                }
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

    private void iniciarSesionFb(String userId, String token, String name, String email) {
        final ProgressDialog progressDialog = ProgressDialog.show(this.getActivity(), null, mainActivity.getString(R.string.mensaje_progreso_validando_usuario), true, false);
        User user = new User();
        UserService userService = ServiceGenerator.getUserService();
        final String[] bodyString = new String[2];
        user.setName(name);
        user.setEmail(email);
        user.setToken(token);
        user.setUserFbId(userId);
        if (email == null || email.equals("")) {
            user.setEmail(userId + "@facebook.com");
        }

        userService.iniciarSesionFb(user.getName(),
                user.getEmail(),
                user.getUserFbId(),
                user.getToken(),
                new Callback<Response>() {
                    @Override
                    public void success(Response res, Response response) {
                        try {
                            bodyString[0] = new String(((TypedByteArray) res.getBody()).getBytes());
                            JSONObject resJson = new JSONObject(bodyString[0]);
                            guardarSesionUsuario(resJson);
                            progressDialog.setMessage(getString(R.string.mensaje_progreso_cargando_configuraciones));
                            ConfigurationService configurationService = ServiceGenerator.getConfigurationService();
                            String token = resJson.getString("token");
                            configurationService.obtenerConfiguracion(token, new Callback<Response>() {
                                @Override
                                public void success(Response response, Response response2) {
                                    bodyString[1] = new String(((TypedByteArray) response.getBody()).getBytes());
                                    final JSONArray resArrayJson;
                                    try {
                                        resArrayJson = new JSONArray(bodyString[1]);
                                        guardarConfiguraciones(resArrayJson);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    progressDialog.dismiss();
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    progressDialog.dismiss();
                                    mostrarToast(getString(R.string.mensaje_error_cargando_configuraciones));
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        progressDialog.dismiss();
                        mostrarAlertDialog(mainActivity.getString(R.string.mensaje_error_iniciando_sesion_fb));
                    }
                });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Facebook.
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void iniciarSesion() {
        final ProgressDialog progressDialog = ProgressDialog.show(this.getActivity(), null,
                mainActivity.getString(R.string.mensaje_progreso_iniciando_sesion), true, false);
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
                        try {
                            bodyString[0] = new String(((TypedByteArray) user.getBody()).getBytes());
                            final JSONObject resJson = new JSONObject(bodyString[0]);
                            guardarSesionUsuario(resJson);
                            progressDialog.setMessage(getString(R.string.mensaje_progreso_cargando_configuraciones));
                            ConfigurationService configurationService = ServiceGenerator.getConfigurationService();
                            String token = resJson.getString("token");
                            configurationService.obtenerConfiguracion(token, new Callback<Response>() {
                                @Override
                                public void success(Response response, Response response2) {
                                    bodyString[1] = new String(((TypedByteArray) response.getBody()).getBytes());
                                    final JSONArray resArrayJson;
                                    try {
                                        resArrayJson = new JSONArray(bodyString[1]);
                                        guardarConfiguraciones(resArrayJson);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    progressDialog.dismiss();
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    progressDialog.dismiss();
                                    mostrarToast(getString(R.string.mensaje_error_cargando_configuraciones));
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
                            JSONObject errorJson = new JSONObject(bodyString[0]);
                            mostrarToast(errorJson.getString("data"));
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
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
            Usuario usuario = new Usuario(user);
            dao = mainActivity.getHelper().getUsuarioDao();
            usuario.setIdLocal(1);
            dao.createOrUpdate(usuario);
            mainActivity.actualizarSesionMenuLateral();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void mostrarToast(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }

    private void mostrarAlertDialog(String s) {
        new AlertDialog.Builder(getActivity())
                .setTitle(mainActivity.getString(R.string.error))
                .setMessage(s)
                .setPositiveButton(mainActivity.getString(R.string.boton_aceptar), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}
