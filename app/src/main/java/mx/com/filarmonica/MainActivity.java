package mx.com.filarmonica;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
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

import conexion.ConexionInternet;
import date.DateControl;
import date.DateDifference;
import utilities.TabletManager;

public class MainActivity extends Activity
{
    //Tutorial
    ImageView imageViewHand = null;
    static TranslateAnimation animation;
    ImageView imageViewFondoDrawerTutorial = null;

    //Constants.
    private static final int SLEEP_SECOND = 1000;
    private static final int TIEMPO_ESPERA_BUSQUEDA_PROXIMO_CONCIERTO = (1000 * 60 * 60) * 2;

    //Contexto.
    private static Context contexto;
    private static ProgressDialog progressDialog;

    //Variable para guardar si el dispositivo es tablet.
    private boolean esTablet;

    //Variables del Drawer.
    private ListView list_view_drawer;

    //Variables del layout.
    private TextView lblReloj;
    private TextView lblProximoConcierto;
    private TextView lblDias;
    private TextView lblHoras;
    private TextView lblMinutos;
    private TextView lblSegundos;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        //Obtenemos el contexto.
        contexto = MainActivity.this;

        //Shared poreferences inicio
        SharedPreferences shared = getSharedPreferences("Inicio",Context.MODE_PRIVATE);
        String llave =shared.getString("Inicio","Primera");

        //Comprobamos si es tablet y colocamos horizontalmente la Activity de ser así.
        if(esTablet = TabletManager.esTablet(contexto))
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        //Trigger para salir de la aplicación en caso de que no haya datos insertados debido a la
        // conexión a internet.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras != null)
        {
            if(extras.getString("DatosInsertados").equals("NoInsertados"))
            {
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Configuración Necesaria");
                alertDialog.setMessage(getResources().getString(R.string.conexion_requerida));
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok", new DialogInterface
                        .OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        finish();
                        System.exit(0);
                    }
                });
                alertDialog.show();
            }
        }
        imageViewFondoDrawerTutorial = (ImageView) findViewById(R.id.tutorial_fondo_drawer);

        //Comprobamos la conexión.
        if(!ConexionInternet.verificarConexion(contexto))
        {
            Toast.makeText(contexto, getResources().getString(R.string.conexion_fallida),
                    Toast.LENGTH_LONG).show();
        }

        //Obtenemos las referencias del layout.
        lblReloj		    = (TextView) findViewById(R.id.lbl_contador_proximo_concierto);
        lblProximoConcierto = (TextView) findViewById(R.id.lbl_proximo_concierto);
        lblDias             = (TextView) findViewById(R.id.dias);
        lblHoras            = (TextView) findViewById(R.id.horas);
        lblMinutos          = (TextView) findViewById(R.id.minutos);
        lblSegundos         = (TextView) findViewById(R.id.segundos);

        //Colocamos la fuente al contador.
        Typeface roboto = Typeface.createFromAsset(getAssets(), "fonts/Roboto/Roboto-Light.ttf");
        lblReloj.setTypeface(roboto);
        lblProximoConcierto.setTypeface(roboto);
        lblDias.setTypeface(roboto);
        lblHoras.setTypeface(roboto);
        lblMinutos.setTypeface(roboto);
        lblSegundos.setTypeface(roboto);

        /******************************* ListView Drawer *****************************/
        list_view_drawer = (ListView) findViewById(R.id.drawer_listView);
        list_view_drawer.setAdapter(new ListViewAdapter(this));

        //Se iniclializa el imageview
        imageViewHand = (ImageView) findViewById(R.id.hand_tutorial_drawer);
        ///Animación

        //Ajustar el ListView al ancho de la pantalla
        DisplayMetrics display_metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(display_metrics);
        int height = display_metrics.heightPixels;
        int width = display_metrics.widthPixels;

        if(esTablet)
        {
            list_view_drawer.getLayoutParams().width  = width/4;
        }
        else
        {
            list_view_drawer.getLayoutParams().width  = width;
        }
        list_view_drawer.getLayoutParams().height = height;

        if(llave.equals("Primera")){
            animation = new TranslateAnimation(0, 500, 0, 0);
            animation.setDuration(4000);
            animation.setRepeatCount(Animation.INFINITE);
            animation.setFillAfter(true);
            imageViewHand.startAnimation(animation);

            if(!esTablet)
            {
                DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawerLayout.setDrawerListener(new RightMenuListener());
            }

            SharedPreferences.Editor editor = shared.edit();
            editor.putString("Inicio","Segunda");
            editor.commit();

        }
        else{
            imageViewHand.setVisibility(View.GONE);
            imageViewFondoDrawerTutorial.setVisibility(View.GONE);
        }

        /******************************* ListView Drawer *****************************/

        //Obtenemos el próximo concierto.
        //Buscamos en la base de datos local, sino accedemos a la base de datos remota.
        SharedPreferences sharedPreferences = getSharedPreferences("Filarmonica",
            Context.MODE_PRIVATE);
        String resultadoSharedPreferences = sharedPreferences.getString("DatosInsertados", "NoInsertados");

        if(!resultadoSharedPreferences.equals("NoInsertados"))
        {
            Log.i("frank.frank", "Entró en la conexión local");
            ConexionLocalProximoConcierto conexionLocal = new ConexionLocalProximoConcierto();
            conexionLocal.execute("");
        }
        else
        {
            if(ConexionInternet.verificarConexion(contexto))
            {
                Log.i("frank.frank", "Entró en la conexión remota");
                ConexionRemotaProximoConcierto json = new ConexionRemotaProximoConcierto();
                json.execute("");
            }
        }
    }//OnCreate


    // Animación del hand




    ////////
    /***************************************** CONEXIONES *******************************************/
    //Clase para acceder al JSON.
    private class ConexionRemotaProximoConcierto extends AsyncTask<String, Void, ArrayList<String>>
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
        protected void onPreExecute()
        {
            super.onPreExecute();
            lblProximoConcierto.setVisibility(View.INVISIBLE);
            lblDias.setVisibility(View.INVISIBLE);
            lblHoras.setVisibility(View.INVISIBLE);
            lblMinutos.setVisibility(View.INVISIBLE);
            lblSegundos.setVisibility(View.INVISIBLE);
            progressDialog = new ProgressDialog(contexto, R.style.MyTheme);
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);

            progressDialog.show();
        }

        @Override
        protected ArrayList<String> doInBackground(String... arg0)
        {
            ArrayList<String> fecha = new ArrayList<>();

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

                    //trigger de seguridad para salir en caso de que solo se lea 1 concierto.
                    if(jsonArray.length() < NUMERO_CONCIERTOS_A_LEER)
                    {
                        horas[0] = horas[0] + ":" + minutos[0];
                        fecha.add(fechas[0]);
                        fecha.add(horas[0]);
                        return fecha;
                    }
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
                            fecha.add(fechas[0]);
                            fecha.add(horas[0]);
                            Log.i(TAG, "Agregado: " + fechas[0]);
                        }
                        else
                        {
                            //Validar mensaje de que se está actualmente en concierto.
                            fecha.add("tocandoAhora");
                            Log.i(TAG, "Agregado: tocandoAhora");
                            return fecha;
                        }
                    }
                    else
                    {
                        //VERIFIQUE CONCIERTO MAÑANA
                        //Mandamos el segundo concierto.
                        horas[1] = horas[1] + ":" + minutos[1];
                        fecha.add(fechas[1]);
                        fecha.add(horas[1]);
                        Log.i(TAG, "Agregado: " + fechas[1]);
                    }
                }
                else
                {
                    //Mandamos el primer concierto.
                    horas[0] = horas[0] + ":" + minutos[0];
                    fecha.add(fechas[0]);
                    fecha.add(horas[0]);
                    Log.i(TAG, "Agregado: " + fechas[0]);
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
            Log.i(TAG, "Fecha: " + fecha);
            return fecha;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result)
        {
            progressDialog.dismiss();
            lblProximoConcierto.setVisibility(View.VISIBLE);
            lblDias.setVisibility(View.VISIBLE);
            lblHoras.setVisibility(View.VISIBLE);
            lblMinutos.setVisibility(View.VISIBLE);
            lblSegundos.setVisibility(View.VISIBLE);

            if(result.size() > 0)
            {
                if(result.get(0).equals("tocandoAhora"))
                {

                    DateDifference countdownTimer = DateDifference.getDateDifferenceCompletedTime();
                    iniciarReloj(countdownTimer);
                }
                else
                {
                    //Mandamos la fecha y la hora al parser.
                    DateControl dateControl = new DateControl(result);
                    DateDifference countdownTimer = dateControl.startCountdown();
                    iniciarReloj(countdownTimer);
                }
            }
            else
            {
                Toast.makeText(contexto, "Verifica tu conexión a internet. Intensidad baja.",
                        Toast.LENGTH_LONG).show();

                progressDialog.dismiss();
                lblReloj.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
                lblDias.setVisibility(View.GONE);
                lblHoras.setVisibility(View.GONE);
                lblMinutos.setVisibility(View.GONE);
                lblSegundos.setVisibility(View.GONE);

                lblProximoConcierto.setVisibility(View.VISIBLE);
                lblProximoConcierto.setText(getText(R.string.sin_conciertos));

                //Actualizar al presionar el TextView.
                lblReloj.setText(getText(R.string.click_actualizar));
                lblReloj.setClickable(true);
                lblReloj.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if(ConexionInternet.verificarConexion(contexto))
                        {
                            ConexionRemotaProximoConcierto json = new
                                    ConexionRemotaProximoConcierto();
                            json.execute("");
                        }
                        else
                        {
                            Toast.makeText(contexto, "Verifica tu conexión a internet.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }
    }

    private class ConexionLocalProximoConcierto extends AsyncTask<String, Void, ArrayList<String>>
    {

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            lblReloj.setClickable(false);
            lblProximoConcierto.setVisibility(View.INVISIBLE);
            lblDias.setVisibility(View.INVISIBLE);
            lblHoras.setVisibility(View.INVISIBLE);
            lblMinutos.setVisibility(View.INVISIBLE);
            lblSegundos.setVisibility(View.INVISIBLE);
            progressDialog = new ProgressDialog(contexto, R.style.MyTheme);
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);

            progressDialog.show();
        }

        @Override
        protected ArrayList<String> doInBackground(String... params)
        {
            ConexionBD db = new ConexionBD(contexto);
            return db.obtenerFechaProximoEvento();
        }

        @Override
        protected void onPostExecute(ArrayList<String> result)
        {
            super.onPostExecute(result);
            if(result != null)
            {
                if(result.get(0).equals("sin conciertos"))
                {
                    progressDialog.dismiss();
                    lblReloj.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
                    lblDias.setVisibility(View.GONE);
                    lblHoras.setVisibility(View.GONE);
                    lblMinutos.setVisibility(View.GONE);
                    lblSegundos.setVisibility(View.GONE);

                    lblProximoConcierto.setVisibility(View.VISIBLE);
                    lblProximoConcierto.setText(getText(R.string.sin_conciertos));

                    //Actualizar al presionar el TextView.
                    lblReloj.setText(getText(R.string.click_actualizar));
                    lblReloj.setClickable(true);
                    lblReloj.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            if(ConexionInternet.verificarConexion(contexto))
                            {
                                ConexionRemotaProximoConcierto json = new
                                        ConexionRemotaProximoConcierto();
                                json.execute("");
                            }
                            else
                            {
                                Toast.makeText(contexto, "Verifica tu conexión a internet.",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                else
                {
                    progressDialog.dismiss();
                    lblProximoConcierto.setVisibility(View.VISIBLE);
                    lblDias.setVisibility(View.VISIBLE);
                    lblHoras.setVisibility(View.VISIBLE);
                    lblMinutos.setVisibility(View.VISIBLE);
                    lblSegundos.setVisibility(View.VISIBLE);
                    //Mandamos la fecha y la hora al parser.
                    DateControl dateControl = new DateControl(result);
                    DateDifference countdownTimer = dateControl.startCountdown();
                    iniciarReloj(countdownTimer);
                }
            }
            //Si hubo error al obtener los datos de manera local, los obtenemos de manera remota.
            else
            {
                ConexionRemotaProximoConcierto json = new ConexionRemotaProximoConcierto();
                json.execute("");
            }
        }
    }

    /*************************************** CONEXIONES *****************************************/

    public void iniciarReloj(final DateDifference dateDifference)
    {
        Thread thread = new Thread(new Runnable()
        {
            DateDifference countdownTimer = dateDifference;
            @Override
            public void run()
            {
                while(!countdownTimer.isCompletedTime())
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            String tiempo_faltante = String.format("%02d:%02d:%02d:%02d",
                                    countdownTimer.getDay(), countdownTimer.getHour(),
                                    countdownTimer.getMinute(), countdownTimer.getSecond());
                            lblReloj.setText(tiempo_faltante);
                            countdownTimer.substractSecond();
                        }
                    });
                    try
                    {
                        Thread.sleep(SLEEP_SECOND);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        //Una vez el concierto empezado, colocar mensaje y después de determinado tiempo
                        //volver a hacer una consulta en busca del próximo concierto.
                        lblReloj.setText(getText(R.string.en_concierto));
                        lblReloj.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
                        lblDias.setVisibility(View.GONE);
                        lblHoras.setVisibility(View.GONE);
                        lblMinutos.setVisibility(View.GONE);
                        lblSegundos.setVisibility(View.GONE);
                        lblProximoConcierto.setVisibility(View.GONE);
                    }
                });

                //Esperamos y buscamos por próximos conciertos.
                try
                {
                    Thread.sleep(TIEMPO_ESPERA_BUSQUEDA_PROXIMO_CONCIERTO);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                ConexionLocalProximoConcierto conexionLocal = new ConexionLocalProximoConcierto();
                conexionLocal.execute("");
            }
        });

        thread.start();
    }
    private class RightMenuListener implements android.support.v4.widget.DrawerLayout.DrawerListener {
        @Override
        public void onDrawerSlide(View view, float v) {

        }

        @Override
        public void onDrawerOpened(View view) {
            imageViewHand.clearAnimation();
            imageViewHand.setVisibility(View.GONE);
            imageViewFondoDrawerTutorial.setVisibility(View.GONE);
            NotificationManager notificationManager = (NotificationManager) contexto
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(012345);
        }

        @Override
        public void onDrawerClosed(View view) {

        }

        @Override
        public void onDrawerStateChanged(int i) {

        }
    }
}
