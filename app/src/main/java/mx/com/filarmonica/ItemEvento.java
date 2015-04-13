package mx.com.filarmonica;

import java.util.ArrayList;

public class ItemEvento  implements Comparable<ItemEvento>
{
	int id;
	String programa;
	String programa_en;
	String titulo;
	String titulo_en;
	String descripcion;
	String descripcion_en;
	String estado;
	int temporada_id;
    boolean evento_culminado;
	ArrayList<String> fechas = new ArrayList<String>();
	ArrayList<String> localidades = new ArrayList<String>();
	ArrayList<String> costos = new ArrayList<String>();
	public ItemEvento(int id, String programa,String programa_en,String titulo,String titulo_en,String descripcion, String descripcion_en,String estado,int temporada_id){
		this.id = id;
		this.programa = programa;
		this.programa_en = programa_en;
		this.titulo = titulo;
		this.titulo_en = titulo_en;
		this.descripcion = descripcion;
		this.descripcion_en = descripcion;
		this.estado = estado;
		this.temporada_id = temporada_id;
        this.evento_culminado = false;
	}


    //Método para comparar las fechas de los eventos y así poder ordenarlos.
    @Override
    public int compareTo(ItemEvento otroEvento)
    {
        //Comparamos la primera fecha del evento contra la primera fecha del otro evento.
        return otroEvento.getFechas().get(0).compareTo(this.getFechas().get(0));
    }

    public void addCosto(String costo){  costos.add(costo); }
	public int getId (){
		return id;
	}
	public String getPrograma(){
		return programa;
	}
	public String getProgramaEn(){
		return programa_en;
	}
	public String getTitulo(){
		return titulo;
	}
	public String getTituloEn(){
		return titulo_en;
	}
	public String getDescripcion(){
		return descripcion;
	}
	public String getDescripcionEn(){
		return descripcion_en;
	}
	public String getEstado(){
		return estado;
	}
	public int getTemporadaId(){
		return temporada_id;
	}
	public void addFecha(String fecha){
		fechas.add(fecha);
	}
	public void addLocalidad(String localidad){
		localidades.add(localidad);
	}
	public ArrayList<String> getFechas(){
		return fechas;
	}
	public ArrayList<String> getLocalidades(){
		return localidades;
	}
	public int getCountFechas(){
		return fechas.size();
	}
    public ArrayList<String> getCostos(){return costos; }
	public int getCountLocalidades(){
		return localidades.size();
	}
    public void setEventoCulminado(boolean eventoCulminado)
    {
        this.evento_culminado = eventoCulminado;
    }
    public boolean getEventoCulminado()
    {
        return evento_culminado;
    }
}
