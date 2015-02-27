package com.example.ruben.filarmonica;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import tabs.SlidingTabLayout;

/**
 * Created by natafrank on 1/20/15.
 */
public class Streaming extends ActionBarActivity
{

    //Contexto
    private static Context contexto;

    //Número de tabs que contiene la actividad.
    private static final int NUMERO_TABS = 2;

    //Pager que controla los eventos del ViewPager.
    private ViewPager mPager;

    //Manejador de los tabs.
    private static SlidingTabLayout mTabs;

    //ListView del drawer.
    private ListView list_view_drawer;

    //Arreglos.
    private ArrayList<ItemDrawer> array_item_drawer;
    private TypedArray array_iconos;

    /******* Variables del reproductor de video. ********/
    //Reproductor.
    static VideoView videoPlayer;

    //Lista de videos del canal de youtube.
    static ArrayList<ItemYoutube> listaVideos;

    //TextView con el título del video.
    static TextView textViewTituloVideo;

    //Vista que mostrará las miniaturas de video.
    static RecyclerView mRecyclerView;

    //Adaptador del RecyclerView.
    static RecyclerView.Adapter adapter;

    //Fragmento del reproductor de youtube.
    static YouTubePlayerSupportFragment youTubePlayerSupportFragment;

    //Manager de fragmentos.
    static FragmentManager supportManager;

    //Imágenes de las tabs.
    private static int[] imagenesTab =
    {
        R.drawable.video_icon,
        R.drawable.musica_icon_off,
        R.drawable.video_icon_off,
        R.drawable.musica_icon
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);

        //Obtenemos el contexto.
        contexto = getApplicationContext();

        //Ocultamos el ActionBar.
        getSupportActionBar().hide();

        supportManager = getSupportFragmentManager();

        //Obtenemos las referencias.
        mPager = (ViewPager) findViewById(R.id.pager);
        mTabs  = new SlidingTabLayout(contexto);
        mTabs  = (SlidingTabLayout) findViewById(R.id.tabs);
        //Colocamos la descripción.
        mTabs.setDescription("Streaming");
        //Colocamos el adapdator al ViewPager.
        mPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));

        //Distribuimos las tabs en ancho de manera equivalente.
        mTabs.setDistributeEvenly(true);

        //Colocamos el ViewPager a las Tabs.
        mTabs.setViewPager(mPager);

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
                        Intent i = new Intent(contexto, ListaEventos.class);
                        startActivity(i);
                        break;
                    }
                    case 1:
                    {
                        Intent i = new Intent(contexto, Noticias.class);
                        startActivity(i);
                        break;
                    }
                }
            }

        });
        /******************************* ListView Drawer *****************************/

        //Cargamos los videos del canal.
        GetDataYouTube hilo = new GetDataYouTube(adapter,mRecyclerView);
        hilo.execute();
        try
        {
            listaVideos = hilo.get();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        catch (ExecutionException e)
        {
            e.printStackTrace();
        }
    }//onCreate

    //Adaptador del ViewPager.
    public static class MyPagerAdapter extends FragmentPagerAdapter
    {

        public MyPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        //Método que retorna el fragmento donde se encuentra el layout inflado.
        @Override
        public Fragment getItem(int position)
        {
            /*Regresamos el Fragment en base a la posición.
             0.- Youtube.
             1.- Música. */
            switch(position)
            {
                case 0:
                {
                    MyYoutubePlayerFragment myYoutubePlayerFragment =
                            MyYoutubePlayerFragment.newInstance(position);
                    return myYoutubePlayerFragment;
                }
                case 1:
                {
                    MyMusicPlayerFragment myMusicPlayerFragment =
                            MyMusicPlayerFragment.newInstance(position);
                    return myMusicPlayerFragment;
                }
            }

            //En caso de no entrar en ningún caso, retornamos null.
            return null;
        }

        //Método que retorna el número de tabs que tendrá el ViewPager.
        @Override
        public int getCount()
        {
            return NUMERO_TABS;
        }

        //Método para poner nombre (en este caso imágenes) a las tabs.
        @Override
        public CharSequence getPageTitle(int position)
        {
            //Cargamos la imagen en base a la posición de la tab.
            Drawable imagenTab = contexto.getResources().getDrawable(imagenesTab[position]);
            imagenTab.setBounds(0, 0, 60, 60);
            SpannableString spannableString = new SpannableString(" ");
            ImageSpan imageSpan = new ImageSpan(imagenTab, ImageSpan.ALIGN_BOTTOM);
            spannableString.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannableString;
        }
    }

    //Clase que crea el fragmento del reproductor de música y de video.
    public static class MyMusicPlayerFragment extends Fragment
    {
        //Método que regresa un fragmento.
        static MyMusicPlayerFragment newInstance(int position)
        {
            MyMusicPlayerFragment myMusicPlayerFragment = new MyMusicPlayerFragment();
            Bundle args = new Bundle();
            args.putInt("position", position);
            myMusicPlayerFragment.setArguments(args);
            return myMusicPlayerFragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState)
        {
            //Vista que contendrá el layout que vamos a retornar.
            View layout = null;

            layout = inflater.inflate(R.layout.fragment_reproductor_musica, container, false);

            //Retornamos la vista.
            return layout;
        }
    }

    public static class MyYoutubePlayerFragment extends Fragment
                        implements YouTubePlayer.OnInitializedListener
    {
        //Método que regresa un fragmento.
        static MyYoutubePlayerFragment newInstance(int position)
        {
            MyYoutubePlayerFragment myYoutubePlayerFragment = new MyYoutubePlayerFragment();
            Bundle args = new Bundle();
            args.putInt("position", position);
            myYoutubePlayerFragment.setArguments(args);
            return myYoutubePlayerFragment;
        }

        @Override
        public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle)
        {
            //Vista que contendrá el layout a retornar.
            View layout = null;

            //Inflamos el layout.
            layout = layoutInflater.inflate(R.layout.fragment_youtube, viewGroup, false);

            //Obtenemos las referencias.
            textViewTituloVideo = (TextView) layout.findViewById(R.id.titulo_video);
            mRecyclerView = (RecyclerView) layout.findViewById(R.id.lista_videos);

            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    supportManager.beginTransaction();

            youTubePlayerSupportFragment = new YouTubePlayerSupportFragment();
            fragmentTransaction.add(R.id.youtubeFragment, youTubePlayerSupportFragment);
            fragmentTransaction.commit();

            //Colocamos el nombre del video.
            textViewTituloVideo.setText(listaVideos.get(0).getTitulo());

            //Configuramos el RecyclerView.
            mRecyclerView.setLayoutManager(new LinearLayoutManager(contexto));
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());

            //Inicializamos el fragmento de youtube.
            youTubePlayerSupportFragment.initialize("AIzaSyC_gDmJgRqgTXP2F8sJJI1nOkNhyIh8DFI", this);

            //Retornamos el layout.
            return layout;
        }

        @Override
        public void onInitializationSuccess(YouTubePlayer.Provider provider,
                        YouTubePlayer youTubePlayer, boolean b)
        {
            adapter = new AdapterListaVideos(contexto, listaVideos, youTubePlayer, textViewTituloVideo);
            mRecyclerView.setAdapter(adapter);
            if(!b)
            {
                youTubePlayer.cueVideo(listaVideos.get(0).getUrlYouTube());
            }
        }

        @Override
        public void onInitializationFailure(YouTubePlayer.Provider provider,
                        YouTubeInitializationResult youTubeInitializationResult)
        {
            Toast.makeText(contexto,
                    "Error " + youTubeInitializationResult.toString(),
                    Toast.LENGTH_LONG).show();
        }

        @Override
        public void onConfigurationChanged(Configuration newConfig)
        {
            super.onConfigurationChanged(newConfig);
        }
    }
}
