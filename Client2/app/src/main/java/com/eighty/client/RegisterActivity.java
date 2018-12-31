package com.eighty.client;

import android.content.Intent;
import android.support.design.button.MaterialButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import io.socket.emitter.Emitter;

public class RegisterActivity extends AppCompatActivity {

    private MaterialEditText edtEmail;
    private MaterialEditText edtPassword;
    private MaterialEditText edtConfirmPassword;
    private MaterialButton btnRegister;
    private Toolbar toolbar;

    private void initView() {
        edtEmail = findViewById(R.id.edtVerify);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        toolbar = findViewById(R.id.toolBar);
    }

    private void setListener() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Objects.requireNonNull(edtEmail.getText()).toString().trim().length() == 0) {
                    edtEmail.setError("Xin vui lòng nhập email");
                    return;
                }
                if (Objects.requireNonNull(edtPassword.getText()).toString().trim().length() == 0) {
                    edtPassword.setError("Xin vui lòng nhập password");
                    return;
                }
                if (Objects.requireNonNull(edtConfirmPassword.getText()).toString().trim().length() == 0) {
                    edtConfirmPassword.setError("Xin vui lòng nhập lại password");
                    return;
                }
                if (!String.valueOf(edtPassword.getText()).equals(String.valueOf(edtConfirmPassword.getText()))) {
                    Toast.makeText(RegisterActivity.this, "Mật khẩu nhập không khớp",Toast.LENGTH_SHORT).show();
                }
                else {
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("email", edtEmail.getText().toString());
                        obj.put("password", edtPassword.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    SocketSingleton.getSocket().emit("REQUEST_SIGNUP", obj);
                    SocketSingleton.getSocket().on("RESPONSE_SIGNUP", onResponseSignup);
                }
            }
        });
    }

    private Emitter.Listener onResponseSignup = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject object = (JSONObject)args[0];
                    try {
                        Boolean error = object.getBoolean("error");
                        if (error) {
                            String message = object.getString("message");
                            switch (message) {
                                case "EMAIL_ALREADY_EXISTS":
                                    Toast.makeText(RegisterActivity.this, "Email đã tồn tại", Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    Toast.makeText(RegisterActivity.this, "Lỗi không xác định", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                            SocketSingleton.getSocket().off("RESPONSE_SIGNUP");
                        } else {
                            Toast.makeText(RegisterActivity.this, "Đăng ký tài khoản thành công, xin xác nhận tài khoản", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(RegisterActivity.this, VerifyActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("email", Objects.requireNonNull(edtEmail.getText()).toString());
                            intent.putExtras(bundle);
                            SocketSingleton.getSocket().off("RESPONSE_SIGNUP");
                            startActivity(intent);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();
        setListener();

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Đăng ký");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
