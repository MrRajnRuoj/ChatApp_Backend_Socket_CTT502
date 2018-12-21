package com.eighty.client;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.button.MaterialButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import io.socket.emitter.Emitter;

public class LoginActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextInputLayout txtLayEmail;
    private TextInputEditText edtEmail;
    private TextInputLayout txtLayPassword;
    private TextInputEditText edtPassword;
    private MaterialButton btnLogin;

    private void initView() {
        toolbar = findViewById(R.id.toolBar);
        txtLayEmail = findViewById(R.id.txtLayEmail);
        txtLayPassword = findViewById(R.id.txtLayPassword);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
    }

    private void setListener() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Objects.requireNonNull(edtEmail.getText()).toString().trim().length() == 0) {
                    txtLayEmail.setError("Xin nhập email");
                    return;
                }
                if (Objects.requireNonNull(edtPassword.getText()).toString().trim().length() == 0) {
                    txtLayPassword.setError("Xin nhập mật khẩu");
                    return;
                }
                JSONObject obj = new JSONObject();
                try {
                    obj.put("email", edtEmail.getText().toString());
                    obj.put("password", edtPassword.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                SocketSingleton.getSocket().emit("REQUEST_LOGIN", obj);
                SocketSingleton.getSocket().on("RESPONSE_LOGIN", onResponseLogin);

            }
        });
        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                txtLayEmail.setError("");
            }
        });
        edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                txtLayPassword.setError("");
            }
        });
    }

    public Emitter.Listener onResponseLogin = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject object = (JSONObject)args[0];
                    try {
                        Boolean error = object.getBoolean("error");
                        String message = object.getString("message");
                        if (error) {
                            switch (message) {
                                case "EMAIL_NOT_EXIST":
                                    Toast.makeText(LoginActivity.this, "Email không tồn tại", Toast.LENGTH_SHORT).show();
                                    break;
                                case "EMAIL_OR_PASSWORD_NOT_CORRECT":
                                    Toast.makeText(LoginActivity.this, "Email hoặc mật khẩu không chính xác", Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    Toast.makeText(LoginActivity.this, "Lỗi không xác định", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                            SocketSingleton.getSocket().off("RESPONSE_LOGIN");
                        } else {
                            switch (message) {
                                case "VERIFY_ACCOUNT":
                                {
                                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);
                                    alertDialog.setMessage("Bạn chưa xác nhận email, vui lòng kiểm tra mail và nhập mã xác nhận");
                                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(LoginActivity.this, VerifyActivity.class);
                                            Bundle bundle = new Bundle();
                                            bundle.putString("email", Objects.requireNonNull(edtEmail.getText()).toString());
                                            intent.putExtras(bundle);
                                            SocketSingleton.getSocket().off("RESPONSE_LOGIN");
                                            startActivity(intent);
                                        }
                                    });
                                    alertDialog.show();
                                }
                                break;
                                case "LOGIN_SUCCESS":
                                    SocketSingleton.getSocket().off("RESPONSE_LOGIN");
                                    String token = object.getString("token");
                                    SharedPreferences sharedPreferences = getSharedPreferences("secret", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("token", token);
                                    editor.apply();
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    break;
                                default:
                                    Toast.makeText(LoginActivity.this, "Lỗi không xác định", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                            SocketSingleton.getSocket().off("RESPONSE_LOGIN");
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

        Intent callingIntent = getIntent();
        Bundle bundle = callingIntent.getExtras();
        if (bundle != null) {
            String token = bundle.getString("token", "");
            if (!Objects.equals(token, "")) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("token", token);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                SocketSingleton.getSocket().emit("REQUEST_LOGIN", obj);
                SocketSingleton.getSocket().on("RESPONSE_LOGIN", onResponseLogin);
            }
        }

        setContentView(R.layout.activity_login);

        initView();
        setListener();

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Đăng nhập");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
