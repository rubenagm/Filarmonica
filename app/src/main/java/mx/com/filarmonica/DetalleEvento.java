package mx.com.filarmonica;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import utilities.TabletManager;


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
    ImageView imageViewComprar;

    int idEvento;

    //Variables del Drawer.
    private ListView list_view_drawer;

    private boolean esTablet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalle_evento);
        contexto = getApplicationContext();

        //Comprobamos si es tablet y colocamos horizontalmente la Activity de ser así.
        if(esTablet = TabletManager.esTablet(contexto))
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        final Intent intent = getIntent();
        final String action = intent.getAction();

        if (Intent.ACTION_VIEW.equals(action))
        {
            String urlEvento = intent.getDataString();
            StringTokenizer tokenizerUrlEvento = new StringTokenizer(urlEvento, "?");

            //Obtenemos el id del evento.
            tokenizerUrlEvento.nextToken();//Quitamos la primera parte del tokenizer, basura.
            String auxIdEvento = tokenizerUrlEvento.nextToken();
            auxIdEvento = auxIdEvento.substring(3);
            idEvento = Integer.parseInt(auxIdEvento);
        }
        else
        {
            idEvento = getIntent().getExtras().getInt("idEvento");
        }

        //Ocultamos el ActionBar.
        getSupportActionBar().hide();

        textViewTitulo        = (TextView) findViewById(R.id.titulo_evento_detalle);
        imageViewImagenEvento = (ImageView) findViewById(R.id.imagen_evento);
        textViewPrograma      = (TextView) findViewById(R.id.detalle_programa_evento);
        textViewFechas        = (TextView) findViewById(R.id.detalle_fechas_evento);
        textViewDirector      = (TextView) findViewById(R.id.detalle_nombre_director);
        textViewLocalidades   = (TextView) findViewById(R.id.localidades);
        textViewCostos        = (TextView) findViewById(R.id.costos);
        textViewDescripcion   = (TextView) findViewById(R.id.detalle_descripcion_evento);
        imageViewComprar      = (ImageView) findViewById(R.id.comprar);

        //Click Listeners.
        imageViewComprar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse(contexto.getResources().getString(R.string.ticketmaster)));
                contexto.startActivity(intent);
            }
        });

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
            costosString += "$ "+costos.get(x) +"\n";
        }
        textViewCostos.setText(costosString);
        ContextWrapper contextWrapper = new ContextWrapper(contexto);
        File directorio = contextWrapper.getDir("Imagenes" + "Eventos", Context.MODE_PRIVATE);
        File archivoImagen = new File(directorio, mEvento.get(0).getId()+".png");
        try
        {
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(archivoImagen));
            imageViewImagenEvento.setImageBitmap(bitmap);
        }
        catch (FileNotFoundException e)
        {
            Toast.makeText(contexto, contexto.getText(R.string.error_imagen), Toast.
                    LENGTH_SHORT).show();
            e.printStackTrace();
        }
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
        String hora = "";
        String minutos = "";
        StringTokenizer token = new StringTokenizer(fecha,"/");
        fechaFinal = token.nextToken();
        hora = token.nextToken();
        minutos = token.nextToken();
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

        return dia + " de " + mes + " del " + año + "  - " + hora + ":" + minutos + " hrs.";
    }
}
