package com.example.ruben.filarmonica;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;

import tabs.SlidingTabLayout;

/**
 * Created by natafrank on 1/20/15.
 */
public class Streaming extends ActionBarActivity
{

    //Varibles para el streaming
    static final String LINK_STREAMING = "http://74.63.97.188:1935/gnode05/videognode05/playlist.m3u8?DVR";
    static final String DIA_DE_STREAMING = "domingo";
    static VideoView videoViewStreaming = null;
    static final int ID_NOTIFICACION_MUSICA = 145261271;
    static final String DIA_NOCHE = "PM";
    static final int HORA_INICIO = 12; //Hora puesta en formato de 24hrs
    static final int HORA_FIN = 14; //Hora puesta en formato de 24hrs
    static final int MINUTOS_INICIO = 30;
    static String fechaProximoStreaming = "";
    static RelativeLayout layoutControlVolumenStreaming = null;
    static SeekBar controlVolumenStreaming = null;

    //Declaracion de controles para el reproductor
    static ImageButton botonPlay;
    static TextView textViewTituloCancion;
    static TextView textViewDirector;
    static TextView textViewduracion;
    ///////
    static boolean banderaConexion = false;
    //VARIABLES NECESARIAS PARA EL REPRODUCTOR DE MUSICA
    static public ServicioMusica musicSrv;
    static public Intent playIntent;
    static public boolean musicBound=false;
    static ArrayList<ItemStreamingMusica> canciones = new ArrayList<ItemStreamingMusica>();

    //control de volumen de reproductor de música
    static public SeekBar controlVolumen = null;
    static public AudioManager audioManager = null;
    static public RelativeLayout layoutVolumen = null;
    static public ImageButton botonMostrarControlVolumen = null;
     //Contexto
    private static Context contexto;

    //Número de tabs que contiene la actividad.
    private static final int NUMERO_TABS = 3;

    //Pager que controla los eventos del ViewPager.
    private ViewPager mPager;

    //Manejador de los tabs.
    private static SlidingTabLayout mTabs;

    //ListView del drawer.
    private ListView list_view_drawer;

    /******* Variables del reproductor de video. *******
     * */
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
    ///////Streaming
    static RecyclerView mRecyclerViewStreaming;

    //Adaptador del RecyclerView.
    static RecyclerView.Adapter adapterStreaming;

    //Fragmento del reproductor de youtube.
    static YouTubePlayerSupportFragment youTubePlayerSupportFragment;

    //Manager de fragmentos.
    static FragmentManager supportManager;

    //Imágenes de las tabs.
    private static int[] imagenesTab =
    {
        R.drawable.video_icon,
        R.drawable.musica_icon_off,
        R.drawable.live_icon,
        R.drawable.video_icon_off,
        R.drawable.musica_icon,
        R.drawable.live_icon_on

    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_tabs);
        //

        audioManager =(AudioManager) getSystemService(Context.AUDIO_SERVICE);
        //Obtenemos el contexto.
        contexto = getApplicationContext();

        //Ocultamos el ActionBar.
        getSupportActionBar().hide();

        supportManager = getSupportFragmentManager();

        //Obtenemos las referencias.
        mPager = (ViewPager) findViewById(R.id.pager);
        mTabs  = new SlidingTabLayout(contexto);
        mTabs  = (SlidingTabLayout) findViewById(R.id.tabs);

        //Colocamos el adapdator al ViewPager.
        mPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));

        mTabs.setDescription("Streaming");
        //Distribuimos las tabs en ancho de manera equivalente.
        mTabs.setDistributeEvenly(true);

        //Colocamos el ViewPager a las Tabs.
        mTabs.setViewPager(mPager);


        /******************************* ListView Drawer *****************************/
        list_view_drawer = (ListView) findViewById(R.id.drawer_listView);
        list_view_drawer.setAdapter(new ListViewAdapter(this));

        //Ajustar el ListView al ancho de la pantalla
        DisplayMetrics display_metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(display_metrics);
        int width = display_metrics.widthPixels;
        list_view_drawer.getLayoutParams().width = width;
        int height = display_metrics.heightPixels;
        list_view_drawer.getLayoutParams().height = height;
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
             1.- Música.
             2.- Streaming.*/
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
                case 2:
                {
                    MyStreamingFragment myStreamingFragment =
                            MyStreamingFragment.newInstance(position);
                    return myStreamingFragment;
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

    //Clase que crea el fragmento del streaming.
    public static class MyStreamingFragment extends Fragment
    {
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState)
        {
            View layout = inflater.inflate(R.layout.fragment_streaming, container, false);

            //Se obtiene el día actual
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
            Date d = new Date();
            String dayOfTheWeek = sdf.format(d);

            //Log.i("Streaming dia de la semana",dayOfTheWeek);

            //Se obtiene la hora
            Date now = new Date();
            SimpleDateFormat sdf2 = new SimpleDateFormat("kk:mm");
            String formattedTime = sdf2.format(now);

            Log.i("Streaming hora",formattedTime);

            //se inicializa el boton de play
            final ImageView imageViewBotonPlay = (ImageView) layout.findViewById(R.id.streaming_live_play_streaming);

            //saber que días faltan para el concierto
            int diasfaltantes = retornarDiasFaltantes(dayOfTheWeek);

            {
                Calendar c = Calendar.getInstance();
                c.add(Calendar.DATE, diasfaltantes);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
                SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
                fechaProximoStreaming = sdf1.format(c.getTime());
            }

            //En caso de no ser el día que se reproduzca el streaming.
            if(dayOfTheWeek.equals(DIA_DE_STREAMING)&& validarHoraStreaming(formattedTime)){

                //Ocultar el layout que muestra que el streaming no está disponible
                RelativeLayout layoutStreaming = (RelativeLayout) layout.findViewById(R.id.streaming_layout_no_mostrar_video);

                //Ocultarlo
                layoutStreaming.setVisibility(View.GONE);

                //Habilitar el botón de play
                videoViewStreaming = (VideoView) layout.findViewById(R.id.video_streaming);
                imageViewBotonPlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //En caso de estar reproduciendo
                        if(videoViewStreaming.isPlaying()){
                            imageViewBotonPlay.setImageResource(R.drawable.reproductor_boton_play);
                            videoViewStreaming.pause();
                        }
                        //En caso de no estar reproduciendo el video
                        else
                        {
                            imageViewBotonPlay.setImageResource(R.drawable.reproductor_boton_pause);
                            Charset.forName("UTF-8").encode(LINK_STREAMING);
                            Uri vidUri = Uri.parse(LINK_STREAMING);
                            videoViewStreaming.setVideoURI(vidUri);
                            videoViewStreaming.start();
                        }
                    }
                });

                //Control de volumen de streaming
                controlVolumenStreaming = (SeekBar) layout.findViewById(R.id.streaming_control_volumen);
                layoutControlVolumenStreaming = (RelativeLayout) layout.findViewById(R.id.streaming_layout_control_volumen);
                layoutControlVolumenStreaming.setVisibility(View.GONE);//Se oculta

                controlVolumenStreaming.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
                controlVolumenStreaming.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        layoutControlVolumenStreaming.setVisibility(View.GONE);
                    }
                });

                ImageView botonVolumenStreaming = (ImageView) layout.findViewById(R.id.streaming_live_volumen_streaming);
                botonVolumenStreaming.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        layoutControlVolumenStreaming.setVisibility(View.VISIBLE);
                    }
                });
            }
            else{
                //Mostrar mensaje de que el streaming no está disponible
                imageViewBotonPlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(contexto,"El streaming solo está disponible los domingos",Toast.LENGTH_SHORT).show();
                    }
                });

                //validar la fecha para colocarla en el textview
                TextView textViewTextOStreamingProximoStreaming = (TextView) layout.findViewById(R.id.texto_streaming_proximo_streaming);

                textViewTextOStreamingProximoStreaming.setText("Disfruta del streaming de la Filarmonica de Jalisco el próximo domingo: "+fechaProximoStreaming);
            }

            return layout;
        }

        //Método que regresa un fragmento.
        static MyStreamingFragment newInstance(int position)
        {
            MyStreamingFragment streamingFragment = new MyStreamingFragment();
            Bundle args = new Bundle();
            args.putInt("position", position);
            streamingFragment.setArguments(args);
            return streamingFragment;


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
            SeekBar barra = (SeekBar) layout.findViewById(R.id.reproductor_barra_posicion);

            //Control de volumen
            controlVolumen = (SeekBar) layout.findViewById(R.id.reproductor_control_volumen);
            layoutVolumen = (RelativeLayout) layout.findViewById(R.id.reproductor_layout_control_volumen);
            layoutVolumen.setVisibility(View.GONE);

            botonMostrarControlVolumen = (ImageButton) layout.findViewById(R.id.boton_volumen);

            botonMostrarControlVolumen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    layoutVolumen.setVisibility(View.VISIBLE);
                }
            });
            controlVolumen.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
            controlVolumen.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    layoutVolumen.setVisibility(View.GONE);
                }
            });



            //Inicializacion de los textView del streaming
            textViewDirector = (TextView) layout.findViewById(R.id.reproductor_streaming_director);
            textViewTituloCancion = (TextView) layout.findViewById(R.id.reproductor_streaming_titulo_cancion);
            textViewduracion = (TextView) layout.findViewById(R.id.reproductor_streaming_duracion);
            textViewDirector.setText(canciones.get(0).getDirector());
            textViewTituloCancion.setText(canciones.get(0).getTitulo());
            textViewduracion.setText(canciones.get(0).getDuracion());

            //Recycler view
            mRecyclerViewStreaming = (RecyclerView) layout.findViewById(R.id.lista_streaming);
            mRecyclerViewStreaming.setLayoutManager(new LinearLayoutManager(contexto));
            mRecyclerViewStreaming.setItemAnimator(new DefaultItemAnimator());

            //BotonPlay
            botonPlay = (ImageButton) layout.findViewById(R.id.boton_play);
            botonPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    musicSrv.setItems(textViewTituloCancion,textViewDirector,textViewduracion);
                    if(musicSrv.getReproduccion()){
                        musicSrv.pauseSong();
                        botonPlay.setImageResource(R.drawable.reproductor_boton_play);
                    }
                    else{
                        musicSrv.setSong(0);
                        musicSrv.playSong();
                        botonPlay.setImageResource(R.drawable.reproductor_boton_pause);
                    }
                }
            });

            //Boton de siguiente cancion
            ImageButton botonNext = (ImageButton) layout.findViewById(R.id.boton_siguiente);
            botonNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    musicSrv.setItems(textViewTituloCancion,textViewDirector,textViewduracion);
                    musicSrv.nextSong();
                }
            });

            //Boton de cancion previa
            ImageButton botonPrev = (ImageButton) layout.findViewById(R.id.boton_anterior);
            botonPrev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    musicSrv.setItems(textViewTituloCancion,textViewDirector,textViewduracion);
                    musicSrv.prevSong();
                }
            });

            //checar si la conexión con el servicio ya existe
            if(banderaConexion){
                musicSrv.setItems(textViewTituloCancion,textViewDirector,textViewduracion);
                //Si ya existe y se está reproduciendo
                if(musicSrv != null){

                    adapterStreaming = new AdapterListaStreaming(contexto,canciones,musicSrv);
                    mRecyclerViewStreaming.setAdapter(adapterStreaming);
                }
            }
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
        if(listaVideos!=null)     textViewTituloVideo.setText(listaVideos.get(0).getTitulo());

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
    ////CONEXION DEL SERVICIO DE MUSICA CON EL ACTIVITY
    private static ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ServicioMusica.MusicBinder binder = (ServicioMusica.MusicBinder)service;
            //get service
            musicSrv = binder.getService();
            //pass list
            musicSrv.setList(canciones);
            musicSrv.setItems(textViewTituloCancion,textViewDirector,textViewduracion);
            musicBound = true;
            //Cuando la conexión se realiza, se manda el objeto del servicio para que pueda ser utilizado por la lista de canciones
            adapterStreaming = new AdapterListaStreaming(contexto,canciones,musicSrv);
            mRecyclerViewStreaming.setAdapter(adapterStreaming);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };


    @Override
    protected void onStart() {
        super.onStart();

        if(playIntent==null){
            //Agregar las canciones a la lista
            canciones.add(new ItemStreamingMusica(1,"Bruckner Symphony No.4","Marco Parisotto","14:48","http://ofj.com.mx/App/mp3/1.mp3"));
            canciones.add(new ItemStreamingMusica(2,"Beatles Suite","Marco Parisotto","6:26","http://ofj.com.mx/App/mp3/2.mp3"));
            canciones.add(new ItemStreamingMusica(3,"Brahms Symphony No.1 mvt 1","Marco Parisotto","13:15","http://ofj.com.mx/App/mp3/3.mp3"));
            canciones.add(new ItemStreamingMusica(4,"Tchaikovsky Serenade","Marco Parisotto","17:19","http://ofj.com.mx/App/mp3/4.mp3"));
            playIntent = new Intent(Streaming.this, ServicioMusica.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);

        }
        else{
            banderaConexion = true;
        }
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(this, Streaming.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

// build notification
// the addAction re-use the same intent to keep the example short
        Notification n  = new Notification.Builder(this)
                .setContentTitle("Reproduciendo")
                .setContentText("Presiona para ir al preproductor")
                .setSmallIcon(R.drawable.reproductor_boton_play)
                .setContentIntent(pIntent)
                .setAutoCancel(true).build();


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(ID_NOTIFICACION_MUSICA, n);
        finish();
        Intent i = new Intent(Streaming.this, MainActivity.class);
        startActivity(i);
    }

    static public boolean validarHoraStreaming(String hora){

        //En caso de ser el tiempo correcto se obtiene la hora
        StringTokenizer token = new StringTokenizer(hora,":");
        int horaActual =  Integer.parseInt(token.nextToken());
        if(horaActual>=HORA_INICIO && horaActual<=HORA_FIN){
            return true;
        }
        return false;
    }

    static public int retornarDiasFaltantes(String dia ){
        switch(dia){
            case "lunes":
                return 6;
            case "martes":
                return 5;
            case "miercoles":
                return 4;
            case "jueves":
                return 3;
            case "viernes":
                return 2;
            case "sabado":
                return 1;
            case "domingo":
                return 0;
            default:
                return 0;
        }
    }
}
