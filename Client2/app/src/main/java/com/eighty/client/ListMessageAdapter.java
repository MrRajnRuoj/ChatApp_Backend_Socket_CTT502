package com.eighty.client;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ListMessageAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<MsgRow> arrayList;

    public ListMessageAdapter(Context context, ArrayList<MsgRow> arrayList) {
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

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (arrayList.get(position).getPos() == 0) {
            return 0;
        }
        else {
            return 1;
        }
    }

    private static class ViewHolder {
        ImageView imgAva;
        TextView txtMsg;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (getItemViewType(position) == 0) {
                convertView = layoutInflater.inflate(R.layout.custom_msg_left, null);
            }
            else {
                convertView = layoutInflater.inflate(R.layout.custom_msg_right, null);
            }

            viewHolder = new ViewHolder();
            viewHolder.txtMsg = convertView.findViewById(R.id.txtMsg);
            viewHolder.imgAva = convertView.findViewById(R.id.imgAva);

            viewHolder.txtMsg.setText(arrayList.get(position).getMsg());
            viewHolder.imgAva.setImageResource(R.drawable.ic_person);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.txtMsg.setText(arrayList.get(position).getMsg());
            viewHolder.imgAva.setImageResource(R.drawable.ic_person);
            convertView.setTag(viewHolder);
        }

//        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//        if (arrayList.get(position).getPos() == 0) {
//            convertView = layoutInflater.inflate(R.layout.custom_msg_left, null);
//        }
//        else {
//            convertView = layoutInflater.inflate(R.layout.custom_msg_right, null);
//        }
//
//        TextView txtMsg = convertView.findViewById(R.id.txtMsg);
//        ImageView imgAva = convertView.findViewById(R.id.imgAva);
//
//        txtMsg.setText(arrayList.get(position).getMsg());
//        imgAva.setImageResource(R.drawable.ic_person);

        return convertView;
    }
}
