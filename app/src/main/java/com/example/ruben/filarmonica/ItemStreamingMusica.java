package com.example.ruben.filarmonica;

/**
 * Created by macmini3cuceimobile on 2/13/15.
 */
public class ItemStreamingMusica {
    int id;
    String titulo;
    String director;
    String duracion;
    String url;

    public ItemStreamingMusica(int id, String titulo, String director, String duracion,String url){
        this.id = id;
        this.titulo = titulo;
        this.director = director;
        this.duracion = duracion;
        this.url = url;

    }
    public int getId(){
        return id;     }

    public String getUrl(){return url;}
    public String getTitulo(){
        return titulo;
    }
    public String getDirector(){
        return director;
    }
    public String getDuracion(){
        return duracion;
    }
}
