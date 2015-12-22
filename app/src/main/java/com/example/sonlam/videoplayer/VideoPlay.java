package com.example.sonlam.videoplayer;

/**
 * Created by Son Lam on 12/18/2015.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v4.app.NotificationBuilderWithBuilderAccessor;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.SeekBar;
import java.io.IOException;
import java.util.logging.Handler;
import java.util.prefs.PreferenceChangeListener;

public class VideoPlay extends AppCompatActivity implements
        MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnInfoListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnVideoSizeChangedListener,
        SurfaceHolder.Callback,
        MediaController.MediaPlayerControl {


    private MediaPlayer mediaPlayer = null;
    private String videoUri = null; //url media file
    private SurfaceView videoView = null;
    private SurfaceHolder videoHolder = null;
    private String DEBUG = "video play";
    private MediaController mediaController = null;
    private SeekBar volumeSeekbar = null;
    private AudioManager audioManager = null; //manager audio
    private Runnable volumeSeebarRunnable = null;
    Handler mHandler = null;
    private boolean isPaused;
    private static String _filePath;
    public static String get_filePath() {
        return _filePath;
    }
    public static void set_filePath(String _filePath) {
        VideoPlay._filePath = _filePath;
    }
    private static int _seekTime;
    public static int get_seekTime() {
        return _seekTime;
    }
    public static void set_seekTime(int _seekTime) {
        VideoPlay._seekTime = _seekTime;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        setContentView(R.layout.video_play);

        //fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //set screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        videoView = (SurfaceView) findViewById(R.id.videoSurface);
        volumeSeekbar = (SeekBar) findViewById(R.id.volumeSeekbar);
        //Hide volume Seekbar
        volumeSeekbar.setVisibility(View.GONE);
        videoHolder = videoView.getHolder();
        videoHolder.addCallback(this);
        mediaController = new MediaController(this);
        initialMediaPlayer();
        videoUri = getVideoUri();
        try {
            mediaPlayer.setDataSource(videoUri);
        } catch (IOException e) {
            e.printStackTrace();
            finish();
        }

        volumeSeebarRunnable = new Runnable() {
            @Override
            public void run() {
                volumeSeekbar.setVisibility(View.INVISIBLE);
                volumeSeekbar.setVisibility(View.GONE);
            }
        };

    //Notification
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // prepare intent which is triggered if the
        // notification is selected
        Intent resultIntent = new Intent(this, VideoList.class);
        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        Notification n = new Notification.Builder(this)
                .setContentTitle("Ivy Player")
                .setContentText("Playing " + videoUri.substring(videoUri.lastIndexOf('/') + 1, videoUri.length()))
                .setSmallIcon(R.mipmap.ic_launcher_noti)
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true).build();
        nm.cancelAll();
        nm.notify(0, n);
        //Swipe to adjust volume
        videoView.setOnTouchListener(new OnSwipeTouchListener(VideoPlay.this) {
            //adjust audio
            @Override
            public void onSwipeTop() {
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
            }

            //adjust audio
            @Override
            public void onSwipeBottom() {
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
            }
            @Override
            public void onSwipeRight()
            {
                if((mediaPlayer.getDuration()-mediaPlayer.getCurrentPosition())>10000)
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+10000);
                else
                    mediaPlayer.seekTo(0);
            }
            public void onSwipeLeft()
            {
                if((mediaPlayer.getCurrentPosition())>10000)
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-10000);
                else
                    mediaPlayer.seekTo(0);
            }
            public boolean onTouch(View v, MotionEvent event) {
                mediaController.show();
                return gestureDetector.onTouchEvent(event);
            }
        });
    }

    public void playPreviousVideo()
    {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == android.view.KeyEvent.KEYCODE_BACK)
        {
            mediaPlayer.stop();
        }
        else if(keyCode == android.view.KeyEvent.KEYCODE_HOME)
        {
            mediaPlayer.pause();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_video_play, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String getVideoUri() {
        Intent intent = getIntent();
        Uri videoUri = intent.getData();
        return videoUri.toString();
    }
    private void initialMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnInfoListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnVideoSizeChangedListener(this);
        set_filePath(getVideoUri());
    }

    private void setVideoSize() {                       //set Video size
        // // Get the dimensions of the video
        int videoWidth = mediaPlayer.getVideoWidth();
        int videoHeight = mediaPlayer.getVideoHeight();
        float videoProportion = (float) videoWidth / (float) videoHeight;
        // Get the width of the screen
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        float screenProportion = (float) screenWidth / (float) screenHeight;
        // Get the SurfaceView layout parameters
        ViewGroup.LayoutParams lp = videoView.getLayoutParams();
        if (videoProportion > screenProportion) {
            lp.width = screenWidth;
            lp.height = (int) ((float) screenWidth / videoProportion);
        } else {
            lp.width = (int) (videoProportion * (float) screenHeight);
            lp.height = screenHeight;
        }
        // Commit the layout parameters
        videoView.setLayoutParams(lp);
        mediaPlayer.start();
    }

    //Initial volume seekbar control
    private void initControls() {
        try {
            volumeSeekbar = (SeekBar) findViewById(R.id.volumeSeekbar);
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            volumeSeekbar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            volumeSeekbar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
            volumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        //mAnimationHandler.removeCallbacks(mFadeOut);
        super.onConfigurationChanged(newConfig);

        setVideoSize();
    }
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
    }
    @Override
    public void onCompletion(MediaPlayer mp) {
        mp.setLooping(true);
    }
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }
    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }
    @Override
    public void onPrepared(MediaPlayer mp) {
        setVideoSize();
        mediaPlayer.start();
        mediaController.setMediaPlayer(this);
        mediaController.setAnchorView(this.findViewById(R.id.MainView));
        mediaController.setEnabled(true);
        mediaController.show();
    }
    @Override
    public void onSeekComplete(MediaPlayer mp) {
        mp.setLooping(true);
    }
    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if (isPaused) {
                mediaPlayer.start();
                isPaused = false;
            }
            else
                mediaPlayer.prepare();
            //mediaPlayer.prepareAsync();
            mediaPlayer.setDisplay(videoHolder);
        }catch (IOException i){
            mediaPlayer.prepareAsync();
            mediaPlayer.setDisplay(videoHolder);
        }
        //mediaPlayer.prepareAsync();
        //mediaPlayer.setDisplay(videoHolder);
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPaused = true;
        }
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }
    @Override
    public void start() {
        mediaPlayer.start();

    }
    @Override
    public void pause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }
    @Override
    public int getDuration() {
        return mediaPlayer.getDuration();
    }
    @Override
    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }
    @Override
    public void seekTo(int pos) {
        mediaPlayer.seekTo(pos);
    }
    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }
    @Override
    public int getBufferPercentage() {
        return 0;
    }
    @Override
    public boolean canPause() {
        return true;
    }
    @Override
    public boolean canSeekBackward() {
        return true;
    }
    @Override
    public boolean canSeekForward() {
        return true;
    }
    @Override
    public int getAudioSessionId() {
        return 0;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mediaController.show();
        volumeSeekbar.setVisibility(View.VISIBLE);
        volumeSeekbar.removeCallbacks(volumeSeebarRunnable);
        volumeSeekbar.postDelayed(volumeSeebarRunnable, 3000);
        initControls();
        return super.onTouchEvent(event);
    }
}
