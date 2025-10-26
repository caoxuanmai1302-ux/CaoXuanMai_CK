package com.example.caoxuanmai_ck.data;

import com.google.gson.annotations.SerializedName;

public class SeatCell {
    public long id;
    public int rowIndex;
    public int colIndex;

    // Map từ JSON "isVip" của API sang biến "vip" trong app
    @SerializedName("isVip")
    public boolean vip;

    public String code;

    // Client-only (không đến từ server)
    public boolean taken;    // đã bị giữ/đặt
    public boolean selected; // người dùng đang chọn
}
