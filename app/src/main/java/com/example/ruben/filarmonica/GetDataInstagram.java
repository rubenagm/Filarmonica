package com.example.ruben.filarmonica;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by macmini3cuceimobile on 2/27/15.
 */
public class GetDataInstagram extends AsyncTask<Void,Void,ArrayList<ItemImagenInstagram>> {
    Context contexto;
    public GetDataInstagram(Context contexto){
        this.contexto = contexto;
    }
    FtpDownload ftp;
    @Override
    protected ArrayList<ItemImagenInstagram> doInBackground(Void... params) {
        ArrayList<ItemImagenInstagram> imagenes = new ArrayList<ItemImagenInstagram>();
        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        HttpGet request = new HttpGet("https://api.instagram.com/v1/users/239397258/media/recent/?client_id=93b3b8b1257944829c9e4670cbcc819d");
        ConexionBD db = new ConexionBD(contexto);
        //Inicializar el ftp
        ftp = new FtpDownload();

        try
        {

            HttpResponse response = httpclient.execute(request);
            HttpEntity resEntity = response.getEntity();
            String responseString= EntityUtils.toString(resEntity); // content will be consume only once

            JSONObject json = new JSONObject(responseString);

            JSONArray jsonArray = json.getJSONArray("data");
            int contador = 0;
            String[] urlImagenNd = new String[3];
            String[] urlImagenHd = new String[3];
            String[] texto = new String[3];
            String[] link = new String[3];
            for(int x = 0; x<jsonArray.length();x++){

                urlImagenNd[contador] = jsonArray.getJSONObject(x).getJSONObject("images").getJSONObject("low_resolution").getString("url");
                urlImagenHd[contador] = jsonArray.getJSONObject(x).getJSONObject("images").getJSONObject("standard_resolution").getString("url");
                texto[contador] = jsonArray.getJSONObject(x).getJSONObject("caption").getString("text");
                link[contador] = jsonArray.getJSONObject(x).getString("link");

                //ftp.descargarArchivo(4,urlImagenHd[contador]);
                //ftp.descargarArchivo(4,urlImagenNd[contador]);
                db.insertarImagenInstagram(urlImagenNd[contador],urlImagenHd[contador],texto[contador],link[contador]);
                if(contador ==2){
                    // imagenes.add(new ItemImagenInstagram(urlImagenHd,urlImagenNd,texto));
                    contador =-1;
                    Log.i("JSON Instagram", "Fila insertada");
                }
                contador++;

            }

        }
        catch(Exception e1)
        {
            e1.printStackTrace();
        }

        httpclient.getConnectionManager().shutdown();

        return imagenes;
    }


    public String nombreImagen (String urlImagen){
        String id="";
        StringTokenizer token = new StringTokenizer(urlImagen,"/");
        while(token.hasMoreTokens()){
            id = token.nextToken();
        }
        StringTokenizer token2 = new StringTokenizer(id,".");

        return token2.nextToken();
    }
    public String getIdImagen(String url){
        String idImagen="";
        StringTokenizer token = new StringTokenizer(url,"/");
        while(token.hasMoreTokens()){
            idImagen = token.nextToken();
        }
        StringTokenizer token2 = new StringTokenizer(idImagen,".");
        idImagen = token2.nextToken();
        return idImagen;
    }
}
