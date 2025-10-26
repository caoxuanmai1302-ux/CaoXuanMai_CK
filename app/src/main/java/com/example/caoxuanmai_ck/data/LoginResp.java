package com.example.caoxuanmai_ck.data;

import com.google.gson.annotations.SerializedName;

public class LoginResp {
    // Dạng phẳng: { "token": "...", "role": "ADMIN", "fullName": "...", "userId": 123 }
    @SerializedName(value = "token", alternate = {"accessToken", "jwt"})
    public String token;
    public String role;
    public String fullName;
    public Long userId;

    // Dạng lồng: { "token": "...", "user": { "id": 123, "fullName": "...", "role": "ADMIN" } }
    public User user;

    public String error; // nếu API trả lỗi

    public static class User {
        public Long id;
        public String fullName;
        public String role;
    }

    // ---- Getters an toàn cho cả 2 dạng ----
    public String getToken()    { return token; }
    public String getRole()     { return role != null ? role : (user != null ? user.role : null); }
    public String getFullName() { return fullName != null ? fullName : (user != null ? user.fullName : null); }
    public Long   getUserId()   { return userId != null ? userId : (user != null ? user.id : null); }
}
