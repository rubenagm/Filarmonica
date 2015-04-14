package mx.com.filarmonica;

/**
 * Created by root on 19/01/15.
 */
public class ItemFacebook {
    String contenido;
    String urlImagen;
    String urlFacebook;
    String id;
    public ItemFacebook(String id, String contenido,String urlImagen, String urlFacebook)
    {
        this.id = id;
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

    public String getId(){return id;}
}
