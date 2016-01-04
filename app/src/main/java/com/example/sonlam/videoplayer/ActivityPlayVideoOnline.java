package com.example.sonlam.videoplayer;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

/**
 * Created by Son Lam on 12/26/2015.
 */
public class ActivityPlayVideoOnline extends Activity {
    //EditText edCm;
    //Button btnCm;
    //ListView lvCm;
    //ParseComment comment;
    ProgressBar progressBar = null;

    VideoView videoView = null;

    String videoUrl = "video path here";

    Context context = null;

    @Override
    public void onCreate(Bundle iclic) {
        super.onCreate(iclic);
        videoUrl = getIntent().getStringExtra("Url");
        context = null;

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.video_online);
        //lvCm=(ListView)findViewById(R.id.lvComment);
        //edCm=(EditText)findViewById(R.id.edComment);
        //btnCm=(Button)findViewById(R.id.btnComment);
        videoView = (VideoView) findViewById(R.id.videoViewOnline);
        MediaController controller = new MediaController(this);
        controller.setAnchorView(this.videoView);
        controller.setMediaPlayer(this.videoView);
        this.videoView.setMediaController(controller);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        Uri videoUri = Uri.parse(videoUrl);

        videoView.setVideoURI(videoUri);
        videoView.start();

        progressBar.setVisibility(View.VISIBLE);

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                // TODO Auto-generated method stub
                mp.start();
                mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int arg1,
                                                   int arg2) {
                        // TODO Auto-generated method stub
                        progressBar.setVisibility(View.GONE);
                        mp.start();
                    }
                });

            }
        });

        //


        /*lvCm.setAdapter(TabVideo.apdaterListComment);
        btnCm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edCm.getText().toString() != "" && ParseUser.getCurrentUser().getUsername() != "") {
                    comment = new ParseComment();
                    //phan quyen
                    ParseACL postACL = new ParseACL(ParseUser.getCurrentUser());
                    postACL.setPublicReadAccess(true);
                    comment.setACL(postACL);
                    comment.setUser(ParseUser.getCurrentUser());
                    comment.setComment(edCm.getText().toString());
                    comment.setUsername(ParseUser.getCurrentUser().getUsername());
                    edCm.setText("");
                    //
                    comment.setVideo(TabVideo.mAdapter.getItem(getIntent().getIntExtra("position", 0)));
                    comment.saveEventually();
                    TabVideo.apdaterListComment.insert(comment, 0);
                }
                //---------
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });

                //---------

            }
        });*/
    }
}
