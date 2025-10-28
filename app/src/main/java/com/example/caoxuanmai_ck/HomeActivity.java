package com.example.caoxuanmai_ck;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caoxuanmai_ck.core.Session;
import com.example.caoxuanmai_ck.data.MovieDto;
import com.example.caoxuanmai_ck.net.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    TextView tvHello;
    RecyclerView rvMovies;
    Button btnTickets, btnAdmin, btnLogout;
    MoviesAdapter adapter;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_home);

        // Nếu chưa có token -> quay lại màn đăng nhập (tránh vào Home rỗng gây crash)
        String token = Session.token(this);
        if (token == null || token.isEmpty()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        // Ánh xạ view
        tvHello    = findViewById(R.id.tvHello);
        rvMovies   = findViewById(R.id.rvMovies);
        btnTickets = findViewById(R.id.btnTickets);
        btnAdmin   = findViewById(R.id.btnAdmin);
        btnLogout  = findViewById(R.id.btnLogout);

        // Nếu layout thiếu ID nào đó -> thoát an toàn để tránh NPE
        if (tvHello == null || rvMovies == null || btnTickets == null || btnAdmin == null || btnLogout == null) {
            Toast.makeText(this, "activity_home.xml thiếu ID. Dán đúng file layout mình gửi.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Chào tên
        String name = Session.name(this);
        tvHello.setText(name != null && !name.isEmpty() ? "Xin chào, " + name : "Xin chào");

        // Ẩn/hiện Admin theo role
        String role = Session.role(this);
        btnAdmin.setVisibility("ADMIN".equals(role) ? View.VISIBLE : View.GONE);

        // RecyclerView phim
        rvMovies.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new MoviesAdapter(new ArrayList<>(), m -> {
            Intent i = new Intent(HomeActivity.this, MovieDetailActivity.class);
            i.putExtra("id", m.id);
            startActivity(i);
        });
        rvMovies.setAdapter(adapter);

        // Nút Vé của tôi
        btnTickets.setOnClickListener(v -> {
            try {
                startActivity(new Intent(HomeActivity.this, TicketsActivity.class));
            } catch (Exception e) {
                Toast.makeText(HomeActivity.this, "Mục Vé của tôi sẽ có sau", Toast.LENGTH_SHORT).show();
            }
        });

        // Nút Admin
        btnAdmin.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, AdminActivity.class)));

        // Nút Đăng xuất
        btnLogout.setOnClickListener(v -> {
            Session.clear(HomeActivity.this);
            Intent i = new Intent(HomeActivity.this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        });

        // Tải danh sách phim
        loadMovies();
    }

    void loadMovies() {
        ApiClient.get(this).movies().enqueue(new Callback<List<MovieDto>>() {
            @Override public void onResponse(Call<List<MovieDto>> c, Response<List<MovieDto>> r) {
                if (!r.isSuccessful() || r.body() == null) { toast("Lỗi tải phim"); return; }
                adapter.setData(r.body());
            }
            @Override public void onFailure(Call<List<MovieDto>> c, Throwable t) { toast(t.getMessage()); }
        });
    }

    void toast(String s){ Toast.makeText(this, s, Toast.LENGTH_SHORT).show(); }
}
