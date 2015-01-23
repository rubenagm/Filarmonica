package com.example.ruben.filarmonica;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by macmini3cuceimobile on 21/01/15.
 */
public class ServicioActualizacionBD extends Service {
    Context contexto;
    ActualizarBD hiloActualizarBD;
    @Override
    public void onCreate(){
        //Cuando es creado el servicio
        contexto = getApplicationContext();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int idArranque){
        Log.i("Servicio","Servicio Creado");
        ConexionBD db = new ConexionBD(contexto);
        SharedPreferences sh = getSharedPreferences("Filarmonica", Context.MODE_PRIVATE);
        String respuesta = sh.getString("DatosInsertados", "NoInsertados");
        if (respuesta.equals("NoInsertados")) {
            ObtenerEventos hilo = new ObtenerEventos(contexto, sh);
            hilo.execute("");
        }
        else{
            ActualizarBD hilo = new ActualizarBD(contexto);
            hilo.execute();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy(){

    }


}
