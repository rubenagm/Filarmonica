package mx.com.filarmonica;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import conexion.ConexionInternet;
import tabs.SlidingTabLayout;
import utilities.TabletManager;


public class Noticias extends ActionBarActivity
{
    static DialogoMostrarImagenInstagram newFragment ;
    static FragmentTransaction ft;
    static Fragment prev;
    /// Dialogo

    ////
    private static Context contexto;
    private final static int NUMERO_TABS = 3;

    //Imágenes de las tabs al iniciar la activity.
    private static int[] imagenesTab =
            {
                    R.drawable.fb_on,
                    R.drawable.tw,
                    R.drawable.insta,
            };

    //Pager que controla los eventos del ViewPager.
    private ViewPager mPager;

    //Manejador de los tabs.
    private SlidingTabLayout mTabs;

    //ListView del drawer.
    private ListView list_view_drawer;

    //Arreglos.
    private static ArrayList<ItemTwitter> twitterArray;
    private static ArrayList<ItemFacebook> facebookArray;
    private static  ArrayList<ItemImagenInstagram> imagenesInstagram;

    //Bandera que indicará si ya hay noticias en la Base de Datos y únicamente es necesario
    //actualizar.
    private static boolean hayNoticiasEnBD = false;

    //Progress bar de los hilos al obtener la información.
    private ProgressBar progressBar;

    private boolean esTablet;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        ft = getSupportFragmentManager().beginTransaction();
        prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);

        //Obtenemos el contexto.
        contexto = getApplicationContext();
        getSupportActionBar().hide();

        //Comprobamos si es tablet y colocamos horizontalmente la Activity de ser así.
        if(esTablet = TabletManager.esTablet(contexto))
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        //Obtenemos las referencias.
        mPager = (ViewPager) findViewById(R.id.pager);
        mTabs  = new SlidingTabLayout(contexto);
        mTabs  = (SlidingTabLayout) findViewById(R.id.tabs);

        //Colocamos el adapdator al ViewPager.
        mPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));

        //Distribuimos las tabs en ancho de manera equivalente.
        mTabs.setDistributeEvenly(true);

        //Colocamos la descripción.
        mTabs.setDescription("Noticias");
        //Colocamos el ViewPager a las Tabs.
        mTabs.setViewPager(mPager);

        //Comprobamos si tiene hay noticas insertadas en la base de datos.
        SharedPreferences sharedPreferences = getSharedPreferences("Noticias",
                Context.MODE_PRIVATE);
        String resultadoSharedPreferences = sharedPreferences.getString("NoticiasInsertadas",
                "NoInsertadas");

        if(resultadoSharedPreferences.equals("NoInsertadas"))
        {
            boolean noticiasInsertadas = true;

            GetDataTwitter hiloTwitter = new GetDataTwitter();
            hiloTwitter.execute();
            try {
                twitterArray = hiloTwitter.get();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
                noticiasInsertadas = false;
            }
            catch (ExecutionException e)
            {
                e.printStackTrace();
                noticiasInsertadas = false;
            }

            //Obtenemos los datos de facebook.
            GetDataFacebook facebookThread = new GetDataFacebook();
            facebookThread.execute();
            try
            {
                facebookArray = facebookThread.get();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
                noticiasInsertadas = false;
            }
            catch (ExecutionException e)
            {
                e.printStackTrace();
                noticiasInsertadas = false;
            }

            //Marcamos las noticias como insertadas.
            if(noticiasInsertadas)
            {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("NoticiasInsertadas","Insertadas");
                editor.commit();
            }
        }
        else
        {
            //Prendemos la bandera de actualización.
            hayNoticiasEnBD = true;

            //Cargamos las noticias.
            ConexionBD db = new ConexionBD(contexto);
            facebookArray = db.obtenerEstadosFacebook();
            twitterArray  = db.obtenerTweets();
        }

        GetDataInstagram instragramThread = new GetDataInstagram(contexto);
        instragramThread.execute();

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
    }//OnCreate

    public static class MyPagerAdapter extends FragmentPagerAdapter
    {

        public MyPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        //Método que retorna el fragmento donde se encuentra el layout inflado.
        @Override
        public Fragment getItem(int position)
        {
            switch(position)
            {
                case 0:
                {
                    return FragmentFacebook.newInstance(position);
                }
                case 1:
                {
                    return FragmentTwitter.newInstance(position);
                }
                case 2:
                {
                    return FragmentGaleria.newInstance(position);
                }
            }

            //En caso de no entrar en ningún caso, retornamos null.
            return null;
        }

        //Método que retorna el número de tabs que tendrá el ViewPager.
        @Override
        public int getCount()
        {
            return NUMERO_TABS;
        }

        //Método para poner le nombre a las tabs.

        @Override
        public CharSequence getPageTitle(int position)
        {
            //Cargamos la imagen en base a la posición de la tab.
            Drawable imagenTab = contexto.getResources().getDrawable(imagenesTab[position]);
            imagenTab.setBounds(0, 0, 60, 60);
            SpannableString spannableString = new SpannableString(" ");
            ImageSpan imageSpan = new ImageSpan(imagenTab, ImageSpan.ALIGN_BOTTOM);
            spannableString.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannableString;
        }
    }//MyPagerAdapter.

    //Fragment de Twitter.
    public static class FragmentTwitter extends Fragment
    {
        //Método que regresa un fragmento.
        static FragmentTwitter newInstance(int position)
        {
            FragmentTwitter fragment = new FragmentTwitter();
            Bundle args = new Bundle();
            args.putInt("position", position);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState)
        {
            //Vista que contendrá el layout a retornar.
            View layout = null;

            //Inflamos el layout.(PRUEBA, CAMBIAR EL LAYOUT).
            layout = inflater.inflate(R.layout.activity_lista_cards, container, false);

            //Obtenemos las referencias.
            final RecyclerView mRecyclerView = (RecyclerView) layout.findViewById(R.id.lista_cards);
            final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) layout
                    .findViewById(R.id.swipe_refresh);

            RecyclerView.Adapter adapter = new AdapterListaTwitter(twitterArray,contexto);
            mRecyclerView.setAdapter(adapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(contexto));
            mRecyclerView.setItemAnimator( new DefaultItemAnimator());

            //Actualizamos si es necesario.
            if(hayNoticiasEnBD && ConexionInternet.verificarConexion(contexto))
            {
                GetDataTwitter twitterThread = new GetDataTwitter(mRecyclerView, mSwipeRefreshLayout,
                        contexto);
                twitterThread.execute();
            }

            //Configuramos el swipe.
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
            {
                @Override
                public void onRefresh()
                {
                    if(ConexionInternet.verificarConexion(contexto))
                    {
                        GetDataTwitter twitterThread = new GetDataTwitter(mRecyclerView, mSwipeRefreshLayout,
                                contexto);
                        twitterThread.execute();
                    }
                    else
                    {
                        Toast.makeText(contexto, "No hay conexión a Internet.", Toast.LENGTH_SHORT)
                                .show();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }
            });

            return layout;
        }
    }//Fragment de Noticias.

    //Fragment de Facebook
    public static class FragmentFacebook extends Fragment
    {
        //Método que regresa un fragmento.
        static FragmentFacebook newInstance(int position)
        {
            FragmentFacebook fragment = new FragmentFacebook();
            Bundle args = new Bundle();
            args.putInt("position", position);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState)
        {
            //Vista que contendrá el layout a retornar.
            View layout = null;

            //Inflamos el layout.
            layout = inflater.inflate(R.layout.activity_lista_cards, container, false);

            //Obtenemos las referencias.
            final RecyclerView mRecyclerView = (RecyclerView)layout.findViewById(R.id.lista_cards);
            final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) layout
                    .findViewById(R.id.swipe_refresh);

            //Configuramos el RecyclerView.
            mRecyclerView.setLayoutManager(new LinearLayoutManager(contexto));
            mRecyclerView.setAdapter(new AdapterListaFacebook(facebookArray,contexto));
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());

            //Actualizamos, si es necesario.
            if(hayNoticiasEnBD && ConexionInternet.verificarConexion(contexto))
            {
                GetDataFacebook facebookThread = new GetDataFacebook(mRecyclerView, mSwipeRefreshLayout,
                        contexto);
                facebookThread.execute();
            }

            //Configuramos el swipe.
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
            {
                @Override
                public void onRefresh()
                {
                    if(ConexionInternet.verificarConexion(contexto))
                    {
                        GetDataFacebook facebookThread = new GetDataFacebook(mRecyclerView, mSwipeRefreshLayout,
                                contexto);
                        facebookThread.execute();
                    }
                    else
                    {
                        Toast.makeText(contexto, "No hay conexión a Internet.", Toast.LENGTH_SHORT)
                                .show();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }
            });

            return layout;
        }
    }//Fragment de Facebook

    //Fragment de Galería
    public static class FragmentGaleria extends Fragment
    {
        //Método que regresa un fragmento.
        static FragmentGaleria newInstance(int position)
        {
            FragmentGaleria fragment = new FragmentGaleria();
            Bundle args = new Bundle();
            args.putInt("position", position);
            fragment.setArguments(args);


            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState)
        {
            //Vista que contendrá el layout a retornar.
            View layout = null;

            //Inflamos el layout.(PRUEBA, CAMBIAR EL LAYOUT).
            layout = inflater.inflate(R.layout.fragment_galeria, container, false);

            RecyclerView mRecyclerView = (RecyclerView)layout.findViewById(R.id.imagenes_instagram);
            ConexionBD db = new ConexionBD(contexto);
            imagenesInstagram = db.obtenerDatosInstragram();
            //Configuramos el RecyclerView.
            mRecyclerView.setLayoutManager(new LinearLayoutManager(contexto));
            mRecyclerView.setAdapter(new AdapterImagenesInstagram(imagenesInstagram,contexto,newFragment,ft,prev,getActivity()));
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());

            return layout;
        }
    }//Fragment de Galería
}//Noticias
