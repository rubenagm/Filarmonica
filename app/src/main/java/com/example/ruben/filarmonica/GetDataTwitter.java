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

/**
 * Created by macmini3cuceimobile on 3/24/15.
 */
public class GetDataTwitter extends AsyncTask<Void,Void,ArrayList<ItemTwitter>> {


    @Override
    protected ArrayList<ItemTwitter> doInBackground(Void... params) {
        ArrayList<ItemTwitter> tweets = new ArrayList<ItemTwitter>();
        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        HttpGet request = new HttpGet("http://ofj.com.mx/App/twitter.php");
        // HttpGet request = new HttpGet("http://gdata.youtube.com/feeds/api/users/mbbangalore/uploads?v=2&alt=jsonc");
        try {

            HttpResponse response = httpclient.execute(request);
            HttpEntity resEntity = response.getEntity();
            String responseString = EntityUtils.toString(resEntity); // content will be consume only once

            JSONArray json = new JSONArray(responseString);
            for (int x = 0; x < json.length(); x++) {
                String urlImagen = "";
                String id = json.getJSONObject(x).getString("id");
                String contenido = json.getJSONObject(x).getString("text");
                String fecha = json.getJSONObject(x).getString("created_at");
                tweets.add(new ItemTwitter(id,contenido,fecha));
                //Si existen imagenes
                if(!json.getJSONObject(x).getJSONObject("entities").isNull("media")){
                    urlImagen = json.getJSONObject(x).getJSONObject("entities").getJSONArray("media").getJSONObject(0).getString("media_url");
                    tweets.get(x).setUrlImagen(urlImagen);
                }
                //Si existen links
                if(!json.getJSONObject(x).getJSONObject("entities").isNull("urls")){
                    int urlsCount = json.getJSONObject(x).getJSONObject("entities").getJSONArray("urls").length();
                    if(urlsCount>0){
                        //Se obtienen todas las urls
                        for(int y=0;y<urlsCount;y++){
                            String url = json.getJSONObject(x).getJSONObject("entities").getJSONArray("urls").getJSONObject(y).getString("url");
                            tweets.get(x).addLinks(url); //se agrega al objeto
                        }
                    }
                }
                //Si existen hashtags
                if(!json.getJSONObject(x).getJSONObject("entities").isNull("hashtags")){
                    int hashTagsCount = json.getJSONObject(x).getJSONObject("entities").getJSONArray("hashtags").length();
                    if(hashTagsCount>0){
                        //Se obtienen todos los hashtags
                        for(int y=0;y<hashTagsCount;y++){
                            String hashtag = json.getJSONObject(x).getJSONObject("entities").getJSONArray("hashtags").getJSONObject(y).getString("text");
                            tweets.get(x).addHashTags(hashtag); //se agrega al objeto
                        }
                    }
                }
                //Si existen menciones
                if(!json.getJSONObject(x).getJSONObject("entities").isNull("user_mentions")){
                    int usersCount = json.getJSONObject(x).getJSONObject("entities").getJSONArray("user_mentions").length();
                    if(usersCount>0){
                        //Se obtienen todos los hashtags
                        for(int y=0;y<usersCount;y++){
                            String user = json.getJSONObject(x).getJSONObject("entities").getJSONArray("user_mentions").getJSONObject(y).getString("screen_name");
                            tweets.get(x).addUsers(user); //se agrega al objeto
                        }
                    }
                }
            }

        } catch (Exception e1) {
            e1.printStackTrace();
        }

        httpclient.getConnectionManager().shutdown();

        return tweets;
    }
}
