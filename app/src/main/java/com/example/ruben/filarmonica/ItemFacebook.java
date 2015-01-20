package com.example.ruben.filarmonica;

/**
 * Created by root on 19/01/15.
 */
public class ItemFacebook {
    String contenido;
    String urlImagen;
    public ItemFacebook(String contenido,String urlImagen){
        this.contenido = contenido;
        this.urlImagen = urlImagen;
    }

    public String getContenido(){
        return contenido;
    }

    public String getUrlImagen(){
        return urlImagen;
    }
}
