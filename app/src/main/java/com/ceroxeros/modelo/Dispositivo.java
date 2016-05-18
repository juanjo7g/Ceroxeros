package com.ceroxeros.modelo;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by Juan on 18/05/2016.
 */
public class Dispositivo {
    public static final String ID_LOCAL = "id_local";

    public static final String MAC = "mac";
    public static final String USUARIO = "usuario";

    @DatabaseField(generatedId = true, columnName = ID_LOCAL)
    private int idLocal;

    @DatabaseField(columnName = MAC)
    private String mac;
    @DatabaseField(foreign = true, columnName = USUARIO)
    private Usuario usuario;

    public int getIdLocal() {
        return idLocal;
    }

    public void setIdLocal(int idLocal) {
        this.idLocal = idLocal;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
