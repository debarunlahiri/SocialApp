package com.lahiriproductions.socialapp.AppInterface;

public interface ApiInterface {

    String BASE_URL = "http://www.expertfiling.in/";

    String API_GET_EVENTS = "index/event";
    String API_GET_RINGTONE = "index/api_mobile/ring";
    String API_INSERT_NOTIFICATION = "index/api_mobile/insert_notification";
    String API_GET_USER_STATUS = BASE_URL + "index/api_mobile/user_status";
    String API_INSERT_USER = BASE_URL + "index/api_mobile/insert";
    String API_GET_RADIO = BASE_URL + "index/api_mobile/radio_name";
}
