package mx.com.filarmonica;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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
 * Created by macmini3cuceimobile on 3/6/15.
 */
public class DialogoMostrarImagenInstagram extends DialogFragment  {

    int position = 0;
    int contador = 0;
    //Arreglos necesario para guardar los datos a mostrar en caso de querer ver otra imagen
    ArrayList<String> imagenes;
    ArrayList<String> links;
    ArrayList<String> texto;

    //Imagen
    ImageView imageViewimagen;

    //Botones de imagenes siguiente y anterior en instagram
    ImageView imageViewImagenSiguiente = null;
    ImageView imageViewImagenAnterior = null;

    private final static int IMAGEN_TIPO_INSTAGRAM = 4;

    TextView textViewtexto;
    TextView textViewVerEnInstagram;
    Context contexto;
    RelativeLayout layoutCerrar = null;

    //El progres bar de cargando
    RelativeLayout progressCargandoImagen;
    static DialogoMostrarImagenInstagram newInstance(int position, ArrayList<String> imagenes,
                                ArrayList<String> texto, ArrayList<String> links)
    {
        DialogoMostrarImagenInstagram f = new DialogoMostrarImagenInstagram();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putStringArrayList("imagenes", imagenes);
        args.putStringArrayList("texto", texto);
        args.putStringArrayList("links", links);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Se hace transparente el dialogo
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //se obtiene el contexto
        contexto = getDialog().getContext();


        //Se obtiene por parametros los datos a mostrar
        position = getArguments().getInt("position");
        imagenes = getArguments().getStringArrayList("imagenes");
        texto = getArguments().getStringArrayList("texto");
        links = getArguments().getStringArrayList("links");

        contador = position; //Se igualan para saber la posicion

        //Se crea el dialogo
        View v = inflater.inflate(mx.com.filarmonica.R.layout.dialogo_mostrar_imagen_instagram, container, false);

        //Inicializar los botones de imagen anterior u siguiente
        imageViewImagenAnterior = (ImageView) v.findViewById(mx.com.filarmonica.R.id.boton_imagen_anterior_instagram);
        imageViewImagenSiguiente = (ImageView) v.findViewById(mx.com.filarmonica.R.id.boton_imagen_siguiente_instagram);


        //se inicializa el botón de cerrar el drawer (Es un layout)
        layoutCerrar = (RelativeLayout) v.findViewById(mx.com.filarmonica.R.id.layout_cerrar_dialogo);

        //Se ponen las propiedades de transparencia y se quita el titulo
        getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        //Se inicializa el objeto para mostrar la imagen
        imageViewimagen = (ImageView) v.findViewById(mx.com.filarmonica.R.id.dialogo_imagen_instagram);

        //Inicializar el progress bar
        progressCargandoImagen = (RelativeLayout) v.findViewById(mx.com.filarmonica.R.id.relative_progress);

        //
        //Cargamos la imagen. Comprobamos si existe, sino la descargamos.
        String imagen = imagenes.get(position);
        if(imagen!= null){

            ContextWrapper contextWrapper = new ContextWrapper(contexto);
            File directorio = contextWrapper.getDir("Imagenes" + "Instagram",
                    Context.MODE_PRIVATE);
            File archivoImagen = new File(directorio, DescargarImagen.nombreImagenUrl(imagen)
                    + ".png");

            if(!archivoImagen.exists())
            {
                DescargarImagen descargarImagen = new DescargarImagen(IMAGEN_TIPO_INSTAGRAM,
                        progressCargandoImagen, imageViewimagen,contexto);
                descargarImagen.execute(imagen);
            }
            else
            {
                try
                {
                    Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(archivoImagen));
                    imageViewimagen.setImageBitmap(bitmap);
                }
                catch (FileNotFoundException e)
                {
                    Toast.makeText(contexto, contexto.getText(R.string.error_imagen), Toast.
                            LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }

        textViewtexto = (TextView) v.findViewById(mx.com.filarmonica.R.id.dialogo_informacion_instagram);
        textViewtexto.setText(texto.get(position));

        //Configuramos el ver en instagram.
        textViewVerEnInstagram = (TextView) v.findViewById(mx.com.filarmonica.R.id.dialogo_ver_en_instagram);
        textViewVerEnInstagram.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse(links.get(position)));
                contexto.startActivity(intent);
            }
        });

        layoutCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        //Comportamiento botones
        imageViewImagenSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 //se aumenta una posicion
                if(contador>imagenes.size()-2){
                    contador = 0;
                }
                else
                {
                    contador++;
                }
                //se carga la imagen
                String imagen = imagenes.get(contador);
                if(imagen == null || imagen.equals(""))
                {
                    contador = 0;
                    imagen = imagenes.get(contador);
                }

                ContextWrapper contextWrapper = new ContextWrapper(contexto);
                File directorio = contextWrapper.getDir("Imagenes" + "Instagram",
                        Context.MODE_PRIVATE);
                File archivoImagen = new File(directorio, DescargarImagen.nombreImagenUrl(imagen)
                        + ".png");

                if(!archivoImagen.exists())
                {
                    DescargarImagen descargarImagen = new DescargarImagen(IMAGEN_TIPO_INSTAGRAM,
                            progressCargandoImagen, imageViewimagen,contexto);
                    descargarImagen.execute(imagen);
                }
                else
                {
                    try
                    {
                        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(archivoImagen));
                        imageViewimagen.setImageBitmap(bitmap);
                    }
                    catch (FileNotFoundException e)
                    {
                        Toast.makeText(contexto, contexto.getText(R.string.error_imagen), Toast.
                                LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }

                textViewtexto.setText(texto.get(contador));

            }
        });

        //Imagen anterior
        imageViewImagenAnterior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //se resta una posicion
                if(contador<1){
                    contador = imagenes.size()-1;
                }
                else
                {
                    contador--;
                }
                //se carga la imagen
                String imagen = imagenes.get(contador);


                if(imagen== null || imagen.equals(""))
                {
                    contador = imagenes.size();

                    while(imagen== null || imagen.equals(""))
                    {
                        contador--;
                        imagen = imagenes.get(contador);
                    }
                }

                ContextWrapper contextWrapper = new ContextWrapper(contexto);
                File directorio = contextWrapper.getDir("Imagenes" + "Instagram",
                        Context.MODE_PRIVATE);
                File archivoImagen = new File(directorio, DescargarImagen.nombreImagenUrl(imagen)
                        + ".png");

                if(!archivoImagen.exists())
                {
                    DescargarImagen descargarImagen = new DescargarImagen(IMAGEN_TIPO_INSTAGRAM,
                            progressCargandoImagen, imageViewimagen,contexto);
                    descargarImagen.execute(imagen);
                }
                else
                {
                    try
                    {
                        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(archivoImagen));
                        imageViewimagen.setImageBitmap(bitmap);
                    }
                    catch (FileNotFoundException e)
                    {
                        Toast.makeText(contexto, contexto.getText(R.string.error_imagen), Toast.
                                LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
                textViewtexto.setText(texto.get(contador));
            }
        });
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public void setPosition(int position){
        this.position = position;
    }
    public void setContexto(Context contexto){
        this.contexto = contexto;
    }




}