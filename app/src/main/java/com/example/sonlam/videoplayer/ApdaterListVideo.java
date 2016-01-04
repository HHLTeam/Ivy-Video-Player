package com.example.sonlam.videoplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Son Lam on 12/26/2015.
 */
public class ApdaterListVideo extends ArrayAdapter<VideoParseStorage> {
    private Context mContext;
    private List<VideoParseStorage> listData, myArrAllFilter;
    private ImageView imgVideo;
    private TextView tvTitle,tvDateUp;
    private RatingBar rate;

    public ApdaterListVideo(Context context, List<VideoParseStorage> objects){
        super(context, R.layout.custom_fragment_videolist, objects);
        myArrAllFilter = new ArrayList<>();
        myArrAllFilter.addAll(objects);
        listData = objects;
        mContext = context;

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater mLayoutInflater = LayoutInflater.from(mContext);
            convertView = mLayoutInflater.inflate(R.layout.custom_fragment_videolist, null);
        }

        if(listData.get(position)!=null) {
            tvTitle = (TextView) convertView.findViewById(R.id.TxtView_VideoTitle);
            tvDateUp = (TextView) convertView.findViewById(R.id.TxtView_Date);
            rate = (RatingBar) convertView.findViewById(R.id.ratingBar);
            imgVideo = (ImageView) convertView.findViewById(R.id.imageView_VideoOnline);


            tvTitle.setText(listData.get(position).getTenVideo());
            tvDateUp.setText(listData.get(position).getDate());
            rate.setRating(listData.get(position).getDanhGia());

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPurgeable  = true;

            Bitmap bitmap = BitmapFactory.decodeByteArray(listData.get(position).getAnhVideo(), 0, listData.get(position).getAnhVideo().length, options);
            imgVideo.setImageBitmap(bitmap);



        }


        //if ()

        return convertView;
    }
}
