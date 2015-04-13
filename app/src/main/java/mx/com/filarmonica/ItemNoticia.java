package mx.com.filarmonica;

/**
 * Created by root on 16/01/15.
 */
public class ItemNoticia {
    int id;
    String titulo;
    String titulo_en;
    String contenido;
    String contenido_en;
    String fecha;
    String publicada;
    String fecha_creacion;

    public ItemNoticia(int id, String titulo, String titulo_en, String contenido, String contenido_en, String fecha, String publicada, String fecha_creacion){
        this.id = id;
        this.titulo = titulo;
        this.titulo_en = titulo_en;
        this.contenido = contenido;
        this.contenido_en = contenido_en;
        this.fecha = fecha;
        this.publicada = publicada;
        this.fecha_creacion = fecha_creacion;
    }

    public int getId(){return id;}
    public String getTitulo(){return titulo;}
    public String getTitulo_en(){return titulo_en;}
    public String getContenido(){return contenido;}
    public String getContenido_en(){return contenido_en;}
    public String getFecha(){return fecha;}
    public String getPublicada(){return publicada;}
    public String getFecha_creacion(){return fecha_creacion;}
}
