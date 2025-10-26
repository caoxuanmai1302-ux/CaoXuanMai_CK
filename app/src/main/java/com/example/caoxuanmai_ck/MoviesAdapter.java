package com.example.caoxuanmai_ck;


import android.view.*; import android.widget.*; import androidx.cardview.widget.CardView; import androidx.recyclerview.widget.RecyclerView; import com.bumptech.glide.Glide; import com.example.caoxuanmai_ck.data.MovieDto; import java.util.*;


public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.VH> {
    public interface OnClick { void go(MovieDto m); }
    private final List<MovieDto> data; private final OnClick onClick;
    public MoviesAdapter(List<MovieDto> d, OnClick oc){ data=d; onClick=oc; }
    public void setData(List<MovieDto> d){ data.clear(); data.addAll(d); notifyDataSetChanged(); }


    @Override public VH onCreateViewHolder(ViewGroup p, int v){ View view=LayoutInflater.from(p.getContext()).inflate(R.layout.item_movie, p, false); return new VH(view); }
    @Override public void onBindViewHolder(VH h, int i){ MovieDto m=data.get(i);
        h.tvTitle.setText(m.title); h.tvGenre.setText(m.genre+" • "+m.durationMin+"p");
        String url = (m.poster!=null && (m.poster.startsWith("http://")||m.poster.startsWith("https://"))) ? m.poster : "https://picsum.photos/seed/"+m.id+"/600/800";
        Glide.with(h.img.getContext()).load(url).into(h.img);
        h.card.setOnClickListener(v-> onClick.go(m));
    }
    @Override public int getItemCount(){ return data.size(); }


    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvGenre; CardView card; ImageView img;
        VH(View v){ super(v); tvTitle=v.findViewById(R.id.tvTitle); tvGenre=v.findViewById(R.id.tvGenre); card=v.findViewById(R.id.cardMovie); img=v.findViewById(R.id.imgPoster); }
    }
}