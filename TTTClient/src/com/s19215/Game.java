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
    private Scanner clientIn;

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
        clientIn = new Scanner(System.in);
        while(isRunning){
            printInitMessages();
            command = clientIn.nextLine();
            command = command.trim();
            if(command.equals("LOGOUT"))
                logout();
            else if(command.equals("LIST"))
                getPlayersList();
            else if(command.equals("PLAY"))
                try {
                    startGame();
                } catch (IOException e){
                    System.out.println("Gra została przerwana.");
                }
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
            if(msg.equals("INTERRUPT")) {
                System.out.println("Gra została przerwana");
                run = false;
            } else if (msg.equals("VICTORY")){
                System.out.println("Zwycięstwo!");
                run = false;
            } else if (msg.equals("DEFEAT")){
                System.out.println("Przegrana :(");
                run = false;
            } else {
                printField(msg.substring(4));
                if(msg.substring(0,4).equals("TURN")){
                    System.out.println("PODAJ NR POLA:");
                    int no = clientIn.nextInt();
                    sendMessage("MOVE"+(no%3)+(no/3));
                }
                else{
                    System.out.println("OCZEKIWANIE NA RUCH PRZECIWNIKA");
                }
            }
        }
    }

    private void printField(String substring) {
        System.out.println("=========");
        for(int i = 0; i < 9; i++){
            System.out.print(numerToXO(substring.charAt(i)));
            if((i+1)%3==0)
                System.out.println();
        }
    }

    private String numerToXO(char charAt) {
        if(charAt=='1')
            return "X";
        else if(charAt=='0')
            return "O";
        else
            return " ";
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
