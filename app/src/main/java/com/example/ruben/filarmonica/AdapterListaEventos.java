package com.example.ruben.filarmonica;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Ruben on 08/01/2015.
 */
public class AdapterListaEventos extends RecyclerView.Adapter<AdapterListaEventos.ViewHolder> {
    //Clase View Holder
    Context contexto;
    ArrayList<ItemEvento> mEvento;
    String DIRECTORIO = "/storage/emulated/0/Imagenes/imagenes";
    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView mTextView;
        public TextView textViewProgramaEvento;
        public TextView textViewFechasEvento;
        public TextView textViewMasDetalles;
        public ImageView imageViewImagenEvento;
        public ViewHolder(View v){
            super(v);
            textViewMasDetalles = (TextView) v.findViewById(R.id.text_mas_detalles);
            mTextView = (TextView) v.findViewById(R.id.titulo_evento);
            textViewProgramaEvento = (TextView) v.findViewById(R.id.programa_evento);
            textViewFechasEvento = (TextView) v.findViewById(R.id.fechas_evento);
            imageViewImagenEvento = (ImageView) v.findViewById(R.id.imagen_evento);

        }
    }
    //Termina clase ViewHolder
    public AdapterListaEventos(Context contexto,ArrayList<ItemEvento> mEvento){
        this.contexto = contexto;
        this.mEvento = mEvento;
    }

    @Override
    public AdapterListaEventos.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_evento,parent,false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    public void onBindViewHolder(ViewHolder holder, final int position){
        holder.mTextView.setText(mEvento.get(position).getTitulo().toString());
        holder.textViewProgramaEvento.setText(mEvento.get(position).getPrograma().toString());
        holder.textViewFechasEvento.setText(obtenerFechas(mEvento.get(position).getFechas()));

        Bitmap bitmap = BitmapFactory.decodeFile(DIRECTORIO+mEvento.get(position).getId()+".png");
        holder.imageViewImagenEvento.setImageBitmap(bitmap);
        holder.textViewMasDetalles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context contextoView = v.getContext();
                Intent i = new Intent(contextoView, DetalleEvento.class);
                i.putExtra("idEvento", mEvento.get(position).getId());
                contextoView.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mEvento.size();
    }

    public String obtenerFechas(ArrayList<String> fechas){
        String fechaReturn ="";
        for(int x = 0; x<fechas.size();x++){
            fechaReturn += fechas.get(x) + "\n";
        }
        return fechaReturn;
    }
}