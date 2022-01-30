package com.lahiriproductions.socialapp.AppInterface;

import com.google.protobuf.Api;
import com.lahiriproductions.socialapp.models.SendNotification;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiCalls {

    @Headers("Content-Type: application/json")
    @GET(ApiInterface.API_GET_EVENTS)
    Call<com.lahiriproductions.socialapp.events.Events> getEvents();

    @POST(ApiInterface.API_INSERT_NOTIFICATION)
    @FormUrlEncoded
    Call<SendNotification> sendNotification(@FieldMap Map<String,String> params);

}
