package com.example.ruben.filarmonica;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
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

//////////// CLASE PARA OBTENER DATOS YOUTUBE
public class GetDataYouTube extends AsyncTask<Void,Void,ArrayList<ItemYoutube>>
{
    Context contexto;

    ArrayList<ItemYoutube> videos;
    RecyclerView.Adapter adapter;
    RecyclerView mRecyclerView;
    AdapterListaVideos adapterVideos;
    public GetDataYouTube(RecyclerView.Adapter adapter,RecyclerView mRecyclerView){
        this.adapter = adapter;
        this. mRecyclerView = mRecyclerView;
    }
    @Override
    protected void onPostExecute(ArrayList<ItemYoutube> result) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
        // pd.show();
    }

    @Override
    protected ArrayList<ItemYoutube> doInBackground(Void... params) {
        // TODO Auto-generated method stub

        videos = getData();

        return videos;
    }
    public ArrayList<ItemYoutube> getData()
    {
        ArrayList<ItemYoutube> videos = new ArrayList<ItemYoutube>();
        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        HttpGet request = new HttpGet("http://gdata.youtube.com/feeds/api/videos?author=MonicaArtists&alt=json");
        // HttpGet request = new HttpGet("http://gdata.youtube.com/feeds/api/users/mbbangalore/uploads?v=2&alt=jsonc");
        try
        {
            HttpResponse response = httpclient.execute(request);
            HttpEntity resEntity = response.getEntity();
            String responseString= EntityUtils.toString(resEntity); // content will be consume only once

            JSONObject json = new JSONObject(responseString);

            JSONArray jsonArray = json.getJSONObject("feed").getJSONArray("entry");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String title = jsonObject.getJSONObject("title").getString("$t");
                String contenido = jsonObject.getJSONObject("content").getString("$t");
                String urlImagen = jsonObject.getJSONObject("media$group").getJSONArray("media$thumbnail").getJSONObject(0).getString("url");
                videos.add(new ItemYoutube(title,contenido,"","","",urlImagen));
                Log.i("Json", title + "\n" + urlImagen);
            }
        }
        catch(Exception e1)
        {
            e1.printStackTrace();
        }

        httpclient.getConnectionManager().shutdown();

        return videos;
    }

}
