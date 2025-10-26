package com.example.caoxuanmai_ck;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import com.example.caoxuanmai_ck.data.BookingCreate;
import com.example.caoxuanmai_ck.data.BookingCreated;
import com.example.caoxuanmai_ck.data.GenericResp;
import com.example.caoxuanmai_ck.data.SeatCell;
import com.example.caoxuanmai_ck.data.SeatsResp;
import com.example.caoxuanmai_ck.net.ApiClient;
import com.example.caoxuanmai_ck.net.ApiService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SeatActivity extends AppCompatActivity {

    long showId;
    double price;
    GridLayout grid;
    Button btnBook;
    TextView tvTotal;

    final Set<String> picked = new HashSet<>();
    final Set<String> taken  = new HashSet<>();

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_seat);

        showId = getIntent().getLongExtra("showId", 0);
        price  = getIntent().getDoubleExtra("price", 0);

        grid    = findViewById(R.id.gridSeats);
        btnBook = findViewById(R.id.btnBook);
        tvTotal = findViewById(R.id.tvTotal);

        btnBook.setOnClickListener(v -> createBooking());
        load();
    }

    void load() {
        ApiClient.get(this).seats(showId).enqueue(new Callback<SeatsResp>() {
            @Override public void onResponse(Call<SeatsResp> c, Response<SeatsResp> r) {
                if (!r.isSuccessful() || r.body() == null) { toast("Lỗi load ghế"); return; }
                render(r.body());
            }
            @Override public void onFailure(Call<SeatsResp> c, Throwable t) { toast(t.getMessage()); }
        });
    }

    void render(SeatsResp s) {
        grid.removeAllViews();
        grid.setColumnCount(s.cols);

        taken.clear();
        if (s.taken != null) taken.addAll(s.taken);

        // Tạo nút ghế theo tọa độ
        for (SeatCell cell : s.seats) {
            Button b = new Button(this);
            b.setText(cell.code);
            b.setBackgroundResource(R.drawable.bg_seat_state); // selector VIP / picked / taken
            b.setAllCaps(false);
            b.setPadding(16, 16, 16, 16);

            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = 0;
            lp.height = GridLayout.LayoutParams.WRAP_CONTENT;
            lp.columnSpec = GridLayout.spec(cell.colIndex, 1f);
            lp.rowSpec    = GridLayout.spec(cell.rowIndex);
            b.setLayoutParams(lp);

            boolean isTaken = taken.contains(cell.code);
            b.setEnabled(!isTaken);
            b.setAlpha(isTaken ? 0.8f : 1f);
            b.setSelected(false); // chưa chọn

            b.setOnClickListener(v -> {
                if (picked.contains(cell.code)) {
                    picked.remove(cell.code);
                    v.setSelected(false);
                } else {
                    picked.add(cell.code);
                    v.setSelected(true);
                }
                updateTotal();
            });

            grid.addView(b);
        }
        updateTotal();
    }

    void updateTotal() {
        double total = price * picked.size();
        tvTotal.setText("Tổng: " + MoneyJava.vnd(total));
        btnBook.setEnabled(!picked.isEmpty());
    }

    void createBooking() {
        if (picked.isEmpty()) { toast("Chọn ghế trước"); return; }

        ApiService api = ApiClient.get(this);
        BookingCreate req = new BookingCreate(showId, new ArrayList<>(picked));

        api.createBooking(req).enqueue(new Callback<BookingCreated>() {
            @Override public void onResponse(Call<BookingCreated> c, Response<BookingCreated> r) {
                BookingCreated b = r.body();
                if (!r.isSuccessful() || b == null || b.id == 0) {
                    toast(b != null && b.error != null ? b.error : "Đặt vé lỗi");
                    return;
                }
                // Thanh toán giả lập ngay
                api.pay(b.id, new GenericResp()).enqueue(new Callback<GenericResp>() {
                    @Override public void onResponse(Call<GenericResp> c2, Response<GenericResp> r2) {
                        toast("Thanh toán thành công");
                        finish(); // quay lại
                    }
                    @Override public void onFailure(Call<GenericResp> c2, Throwable t) { toast(t.getMessage()); }
                });
            }
            @Override public void onFailure(Call<BookingCreated> c, Throwable t) { toast(t.getMessage()); }
        });
    }

    void toast(String s) { Toast.makeText(this, s, Toast.LENGTH_SHORT).show(); }
}
