package com.example.ruben.filarmonica;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.jsoup.Jsoup;

import java.util.ArrayList;

public class ConexionBD extends SQLiteOpenHelper{
	private static String DATABASE_NAME = "prueba";
	private static int DATABASE_VERSION = 1;
    private static String SQL_SELECT_ID_MAYOR_LOCALIDAD_EVENTO = "SELECT MAX(id) AS id " +
            "FROM localidad_evento";
    private static String SQL_SELECT_ID_MAYOR_FECHA = "SELECT MAX(id) AS id FROM fecha";
    private static String SQL_SELECT_ID_MAYOR_NOTICIA = "SELECT MAX(id) AS id FROM noticia";
    private static String SQL_SELECT_ID_MAYOR_EVENTO="SELECT MAX(id) AS id FROM evento";
    private static String SQL_SELECT_NOTICIA = "SELECT * FROM noticia";
    private static String SQL_CREATE_TABLA_NOTICIA = "CREATE TABLE noticia (id INTEGER, " +
            "titulo TEXT,titulo_en TEXT,contenido TEXT, contenido_en TEXT,fecha DATE, " +
            "publicada TEXT, fecha_creacion TEXT)";
    private static String SQL_CREATE_TABLA_VIDEOS = "CREATE TABLE video (titulo TEXT, " +
            "contenido TEXT,duracion TEXT,urlimagen TEXT)";
    private static String SQL_SELECT_TABLA_LOCALIDAD_EVENTO = "SELECT nombre,costo " +
            "FROM localidad_evento WHERE evento_id = ";
    private static String SQL_CREATE_TABLA_LOCALIDAD = "CREATE TABLE localidad " +
            "(id INTEGER PRIMARY KEY, nombre TEXT , costo TEXT,sede_id INTEGER)";
    private static String SQL_CREATE_TABLA_LOCALIDAD_EVENTO = "CREATE TABLE localidad_evento " +
            "(id INTEGER PRIMARY KEY, nombre TEXT, costo TEXT,evento_id INTEGER , " +
            "FOREIGN KEY (evento_id) REFERENCES evento (id))";
	private static String SQL_CREATE_TABLA_EVENTO = "CREATE TABLE evento (id integer PRIMARY KEY, " +
            "programa text, programa_en text, titulo text, titulo_en text, descripcion text, " +
            "descripcion_en text, estado text, temporada_id integer);";
	private static String SQL_CREATE_TABLA_FECHA = "CREATE TABLE fecha (id integer PRIMARY KEY, " +
            "fecha date, hora text,minuto text,evento_id integer," +
            " FOREIGN KEY (evento_id) REFERENCES evento (id));";
    private static String SQL_SELECT_EVENTO = "SELECT E.id ,E.programa,E.programa_en,E.titulo," +
            "E.titulo_en,E.descripcion,E.descripcion_en,E.estado,E.temporada_id " +
            "FROM evento AS E JOIN fecha AS F " +
            "ON F.evento_id = E.id GROUP BY E.id ORDER BY E.id desc ";
    private static String SQL_SELECT_EVENTO_UNICO = "SELECT E.id ,E.programa,E.programa_en," +
            "E.titulo,E.titulo_en,E.descripcion,E.descripcion_en,E.estado,E.temporada_id " +
            "FROM evento AS E JOIN fecha AS F ON F.evento_id = E.id WHERE E.id = "; //Parte que falta GROUP BY E.id
	private static String SQL_DELETE_DATOS_EVENTOS = "DELETE FROM evento";
	private static String SQL_DELETE_DATOS_FECHAS = "DELETE FROM fecha";
	private static String SQL_SELECT_FECHA_EVENTOS = "SELECT * FROM fecha WHERE evento_id = ";

    // Base de datos de Instagram

    private final String SQL_CREATE_TABLE_INSTAGRAM = "CREATE TABLE Instagram (ImagenNd TEXT UNIQUE,ImagenHd TEXT UNIQUE,Texto TEXT,Link TEXT)";
	private final String SQL_SELECT_INSTAGRAM = "SELECT * FROM Instagram";

    public ConexionBD(Context contexto){
		super(contexto,DATABASE_NAME,null,DATABASE_VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_TABLA_EVENTO);
		db.execSQL(SQL_CREATE_TABLA_FECHA);
        db.execSQL(SQL_CREATE_TABLA_LOCALIDAD);
        db.execSQL(SQL_CREATE_TABLA_LOCALIDAD_EVENTO);
        db.execSQL(SQL_CREATE_TABLA_VIDEOS);
        db.execSQL(SQL_CREATE_TABLA_NOTICIA);
        db.execSQL(SQL_CREATE_TABLE_INSTAGRAM);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

    //OBTENER ID MAYOR DE LA TABLA EVENTO
    public int obtenerIdMayorEvento(){

        int id = 0;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery(SQL_SELECT_ID_MAYOR_EVENTO,null);

        res.moveToPosition(0);

        id = res.getInt(res.getColumnIndex("id"));
        Log.i("SELECT DB","id mayor " + id);
        return id;
    }
    //OBTENER MAYOR ID DE LA TABLA NOTICIA
    public int obtenerIdMayorNoticia(){
        int id = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery(SQL_SELECT_ID_MAYOR_NOTICIA,null);

        res.moveToFirst();
        id = res.getInt(res.getColumnIndex("id"));
        Log.i("SELECT DB","id mayor Noticia " + id );
        return id;
    }

    //OBTENER ID MAYOR DE LA TABLA FECHA
    public int obtenerIdMayorFecha(){
        int id = 0;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery(SQL_SELECT_ID_MAYOR_FECHA,null);

        res.moveToFirst();

        id = res.getInt(res.getColumnIndex("id"));
        Log.i("SELECT DB","id mayor fecha "+ id);
        return id;
    }

    //OBTENER ID MAYOR DE LA TABLA LOCALIDAD_EVENTO
    public int obtenerIdMayorLocalidadEvento(){
        int id = 0;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery(SQL_SELECT_ID_MAYOR_LOCALIDAD_EVENTO,null);

        res.moveToFirst();

        id = res.getInt(res.getColumnIndex("id"));
        Log.i("SELECT DB","id mayor localidad_evento "+ id);
        return id;
    }




	//INSERTAR UN EVENTO
	public boolean insertEvento (int id, String programa,String programa_en,String titulo,
                String titulo_en,String descripcion, String descripcion_en,String estado,
                int temporada_id){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("id",id );
		contentValues.put("programa", programa);
		contentValues.put("programa_en",programa_en );
		contentValues.put("titulo", titulo);
		contentValues.put("titulo_en", titulo_en);
		contentValues.put("descripcion", descripcion );
		contentValues.put("descripcion_en", descripcion_en);
		contentValues.put("estado", estado );
		contentValues.put("temporada_id", temporada_id );
		db.insert("evento", null, contentValues);
		Log.i("Insert DB", "Se ha insertado 1 evento");
		return true;
	}

	//INSERTAR UNA NOTICIA
    public boolean insertNoticia(int id, String titulo, String titulo_en,String contenido,
                 String contenido_en, String fecha , String publicada, String fecha_creacion){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id",id);
        contentValues.put("titulo",titulo);
        contentValues.put("titulo_en",titulo_en);
        contentValues.put("contenido",contenido);
        contentValues.put("contenido_en",contenido_en);
        contentValues.put("fecha",fecha);
        contentValues.put("publicada",publicada);
        contentValues.put("fecha_creacion",fecha_creacion);

        db.insert("noticia",null,contentValues);
        Log.i("Insert DB", "Noticia insertada");
        return true;
    }

    //OBTENER NOTICIAS
    public ArrayList<ItemNoticia> obtenerNoticias(){
        ArrayList<ItemNoticia> noticias = new ArrayList<>();

        int id;
        String titulo;
        String titulo_en;
        String contenido;
        String contenido_en;
        String fecha;
        String publicada;
        String fecha_creacion;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery(SQL_SELECT_NOTICIA,null);

        res.moveToFirst();

        while(res.isAfterLast() == false){
            id = res.getInt(res.getColumnIndex("id"));
            titulo = res.getString(res.getColumnIndex("titulo"));
            titulo_en = res.getString(res.getColumnIndex("titulo_en"));
            contenido = res.getString(res.getColumnIndex("contenido"));
            contenido_en = res.getString(res.getColumnIndex("contenido_en"));
            fecha = res.getString(res.getColumnIndex("fecha"));
            publicada = res.getString(res.getColumnIndex("publicada"));
            fecha_creacion = res.getString(res.getColumnIndex("fecha_creacion"));
            contenido = Jsoup.parse(contenido).text();
            noticias.add(new ItemNoticia(id,titulo,titulo_en,contenido,contenido_en,fecha,publicada,
                    fecha_creacion));

            res.moveToNext();
        }

        Log.i("SELECT DB","Obtenidas las noticias");
        return noticias;
    }


	//INSERTAR UNA FECHA
	public boolean insertFecha(int id, String fecha, String hora, String minuto, int evento_id){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contenValues = new ContentValues();
		contenValues.put("id", id);
		contenValues.put("fecha",fecha );
		contenValues.put("hora", hora);
		contenValues.put("minuto", minuto);
		contenValues.put("evento_id",  evento_id);
		db.insert("fecha", null, contenValues);
		Log.i("Insert DB", "Se ha insertado una fecha");
		return true;
	}

    //INSERTAR UNA LOCALIDAD
    public boolean insertLocalidad(int id,String nombre , String costo,int id_sede){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id",id);
        contentValues.put("nombre",nombre);
        contentValues.put("costo",costo);
        contentValues.put("sede_id",id_sede);
        db.insert("localidad",null,contentValues);
        Log.i("Insert DB","Localidad insertada");
        return true;
    }

    public boolean insertLocalidadEvento(int id , String nombre, String costo, int id_evento){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id",id);
        contentValues.put("nombre",nombre);
        contentValues.put("costo",costo);
        contentValues.put("evento_id",id_evento);
        db.insert("localidad_evento",null,contentValues);
        Log.i("Insert DB","Localidad_evento Insertado");
        return true;
    }
	
	//LIMPIA LA BASE DE DATOS PARA VOLVER A SINCRONIZAR LOS DATOS
	public boolean limpiarDB(){
		SQLiteDatabase db = this.getReadableDatabase();
		db.execSQL(SQL_DELETE_DATOS_EVENTOS);
		db.execSQL(SQL_DELETE_DATOS_FECHAS);
		return true;
	}
	
	//DEVUELVE LOS EVENTOS M�S RECIENTES
	public ArrayList<ItemEvento> obtenerEventos(){
		ArrayList<ItemEvento> eventos = new ArrayList<ItemEvento>();
		//variables temporales para asignar al arreglo
		int id;
		String programa;
		String programa_en;
		String titulo;
		String titulo_en;
		String descripcion;
		String descripcion_en;
		String estado;
		int temporada_id;
		//
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor res = db.rawQuery(SQL_SELECT_EVENTO, null);
		
		res.moveToFirst();
		int contadorEvento = 0;
        Log.i("Base de datos","Comienza la obtencion de eventos");
		while(res.isAfterLast()==false){

			id = res.getInt(res.getColumnIndex("id"));
			programa = res.getString(res.getColumnIndex("programa"));
			programa_en = res.getString(res.getColumnIndex("programa_en"));
			titulo = res.getString(res.getColumnIndex("titulo"));
			titulo_en = res.getString(res.getColumnIndex("titulo_en"));
			descripcion = res.getString(res.getColumnIndex("descripcion"));
			descripcion_en = res.getString(res.getColumnIndex("descripcion_en"));
			estado = res.getString(res.getColumnIndex("estado"));
			temporada_id = res.getInt(res.getColumnIndex("temporada_id"));
			eventos.add(new ItemEvento(id, programa, programa_en, titulo,titulo_en, descripcion,
                    descripcion_en, estado, temporada_id));
			//Consulta las fechas para el evento
			Cursor resFecha = db.rawQuery(SQL_SELECT_FECHA_EVENTOS + id, null);
			resFecha.moveToFirst();
			while(resFecha.isAfterLast() == false){
				String fecha_f = resFecha.getString(resFecha.getColumnIndex("fecha"));
				fecha_f += "/"+resFecha.getString(resFecha.getColumnIndex("hora")); 
				fecha_f+= "/"+ resFecha.getString(resFecha.getColumnIndex("minuto"));
				eventos.get(contadorEvento).addFecha(fecha_f);
				resFecha.moveToNext();
			}
			//Terminan las consultas de fechas.
			
			res.moveToNext();
			contadorEvento ++;
		}
		
		Log.i("Reegresa eventos", "Resultado\n" + eventos.toArray().toString());
		return eventos;
	}
    //// Guardar en tabla datos Instagram
    public void insertarImagenInstagram(String imagenNd,String imagenHd,String texto,String link){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("ImagenHd",imagenHd);
        contentValues.put("ImagenNd",imagenNd);
        contentValues.put("Texto",texto);
        contentValues.put("Link",link);

        db.insert("Instagram",null,contentValues);
        Log.i("Instagram","Datos insertados");
    }

    ////Obtener datos Instagram
    public ArrayList<ItemImagenInstagram> obtenerDatosInstragram(){
        ArrayList<ItemImagenInstagram> imagenes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery(SQL_SELECT_INSTAGRAM,null);
        res.moveToFirst();


        while(res.isAfterLast() == false){
            int contador = 0;
            String urlImagenNd1="";
            String urlImagenNd2="";
            String urlImagenNd3="";
            String textoImagen1="";
            String textoImagen2="";
            String textoImagen3="";
            String urlImagenHd1="";
            String urlImagenHd2="";
            String urlImagenHd3="";
            String link1 = "";
            String link2 = "";
            String link3 = "";
            if(res.isAfterLast()==false) {
                urlImagenNd1 = res.getString(res.getColumnIndex("ImagenNd"));
                urlImagenHd1 = res.getString(res.getColumnIndex("ImagenHd"));
                textoImagen1 = res.getString(res.getColumnIndex("Texto"));
                link1 = res.getString(res.getColumnIndex("Link"));
                res.moveToNext();
            }
            if(res.isAfterLast()==false) {
                urlImagenNd2 = res.getString(res.getColumnIndex("ImagenNd"));
                urlImagenHd2 = res.getString(res.getColumnIndex("ImagenHd"));
                textoImagen2 = res.getString(res.getColumnIndex("Texto"));
                link2 = res.getString(res.getColumnIndex("Link"));
                res.moveToNext();
            }
            if(res.isAfterLast()==false) {
                urlImagenNd3 = res.getString(res.getColumnIndex("ImagenNd"));
                urlImagenHd3 = res.getString(res.getColumnIndex("ImagenHd"));
                textoImagen3 = res.getString(res.getColumnIndex("Texto"));
                link3 = res.getString(res.getColumnIndex("Link"));
                res.moveToNext();
            }
            imagenes.add(new ItemImagenInstagram(urlImagenHd1,urlImagenHd2,urlImagenHd3,urlImagenNd1,urlImagenNd2,urlImagenNd3,textoImagen1,textoImagen2,textoImagen3,link1,link2,link3));

        }
        return imagenes;
    }

    /**************************************Obtener un evento ******************///

    public ArrayList<ItemEvento> obtenerUnEvento(String idEvento){
        ArrayList<ItemEvento> evento = new ArrayList<ItemEvento>();
        //variables temporales para asignar al arreglo
        int id;
        String programa;
        String programa_en;
        String titulo;
        String titulo_en;
        String descripcion;
        String descripcion_en;
        String estado;
        int temporada_id;
        //

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery(SQL_SELECT_EVENTO_UNICO + idEvento + " GROUP BY E.id",null);

        res.moveToFirst();
        int contadorEvento = 0;
        Log.i("Base de datos","Comienza la obtencion de eventos");
        while(res.isAfterLast()==false){

            id = res.getInt(res.getColumnIndex("id"));
            programa = res.getString(res.getColumnIndex("programa"));
            programa_en = res.getString(res.getColumnIndex("programa_en"));
            titulo = res.getString(res.getColumnIndex("titulo"));
            titulo_en = res.getString(res.getColumnIndex("titulo_en"));
            descripcion = res.getString(res.getColumnIndex("descripcion"));
            descripcion_en = res.getString(res.getColumnIndex("descripcion_en"));
            estado = res.getString(res.getColumnIndex("estado"));
            temporada_id = res.getInt(res.getColumnIndex("temporada_id"));

            descripcion = Jsoup.parse(descripcion.replaceAll("(?i)<br[^>]*>", "br2n")).text();
            descripcion = descripcion.replaceAll("br2n", "\n\n");

            descripcion_en = Jsoup.parse(descripcion_en).text();
            evento.add(new ItemEvento(id, programa, programa_en, titulo,titulo_en,
                    descripcion, descripcion_en, estado, temporada_id));

            //Consulta las fechas para el evento
            Cursor resFecha = db.rawQuery(SQL_SELECT_FECHA_EVENTOS + id, null);
            resFecha.moveToFirst();
            while(resFecha.isAfterLast() == false){
                String fecha_f = resFecha.getString(resFecha.getColumnIndex("fecha"));
                fecha_f += "/"+resFecha.getString(resFecha.getColumnIndex("hora"));
                fecha_f+= "/"+ resFecha.getString(resFecha.getColumnIndex("minuto"));
                evento.get(contadorEvento).addFecha(fecha_f);
                resFecha.moveToNext();
            }
            //Terminan las consultas de fechas

            //CONSULTAR LOCALIDADES Y COSTOS
            Cursor resLocalidades = db.rawQuery(SQL_SELECT_TABLA_LOCALIDAD_EVENTO + id, null);
            resLocalidades.moveToFirst();
            while (resLocalidades.isAfterLast() == false){
                String nombre = resLocalidades.getString(resLocalidades.getColumnIndex("nombre"));
                String costo = resLocalidades.getString(resLocalidades.getColumnIndex("costo"));
                evento.get(contadorEvento).addLocalidad(nombre);
                evento.get(contadorEvento).addCosto(costo);
                resLocalidades.moveToNext();
            }
            //SE TERMINA DE CONSULTAR LOCALIDADES Y COSTOS

            res.moveToNext();
            contadorEvento ++;
        }
        return evento;
    }
}
