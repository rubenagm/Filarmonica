package com.example.ruben.filarmonica;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.StringTokenizer;


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
    TextView textViewDescripcion;
    String DIRECTORIO = "/storage/emulated/0/Imagenes/imagenes";

    //Variables del Drawer.
    private ListView list_view_drawer;

    //Arreglos.
    private ArrayList<ItemDrawer> array_item_drawer;
    private TypedArray array_iconos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalle_evento);

        //Ocultamos el ActionBar.
        getSupportActionBar().hide();

        textViewTitulo = (TextView) findViewById(R.id.titulo_evento_detalle);
        imageViewImagenEvento = (ImageView) findViewById(R.id.imagen_evento);
        textViewPrograma = (TextView) findViewById(R.id.detalle_programa_evento);
        textViewFechas = (TextView) findViewById(R.id.detalle_fechas_evento);
        textViewDirector = (TextView) findViewById(R.id.detalle_nombre_director);
        textViewLocalidades = (TextView) findViewById(R.id.localidades);
        textViewCostos = (TextView) findViewById(R.id.costos);
        textViewDescripcion = (TextView) findViewById(R.id.detalle_descripcion_evento);

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
                array_item_drawer.add(new ItemDrawer(array_iconos.getResourceId(i, -1), array_iconos.getResourceId(i+1, -1)));
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
                        Toast.makeText(contexto, "Has presionado conciertos cambio", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(contexto, ListaEventos.class);
                        startActivity(i);
                        break;
                    }
                    case 1:
                    {
                        Toast.makeText(contexto, "Has presionado Noticias", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }

        });
        /******************************* ListView Drawer *****************************/

        int idEvento = getIntent().getExtras().getInt("idEvento");

        ConexionBD db = new ConexionBD(contexto);
        mEvento = db.obtenerUnEvento(""+idEvento);

        textViewTitulo.setText(mEvento.get(0).getTitulo());
        textViewPrograma.setText(mEvento.get(0).getPrograma());
        textViewDescripcion.setText(mEvento.get(0).getDescripcionEn());
        //Fechas
        ArrayList<String> fechas = mEvento.get(0).getFechas();
        String fechasString = "";
        for(int x = 0; x<fechas.size();x++){
           fechasString += LimpiarFecha(fechas.get(x))+"\n";
        }
        textViewFechas.setText(fechasString);

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
    //FUNCION PARA LIMPIAR LA FECHA
    public String LimpiarFecha(String fecha){
        String fechaFinal="";
        String año = "";
        String mes = "";
        String dia = "";
        StringTokenizer token = new StringTokenizer(fecha,"/");
        fechaFinal = token.nextToken();
        StringTokenizer token2 = new StringTokenizer(fechaFinal,"-");
        año = token2.nextToken();
        mes = token2.nextToken();
        dia = token2.nextToken();

        switch (mes){
            case "01":
                mes = "Enero";
                break;
            case "02":
                mes = "Febrero";
                break;
            case "03":
                mes = "Marzo";
                break;
            case "04":
                mes = "Abril";
                break;
            case"05":
                mes = "Mayo";
                break;
            case "06":
                mes = "Junio";
                break;
            case "07":
                mes = "Julio";
                break;
            case "08":
                mes = "Agosto";
                break;
            case "09":
                mes = "Septiembre";
                break;
            case "10":
                mes = "Octubre";
                break;
            case "11":
                mes = "Noviembre";
                break;
            case "12":
                mes = "Diciembre";
                break;
        }

        return dia + " de " + mes + " del " + año ;
    }
}
