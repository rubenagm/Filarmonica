package com.example.ruben.filarmonica;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import date.DateControl;

public class MainActivity extends Activity
{

    //Contexto.
    private Context contexto;

    //Variables del Drawer.
    private ListView list_view_drawer;

    //Arreglos.
    private ArrayList<ItemDrawer> array_item_drawer;
    private TypedArray array_iconos;

    //Variables para el contador.
    private static ArrayList<String> fecha;

    //Variables que se usarán para crear el reloj.
    private int dia_efectivo;
    private int hora_efectiva;
    private int minuto_efectivo;
    private int segundo_efectivo;

    //Variables del layout.
    private TextView lblReloj;

    private static Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);


         // Hilo de actualización de eventos y contenido multimedia
        Intent in = new Intent(MainActivity.this, ServicioActualizacionBD.class);
        startService(in);
        //Obtenemos el contexto.
        contexto = getApplicationContext();

        //Obtenemos als referencias del layout.
        lblReloj		 = (TextView) findViewById(R.id.lbl_contador_proximo_concierto);

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
                array_item_drawer.add(new ItemDrawer(array_iconos.getResourceId(i, -1),
                        array_iconos.getResourceId(i+1, -1)));
            }
            else
            {
                array_item_drawer.add(new ItemDrawer(array_iconos.getResourceId(i, -1)));
            }
        }

        //Colocamos el adaptador al ListView.
        list_view_drawer.setAdapter(new ListAdapterDrawer(this, array_item_drawer));

        //Colocamos el click listener al ListView.
        list_view_drawer.setOnItemClickListener(new OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                switch(position)
                {
                    case 0:
                    {
                        Intent i = new Intent(MainActivity.this,ListaEventos.class);
                        startActivity(i);
                        break;
                    }
                    case 1:
                    {
                        Intent i = new Intent(MainActivity.this,Noticias.class);
                        startActivity(i);
                        break;
                    }
                }
            }

        });
        /******************************* ListView Drawer *****************************/

        ConexionProximoConcierto json = new ConexionProximoConcierto();
        json.execute("");
        try
        {
            fecha = json.get();

            if(fecha.get(0).equals("tocandoAhora"))
            {
                lblReloj.setText("Tocando en este momento.");
            }
            else
            {
                //Mandamos la fecha y la hora al parser.
                DateControl dateControl = new DateControl(fecha);

            }
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        catch (ExecutionException e)
        {
            e.printStackTrace();
        }
    }//OnCreate

    /***************************************** CONEXIONES *******************************************/
    //Clase para acceder al JSON.
    private static class ConexionProximoConcierto extends AsyncTask<String, Void, ArrayList<String>>
    {

        //Etiquetas JSON.
        private final static String JSON_FECHA  = "fecha";
        private final static String JSON_HORA   = "hora";
        private final static String JSON_MINUTO = "minuto";
        private final static int NUMERO_CONCIERTOS_A_LEER = 2;

        //Variables de control
        private final static String TAG = "frank";

        //Query a mandar.
        //Vamos a tomar los 2 conciertos más recientes y vamos a comprobar si el primero ya pasó
        //de ser así, pasamos al segundo concierto.
        private final static String QUERY_PROXIMO_CONCIERTO = "SELECT * FROM fecha " +
                "WHERE fecha >= CURRENT_DATE ORDER BY fecha ASC LIMIT 2";

        //Variables de conexión
        private HttpClient mHttpClient = new DefaultHttpClient();
        private HttpPost mHttpPost = new HttpPost("http://ofj.com.mx/App/prueba1.php");

        @Override
        protected ArrayList<String> doInBackground(String... arg0)
        {
            //Inicializamos el arreglo.
            ArrayList<String> arreglo = new ArrayList<String>();

            String[] fechas  = new String[NUMERO_CONCIERTOS_A_LEER];
            String[] horas   = new String[NUMERO_CONCIERTOS_A_LEER];
            String[] minutos = new String[NUMERO_CONCIERTOS_A_LEER];

            try
            {
                Log.i(TAG, "QUERY: " + QUERY_PROXIMO_CONCIERTO);
                //Agregamos el POST
                List<NameValuePair> mNameValuePairs = new ArrayList<NameValuePair>(1);
                mNameValuePairs.add(new BasicNameValuePair("query", QUERY_PROXIMO_CONCIERTO));
                mHttpPost.setEntity(new UrlEncodedFormEntity(mNameValuePairs));

                //Mandamos el POST al servidor
                HttpResponse response = mHttpClient.execute(mHttpPost);

                //Creamos el resultado
                HttpEntity entity = response.getEntity();
                String resultado = EntityUtils.toString(entity, "UTF-8");

                //Limpiamos el resultado
                resultado.replace("\n", "");
                resultado = resultado.substring(9);

                //Resultado HTTPRESPONSE
                Log.i(TAG, "Resultado: " + resultado);

                //Creamos el JSON del arreglo "data"
                JSONObject jsonObject = new JSONObject(resultado);
                JSONArray jsonArray = jsonObject.getJSONArray("data");

                //Recogemos la información de los dos eventos.
                JSONObject[] conciertosProximos = new JSONObject[NUMERO_CONCIERTOS_A_LEER];
                for(int i = 0; i < NUMERO_CONCIERTOS_A_LEER; i++)
                {
                    conciertosProximos[i] = jsonArray.getJSONObject(i);
                    fechas[i]  = conciertosProximos[i].getString(JSON_FECHA);
                    horas[i]   = conciertosProximos[i].getString(JSON_HORA);
                    minutos[i] = conciertosProximos[i].getString(JSON_MINUTO);
                }

                //Comprobamos cual será el siguiente concierto y lo añadimos al arreglo.
                Time fechaActual = new Time();
                fechaActual.setToNow();

                //Aumentamos en 1 el mes para corregir el formato [0 - 11].
                fechaActual.month += 1;

                if(fechas[0].equals(fechaActual.year + "-" +
                        String.format("%02d", fechaActual.month) + "-" +
                        String.format("%02d", fechaActual.monthDay)))
                {
                    if(Integer.parseInt(horas[0]) >= fechaActual.hour)
                    {
                        if(Integer.parseInt(minutos[0]) >= fechaActual.minute)
                        {
                            //Mandamos el primer concierto.
                            horas[0] = horas[0] + ":" + minutos[0];
                            arreglo.add(fechas[0]);
                            arreglo.add(horas[0]);
                        }
                        else
                        {
                            //Validar mensaje de que se está actualmente en concierto.
                            arreglo.add("tocandoAhora");
                            return arreglo;
                        }
                    }
                    else
                    {
                        //VERIFIQUE CONCIERTO MAÑANA
                        //Mandamos el segundo concierto.
                        horas[1] = horas[1] + ":" + minutos[1];
                        arreglo.add(fechas[1]);
                        arreglo.add(horas[1]);
                    }
                }
                else
                {
                    //Mandamos el primer concierto.
                    horas[0] = horas[0] + ":" + minutos[0];
                    arreglo.add(fechas[0]);
                    arreglo.add(horas[0]);
                }
            }

            catch(JSONException e)
            {
                Log.e(TAG, "Error al leer el JSON\n" + e);
            }
            catch(IOException e)
            {
                Log.e(TAG, "Error con la conexión HTTP");
            }

            return arreglo;
        }

    }


    /***************************************** CONEXIONES *******************************************/
}
