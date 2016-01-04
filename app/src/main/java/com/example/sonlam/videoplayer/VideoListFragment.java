package com.example.sonlam.videoplayer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Son Lam on 12/25/2015.
 */
public class VideoListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private boolean isRefresh;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<VideoParseStorage> listData;
    private ListView listView;
    public static ApdaterListVideo mAdapter;

    private ProgressDialog dialog = null;





    VideoList videoList = new VideoList();
    private static Editable editable;





    public VideoListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onRefresh() {
        fetchMovies();
    }
    private void fetchMovies() {
        // showing refresh animation before making http call
        isRefresh=true;
        swipeRefreshLayout.setRefreshing(true);

        ParseQuery<VideoParseStorage> query= ParseQuery.getQuery(VideoParseStorage.class);

        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        query.orderByDescending("rate");
        query.findInBackground(new FindCallback<VideoParseStorage>() {
            @Override
            public void done(List<VideoParseStorage> list, ParseException e) {
                if (e == null) {
                    listData.clear();
                    for (int i = 0; i < list.size(); i++) {
                        listData.add(list.get(i));
                    }
                }
                mAdapter = new ApdaterListVideo(getActivity(), listData);
                listView.setAdapter(mAdapter);
                swipeRefreshLayout.setRefreshing(false);
                isRefresh = false;
            }
        });

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.tab_listview, container, false);

        listView=(ListView)view.findViewById(R.id.listvideo);
        listData=new ArrayList<>();
        //swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        //swipeRefreshLayout.setOnRefreshListener(this);

        ParseQuery<VideoParseStorage> query= ParseQuery.getQuery(VideoParseStorage.class);

        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        query.orderByDescending("Rate");
        query.findInBackground(new FindCallback<VideoParseStorage>() {
            @Override
            public void done(List<VideoParseStorage> list, ParseException e) {
                if (e == null) {
                    listData.clear();
                    for (int i = 0; i < list.size(); i++) {
                        listData.add(list.get(i));
                    }
                }
                mAdapter = new ApdaterListVideo(getActivity(), listData);
                listView.setAdapter(mAdapter);
            }
        });
        //
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final Intent pop_view = new Intent(getActivity(), pop_ViewListItem.class);
                pop_view.putExtra("position", position);
                pop_view.putExtra("Rate", mAdapter.getItem(position).getDanhGia());
                //pop_view.putExtra("Date", mAdapter.getItem(position).getDate().toString());
                pop_view.putExtra("VideoSumUp", mAdapter.getItem(position).getMotaVideo());
                pop_view.putExtra("User", mAdapter.getItem(position).getUser());
                // Bitmap bitmap= BitmapFactory.decodeByteArray(mAdapter.getItem(position).getFileImage(), 0, mAdapter.getItem(position).getFileImage().length);

                String url = mAdapter.getItem(position).getUrl();
                pop_view.putExtra("Url", mAdapter.getItem(position).getUrl());
                pop_view.putExtra("VideoTitle", mAdapter.getItem(position).getTenVideo());
                pop_view.putExtra("VideoImage", mAdapter.getItem(position).getAnhVideo());

                //
                editable = new SpannableStringBuilder(mAdapter.getItem(position).getUrl());
                videoList.setYouEditTextValue(editable);

                //Comment


                //



                            startActivity(pop_view);






            }


        });

        return view;
    }
}
