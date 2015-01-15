package com.example.ruben.filarmonica;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

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
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView textViewTituloVideo;
        TextView textViewDuracion;
        public ViewHolder(View v){
            super(v);
            textViewTituloVideo = (TextView) v.findViewById(R.id.titulo_video);
            textViewDuracion = (TextView) v.findViewById(R.id.duracion_video);
        }
    }
    public void guardarVideos (ArrayList<ItemYoutube> videos){
        this.videos = videos;
    }
}
