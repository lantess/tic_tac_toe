package com.s19215;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Game {
    private Socket skc;
    private boolean isRunning;

    public Game(InetAddress addr, int port) throws IOException {
        skc = new Socket(addr,port);
        isRunning = true;
    }

    private void printInitMessages() {
        System.out.println("=======================================");
        System.out.println("LIST - aby wyświetlić aktywnych graczy");
        System.out.println("PLAY - aby zagrać");
        System.out.println("LOGOUT - aby wyjść");
        System.out.println("=======================================");
    }


    public void start() throws IOException{
        String command;
        Scanner clientIn = new Scanner(System.in);
        while(isRunning){
            printInitMessages();
            command = clientIn.nextLine();
            command = command.trim();
            if(command.equals("LOGOUT"))
                logout();
            else if(command.equals("LIST"))
                getPlayersList();
            else if(command.equals("PLAY"))
                startGame();
            else
                System.out.println("Nieprawidłowe polecenie.");
        }
        clientIn.close();
    }

    private void startGame() throws IOException{
        sendMessage("PLAY");
        boolean run = true;
        while(run){
            String msg = getNextInputLine();
            System.out.println(msg);
        }
    }

    private void logout() throws IOException{
        isRunning = false;
        sendMessage("LOGOUT");
        skc.close();
    }

    private void getPlayersList() throws IOException{
        sendMessage("LIST");
        String in = getNextInputLine();
        System.out.println(in);
    }

    private String getNextInputLine() throws IOException{
        BufferedReader in = new BufferedReader(new InputStreamReader(skc.getInputStream()));
        String s = in.readLine();
        return s;
    }

    private void sendMessage(String msg) throws IOException{
        PrintWriter out = new PrintWriter(skc.getOutputStream());
        out.write(msg+"\r");
        out.flush();
    }
}
