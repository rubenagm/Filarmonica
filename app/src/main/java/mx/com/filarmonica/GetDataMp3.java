package mx.com.filarmonica;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageButton;
import android.widget.ImageView;

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
 * Created by Ruben on 22/06/2015.
 */
public class GetDataMp3 extends AsyncTask<Void,Void,ArrayList<ItemStreamingMusica>>{

    ArrayList<ItemStreamingMusica> canciones = new ArrayList<>();
    public final String URL = "http://ofj.com.mx/App/mp3/";
    Context contexto = null;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    ImageButton imageViewBoton;
    ServicioMusica servicioMusica;
    //Constructor
    public GetDataMp3(Context contexto, RecyclerView recyclerView, RecyclerView.Adapter adapter, ImageButton imageViewBoton,ServicioMusica servicioMusica){
        this.contexto       = contexto;
        this.adapter        = adapter;
        this.recyclerView   = recyclerView;
        this.imageViewBoton = imageViewBoton;
        this.servicioMusica = servicioMusica;
    }

    public GetDataMp3(){}
    @Override
    protected ArrayList<ItemStreamingMusica> doInBackground(Void... voids) {
        canciones.clear();

        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        HttpGet request = new HttpGet("http://ofj.com.mx/App/mp3/mp3.json");

        try {

            HttpResponse response = httpclient.execute(request);
            HttpEntity resEntity = response.getEntity();
            String responseString = EntityUtils.toString(resEntity); // content will be consume only once
            JSONObject jsonObjectPincipal = new JSONObject(responseString);
            JSONArray jsonArray = jsonObjectPincipal.getJSONArray("mp3");
            //Se recorren todos los elementos
            for(int x = 0;x<jsonArray.length(); x++){
                //Se obtienen los datos
                int id              = Integer.parseInt(jsonArray.getJSONObject(x).getString("Id"));
                String titulo       = jsonArray.getJSONObject(x).getString("titulo");
                String director     = jsonArray.getJSONObject(x).getString("Director");
                String duracion     = jsonArray.getJSONObject(x).getString("Duracion");
                String url          = URL + id;

                canciones.add(new ItemStreamingMusica(id,titulo,director,duracion,url));
            }
        }
        catch(Exception e){

        }

        return canciones;
    }


}
