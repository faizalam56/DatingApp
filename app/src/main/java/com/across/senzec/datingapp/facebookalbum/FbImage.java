package com.across.senzec.datingapp.facebookalbum;

/**
 * Created by zmq161 on 15/7/17.
 */
public class FbImage {
    private String FbImageId;
    private String FbImageUrl;

    public FbImage( String fbImageUrl) {
        FbImageUrl = fbImageUrl;
    }
    public String getFbImageId() {
        return FbImageId;
    }

    public String getFbImageUrl() {
        return FbImageUrl;
    }

}
