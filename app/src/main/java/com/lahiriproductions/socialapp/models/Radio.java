package com.lahiriproductions.socialapp.models;

public class Radio {

    private String radio_name;
    private int radio_image;
    private String radio_stream_url;

    public Radio(String radio_name, int radio_image, String radio_stream_url) {
        this.radio_name = radio_name;
        this.radio_image = radio_image;
        this.radio_stream_url = radio_stream_url;
    }

    public String getRadio_name() {
        return radio_name;
    }

    public void setRadio_name(String radio_name) {
        this.radio_name = radio_name;
    }

    public String getRadio_stream_url() {
        return radio_stream_url;
    }

    public void setRadio_stream_url(String radio_stream_url) {
        this.radio_stream_url = radio_stream_url;
    }

    public int getRadio_image() {
        return radio_image;
    }

    public void setRadio_image(int radio_image) {
        this.radio_image = radio_image;
    }
}
