package com.example.ruben.filarmonica;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;


public class Streaming extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener
{
    TextView textViewDuracionVideo;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streaming);

        //Obtenemos las referecias.
        textViewDuracionVideo = (TextView) findViewById(R.id.titulo_video);

        //Cambiamos la fuente del TextView.
        Typeface fuente = Typeface.createFromAsset(getAssets(), "fonts/Roboto/Roboto-Light.ttf");
        textViewDuracionVideo.setTypeface(fuente);

        YouTubePlayerView player = (YouTubePlayerView) findViewById(R.id.youtube_player);
        player.initialize("AIzaSyC_gDmJgRqgTXP2F8sJJI1nOkNhyIh8DFI",this);

    }
    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b)
    {
        if(!b){
            youTubePlayer.cueVideo("uz0UrrhJEbM");
            int duracion  = youTubePlayer.getDurationMillis();
            textViewDuracionVideo.setText("Duracion: "+duracion);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Toast.makeText(this,
                "Error " + youTubeInitializationResult.toString(),
                Toast.LENGTH_LONG).show();
    }
}
