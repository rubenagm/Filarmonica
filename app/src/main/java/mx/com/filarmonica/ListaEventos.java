package mx.com.filarmonica;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import conexion.ConexionInternet;
import thread.RespuestaAsyncTask;
import utilities.TabletManager;


public class ListaEventos extends ActionBarActivity
{
    //Variables layout.
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView lblNoHayInformacion;

    private Context contexto;

    private ConexionBD db;

    private boolean esTablet;

    //Variables del Drawer.
    private ListView list_view_drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_cards);
        contexto = ListaEventos.this;

        //Ocultamos el action bar.
        getSupportActionBar().hide();

        //Comprobamos si es tablet y colocamos horizontalmente la Activity de ser así.
        if(esTablet = TabletManager.esTablet(contexto))
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        //Verificamos la conexión a internet.
        if(!ConexionInternet.verificarConexion(contexto))
        {
            Toast.makeText(contexto, contexto.getResources().getString(R.string
                    .conexion_fallida), Toast.LENGTH_SHORT).show();
        }

        /******************************* ListView Drawer *****************************/

        list_view_drawer = (ListView) findViewById(R.id.drawer_listView);
        list_view_drawer.setAdapter(new ListViewAdapter(this));

        //Ajustar el ListView al ancho de la pantalla
        DisplayMetrics display_metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(display_metrics);
        int width = display_metrics.widthPixels;
        int height = display_metrics.heightPixels;

        if(esTablet)
        {
            list_view_drawer.getLayoutParams().width  = width/4;
        }
        else
        {
            list_view_drawer.getLayoutParams().width  = width;
        }
        list_view_drawer.getLayoutParams().height = height;

        /******************************* ListView Drawer *****************************/

        mRecyclerView = (RecyclerView) findViewById(R.id.lista_cards);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator( new DefaultItemAnimator());
        ArrayList<ItemEvento> lista = new ArrayList<>();
        db = new ConexionBD(contexto);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                SharedPreferences sharedPreferences = getSharedPreferences("Filarmonica",
                        Context.MODE_PRIVATE);
                String respuesta = sharedPreferences.getString("DatosInsertados","NoInsertados");

                if(respuesta.equals("Insertados"))
                {
                    /*
                    ArrayList<ItemEvento> lista = db.obtenerEventos();
                    RecyclerView.Adapter adapter = new AdapterListaEventos(contexto,lista);
                    lblNoHayInformacion = (TextView) findViewById(R.id.lblNoHayInformacion);
                    lblNoHayInformacion.setVisibility(View.INVISIBLE);
                    mRecyclerView.setAdapter(adapter);
                    mSwipeRefreshLayout.setRefreshing(false);*/
                    ActualizarBD actualizarBD = new ActualizarBD(contexto,mSwipeRefreshLayout,mRecyclerView);
                    actualizarBD.execute();
                }
                else
                {
                    ActualizarEventos actualizarEventos = new ActualizarEventos(mSwipeRefreshLayout,
                            mRecyclerView, lblNoHayInformacion, sharedPreferences);
                    actualizarEventos.execute();
                }
            }
        });

        lista = db.obtenerEventos();

        //Si tenemos eventos colocamos el Adapter, sino mostramos el TexrView de vacío.
        if(lista.size() > 0)
        {
            RecyclerView.Adapter adapter = new AdapterListaEventos(contexto,lista);
            mRecyclerView.setAdapter(adapter);
        }
        else
        {
            lblNoHayInformacion = (TextView) findViewById(R.id.lblNoHayInformacion);
            lblNoHayInformacion.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lista_eventos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Clase para atualziar los eventos cuando no hay datos en la Base de Datos.
    private class ActualizarEventos extends AsyncTask<Void, Integer, ArrayList<ItemEvento>>
        implements RespuestaAsyncTask
    {

        private SwipeRefreshLayout swipeRefreshLayout;
        private RecyclerView recyclerView;
        private TextView lblNoHayInformacion;
        private SharedPreferences sharedPreferences;

        public ActualizarEventos(SwipeRefreshLayout swipeRefreshLayout, RecyclerView recyclerView,
                                 TextView lblNoHayInformacion, SharedPreferences sharedPreferences)
        {
            this.swipeRefreshLayout = swipeRefreshLayout;
            this.recyclerView = recyclerView;
            this.lblNoHayInformacion = lblNoHayInformacion;
            this.sharedPreferences = sharedPreferences;
        }

        @Override
        protected void onPreExecute()
        {
            //Comprobar conexión a internet.
        }

        @Override
        protected ArrayList<ItemEvento> doInBackground(Void... params)
        {
            Log.i("frank.frank", "Entró al AsyncTask");
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<ItemEvento> itemEventos)
        {
            ObtenerEventos obtenerEventos = new ObtenerEventos(contexto, sharedPreferences, this);
            obtenerEventos.execute();
        }

        @Override
        public void respuesta(ArrayList resultado)
        {
            if(resultado !=null)
            {
                if(resultado.size() > 0)
                {
                    //Si el resultado es mayor que 0, concluimos que los eventos están en la
                    //base de datos, por lo que podremos obtenerlos.
                    //resultado = db.obtenerEventos();
                    //RecyclerView.Adapter adapter = new AdapterListaEventos(contexto, resultado);
                    //lblNoHayInformacion.setVisibility(View.INVISIBLE);
                    //recyclerView.setAdapter(adapter);
                    //swipeRefreshLayout.setRefreshing(false);

                    ActualizarBD actualizarBD = new ActualizarBD(contexto,swipeRefreshLayout,recyclerView);
                    actualizarBD.execute();
                }
                else
                {
                   // swipeRefreshLayout.setRefreshing(false);
                }
            }
            else
            {
                //swipeRefreshLayout.setRefreshing(false);
            }
        }
    }
}
