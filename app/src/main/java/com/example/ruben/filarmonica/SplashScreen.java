package com.example.ruben.filarmonica;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.VideoView;

import java.util.Timer;
import java.util.TimerTask;


public class SplashScreen extends ActionBarActivity {
    private static final int TIEMPO_DURACION_VIDEO = 5000;
    private Context contexto;
    private String respuesta = "";
    private ProgressDialog progressDialog = null;
    private VideoView videoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog = new ProgressDialog(SplashScreen.this);
        progressDialog.setCancelable(false);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        contexto = getApplicationContext();

        getSupportActionBar().hide();
        setContentView(R.layout.splash_screen);

        final SharedPreferences sharedPreferences = getSharedPreferences("Filarmonica",
                Context.MODE_PRIVATE);
        respuesta = sharedPreferences.getString("DatosInsertados","NoInsertados");

        new Thread( new Runnable() {
            @Override
            public void run() {
                if(respuesta.equals("NoInsertados")){
                    Intent in = new Intent(SplashScreen.this, ServicioActualizacionBD.class);
                    startService(in);
                }
                //ObtenerEventos hilo = new ObtenerEventos(contexto,sharedPreferences);
                //hilo.execute();
            }
        }).start();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {

                Intent in = new Intent().setClass(SplashScreen.this,MainActivity.class);
                startActivity(in);
                finish();
            }
        };

        //Creamos el splash y el hilo que esperarà el tiempo definido
        Timer timer = new Timer();

        //Cargamos el video.
        videoView = (VideoView) findViewById(R.id.video_view);
        Uri video = Uri.parse("android.resource://" + getPackageName() + "/"
                + R.raw.video_prueba);

        videoView.setVideoURI(video);
        videoView.start();

        timer.schedule(task,TIEMPO_DURACION_VIDEO);
    }

}
