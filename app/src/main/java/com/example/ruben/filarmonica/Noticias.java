package com.example.ruben.filarmonica;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;


public class Noticias extends ActionBarActivity {
    RecyclerView mRecyclerView;
    TextView textViewTituloNoticias;
    Context contexto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticias);
        getSupportActionBar().hide();
        contexto = getApplicationContext();

        mRecyclerView = (RecyclerView) findViewById(R.id.lista_noticias);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator( new DefaultItemAnimator());

        ConexionBD db = new ConexionBD(contexto);

        ArrayList<ItemNoticia> noticias = db.obtenerNoticias();

        RecyclerView.Adapter adapter = new AdapterListaNoticias(contexto,noticias);

        mRecyclerView.setAdapter(adapter);

        textViewTituloNoticias = (TextView) findViewById(R.id.etiqueta_titulo_noticias);
        textViewTituloNoticias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Noticias.this,Facebook.class);
                startActivity(i);
            }
        });
    }





}
