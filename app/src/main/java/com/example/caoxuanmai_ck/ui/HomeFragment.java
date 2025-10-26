package com.example.caoxuanmai_ck.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caoxuanmai_ck.MoviesAdapter;
import com.example.caoxuanmai_ck.R;
import com.example.caoxuanmai_ck.ShowtimesActivity;
import com.example.caoxuanmai_ck.data.MovieDto;
import com.example.caoxuanmai_ck.net.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView rv;
    private MoviesAdapter adapter;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        rv = v.findViewById(R.id.rvMovies);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new MoviesAdapter(new ArrayList<>(), m -> {
            Intent i = new Intent(requireContext(), ShowtimesActivity.class);
            i.putExtra("movieId", m.id);
            startActivity(i);
        });
        rv.setAdapter(adapter);

        ApiClient.get(requireContext()).movies().enqueue(new Callback<List<MovieDto>>() {
            @Override public void onResponse(Call<List<MovieDto>> call, Response<List<MovieDto>> resp) {
                if (resp.isSuccessful() && resp.body() != null) {
                    adapter.setData(resp.body());
                } else {
                    Toast.makeText(requireContext(), "Lỗi tải phim", Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(Call<List<MovieDto>> call, Throwable t) {
                Toast.makeText(requireContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
