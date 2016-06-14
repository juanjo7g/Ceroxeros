package com.ceroxeros.rest.services;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by juan on 29/05/16.
 */
public interface UserService {
    @FormUrlEncoded
    @POST("/api/v1/user/post")
    void crearUsuario(@Field("name") String name,
                      @Field("username") String username,
                      @Field("password1") String password1,
                      @Field("password2") String password2,
                      @Field("email") String email,
                      Callback<Response> callback);

    @FormUrlEncoded
    @POST("/api/v1/user/loginFb")
    void iniciarSesionFb(@Field("name") String name,
                         @Field("email") String email,
                         @Field("userFbId") String userFbId,
                         @Field("token") String token,
                         Callback<Response> callback);

    @GET("/api/v1/user/login")
    void iniciarSesion(@Query("username") String username,
                       @Query("password") String password,
                       Callback<Response> callback);
}
