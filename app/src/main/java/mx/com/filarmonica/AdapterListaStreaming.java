package mx.com.filarmonica;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by macmini3cuceimobile on 2/13/15.
 */
public class AdapterListaStreaming extends RecyclerView.Adapter<AdapterListaStreaming.ViewHolder> {

    ArrayList<ItemStreamingMusica> lista;
    Context contexto;
    ServicioMusica musicService;

    public AdapterListaStreaming(Context contexto, ArrayList<ItemStreamingMusica> lista,ServicioMusica musicService){
        this.contexto = contexto;
        this.lista = lista;
        this.musicService = musicService;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_streaming,parent,false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.textViewTituloCanciones.setText(lista.get(position).getTitulo());
        holder.textViewDirectorCanciones.setText(lista.get(position).getDirector());
        holder.textViewDuracion.setText(lista.get(position).getDuracion());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicService.setSong(position);
                musicService.playSong();
            }
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView textViewTituloCanciones;
        TextView textViewDirectorCanciones;
        TextView textViewDuracion;
        RelativeLayout layout;
        public ViewHolder(View v){
            super(v);
            layout = (RelativeLayout) v.findViewById(R.id.layout_item_streaming);
            textViewTituloCanciones = (TextView)v.findViewById(R.id.reproductor_nombre_cancion);
            textViewDirectorCanciones = (TextView) v.findViewById(R.id.reproductor_director_cancion);
            textViewDuracion = (TextView) v.findViewById(R.id.reproductor_duracion_cancion);

        }
    }
}
