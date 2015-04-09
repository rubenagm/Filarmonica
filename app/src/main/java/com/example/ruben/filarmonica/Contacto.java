package com.example.ruben.filarmonica;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ListView;
import android.widget.VideoView;


public class Contacto extends Activity
{

    //Contexto.
    private Context contexto;

    //Variables del Drawer.
    private ListView list_view_drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacto);

        //Obtenemos el contexto.
        contexto = getApplicationContext();

        //Obtenemos las refererencias.
        VideoView videoView = (VideoView) findViewById(R.id.video_contacto);

        //Cargamos el video.
        videoView = (VideoView) findViewById(R.id.video_contacto);
        videoView.setZOrderOnTop(true);
        Uri video = Uri.parse("android.resource://" + getPackageName() + "/"
                + R.raw.video_contacto);

        videoView.setVideoURI(video);
        videoView.start();
        if(videoView.isPlaying())
        {
            videoView.setZOrderOnTop(false);
        }

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
    }
}
