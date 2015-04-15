package mx.com.filarmonica;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import java.io.IOException;
import java.util.ArrayList;

import android.media.AudioManager;
import android.os.Binder;
import android.os.PowerManager;
import android.widget.TextView;

/**
 * Created by macmini3cuceimobile on 2/13/15.
 */
public class ServicioMusica extends Service implements MediaPlayer.OnPreparedListener,MediaPlayer.OnErrorListener,MediaPlayer.OnCompletionListener {

    //media player
    private MediaPlayer player;
    //song list
    private ArrayList<ItemStreamingMusica> songs;
    //current position
    private int songPosn;

    //Información que se muestra dentro del streaming
    TextView textViewTituloCancion;
    TextView textViewDirector;
    TextView textViewDuracion;

    //bandera cambio de posicion
    boolean banderaCambioPosicion = false;

    private final IBinder musicBind = new MusicBinder();
    boolean banderaPause = false;
    @Override
    public void onCreate(){
        //create the service
        //create the service
        super.onCreate();
        //initialize position
        songPosn=0;
        //create player
        player = new MediaPlayer();
        initMusicPlayer();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        player.start();
    }
    @Override
    public boolean onUnbind(Intent intent){
        player.stop();
        player.release();
        return false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }
    public void initMusicPlayer(){
        //set player properties
        player.setWakeMode(getApplicationContext(),PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }
    public void setList(ArrayList<ItemStreamingMusica> theSongs){
        songs=theSongs;
    }
    public class MusicBinder extends Binder {
        ServicioMusica getService() {
            return ServicioMusica.this;
        }
    }

    public void playSong(){
        textViewTituloCancion.setText(songs.get(songPosn).getTitulo());
        textViewDirector.setText(songs.get(songPosn).getDirector());
        if(banderaPause && banderaCambioPosicion == false){
            player.start();
        }
        else{
            try {
                player.reset();
                player.setDataSource(songs.get(songPosn).getUrl());
                player.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
            banderaPause = false;
            banderaCambioPosicion = true;
        }
    }
    public void setBanderaCambioPosicion(boolean posicion){
        banderaCambioPosicion = posicion;
    }
    public int getPosition(){
        return player.getCurrentPosition();
    }
    public int getMaxTrack(){
        return player.getDuration();
    }
    public void setSong(int songIndex){
        songPosn = songIndex;
    }
    public boolean getReproduccion(){


        return player.isPlaying();
    }
    public void nextSong(){
        songPosn++;
        if(songPosn > (songs.size()-1)){
            songPosn =0;
        }
        setSong(songPosn);
        playSong();

        textViewTituloCancion.setText(songs.get(songPosn).getTitulo());
        textViewDirector.setText(songs.get(songPosn).getDirector());
    }
    public void prevSong(){

        songPosn--;
        if(songPosn < 0){
            songPosn =(songs.size()-1);
        }
        setSong(songPosn);
        playSong();

        textViewTituloCancion.setText(songs.get(songPosn).getTitulo());
        textViewDirector.setText(songs.get(songPosn).getDirector());
    }

    public void pauseSong(){

        textViewTituloCancion.setText(songs.get(songPosn).getTitulo());
        textViewDirector.setText(songs.get(songPosn).getDirector());
        player.pause();
        banderaPause = true;
    }
    public void setItems(TextView textViewTituloCancion,TextView textViewDirector, TextView textViewDuracion){
        this.textViewTituloCancion = textViewTituloCancion;
        this.textViewDirector = textViewDirector;
        this.textViewDuracion = textViewDuracion;

        textViewTituloCancion.setText(songs.get(songPosn).getTitulo());
        textViewDirector.setText(songs.get(songPosn).getDirector());
    }
}
