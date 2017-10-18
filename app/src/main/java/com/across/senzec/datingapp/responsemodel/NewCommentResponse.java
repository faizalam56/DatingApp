package com.across.senzec.datingapp.responsemodel;

import java.io.Serializable;

/**
 * Created by ravi on 23/9/17.
 */

public class NewCommentResponse implements Serializable{
    public String status;
    public Response response;

    public class Response implements Serializable {
        public String message;
        public String post_id;
        public String user_id;
        public String updated_at;
        public String created_at;
        public String id;
    }
}
