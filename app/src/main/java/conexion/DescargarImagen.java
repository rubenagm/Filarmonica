package conexion;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;

/**
 * Created by natafrank on 3/20/15.
 */
public class DescargarImagen extends AsyncTask<String, Void, Boolean>
{
    //URLS.
    private final static String URL_EVENTO  = "http://www.ofj.com.mx/img/uploads/events/655x308/";

    //Ruta donde se guardarán todas la imagenes.
    private final static String RUTA_IMAGENES = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/Imagenes/";

    //Extensiones de control para las imágenes.
    private final static String DIRECTORIO_EVENTOS   = "Eventos/";
    private final static String DIRECTORIO_TWITTER   = "Twitter/";
    private final static String DIRECTORIO_FACEBOOK  = "Facebook/";
    private final static String DIRECTORIO_INSTAGRAM = "Instagram/";

    private final static int CALIDAD_DE_COMPRESION = 10;

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

    private int tipo;
    private RelativeLayout progressBar;
    private ImageView imagen;
    private String archivo;

    public DescargarImagen(int tipo, RelativeLayout progressBar, ImageView imagen)
    {
        this.tipo         = tipo;
        this.progressBar  = progressBar;
        this.imagen       = imagen;
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
        String urlDescarga = "";//url a formar para realizar petición FTP.
        archivo = params[0];//Obtenemos el archivo del parámetro.

        //Creamos la url
        switch(tipo)
        {
            case TIPO_EVENTO:
            {
                urlDescarga = URL_EVENTO + archivo + ".jpg";
                archivo = DIRECTORIO_EVENTOS + archivo + ".png";
                break;
            }
            case TIPO_TWITTER:
            {
                archivo = DIRECTORIO_TWITTER + nombreImagen(urlDescarga) + ".png";
                break;
            }
            case TIPO_FACEBOOK:
            {
                archivo = DIRECTORIO_FACEBOOK + nombreImagen(urlDescarga) + ".png";
                break;
            }
            case TIPO_INSTAGRAM:
            {
                archivo = DIRECTORIO_INSTAGRAM + nombreImagen(urlDescarga) + ".png";
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

            //Verificamos el directorio donde se guardarán las imágenes.
            File directorio = new File(RUTA_IMAGENES + getDirectorio());
            if(!directorio.exists())
            {
                directorio.mkdirs();
            }

            //Guardamos la imagen.
            File archivoGuardar = new File(RUTA_IMAGENES + archivo);
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
            Bitmap bitmap = BitmapFactory.decodeFile(RUTA_IMAGENES + archivo);
            imagen.setImageBitmap(bitmap);
            progressBar.setVisibility(View.GONE);
            imagen.setVisibility(View.VISIBLE);
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
}
