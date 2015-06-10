package mx.com.filarmonica;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.webkit.WebView;
import android.widget.ListView;

import utilities.TabletManager;


public class Patronato extends Activity
{

    //Variables del Drawer.
    private ListView list_view_drawer;

    private WebView webView;
    private final static String URL_PATROCINADORES = "http://ofj.com.mx/orquesta/patrocinadores/";

    private boolean esTablet;

    private Context contexto;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patronato);

        //Obtenemos el contexto.
        contexto = Patronato.this;

        //Comprobamos si es tablet y colocamos horizontalmente la Activity de ser así.
        if(esTablet = TabletManager.esTablet(contexto))
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        webView = (WebView) findViewById(R.id.web_view);
        webView.loadUrl(URL_PATROCINADORES);
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
    }
}
