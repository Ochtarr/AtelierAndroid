package fac.appandroid;

import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.util.UUID;

import static java.nio.file.Paths.get;

public class Player extends AppCompatActivity {

    public final UUID MY_UUID = UUID.randomUUID();

    //for log
    private static final String TAG = "PlayerActivity";

    protected VideoView vidView;
    private boolean hasBeenPlayed; // boolean pour savoir si on a quitté l'app en cours de lecture, utilisé pour reprendre la vidéo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String receivedFilePathName = (String) getIntent().getSerializableExtra("receivedFilePathName");
        setContentView(R.layout.activity_player);

        // Get a reference to the VideoView instance as follows, using the id we set in the XML layout.
        vidView = (VideoView)findViewById(R.id.video);

        // Add playback controls.
        MediaController vidControl = new MediaController(this);
        // Set it to use the VideoView instance as its anchor.
        vidControl.setAnchorView(vidView);
        // Set it as the media controller for the VideoView object.
        vidView.setMediaController(vidControl);

        Uri vidUri = Uri.fromFile(new File(receivedFilePathName));
        // Parse the address string as a URI so that we can pass it to the VideoView object.
        vidView.setVideoURI(vidUri);

        // just before the video starts, we display on log the duration of the video
        //vidView.setOnPreparedListener(new
        //    MediaPlayer.OnPreparedListener()  {
        //        @Override
        //        public void onPrepared(MediaPlayer mp) {
        //            Log.i(TAG, "Duration = " +
        //                    vidView.getDuration());
        //            Toast.makeText(getApplicationContext(), vidView.getDuration(), Toast.LENGTH_SHORT).show();
        //        }
        //    }
        //);
        // Start playback.
        vidView.start();

    }

    protected void onPause(){
        super.onPause();
        if(vidView.isPlaying()){
            hasBeenPlayed = true;
            vidView.pause();
        }
    }

    protected void onResume(){
        super.onResume();
        if(hasBeenPlayed == true){
            hasBeenPlayed = false;
            vidView.resume();
        }
    }
}