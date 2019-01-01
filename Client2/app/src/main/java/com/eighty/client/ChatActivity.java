package com.eighty.client;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.emitter.Emitter;

public class ChatActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private ListView listMessage;
    private ArrayList<MsgRow> arrayList;
    private ListMessageAdapter adapter;

    private EditText edtSend;
    private ImageButton imageButton;

    private int id;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        SocketSingleton.getSocket().on("RECIEVE_MESSAGE", onReceiveMessage);

        id = getIntent().getExtras().getInt("id", -1);
        email = getIntent().getExtras().getString("email", "");

        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getIntent().getExtras().getString("email", ""));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listMessage = findViewById(R.id.listMessage);
        edtSend = findViewById(R.id.edtSend);
        imageButton = findViewById(R.id.imageButton);

        arrayList = new ArrayList<>();
        adapter = new ListMessageAdapter(getApplicationContext(), arrayList);
        listMessage.setAdapter(adapter);
        listMessage.setStackFromBottom(true);

        JSONObject object_msg = new JSONObject();
        try {
            object_msg.put("toUserID", id);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        SocketSingleton.getSocket().emit("REQUEST_MESSAGE", object_msg);
        SocketSingleton.getSocket().on("REQUEST_MESSAGE", onGetMsgList);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtSend.getText().toString().length() == 0) {
                    Toast.makeText(ChatActivity.this, "Xin nhập tin nhắn", Toast.LENGTH_SHORT).show();
                }
                else {
                    JSONObject object = new JSONObject();
                    try {
                        object.put("toUserID", id);
                        object.put("message", edtSend.getText().toString());
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                    SocketSingleton.getSocket().emit("SEND_MESSAGE", object);
                    //SocketSingleton.getSocket().on("SEND_MESSAGE", onResponseSendMsg);
                }
            }
        });
    }

    private Emitter.Listener onGetMsgList = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject object = (JSONObject) args[0];
                    try {
                        if (!object.getBoolean("error")) {
                            JSONArray listMsg = object.getJSONArray("messageData");
                            for (int i = 0; i < listMsg.length(); i++) {
                                if (listMsg.getJSONObject(i).getString("sender").equals(email)) {
                                    arrayList.add(new MsgRow(listMsg.getJSONObject(i).getString("message"), 0));
                                }
                                else {
                                    arrayList.add(new MsgRow(listMsg.getJSONObject(i).getString("message"), 1));
                                }
                                adapter.notifyDataSetChanged();
                            }
                        }
                        else {
                            Toast.makeText(ChatActivity.this, "Không thể get msg từ server", Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener onReceiveMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject object = (JSONObject) args[0];
                    try {
                        if (object.getInt("senderID") == id) {
                            arrayList.add(new MsgRow(object.getString("message"), 0));
                        }
                        else {
                            arrayList.add(new MsgRow(object.getString("message"), 1));
                        }
                        adapter.notifyDataSetChanged();
                        edtSend.setText("");
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

//    private Emitter.Listener onResponseSendMsg = new Emitter.Listener() {
//        @Override
//        public void call(Object... args) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    JSONObject object = (JSONObject) args[0];
//                    try {
//                        Boolean error = object.getBoolean("error");
//                        if (error) {
//                            Toast.makeText(ChatActivity.this, object.getString("message"), Toast.LENGTH_SHORT).show();
//                        }
//                        else {
//                            arrayList.add(new MsgRow(edtSend.getText().toString(), 1));
//                            adapter.notifyDataSetChanged();
//                            edtSend.setText("");
//                        }
//                        SocketSingleton.getSocket().off("SEND_MESSAGE");
//                    }
//                    catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//        }
//    };

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
