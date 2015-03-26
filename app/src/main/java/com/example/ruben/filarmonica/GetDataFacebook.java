package com.example.ruben.filarmonica;

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
 * Created by root on 19/01/15.
 */
public class GetDataFacebook extends AsyncTask<Void,Void,ArrayList<ItemFacebook>>{


    @Override
    protected ArrayList<ItemFacebook> doInBackground(Void... params) {
        ArrayList<ItemFacebook> publicaciones = new ArrayList<ItemFacebook>();
        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        HttpGet request = new HttpGet("http://graph.facebook.com/208358432538534/photos/");
        // HttpGet request = new HttpGet("http://gdata.youtube.com/feeds/api/users/mbbangalore/uploads?v=2&alt=jsonc");
        try
        {

            HttpResponse response = httpclient.execute(request);
            HttpEntity resEntity = response.getEntity();
            String responseString= EntityUtils.toString(resEntity); // content will be consume only once

            JSONObject json = new JSONObject(responseString);

            JSONArray jsonArray = json.getJSONArray("data");
            for(int x = 0; x<jsonArray.length();x++){
                String contenido = "", urlImagen = "";

                if(!jsonArray.getJSONObject(x).isNull("name")){
                    contenido = jsonArray.getJSONObject(x).getString("name");
                    urlImagen = jsonArray.getJSONObject(x).getString("source");
                    FtpDownload ftp = new FtpDownload();
                    //ftp.descargarArchivo(3,urlImagen);
                    urlImagen= nombreImagen(urlImagen);
                    publicaciones.add(new ItemFacebook(contenido,urlImagen+"NotFacebook.png"));
                }
                Log.i("JSON Facebook", contenido + " ---- "+urlImagen);

            }

        }
        catch(Exception e1)
        {
            e1.printStackTrace();
        }

        httpclient.getConnectionManager().shutdown();

        return publicaciones;
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
}
