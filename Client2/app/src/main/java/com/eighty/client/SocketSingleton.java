package com.eighty.client;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

class SocketSingleton {
    private static final SocketSingleton ourInstance = new SocketSingleton();
//    private static final String SERVER_ADDRESS = "https://chatapp-224217.appspot.com";
    private static final String SERVER_ADDRESS = "http://192.168.1.6:3231";

    private static Socket socket;

    static synchronized Socket getSocket() {
        return socket;
    }

//    static synchronized SocketSingleton getInstance() {
//        return ourInstance;
//    }

    private SocketSingleton() {
        try {
            IO.Options options = new IO.Options();
            options.secure = true;
            socket = IO.socket(SERVER_ADDRESS, options);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
