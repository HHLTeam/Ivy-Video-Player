package com.example.sonlam.videoplayer;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import java.util.Random;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.view.MenuInflater;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;




public class VideoList extends AppCompatActivity {


    private static final String TAG = VideoList.class.getSimpleName();
    SwipeRefreshLayout swipeRefreshLayout;
    private AlertDialog.Builder alertStreaming;

    public Editable getYouEditTextValue() {
        return YouEditTextValue;
    }

    public void setYouEditTextValue(Editable youEditTextValue) {
        YouEditTextValue = youEditTextValue;
    }

    private static Editable YouEditTextValue;
    private EditText editTextStream;

    public void setEditTextStream(EditText editTextStream) {
        this.editTextStream = editTextStream;
    }

    public EditText getEditTextStream() {
        return editTextStream;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VideoPlay vl=new VideoPlay();
                if(vl.get_filePath()==null){
                    int random = (int) (Math.random() * (videoList.getCount()+1));
                    jumpToVideoPlay(videoInfos.get(random).getUrl());
                }
                else
                {
                    jumpToVideoPlay(vl.get_filePath());
                }




            }
        });


        videoResolver = this.getContentResolver();
        final Cursor videoCursor;
        videoResolver = getContentResolver();
        videoInfos = new ArrayList<Video_Info>();
        videoCursor = videoResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoColums, null, null, null);
        if (videoCursor.moveToFirst()) {
            do {
                Video_Info videoInfo = new Video_Info();
                videoInfo.setTitle(videoCursor.getString(videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)));

                videoInfo.setUrl(videoCursor.getString(videoCursor.getColumnIndex(MediaStore.Video.Media.DATA)));
                videoInfo.setMimeType(videoCursor.getString(videoCursor.getColumnIndex(MediaStore.Video.Media.MIME_TYPE)));
                videoInfos.add(videoInfo);
            } while (videoCursor.moveToNext());
        }

        videoList = (ListView) findViewById(R.id.videoList);
        mAdapter = new Video_Adapter(this, videoInfos);
        videoList.setAdapter(mAdapter);
        videoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                jumpToVideoPlay(videoInfos.get(i).getUrl());
            }
        });


        registerForContextMenu(videoList); //Dang ki right click


        videoList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP && event.getAction() == MotionEvent.ACTION_DOWN) {
                    txtsearch.setVisibility(View.INVISIBLE);
                }
                return false;
            }
        });
        txtsearch = (EditText) findViewById(R.id.edit_text_search_listview);
        txtsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mAdapter.getFilter().filter(charSequence);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //Streaming Video from URL ...
        alertStreaming = new AlertDialog.Builder(this);          //Tao dialog Streaming

        editTextStream = new EditText(VideoList.this);
        alertStreaming.setMessage("Stream to URL: ");
        alertStreaming.setTitle("Streaming Video");

        alertStreaming.setView(editTextStream);

        alertStreaming.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //What ever you want to do with the value
                YouEditTextValue = editTextStream.getText();
                setYouEditTextValue(YouEditTextValue);

                //setEditTextStream(editTextStream);
                //getEditTextStream();

                Intent intent = new Intent(VideoList.this, PlayVideo.class);        //Lien lac giua Video List va PlayVideo
                startActivity(intent);

            }
        });

        alertStreaming.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
                dialog.cancel();
            }
        });

        videoList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                File file = new File(videoInfos.get(position).getUrl());
                file.delete();
                boolean deleted = file.delete();
                if (file.exists() == false && deleted == true) {
                    mAdapter.remove(mAdapter.getItem(position));
                    Toast.makeText(VideoList.this, "Tành công", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(VideoList.this, "Không thành công", Toast.LENGTH_SHORT).show();


                return false;
            }
        });


    }


    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        System.exit(0);
                    }
                }).setNegativeButton("No", null).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        // Inflate the menu; this adds items to the action bar if it is present.
   /*     getMenuInflater().inflate(R.menu.menu_video_list, menu);
        MenuItem item = menu.findItem(R.id.edit_text_search_listview);*/

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_video_list, menu);

        //SearchView searchView =(SearchView)item.getActionView();
        //searchView.setOnQueryTextListener(this);
        return true;

    }

    public void jumpToVideoPlay(String url) {
        Intent intent = new Intent(VideoList.this, VideoPlay.class);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_info:
                showInfo();
                return true;
            case R.id.open_search:
                showSearchBox();
                return true;
            case R.id.action_stream:
                alertStreaming.show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showSearchBox() {
        EditText edTx1 = (EditText) findViewById(R.id.edit_text_search_listview);
        if (edTx1.getVisibility() == View.INVISIBLE)
            edTx1.setVisibility(View.VISIBLE);
        else if (edTx1.getVisibility() == View.VISIBLE)
            edTx1.setVisibility(View.INVISIBLE);
    }

    public void showInfo() {
        DialogFragment newFragment = new InfoDialogFragment();
        newFragment.show(getSupportFragmentManager(), "Info");
    }





    static public class InfoDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.info)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }


    Video_Adapter mAdapter;
    private ListView videoList = null;
    private ListView videoListSearch = null;
    private BaseAdapter videoAdapter = null;

    private static ContentResolver videoResolver = null;
    EditText txtsearch;
    ArrayList<Video_Info> videoInfos;
    //
    private static String[] videoColums = {
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.MIME_TYPE
    };

    public void LoadVideoInfo() {
        Cursor videoCursor;
        videoResolver = getContentResolver();
        videoInfos = new ArrayList<Video_Info>();
        videoCursor = videoResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoColums, null, null, null);
        if (videoCursor.moveToFirst()) {
            do {
                Video_Info videoInfo = new Video_Info();
                videoInfo.setTitle(videoCursor.getString(videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)));

                videoInfo.setUrl(videoCursor.getString(videoCursor.getColumnIndex(MediaStore.Video.Media.DATA)));
                videoInfo.setMimeType(videoCursor.getString(videoCursor.getColumnIndex(MediaStore.Video.Media.MIME_TYPE)));
                videoInfos.add(videoInfo);
            } while (videoCursor.moveToNext());
        }
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        //onCreate Context Menu (right click)
        super.onCreateContextMenu(menu, v, menuInfo);

        if (v.getId() == R.id.videoList) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_right_click, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case R.id.share:
                // add stuff here
                return true;
            case R.id.delete:
                // remove stuff here
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}
