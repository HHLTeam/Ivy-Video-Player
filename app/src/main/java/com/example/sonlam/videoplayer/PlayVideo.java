package com.example.sonlam.videoplayer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

/**
 * Created by Son Lam on 12/20/2015.
 */
public class PlayVideo extends Activity {            //Play online video
    VideoList videoList = new VideoList();
    //private String videoPath = videoList.getEditTextStream().getText().toString();      //Get Video URL
    private String videoPath = videoList.getYouEditTextValue().toString();

    private static ProgressDialog progressDialog;
    String videourl;
    VideoView videoView ;

    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.stream_video);

        videoView = (VideoView) findViewById(R.id.streamVideoView);

        progressDialog = ProgressDialog.show(PlayVideo.this, "", "Buffering video...",true);
        progressDialog.setCancelable(true);


        PlayVideo();

    }

    private void PlayVideo()
    {
        try
        {
            getWindow().setFormat(PixelFormat.TRANSLUCENT);
            MediaController mediaController = new MediaController(PlayVideo.this);
            mediaController.setAnchorView(videoView);

            Uri video = Uri.parse(videoPath);
            videoView.setMediaController(mediaController);
            videoView.setVideoURI(video);
            videoView.requestFocus();
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
            {

                public void onPrepared(MediaPlayer mp)
                {
                    progressDialog.dismiss();
                    videoView.start();
                }
            });

        }
        catch(Exception e)
        {
            progressDialog.dismiss();
            System.out.println("Video Play Error :"+e.toString());
            finish();
        }

    }


}
