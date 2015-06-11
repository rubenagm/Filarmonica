package mx.com.filarmonica;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import conexion.DescargarImagen;

/**
 * Created by natafrank on 3/5/15.
 */
public class ListViewAdapter extends BaseAdapter
{
    //Constantes.
    //Número de elementos del ListView, con la finalidad de hacerlo responsivo.
    private final static int NUMERO_ELEMENTOS_LIST_VIEW = 1;

    private Activity activity;
    private boolean esTablet;

    public ListViewAdapter(Activity activity)
    {
        this.activity = activity;
        if((activity.getResources().getConfiguration().screenLayout & Configuration
                .SCREENLAYOUT_SIZE_XLARGE) == Configuration.SCREENLAYOUT_SIZE_XLARGE)
        {
            esTablet = true;
        }
        else
        {
            esTablet = false;
        }
    }

    @Override
    public int getCount()
    {
        return NUMERO_ELEMENTOS_LIST_VIEW;
    }

    @Override
    public Object getItem(int position)
    {
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        return NUMERO_ELEMENTOS_LIST_VIEW;
    }

    public static class Fila
    {
        ImageView imgConciertos;
        ImageView letrasConcierto;
        ImageView imgNoticias;
        ImageView imgStreaming;
        ImageView imgContactoPatrocinadores;
        LinearLayout linearLayout;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        //Objeto que representa el navigation drawer.
        Fila view;

        //Cargamos el inflater.
        LayoutInflater inflater = activity.getLayoutInflater();

        if(convertView == null)
        {
            view = new Fila();

            convertView = inflater.inflate(R.layout.item_drawer, parent, false);

            //Obtenemos las referencias.
            view.imgConciertos = (ImageView) convertView.findViewById(R.id.icono_conciertos);
            view.imgContactoPatrocinadores   = (ImageView) convertView.findViewById
                    (R.id.icono_contacto_patrocinadores);
            view.imgStreaming  = (ImageView) convertView.findViewById(R.id.icono_streaming);
            view.imgNoticias   = (ImageView) convertView.findViewById(R.id.icono_noticias);

            view.linearLayout  = (LinearLayout) convertView.findViewById(R.id.linear_layout_drawer);

            //Cargamos la imagen dinámica del menú.
            ContextWrapper contextWrapper = new ContextWrapper(activity);
            File directorio = contextWrapper.getDir(DescargarImagen.RUTA_IMAGENES + DescargarImagen
                    .DIRECTORIO_WEB_SERVICE, Context.MODE_PRIVATE);
            File archivoImagen = new File(directorio, "menu.jpg");
            if(archivoImagen.exists())
            {
                try
                {
                    Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(archivoImagen));
                    BitmapDrawable bitmapDrawable = new BitmapDrawable(activity.getResources(), bitmap);
                    view.imgConciertos.setBackground(bitmapDrawable);
                    view.letrasConcierto = (ImageView) convertView.findViewById(R.id.letra_conciertos);
                    view.letrasConcierto.setVisibility(View.VISIBLE);
                }
                catch (FileNotFoundException e)
                {
                    e.printStackTrace();
                }
            }

            //Colocamos la altura del linear layout.
            DisplayMetrics display_metrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(display_metrics);
            final int height = display_metrics.heightPixels;
            final int width  = display_metrics.widthPixels;

            //Trigger para ajustar el ancho en la tablet.
            if(esTablet)
            {
                view.linearLayout.getLayoutParams().width = width / 4;
            }

            view.linearLayout.getLayoutParams().height = height;

            //Colocamos los ClickListeners.
            view.imgConciertos.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent i = new Intent(activity, ListaEventos.class);
                    activity.startActivity(i);
                }
            });

            view.imgNoticias.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent i = new Intent(activity, Noticias.class);
                    activity.startActivity(i);
                }
            });

            view.imgStreaming.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent i = new Intent(activity, Streaming.class);
                    activity.startActivity(i);
                }
            });

            view.imgContactoPatrocinadores.setOnTouchListener(new View.OnTouchListener()
            {
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    Intent i;
                    if(event.getAction() == MotionEvent.ACTION_DOWN)
                    {
                        if(event.getY() <= height / 6)
                        {
                            i = new Intent(activity, Contacto.class);
                        }
                        else
                        {
                            i = new Intent(activity, Patronato.class);
                        }
                        activity.startActivity(i);
                    }

                    return true;
                }
            });

            convertView.setTag(view);
        }
        else
        {
            view = (Fila) convertView.getTag();
        }

        return convertView;
    }
}
