package com.example.caoxuanmai_ck;


import android.content.Intent; import android.os.Bundle; import android.widget.*; import androidx.appcompat.app.AppCompatActivity; import androidx.recyclerview.widget.*; import com.bumptech.glide.Glide; import com.example.caoxuanmai_ck.data.*; import com.example.caoxuanmai_ck.net.*; import java.util.*; import retrofit2.*;


public class MovieDetailActivity extends AppCompatActivity {
    long id; TextView tvTitle, tvDesc; RecyclerView rv; ImageView imgPoster;
    @Override protected void onCreate(Bundle b){ super.onCreate(b); setContentView(R.layout.activity_movie_detail);
        id=getIntent().getLongExtra("id",0);
        imgPoster=findViewById(R.id.imgPoster); tvTitle=findViewById(R.id.tvTitle); tvDesc=findViewById(R.id.tvDesc); rv=findViewById(R.id.rvShowtimes);
        rv.setLayoutManager(new LinearLayoutManager(this));
        ApiClient.get(this).movieDetail(id).enqueue(new Callback<MovieDetailResp>(){
            @Override public void onResponse(Call<MovieDetailResp> c, Response<MovieDetailResp> r){
                if(!r.isSuccessful()||r.body()==null){ toast("Lỗi"); return; }
                tvTitle.setText(r.body().movie.title); tvDesc.setText(r.body().movie.description);
                String url = (r.body().movie.poster!=null && (r.body().movie.poster.startsWith("http://")||r.body().movie.poster.startsWith("https://"))) ? r.body().movie.poster : "https://picsum.photos/seed/"+r.body().movie.id+"/800/400";
                Glide.with(MovieDetailActivity.this).load(url).into(imgPoster);
                rv.setAdapter(new ShowtimesAdapter(r.body().showtimes, st -> { Intent i=new Intent(MovieDetailActivity.this, SeatActivity.class); i.putExtra("showId", st.id); i.putExtra("price", st.price); startActivity(i); }));
            }
            @Override public void onFailure(Call<MovieDetailResp> c, Throwable t){ toast(t.getMessage()); }
        });
    }
    void toast(String s){ Toast.makeText(this, s, Toast.LENGTH_SHORT).show(); }


    static class ShowtimesAdapter extends RecyclerView.Adapter<ShowtimesAdapter.VH>{
        interface OnClick{ void pick(ShowtimeDto s); }
        java.util.List<ShowtimeDto> data; OnClick oc; ShowtimesAdapter(java.util.List<ShowtimeDto> d, OnClick o){data=d;oc=o;}
        @Override public VH onCreateViewHolder(android.view.ViewGroup p, int v){ return new VH(android.view.LayoutInflater.from(p.getContext()).inflate(R.layout.item_showtime,p,false)); }
        @Override public void onBindViewHolder(VH h,int i){ ShowtimeDto s=data.get(i); h.tv.setText(s.auditoriumName+" • "+s.startAt+" • "+MoneyJava.vnd(s.price)); h.itemView.setOnClickListener(v->oc.pick(s)); }
        @Override public int getItemCount(){return data.size();}
        static class VH extends RecyclerView.ViewHolder{ TextView tv; VH(android.view.View v){super(v); tv=v.findViewById(R.id.tvST);} }
    }
}