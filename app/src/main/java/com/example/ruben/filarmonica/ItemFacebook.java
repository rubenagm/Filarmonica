package com.example.ruben.filarmonica;

/**
 * Created by root on 19/01/15.
 */
public class ItemFacebook {
    String contenido;
    String urlImagen;
    String urlFacebook;
    public ItemFacebook(String contenido,String urlImagen, String urlFacebook)
    {
        this.contenido   = contenido;
        this.urlImagen   = urlImagen;
        this.urlFacebook = urlFacebook;
    }

    public String getContenido(){
        return contenido;
    }

    public String getUrlImagen(){
        return urlImagen;
    }

    public String getUrlFacebook()
    {
        return urlFacebook;
    }
}
