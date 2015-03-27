package com.example.ruben.filarmonica;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by root on 19/01/15.
 */
public class AdapterListaFacebook extends RecyclerView.Adapter<AdapterListaFacebook.ViewHolder>
{
    ArrayList<ItemFacebook> publicaciones;
    String DIRECTORIO = "/storage/emulated/0/Imagenes/imagenes";

    public AdapterListaFacebook(ArrayList<ItemFacebook> publicaciones)
    {
        this.publicaciones = publicaciones;
    }

    //Contenedor.
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView textViewContenido;
        ImageView imageViewImagenFacebook;

        public ViewHolder (View v)
        {
            super(v);
            textViewContenido = (TextView) v.findViewById(R.id.contenido_publicacion);
            imageViewImagenFacebook = (ImageView) v.findViewById(R.id.imagen_publicacion);
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
            viewHolder.textViewContenido.setText(publicaciones.get(i).getContenido());
            //Bitmap bitmap = BitmapFactory.decodeFile(DIRECTORIO+publicaciones.get(i).getUrlImagen());
            //viewHolder.imageViewImagenFacebook.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return publicaciones.size();
    }

}
