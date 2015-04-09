package com.example.ruben.filarmonica;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
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
 * Created by root on 19/01/15.
 */
public class AdapterListaFacebook extends RecyclerView.Adapter<AdapterListaFacebook.ViewHolder>
{
    ArrayList<ItemFacebook> publicaciones;
    String DIRECTORIO = Environment.getExternalStorageDirectory().getAbsolutePath() +
            "/Imagenes/Facebook/";
    private final static int IMAGEN_TIPO_FACEBOOK   = 3;
    Context contexto;
    public AdapterListaFacebook(ArrayList<ItemFacebook> publicaciones,Context contexto)
    {
        this.contexto = contexto;
        this.publicaciones = publicaciones;
    }

    //Contenedor.
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView textViewContenido;
        ImageView imageViewImagenFacebook;
        RelativeLayout progressCargandoImagen;

        public ViewHolder (View v)
        {
            super(v);
            textViewContenido = (TextView) v.findViewById(R.id.contenido_publicacion);
            imageViewImagenFacebook = (ImageView) v.findViewById(R.id.imagen_publicacion);
            progressCargandoImagen = (RelativeLayout) v.findViewById(R.id.relative_progress);
        }
    }

    //Creamos la vista.
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_item_facebook,
                viewGroup, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    //Colocamos la información.
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i)
    {
        viewHolder.setIsRecyclable(false);

        viewHolder.textViewContenido.setText(publicaciones.get(i).getContenido());
        //Bitmap bitmap = BitmapFactory.decodeFile(DIRECTORIO+publicaciones.get(i).getUrlImagen());
        //viewHolder.imageViewImagenFacebook.setImageBitmap(bitmap);

        //Cargamos la imagen. Comprobamos si existe, sino la descargamos.
        String rutaAccesoImagen = DIRECTORIO + DescargarImagen.nombreImagenUrl(publicaciones.get(i).getUrlImagen()) + ".png";
        String imagen = publicaciones.get(i).getUrlImagen();
        File archivoImagen = new File(rutaAccesoImagen);
        if(!archivoImagen.exists())
        {
            DescargarImagen descargarImagen = new DescargarImagen(IMAGEN_TIPO_FACEBOOK,
                    viewHolder.progressCargandoImagen, viewHolder.imageViewImagenFacebook,contexto);
            descargarImagen.execute(imagen);
        }
        else
        {
            Bitmap bitmap = BitmapFactory.decodeFile(rutaAccesoImagen);
            viewHolder.imageViewImagenFacebook.setImageBitmap(bitmap);
        }
    }

    @Override
    public int getItemCount() {
        return publicaciones.size();
    }

}
