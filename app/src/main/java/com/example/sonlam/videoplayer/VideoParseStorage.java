package com.example.sonlam.videoplayer;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.security.PublicKey;


/**
 * Created by Son Lam on 12/23/2015.
 */


@ParseClassName("VideoParseStorage")
public class VideoParseStorage extends ParseObject {
    //Url
    //Ten Video
    //Mo ta - string
    //User - string
    public String getUrl(){
        return getString("URL");
    }

    public void setUrl(String url){
        put("URL", url );
    }

    public String getTenVideo(){
        return getString("VideoTitle");

    }

    public void setTenVideo(String TenVideo){
        put("VideoTitle", TenVideo );
    }

    public String getMotaVideo(){
        return getString("VideoSumUp");
    }

    public void setMoTaVideo(String Mota){
        put("VideoSumUp", Mota );
    }

    public String getUser(){
        return getString("User");
    }

    public void setUser(String User){
        put("User", User );
    }

    public int getLuotXem(){
        return getNumber("View").intValue();
    }

    public void setUser(int luotxem){
        put("View", luotxem );
    }
    public int getDanhGia(){
        return getNumber("Rate").intValue();
    }

    public void setDanhGia(int danhgia){
        put("Rate", danhgia );
    }

    public byte[] getAnhVideo(){
        try {
            return getParseFile("VideoImage").getData();
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setAnhVideo(ParseFile videoimage){
        put("VideoImage", videoimage);
    }
}
