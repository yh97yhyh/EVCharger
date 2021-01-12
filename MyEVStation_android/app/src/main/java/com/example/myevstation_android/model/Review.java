package com.example.myevstation_android.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Review {

    @SerializedName("id")
    @Expose
    private String statId;

    @SerializedName("userNick")
    @Expose
    private String nickname;

    @SerializedName("contents")
    @Expose
    private String contents;

    public String getStatId() {
        return statId;
    }

    public String getNickname() {
        return nickname;
    }

    public String getContents() {
        return contents;
    }
}
