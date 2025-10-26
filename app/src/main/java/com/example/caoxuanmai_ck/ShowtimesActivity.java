package com.example.caoxuanmai_ck;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caoxuanmai_ck.data.ShowtimeDto;
import com.example.caoxuanmai_ck.net.ApiClient;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowtimesActivity extends AppCompatActivity {
    long movieId;
    RecyclerView rv;
    STAdapter adapter;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_showtimes);

        movieId = getIntent().getLongExtra("movieId", 0);

        rv = findViewById(R.id.rvShowtimes);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new STAdapter(new ArrayList<>(), st -> {
            Intent i = new Intent(ShowtimesActivity.this, SeatActivity.class);
            i.putExtra("showId", st.id);
            startActivity(i);
        });
        rv.setAdapter(adapter);

        ApiClient.get(this).showtimesByMovie(movieId)
                .enqueue(new Callback<List<ShowtimeDto>>() {
                    @Override
                    public void onResponse(Call<List<ShowtimeDto>> c, Response<List<ShowtimeDto>> r) {
                        if (r.isSuccessful() && r.body() != null) {
                            adapter.setData(r.body());
                        } else {
                            Toast.makeText(ShowtimesActivity.this, "Lỗi tải suất", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ShowtimeDto>> c, Throwable t) {
                        Toast.makeText(ShowtimesActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    static class STAdapter extends RecyclerView.Adapter<STAdapter.VH> {
        interface Click { void go(ShowtimeDto st); }

        private final NumberFormat vnd = NumberFormat.getCurrencyInstance(new Locale("vi","VN"));
        List<ShowtimeDto> data;
        Click click;

        STAdapter(List<ShowtimeDto> d, Click c) { this.data = d; this.click = c; }
        void setData(List<ShowtimeDto> d) { this.data = d; notifyDataSetChanged(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView txtTitle;
            VH(View v) {
                super(v);
                txtTitle = v.findViewById(R.id.txtTitle);
            }
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_showtime, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            ShowtimeDto st = data.get(position);
            // Giả sử st.startAt là chuỗi giờ, và st.price là số
            holder.txtTitle.setText("Suất " + st.id + " • " + st.startAt + " • " + vnd.format(st.price));
            holder.itemView.setOnClickListener(view -> click.go(st));
        }

        @Override
        public int getItemCount() { return (data == null) ? 0 : data.size(); }
    }
}
