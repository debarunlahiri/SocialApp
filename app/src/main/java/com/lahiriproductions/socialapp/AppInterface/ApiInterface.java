package com.lahiriproductions.socialapp.AppInterface;

public interface ApiInterface {

    String BASE_URL = "http://calimak.com/takitym/app/api_mobile/";

    String API_GET_EVENTS = "event";
    String API_GET_RINGTONE = "ring";
    String API_GET_RINGTONE_1 = "ring1";
    String API_GET_RINGTONE_2 = "ring2";
    String API_GET_RINGTONE_3 = "ring3";
    String API_INSERT_NOTIFICATION = "notification";
    String API_GET_USER_STATUS = BASE_URL + "user_status";
    String API_INSERT_USER = BASE_URL + "insert";
    String API_GET_RADIO = BASE_URL + "radio_name";

    String DEFAULT_PROFILE_PIC = "https://icon-library.com/images/default-profile-icon/default-profile-icon-6.jpg";
}
