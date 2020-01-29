package com.s19215;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class Game {
    private Socket skc;

    public Game(InetAddress addr, int port) throws IOException {
        skc = new Socket(addr,port);
    }


    public void start() {

    }
}
