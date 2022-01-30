
package com.lahiriproductions.socialapp.events;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Events {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("response")
    @Expose
    private String response;
    @SerializedName("data")
    @Expose
    private List<com.lahiriproductions.socialapp.events.EventsData> data = null;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public List<com.lahiriproductions.socialapp.events.EventsData> getData() {
        return data;
    }

    public void setData(List<com.lahiriproductions.socialapp.events.EventsData> data) {
        this.data = data;
    }

}
