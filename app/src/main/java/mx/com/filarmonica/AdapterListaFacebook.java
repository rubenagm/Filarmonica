package mx.com.filarmonica;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import conexion.DescargarImagen;

/**
 * Created by root on 19/01/15.
 */
public class AdapterListaFacebook extends RecyclerView.Adapter<AdapterListaFacebook.ViewHolder>
{
    ArrayList<ItemFacebook> publicaciones;
    String DIRECTORIO = Environment.getExternalStorageDirectory().getAbsolutePath() +
            "/Imagenes/Facebook/";
    private final static int IMAGEN_TIPO_FACEBOOK = 3;
    Context contexto;

    public AdapterListaFacebook(ArrayList<ItemFacebook> publicaciones,Context contexto)
    {
        this.contexto = contexto;
        this.publicaciones = publicaciones;
    }

    //Contenedor.
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView textViewContenido;
        ImageView imageViewImagenFacebook;
        RelativeLayout progressCargandoImagen;
        TextView lblVerEnFacebook;

        public ViewHolder (View v)
        {
            super(v);
            textViewContenido = (TextView) v.findViewById(R.id.contenido_publicacion);
            imageViewImagenFacebook = (ImageView) v.findViewById(R.id.imagen_publicacion);
            progressCargandoImagen = (RelativeLayout) v.findViewById(R.id.relative_progress);
            lblVerEnFacebook = (TextView) v.findViewById(R.id.ver_en_facebook);
        }
    }

    //Creamos la vista.
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_item_facebook,
                viewGroup, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    //Colocamos la información.
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i)
    {
        //Desactivamos el reciclaje del RecyclerView.
        viewHolder.setIsRecyclable(false);

        //Obtenemos la publicación.
        ItemFacebook publicacion = publicaciones.get(i);

        viewHolder.textViewContenido.setText(publicacion.getContenido());

        //Cargamos la imagen. Comprobamos si existe, sino la descargamos.
        ContextWrapper contextWrapper = new ContextWrapper(contexto);
        File directorio = contextWrapper.getDir("Imagenes" + "Facebook", Context.MODE_PRIVATE);
        File archivoImagen = new File(directorio, DescargarImagen.nombreImagenUrl(publicacion
                .getUrlImagen()) + ".png");
        String imagen = publicacion.getUrlImagen();
        if(!archivoImagen.exists())
        {
            DescargarImagen descargarImagen = new DescargarImagen(IMAGEN_TIPO_FACEBOOK,
                    viewHolder.progressCargandoImagen, viewHolder.imageViewImagenFacebook,contexto);
            descargarImagen.execute(imagen);
        }
        else
        {
            try
            {
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(archivoImagen));
                viewHolder.imageViewImagenFacebook.setImageBitmap(bitmap);
            }
            catch (FileNotFoundException e)
            {
                Toast.makeText(contexto, contexto.getText(R.string.error_imagen), Toast.
                        LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }

        //Asignamos la url.
        asignarUrlFacebook(viewHolder.lblVerEnFacebook, publicacion.getUrlFacebook());
    }

    @Override
    public int getItemCount() {
        return publicaciones.size();
    }

    //Método para poner la url de facebook en l etiquete de "ver en facebook".
    public void asignarUrlFacebook(TextView lblVerEnFacebook, final String url)
    {
        lblVerEnFacebook.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse(url));
                contexto.startActivity(intent);
            }
        });
    }

}
