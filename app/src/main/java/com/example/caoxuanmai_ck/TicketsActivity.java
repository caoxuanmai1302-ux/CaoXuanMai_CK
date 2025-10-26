package com.example.caoxuanmai_ck;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.caoxuanmai_ck.core.Session;

public class TicketsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_tickets);

        TextView tv = findViewById(R.id.tvInfo);
        String name = Session.name(this);
        tv.setText((name != null && !name.isEmpty())
                ? "Vé của " + name + " sẽ hiển thị tại đây."
                : "Vé của tôi");
    }
}
