package com.example.myevstation_android;

public class Station {
    String id;
    String statNm;
    String addr;
    String chgerType;
    String useTime;
    String busiNm;
    String busiCall;

    public Station(String id, String statNm, String addr, String chgerType,
                   String useTime, String busiNm, String busiCall) {
        this.id = id;
        this.statNm = statNm;
        this.addr = addr;
        this.chgerType = chgerType;
        this.useTime = useTime;
        this.busiNm = busiNm;
        this.busiCall = busiCall;
    }
}
