package com.example.sonlam.videoplayer;


import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Son Lam on 12/23/2015.
 */

public class OfflineFragment extends Fragment {
    public static ListView videoList = null;

    Uri parcialUri = Uri.parse("content://media/external/audio/media");

    public OfflineFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View mView = inflater.inflate(R.layout.activity_video_list, container, false);

        videoResolver = getActivity().getContentResolver();
        final Cursor videoCursor;

        videoInfos = new ArrayList<Video_Info>();
        videoCursor = videoResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoColums, null, null, null);
        if (videoCursor.moveToFirst()) {
            do {
                Video_Info videoInfo = new Video_Info();
                videoInfo.setTitle(videoCursor.getString(videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)));
                int temp=videoCursor.getInt(videoCursor.getColumnIndex(MediaStore.MediaColumns._ID));
                videoInfo.setUri(Uri.withAppendedPath(parcialUri, "" + temp));
                videoInfo.setUrl(videoCursor.getString(videoCursor.getColumnIndex(MediaStore.Video.Media.DATA)));
                videoInfo.setMimeType(videoCursor.getString(videoCursor.getColumnIndex(MediaStore.Video.Media.MIME_TYPE)));
                videoInfos.add(videoInfo);
            } while (videoCursor.moveToNext());
        }

        videoList = (ListView)mView.findViewById(R.id.videoList);
        mAdapter = new Video_Adapter(getActivity(), videoInfos);
        videoList.setAdapter(mAdapter);

        videoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                jumpToVideoPlay(videoInfos.get(i).getUrl());
            }
        });


        registerForContextMenu(videoList);                                      //Dang ki right click


        videoList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP && event.getAction() == MotionEvent.ACTION_DOWN) {
                    txtsearch.setVisibility(View.INVISIBLE);
                }
                return false;
            }
        });
        txtsearch = (EditText)mView.findViewById(R.id.edit_text_search_listview);
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

        videoList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {


            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                File file = new File(videoInfos.get(position).getUrl());
                file.delete();
                getActivity().getContentResolver().delete(videoInfos.get(position).getUri(), null, null);
                mAdapter.remove(mAdapter.getItem(position));
                Toast.makeText(getActivity(), "Thành công", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        videoList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {


            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                File file = new File(videoInfos.get(position).getUrl());
                file.delete();
                getActivity().getContentResolver().delete(videoInfos.get(position).getUri(), null, null);
                mAdapter.remove(mAdapter.getItem(position));
                Toast.makeText(getActivity(), "Thành công", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        FloatingActionButton fab = (FloatingActionButton)mView.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VideoPlay vl = new VideoPlay();
                if (vl.get_filePath() == null) {
                    int random = (int) (Math.random() * (videoList).getCount() + 1);
                    jumpToVideoPlay(videoInfos.get(random).getUrl());
                } else {
                    jumpToVideoPlay(vl.get_filePath());
                }


            }
        });
        return mView;
    }
    public void jumpToVideoPlay(String url) {
        Intent intent = new Intent(getActivity(), VideoPlay.class);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    Video_Adapter mAdapter;

    private static ContentResolver videoResolver = null;
    EditText txtsearch;
    ArrayList<Video_Info> videoInfos;
    private static String[] videoColums = {
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.MIME_TYPE
    };

    public void LoadVideoInfo() {
        Cursor videoCursor;
        videoResolver = getActivity().getContentResolver();
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

}