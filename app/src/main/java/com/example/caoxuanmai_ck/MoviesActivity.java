package com.example.caoxuanmai_ck;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caoxuanmai_ck.data.MovieDto;
import com.example.caoxuanmai_ck.net.ApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoviesActivity extends AppCompatActivity {
    RecyclerView rv;
    MoviesAdapter adapter;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_movies);

        rv = findViewById(R.id.rvMovies);
        rv.setLayoutManager(new LinearLayoutManager(this)); // danh sách dọc

        adapter = new MoviesAdapter(m -> {
            Intent i = new Intent(MoviesActivity.this, MovieDetailActivity.class);
            i.putExtra("id", m.id);
            startActivity(i);
        });
        rv.setAdapter(adapter);

        ApiClient.get(this).movies().enqueue(new Callback<List<MovieDto>>() {
            @Override public void onResponse(Call<List<MovieDto>> call, Response<List<MovieDto>> resp) {
                if (resp.isSuccessful() && resp.body() != null) {
                    adapter.setData(resp.body());
                } else {
                    Toast.makeText(MoviesActivity.this, "Lỗi tải phim", Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(Call<List<MovieDto>> call, Throwable t) {
                Toast.makeText(MoviesActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
