package com.example.ruben.filarmonica;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import java.util.Timer;
import java.util.TimerTask;


public class SplashScreen extends ActionBarActivity {
    private static final int TIEMPO = 5000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.splash_screen);
        SharedPreferences sharedPreferences = getSharedPreferences("Filarmonica", Context.MODE_PRIVATE);
        String respuesta = sharedPreferences.getString("DatosInsertados","NoInsertados");

        new Thread( new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashScreen.this,ServicioActualizacionBD.class);
                startService(i);
            }
        }).run();
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
        timer.schedule(task,TIEMPO);
    }

}
