package com.example.caoxuanmai_ck.core;

import android.content.Context;
import android.content.SharedPreferences;

public class Session {
    private static final String SP = "cinema_session";
    private static final String K_TOKEN = "token";
    private static final String K_ROLE = "role";
    private static final String K_NAME = "name";
    private static final String K_UID = "uid";

    public static void save(Context c, String token, String role, String name, long userId){
        SharedPreferences sp = c.getSharedPreferences(SP, Context.MODE_PRIVATE);
        sp.edit()
                .putString(K_TOKEN, token)
                .putString(K_ROLE, role)
                .putString(K_NAME, name)
                .putLong(K_UID, userId)
                .apply();
    }

    public static String token(Context c){
        return c.getSharedPreferences(SP, Context.MODE_PRIVATE).getString(K_TOKEN, null);
    }

    public static String role(Context c){
        return c.getSharedPreferences(SP, Context.MODE_PRIVATE).getString(K_ROLE, null);
    }

    public static String name(Context c){
        return c.getSharedPreferences(SP, Context.MODE_PRIVATE).getString(K_NAME, null);
    }

    public static long userId(Context c){
        return c.getSharedPreferences(SP, Context.MODE_PRIVATE).getLong(K_UID, 0L);
    }

    public static void clear(Context c){
        c.getSharedPreferences(SP, Context.MODE_PRIVATE).edit().clear().apply();
    }
}
