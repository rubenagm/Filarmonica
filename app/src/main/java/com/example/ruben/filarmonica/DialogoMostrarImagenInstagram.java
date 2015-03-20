package com.example.ruben.filarmonica;

import android.app.Notification;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by macmini3cuceimobile on 3/6/15.
 */
public class DialogoMostrarImagenInstagram extends DialogFragment {

    int position = 0;
    ArrayList<String> imagenes;
    ArrayList<String> links;
    ArrayList<String> texto;
    ImageView imageViewimagen;
    String DIRECTORIO = "/storage/emulated/0/Imagenes/imagenes";
    TextView textViewtexto;
    TextView textViewverEnInstagram;
    Context contexto;
    RelativeLayout layoutCerrar = null;
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

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        position = getArguments().getInt("position");
        imagenes = getArguments().getStringArrayList("imagenes");
        texto = getArguments().getStringArrayList("texto");
        links = getArguments().getStringArrayList("links");
        View v = inflater.inflate(R.layout.dialogo_mostrar_imagen_instagram, container, false);
        layoutCerrar = (RelativeLayout) v.findViewById(R.id.layout_cerrar_dialogo);
        getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        imageViewimagen = (ImageView) v.findViewById(R.id.dialogo_imagen_instagram);
        String archivo = DIRECTORIO + "Instagram_" + imagenes.get(position)+".png";
        Bitmap bit1 = BitmapFactory.decodeFile(archivo);
        textViewtexto = (TextView) v.findViewById(R.id.dialogo_informacion_instagram);
        textViewtexto.setText(texto.get(position));
        imageViewimagen.setImageBitmap(bit1);
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