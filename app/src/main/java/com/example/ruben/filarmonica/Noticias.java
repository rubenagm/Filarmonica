package com.example.ruben.filarmonica;

import android.app.Activity;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import conexion.ConexionInternet;
import tabs.SlidingTabLayout;


public class Noticias extends ActionBarActivity
{
    static DialogoMostrarImagenInstagram newFragment ;
    static FragmentTransaction ft;
    static Fragment prev;
    /// Dialogo

    ////
    private static Context contexto;
    private final static int NUMERO_TABS = 3;

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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        //Dialogo

        ft = getSupportFragmentManager().beginTransaction();
        prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);
        getSupportActionBar().hide();

        //Obtenemos el contexto.
        contexto = getApplicationContext();

        //Verificamos la conexión a internet.
        if(!ConexionInternet.verificarConexion(contexto))
        {
            Toast.makeText(contexto, contexto.getResources().getString(R.string
                    .conexion_fallida), Toast.LENGTH_SHORT).show();
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


        /******** Parte de twitter ******/
        GetDataTwitter twitterThread = new GetDataTwitter();
        twitterThread.execute();

        try {
            twitterArray = twitterThread.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        /////////****////////////////

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
        }
        catch (ExecutionException e)
        {
            e.printStackTrace();
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
        list_view_drawer.getLayoutParams().width = width;
        int height = display_metrics.heightPixels;
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
                    return FragmentNoticias.newInstance(position);
                }
                case 1:
                {
                    return FragmentFacebook.newInstance(position);
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
            switch(position)
            {
                case 0:
                {
                    return "NOTICIAS";
                }
                case 1:
                {
                    return "FACEBOOK";
                }
                case 2:
                {
                    return "GALERÍA";
                }
            }

            return null;
        }
    }//MyPagerAdapter.

    //Fragment de Noticias.
    public static class FragmentNoticias extends Fragment
    {
        //Método que regresa un fragmento.
        static FragmentNoticias newInstance(int position)
        {
            FragmentNoticias fragment = new FragmentNoticias();
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
            RecyclerView mRecyclerView = (RecyclerView) layout.findViewById(R.id.lista_cards);

            //Configuramos el recycler view.
            RecyclerView.Adapter adapter = new AdapterListaTwitter(contexto, twitterArray);
            mRecyclerView.setAdapter(adapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(contexto));
            mRecyclerView.setItemAnimator( new DefaultItemAnimator());
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

            //Inflamos el layout.(PRUEBA, CAMBIAR EL LAYOUT).
            layout = inflater.inflate(R.layout.activity_lista_cards, container, false);

            //Obtenemos las referencias.
            RecyclerView mRecyclerView = (RecyclerView)layout.findViewById(R.id.lista_cards);

            //Configuramos el RecyclerView.
            mRecyclerView.setLayoutManager(new LinearLayoutManager(contexto));
            mRecyclerView.setAdapter(new AdapterListaFacebook(facebookArray));
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());

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
