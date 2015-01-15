package com.example.ruben.filarmonica;

/**
 * Created by Ruben on 14/01/2015.
 */
public class ItemYoutube {
    String titulo;
    String contenido;
    String duracion;
    String views;
    String fechaPublicacion;
    String urlImagen;

    public ItemYoutube(String titulo,String contenido, String duracion, String views, String fechaPublicacion,String urlImagen){
        this.titulo = titulo;
        this.contenido = contenido;
        this.duracion = duracion;
        this.views = views;
        this.fechaPublicacion = fechaPublicacion;
        this.urlImagen = urlImagen;
    }

    public String getTitulo(){
        return titulo;
    }
    public String getContenido(){
        return contenido;
    }
    public String getDuracion(){
        return duracion;
    }
    public String getViews(){
        return views;
    }
    public String getUrlImagen(){return urlImagen;}

    public String getFechaPublicacion(){
        return fechaPublicacion;
    }

}
