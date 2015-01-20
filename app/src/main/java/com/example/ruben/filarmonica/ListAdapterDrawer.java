package com.example.ruben.filarmonica;

import android.app.Activity;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ListAdapterDrawer extends BaseAdapter
{
	
	Activity activity;
	ArrayList<ItemDrawer> array_drawer;
	
	public ListAdapterDrawer(Activity activity, ArrayList<ItemDrawer> array_drawer) 
	{
		this.activity     = activity;
		this.array_drawer = array_drawer;
	}
	
	@Override
	public int getCount() 
	{
		return array_drawer.size();
	}

	@Override
	public Object getItem(int position)
	{
		return array_drawer.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	//M�todos para poder implementar varios layouts en el mismo
	//ListView.
	@Override
	public int getItemViewType(int position)
	{
		if(position == 2)
		{
			return 1;
		}
		else
		{
			return 0;
		}
	}
	
	//Cantidad de layouts a implementar.
	@Override
	public int getViewTypeCount() 
	{
		return 2;
	}
	
	//Clase que representa una l�nea del layout.
	public static class Fila
	{
		TextView icono;
		TextView icono2;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		Fila view;
		
		//Variable que guarda el tipo de layout que vamos a implementar.
		int tipo = getItemViewType(position);
		
		//Cargamos el inflater.
		LayoutInflater inflater = activity.getLayoutInflater();
		
		if(convertView == null)
		{
			view = new Fila();
			
			//Tomamos un item.
			ItemDrawer item = array_drawer.get(position);

            //Creamos el objeto para manejar las medidas de la pantalla.
            DisplayMetrics display_metrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(display_metrics);

            //Sacamos el ancho de la pantalla para acomodar los items que van de 2
            int width = display_metrics.widthPixels / 2;

			//Implementaci�n layout simple.
			if(tipo == 0)
			{
				//Inflamos el layout.
				convertView = inflater.inflate(R.layout.item_drawer, null);
				
				//Obtenemos las referencias.
				view.icono = (TextView) convertView.findViewById(R.id.icono);
				
				//Colocamos la imagen.
				view.icono.setBackgroundResource(item.getIcono());
			}
			//Implementaci�n del layout doble.
			else
			{
				
				//Inflamos el layout.
				convertView = inflater.inflate(R.layout.item_drawer_doble, null);
				
				//Obtenemos las referencias y configuramos.
				//�cono 1.
				view.icono = (TextView) convertView.findViewById(R.id.icono);

                //Establecemos ancho y alto de la imagen.
                view.icono.getLayoutParams().width = width;

				view.icono.setBackgroundResource(item.getIcono());
				
				//Colocamos el click listener.
				view.icono.setOnClickListener(new View.OnClickListener()
				{

					@Override
					public void onClick(View v) 
					{
						switch(v.getId())
						{
							case R.id.icono:
							{
                                Intent i = new Intent(activity,Streaming.class);
                                activity.startActivity(i);
								break;
							}
						}
					}
					
				});
				
				//�cono 2.
				view.icono2 = (TextView) convertView.findViewById(R.id.icono2);

                //Establecemos ancho de la imagen.
				view.icono2.getLayoutParams().width = width;

				view.icono2.setBackgroundResource(item.getIcono2());
				
				view.icono2.setOnClickListener(new View.OnClickListener()
				{

					@Override
					public void onClick(View v)
					{
						switch(v.getId())
						{
							case R.id.icono2:
							{
								Toast.makeText(activity, "Has presionado Contacto", Toast.LENGTH_SHORT).show();
								break;
							}
						}
					}
					
				});
			}
			
			convertView.setTag(view);
		}
		else
		{
			view = (Fila) convertView.getTag();
		}
		
		return convertView;
	}//getView	
}//ListAdapterDrawer
