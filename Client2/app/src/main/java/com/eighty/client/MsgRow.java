package com.eighty.client;

public class MsgRow {

    String msg;
    int pos;

    public MsgRow(String msg, int pos) {
        this.msg = msg;
        this.pos = pos;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }
}
