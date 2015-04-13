package mx.com.filarmonica;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;

import conexion.DescargarImagen;

/**
 * Created by Ruben on 08/01/2015.
 */
public class AdapterListaEventos extends RecyclerView.Adapter<AdapterListaEventos.ViewHolder>
{

    private final static int IMAGEN_TIPO_EVENTO = 1;

    //Clase View Holder
    Context contexto;
    ArrayList<ItemEvento> mEvento;
    String DIRECTORIO = Environment.getExternalStorageDirectory().getAbsolutePath() +
            "/Imagenes/Eventos/";

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView mTextView;
        public TextView textViewProgramaEvento;
        public TextView textViewFechasEvento;
        public TextView textViewMasDetalles;
        public ImageView imageViewImagenEvento;
        public TextView textViewCompartir;
        public RelativeLayout progressCargandoImagen;
        public TextView textEventoCulminado;
        public ImageView imageViewComprar;
        public ViewHolder(View v){
            super(v);
            textViewCompartir      = (TextView) v.findViewById(R.id.texto_compartir);
            textViewMasDetalles    = (TextView) v.findViewById(R.id.text_mas_detalles);
            mTextView              = (TextView) v.findViewById(R.id.titulo_evento);
            textViewProgramaEvento = (TextView) v.findViewById(R.id.programa_evento);
            textViewFechasEvento   = (TextView) v.findViewById(R.id.fechas_evento);
            imageViewImagenEvento  = (ImageView) v.findViewById(R.id.imagen_evento);
            progressCargandoImagen = (RelativeLayout) v.findViewById(R.id.relative_progress);
            textEventoCulminado    = (TextView) v.findViewById(R.id.text_evento_culminado);
            imageViewComprar       = (ImageView) v.findViewById(R.id.comprar);
        }
    }
    //Termina clase ViewHolder
    public AdapterListaEventos(Context contexto,ArrayList<ItemEvento> mEvento)
    {
        this.contexto = contexto;
        this.mEvento = mEvento;

        //Ordenamos los eventos por fecha.
        Collections.sort(mEvento);

        //Marcamos los eventos culminados.
        marcarEventosCulminados();
    }

    @Override
    public AdapterListaEventos.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_evento,
                parent,false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    public void onBindViewHolder(ViewHolder holder, final int position)
    {
        //Obtenemos el evento.
        ItemEvento evento = mEvento.get(position);

        //Cancelamos el reciclaje del RecyclerView.
        holder.setIsRecyclable(false);

        holder.mTextView.setText(evento.getTitulo().toString());
        holder.textViewProgramaEvento.setText(evento.getPrograma().toString());

        //fechas
        ArrayList<String> fechas = evento.getFechas();
        String fechasString ="";
        for(int x = 0;x<fechas.size();x++){
            fechasString += LimpiarFecha(fechas.get(x)) + "\n";
        }

        holder.textViewFechasEvento.setText(fechasString);

        //Cargamos la imagen. Comprobamos si existe, sino la descargamos.
        String rutaAccesoImagen = DIRECTORIO + evento.getId() + ".png";
        File archivoImagen = new File(rutaAccesoImagen);
        if(!archivoImagen.exists())
        {
            DescargarImagen descargarImagen = new DescargarImagen(IMAGEN_TIPO_EVENTO,
                    holder.progressCargandoImagen, holder.imageViewImagenEvento, contexto);
            descargarImagen.execute(Integer.toString(evento.getId()));
        }
        else
        {
            Bitmap bitmap = BitmapFactory.decodeFile(rutaAccesoImagen);
            holder.imageViewImagenEvento.setImageBitmap(bitmap);
        }

        holder.textViewMasDetalles.setText("MÁS DETALLES"+"\n");
        holder.textViewCompartir.setText("COMPARTIR" + "\n");

        /**************************** ClickListeners *********************/
        holder.textViewMasDetalles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context contextoView = v.getContext();
                Intent i = new Intent(contextoView, DetalleEvento.class);
                i.putExtra("idEvento", mEvento.get(position).getId());
                contextoView.startActivity(i);
            }
        });

        holder.textViewCompartir.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final String URL_EVENTO = "http://ofj.com.mx/conciertos-eventos/evento/?id=";
                Context contextoView = v.getContext();

                String urlEfectiva = URL_EVENTO + mEvento.get(position).getId();

                Intent compartir = new Intent();
                compartir.setAction(Intent.ACTION_SEND);
                compartir.putExtra(Intent.EXTRA_TEXT, urlEfectiva);
                compartir.setType( "text/plain");
                contextoView.startActivity(compartir);
            }
        });

        holder.imageViewComprar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(contexto.getResources().getString(R.string.ticketmaster)));
                contexto.startActivity(intent);
            }
        });

        //Si el evento ha sido culminado colocamos la maarca.
        if(evento.getEventoCulminado())
        {
            holder.textEventoCulminado.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mEvento.size();
    }

    //Método para marcar los eventos que han sido culminados.
    public void marcarEventosCulminados()
    {
        //Establecemos la fecha actual.
        Time ahora = new Time();
        ahora.setToNow();

        //Obtenemos el mes de esta manera por cuestiones de control, ya que los meses en la clase
        //Time se expresan así (0 - 11);
        int mes = ahora.month + 1;

        //Fecha actual que servirá de punto de comparación.
        String fechaActual = String.format("%04d-%02d-%02d", ahora.year, mes, ahora.monthDay);

        //Marca que nos indicará que a partir de aquí todos los eventos han sido concluidos.
        boolean marcaConcluidos = false;
        int i = 0;

        while(!marcaConcluidos)
        {
            //Obtenemos el evento.
            ItemEvento evento = mEvento.get(i);

            //Comparamos con la última fecha de cada evento.
            if(evento.getFechas().get(evento.getCountFechas() - 1).compareTo(fechaActual) < 0)
            {
                marcaConcluidos = true;
            }
            else
            {
                i++;
            }
        }

        //Marcamos los eventos.
        for(int j = i; j < mEvento.size(); j++)
        {
            mEvento.get(j).setEventoCulminado(true);
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