package com.ceroxeros.rest.services;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by juan on 14/06/16.
 */
public interface QualificationService {
    @FormUrlEncoded
    @POST("/api/v1/qualification/post")
    void crearCalificacion(@Field("mode") String mode,
                           @Field("intensity") Float intensity,
                           @Field("quantity") Float quantity,
                           @Field("feedback") String feedback,
                           @Field("comfort") int comfort,
                           @Field("satisfaction") int satisfaction,
                           Callback<Response> callback);
}
