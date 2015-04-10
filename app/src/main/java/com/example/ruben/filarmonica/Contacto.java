package com.example.ruben.filarmonica;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.VideoView;


public class Contacto extends Activity
{

    //Variables del Drawer.
    private ListView list_view_drawer;
    private VideoView videoView;

    //Variables del Layout.
    private ImageView btnEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacto);

        //Obtenemos las refererencias.
        videoView = (VideoView) findViewById(R.id.video_contacto);
        btnEmail  = (ImageView) findViewById(R.id.btn_email);

        //Cargamos el video.
        videoView.setBackgroundColor(Color.WHITE);
        Uri video = Uri.parse("android.resource://" + getPackageName() + "/"
                + R.raw.video_contacto);

        videoView.setVideoURI(video);
        videoView.start();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
        {
            @Override
            public void onPrepared(MediaPlayer mp)
            {
                videoView.setBackgroundColor(Color.TRANSPARENT);
            }
        });

        //Colocamos el listener del video.
        btnEmail.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","info@ofj.com.mx", null));
                startActivity(Intent.createChooser(emailIntent, "Enviar Correo..."));
            }
        });

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
