package conexion;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

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
    private final static String URL_NOTICIA = "http://ofj.com.mx/img/uploads/noticias/";

    //Ruta donde se guardarán todas la imagenes.
    private final static String RUTA_IMAGENES = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/Imagenes/";

    //Extensiones de control para las imágenes.
    private final static String EXTENSION_NOTICIAS = "Not";
    private final static String EXTENSION_FACEBOOK = "notFacebook";
    private final static String PREFIJO_IMAGENES   = "imagenes";

    private final static int CALIDAD_DE_COMPRESION = 85;

    /*
     * TIPOS
     * 1.- Evento
     * 2.- Noticias
     * 3.- Facebook
     */
    private final static int TIPO_EVENTO   = 1;
    private final static int TIPO_NOTICIA  = 2;
    private final static int TIPO_FACEBOOK = 3;

    private int tipo;
    private ProgressBar progressBar;
    private ImageView imagen;
    private String accesoImagen;
    private LinearLayout linearLayout;

    public DescargarImagen(int tipo, ProgressBar progressBar, ImageView imagen,
                           LinearLayout linearLayout)
    {
        this.tipo         = tipo;
        this.progressBar  = progressBar;
        this.imagen       = imagen;
        this.linearLayout = linearLayout;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        progressBar.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.GONE);
    }

    @Override
    protected Boolean doInBackground(String... params)
    {
        String urlDescarga = "";//url a formar para realizar petición FTP.
        String archivo = params[0];//Obtenemos el archivo del parámetro.

        //Creamos la url
        switch(tipo)
        {
            case TIPO_EVENTO:
            {
                urlDescarga = URL_EVENTO + archivo + ".jpg";
                archivo = archivo + ".png";
                break;
            }
            case TIPO_NOTICIA:
            {
                urlDescarga = URL_NOTICIA + archivo + ".jpg";
                archivo = archivo + EXTENSION_NOTICIAS + ".png";
                break;
            }
            case TIPO_FACEBOOK:
            {
                archivo = nombreImagen(urlDescarga) + EXTENSION_FACEBOOK + ".png";
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

            //Verificamos el directorio donde se guardarán als imágenes.
            File directorio = new File(RUTA_IMAGENES);
            if(!directorio.exists())
            {
                directorio.mkdirs();
            }

            //Guardamos la imagen.
            accesoImagen = PREFIJO_IMAGENES + archivo;
            File archivoGuardar = new File(directorio, accesoImagen);
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
            progressBar.setVisibility(View.GONE);
            Bitmap bitmap = BitmapFactory.decodeFile(RUTA_IMAGENES + accesoImagen);
            imagen.setImageBitmap(bitmap);
            linearLayout.setVisibility(View.VISIBLE);
        }
        else
        {
            //Si la imagen no se pudo descargar se puede poner una imagen por defecto
            //para el evento.
        }
    }

    //Función para obtener el nombre de la imagen de Facebook.
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
