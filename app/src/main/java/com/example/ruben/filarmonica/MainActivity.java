package com.example.ruben.filarmonica;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        //Obtenemos el contexto.
        contexto = getApplicationContext();
        /*-------- Obtener eventos ---*/
        SharedPreferences sharedPreferences = getSharedPreferences("Filarmonica",Context.MODE_PRIVATE);
        String respuesta = sharedPreferences.getString("DatosInsertados","NoInsertados");

        if(respuesta.equals("NoInsertados")){
            ObtenerEventos hilo = new ObtenerEventos(contexto,sharedPreferences);
            hilo.execute("");
        }
        //Ocultamos el actionbar.
        //getActionBar().hide();

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

            //Mandamos la fecha y la hora al parser.
            ParserFecha parser_fecha = new ParserFecha(fecha, lblReloj);

            //Obtenemos los valores.
            dia_efectivo = parser_fecha.getDiaEfectivo();
            hora_efectiva = parser_fecha.getHoraEfectiva();
            minuto_efectivo = parser_fecha.getMinutoEfectivo();
            segundo_efectivo = parser_fecha.getSegundoEfectivo();

            //Iniciamos el reloj
            iniciarReloj();
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

        //Variables de control
        private final static String TAG = "frank";

        //Query a mandar.
        private final static String QUERY_PROXIMO_CONCIERTO = "SELECT * FROM evento AS E"
                + " JOIN fecha AS F ON E.id = F.evento_id"
                + " WHERE F.fecha > CURRENT_TIMESTAMP"
                + " ORDER BY F.fecha"
                + " LIMIT 1 ;";

        //Variables de conexión
        private HttpClient mHttpClient = new DefaultHttpClient();
        private HttpPost mHttpPost = new HttpPost("http://ofj.com.mx/App/prueba.php");

        @Override
        protected ArrayList<String> doInBackground(String... arg0)
        {
            //Inicializamos el arreglo.
            ArrayList<String> arreglo = new ArrayList<String>();

            String fecha  = "";
            String hora   = "";
            String minuto = "";

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

                //Creamos el JSON del evento dentro del arreglo data.
                JSONObject jsonElement = jsonArray.getJSONObject(0);

                //Recogemos los datos que necesitamos.
                fecha  = jsonElement.getString(JSON_FECHA);
                hora   = jsonElement.getString(JSON_HORA);
                minuto = jsonElement.getString(JSON_MINUTO);

                //Mandamos la hora en el fomato "20:30", ya que así lo trabaja el parser de la fecha.
                hora = hora + ":" + minuto;
            }

            catch(JSONException e)
            {
                Log.e(TAG, "Error al leer el JSON\n" + e);
            }
            catch(IOException e)
            {
                Log.e(TAG, "Error con la conexión HTTP");
            }

            arreglo.add(fecha);
            arreglo.add(hora);

            return arreglo;
        }

    }


    /***************************************** CONEXIONES *******************************************/

    public void iniciarReloj()
    {

        Thread thread = new Thread(new Runnable()
        {
            int i, j, k, l;
            @Override
            public void run()
            {
                for(i = dia_efectivo; i >= 0; i --)
                {
                    for(j = hora_efectiva; j >= 0; j--)
                    {
                        for(k = minuto_efectivo; k >= 0; k --)
                        {
                            for(l = segundo_efectivo; l >= 0; l--)
                            {
                                runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        String tiempo_faltante = i + ":" + j + ":" + k + ":" + l;
                                        tiempo_faltante = String.format("%02d:%02d:%02d:%02d", i, j, k, l);
                                        lblReloj.setText(tiempo_faltante);
                                    }
                                });
                                try
                                {
                                    Thread.sleep(1000);
                                }
                                catch (InterruptedException e)
                                {
                                    e.printStackTrace();
                                }
                            }

                            segundo_efectivo = 59;

                        }
                        minuto_efectivo = 59;
                    }
                    hora_efectiva = 23;
                }
            }

        });

        thread.start();
    }
}
