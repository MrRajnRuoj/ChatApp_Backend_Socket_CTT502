package com.eighty.client;

import android.content.Intent;
import android.support.design.button.MaterialButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import io.socket.emitter.Emitter;

public class VerifyActivity extends AppCompatActivity {

    private MaterialEditText edtEmail;
    private MaterialEditText edtVerify;
    private MaterialButton btnVerify;
    private MaterialButton btnResend;
    private Toolbar toolbar;

    private void initView() {
        edtEmail = findViewById(R.id.edtEmail);
        edtVerify = findViewById(R.id.edtVerify);
        btnVerify = findViewById(R.id.btnVerify);
        btnResend = findViewById(R.id.btnResend);
        toolbar = findViewById(R.id.toolBar);
    }

    private void setListener() {
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Objects.requireNonNull(edtVerify.getText()).toString().trim().length() == 0) {
                    edtVerify.setError("Xin nhập mã xác nhận");
                    return;
                }
                if (Objects.requireNonNull(edtEmail.getText()).toString().trim().length() == 0) {
                    edtEmail.setError("Xin nhập email");
                    return;
                }
                JSONObject obj = new JSONObject();
                try {
                    obj.put("email", edtEmail.getText().toString());
                    obj.put("code", Integer.parseInt(edtVerify.getText().toString()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                SocketSingleton.getSocket().emit("REQUEST_VERIFY_EMAIL", obj);
                SocketSingleton.getSocket().on("RESPONSE_VERIFY_EMAIL", onResponseVerifyEmail);
            }
        });
        btnResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Objects.requireNonNull(edtEmail.getText()).toString().trim().length() == 0) {
                    edtEmail.setError("Xin nhập email");
                    return;
                }
                JSONObject obj = new JSONObject();
                try {
                    obj.put("email", edtEmail.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                SocketSingleton.getSocket().emit("REQUEST_RESEND_VERIFY_CODE", obj);
                SocketSingleton.getSocket().on("RESPONSE_RESEND_VERIFY_CODE", onResponseResend);
            }
        });
    }

    private Emitter.Listener onResponseResend = new Emitter.Listener() {
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
                                default:
                                    Toast.makeText(VerifyActivity.this, "Lỗi không xác định", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        } else {
                            Toast.makeText(VerifyActivity.this, "Yêu cầu gửi lại mã xác nhận thành công", Toast.LENGTH_SHORT).show();
                        }
                        SocketSingleton.getSocket().off("RESPONSE_VERIFY_EMAIL");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener onResponseVerifyEmail = new Emitter.Listener() {
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
                                case "INCORRECT_VERIFY_CODE":
                                    Toast.makeText(VerifyActivity.this, "Mã xác nhận không chính xác", Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    Toast.makeText(VerifyActivity.this, "Lỗi không xác định", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        } else {
                            Toast.makeText(VerifyActivity.this, "Xác nhận tài khoản thành công, bạn có thể đăng nhập", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(VerifyActivity.this, LoginActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("email", Objects.requireNonNull(edtEmail.getText()).toString());
                            intent.putExtras(bundle);
                            SocketSingleton.getSocket().off("RESPONSE_VERIFY_EMAIL");
                            startActivity(intent);
                        }
                        SocketSingleton.getSocket().off("RESPONSE_VERIFY_EMAIL");
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
        setContentView(R.layout.activity_verify);

        initView();
        setListener();

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Xác nhận email");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            edtEmail.setText(bundle.getString("email"));
        }
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                onBackPressed();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
