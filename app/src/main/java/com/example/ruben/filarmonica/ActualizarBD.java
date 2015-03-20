package com.example.ruben.filarmonica;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by macmini3cuceimobile on 21/01/15.
 */
public class ActualizarBD extends AsyncTask<Void,Void,Void> {
    //*FtpDownload ftpDownload;
    int idMayorEvento ;
    int idMayorNoticia;
    int idMayorLocalidadEvento;
    int idMayorFecha;
    final String JSON_ID = "id";
    final String JSON_PROGRAMA = "programa";
    final String JSON_PROGRAMA_EN = "programa_en";
    final String JSON_TITULO = "titulo";
    final String JSON_TITULO_EN = "titulo_en";
    final String JSON_DESCRIPCION = "descripcion";
    final String JSON_DESCRIPCION_EN = "descripcion_en";
    final String JSON_ESTADO = "estado";
    final String JSON_TEMPORADA_ID = "temporada_id";

    final String QUERY_EVENTOS = "SELECT * FROM evento WHERE id > ";
    final String QUERY_LOCALIDADES_EVENTO = "SELECT * FROM localidad_evento WHERE id > ";
    final String QUERY_FECHAS = "SELECT * FROM fecha WHERE id > ";
    final String QUERY_NOTICIA = "SELECT * FROM noticia WHERE id > ";
    ConexionBD db;
    private HttpClient mHttpClient = new DefaultHttpClient();
    private HttpPost mHttPost = new HttpPost("http://www.ofj.com.mx/App/prueba1.php");
    Context contexto;
    ///METODO CONSTRUCTOR
    public ActualizarBD(Context contexto){
        this.contexto = contexto;
    }
    //////
    @Override
    protected Void doInBackground(Void... params) {

        ArrayList<ItemEvento> eventos = new ArrayList<ItemEvento>();
        db = new ConexionBD(contexto);
        idMayorEvento = db.obtenerIdMayorEvento();
        idMayorFecha = db.obtenerIdMayorEvento();
        idMayorLocalidadEvento = db.obtenerIdMayorLocalidadEvento();
        idMayorNoticia = db.obtenerIdMayorNoticia();

        try {
            List<NameValuePair> mNameValuePairs = new ArrayList<NameValuePair>(1);
            mNameValuePairs.add(new BasicNameValuePair("query", QUERY_EVENTOS + db.obtenerIdMayorEvento()));
            mHttPost.setEntity(new UrlEncodedFormEntity(mNameValuePairs));
            //ftpDownload = new FtpDownload();
            HttpResponse response = mHttpClient.execute(mHttPost);

            HttpEntity entity = response.getEntity();
            String resultado = EntityUtils.toString(entity, "UTF-8");

            //Log.i("JSON",resultado);
            resultado = resultado.substring(9);
            Log.i("JSON", resultado);
            JSONObject jsonObject = new JSONObject(resultado);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            int contadorEventos = 0;
            for (int i = 0; i < jsonArray.length(); i++) {
                contadorEventos++;
                JSONObject jsonElement = jsonArray.getJSONObject(i);

                int id = jsonElement.getInt(JSON_ID);

                //ftpDownload.descargarArchivo(1, id + "");
                String programa = jsonElement.getString(JSON_PROGRAMA);
                String programa_en = jsonElement.getString(JSON_PROGRAMA_EN);
                String titulo = jsonElement.getString(JSON_TITULO);
                String titulo_en = jsonElement.getString(JSON_TITULO_EN);
                String descripcion = jsonElement.getString(JSON_DESCRIPCION);
                String descripcion_en = jsonElement.getString(JSON_DESCRIPCION_EN);
                String estado = jsonElement.getString(JSON_ESTADO);
                int temporada_id = jsonElement.getInt(JSON_TEMPORADA_ID);

                //Agregamos los valores al arreglo
                db.insertEvento(id, programa, programa_en, titulo, titulo_en, descripcion, descripcion_en, estado, temporada_id);
                eventos.add(new ItemEvento(id, programa, programa_en, titulo, titulo_en, descripcion, descripcion_en, estado, temporada_id));
            }
            Log.i("Eventos insertados", "" + contadorEventos);
        } catch (JSONException e) {
            Log.e("JSON", "Error al leer el JSON\n" + e);
        } catch (IOException e) {
            Log.e("HTTP", "Error con la conexi�n HTTP");
        }



        try {
            List<NameValuePair> mNameValuePairs = new ArrayList<NameValuePair>(1);
            mNameValuePairs.add(new BasicNameValuePair("query", QUERY_FECHAS + db.obtenerIdMayorFecha()));
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
                db.insertFecha(id, fecha, hora, minuto, evento_id);
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

        //Se comienza a guardar Localidades_eventos
        try {
            List<NameValuePair> mNameValuePairs = new ArrayList<NameValuePair>(1);
            mNameValuePairs.add(new BasicNameValuePair("query", QUERY_LOCALIDADES_EVENTO + db.obtenerIdMayorLocalidadEvento()));
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

                db.insertLocalidadEvento(id,nombre,costo,evento_id);

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
            mNameValuePairs.add(new BasicNameValuePair("query", QUERY_NOTICIA + db.obtenerIdMayorNoticia()));
            mHttPost.setEntity(new UrlEncodedFormEntity(mNameValuePairs));

            HttpResponse response = mHttpClient.execute(mHttPost);

            HttpEntity entity = response.getEntity();
            String resultado = EntityUtils.toString(entity,"UTF-8");
            //ftpDownload = new FtpDownload();
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

                db.insertNoticia(id,titulo,titulo_en,contenido,contenido_en,fecha,publicada,fecha_creacion);

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
        /*
        int mayorEvento = db.obtenerIdMayorEvento();
        for(int x = db.obtenerIdMayorEvento();x>=0;x--){
            displayNotification(createBasicNotification("OFJ","Descargando contenido multimedia",mayorEvento-x,mayorEvento));
            File file = new File("/storage/emulated/0/Imagenes/imagenes"+x+".png");
            if(!file.exists()) {
                try {
                    ftpDownload.descargarArchivo(1, x + "");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        int mayorNoticia = db.obtenerIdMayorNoticia();
        for(int x = db.obtenerIdMayorNoticia();x>=0;x--){
            displayNotification(createBasicNotification("OFJ","Descargando contenido multimedia",mayorNoticia-x,mayorNoticia));
            File file = new File("/storage/emulated/0/Imagenes/imagenes"+x+"Not.png");
            if(!file.exists()) {
                try {
                    ftpDownload.descargarArchivo(2, x + "");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        */


        return null;
    }
    private Notification createBasicNotification(String titulo, String contenido,int progress, int total) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(contexto);
        Notification notification = builder
                .setSmallIcon(R.drawable.ic_av_loop)
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
