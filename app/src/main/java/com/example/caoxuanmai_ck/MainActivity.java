package com.example.caoxuanmai_ck;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.caoxuanmai_ck.core.Session;
import com.example.caoxuanmai_ck.data.LoginReq;
import com.example.caoxuanmai_ck.data.LoginResp;
import com.example.caoxuanmai_ck.net.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    EditText edtLogin, edtPassword;
    Button btnLogin, btnRegister, btnGuest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ánh xạ view
        edtLogin    = findViewById(R.id.edtLogin);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin    = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        btnGuest    = findViewById(R.id.btnGuest);

        // Nếu đã có token -> vào Home
        String tk = Session.token(this);
        if (tk != null && !tk.isEmpty()) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return;
        }

        btnLogin.setOnClickListener(v -> doLogin());
        btnRegister.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, RegisterActivity.class)));
        btnGuest.setOnClickListener(v -> {
            Session.clear(MainActivity.this);
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
        });
    }

    private void doLogin() {
        String login = edtLogin.getText().toString().trim();
        String pass  = edtPassword.getText().toString().trim();

        if (login.isEmpty() || pass.isEmpty()) {
            toast("Nhập tài khoản và mật khẩu");
            return;
        }

        ApiClient.get(this).login(new LoginReq(login, pass))
                .enqueue(new Callback<LoginResp>() {
                    @Override
                    public void onResponse(Call<LoginResp> call, Response<LoginResp> resp) {
                        LoginResp body = resp.body();
                        if (!resp.isSuccessful() || body == null || body.getToken() == null) {
                            toast(body != null && body.error != null ? body.error : "Đăng nhập thất bại");
                            return;
                        }
                        // Lưu session
                        Session.save(
                                MainActivity.this,
                                body.getToken(),
                                body.getRole(),
                                body.getFullName(),
                                body.getUserId() != null ? body.getUserId() : 0L
                        );

                        startActivity(new Intent(MainActivity.this, HomeActivity.class));
                        finish();
                    }

                    @Override
                    public void onFailure(Call<LoginResp> call, Throwable t) {
                        toast(t.getMessage() != null ? t.getMessage() : "Lỗi mạng");
                    }
                });
    }

    private void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}
