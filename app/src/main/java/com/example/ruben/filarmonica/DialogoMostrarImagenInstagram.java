package com.example.ruben.filarmonica;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import conexion.DescargarImagen;

/**
 * Created by macmini3cuceimobile on 3/6/15.
 */
public class DialogoMostrarImagenInstagram extends DialogFragment  {

    int position = 0;

    //Arreglos necesario para guardar los datos a mostrar en caso de querer ver otra imagen
    ArrayList<String> imagenes;
    ArrayList<String> links;
    ArrayList<String> texto;

    //Imagen
    ImageView imageViewimagen;

    //Directorio
    String DIRECTORIO = Environment.getExternalStorageDirectory().getAbsolutePath() +
            "/Imagenes/Instagram/";

    private final static int IMAGEN_TIPO_INSTAGRAM = 4;

    TextView textViewtexto;
    TextView textViewVerEnInstagram;
    Context contexto;
    RelativeLayout layoutCerrar = null;

    //El progres bar de cargando
    RelativeLayout progressCargandoImagen;
    static DialogoMostrarImagenInstagram newInstance(int position, ArrayList<String> imagenes, ArrayList<String> texto, ArrayList<String> links) {
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

        //Se crea el dialogo
        View v = inflater.inflate(R.layout.dialogo_mostrar_imagen_instagram, container, false);


        //se inicializa el botón de cerrar el drawer (Es un layout)
        layoutCerrar = (RelativeLayout) v.findViewById(R.id.layout_cerrar_dialogo);

        //Se ponen las propiedades de transparencia y se quita el titulo
        getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        //Se inicializa el objeto para mostrar la imagen
        imageViewimagen = (ImageView) v.findViewById(R.id.dialogo_imagen_instagram);

        //Inicializar el progress bar
        progressCargandoImagen = (RelativeLayout) v.findViewById(R.id.relative_progress);

        //
        //Cargamos la imagen. Comprobamos si existe, sino la descargamos.
        String imagen = imagenes.get(position);
        if(imagen!= null){

            String rutaAccesoImagen = DIRECTORIO + DescargarImagen.nombreImagenUrl(imagen) + ".png";
            File archivoImagen = new File(rutaAccesoImagen);
            if(!archivoImagen.exists())
            {
                DescargarImagen descargarImagen = new DescargarImagen(IMAGEN_TIPO_INSTAGRAM,
                        progressCargandoImagen, imageViewimagen,contexto);
                descargarImagen.execute(imagen);
            }
            else
            {
                Bitmap bitmap = BitmapFactory.decodeFile(rutaAccesoImagen);
                imageViewimagen.setImageBitmap(bitmap);
            }
        }

        textViewtexto = (TextView) v.findViewById(R.id.dialogo_informacion_instagram);
        textViewtexto.setText(texto.get(position));

        //Configuramos el ver en instagram.
        textViewVerEnInstagram = (TextView) v.findViewById(R.id.dialogo_ver_en_instagram);
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