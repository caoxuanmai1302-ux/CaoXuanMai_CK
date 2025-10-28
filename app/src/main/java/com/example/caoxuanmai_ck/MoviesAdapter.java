package com.example.caoxuanmai_ck;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.caoxuanmai_ck.data.MovieDto;

import java.util.ArrayList;
import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.VH> {

    public interface OnClick { void go(MovieDto m); }

    private final List<MovieDto> data = new ArrayList<>();
    private final OnClick onClick;

    public MoviesAdapter(OnClick oc) {
        this.onClick = oc;
        setHasStableIds(true);
    }

    public void setData(List<MovieDto> d){
        data.clear();
        if (d != null) data.addAll(d);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie_row, parent, false); // dùng layout row
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int i) {
        MovieDto m = data.get(i);

        h.tvTitle.setText(m.title != null ? m.title : "");
        // subtitle: "Thể loại • 120p"
        String genre = m.genre != null ? m.genre : "";
        String dur   = (m.durationMin > 0) ? (m.durationMin + "p") : "";
        String dot   = (!genre.isEmpty() && !dur.isEmpty()) ? " • " : "";
        h.tvGenre.setText(genre + dot + dur);

        // poster fallback
        String url = (m.poster != null && (m.poster.startsWith("http://") || m.poster.startsWith("https://")))
                ? m.poster
                : "https://picsum.photos/seed/" + (m.id > 0 ? m.id : i) + "/400/600";
        Glide.with(h.img.getContext()).load(url).into(h.img);

        h.itemView.setOnClickListener(v -> { if (onClick != null) onClick.go(m); });
    }

    @Override public int getItemCount() { return data.size(); }

    // id là primitive long → trả về luôn (nếu id <=0 thì dùng position tránh trùng)
    @Override public long getItemId(int position) {
        MovieDto m = data.get(position);
        return (m.id > 0 ? m.id : position);
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView img;
        TextView tvTitle, tvGenre; // tvYear đã bỏ
        VH(@NonNull View v) {
            super(v);
            img = v.findViewById(R.id.imgPoster);
            tvTitle = v.findViewById(R.id.tvTitle);
            tvGenre = v.findViewById(R.id.tvGenre);
        }
    }
}
