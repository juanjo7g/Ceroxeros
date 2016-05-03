package com.ceroxeros.modelo;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

import java.util.Date;

/**
 * Created by Juan on 02/05/2016.
 */
public class Usuario {
    public static final String ID_LOCAL = "id_local";
    public static final String ID_REMOTO = "id_remoto";

    public static final String TOKEN = "token";

    public static final String NOMBRE_USUARIO = "nombre_usuario";
    public static final String NOMBRE_COMPLETO = "nombre_completo";
    public static final String FECHA_NACIMIENTO = "fecha_nacimiento";
    public static final String CORREO = "correo";
    public static final String FOTO = "foto";

    public static final String SINCRONIZADO = "sincronizado";

    @DatabaseField(generatedId = true, columnName = ID_LOCAL)
    private int idLocal;
    @DatabaseField(columnName = ID_REMOTO)
    private String idRemoto;

    @DatabaseField(columnName = TOKEN)
    private String token;

    @DatabaseField(columnName = NOMBRE_USUARIO)
    private String nombreUsuario;
    @DatabaseField(columnName = NOMBRE_COMPLETO)
    private String nombreCompleto;
    @DatabaseField(columnName = FECHA_NACIMIENTO)
    private Date fechaNacimiento;
    @DatabaseField(columnName = CORREO)
    private String correo;
    @DatabaseField(columnName = FOTO, dataType = DataType.BYTE_ARRAY)
    private byte[] foto;

    @DatabaseField(columnName = SINCRONIZADO)
    private Boolean sincronizado = Boolean.FALSE;

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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public byte[] getFoto() {
        return foto;
    }

    public void setFoto(byte[] foto) {
        this.foto = foto;
    }

    public Boolean getSincronizado() {
        return sincronizado;
    }

    public void setSincronizado(Boolean sincronizado) {
        this.sincronizado = sincronizado;
    }
}
