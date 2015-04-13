package mx.com.filarmonica;

/**
 * Created by macmini3cuceimobile on 2/27/15.
 */
public class ItemImagenInstagram {
    String urlImagenNd1;
    String urlImagenNd2;
    String urlImagenNd3;
    String textoImagen1;
    String textoImagen2;
    String textoImagen3;
    String urlImagenHd1;
    String urlImagenHd2;
    String urlImagenHd3;
    String link1;
    String link2;
    String link3;

    public ItemImagenInstagram(String urlImagenHd1,String urlImagenHd2,String urlImagenHd3,String urlImagenNd1,String urlImagenNd2,String urlImagenNd3,String textoImagen1,String textoImagen2,String textoImagen3,String link1, String link2, String link3){
        this.urlImagenNd1 = urlImagenNd1;
        this.urlImagenNd2 = urlImagenNd2;
        this.urlImagenNd3 = urlImagenNd3;
        this.urlImagenHd1 = urlImagenHd1;
        this.urlImagenHd2 = urlImagenHd2;
        this.urlImagenHd3 = urlImagenHd3;
        this.textoImagen1 = textoImagen1;
        this.textoImagen2 = textoImagen2;
        this.textoImagen3 = textoImagen3;
        this.link1 = link1;
        this.link2 = link2;
        this.link3 = link3;
    }

    public String getUrlImagenNd1(){
        return urlImagenNd1;
    }
    public String getUrlImagenHd1(){
        return urlImagenHd1;
    }
    public String getTextoImagen1(){
        return textoImagen1;
    }
    public String getUrlImagenNd2(){
        return urlImagenNd2;
    }
    public String getUrlImagenHd2(){
        return urlImagenHd2;
    }
    public String getTextoImagen2(){
        return textoImagen2;
    }
    public String getUrlImagenNd3(){
        return urlImagenNd3;
    }
    public String getUrlImagenHd3(){
        return urlImagenHd3;
    }
    public String getTextoImagen3(){
        return textoImagen3;
    }
    public String getLink1(){return link1;}
    public String getLink2(){return link2;}
    public String getLink3(){return link3;}



}
