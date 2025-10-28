package com.example.caoxuanmai_ck;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.caoxuanmai_ck.data.MovieDetailResp;
import com.example.caoxuanmai_ck.data.ShowtimeDto;
import com.example.caoxuanmai_ck.net.ApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailActivity extends AppCompatActivity {

    private long id;
    private TextView tvTitle, tvDesc;
    private ImageView imgPoster;
    private RecyclerView rvShowtimes;
    private ShowtimesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        id = getIntent().getLongExtra("id", 0);

        tvTitle = findViewById(R.id.tvTitle);
        tvDesc = findViewById(R.id.tvDesc);
        imgPoster = findViewById(R.id.imgPoster);
        rvShowtimes = findViewById(R.id.rvShowtimes);

        rvShowtimes.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ShowtimesAdapter(List.of(), s -> {
            Intent i = new Intent(this, SeatActivity.class);
            i.putExtra("showId", s.id);
            i.putExtra("price", s.price);
            startActivity(i);
        });
        rvShowtimes.setAdapter(adapter);

        loadData();
    }

    private void loadData() {
        ApiClient.get(this).movieDetail(id).enqueue(new Callback<MovieDetailResp>() {
            @Override
            public void onResponse(Call<MovieDetailResp> call, Response<MovieDetailResp> res) {
                if (!res.isSuccessful() || res.body() == null) {
                    toast("Không tải được chi tiết phim");
                    return;
                }

                var body = res.body();
                tvTitle.setText(body.movie.title);
                tvDesc.setText(body.movie.description);

                String posterUrl = body.movie.poster;
                if (posterUrl == null || posterUrl.isEmpty() ||
                        (!posterUrl.startsWith("http://") && !posterUrl.startsWith("https://"))) {
                    posterUrl = "https://picsum.photos/seed/" + body.movie.id + "/800/400";
                }
                Glide.with(MovieDetailActivity.this).load(posterUrl).into(imgPoster);

                adapter.setData(body.showtimes);
            }

            @Override
            public void onFailure(Call<MovieDetailResp> call, Throwable t) {
                toast(t.getMessage());
            }
        });
    }

    private void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    // ------------------ Adapter phụ ------------------
    static class ShowtimesAdapter extends RecyclerView.Adapter<ShowtimesAdapter.VH> {
        interface OnClick { void pick(ShowtimeDto s); }
        private List<ShowtimeDto> data;
        private final OnClick click;

        ShowtimesAdapter(List<ShowtimeDto> d, OnClick c) {
            data = d;
            click = c;
        }

        void setData(List<ShowtimeDto> newData) {
            this.data = newData;
            notifyDataSetChanged();
        }

        @Override
        public VH onCreateViewHolder(android.view.ViewGroup p, int v) {
            android.view.View v1 = android.view.LayoutInflater.from(p.getContext())
                    .inflate(R.layout.item_showtime, p, false);
            return new VH(v1);
        }

        @Override
        public void onBindViewHolder(VH h, int i) {
            ShowtimeDto s = data.get(i);
            h.tv.setText(s.auditoriumName + " • " + s.startAt + " • " + MoneyJava.vnd(s.price));
            h.itemView.setOnClickListener(v -> click.pick(s));
        }

        @Override public int getItemCount() { return data != null ? data.size() : 0; }

        static class VH extends RecyclerView.ViewHolder {
            TextView tv;
            VH(android.view.View v) { super(v); tv = v.findViewById(R.id.tvST); }
        }
    }
}
