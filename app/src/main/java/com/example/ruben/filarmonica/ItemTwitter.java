package com.example.ruben.filarmonica;

import java.util.ArrayList;

/**
 * Created by macmini3cuceimobile on 3/24/15.
 */
public class ItemTwitter {

    String text = "";
    String fecha = "";
    String urlImagen = "";
    String id = "";
    ArrayList<String> hashtags = new ArrayList<>();
    ArrayList<String> links = new ArrayList<>();
    ArrayList<String> users = new ArrayList<>();

    public ItemTwitter(String id,String text,String fecha) {
        this.id = id;
        this.text = text;
        this.fecha = fecha;
    }

    public void setUrlImagen(String urlImagen){
        this.urlImagen = urlImagen;
    }

    public String getText(){
        return text;
    }

    public String getUrlImagen(){
        return urlImagen;
    }

    public String getId(){
        return id;
    }

    public String getFecha(){
        return fecha;
    }

    public void addHashTags(String tag){
        hashtags.add(tag);
    }
    public ArrayList<String> getHashtags(){
        return hashtags;
    }

    public void addLinks(String link){
        links.add(link);
    }

    public ArrayList<String> getLinks(){
        return links;
    }

    public void addUsers(String user){
        users.add(user);
    }
    public ArrayList<String> getUsers(){
        return users;
    }
}
