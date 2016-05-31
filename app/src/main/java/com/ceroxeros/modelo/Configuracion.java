package com.ceroxeros.modelo;

import com.ceroxeros.rest.model.Configuration;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by Juan on 02/05/2016.
 */
@DatabaseTable
public class Configuracion {
    public static final String ID_LOCAL = "id_local";
    public static final String ID_REMOTO = "id_remoto";

    public static final String NOMBRE = "nombre";
    public static final String USUARIO = "usuario";
    public static final String FECHA_CREACION = "fecha_creacion";

    public static final String MODO = "modo";
    public static final String INTENSIDAD = "intensidad";

    public static final String ELIMINADO = "eliminado";
    public static final String SINCRONIZADO = "sincronizado";

    @DatabaseField(generatedId = true, allowGeneratedIdInsert = true, columnName = ID_LOCAL)
    private int idLocal;
    @DatabaseField(columnName = ID_REMOTO)
    private String idRemoto;

    @DatabaseField(columnName = NOMBRE)
    private String nombre;
    @DatabaseField(foreign = true, columnName = USUARIO)
    private Usuario usuario;
    @DatabaseField(columnName = FECHA_CREACION)
    private Date fechaCreacion;

    @DatabaseField(columnName = MODO)
    private String modo;
    @DatabaseField(columnName = INTENSIDAD)
    private Float intensidad;

    @DatabaseField(columnName = ELIMINADO)
    private Boolean eliminado = Boolean.FALSE;
    @DatabaseField(columnName = SINCRONIZADO)
    private Boolean sincronizado = Boolean.FALSE;

    public Configuracion() {
    }

    public Configuracion(Configuration configuration) {
        if (configuration != null) {
            if (configuration.get_id() != null) {
                idRemoto = configuration.get_id();
            }
            if (configuration.getName() != null) {
                nombre = configuration.getName();
            }
            if (configuration.getMode() != null) {
                modo = configuration.getMode();
            }
            if (configuration.getIntensity() != null) {
                intensidad = configuration.getIntensity();
            }
        }
    }

    public int getIdLocal() {
        return idLocal;
    }

    public void setIdLocal(int idLocal) {
        this.idLocal = idLocal;
    }

    public String getIdRemoto() {
        return idRemoto;
    }

    public void setIdRemoto(String idRemoto) {
        this.idRemoto = idRemoto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getModo() {
        return modo;
    }

    public void setModo(String modo) {
        this.modo = modo;
    }

    public Float getIntensidad() {
        return intensidad;
    }

    public void setIntensidad(Float intensidad) {
        this.intensidad = intensidad;
    }

    public Boolean getEliminado() {
        return eliminado;
    }

    public void setEliminado(Boolean eliminado) {
        this.eliminado = eliminado;
    }

    public Boolean getSincronizado() {
        return sincronizado;
    }

    public void setSincronizado(Boolean sincronizado) {
        this.sincronizado = sincronizado;
    }

    @Override
    public String toString() {
        return "Modo: " + getModo() + " Intensidad: " + getIntensidad();
    }
}
