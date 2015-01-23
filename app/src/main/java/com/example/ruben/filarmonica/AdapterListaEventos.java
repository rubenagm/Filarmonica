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
import java.util.StringTokenizer;

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
        public TextView textViewCompartir;
        public ViewHolder(View v){
            super(v);
            textViewCompartir = (TextView) v.findViewById(R.id.texto_compartir);
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

        //fechas
        ArrayList<String> fechas = mEvento.get(position).getFechas();
        String fechasString ="";
        for(int x = 0;x<fechas.size();x++){
            fechasString += LimpiarFecha(fechas.get(x)) + "\n";
        }

        holder.textViewFechasEvento.setText(fechasString);

        Bitmap bitmap = BitmapFactory.decodeFile(DIRECTORIO+mEvento.get(position).getId()+".png");
        holder.imageViewImagenEvento.setImageBitmap(bitmap);
        holder.textViewMasDetalles.setText("MÁS DETALLES"+"\n");
        holder.textViewCompartir.setText("COMPARTIR" + "\n");
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


    //FUNCION PARA LIMPIAR LA FECHA
    public String LimpiarFecha(String fecha){
        String fechaFinal="";
        String año = "";
        String mes = "";
        String dia = "";
        StringTokenizer token = new StringTokenizer(fecha,"/");
        fechaFinal = token.nextToken();
        StringTokenizer token2 = new StringTokenizer(fechaFinal,"-");
        año = token2.nextToken();
        mes = token2.nextToken();
        dia = token2.nextToken();

        switch (mes){
            case "01":
                mes = "Enero";
                break;
            case "02":
                mes = "Febrero";
                break;
            case "03":
                mes = "Marzo";
                break;
            case "04":
                mes = "Abril";
                break;
            case"05":
                mes = "Mayo";
                break;
            case "06":
                mes = "Junio";
                break;
            case "07":
                mes = "Julio";
                break;
            case "08":
                mes = "Agosto";
                break;
            case "09":
                mes = "Septiembre";
                break;
            case "10":
                mes = "Octubre";
                break;
            case "11":
                mes = "Noviembre";
                break;
            case "12":
                mes = "Diciembre";
                break;
        }

        return dia + " de " + mes + " del " + año ;
    }

    ////
    public String obtenerFechas(ArrayList<String> fechas){
        String fechaReturn ="";
        for(int x = 0; x<fechas.size();x++){
            fechaReturn += fechas.get(x) + "\n";
        }
        return fechaReturn;
    }
}