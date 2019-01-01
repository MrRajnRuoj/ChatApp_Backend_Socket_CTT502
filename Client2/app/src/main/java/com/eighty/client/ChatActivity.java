package com.eighty.client;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private ListView listMessage;
    private ArrayList<MsgRow> arrayList;
    private ListMessageAdapter adapter;

    private EditText edtSend;
    private ImageButton imageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

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

        arrayList.add(new MsgRow("1", 0));
        arrayList.add(new MsgRow("2", 1));
        arrayList.add(new MsgRow("3", 0));
        arrayList.add(new MsgRow("4", 1));
        arrayList.add(new MsgRow("5", 0));
        arrayList.add(new MsgRow("6", 0));
        arrayList.add(new MsgRow("7", 1));
        arrayList.add(new MsgRow("1", 0));
        arrayList.add(new MsgRow("2", 1));
        arrayList.add(new MsgRow("3", 0));
        arrayList.add(new MsgRow("4", 1));
        arrayList.add(new MsgRow("5", 0));
        arrayList.add(new MsgRow("6", 0));
        arrayList.add(new MsgRow("7", 1));
        arrayList.add(new MsgRow("1", 0));
        arrayList.add(new MsgRow("2", 1));
        arrayList.add(new MsgRow("3", 0));
        arrayList.add(new MsgRow("cá ăn mèo", 1));
        arrayList.add(new MsgRow("5", 0));
        arrayList.add(new MsgRow("6", 0));
        arrayList.add(new MsgRow("mèo ăn cá", 1));
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
