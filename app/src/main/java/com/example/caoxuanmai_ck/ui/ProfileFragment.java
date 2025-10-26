package com.example.caoxuanmai_ck.ui;

import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.caoxuanmai_ck.MainActivity;
import com.example.caoxuanmai_ck.R;

public class ProfileFragment extends Fragment {

    private TextView tvUser, tvEmail;
    private Button btnLogout;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        super.onViewCreated(v, s);
        tvUser   = v.findViewById(R.id.tvUser);
        tvEmail  = v.findViewById(R.id.tvEmail);
        btnLogout= v.findViewById(R.id.btnLogout);

        // Đọc thông tin user từ Session/SharedPreferences
        // Ví dụ:
        String name = getPref("user_name", "User");
        String email = getPref("user_email", "-");
        tvUser.setText("Xin chào, " + name);
        tvEmail.setText("Email: " + email);

        btnLogout.setOnClickListener(v1 -> {
            // Xoá token / clear session
            clearAuth();
            // Về màn đăng nhập chính
            Intent i = new Intent(requireContext(), MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            requireActivity().finish();
        });
    }

    private String getPref(String k, String def){
        android.content.SharedPreferences sp =
                requireContext().getSharedPreferences("app_prefs", 0);
        return sp.getString(k, def);
    }

    private void clearAuth(){
        android.content.SharedPreferences sp =
                requireContext().getSharedPreferences("app_prefs", 0);
        sp.edit()
                .remove("auth_token")
                .remove("user_name")
                .remove("user_email")
                .apply();
    }
}
