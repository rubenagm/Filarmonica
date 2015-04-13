package mx.com.filarmonica;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by macmini3cuceimobile on 3/24/15.
 */
public class GetDataTwitter extends AsyncTask<Void,Void,ArrayList<ItemTwitter>>
{

    private RecyclerView recyclerView;
    private Context contexto;
    private SwipeRefreshLayout swipeRefreshLayout;

    //Constructor que recibirá el contexto y el RecyclerView en donde se insertarán los tweets.
    public GetDataTwitter(RecyclerView recyclerView, SwipeRefreshLayout swipeRefreshLayout,
                          Context contexto)
    {
        this.recyclerView       = recyclerView;
        this.swipeRefreshLayout = swipeRefreshLayout;
        this.contexto           = contexto;
    }

    public GetDataTwitter(){}

    @Override
    protected ArrayList<ItemTwitter> doInBackground(Void... params) {
        ArrayList<ItemTwitter> tweets = new ArrayList<ItemTwitter>();
        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        HttpGet request = new HttpGet("http://ofj.com.mx/App/twitter.php");

        try {

            HttpResponse response = httpclient.execute(request);
            HttpEntity resEntity = response.getEntity();
            String responseString = EntityUtils.toString(resEntity); // content will be consume only once

            JSONArray json = new JSONArray(responseString);
            for (int x = 0; x < json.length(); x++) {
                String urlImagen = "";
                String id = json.getJSONObject(x).getString("id");
                String contenido = json.getJSONObject(x).getString("text");
                String fecha = parsearFecha(json.getJSONObject(x).getString("created_at"));
                String urlTwitter = "https://twitter.com/OFilarmonicaJal/status/" + id;
                tweets.add(new ItemTwitter(id,contenido,fecha, urlTwitter));
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
                            tweets.get(x).addHashTags("#"+hashtag); //se agrega al objeto
                        }
                    }
                }
                //Si existen menciones
                if(!json.getJSONObject(x).getJSONObject("entities").isNull("user_mentions")){
                    int usersCount = json.getJSONObject(x).getJSONObject("entities").getJSONArray("user_mentions").length();
                    if(usersCount>0){
                        //Se obtienen todos los usuarios
                        for(int y=0;y<usersCount;y++){
                            String user = json.getJSONObject(x).getJSONObject("entities").getJSONArray("user_mentions").getJSONObject(y).getString("screen_name");
                            tweets.get(x).addUsers("@"+user); //se agrega al objeto
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

    @Override
    protected void onPostExecute(ArrayList<ItemTwitter> tweets)
    {
        if(recyclerView != null)
        {
            RecyclerView.Adapter adapter = new AdapterListaTwitter(tweets,contexto);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(contexto));
            recyclerView.setItemAnimator( new DefaultItemAnimator());
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    public String parsearFecha(String fecha)
    {
        final int INICIO_FECHA = 4;
        final int FIN_FECHA    = 10;

        String fechaParseada;
        String dia;
        String mes;

        fechaParseada = fecha.substring(INICIO_FECHA, FIN_FECHA);
        mes = fechaParseada.substring(0, 3);
        dia = fechaParseada.substring(4, 6);

        mes = parsearMes(mes);

        return dia + " " + mes;
    }

    public String parsearMes(String mes)
    {
        if(mes.equals("Jan"))
        {
            return "Enero";
        }
        else if(mes.equals("Feb"))
        {
            return "Febrero";
        }
        else if(mes.equals("Mar"))
        {
            return "Marzo";
        }
        else if(mes.equals("Apr"))
        {
            return "Abril";
        }
        else if(mes.equals("May"))
        {
            return "Mayo";
        }
        else if(mes.equals("Jun"))
        {
            return "Junio";
        }
        else if(mes.equals("Jul"))
        {
            return "Julio";
        }
        else if(mes.equals("Aug"))
        {
            return "Agosto";
        }
        else if(mes.equals("Sep"))
        {
            return "Septiembre";
        }
        else if(mes.equals("Oct"))
        {
            return "Octubre";
        }
        else if(mes.equals("Nov"))
        {
            return "Noviembre";
        }
        else if(mes.equals("Dec"))
        {
            return "Diciembre";
        }

        return "Fecha";
    }
}
