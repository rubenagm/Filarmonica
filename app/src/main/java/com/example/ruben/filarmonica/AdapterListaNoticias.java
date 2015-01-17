package com.example.ruben.filarmonica;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by root on 16/01/15.
 */
public class AdapterListaNoticias extends RecyclerView.Adapter<AdapterListaNoticias.ViewHolder>{
    ArrayList<ItemNoticia> noticias;
    Context contexto;
    String DIRECTORIO = "/storage/emulated/0/Imagenes/imagenes";

    public AdapterListaNoticias(Context contexto, ArrayList<ItemNoticia> noticias){
        this.noticias = noticias;
        this.contexto = contexto;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_item_noticia,viewGroup,false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.textViewTitulo.setText(noticias.get(i).getTitulo());
        viewHolder.textViewFecha.setText(noticias.get(i).getFecha());
        viewHolder.textViewContenido.setText(noticias.get(i).getContenido());
        Bitmap bitmap = BitmapFactory.decodeFile(DIRECTORIO+noticias.get(i).getId()+"Not.png");
        viewHolder.imageViewImagenNoticia.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return noticias.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textViewTitulo;
        TextView textViewFecha;
        TextView textViewContenido;
        ImageView imageViewImagenNoticia;
        public ViewHolder(View v){
            super(v);
            textViewTitulo = (TextView) v.findViewById(R.id.lista_noticias_titulo);
            textViewFecha = (TextView) v.findViewById(R.id.lista_noticas_fecha);
            textViewContenido = (TextView) v.findViewById(R.id.lista_noticias_contenido);
            imageViewImagenNoticia = (ImageView) v.findViewById(R.id.lista_noticia_imagen);
        }

    }

}
