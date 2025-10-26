package com.example.caoxuanmai_ck;


import android.os.Bundle; import android.widget.*; import androidx.appcompat.app.AppCompatActivity;
import com.example.caoxuanmai_ck.core.Session; import com.example.caoxuanmai_ck.data.*; import com.example.caoxuanmai_ck.net.*; import retrofit2.*;


public class RegisterActivity extends AppCompatActivity {
    EditText edtName, edtPhone, edtEmail, edtPass; Button btnOk;
    @Override protected void onCreate(Bundle b){ super.onCreate(b); setContentView(R.layout.activity_register);
        edtName=findViewById(R.id.edtName); edtPhone=findViewById(R.id.edtPhone); edtEmail=findViewById(R.id.edtEmail); edtPass=findViewById(R.id.edtPass); btnOk=findViewById(R.id.btnRegister);
        btnOk.setOnClickListener(v -> submit()); }
    void submit(){
        RegisterReq req = new RegisterReq();
        req.fullName=edtName.getText().toString().trim(); req.phone=edtPhone.getText().toString().trim(); req.email=edtEmail.getText().toString().trim(); req.password=edtPass.getText().toString();
        ApiClient.get(this).register(req).enqueue(new Callback<LoginResp>(){
            @Override public void onResponse(Call<LoginResp> c, Response<LoginResp> r){
                LoginResp body=r.body(); if(!r.isSuccessful()||body==null||body.token==null){ Toast.makeText(RegisterActivity.this, body!=null&&body.error!=null?body.error:"Đăng ký thất bại", Toast.LENGTH_SHORT).show(); return; }
                Session.save(RegisterActivity.this, body.token, body.role, body.name, body.userId);
                Toast.makeText(RegisterActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show(); finish(); }
            @Override public void onFailure(Call<LoginResp> c, Throwable t){ Toast.makeText(RegisterActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show(); }
        });
    }
}