package com.example.ruben.filarmonica;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by root on 19/01/15.
 */
public class AdapterListaFacebook extends RecyclerView.Adapter<AdapterListaFacebook.ViewHolder>{
    ArrayList<ItemFacebook> publicaciones;
    Context contexto;
    String DIRECTORIO = "/storage/emulated/0/Imagenes/imagenes";
    public AdapterListaFacebook(Context  contexto,ArrayList<ItemFacebook> publicaciones){
        this.publicaciones = publicaciones;
        this.contexto = contexto;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_item_facebook, viewGroup, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
            viewHolder.textViewContenido.setText(publicaciones.get(i).getContenido());
            Bitmap bitmap = BitmapFactory.decodeFile(DIRECTORIO+publicaciones.get(i).getUrlImagen());
            viewHolder.imageViewImagenFacebook.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return publicaciones.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textViewContenido;
        ImageView imageViewImagenFacebook;
        public ViewHolder (View v){
            super(v);
            textViewContenido = (TextView) v.findViewById(R.id.lista_facebook_contenido);
            imageViewImagenFacebook = (ImageView) v.findViewById(R.id.lista_facebook_imagen);


        }
    }


}
