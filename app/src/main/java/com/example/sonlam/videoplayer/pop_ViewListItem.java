package com.example.sonlam.videoplayer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * Created by Son Lam on 12/26/2015.
 */
public class pop_ViewListItem extends Activity {
    VideoList videoList = new VideoList();
    Button btnXem;
    ImageView img;
    TextView nameUser, describe, ngayxuatban, tenvideo;
    RatingBar xemDanhGia, DanhGia;
    int position = 0;
    float rate = 0.0f;

    private static Editable editable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_video_info);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * 0.9), (int) (height * .6));

        img = (ImageView) findViewById(R.id.imageView_popup);

        tenvideo = (TextView) findViewById(R.id.txtView_VideoTitle_Popup);

        nameUser = (TextView) findViewById(R.id.txtView_User_Popoup);

        ngayxuatban = (TextView) findViewById(R.id.txtView_NgayXb_Popup);

        describe = (TextView) findViewById(R.id.txtView_Mota_Popup);

        btnXem = (Button) findViewById(R.id.btn_Xem_popup);

        Bitmap bitmap = BitmapFactory.decodeByteArray(getIntent().getByteArrayExtra("VideoImage"), 0, getIntent().getByteArrayExtra("VideoImage").length);
        img.setImageBitmap(bitmap);
        tenvideo.setText(tenvideo.getText().toString() + getIntent().getStringExtra("VideoTitle"));
        describe.setText(describe.getText().toString() + getIntent().getStringExtra("VideoSumUp"));
        nameUser.setText(nameUser.getText().toString() + getIntent().getStringExtra("User"));
        //ngayxuatban.setText(ngayxuatban.getText().toString() + getIntent().getStringExtra("Date").split("GMT")[0]);
        xemDanhGia = (RatingBar) findViewById(R.id.ratingBar_user_popup);
        xemDanhGia.setRating(getIntent().getFloatExtra("Rate", 0.0f));
        DanhGia = (RatingBar) findViewById(R.id.ratingBar_you_popup);
        position = getIntent().getIntExtra("position", 0);
        DanhGia.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                rate = rating;
                VideoListFragment.mAdapter.getItem(position).setDanhGia((getIntent().getFloatExtra("Rate", 0.0f) + rate) / 2);
                VideoListFragment.mAdapter.getItem(position).saveEventually();
            }
        });

        btnXem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //video/upload/v1450321205/First_Game_f730mf.mp4

                Intent intent = new Intent(getApplicationContext(), PlayVideo.class);
                startActivity(intent);
            }
        });
    }
}
