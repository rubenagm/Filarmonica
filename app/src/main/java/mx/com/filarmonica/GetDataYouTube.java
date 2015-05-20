package mx.com.filarmonica;

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
import java.util.StringTokenizer;

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
        HttpGet request = new HttpGet("https://www.googleapis.com/youtube/v3/search?part=snippet&order=rating&type=video&videoEmbeddable=true&maxResults=20&channelId=UCN2VyhehLANsWvqByqSd8FA&key=AIzaSyDg28A9LySW4VqSUfmoWKB1xjhToLpbx1s");
        // HttpGet request = new HttpGet("http://gdata.youtube.com/feeds/api/users/mbbangalore/uploads?v=2&alt=jsonc");
        try
        {
            HttpResponse response = httpclient.execute(request);
            HttpEntity resEntity = response.getEntity();
            String responseString= EntityUtils.toString(resEntity); // content will be consume only once

            JSONObject json = new JSONObject(responseString);

            JSONArray jsonArray = json.getJSONArray("items");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getJSONObject("id").getString("videoId");
                String title = jsonObject.getJSONObject("snippet").getString("title");
                String contenido = jsonObject.getJSONObject("snippet").getString("description");
                String urlImagen = jsonObject.getJSONObject("snippet").getJSONObject("thumbnails").getJSONObject("default").getString("url");
                String duracion = "";
                id = obtenerIdYoutube(id);
                videos.add(new ItemYoutube(id,title,contenido,duracion,"","",urlImagen));
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

    public String obtenerIdYoutube(String url){
        StringTokenizer token = new StringTokenizer(url,"/");
        String id = "";
        while(token.hasMoreTokens()){
            id = token.nextToken();
        }
        Log.i("Youtube",id);
        return id;
    }

}
