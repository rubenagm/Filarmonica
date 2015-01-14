package com.example.ruben.filarmonica;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ConexionBD extends SQLiteOpenHelper{
	private static String DATABASE_NAME = "prueba";
	private static int DATABASE_VERSION = 1;
	private static String SQL_CREATE_TABLA_EVENTO = "CREATE TABLE evento (id integer PRIMARY KEY, programa text, programa_en text, titulo text, titulo_en text, descripcion text, descripcion_en text, estado text, temporada_id integer);";
	private static String SQL_CREATE_TABLA_FECHA = "CREATE TABLE fecha (id integer PRIMARY KEY, fecha date, hora text,minuto text,evento_id integer, FOREIGN KEY (evento_id) REFERENCES evento (id));";
    private static String SQL_SELECT_EVENTO = "SELECT E.id ,E.programa,E.programa_en,E.titulo,E.titulo_en,E.descripcion,E.descripcion_en,E.estado,E.temporada_id FROM evento AS E JOIN fecha AS F ON F.evento_id = E.id GROUP BY E.id ORDER BY E.id desc ";
    private static String SQL_SELECT_EVENTO_UNICO = "SELECT E.id ,E.programa,E.programa_en,E.titulo,E.titulo_en,E.descripcion,E.descripcion_en,E.estado,E.temporada_id FROM evento AS E JOIN fecha AS F ON F.evento_id = E.id WHERE E.id = "; //Parte que falta GROUP BY E.id
	private static String SQL_DELETE_DATOS_EVENTOS = "DELETE FROM evento";
	private static String SQL_DELETE_DATOS_FECHAS = "DELETE FROM fecha";
	private static String SQL_SELECT_FECHA_EVENTOS = "SELECT * FROM fecha WHERE evento_id = ";
	public ConexionBD(Context contexto){
		super(contexto,DATABASE_NAME,null,DATABASE_VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_TABLA_EVENTO);
		db.execSQL(SQL_CREATE_TABLA_FECHA);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
	
	//INSERTAR UN EVENTO
	public boolean insertEvento (int id, String programa,String programa_en,String titulo,String titulo_en,String descripcion, String descripcion_en,String estado,int temporada_id){
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
            Log.i("Base de datos - Evento obtenido",""+id+ programa+ programa_en+ titulo+titulo_en+ descripcion+ descripcion_en+ estado+ temporada_id);
			eventos.add(new ItemEvento(id, programa, programa_en, titulo,titulo_en, descripcion, descripcion_en, estado, temporada_id));
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
			//Terminan las consultas de fechas
			
			res.moveToNext();
			contadorEvento ++;
		}
		
		Log.i("Reegresa eventos", "Resultado\n" + eventos.toArray().toString());
		return eventos;
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
            Log.i("Base de datos - Evento obtenido",""+id+ programa+ programa_en+ titulo+titulo_en+ descripcion+ descripcion_en+ estado+ temporada_id);
            evento.add(new ItemEvento(id, programa, programa_en, titulo,titulo_en, descripcion, descripcion_en, estado, temporada_id));
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

            res.moveToNext();
            contadorEvento ++;
        }
        return evento;
    }
}
