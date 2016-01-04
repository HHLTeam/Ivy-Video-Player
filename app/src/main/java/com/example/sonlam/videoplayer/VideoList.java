package com.example.sonlam.videoplayer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;


public class VideoList extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private static Editable YouEditTextValue;
    private EditText editTextStream;
    private AlertDialog.Builder alertStreaming;
    public Editable getYouEditTextValue() {
        return YouEditTextValue;
    }
    public void setYouEditTextValue(Editable youEditTextValue) {
        YouEditTextValue = youEditTextValue;
    }
   @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

       getWindow().setSoftInputMode(                                    //Solve hidden editText caused by keyboard
               WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

         viewPager = (ViewPager)findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        //Streaming Video from URL ...
        alertStreaming = new AlertDialog.Builder(this);                            //Tao dialog Streaming
        editTextStream = new EditText(VideoList.this);
        alertStreaming.setMessage("Stream to URL: ");
        alertStreaming.setTitle("Streaming Video");
        alertStreaming.setView(editTextStream);
        alertStreaming.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                YouEditTextValue = editTextStream.getText();
                setYouEditTextValue(YouEditTextValue);
                Intent intent = new Intent(VideoList.this, PlayVideo.class);        //Lien lac giua Video List va PlayVideo
                startActivity(intent);

            }
        });
        alertStreaming.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new OfflineFragment(), "Your video");
        adapter.addFragment(new VideoUpLoadFragment(), "Upload");
        adapter.addFragment(new VideoListFragment(), "O.Video List");
        viewPager.setAdapter(adapter);
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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_video_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
            case R.id.refresh:

                //videoList.invalidateViews();

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
}
