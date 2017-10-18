package com.across.senzec.datingapp.responsemodel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ravi on 10/7/17.
 */

public class FetchEventResponse implements Serializable{
    public String status;
    public List<Response> response;

    public class Response implements Serializable{
        public String created_at;
        public String id;
        public String latitude;
        public String longitude;
        public String name;
        public String time;
        public String type;
        public String updated_at;
        public String user_id;
        public String venue;

    }
}
