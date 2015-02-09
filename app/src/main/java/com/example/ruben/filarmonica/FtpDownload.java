package com.example.ruben.filarmonica;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.StringTokenizer;

/**
 * Created by Ruben on 13/01/2015.
 */
public class FtpDownload {
    FTPClient ftp = null;
    private final String URL = "http://www.ofj.com.mx/img/uploads/events/655x308/";
    private final String URLImagen = "http://ofj.com.mx/img/uploads/noticias/";
    private final String URLImagenFacebook = "";

    public boolean descargarArchivo(int tipo,String nombreArchivo) throws IOException {

        String urlS = "";
        if(tipo ==1){
            urlS = URL;
        }
        if(tipo ==2)
        {
            urlS = URLImagen;
        }
        if(tipo==3){
            urlS = nombreArchivo;
            nombreArchivo = "";
        }
        try {
            URL url = new URL(urlS+nombreArchivo+".jpg");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                    "/Imagenes";
            File dir = new File(file_path);
            if(!dir.exists())
                dir.mkdirs();
            if(tipo == 2) nombreArchivo += "Not";
            if(tipo == 3) nombreArchivo = nombreImagen(urlS) + "NotFacebook";
            File file = new File(dir, "imagenes" + nombreArchivo + ".png");
            Log.i("FTP Download ","salvado en "+file.getAbsolutePath());
            FileOutputStream fOut = new FileOutputStream(file);
            myBitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            Log.i("FTP Fownload","Imagen descargada");
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public String nombreImagen (String urlImagen){
        String id="";
        StringTokenizer token = new StringTokenizer(urlImagen,"/");
        while(token.hasMoreTokens()){
            id = token.nextToken();
        }
        StringTokenizer token2 = new StringTokenizer(id,".");

        return token2.nextToken();
    }
}
