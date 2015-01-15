package com.example.ruben.filarmonica;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Ruben on 14/01/2015.
 */
public class AdapterListaVideos extends RecyclerView.Adapter<AdapterListaVideos.ViewHolder> {
    ArrayList<ItemYoutube> videos;
    Context contexto;
    public AdapterListaVideos(Context contexto, ArrayList<ItemYoutube> videos){
        this.videos = videos;
        this.contexto = contexto;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_lista_videos,parent,false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textViewTituloVideo.setText(videos.get(position).getTitulo());
        mostrarImagen hilo = new mostrarImagen(holder.imageViewImagenPrevia,videos.get(position).getUrlImagen());
        hilo.execute();
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView textViewTituloVideo;
        TextView textViewDuracion;
        ImageView imageViewImagenPrevia;
        public ViewHolder(View v){
            super(v);
            textViewTituloVideo = (TextView) v.findViewById(R.id.titulo_video);
            textViewDuracion = (TextView) v.findViewById(R.id.duracion_video);
            imageViewImagenPrevia = (ImageView) v.findViewById(R.id.imagen_video);
        }
    }

    static class mostrarImagen extends AsyncTask<Void,Void,Void>{
        Bitmap mBitmap;
        ImageView mImageView;
        static String url2;
        public mostrarImagen(ImageView mImageView,String url2){
            this.mImageView = mImageView;
            this.url2 = url2;
        }
        @Override
        protected Void doInBackground(Void... params) {

            try {
                URL url = new URL(url2);
                HttpURLConnection connection = null;
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                mBitmap = BitmapFactory.decodeStream(input);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            mImageView.setImageBitmap(mBitmap);
        }
    }
}
