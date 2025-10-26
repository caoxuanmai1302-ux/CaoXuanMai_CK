package com.example.caoxuanmai_ck.net;


import android.content.Context;


import com.example.caoxuanmai_ck.core.Session;


import java.util.concurrent.TimeUnit;


import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiClient {
    private static ApiService inst;


    public static ApiService get(Context c){
        if(inst == null){
            HttpLoggingInterceptor log = new HttpLoggingInterceptor();
            log.setLevel(HttpLoggingInterceptor.Level.BODY);


            Interceptor auth = chain -> {
                Request o = chain.request();
                String t = Session.token(c);
                if(t != null){
                    Request n = o.newBuilder().addHeader("Authorization", "Bearer "+t).build();
                    return chain.proceed(n);
                }
                return chain.proceed(o);
            };


            OkHttpClient ok = new OkHttpClient.Builder()
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .addInterceptor(auth)
                    .addInterceptor(log)
                    .build();


            Retrofit r = new Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:3000/api/")
                    .client(ok)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            inst = r.create(ApiService.class);
        }
        return inst;
    }
}