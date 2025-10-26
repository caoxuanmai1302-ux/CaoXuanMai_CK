package com.example.caoxuanmai_ck;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caoxuanmai_ck.data.SeatCell;

import java.util.ArrayList;
import java.util.List;

public class SeatAdapter extends RecyclerView.Adapter<SeatAdapter.VH>{
    public interface Changed { void onChanged(); }
    private final List<SeatCell> data;
    private final Changed changed;

    public SeatAdapter(List<SeatCell> d, Changed c){
        data = (d != null) ? d : new ArrayList<>();
        changed = c;
        setHasStableIds(true);
    }

    static class VH extends RecyclerView.ViewHolder{
        TextView t;
        VH(@NonNull View v){ super(v); t = v.findViewById(R.id.txtCode); }
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p, int vt){
        return new VH(LayoutInflater.from(p.getContext())
                .inflate(R.layout.item_seat, p, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int i){
        SeatCell s = data.get(i);
        h.t.setText(s.code);

        // Nền: VIP hay thường
        h.itemView.setBackgroundResource(
                s.vip ? R.drawable.bg_seat_state_vip : R.drawable.bg_seat_state_normal
        );

        // Trạng thái enable/selected điều khiển selector
        h.itemView.setEnabled(!s.taken);
        h.itemView.setSelected(s.selected);

        // Màu text tùy taken
        h.t.setTextColor(s.taken ? 0xFF666666 : 0xFF222222);

        // Click chọn/bỏ
        h.itemView.setOnClickListener(v -> {
            if (s.taken) return;
            s.selected = !s.selected;
            notifyItemChanged(h.getBindingAdapterPosition());
            if (changed != null) changed.onChanged();
        });
    }

    @Override public int getItemCount(){ return data.size(); }

    @Override public long getItemId(int position) { return data.get(position).code.hashCode(); }

    public List<String> selectedCodes(){
        ArrayList<String> out = new ArrayList<>();
        for (SeatCell s : data) if (s.selected) out.add(s.code);
        return out;
    }

    public int takenCount(){
        int c = 0; for (SeatCell s : data) if (s.taken) c++; return c;
    }

    public List<SeatCell> getAll() { return data; }
}
