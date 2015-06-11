package conexion;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import mx.com.filarmonica.MainActivity;
import thread.RespuestaAsyncTask;

/**
 * Created by natafrank on 6/16/15.
 */
public class ActualizarImagen
{
    public final static String KEY_ACTUALIZAR = "actualizar_imagen";

    private Context contexto;
    private RespuestaAsyncTask respuesta;

    /**
     * Constructor.
     *
     * @param contexto Contexto de la actividad.
     * @param respuesta Clase que va a utilziar el resultado devuelto en el método OnPostExecute.
     */
    public ActualizarImagen(Context contexto, RespuestaAsyncTask respuesta)
    {
        this.contexto = contexto;
        this.respuesta = respuesta;
    }

    public void verificarActualzacion()
    {
        VerificarActualizacion hiloVerificador = new VerificarActualizacion();
        hiloVerificador.execute();
    }

    /**
     * Esta clase se encarga de informar sobre las actualizaciones de imágenes disponibles en el
     * servidor.
     */
    private class VerificarActualizacion extends AsyncTask<Void, Void, Integer>
    {
        private String numeroImagenJson;

        @Override
        protected Integer doInBackground(Void... params)
        {
            //Códigos de retorno.
            final int ERROR_ACTULIZAR         = -1;
            final int NO_HAY_ACTUALZIACIONES  = 0;
            final int HAY_ACTUALIZACIONES     = 1;

            if(ConexionInternet.verificarConexion(contexto))
            {
                //URL.
                final String URL_JSON_ACTUALIZAR_IMAGEN = "http://ofj.com.mx/App/imagen.json";

                //Etiquetas JSON.
                final String JSON_ROOT = "imagen";
                final String JSON_NUEVO = "nuevo";

                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(URL_JSON_ACTUALIZAR_IMAGEN);
                try
                {
                    HttpResponse response = httpClient.execute(httpGet);
                    HttpEntity entity = response.getEntity();
                    String resultado = EntityUtils.toString(entity, "UTF-8");
                    JSONObject json = new JSONObject(resultado);
                    JSONArray jsonRoot = json.getJSONArray(JSON_ROOT);

                    //Solo hay un objeto en el JSON por eso se utiliza el 0.
                    numeroImagenJson = jsonRoot.getJSONObject(0).getString(JSON_NUEVO);
                    SharedPreferences sharedPreferences = contexto.getSharedPreferences
                            (MainActivity.KEY_APLICACION, Context.MODE_PRIVATE);
                    String numeroImagenInterno = sharedPreferences.getString(KEY_ACTUALIZAR, "0");
                    Log.i("ActualizarImagen", "Resultado: " + resultado + "Número shared " +
                            "preferences: " + numeroImagenInterno);
                    if(numeroImagenInterno.equals(numeroImagenJson) || numeroImagenJson.equals("0"))
                    {
                        return NO_HAY_ACTUALZIACIONES;
                    }
                    else
                    {
                        return HAY_ACTUALIZACIONES;
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    return ERROR_ACTULIZAR;
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                    return ERROR_ACTULIZAR;
                }
            }
            else
            {
                return ERROR_ACTULIZAR;
            }
        }

        @Override
        protected void onPostExecute(Integer resultado)
        {
            ArrayList<Integer> resultados = new ArrayList<>();
            resultados.add(resultado);
            resultados.add(Integer.parseInt(numeroImagenJson));
            Log.i("ActualizarImagen", "Retorno: " + resultado + "\nJson: " + numeroImagenJson);
            respuesta.respuesta(resultados);
        }
    }
}
