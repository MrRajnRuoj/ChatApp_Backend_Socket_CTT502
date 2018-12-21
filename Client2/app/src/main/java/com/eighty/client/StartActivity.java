package com.eighty.client;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.button.MaterialButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import io.socket.client.Socket;

public class StartActivity extends AppCompatActivity {

    private MaterialButton btnLogin;
    private MaterialButton btnRegister;

    private void initView() {
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
    }

    private void setListener() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, RegisterActivity.class));
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, LoginActivity.class));
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        SocketSingleton.getSocket().connect();

        SharedPreferences sharedPreferences = getSharedPreferences("secret", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");

        if (token != null && !token.equals("")) {
            Intent intent = new Intent(StartActivity.this, LoginActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("token", token);
            intent.putExtras(bundle);
            startActivity(intent);
        }

        initView();
        setListener();
    }
}
