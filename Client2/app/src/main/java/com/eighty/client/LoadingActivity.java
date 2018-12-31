package com.eighty.client;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.emitter.Emitter;

public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        SocketSingleton.getSocket().connect();

        SharedPreferences sharedPreferences = getSharedPreferences("secret", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");

        if (token != null && !token.equals("")) {
//            Intent intent = new Intent(LoadingActivity.this, LoginActivity.class);
//            Bundle bundle = new Bundle();
//            bundle.putString("token", token);
//            intent.putExtras(bundle);
//            startActivity(intent);
            JSONObject obj = new JSONObject();
            try {
                obj.put("token", token);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            SocketSingleton.getSocket().emit("REQUEST_LOGIN", obj);
            SocketSingleton.getSocket().on("RESPONSE_LOGIN", onResponseLogin);
        }
        else {
            startActivity(new Intent(LoadingActivity.this, StartActivity.class));
            finish();
        }
    }

    private Emitter.Listener onResponseLogin = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject object = (JSONObject)args[0];
                    try {
                        Boolean error = object.getBoolean("error");
                        if (error) {
                            startActivity(new Intent(LoadingActivity.this, StartActivity.class));
                        }
                        else {
                            JSONObject user = object.getJSONObject("userInfo");
                            String email = user.getString("email");
                            int id = user.getInt("id");
                            Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("email", email);
                            bundle.putInt("id", id);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                        SocketSingleton.getSocket().off("RESPONSE_LOGIN");
                        finish();
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };
}
