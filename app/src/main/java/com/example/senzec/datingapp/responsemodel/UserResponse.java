package com.example.senzec.datingapp.responsemodel;


import java.io.Serializable;

public class UserResponse implements Serializable{
    public String status;
    public String response;

    @Override
    public String toString() {
        return status+" "+response;
    }
}
