package com.example.caoxuanmai_ck.data;

import com.google.gson.annotations.SerializedName;

public class SeatCell {
    public long id;
    public int rowIndex;
    public int colIndex;
    @SerializedName("isVip") public boolean vip;
    public String code;

    // client-only
    public boolean taken;
    public boolean selected;
}
