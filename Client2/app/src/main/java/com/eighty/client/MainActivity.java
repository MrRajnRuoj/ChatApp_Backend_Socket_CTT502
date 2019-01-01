package com.eighty.client;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView nav_view;

    private TextView header_nickname;
    private TextView header_email;

    private ListView listChat;
    private ArrayList<ChatRow> arrayList;
    private ListChatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SocketSingleton.getSocket().on("NOTIFY_FRIEND_REQUEST", onNotifyFriendRequest);

        initView();
        setListener();

        arrayList = new ArrayList<>();
        adapter = new ListChatAdapter(getApplicationContext(), arrayList);
        listChat.setAdapter(adapter);

        Intent callingIntent = getIntent();
        String email = callingIntent.getExtras().getString("email", "");
        int id = callingIntent.getExtras().getInt("id", 0);

        if (!email.equals("")) {
            header_email.setText(email);
            header_nickname.setText(id + "");
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);

        SocketSingleton.getSocket().emit("REQUEST_LIST_FRIEND");
        SocketSingleton.getSocket().on("RESPONSE_LIST_FRIEND", onResponseListFriend);
    }

    private Emitter.Listener onResponseListFriend = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject object = (JSONObject) args[0];
                    try {
                        JSONArray listFriend = object.getJSONArray("listFriend");
                        for (int i = 0; i < listFriend.length(); i++) {
                            arrayList.add(new ChatRow(listFriend.getJSONObject(i).getString("email"), listFriend.getJSONObject(i).getInt("id")));
                        }
                        adapter.notifyDataSetChanged();
                        SocketSingleton.getSocket().off("RESPONSE_LIST_FRIEND");
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener onNotifyFriendRequest = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject object = (JSONObject)args[0];
                    try {
                        JSONObject user = object.getJSONObject("userInfo");
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                        alertDialog.setMessage(" muốn kết bạn với bạn");
                        alertDialog.setNegativeButton("Từ chối", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                JSONObject ret_object = new JSONObject();
                                try {
                                    Boolean isAccept = false;
                                    int userID = Integer.parseInt(header_nickname.getText().toString());
                                    ret_object.put("isAccept", isAccept);
                                    ret_object.put("userID", userID);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                SocketSingleton.getSocket().emit("RESPONSE_FRIEND_REQUEST", ret_object);
                            }
                        });
                        alertDialog.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                JSONObject ret_object = new JSONObject();
                                try {
                                    Boolean isAccept = true;
                                    int userID = Integer.parseInt(header_nickname.getText().toString());
                                    ret_object.put("isAccept", isAccept);
                                    ret_object.put("userID", userID);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                SocketSingleton.getSocket().emit("RESPONSE_FRIEND_REQUEST", ret_object);
                            }
                        });
                        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                JSONObject ret_object = new JSONObject();
                                try {
                                    Boolean isAccept = false;
                                    int userID = Integer.parseInt(header_nickname.getText().toString());
                                    ret_object.put("isAccept", isAccept);
                                    ret_object.put("userID", userID);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                SocketSingleton.getSocket().emit("RESPONSE_FRIEND_REQUEST", ret_object);
                            }
                        });
                        alertDialog.show();
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private void initView() {
        drawerLayout = findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);
        drawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        toolbar = findViewById(R.id.toolBar);
        nav_view = findViewById(R.id.nav_view);

        View nav_header = nav_view.getHeaderView(0);
        header_email = nav_header.findViewById(R.id.header_email);
        header_nickname = nav_header.findViewById(R.id.header_nickname);

        listChat = findViewById(R.id.listChat);
    }

    private void setListener() {
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.drawer_logout: {
                        SocketSingleton.getSocket().emit("REQUEST_LOGOUT");
                        SharedPreferences sharedPreferences = getSharedPreferences("secret", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();
                        startActivity(new Intent(MainActivity.this, StartActivity.class));
                        finish();
                        break;
                    }
                }

                return true;
            }
        });
        listChat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("id", arrayList.get(position).getId());
                bundle.putString("email", arrayList.get(position).getEmail());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.add_friend: {
                View view = getLayoutInflater().inflate(R.layout.custom_dialog, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setView(view);

                TextInputLayout txtLayEmail = view.findViewById(R.id.txtLayEmail);
                TextInputEditText edtEmail = view.findViewById(R.id.edtEmail);
                MaterialButton btnAddFriend = view.findViewById(R.id.btnAddFriend);

                btnAddFriend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (edtEmail.getText().toString().trim().length() == 0) {
                            txtLayEmail.setError("Xin nhập email");
                        }
                        else {
                            JSONObject object = new JSONObject();
                            try {
                                object.put("userEmail", edtEmail.getText().toString().trim());
                            }
                            catch (JSONException e) {
                                e.printStackTrace();
                            }
                            SocketSingleton.getSocket().emit("REQUEST_ADD_FRIEND", object);
                            SocketSingleton.getSocket().on("REQUEST_ADD_FRIEND", onResponseAddFriend);
                        }
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

                alertDialogBuilder.show();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private Emitter.Listener onResponseAddFriend = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject object = (JSONObject)args[0];
                    try {
                        Boolean error = object.getBoolean("error");
                        if (error) {
                            Toast.makeText(MainActivity.this, "Lỗi không xác định", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Gửi yêu cầu thành công", Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };
}
