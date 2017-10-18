package com.example.senzec.datingapp.api;


import com.example.senzec.datingapp.requestmodel.FindMatchRequest;
import com.example.senzec.datingapp.requestmodel.ImageUploadRequest;
import com.example.senzec.datingapp.requestmodel.RegisterUser;
import com.example.senzec.datingapp.requestmodel.UpdateProfileRequest;
import com.example.senzec.datingapp.requestmodel.UserRequest;
import com.example.senzec.datingapp.responsemodel.ChatUserResponse;
import com.example.senzec.datingapp.responsemodel.EditInfoResponse;
import com.example.senzec.datingapp.responsemodel.FindMatchResponse;
import com.example.senzec.datingapp.responsemodel.NotificationResponse;
import com.example.senzec.datingapp.responsemodel.SettingDataResponse;
import com.example.senzec.datingapp.responsemodel.UpdateProfileResponse;
import com.example.senzec.datingapp.responsemodel.UserResponse;
import com.example.senzec.datingapp.utils.Constants;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIInterface {

    String userId = Constants.USERID;
    String user_token = Constants.USERTOKEN;

//    @GET("/sdnext/rest/webservice/employee")
//    Call<Employees.Employee1> getPatient();
//
//    @GET("/sdnext/rest/webservice/employee/{employeeid}")
//    Call<Employees.Employee1> getPatientById(@Path("employeeid") String userId);
//
//    @GET("/sdnext/rest/webservice/employees")
//    Call<Employees> getPatientList();
//
//    @GET("/sdnext/rest/webservice/json/util")
//    Call<Object> getJson();
//
//    @GET("/sdnext/rest/webservice/mesg/notification/{resgId}")
//    Call<Notification> getNotification(@Path("resgId") String resgId);
//
//    @GET("/sdnext/rest/webservice/hello")
//    Call<Object> getMessage();
//
//    @Multipart
//    @POST("/sdnext/rest/webservice/upload")
//    Call<String> uploadVideoToServer(@Part MultipartBody.Part video);


    @POST("/guest/api/v1/is/user/{id}/exists")
    Call<UserResponse> getUserResponse(@Path("id") String id, @Body UserRequest userRequest);

    @POST("/guest/api/v1/user/register")
    Call<UserResponse> getUserRegisterResponse(@Body RegisterUser registerUser);

    @POST("/auth/api/v1/user/{id}/upload/photos?")
    Call<UserResponse> uploadPhoto(@Path("id") String id, @Query("api_token") String token, @Body ImageUploadRequest imageUploadRequest);

    @GET("/auth/api/v1/user/{id}/get/profile?")
    Call<EditInfoResponse> editInfo(@Path("id") String id,@Query("api_token") String token);

    @PUT("/auth/api/v1/user/{id}/update/profile?")
    Call<UpdateProfileResponse> updateProfile(@Path("id") String id, @Query("api_token") String token, @Body UpdateProfileRequest updateProfileRequest);

    @POST("/auth/api/v1/user/{id}/find/users/from/present?")
    Call<FindMatchResponse> findMatch(@Path("id") String id,@Query("api_token") String token,@Body FindMatchRequest findMatchRequest);

    @GET("/auth/api/v1/user/{id}/chat/users?")
    Call<ChatUserResponse> chatUser(@Path("id") String id,@Query("api_token") String token);

    @GET("/auth/api/v1/user/{id}/get/interactions?")
    Call<NotificationResponse> notificationList(@Path("id") String id,@Query("api_token") String token);

    @GET("/auth/api/v1/user/{id}/get/setting?")
    Call<SettingDataResponse> settingData(@Path("id") String id,@Query("api_token") String token);
}
