package com.example.ruben.filarmonica;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.StringTokenizer;


public class ListaEventos extends ActionBarActivity {
    RecyclerView mRecyclerView;
    Context contexto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_eventos);
        contexto = getApplicationContext();
        mRecyclerView = (RecyclerView) findViewById(R.id.lista_eventos);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator( new DefaultItemAnimator());
        ArrayList<ItemEvento> lista = new ArrayList<ItemEvento>();
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
