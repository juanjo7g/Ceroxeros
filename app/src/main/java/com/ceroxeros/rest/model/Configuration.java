package com.ceroxeros.rest.model;

import com.ceroxeros.modelo.Configuracion;

import java.util.Date;

/**
 * Created by juan on 30/05/16.
 */
public class Configuration {
    private String _id;
    private String name;
    private String mode;
    private Float intensity;
    private Date date;
    private String user_id;

    public Configuration() {
    }

    public Configuration(Configuracion configuracionAGuardar) {
        if (configuracionAGuardar != null) {
            if (configuracionAGuardar.getNombre() != null) {
                name = configuracionAGuardar.getNombre();
            }
            if (configuracionAGuardar.getModo() != null) {
                mode = configuracionAGuardar.getModo();
            }
            if (configuracionAGuardar.getIntensidad() != null) {
                intensity = configuracionAGuardar.getIntensidad();
            }
        }
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Float getIntensity() {
        return intensity;
    }

    public void setIntensity(Float intensity) {
        this.intensity = intensity;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
