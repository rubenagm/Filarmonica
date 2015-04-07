package com.example.ruben.filarmonica;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import conexion.ConexionInternet;


public class ListaEventos extends ActionBarActivity {
    RecyclerView mRecyclerView;
    Context contexto;

    //Variables del Drawer.
    private ListView list_view_drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_cards);
        contexto = ListaEventos.this;

        //Ocultamos el action bar.
        getSupportActionBar().hide();

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
        list_view_drawer.getLayoutParams().width = width;
        int height = display_metrics.heightPixels;
        list_view_drawer.getLayoutParams().height = height;

        /******************************* ListView Drawer *****************************/

        mRecyclerView = (RecyclerView) findViewById(R.id.lista_cards);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator( new DefaultItemAnimator());
        ArrayList<ItemEvento> lista = new ArrayList<>();
        ConexionBD db = new ConexionBD(contexto);

        lista = db.obtenerEventos();
        Log.i("InformacionLista",lista.size()+"!");
        RecyclerView.Adapter adapter = new AdapterListaEventos(contexto,lista);
        mRecyclerView.setAdapter(adapter);


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
}
