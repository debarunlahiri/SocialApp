
package com.lahiriproductions.socialapp.models.UserStatus;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserStatus {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private UserStatusData userStatusData;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserStatusData getData() {
        return userStatusData;
    }

    public void setData(UserStatusData userStatusData) {
        this.userStatusData = userStatusData;
    }

}
