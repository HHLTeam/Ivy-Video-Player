package com.example.sonlam.videoplayer;

/**
 * Created by Son Lam on 12/23/2015.
 */

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.nostra13.universalimageloader.utils.L;
import com.parse.ParseFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VideoUpLoadFragment extends Fragment{

    Button btnTim_video, btnTim_Anh, btn_Upload;
    ImageView imageView;
    VideoView videoView;

    String filePath, filePathImage;
    String txtMota="",txtName="";
    Uri uriImage;

    TextView mota,tenvideo;

    private ProgressDialog dialog = null;

    public byte[] inputStreamToByteArray(InputStream inStream) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] buffer = new byte[8192];

        int bytesRead;

        while ((bytesRead = inStream.read(buffer)) > 0) {

            baos.write(buffer, 0, bytesRead);

        }

        return baos.toByteArray();

    }



    public VideoUpLoadFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.video_upload, container, false);

        btnTim_video = (Button)view.findViewById(R.id.btn_chon_video);

        btnTim_Anh=(Button)view.findViewById(R.id.btn_chon_anh);

        mota=(TextView)view.findViewById(R.id.editText_MoTa);
        tenvideo=(TextView)view.findViewById(R.id.editText_TenVideo);

        imageView = (ImageView)view.findViewById(R.id.imageView_VideoUpload);

        videoView=(VideoView)view.findViewById(R.id.videoView_Upload);

        MediaController controller = new MediaController(getActivity());

        controller.setAnchorView(this.videoView);

        controller.setMediaPlayer(this.videoView);

        this.videoView.setMediaController(controller);

        btnTim_video.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                Intent intent = new Intent();

                intent.setType("video/*");

                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(intent, "Select Video"), 2);

            }

        });

        btnTim_Anh.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                Intent intent = new Intent();

                intent.setType("image/*");

                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);

            }

        });

        btn_Upload = (Button)view.findViewById(R.id.btn_upload);
        btn_Upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(filePath!=null && uriImage!=null  && filePathImage!=null)
                {
                    txtMota=mota.getText().toString();
                    txtName=tenvideo.getText().toString();
                    startUpload(filePath, filePathImage);
                }
                /*else

                {
                    ParsePush push = new ParsePush();
                    push.setChannel("Video");
                    push.setMessage("The Giants just scored! It's now 2-2 against the Mets.");
                    push.sendInBackground();
                }*/
            }
        });

        return view;


    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    @SuppressLint("NewApi")
    private String getPath(Uri uri) throws URISyntaxException {
        final boolean needToCheckUri = Build.VERSION.SDK_INT >= 19;
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        // deal with different Uris.
        if (needToCheckUri && DocumentsContract.isDocumentUri(getActivity().getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[] {
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = null;
            try {
                cursor = getActivity().getContentResolver()
                        .query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == getActivity().RESULT_OK) {

                uriImage = data.getData();
                imageView.setImageURI(uriImage);

                try {
                    filePathImage=getPath(uriImage);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == 2) {
            if (resultCode == getActivity().RESULT_OK) {

                Uri selectedImageUri = data.getData();

                try {
                    filePath = getPath(selectedImageUri);
                    videoView.setVideoPath(filePath);
                    videoView.start();

                } catch (URISyntaxException e) {


                }


            } else if (requestCode == 3)//imgage
            {
                if (resultCode == getActivity().RESULT_OK) {

                    uriImage = data.getData();
                    imageView.setImageURI(uriImage);

                }
            }
        }
    }

    private void startUpload(final String filePath, final String image) {
        AsyncTask<String, String, String> task = new AsyncTask<String, String, String>() {
            protected String doInBackground(String... paths) {
                L.d("Running upload task");
                File filevideo = new File(paths[0]);

                AmazonS3Client sS3Client=null;
                String namefile="";

                try {

                    int SDK_INT = android.os.Build.VERSION.SDK_INT;

                    if (SDK_INT > 8) {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                                .permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                        //your codes here

                        //    java.security.Security.setProperty("networkaddress.cache.ttl" , "60");
                        AWSCredentials credentials = new BasicAWSCredentials("AKIAJ2M4WUIICOJ35TBA", "/VtL8R9g9zHizadlCJp81Bo26fhwlpgQUtZKo+SE");
                        //    sS3Client = new AmazonS3Client(getCredProvider(context.getApplicationContext()));


                        ClientConfiguration configuration = new ClientConfiguration();
                        configuration.setMaxErrorRetry(3);
                        configuration.setConnectionTimeout(5 * 50000);
                        configuration.setSocketTimeout(5 * 50000);
                        configuration.setProtocol(Protocol.HTTP);

                        sS3Client = new AmazonS3Client(credentials,configuration);


                        //    Toast.makeText(getActivity(), sS3Client.getBucketLocation("nmtri.uit"), Toast.LENGTH_SHORT).show();
                        // sS3Client.getUrl("nmtri.uit","6(21).jpg");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                        String currentDateandTime = sdf.format(new Date());
                        //namefile="["+ ParseUser.getCurrentUser().getUsername()+"]"+currentDateandTime+filevideo.getName();

                        namefile= currentDateandTime+filevideo.getName();

                        sS3Client.putObject(new PutObjectRequest("nmtriuit08", namefile, filevideo).withCannedAcl(CannedAccessControlList.PublicRead));


                        //
                        // sS3Client.getBucketLocation("nmtriuit");
                    }

                    //      L.i("Uploaded file: %s", cloudinaryResult.toString());
                } catch (RuntimeException e) {
                    L.e(e, "Error uploading file");
                    return "Error uploading file: " + e.toString();
                } catch (Exception e) {
                    L.e(e, "Error uploading file");
                    return "Error uploading file: " + e.toString();
                }

                String namfile=txtName+".png";

                FileInputStream fileInputStream=null;

                File fileImage = new File(paths[1]);

                byte[] bFile = new byte[(int) fileImage.length()];

                try {
                    //convert file into array of bytes
                    fileInputStream = new FileInputStream(fileImage);
                    fileInputStream.read(bFile);
                    fileInputStream.close();

                    for (int i = 0; i < bFile.length; i++) {
                        System.out.print((char)bFile[i]);
                    }

                    System.out.println("Done");
                }catch(Exception e){
                    e.printStackTrace();
                }



                ParseFile fileparse=new ParseFile(bFile,namfile);
                // update parse
                VideoParseStorage video = new VideoParseStorage();
                try {
                    video.setDanhGia(0);
                    video.setAnhVideo(fileparse);
                    video.setMoTaVideo(txtMota);
                    video.setTenVideo(txtName);


                    //video.setUser(ParseUser.getCurrentUser().getUsername());
                    video.setUser("Son Lam");

                    video.setUrl(sS3Client.getUrl("nmtriuit08",namefile).toString());
                    video.save();
                    L.i("Saved object");
                } catch (Exception e) {
                    L.e(e, "Error saving object");
                    return "Error saving object: " + e.toString();
                }
                uriImage=null;

                return null;
            }

            protected void onPostExecute(String error) {
                if (dialog != null) {
                    dialog.dismiss();
                    dialog = null;
                }
                if (error == null) {
                    getActivity().setResult(getActivity().RESULT_OK);

                } else {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Error")
                            .setMessage(error)
                            .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                }
                            })
                            .setCancelable(true)
                            .create().show();
                }
            }
        };
        dialog = ProgressDialog.show(getActivity(), "Uploading", "Uploading image");
        task.execute(filePath,image);
    }




}
