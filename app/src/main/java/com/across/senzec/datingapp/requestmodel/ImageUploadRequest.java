package com.across.senzec.datingapp.requestmodel;

import java.util.List;


public class ImageUploadRequest {
    public List<Data> photos;

    public class Data{
        public String data;
        public String type;
    }
}
