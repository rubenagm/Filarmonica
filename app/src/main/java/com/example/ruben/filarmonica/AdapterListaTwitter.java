package com.example.ruben.filarmonica;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    private final static int IMAGEN_TIPO_TWITTER   = 2;

    ArrayList<ItemTwitter> tweets;
    String DIRECTORIO = Environment.getExternalStorageDirectory().getAbsolutePath() +
            "/Imagenes/Twitter/";
    final String COLOR_INICIO = "<font color='#AD731C'>";
    final String COLOR_FIN = "</font>";

    //variable que muestra el contenido del twitter con colores en caso de haber
    String contenido = "";
    public AdapterListaTwitter(ArrayList<ItemTwitter> tweets)
    {
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
        viewHolder.setIsRecyclable(false);


        String contenido = tweets.get(i).getText();
        //viewHolder.textViewContenido.setText(contenido);

        String imagen = tweets.get(i).getUrlImagen();
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
                        viewHolder.progressCargandoImagen, viewHolder.imageViewImagenTwitter);
                descargarImagen.execute(imagen);
            }
            else
            {
                Bitmap bitmap = BitmapFactory.decodeFile(rutaAccesoImagen);
                viewHolder.imageViewImagenTwitter.setImageBitmap(bitmap);
            }
        }

        contenido = tweets.get(i).getText();
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
    }

    //Número de items del arreglo.
    @Override
    public int getItemCount() {
        return tweets.size();
    }

    //Clase contenedor de las vistas.
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textViewContenido;
        ImageView imageViewImagenTwitter;
        TextView textViewVerEnTwitter;
        RelativeLayout progressCargandoImagen;
        public ViewHolder(View v){
            super(v);
            textViewVerEnTwitter = (TextView) v.findViewById(R.id.lista_twitter_ver_en_twitter);
            textViewContenido = (TextView) v.findViewById(R.id.lista_twitter_contenido);
            imageViewImagenTwitter = (ImageView) v.findViewById(R.id.lista_twitter_imagen_tweet);
            progressCargandoImagen = (RelativeLayout) v.findViewById(R.id.relative_progress);
            //linearLayoutTwitter = (LinearLayout) v.findViewById(R.id.linear_layout_twitter);
        }

    }

    //Funciòn que pinta los hashtags

    public String pintarPalabras(String original,String palabraPintar){


        //Se obtiene la posicion donde comienza la palabra a pintar
        int inicio = original.indexOf(palabraPintar);

        //Se obtiene lo que hay antes de la palabra que se quiere pintar
        String resultado = original.substring(0,inicio);

        //Se agrega lo necesario para que la palabra se pinte
        resultado+= COLOR_INICIO + palabraPintar + COLOR_FIN;

        resultado += original.substring(inicio+palabraPintar.length());

        return resultado;

    }

}
