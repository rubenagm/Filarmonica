package com.example.ruben.filarmonica;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class ObtenerEventos extends AsyncTask<String, integer, ArrayList<ItemEvento>>{
    FtpDownload ftpDownload;
	final String JSON_ID = "id";
	final String JSON_PROGRAMA = "programa";
	final String JSON_PROGRAMA_EN = "programa_en";
	final String JSON_TITULO = "titulo";
	final String JSON_TITULO_EN = "titulo_en";
	final String JSON_DESCRIPCION = "descripcion";
	final String JSON_DESCRIPCION_EN = "descripcion_en";
	final String JSON_ESTADO = "estado";
	final String JSON_TEMPORADA_ID = "temporada_id";

    final String QUERY_NOTICIA = "SELECT * FROM noticia ORDER BY fecha DESC ";
	final String QUERY_EVENTOS = "SELECT * FROM evento  ORDER BY id DESC";
	final String QUERY_FECHAS = "SELECT * FROM fecha";
    final String QUERY_LOCALIDADES = "SELECT * FROM localidad";
    final String QUERY_LOCALIDADES_EVENTO = "SELECT * FROM localidad_evento";

	private HttpClient mHttpClient = new DefaultHttpClient();
	private HttpPost mHttPost = new HttpPost("http://www.ofj.com.mx/App/prueba1.php");
	Context contexto;
    SharedPreferences sharedPreferences;
	public ObtenerEventos(Context contexto,SharedPreferences sharedPreferences){
		this.contexto = contexto;
        this.sharedPreferences = sharedPreferences;
	}
	@Override
	protected ArrayList<ItemEvento> doInBackground(String... params) {
		ArrayList<ItemEvento> eventos = new ArrayList<ItemEvento>();
        SharedPreferences.Editor editor = sharedPreferences.edit();
		ConexionBD mDB = new ConexionBD(contexto);
		try {
			List<NameValuePair> mNameValuePairs = new ArrayList<NameValuePair>(1);
			mNameValuePairs.add(new BasicNameValuePair("query", QUERY_EVENTOS));			
			mHttPost.setEntity(new UrlEncodedFormEntity(mNameValuePairs));
			HttpResponse response = mHttpClient.execute(mHttPost);
			
			HttpEntity entity = response.getEntity();
			String resultado = EntityUtils.toString(entity,"UTF-8");
			
			//Log.i("JSON",resultado);
			resultado = resultado.substring(9);
			Log.i("JSON",resultado);
			JSONObject jsonObject = new JSONObject(resultado);
			JSONArray jsonArray = jsonObject.getJSONArray("data");
			mDB.limpiarDB();
            int contadorEventos = 0;
            int longitud = jsonArray.length();
			for(int i = 0; i < jsonArray.length(); i++)
			{
                contadorEventos++;
				JSONObject jsonElement = jsonArray.getJSONObject(i);
				displayNotification(createBasicNotification("OFJ","Actualizando eventos",i+1,longitud));
				int id = jsonElement.getInt(JSON_ID);


				String programa = jsonElement.getString(JSON_PROGRAMA);
				String programa_en = jsonElement.getString(JSON_PROGRAMA_EN);
				String titulo = jsonElement.getString(JSON_TITULO);
				String titulo_en = jsonElement.getString(JSON_TITULO_EN);
				String descripcion = jsonElement.getString(JSON_DESCRIPCION);
				String descripcion_en = jsonElement.getString(JSON_DESCRIPCION_EN);
				String estado = jsonElement.getString(JSON_ESTADO);
				int temporada_id = jsonElement.getInt(JSON_TEMPORADA_ID);
				
				//Agregamos los valores al arreglo
				mDB.insertEvento(id, programa, programa_en, titulo, titulo_en, descripcion, descripcion_en, estado, temporada_id);
				eventos.add(new ItemEvento(id, programa, programa_en, titulo, titulo_en, descripcion, descripcion_en, estado, temporada_id));
			}
            Log.i("Eventos insertados",""+contadorEventos);
		}
		catch(JSONException e)
		{
			Log.e("JSON", "Error al leer el JSON\n" + e);
		}
		catch(IOException e)
		{
			Log.e("HTTP", "Error con la conexi�n HTTP");
		}
		
		Log.i("Eventos", "Comienza a guardar fechas");
		
		/// Se comienzan a guardar las fechas

		try {
			List<NameValuePair> mNameValuePairs = new ArrayList<NameValuePair>(1);
			mNameValuePairs.add(new BasicNameValuePair("query", QUERY_FECHAS));
			mHttPost.setEntity(new UrlEncodedFormEntity(mNameValuePairs));

			HttpResponse response = mHttpClient.execute(mHttPost);

			HttpEntity entity = response.getEntity();
			String resultado = EntityUtils.toString(entity,"UTF-8");

			//Log.i("JSON",resultado);
			resultado = resultado.substring(9);
			Log.i("JSON",resultado);
			JSONObject jsonObject = new JSONObject(resultado);
			JSONArray jsonArray = jsonObject.getJSONArray("data");
            int longitud = jsonArray.length();
			for(int i = 0; i < jsonArray.length(); i++)
			{
                displayNotification(createBasicNotification("OFJ","Actualizando fechas",i+1,longitud));
				JSONObject jsonElement = jsonArray.getJSONObject(i);

				int id = jsonElement.getInt("id");
				String fecha = jsonElement.getString("fecha");
				String hora = jsonElement.getString("hora");
				String minuto = jsonElement.getString("minuto");
				int evento_id = jsonElement.getInt("evento_id");

				//Agregamos los valores al arreglo
				mDB.insertFecha(id, fecha, hora, minuto, evento_id);
			}
		}
		catch(JSONException e)
		{
			Log.e("JSON", "Error al leer el JSON\n" + e);
		}
		catch(IOException e)
		{
			Log.e("HTTP", "Error con la conexi�n HTTP");
		}

        //Se comienzan a guardar las localidades




        //Se comienza a guardar Localidades_eventos
        try {
            List<NameValuePair> mNameValuePairs = new ArrayList<NameValuePair>(1);
            mNameValuePairs.add(new BasicNameValuePair("query", QUERY_LOCALIDADES_EVENTO));
            mHttPost.setEntity(new UrlEncodedFormEntity(mNameValuePairs));

            HttpResponse response = mHttpClient.execute(mHttPost);

            HttpEntity entity = response.getEntity();
            String resultado = EntityUtils.toString(entity,"UTF-8");

            //Log.i("JSON",resultado);
            resultado = resultado.substring(9);
            Log.i("JSON",resultado);
            JSONObject jsonObject = new JSONObject(resultado);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            int longitud = jsonArray.length();
            for(int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject jsonElement = jsonArray.getJSONObject(i);
                displayNotification(createBasicNotification("OFJ","Actualizando localidades",i+1,longitud));
                int id = jsonElement.getInt("id");
                String nombre = jsonElement.getString("nombre");
                String costo = jsonElement.getString("costo");
                int evento_id = jsonElement.getInt("evento_id");

                mDB.insertLocalidadEvento(id,nombre,costo,evento_id);

            }
        }
        catch(JSONException e)
        {
            Log.e("JSON", "Error al leer el JSON\n" + e);
        }
        catch(IOException e)
        {
            Log.e("HTTP", "Error con la conexi�n HTTP");
        }

        //Se comienza a guardar las Noticias
        try {

            List<NameValuePair> mNameValuePairs = new ArrayList<NameValuePair>(1);
            mNameValuePairs.add(new BasicNameValuePair("query", QUERY_NOTICIA));
            mHttPost.setEntity(new UrlEncodedFormEntity(mNameValuePairs));

            HttpResponse response = mHttpClient.execute(mHttPost);

            HttpEntity entity = response.getEntity();
            String resultado = EntityUtils.toString(entity,"UTF-8");
            ftpDownload = new FtpDownload();
            //Log.i("JSON",resultado);
            resultado = resultado.substring(9);
            Log.i("JSON",resultado);
            JSONObject jsonObject = new JSONObject(resultado);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            int longitud = jsonArray.length();
            for(int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject jsonElement = jsonArray.getJSONObject(i);
                displayNotification(createBasicNotification("OFJ","Actualizando noticias",i+1,longitud));
                int id = jsonElement.getInt("id");
                //ftpDownload.descargarArchivo(2,id+"");
                String titulo = jsonElement.getString("titulo");
                String titulo_en = jsonElement.getString("titulo_en");
                String contenido = jsonElement.getString("contenido");
                String contenido_en = jsonElement.getString("contenido_en");
                String fecha = jsonElement.getString("fecha");
                String publicada = jsonElement.getString("publicada");
                String fecha_creacion = jsonElement.getString("fecha_creacion");

                mDB.insertNoticia(id,titulo,titulo_en,contenido,contenido_en,fecha,publicada,fecha_creacion);

            }
        }
        catch(JSONException e)
        {
            Log.e("JSON", "Error al leer el JSON\n" + e);
        }
        catch(IOException e)
        {
            Log.e("HTTP", "Error con la conexi�n HTTP");
        }


        editor.putString("DatosInsertados","Insertados");
        editor.commit();
		return eventos;
	}
    private Notification createBasicNotification(String titulo, String contenido,int progress, int total) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(contexto);
        Notification notification = builder
                .setSmallIcon(R.drawable.icon_buy)
                .setContentTitle(titulo)
                .setContentText(contenido)
                .setProgress(total,progress,false)
                .build();

        return notification;
    }
    private void displayNotification(Notification notification) {
        NotificationManager notificationManager = (NotificationManager)contexto.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1 , notification);
    }
}
