package com.example.ruben.filarmonica;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class Facebook extends ActionBarActivity {

    RecyclerView mRecyclerView;
    Context contexto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook);

        contexto = getApplicationContext();

        mRecyclerView = (RecyclerView) findViewById(R.id.lista_noticias_facebook);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator( new DefaultItemAnimator());

        ConexionBD db = new ConexionBD(contexto);
        GetDataFacebook hilo = new GetDataFacebook();
        hilo.execute();
        ArrayList<ItemFacebook> publicaciones = null;
        try {
            publicaciones = hilo.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        RecyclerView.Adapter adapter = new AdapterListaFacebook(publicaciones);

        mRecyclerView.setAdapter(adapter);

    }


}
