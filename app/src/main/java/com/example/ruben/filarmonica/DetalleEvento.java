package com.example.ruben.filarmonica;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class DetalleEvento extends ActionBarActivity {
    Context contexto;
    ArrayList<ItemEvento> mEvento;
    ImageView imageViewImagenEvento;
    TextView textViewTitulo ;
    TextView textViewPrograma;
    TextView textViewFechas;
    TextView textViewDirector;
    TextView textViewLocalidades;
    TextView textViewCostos;
    String DIRECTORIO = "/storage/emulated/0/Imagenes/imagenes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalle_evento);

        textViewTitulo = (TextView) findViewById(R.id.titulo_evento_detalle);
        imageViewImagenEvento = (ImageView) findViewById(R.id.imagen_evento);
        textViewPrograma = (TextView) findViewById(R.id.detalle_programa_evento);
        textViewFechas = (TextView) findViewById(R.id.detalle_fechas_evento);
        textViewDirector = (TextView) findViewById(R.id.detalle_nombre_director);
        textViewLocalidades = (TextView) findViewById(R.id.localidades);
        textViewCostos = (TextView) findViewById(R.id.costos);
        contexto = getApplicationContext();

        int idEvento = getIntent().getExtras().getInt("idEvento");

        ConexionBD db = new ConexionBD(contexto);
        mEvento = db.obtenerUnEvento(""+idEvento);

        textViewTitulo.setText(mEvento.get(0).getTitulo());
        textViewPrograma.setText(mEvento.get(0).getPrograma());
        textViewFechas.setText(mEvento.get(0).getFechas().toString());

        //Obtener localidades
        ArrayList<String> localidades = mEvento.get(0).getLocalidades();
        String localidadesString = "";
        for(int x = 0; x<localidades.size();x++){
            localidadesString += localidades.get(x)+"\n";
        }
        textViewLocalidades.setText(localidadesString);

        //Obtener costos
        ArrayList<String> costos = mEvento.get(0).getCostos();
        String costosString = "";
        for(int x = 0; x<costos.size();x++){
            costosString += costos.get(x) +"\n";
        }
        textViewCostos.setText(costosString);
        Bitmap bitmap = BitmapFactory.decodeFile(DIRECTORIO+mEvento.get(0).getId()+".png");
        imageViewImagenEvento.setImageBitmap(bitmap);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detalle_evento, menu);
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
