package com.ceroxeros.view.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
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
import retrofit.http.FormUrlEncoded;
import retrofit.mime.TypedByteArray;

public class IniciarSesionFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private TextView info;
    private LoginButton loginButtonFacebook;
    private CallbackManager callbackManager;
    private GoogleApiClient mGoogleApiClient;

    private static final int RC_SIGN_IN = 9001;

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
        info = (TextView) view.findViewById(R.id.tokentext);

        /** Inicio de sesion con Facebook. */

        loginButtonFacebook = (LoginButton) view.findViewById(R.id.login_button);
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
                                    info.setText(texto);
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

        /** Inicio de sesion con Google. */
        view.findViewById(R.id.sign_in_button).setOnClickListener(this);
        view.findViewById(R.id.btnSignOut).setOnClickListener(this);

        // Configurando inicio de sesion para obtener los datos deseados.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId()
                .requestProfile()
                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .build();

        // Construyendo un GoogleApiClient con acceso al Google Sign In API y las opciones especificadas en el gso.
        mGoogleApiClient = new GoogleApiClient.Builder(view.getContext())
                .enableAutoManage(this.getActivity(), this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Personalizando el boton de google+
        SignInButton signInButton = (SignInButton) view.findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());

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
                            //todo: revisar si se esta guardando el usuario que inicia sesion :s
                            progressDialog.setMessage("Cargando configuraciones favoritas");
                            ConfigurationService configurationService = ServiceGenerator.getConfigurationService();
                            String token = resJson.getString("token");
                            configurationService.obtenerConfiguracione(token, new Callback<Response>() {
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
                        progressDialog.dismiss();
                        bodyString[0] = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
                        //todo: Manejar error
                        Toast.makeText(getActivity(), "Res: " + bodyString[0], Toast.LENGTH_SHORT).show();
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

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Facebook.
        callbackManager.onActivityResult(requestCode, resultCode, data);

        // Google.
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }

    /**
     * Función para ver el estado de la petición. Si fue satisfactoria u ocurrió algún error.
     *
     * @param result = Trae el estado de la conexión con los servidores de google.
     */
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("Sign in: ", "HandleSignInResult: " + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            String texto = "User Id: "
                    + acct.getId()
                    + "\n" +
                    "Name: "
                    + acct.getDisplayName()
                    + "\n" +
                    "Email: "
                    + acct.getEmail();
            info.setText(texto);
            updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }

    /**
     * Define las acciones a seguir cuando se presione en un boton de la aplicación.
     *
     * @param v = vista que manejará los eventos.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.btnSignOut:
                signOut();
                break;
        }
    }

    /**
     * Función llamada cuando se presiona el boton para iniciar sesión.
     */
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * Funcion llamada cuando se presiona el boton para salir de la sesión
     */
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        updateUI(false);
                    }
                });
    }

    /**
     * Función para actualziar la vista, esconde el botón de inicio de sesión o no, de acuerdo a
     * el estado del loggeo.
     *
     * @param signedIn = True/False para identificar si la sesión esta activa o no.
     */
    private void updateUI(boolean signedIn) {
        if (signedIn) {
            getActivity().findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            getActivity().findViewById(R.id.btnSignOut).setVisibility(View.VISIBLE);
        } else {
            info.setText("Se ha deslogueado.");
            getActivity().findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.btnSignOut).setVisibility(View.GONE);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("SignInActivity", "onConnectionFailed:" + connectionResult);
    }
}
