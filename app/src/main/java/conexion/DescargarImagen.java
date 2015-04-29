package conexion;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;

import mx.com.filarmonica.R;

/**
 * Created by natafrank on 3/20/15.
 */
public class DescargarImagen extends AsyncTask<String, Void, Boolean>
{
    //URLS.
    private final static String URL_EVENTO  = "http://www.ofj.com.mx/img/uploads/events/655x308/";

    //Ruta donde se guardarán todas la imagenes.
    private final static String RUTA_IMAGENES = "Imagenes";

    //Extensiones de control para las imágenes.
    private final static String DIRECTORIO_EVENTOS   = "Eventos";
    private final static String DIRECTORIO_TWITTER   = "Twitter";
    private final static String DIRECTORIO_FACEBOOK  = "Facebook";
    private final static String DIRECTORIO_INSTAGRAM = "Instagram";

    private final static int CALIDAD_DE_COMPRESION        = 10;
    private final static int TIEMPO_ESPERA_REANUDAR_HILO = 60000;

    /*
     * TIPOS
     * 1.- Eventos
     * 2.- Twitter
     * 3.- Facebook
     * 4.- Instagram
     */
    private final static int TIPO_EVENTO    = 1;
    private final static int TIPO_TWITTER   = 2;
    private final static int TIPO_FACEBOOK  = 3;
    private final static int TIPO_INSTAGRAM = 4;

    private Context contexto;
    private int tipo;
    private RelativeLayout progressBar;
    private ImageView imagen;
    private String archivo;
    private File directorio;

    public DescargarImagen(int tipo, RelativeLayout progressBar, ImageView imagen, Context contexto)
    {
        this.tipo         = tipo;
        this.progressBar  = progressBar;
        this.imagen       = imagen;
        this.contexto     = contexto;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        imagen.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected Boolean doInBackground(String... params)
    {
        String urlDescarga = params[0];//url a formar para realizar petición FTP.
        archivo = urlDescarga;//Obtenemos el archivo del parámetro.

        //Creamos la url
        switch(tipo)
        {
            case TIPO_EVENTO:
            {
                urlDescarga = URL_EVENTO + archivo + ".jpg";
                archivo = archivo + ".png";
                break;
            }
            case TIPO_TWITTER:
            {
                archivo = nombreImagen(urlDescarga) + ".png";
                break;
            }
            case TIPO_FACEBOOK:
            {
                urlDescarga = urlDescarga.substring(0, urlDescarga.lastIndexOf('.'));

                archivo = nombreImagen(urlDescarga) + ".png";

                break;
            }
            case TIPO_INSTAGRAM:
            {
                archivo = nombreImagen(urlDescarga) + ".png";
                break;
            }
            default:
            {
                //Error si caemos aquí.
                return false;
            }
        }

        //Establecemos la conexión.
        try
        {
            URL url = new URL(urlDescarga);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);

            //Obtenemos el directorio donde se guardarán las imágenes.
            ContextWrapper contextWrapper = new ContextWrapper(contexto);
            directorio = contextWrapper.getDir(RUTA_IMAGENES + getDirectorio(), Context
                .MODE_PRIVATE);

            //Guardamos la imagen.
            File archivoGuardar = new File(directorio, archivo);
            FileOutputStream outputStream = new FileOutputStream(archivoGuardar);
            bitmap.compress(Bitmap.CompressFormat.PNG, CALIDAD_DE_COMPRESION, outputStream);

            //Cerramos los flujos de datos y retornamos éxito al guardar la imagen.
            outputStream.flush();
            outputStream.close();
            input.close();

            return true;
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    protected void onPostExecute(Boolean resultado)
    {
        super.onPostExecute(resultado);
        //Mostramos la imagen.
        if(Boolean.valueOf(resultado))
        {
            File archivoCargar = new File(directorio, archivo);
            Bitmap bitmap = null;
            try
            {
                bitmap = BitmapFactory.decodeStream(new FileInputStream(archivoCargar));
                imagen.setImageBitmap(bitmap);
                progressBar.setVisibility(View.GONE);
                imagen.setVisibility(View.VISIBLE);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
                Toast.makeText(contexto, contexto.getText(R.string.error_imagen), Toast.
                        LENGTH_SHORT).show();
            }
        }
        else
        {
            //Si la imagen no se pudo descargar se puede poner una imagen por defecto
            //para el evento.
        }
    }

    //Función que regresa el directorio donde se guardará la imágen.
    public String getDirectorio()
    {
        switch(tipo)
        {
            case TIPO_EVENTO:
            {
                return DIRECTORIO_EVENTOS;
            }
            case TIPO_TWITTER:
            {
                return DIRECTORIO_TWITTER;
            }
            case TIPO_FACEBOOK:
            {
                return DIRECTORIO_FACEBOOK;
            }
            case TIPO_INSTAGRAM:
            {
                return DIRECTORIO_INSTAGRAM;
            }
            default:
            {
                //Error si caemos aquí.
                return null;
            }
        }
    }

    //Función para obtener el nombre de la imagen de Facebook, Twitter e Instagram.
    public String nombreImagen (String urlImagen)
    {
        String id="";
        StringTokenizer token = new StringTokenizer(urlImagen,"/");
        while(token.hasMoreTokens())
        {
            id = token.nextToken();
        }
        StringTokenizer token2 = new StringTokenizer(id,".");

        return token2.nextToken();
    }

    //Función para obtener el nombre de la imagen de Facebook, Twitter e Instagram.
    public static String nombreImagenUrl (String urlImagen)
    {
        String id="";
        StringTokenizer token = new StringTokenizer(urlImagen,"/");
        while(token.hasMoreTokens())
        {
            id = token.nextToken();
        }
        StringTokenizer token2 = new StringTokenizer(id,".");

        return token2.nextToken();
    }
}
