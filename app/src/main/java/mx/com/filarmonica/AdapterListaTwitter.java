package mx.com.filarmonica;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import conexion.DescargarImagen;

/**
 * Created by root on 16/01/15.
 */
public class AdapterListaTwitter extends RecyclerView.Adapter<AdapterListaTwitter.ViewHolder>
{
    private final static int IMAGEN_TIPO_TWITTER = 2;

    ArrayList<ItemTwitter> tweets;
    String DIRECTORIO = Environment.getExternalStorageDirectory().getAbsolutePath() +
            "/Imagenes/Twitter/";
    final String COLOR_INICIO = "<font color='#AD731C'>";
    final String COLOR_FIN = "</font>";
    Context contexto;
    //variable que muestra el contenido del twitter con colores en caso de haber
    public AdapterListaTwitter(ArrayList<ItemTwitter> tweets,Context contexto)
    {
        this.contexto = contexto;
        this.tweets = tweets;
    }


    //Creamos nuevas vistas.
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_item_twitter,
                viewGroup,false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    //Vaciamos las información.
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i)
    {
        //Quitamos el reciclado.
        viewHolder.setIsRecyclable(false);

        //Obtenemos el tweet.
        ItemTwitter tweet = tweets.get(i);

        String contenido = tweet.getText();
        //viewHolder.textViewContenido.setText(contenido);

        String imagen = tweet.getUrlImagen();
        if(imagen.equals("")){
            viewHolder.imageViewImagenTwitter.setVisibility(View.GONE);
        }
        else
        {
            //Cargamos la imagen. Comprobamos si existe, sino la descargamos.
            String rutaAccesoImagen = DIRECTORIO + DescargarImagen.nombreImagenUrl(imagen) + ".png";
            File archivoImagen = new File(rutaAccesoImagen);
            if(!archivoImagen.exists())
            {
                DescargarImagen descargarImagen = new DescargarImagen(IMAGEN_TIPO_TWITTER,
                        viewHolder.progressCargandoImagen, viewHolder.imageViewImagenTwitter,contexto);
                descargarImagen.execute(imagen);
            }
            else
            {
                Bitmap bitmap = BitmapFactory.decodeFile(rutaAccesoImagen);
                viewHolder.imageViewImagenTwitter.setImageBitmap(bitmap);
            }
        }
        //Pintar links
        for(int x = 0; x<tweets.get(i).getLinks().size();x++){
            contenido = pintarPalabras(contenido,tweets.get(i).getLinks().get(x));
        }
        //Pintar hashtags
        for(int x = 0; x<tweets.get(i).getHashtags().size();x++){
            contenido = pintarPalabras(contenido,tweets.get(i).getHashtags().get(x));
        }
        //Pintar users
        for(int x = 0; x<tweets.get(i).getUsers().size();x++){
            contenido = pintarPalabras(contenido,tweets.get(i).getUsers().get(x));
        }

        viewHolder.textViewContenido.setText(Html.fromHtml(contenido));
        viewHolder.fecha.setText(tweet.getFecha());

        //Asignamos la URL de Twitter.
       asignarUrlTwitter(viewHolder.textViewVerEnTwitter, tweet.getUrlTwitter());
    }

    //Número de items del arreglo.
    @Override
    public int getItemCount()
    {
       return tweets.size();
    }

    //Clase contenedor de las vistas.
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textViewContenido;
        ImageView imageViewImagenTwitter;
        TextView textViewVerEnTwitter;
        TextView fecha;
        RelativeLayout progressCargandoImagen;
        public ViewHolder(View v){
            super(v);
            textViewVerEnTwitter = (TextView) v.findViewById(R.id.ver_en_twitter);
            textViewContenido = (TextView) v.findViewById(R.id.lista_twitter_contenido);
            imageViewImagenTwitter = (ImageView) v.findViewById(R.id.lista_twitter_imagen_tweet);
            progressCargandoImagen = (RelativeLayout) v.findViewById(R.id.relative_progress);
            fecha = (TextView) v.findViewById(R.id.lista_twitter_tiempo);
        }

    }

    //Funciòn que pinta los hashtags

    public String pintarPalabras(String original,String palabraPintar){


        //Se obtiene la posicion donde comienza la palabra a pintar
        int inicio = original.indexOf(palabraPintar);
        String resultado = original;
        //Se obtiene lo que hay antes de la palabra que se quiere pintar
        if(inicio > 0){
            resultado = original.substring(0,inicio);

            //Se agrega lo necesario para que la palabra se pinte
            resultado+= COLOR_INICIO + palabraPintar + COLOR_FIN;

            resultado += original.substring(inicio+palabraPintar.length());

        }
        return resultado;

    }

    //Método para poner la url de twitter en la etiqueta de "ver en twitter".
    public void asignarUrlTwitter(TextView textViewVerEnTwitter, final String url)
    {
        textViewVerEnTwitter.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse(url));
                contexto.startActivity(intent);
            }
        });
    }

}
