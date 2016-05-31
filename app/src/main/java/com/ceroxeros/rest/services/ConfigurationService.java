package com.ceroxeros.rest.services;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by juan on 30/05/16.
 */
public interface ConfigurationService {
    @FormUrlEncoded
    @POST("/api/v1/configuration/post")
    void crearConfiguracion(@Field("name") String name,
                            @Field("mode") String mode,
                            @Field("intensity") Float intensity,
                            @Field("token") String token,
                            Callback<Response> callback);

    @GET("/api/v1/configuration/get")
    void obtenerConfiguracione(@Query("token") String token,
                               Callback<Response> callback);
}
