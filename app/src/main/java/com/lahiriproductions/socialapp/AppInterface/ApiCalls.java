package com.lahiriproductions.socialapp.AppInterface;

import com.google.protobuf.Api;
import com.lahiriproductions.socialapp.models.QitoRingtone;
import com.lahiriproductions.socialapp.models.SendNotification;
import com.lahiriproductions.socialapp.models.UserStatus.UserStatus;
import com.lahiriproductions.socialapp.models.UserStatus.UserStatusData;
import com.lahiriproductions.socialapp.models.UserStatusRequest;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiCalls {

    @Headers("Content-Type: application/json")
    @GET(ApiInterface.API_GET_EVENTS)
    Call<com.lahiriproductions.socialapp.events.Events> getEvents();

    @Headers("Content-Type: application/json")
    @GET("index/api_mobile/ring")
    Call<QitoRingtone> getRingtones(@Path("api_number") String api_number);

    @POST(ApiInterface.API_INSERT_NOTIFICATION)
    @FormUrlEncoded
    Call<SendNotification> sendNotification(@FieldMap Map<String,String> params);

    @Headers("Content-Type: application/json")
    @POST(ApiInterface.API_GET_USER_STATUS)
//    @FormUrlEncoded
//    Call<UserStatusData> getUserStatus(@FieldMap Map<String,String> params);
    Call<UserStatusData> getUserStatus(@Body UserStatusRequest userStatusRequest);

}
