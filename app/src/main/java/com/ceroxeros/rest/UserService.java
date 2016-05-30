package com.ceroxeros.rest;

import com.ceroxeros.rest.model.User;

import org.json.JSONObject;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

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
}
