package com.ceroxeros.rest;

import com.ceroxeros.rest.services.ConfigurationService;
import com.ceroxeros.rest.services.QualificationService;
import com.ceroxeros.rest.services.UserService;
import com.squareup.okhttp.OkHttpClient;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by juan on 29/05/16.
 */
public class ServiceGenerator {

    public static final String API_BASE_URL = "https://api-ceroxeros.herokuapp.com";
//    public static final String API_BASE_URL_LOCAL = "http://localhost:8080";


    private static final RestAdapter REST_ADAPTER = new RestAdapter.Builder()
            .setEndpoint(API_BASE_URL)
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .setClient(new OkClient(new OkHttpClient()))
            .build();

    private static final UserService USER_SERVICE = REST_ADAPTER.create(UserService.class);
    private static final ConfigurationService CONFIGURATION_SERVICE = REST_ADAPTER.create(ConfigurationService.class);
    private static final QualificationService QUALIFICATION_SERVICE = REST_ADAPTER.create(QualificationService.class);

    public static UserService getUserService() {
        return USER_SERVICE;
    }

    public static ConfigurationService getConfigurationService() {
        return CONFIGURATION_SERVICE;
    }

    public static QualificationService getQualificationService() {
        return QUALIFICATION_SERVICE;
    }
}