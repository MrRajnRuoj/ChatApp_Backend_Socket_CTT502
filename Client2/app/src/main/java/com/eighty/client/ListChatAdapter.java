package com.eighty.client;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ListChatAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ChatRow> arrayList;

    public ListChatAdapter(Context context, ArrayList<ChatRow> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        ImageView imgAva;
        TextView txtEmail;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.custom_listchat_row, null);
            viewHolder = new ViewHolder();
            viewHolder.txtEmail = convertView.findViewById(R.id.txtEmail);
            viewHolder.imgAva = convertView.findViewById(R.id.imgAva);
            viewHolder.txtEmail.setText(arrayList.get(position).getEmail());
            viewHolder.imgAva.setImageResource(R.drawable.ic_person);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.txtEmail.setText(arrayList.get(position).getEmail());
            viewHolder.imgAva.setImageResource(R.drawable.ic_person);
        }

        return convertView;
    }
}
