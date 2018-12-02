package com.mrrajnruoj.chatapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {
    private Socket mSocket;
    Button btnSend, btnSignup, btnVerify;
    EditText edtEmail, edtPassword, edtVerifyCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            mSocket = IO.socket("http://10.10.187.55:3231/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        mSocket.connect();
        btnSend = (Button)findViewById(R.id.btnSend);
        btnSignup = (Button)findViewById(R.id.btnSignup);
        btnVerify = (Button)findViewById(R.id.btnVerify);
        edtEmail = (EditText)findViewById(R.id.edtEmail);
        edtPassword = (EditText)findViewById(R.id.edtPassword);
        edtVerifyCode = (EditText)findViewById(R.id.edtVerifyCode);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("email", edtEmail.getText().toString());
                    obj.put("password", edtPassword.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mSocket.emit("REQUEST_LOGIN", obj);
            }
        });
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("email", edtEmail.getText().toString());
                    obj.put("password", edtPassword.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mSocket.emit("REQUEST_SIGNUP", obj);
            }
        });
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("email", edtEmail.getText().toString());
                    obj.put("password", edtPassword.getText().toString());
                    obj.put("code", Integer.parseInt(edtVerifyCode.getText().toString()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mSocket.emit("REQUEST_VERIFY_EMAIL", obj);
            }
        });
        mSocket.on("RESPONSE_LOGIN", onRecieveData);
        mSocket.on("RESPONSE_SIGNUP", onResponseSignup);
        mSocket.on("RESPONSE_VERIFY_EMAIL", onResponseVerifyEmail);
    }
    private  Emitter.Listener onResponseVerifyEmail = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject object = (JSONObject)args[0];
                    try {
                        //String mess = object.getString("mess");
                        //Toast.makeText(MainActivity.this, mess, Toast.LENGTH_SHORT).show();
                        Boolean error = object.getBoolean("error");
                        if (error) {
                            String mess = object.getString("message");
                            Toast.makeText(MainActivity.this, mess, Toast.LENGTH_SHORT).show();
                        } else {
                            String mess = object.getString("message");
                            Toast.makeText(MainActivity.this, mess, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener onRecieveData = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject object = (JSONObject)args[0];
                    try {
                        //String mess = object.getString("mess");
                        //Toast.makeText(MainActivity.this, mess, Toast.LENGTH_SHORT).show();
                        Boolean error = object.getBoolean("error");
                        if (error) {
                            String mess = object.getString("message");
                            Toast.makeText(MainActivity.this, mess, Toast.LENGTH_SHORT).show();
                        } else {
                            String token = (String) object.get("token");
                            Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };
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
                            String mess = object.getString("message");
                            Toast.makeText(MainActivity.this, mess, Toast.LENGTH_SHORT).show();
                        } else {
                            String mess = object.getString("message");
                            Toast.makeText(MainActivity.this, mess, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };
}
