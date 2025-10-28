package com.example.caoxuanmai_ck;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caoxuanmai_ck.data.BookingCreate;
import com.example.caoxuanmai_ck.data.BookingCreated;
import com.example.caoxuanmai_ck.data.GenericResp;
import com.example.caoxuanmai_ck.data.SeatCell;
import com.example.caoxuanmai_ck.data.SeatsResp;
import com.example.caoxuanmai_ck.net.ApiClient;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SeatActivity extends AppCompatActivity {

    long showId;
    double price;

    RecyclerView rvSeats;
    Button btnBook;
    TextView tvTotal;

    SeatAdapter seatAdapter;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_seat);

        showId = getIntent().getLongExtra("showId", 0);
        price  = getIntent().getDoubleExtra("price", 0);

        rvSeats  = findViewById(R.id.rvSeats);   // <-- KHỚP layout
        btnBook  = findViewById(R.id.btnBook);
        tvTotal  = findViewById(R.id.tvTotal);

        btnBook.setOnClickListener(v -> createBooking());

        loadSeats();
    }

    void loadSeats() {
        ApiClient.get(this).seats(showId).enqueue(new Callback<SeatsResp>() {
            @Override public void onResponse(Call<SeatsResp> c, Response<SeatsResp> r) {
                if (!r.isSuccessful() || r.body() == null) { toast("Lỗi tải ghế"); return; }
                SeatsResp resp = r.body();

                // ghép trạng thái taken từ danh sách code
                Set<String> takenSet = new HashSet<>();
                if (resp.taken != null) takenSet.addAll(resp.taken);
                for (SeatCell sc : resp.seats) sc.taken = takenSet.contains(sc.code);

                // grid theo số cột phòng chiếu
                rvSeats.setLayoutManager(new GridLayoutManager(SeatActivity.this, resp.cols));
                seatAdapter = new SeatAdapter(resp.seats, this::updateTotal);
                rvSeats.setAdapter(seatAdapter);

                updateTotal();
            }
            @Override public void onFailure(Call<SeatsResp> c, Throwable t) { toast(t.getMessage()); }

            // callback dùng cho SeatAdapter.Changed
            void updateTotal() { SeatActivity.this.updateTotal(); }
        });
    }

    void updateTotal() {
        int count = seatAdapter == null ? 0 : seatAdapter.selectedCodes().size();
        double total = price * count;
        tvTotal.setText("Tổng: " + MoneyJava.vnd(total));
        btnBook.setEnabled(count > 0);
    }

    void createBooking() {
        if (seatAdapter == null || seatAdapter.selectedCodes().isEmpty()) {
            toast("Chọn ghế trước");
            return;
        }
        ArrayList<String> codes = new ArrayList<>(seatAdapter.selectedCodes());

        // Tạo đơn
        BookingCreate req = new BookingCreate(showId, codes);
        ApiClient.get(this).createBooking(req).enqueue(new Callback<BookingCreated>() {
            @Override public void onResponse(Call<BookingCreated> c, Response<BookingCreated> r) {
                BookingCreated b = r.body();
                if (!r.isSuccessful() || b == null || b.id == 0) {
                    toast(b != null && b.error != null ? b.error : "Đặt vé lỗi"); return;
                }
                // Thanh toán giả lập
                ApiClient.get(SeatActivity.this).pay(b.id, new GenericResp()).enqueue(new Callback<GenericResp>() {
                    @Override public void onResponse(Call<GenericResp> c2, Response<GenericResp> r2) {
                        toast("Thanh toán thành công");
                        finish();
                    }
                    @Override public void onFailure(Call<GenericResp> c2, Throwable t) { toast(t.getMessage()); }
                });
            }
            @Override public void onFailure(Call<BookingCreated> c, Throwable t) { toast(t.getMessage()); }
        });
    }

    void toast(String s) { Toast.makeText(this, s, Toast.LENGTH_SHORT).show(); }
}
