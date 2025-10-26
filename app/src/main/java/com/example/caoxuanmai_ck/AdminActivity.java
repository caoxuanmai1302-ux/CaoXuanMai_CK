package com.example.caoxuanmai_ck;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.caoxuanmai_ck.core.Session;
import com.example.caoxuanmai_ck.data.GenericResp;
import com.example.caoxuanmai_ck.data.MovieDto;
import com.example.caoxuanmai_ck.data.ShowtimeDto;
import com.example.caoxuanmai_ck.net.ApiClient;
import com.example.caoxuanmai_ck.net.ApiService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminActivity extends AppCompatActivity {

    // Movie inputs
    private EditText edtTitle, edtGenre, edtDur, edtDesc, edtPoster, edtAge;
    private Button btnAddMovie;

    // Showtime inputs
    private EditText edtMvId, edtAudId, edtStartAt, edtPrice;
    private Button btnAddShow, btnLock, btnUnlock, btnPickDate, btnPickTime;

    private final Calendar cal = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_admin);

        // Chặn nếu không phải ADMIN
        if (!"ADMIN".equals(Session.role(this))) {
            Toast.makeText(this, "Chỉ Admin được vào", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // --- Movie ---
        edtTitle  = findViewById(R.id.edtTitle);
        edtGenre  = findViewById(R.id.edtGenre);
        edtDur    = findViewById(R.id.edtDuration);
        edtDesc   = findViewById(R.id.edtDesc);
        edtPoster = findViewById(R.id.edtPoster);
        edtAge    = findViewById(R.id.edtAge);
        btnAddMovie = findViewById(R.id.btnAddMovie);

        // --- Showtime ---
        edtMvId    = findViewById(R.id.edtMvId);
        edtAudId   = findViewById(R.id.edtAudId);
        edtStartAt = findViewById(R.id.edtStartAt);
        edtPrice   = findViewById(R.id.edtPrice);

        btnAddShow  = findViewById(R.id.btnAddShow);
        btnLock     = findViewById(R.id.btnLock);
        btnUnlock   = findViewById(R.id.btnUnlock);
        btnPickDate = findViewById(R.id.btnPickDate);
        btnPickTime = findViewById(R.id.btnPickTime);

        // Clicks
        btnAddMovie.setOnClickListener(v -> doAddMovie());
        btnAddShow.setOnClickListener(v -> doAddShow());
        btnLock.setOnClickListener(v -> doLock(true));
        btnUnlock.setOnClickListener(v -> doLock(false));

        // Date picker
        btnPickDate.setOnClickListener(v -> {
            new android.app.DatePickerDialog(
                    this,
                    (view, y, m, d) -> {
                        cal.set(Calendar.YEAR, y);
                        cal.set(Calendar.MONTH, m);
                        cal.set(Calendar.DAY_OF_MONTH, d);
                        // Sau khi chọn giờ sẽ build chuỗi ISO vào edtStartAt
                    },
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        // Time picker
        btnPickTime.setOnClickListener(v -> {
            new android.app.TimePickerDialog(
                    this,
                    (view, h, min) -> {
                        cal.set(Calendar.HOUR_OF_DAY, h);
                        cal.set(Calendar.MINUTE, min);
                        cal.set(Calendar.SECOND, 0);

                        // Nếu server của bạn lưu DATETIME2 local -> giữ local
                        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                        // Nếu muốn gửi UTC có hậu tố Z, mở 2 dòng dưới:
                        // fmt.setTimeZone(TimeZone.getTimeZone("UTC"));
                        // và thêm "Z" vào cuối chuỗi nếu cần.
                        edtStartAt.setText(fmt.format(cal.getTime()));
                    },
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE),
                    true
            ).show();
        });
    }

    private void doAddMovie() {
        MovieDto m = new MovieDto();
        m.title = edtTitle.getText().toString().trim();
        m.genre = edtGenre.getText().toString().trim();

        try {
            m.durationMin = Integer.parseInt(edtDur.getText().toString().trim());
        } catch (Exception e) {
            m.durationMin = 90;
        }

        m.description = edtDesc.getText().toString().trim();
        m.poster = edtPoster.getText().toString().trim();

        try {
            m.ageRating = Integer.parseInt(edtAge.getText().toString().trim());
        } catch (Exception e) {
            m.ageRating = 13;
        }

        ApiClient.get(this).adminAddMovie(m).enqueue(new Callback<MovieDto>() {
            @Override public void onResponse(Call<MovieDto> c, Response<MovieDto> r) {
                toast(r.isSuccessful() ? "Đã thêm phim" : "Lỗi thêm phim");
            }
            @Override public void onFailure(Call<MovieDto> c, Throwable t) { toast(t.getMessage()); }
        });
    }

    private void doAddShow() {
        ShowtimeDto s = new ShowtimeDto();

        try {
            s.movieId = Long.parseLong(edtMvId.getText().toString().trim());
        } catch (Exception e) {
            toast("movieId không hợp lệ");
            return;
        }

        try {
            s.auditoriumId = Long.parseLong(edtAudId.getText().toString().trim());
        } catch (Exception e) {
            s.auditoriumId = 1;
        }

        s.startAt = edtStartAt.getText().toString().trim();

        try {
            s.price = Double.parseDouble(edtPrice.getText().toString().trim());
        } catch (Exception e) {
            s.price = 90000;
        }

        ApiClient.get(this).adminAddShowtime(s).enqueue(new Callback<ShowtimeDto>() {
            @Override public void onResponse(Call<ShowtimeDto> c, Response<ShowtimeDto> r) {
                toast(r.isSuccessful() ? "Đã thêm suất" : "Lỗi thêm suất");
            }
            @Override public void onFailure(Call<ShowtimeDto> c, Throwable t) { toast(t.getMessage()); }
        });
    }

    // Ghi chú: ở layout mình có note “nhập showtimeId vào ô movieId” để tận dụng 1 ô,
    // nếu bạn muốn tách ô riêng hãy thêm EditText khác.
    private void doLock(boolean on) {
        long showtimeId;
        try {
            showtimeId = Long.parseLong(edtMvId.getText().toString().trim());
        } catch (Exception e) {
            toast("Nhập showtimeId vào ô MovieId để lock/unlock (hoặc tạo ô riêng)");
            return;
        }
        GenericResp req = new GenericResp();
        req.locked = on;

        ApiClient.get(this).adminLock(showtimeId, req).enqueue(new Callback<GenericResp>() {
            @Override public void onResponse(Call<GenericResp> c, Response<GenericResp> r) {
                toast(r.isSuccessful() ? (on ? "Đã khóa suất" : "Đã mở khóa") : "Lỗi thao tác");
            }
            @Override public void onFailure(Call<GenericResp> c, Throwable t) { toast(t.getMessage()); }
        });
    }

    private void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}
