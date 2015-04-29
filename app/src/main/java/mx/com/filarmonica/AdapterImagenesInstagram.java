package mx.com.filarmonica;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import conexion.DescargarImagen;

/**
 * Created by macmini3cuceimobile on 2/27/15.
 */
public class AdapterImagenesInstagram extends RecyclerView.Adapter<AdapterImagenesInstagram.ViewHolder> {
    static ArrayList<ItemImagenInstagram> imagenes = new ArrayList<>();

    private final static int IMAGEN_TIPO_INSTAGRAM = 4;

    static Context contexto;
    static DialogoMostrarImagenInstagram newFragment ;
    static int position2 = 0;
    static Double doble;
    static FragmentTransaction ft;
    static Fragment prev;
    static FragmentActivity activity;
    public AdapterImagenesInstagram(ArrayList<ItemImagenInstagram> imagenes,Context contexto,
               DialogoMostrarImagenInstagram newFragment,FragmentTransaction ft,Fragment prev,
               FragmentActivity activity)
    {
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
        RelativeLayout progressCargandoImagen1;
        RelativeLayout progressCargandoImagen2;
        RelativeLayout progressCargandoImagen3;

        public ViewHolder(View itemView) {
            super(itemView);
            imageViewImagen1 = (ImageView) itemView.findViewById(mx.com.filarmonica.R.id.imagen_instagram_1);
            imageViewImagen2 = (ImageView) itemView.findViewById(mx.com.filarmonica.R.id.imagen_instagram_2);
            imageViewImagen3 = (ImageView) itemView.findViewById(mx.com.filarmonica.R.id.imagen_instagram_3);
            progressCargandoImagen1 = (RelativeLayout) itemView.findViewById(mx.com.filarmonica.R.id.relative_progress1);
            progressCargandoImagen2 = (RelativeLayout) itemView.findViewById(mx.com.filarmonica.R.id.relative_progress2);
            progressCargandoImagen3 = (RelativeLayout) itemView.findViewById(mx.com.filarmonica.R.id.relative_progress3);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(mx.com.filarmonica.R.layout.layout_item_fila_imagenes,
                parent, false);
        AdapterImagenesInstagram.ViewHolder holder = new AdapterImagenesInstagram.ViewHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.setIsRecyclable(false);

        //Imagen 1 del listview
        {
            //Cargamos la imagen. Comprobamos si existe, sino la descargamos.
            String imagen = imagenes.get(position).getUrlImagenNd1();
            if(imagen!= null){

                ContextWrapper contextWrapper = new ContextWrapper(contexto);
                File directorio = contextWrapper.getDir("Imagenes" + "Instagram",
                        Context.MODE_PRIVATE);
                File archivoImagen = new File(directorio, DescargarImagen.nombreImagenUrl(imagen)
                        + ".png");

                if(!archivoImagen.exists())
                {
                    DescargarImagen descargarImagen = new DescargarImagen(IMAGEN_TIPO_INSTAGRAM,
                            holder.progressCargandoImagen1, holder.imageViewImagen1,contexto);
                    descargarImagen.execute(imagen);
                }
                else
                {
                    try
                    {
                        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(archivoImagen));
                        holder.imageViewImagen1.setImageBitmap(bitmap);
                    }
                    catch (FileNotFoundException e)
                    {
                        Toast.makeText(contexto, contexto.getText(R.string.error_imagen), Toast.
                                LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }
        }
        //Imagen 2 del listview
        {
            String imagen = imagenes.get(position).getUrlImagenNd2();
            if(!imagen.equals("")){

                ContextWrapper contextWrapper = new ContextWrapper(contexto);
                File directorio = contextWrapper.getDir("Imagenes" + "Instagram",
                        Context.MODE_PRIVATE);
                File archivoImagen = new File(directorio, DescargarImagen.nombreImagenUrl(imagen)
                        + ".png");
                if(!archivoImagen.exists())
                {
                    DescargarImagen descargarImagen = new DescargarImagen(IMAGEN_TIPO_INSTAGRAM,
                            holder.progressCargandoImagen2, holder.imageViewImagen2,contexto);
                    descargarImagen.execute(imagen);
                }
                else
                {
                    try
                    {
                        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(archivoImagen));
                        holder.imageViewImagen2.setImageBitmap(bitmap);
                    }
                    catch (FileNotFoundException e)
                    {
                        Toast.makeText(contexto, contexto.getText(R.string.error_imagen), Toast.
                                LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }
        }
        //Imagen 3 del listview
        {
            //Cargamos la imagen. Comprobamos si existe, sino la descargamos.
            String imagen = imagenes.get(position).getUrlImagenNd3();
            if(!imagen.equals("")){

                ContextWrapper contextWrapper = new ContextWrapper(contexto);
                File directorio = contextWrapper.getDir("Imagenes" + "Instagram",
                        Context.MODE_PRIVATE);
                File archivoImagen = new File(directorio, DescargarImagen.nombreImagenUrl(imagen)
                        + ".png");

                if(!archivoImagen.exists())
                {
                    DescargarImagen descargarImagen = new DescargarImagen(IMAGEN_TIPO_INSTAGRAM,
                            holder.progressCargandoImagen3, holder.imageViewImagen3,contexto);
                    descargarImagen.execute(imagen);
                }
                else
                {
                    try
                    {
                        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(archivoImagen));
                        holder.imageViewImagen3.setImageBitmap(bitmap);
                    }
                    catch (FileNotFoundException e)
                    {
                        Toast.makeText(contexto, contexto.getText(R.string.error_imagen), Toast.
                                LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }
        }
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

        //Colocamos el ancho de las imágenes.
        DisplayMetrics display_metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(display_metrics);
        int width = display_metrics.widthPixels / 3;
        holder.imageViewImagen1.getLayoutParams().width = width;
        holder.imageViewImagen2.getLayoutParams().width = width;
        holder.imageViewImagen3.getLayoutParams().width = width;
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








