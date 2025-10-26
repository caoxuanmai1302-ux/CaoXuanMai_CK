package com.example.caoxuanmai_ck.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.caoxuanmai_ck.R;
import com.example.caoxuanmai_ck.net.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// DTO mẫu — sửa theo API thực tế
class TicketDto {
    public long id;
    public String movieTitle;
    public String showtime; // "2025-10-23 19:30"
    public List<String> seats;
    public double amount;
}

// Adapter tối giản hiển thị vé
class TicketsAdapter extends RecyclerView.Adapter<TicketsAdapter.VH> {
    static class VH extends RecyclerView.ViewHolder {
        TextView t1, t2, t3, t4;
        VH(@NonNull View v){
            super(v);
            t1 = v.findViewById(android.R.id.text1);
            t2 = v.findViewById(android.R.id.text2);
            t3 = v.findViewById(R.id.text3);
            t4 = v.findViewById(R.id.text4);
        }
    }
    private final List<TicketDto> data = new ArrayList<>();
    void setData(List<TicketDto> d){ data.clear(); if(d!=null) data.addAll(d); notifyDataSetChanged(); }

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int vType){
        // layout đơn: 2 dòng sẵn + 2 dòng phụ
        View v = LayoutInflater.from(p.getContext()).inflate(R.layout.item_ticket_simple, p, false);
        return new VH(v);
    }
    @Override public void onBindViewHolder(@NonNull VH h, int i){
        TicketDto t = data.get(i);
        h.t1.setText(t.movieTitle);
        h.t2.setText("Suất: " + t.showtime);
        h.t3.setText("Ghế: " + (t.seats==null? "-" : t.seats.toString()));
        h.t4.setText("Tổng: " + com.example.caoxuanmai_ck.MoneyJava.vnd(t.amount));
    }
    @Override public int getItemCount(){ return data.size(); }
}

public class TicketsFragment extends Fragment {

    private RecyclerView rv;
    private TextView tvEmpty;
    private TicketsAdapter adapter;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tickets, container, false);
    }

    @Override public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        super.onViewCreated(v, s);
        rv = v.findViewById(R.id.rvTickets);
        tvEmpty = v.findViewById(R.id.tvEmpty);

        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        adapter = new TicketsAdapter();
        rv.setAdapter(adapter);

        // TODO: thay bằng ApiClient.get(...).myBookings()
        // Mình demo dùng mock; khi có API, gọi enqueue rồi setData
        loadMock();
    }

    private void loadMock(){
        List<TicketDto> list = new ArrayList<>();
        TicketDto t = new TicketDto();
        t.id = 1;
        t.movieTitle = "Hành Tinh Diệu Kỳ";
        t.showtime = "2025-10-24 19:30";
        t.seats = java.util.Arrays.asList("B3","B4");
        t.amount = 220000;
        list.add(t);

        adapter.setData(list);
        tvEmpty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
