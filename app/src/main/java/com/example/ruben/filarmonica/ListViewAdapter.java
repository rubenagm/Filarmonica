package com.example.ruben.filarmonica;

import android.app.Activity;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by natafrank on 3/5/15.
 */
public class ListViewAdapter extends BaseAdapter
{
    //Constantes.
    //Número de elementos del ListView, con la finalidad de hacerlo responsivo.
    private final static int NUMERO_ELEMENTOS_LIST_VIEW = 1;

    private Activity activity;

    public ListViewAdapter(Activity activity)
    {
        this.activity = activity;
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
        ImageView imgNoticias;
        ImageView imgStreaming;
        ImageView imgContacto;
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
            view.imgContacto   = (ImageView) convertView.findViewById(R.id.icono_contacto);
            view.imgStreaming  = (ImageView) convertView.findViewById(R.id.icono_streaming);
            view.imgNoticias   = (ImageView) convertView.findViewById(R.id.icono_noticias);
            view.linearLayout  = (LinearLayout) convertView.findViewById(R.id.linear_layout_drawer);

            //Colocamos la altura del linear layout.
            DisplayMetrics display_metrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(display_metrics);
            int height = display_metrics.heightPixels;
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

            view.imgContacto.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent i = new Intent(activity, Contacto.class);
                    activity.startActivity(i);
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
