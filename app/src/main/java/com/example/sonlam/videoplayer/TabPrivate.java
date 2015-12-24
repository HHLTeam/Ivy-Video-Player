/*
package com.example.sonlam.videoplayer;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


*/
/**
 * Created by Son Lam on 12/23/2015.
 *//*


public class TabPrivate extends Fragment {
    Button btnTim_video, btnTim_Anh;
    public byte[] inputStreamToByteArray(InputStream inStream) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] buffer = new byte[8192];

        int bytesRead;

        while ((bytesRead = inStream.read(buffer)) > 0) {

            baos.write(buffer, 0, bytesRead);

        }

        return baos.toByteArray();

    }

    public TabPrivate() {

    }

    @Override

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }



    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,

                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.video_upload, container, false);

        btn = (Button)view.findViewById(R.id.btnTimVideo);

        btnTimAnh=(Button)view.findViewById(R.id.btnTimAnh);

        mota=(TextView)view.findViewById(R.id.txtMoTa);

        imgaview=(ImageView)view.findViewById(R.id.imageView);

        videoView=(VideoView)view.findViewById(R.id.videoView);

        MediaController controller = new MediaController(getActivity());

        controller.setAnchorView(this.videoView);

        controller.setMediaPlayer(this.videoView);

        this.videoView.setMediaController(controller);

        tenvideo=(TextView)view.findViewById(R.id.txtName);

        btnTimVideo.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                Intent intent = new Intent();

                intent.setType("video
");

                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 2);

            }

        });

        btnTimAnh.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                Intent intent = new Intent();

                intent.setType("image/png");

                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);

            }

        });

    }
*/
