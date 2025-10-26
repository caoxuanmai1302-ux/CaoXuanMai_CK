package com.example.caoxuanmai_ck.data;

public class LoginReq {
    public String login;
    public String password;

    public LoginReq() { }

    public LoginReq(String l, String p) {
        this.login = l;
        this.password = p;
    }
}