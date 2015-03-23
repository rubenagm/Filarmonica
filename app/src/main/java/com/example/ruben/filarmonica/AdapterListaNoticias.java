package com.example.ruben.filarmonica;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;

import conexion.DescargarImagen;

/**
 * Created by root on 16/01/15.
 */
public class AdapterListaNoticias extends RecyclerView.Adapter<AdapterListaNoticias.ViewHolder>
{
    private final static int IMAGEN_TIPO_NOTICIA = 2;
    private final static String EXTENSION_NOTICIAS = "Not";

    ArrayList<ItemNoticia> noticias;
    String DIRECTORIO = "/storage/emulated/0/Imagenes/imagenes";

    public AdapterListaNoticias(ArrayList<ItemNoticia> noticias)
    {
        this.noticias = noticias;
    }

    //Creamos nuevas vistas.
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_item_noticia,
                viewGroup,false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    //Vaciamos las información.
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i)
    {
        viewHolder.setIsRecyclable(false);

        viewHolder.textViewTitulo.setText(noticias.get(i).getTitulo());
        viewHolder.textViewFecha.setText(LimpiarFecha(noticias.get(i).fecha));
        String contenido = noticias.get(i).getContenido();
        if(contenido.length()>100) contenido = contenido.substring(0,100) + "...";
        viewHolder.textViewContenido.setText(contenido);

        //Cargamos la imagen. Comprobamos si existe, sino la descargamos.
        String rutaAccesoImagen = DIRECTORIO + noticias.get(i).getId() + EXTENSION_NOTICIAS + ".png";
        File archivoImagen = new File(rutaAccesoImagen);
        if(!archivoImagen.exists())
        {
            DescargarImagen descargarImagen = new DescargarImagen(IMAGEN_TIPO_NOTICIA,
                    viewHolder.progressCargandoImagen, viewHolder.imageViewImagenNoticia,
                    viewHolder.linearLayoutNoticias);
            descargarImagen.execute(Integer.toString(noticias.get(i).getId()));
        }
        else
        {
            Bitmap bitmap = BitmapFactory.decodeFile(DIRECTORIO+noticias.get(i).getId()+"Not.png");
            viewHolder.imageViewImagenNoticia.setImageBitmap(bitmap);
        }
    }

    //Número de items del arreglo.
    @Override
    public int getItemCount() {
        return noticias.size();
    }

    //Clase contenedor de las vistas.
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textViewTitulo;
        TextView textViewFecha;
        TextView textViewContenido;
        ImageView imageViewImagenNoticia;
        ProgressBar progressCargandoImagen;
        LinearLayout linearLayoutNoticias;
        public ViewHolder(View v){
            super(v);
            textViewTitulo = (TextView) v.findViewById(R.id.lista_noticias_titulo);
            textViewFecha = (TextView) v.findViewById(R.id.lista_noticas_fecha);
            textViewContenido = (TextView) v.findViewById(R.id.lista_noticias_contenido);
            imageViewImagenNoticia = (ImageView) v.findViewById(R.id.lista_noticia_imagen);
            progressCargandoImagen = (ProgressBar) v.findViewById(R.id.progress_cargando_imagen);
            linearLayoutNoticias = (LinearLayout) v.findViewById(R.id.linear_layout_noticias);
        }

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

}
