package conexion;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Frank on 01/04/2015.
 */
public class ConexionInternet
{
    public static boolean verificarConexion(Context contexto)
    {
        boolean conectado = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) contexto
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        //Obtenemos todas las conexiones (WiFi, 3g, 4g, etc.).
        NetworkInfo[] redes = connectivityManager.getAllNetworkInfo();

        for(int i = 0; i < redes.length; i++)
        {
            if(redes[i].getState() == NetworkInfo.State.CONNECTED)
            {
                conectado = true;
                break;
            }
        }

        return conectado;
    }
}
