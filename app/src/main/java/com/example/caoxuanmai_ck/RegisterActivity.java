package com.example.caoxuanmai_ck;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.caoxuanmai_ck.core.Session;
import com.example.caoxuanmai_ck.data.LoginReq;
import com.example.caoxuanmai_ck.data.LoginResp;
import com.example.caoxuanmai_ck.data.RegisterReq;
import com.example.caoxuanmai_ck.net.ApiClient;

import java.io.IOException;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    EditText edtFullName, edtPhone, edtEmail, edtPassword;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_register);

        edtFullName = findViewById(R.id.edtFullName);
        edtPhone    = findViewById(R.id.edtPhone);
        edtEmail    = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> doRegister());
    }

    void doRegister() {
        String name  = edtFullName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String pass  = edtPassword.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            toast("Điền đầy đủ thông tin");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            toast("Email không hợp lệ");
            return;
        }
        if (!isStrong(pass)) {
            toast("Mật khẩu yếu (cần chữ hoa, số, ký tự đặc biệt, >=8 ký tự)");
            return;
        }

        RegisterReq req = new RegisterReq();
        req.fullName = name;
        req.phone    = phone;
        req.email    = email;
        req.password = pass;

        ApiClient.get(this).register(req).enqueue(new Callback<LoginResp>() {
            @Override public void onResponse(Call<LoginResp> c, Response<LoginResp> r) {
                // TH1: API trả LoginResp có token
                if (r.isSuccessful() && r.body() != null && r.body().getToken() != null) {
                    saveAndGo(r.body());
                    return;
                }

                // TH2: API trả 200 nhưng không có token -> tự login
                if (r.isSuccessful()) {
                    loginAfterRegister(email, pass);
                    return;
                }

                // TH3: lỗi – đọc thông điệp cụ thể
                String msg = "Đăng ký thất bại";
                if (r.code() == 409) msg = "SĐT/Email đã tồn tại";
                else if (r.errorBody() != null) {
                    try { msg = r.errorBody().string(); } catch (IOException ignored) {}
                }
                toast(msg);
            }

            @Override public void onFailure(Call<LoginResp> c, Throwable t) {
                toast(t.getMessage() != null ? t.getMessage() : "Lỗi mạng");
            }
        });
    }

    private void loginAfterRegister(String email, String pass) {
        ApiClient.get(this).login(new LoginReq(email, pass)).enqueue(new Callback<LoginResp>() {
            @Override public void onResponse(Call<LoginResp> c, Response<LoginResp> r) {
                if (r.isSuccessful() && r.body() != null && r.body().getToken() != null) {
                    saveAndGo(r.body());
                } else {
                    toast("Tạo tài khoản xong nhưng đăng nhập thất bại");
                }
            }
            @Override public void onFailure(Call<LoginResp> c, Throwable t) { toast(t.getMessage()); }
        });
    }

    private void saveAndGo(LoginResp body) {
        Session.save(
                this,
                body.getToken(),
                body.getRole(),
                body.getFullName(),
                body.getUserId() != null ? body.getUserId() : 0L
        );
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    private boolean isStrong(String p) {
        if (p.length() < 8) return false;
        Pattern upper = Pattern.compile(".*[A-Z].*");
        Pattern digit = Pattern.compile(".*\\d.*");
        Pattern special = Pattern.compile(".*[^a-zA-Z0-9].*");
        return upper.matcher(p).matches() && digit.matcher(p).matches() && special.matcher(p).matches();
    }

    private void toast(String s){ Toast.makeText(this, s, Toast.LENGTH_SHORT).show(); }
}
