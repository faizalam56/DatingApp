package com.across.senzec.datingapp.responsemodel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ravi on 6/10/17.
 */

public class TopicListResponse implements Serializable{
    public String status;
    public List<Response> response;

    public class Response implements Serializable{
        public String id;
        public String topic;
        public String user_id;
        public String created_at;
        public String updated_at;
    }
}
