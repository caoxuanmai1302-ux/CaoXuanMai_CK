package com.example.caoxuanmai_ck.net;


import com.example.caoxuanmai_ck.data.*;


import java.util.List;


import retrofit2.Call;
import retrofit2.http.*;


public interface ApiService {
    @GET("health")
    Call<GenericResp> health();


    @POST("auth/register")
    Call<LoginResp> register(@Body RegisterReq req);


    @POST("auth/login")
    Call<LoginResp> login(@Body LoginReq req);


    @GET("movies")
    Call<List<MovieDto>> movies();


    @GET("movies/{id}")
    Call<MovieDetailResp> movieDetail(@Path("id") long id);


    @GET("showtimes/{id}/seats")
    Call<SeatsResp> seats(@Path("id") long showId);


    @POST("bookings")
    Call<BookingCreated> createBooking(@Body BookingCreate req);


    @POST("bookings/{id}/pay")
    Call<GenericResp> pay(@Path("id") long id, @Body GenericResp req);


    @GET("bookings/me")
    Call<List<BookingDto>> myBookings();


    // Admin
    @POST("admin/movies")
    Call<MovieDto> adminAddMovie(@Body MovieDto req);


    @POST("admin/showtimes")
    Call<ShowtimeDto> adminAddShowtime(@Body ShowtimeDto req);


    @POST("admin/showtimes/{id}/lock")
    Call<GenericResp> adminLock(@Path("id") long id, @Body GenericResp req);
}