package com.example.sonlam.videoplayer;

import android.app.Application;
import android.content.Context;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;


/**
 * Created by Son Lam on 12/23/2015.
 */


public class MainApplication extends Application {
    private static MainApplication instance = new MainApplication();

    public MainApplication() {
        instance = this;
    }

    public static Context getContext() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "xThNC6jaddlXS0XdfrJhTAnjOaMg2GUtSlPRFrx4", "v9zDRwtdnqOc9MZxCQ0x51ARbFhp4kPb3kLChllF");
        ParseInstallation.getCurrentInstallation().saveInBackground();
        ParsePush.subscribeInBackground("Video");
        ParseObject.registerSubclass(VideoParseStorage.class);
        //ParseObject.registerSubclass(ParseComment.class);
    }
}
