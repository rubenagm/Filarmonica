package com.example.ruben.filarmonica;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;


public class Contacto extends Activity
{

    //Contexto.
    private Context contexto;

    //Variables del Drawer.
    private ListView list_view_drawer;

    //Arreglos.
    private ArrayList<ItemDrawer> array_item_drawer;
    private TypedArray array_iconos;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacto);

        //Obtenemos el contexto.
        contexto = getApplicationContext();

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
                array_item_drawer.add(new ItemDrawer(array_iconos.getResourceId(i, -1),
                        array_iconos.getResourceId(i+1, -1)));
            }
            else
            {
                array_item_drawer.add(new ItemDrawer(array_iconos.getResourceId(i, -1)));
            }
        }

        //Colocamos el adaptador al ListView.
        list_view_drawer.setAdapter(new ListAdapterDrawer(this, array_item_drawer));

        //Colocamos el click listener al ListView.
        list_view_drawer.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                switch(position)
                {
                    case 0:
                    {
                        Intent i = new Intent(contexto, ListaEventos.class);
                        startActivity(i);
                        break;
                    }
                    case 1:
                    {
                        Intent i = new Intent(contexto, Noticias.class);
                        startActivity(i);
                        break;
                    }
                }
            }

        });
        /******************************* ListView Drawer *****************************/
    }
}
