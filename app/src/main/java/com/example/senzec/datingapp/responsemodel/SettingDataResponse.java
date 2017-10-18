package com.example.senzec.datingapp.responsemodel;


import java.io.Serializable;

public class SettingDataResponse implements Serializable{
    public String status;
    public Response response;

    public class Response implements Serializable{
        public String user_id;
        public String current_loc_name;
        public String current_loc_lat;
        public String current_loc_lon;
        public String current_loc_distance;
        public String future_loc_name;
        public String future_loc_lat;
        public String future_loc_lon;
        public String future_loc_start;
        public String future_loc_end;
        public String future_loc_distance;
        public String age_range_start;
        public String age_range_end;
        public String show_me;
        public String created_at;
        public String updated_at;
    }
}
