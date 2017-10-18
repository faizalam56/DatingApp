package com.across.senzec.datingapp.api;


import com.across.senzec.datingapp.requestmodel.AllPostListRequest;
import com.across.senzec.datingapp.requestmodel.CreateEventRequest;
import com.across.senzec.datingapp.requestmodel.FindMatchRequest;
import com.across.senzec.datingapp.requestmodel.ImageUploadRequest;
import com.across.senzec.datingapp.requestmodel.NewCommentRequest;
import com.across.senzec.datingapp.requestmodel.NewPostRequest;
import com.across.senzec.datingapp.requestmodel.RegisterUser;
import com.across.senzec.datingapp.requestmodel.SetTopicRequest;
import com.across.senzec.datingapp.requestmodel.UpdateProfileRequest;
import com.across.senzec.datingapp.requestmodel.UpdateSettingRequest;
import com.across.senzec.datingapp.requestmodel.UserRequest;
import com.across.senzec.datingapp.responsemodel.UserResponse;
import com.across.senzec.datingapp.utils.Constants;
import com.google.gson.JsonObject;

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



//    @POST("/guest/api/v1/is/user/{id}/exists")
//    Call<UserResponse> getUserResponse(@Path("id") String id, @Body UserRequest userRequest);

    @POST("/guest/api/v1/user/register")
    Call<UserResponse> getUserRegisterResponse(@Body RegisterUser registerUser);

    @POST("/auth/api/v1/user/{id}/upload/photos?")
    Call<UserResponse> uploadPhoto(@Path("id") String id, @Query("api_token") String token, @Body ImageUploadRequest imageUploadRequest);

    @POST("/auth/api/v1/user/{id}/topics/attach?")
    Call<JsonObject> setTopic(@Path("id") String id, @Query("api_token") String token, @Body SetTopicRequest setTopicRequest);

//    @GET("/auth/api/v1/user/{id}/get/profile?")
//    Call<EditInfoResponse> editInfo(@Path("id") String id,@Query("api_token") String token);

//    @PUT("/auth/api/v1/user/{id}/update/profile?")
//    Call<UpdateProfileResponse> updateProfile(@Path("id") String id, @Query("api_token") String token, @Body UpdateProfileRequest updateProfileRequest);

//    @POST("/auth/api/v1/user/{id}/find/users/from/present?")
//    Call<FindMatchResponse> findMatch(@Path("id") String id,@Query("api_token") String token,@Body FindMatchRequest findMatchRequest);

//    @GET("/auth/api/v1/user/{id}/chat/users?")
//    Call<ChatUserResponse> chatUser(@Path("id") String id,@Query("api_token") String token);

//    @GET("/auth/api/v1/user/{id}/get/interactions?")
//    Call<NotificationResponse> notificationList(@Path("id") String id,@Query("api_token") String token);

//    @GET("/auth/api/v1/user/{id}/get/setting?")
//    Call<SettingDataResponse> settingData(@Path("id") String id,@Query("api_token") String token);

//    @GET("/auth/api/v1/user/{id}/event/list?")
//    Call<FetchEventResponse> fetchAllEvent(@Path("id") String id,@Query("api_token") String token);

    @POST("/auth/api/v1/user/{id}/set/setting?")
    Call<UserResponse> updateSetting(@Path("id") String username,@Query("api_token") String token,@Body UpdateSettingRequest updateSettingRequest);

    @POST("/auth/api/v1/user/{username}/event/create?")
    Call<UserResponse> createEvent(@Path("username") String username,@Query("api_token") String token,@Body CreateEventRequest createEventRequest);

    @PUT("/auth/api/v1/user/{username}/event/{eventId}/update?")
    Call<UserResponse> updateCreateEvent(@Path("username") String username,@Path("eventId") String eventId,@Query("api_token") String token,@Body CreateEventRequest createEventRequest);

    @PUT("/auth/api/v1/user/{username}/set/status/inactive?")
    Call<UserResponse> deleteAccount(@Path("username") String username,@Query("api_token") String token);

    @POST("/auth/api/v1/user/{username1}/like/user/{username2}?")
    Call<UserResponse> likeRequest(@Path("username1") String username1,@Path("username2") String username2,@Query("api_token") String token);

    @POST("/auth/api/v1/user/{username1}/dislike/user/{username2}?")
    Call<UserResponse> dislikeRequest(@Path("username1") String username1,@Path("username2") String username2,@Query("api_token") String token);

    @POST("/auth/api/v1/user/{username1}/damn/user/{username2}?")
    Call<UserResponse> damnRequest(@Path("username1") String username1,@Path("username2") String username2,@Query("api_token") String token);

    @PUT("/auth/api/v1/user/{username1}/dislike/user/{username2}/interaction?")
    Call<UserResponse> dislikeRequestPut(@Path("username1") String username1,@Path("username2") String username2,@Query("api_token") String token);

    @PUT("/auth/api/v1/user/{username1}/like/user/{username2}/interaction?")
    Call<UserResponse> likeRequestPut(@Path("username1") String username1,@Path("username2") String username2,@Query("api_token") String token);

    @PUT("/auth/api/v1/user/{username1}/damn/user/{username2}/interaction?")
    Call<UserResponse> damnRequestPut(@Path("username1") String username1,@Path("username2") String username2,@Query("api_token") String token);

    //
    /// /auth/api/v1/user/{username1}/damn/user/{username2}/interaction?api_token=e33616605458ac0a336ccf9296ad2fbd37917eedb3f6bc6f948bba99ca497451



    @POST("/guest/api/v1/is/user/{id}/exists")
    Call<JsonObject> getUserResponse(@Path("id") String id, @Body UserRequest userRequest);

   /* @POST("/auth/api/v1/user/{id}/find/users/from/present?")
    Call<JsonObject> findMatch(@Path("id") String id,@Query("api_token") String token,@Body FindMatchRequest findMatchRequest);*/
   @POST("/auth/api/v1/user/{id}/find/chat/users/from/present?")
   Call<JsonObject> findMatch(@Path("id") String id,@Query("api_token") String token,@Body FindMatchRequest findMatchRequest);

    //    @GET("/auth/api/v1/user/{id}/chat/users?")
//    Call<ChatUserResponse> chatUser(@Path("id") String id,@Query("api_token") String token);


    /*@POST("/auth/api/v1/user/{id}/find/users/from/future?")
    Call<JsonObject> findfutureMatch(@Path("id") String id,@Query("api_token") String token,@Body FindMatchRequest findMatchRequest);*/
    @POST("/auth/api/v1/user/{id}/find/chat/users/from/future?")
    Call<JsonObject> findfutureMatch(@Path("id") String id,@Query("api_token") String token,@Body FindMatchRequest findMatchRequest);

    @GET("/auth/api/v1/user/{id}/get/profile?")
    Call<JsonObject> editInfo(@Path("id") String id,@Query("api_token") String token);

    @PUT("/auth/api/v1/user/{id}/update/profile?")
    Call<JsonObject> updateProfile(@Path("id") String id, @Query("api_token") String token, @Body UpdateProfileRequest updateProfileRequest);

    @GET("/auth/api/v1/user/{id}/chat/users?")
    Call<JsonObject> chatUser(@Path("id") String id,@Query("api_token") String token);


    @GET("/auth/api/v1/user/{id}/get/interactions?")
    Call<JsonObject> notificationList(@Path("id") String id,@Query("api_token") String token);

    @GET("/auth/api/v1/user/{id}/get/setting?")
    Call<JsonObject> settingData(@Path("id") String id,@Query("api_token") String token);

    @GET("/auth/api/v1/user/{id}/event/list?")
    Call<JsonObject> fetchAllEvent(@Path("id") String id,@Query("api_token") String token);


  ////Check new post function Api./////////

    @GET("/auth/api/v1/user/{id}/post/list?")
    Call<JsonObject> fetchAllPost(@Path("id") String id,@Query("api_token") String token);

    @POST("/auth/api/v1/user/{id}/find/users/posts/from/present?")
    Call<JsonObject> fetchAllPostList(@Path("id") String id,@Query("api_token") String token,@Body AllPostListRequest allPostListhRequest);

    @GET("/auth/api/v1/user/{id}/post/{postid}/comment/users?")
    Call<JsonObject> findCommentOnPost(@Path("id") String id,@Path("postid") String postid,@Query("api_token") String token);

    @POST("/auth/api/v1/user/{id}/post/create?")
    Call<JsonObject> createNewPost(@Path("id") String id,@Query("api_token") String token,@Body NewPostRequest newPostRequest);

    @POST("/auth/api/v1/user/{id}/comment/create?")
    Call<JsonObject> createNewComment(@Path("id") String id,@Query("api_token") String token,@Body NewCommentRequest newPostRequest);
}
