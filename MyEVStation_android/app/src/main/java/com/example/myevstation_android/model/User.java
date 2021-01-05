package com.example.myevstation_android.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("uid")
    @Expose
    private String userId;

    @SerializedName("nickname")
    @Expose
    private String nickname;

    public String getUserId() {
        return userId;
    }

    public String getUserNickname() {
        return nickname;
    }

    public Integer getUserUid() {
        return id;
    }


}
