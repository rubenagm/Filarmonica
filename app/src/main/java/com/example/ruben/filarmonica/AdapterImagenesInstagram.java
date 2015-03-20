package com.example.ruben.filarmonica;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by macmini3cuceimobile on 2/27/15.
 */
public class AdapterImagenesInstagram extends RecyclerView.Adapter<AdapterImagenesInstagram.ViewHolder> {
    static ArrayList<ItemImagenInstagram> imagenes = new ArrayList<>();
    int POSITION;
    String DIRECTORIO = "/storage/emulated/0/Imagenes/imagenes";
    static Context contexto;
    static DialogoMostrarImagenInstagram newFragment ;
    static int position2 = 0;
    static Double doble;
    static FragmentTransaction ft;
    static Fragment prev;
    static FragmentActivity activity;
    public AdapterImagenesInstagram(ArrayList<ItemImagenInstagram> imagenes,Context contexto,DialogoMostrarImagenInstagram newFragment,FragmentTransaction ft,Fragment prev,FragmentActivity activity) {
        this.imagenes = imagenes;
        this.contexto = contexto;
        this.newFragment = newFragment;
        this.ft = ft;
        this.prev = prev;
        this.activity = activity;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewImagen1;
        ImageView imageViewImagen2;
        ImageView imageViewImagen3;

        public ViewHolder(View itemView) {
            super(itemView);
            imageViewImagen1 = (ImageView) itemView.findViewById(R.id.imagen_instagram_1);
            imageViewImagen2 = (ImageView) itemView.findViewById(R.id.imagen_instagram_2);
            imageViewImagen3 = (ImageView) itemView.findViewById(R.id.imagen_instagram_3);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_fila_imagenes,
                parent, false);
        AdapterImagenesInstagram.ViewHolder holder = new AdapterImagenesInstagram.ViewHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Bitmap bit1 = BitmapFactory.decodeFile(DIRECTORIO+"Instagram_"+imagenes.get(position).getUrlImagenNd1()+".png");
        Bitmap bit2 = BitmapFactory.decodeFile(DIRECTORIO+"Instagram_"+imagenes.get(position).getUrlImagenNd2()+".png");
        Bitmap bit3 = BitmapFactory.decodeFile(DIRECTORIO+"Instagram_"+imagenes.get(position).getUrlImagenNd3()+".png");
        holder.imageViewImagen1.setImageBitmap(bit1);
        holder.imageViewImagen2.setImageBitmap(bit2);
        holder.imageViewImagen3.setImageBitmap(bit3);

        holder.imageViewImagen1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ft = activity.getSupportFragmentManager().beginTransaction();

                prev = activity.getSupportFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                newFragment = DialogoMostrarImagenInstagram.newInstance(position*3,getArrayImagenes(imagenes),getArrayTexto(imagenes),getArrayLinks(imagenes));
                newFragment.show(ft,"dialog");

            }
        });
        holder.imageViewImagen2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ft = activity.getSupportFragmentManager().beginTransaction();
                prev = activity.getSupportFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                newFragment = DialogoMostrarImagenInstagram.newInstance(position*3+1,getArrayImagenes(imagenes),getArrayTexto(imagenes),getArrayLinks(imagenes));
                newFragment.show(ft,"dialog");
            }
        });
        holder.imageViewImagen3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ft = activity.getSupportFragmentManager().beginTransaction();
                prev = activity.getSupportFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                newFragment = DialogoMostrarImagenInstagram.newInstance(position*3+2,getArrayImagenes(imagenes),getArrayTexto(imagenes),getArrayLinks(imagenes));
                newFragment.show(ft,"dialog");

            }
        });
    }


    @Override
    public int getItemCount() {
        return imagenes.size();
    }



    public ArrayList<String> getArrayImagenes(ArrayList<ItemImagenInstagram> imagenes){
        ArrayList<String> string = new ArrayList<>();
        for(int x = 0;x<imagenes.size();x++){
            string.add(imagenes.get(x).getUrlImagenHd1());
            string.add(imagenes.get(x).getUrlImagenHd2());
            string.add(imagenes.get(x).getUrlImagenHd3());
        }
        return string;
    }
    public ArrayList<String> getArrayTexto(ArrayList<ItemImagenInstagram> imagenes){
        ArrayList<String> string = new ArrayList<>();
        for(int x = 0;x<imagenes.size();x++){
            string.add(imagenes.get(x).getTextoImagen1());
            string.add(imagenes.get(x).getTextoImagen2());
            string.add(imagenes.get(x).getTextoImagen3());
        }
        return string;
    }
    public ArrayList<String> getArrayLinks(ArrayList<ItemImagenInstagram> imagenes){
        ArrayList<String> string = new ArrayList<>();
        for(int x = 0;x<imagenes.size();x++){
            string.add(imagenes.get(x).getLink1());
            string.add(imagenes.get(x).getLink2());
            string.add(imagenes.get(x).getLink3());
        }
        return string;
    }


}








