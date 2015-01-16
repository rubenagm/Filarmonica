package com.example.ruben.filarmonica;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class Streaming extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener
{
    Context contexto;
    TextView textViewTituloVideo;
    RecyclerView mRecyclerView;
    RecyclerView.Adapter adapter;
    ArrayList<ItemYoutube> videos;

    //Variables del Drawer.
    private ListView list_view_drawer;

    //Arreglos.
    private ArrayList<ItemDrawer> array_item_drawer;
    private TypedArray array_iconos;
    YouTubePlayer youtube;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streaming);
        contexto = getApplicationContext();

        /******************************* ListView Drawer *****************************/
        list_view_drawer = (ListView) findViewById(R.id.drawer_listView);
        //Obtenemos las imágenes.
        array_iconos = getResources().obtainTypedArray(R.array.iconos_drawer);

        //Creamos el arreglo de ItemDrawer.
        array_item_drawer = new ArrayList<ItemDrawer>();

        //Ajustar el ListView al ancho de la pantalla
        DisplayMetrics display_metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(display_metrics);
        int width = display_metrics.widthPixels;
        list_view_drawer.getLayoutParams().width = width;

        //Agregamos las imágenes al arreglo de Item.
        for(int i = 0; i < 3; i++)
        {
            if((i % 2) == 0 && i > 0)
            {
                array_item_drawer.add(new ItemDrawer(array_iconos.getResourceId(i, -1), array_iconos.getResourceId(i+1, -1)));
            }
            else
            {
                array_item_drawer.add(new ItemDrawer(array_iconos.getResourceId(i, -1)));
            }
        }

        //Colocamos el adaptador al ListView.
        list_view_drawer.setAdapter(new ListAdapterDrawer(this, array_item_drawer));

        //Colocamos el click listener al ListView.
        list_view_drawer.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                switch(position)
                {
                    case 0:
                    {
                        Toast.makeText(contexto, "Has presionado conciertos cambio", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(contexto,ListaEventos.class);
                        startActivity(i);
                        break;
                    }
                    case 1:
                    {
                        Toast.makeText(contexto, "Has presionado Noticias", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }

        });
        /******************************* ListView Drawer *****************************/

        GetDataYouTube hilo = new GetDataYouTube(adapter,mRecyclerView);
        hilo.execute();
        try {
            videos = hilo.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        //Obtenemos las referecias.
        textViewTituloVideo = (TextView) findViewById(R.id.titulo_video);

        //Cambiamos la fuente del TextView.
        Typeface fuente = Typeface.createFromAsset(getAssets(), "fonts/Roboto/Roboto-Light.ttf");
        textViewTituloVideo.setTypeface(fuente);

        YouTubePlayerView player = (YouTubePlayerView) findViewById(R.id.youtube_player);
        player.initialize("AIzaSyC_gDmJgRqgTXP2F8sJJI1nOkNhyIh8DFI",this);

        textViewTituloVideo.setText(videos.get(0).getTitulo());
        //Recycler View
        mRecyclerView = (RecyclerView) findViewById(R.id.lista_videos);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b)
    {
        youtube = youTubePlayer;
        adapter = new AdapterListaVideos(contexto,videos,youtube, textViewTituloVideo);
        mRecyclerView.setAdapter(adapter);
        if(!b){
            youTubePlayer.cueVideo(videos.get(0).getUrlYouTube());
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Toast.makeText(this,
                "Error " + youTubeInitializationResult.toString(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
    }
}
