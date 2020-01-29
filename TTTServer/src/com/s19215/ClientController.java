package com.s19215;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Collection;

public class ClientController implements Runnable {
    private int id;
    private boolean isWaiting;
    private boolean isRunning;
    private GamesController gc;
    private Socket sck;

    public ClientController(GamesController gc, Socket sck, int id){
        isWaiting = false;
        isRunning = true;
        this.gc = gc;
        this.sck = sck;
        this.id=id;
    }

    @Override
    public void run() {
            try {
                while(isRunning){
                    String command = readDataFromClient();
                        System.out.println(command);
                        if(command.equals("PLAY"))
                            startGame();
                        else if(command.equals("LOGOUT"))
                            killClient();
                        else if(command.equals("LIST"))
                            sendClientList();
                        else
                            sendDataToClient("ERROR");
                }
                killClient();
            } catch (IOException e){
                killClient();
                System.out.println("Problem z połączeniem z klientem "+e.getMessage());
            }
    }

    private void startGame() throws IOException {
        ClientController rival = gc.startGame(this);
        if(rival==null){
            System.out.println("GRACZ 1 DOSZEDL");
            isWaiting = true;
            waitUntilGame();
        }
        else{
            System.out.println("GRACZ 2 DOSZEDL");
            Game game = new Game(this, rival);
            try {
                game.run();
            } catch (IOException e){
                if(!this.sck.isClosed())
                    sendDataToClient("INTERRUPT");
                if(!rival.sck.isClosed())
                    sendDataToClient("INTERRUPT");
            }
            rival.endWaiting();
        }
    }

    private void sendClientList() {
        Collection<ClientController> l = gc.getClientsList();
        String data = "";
        for(ClientController c : l){
            data+=c.getClientinfo()+"\t";
        }
        try {
            sendDataToClient(data);
        } catch (IOException e) {
            System.out.println("Nie można wysłać listy graczy.");
        }

    }

    private void killClient() {
        try{
            sck.close();
        } catch (IOException e){}
        isRunning = false;
        gc.deleteClient(id);
    }

    public String readDataFromClient() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(sck.getInputStream()));
        String s = in.readLine();
        return s;
    }
    public void waitUntilGame(){
        while(isWaiting){

        }
    }
    public void sendDataToClient(String data) throws IOException{
        PrintWriter out = new PrintWriter(sck.getOutputStream(),true);
        out.write(data+"\r");
        out.flush();
    }
    public void endWaiting(){
        isWaiting = false;
    }
    public String getClientinfo(){
        return "["+id+"]"+sck.getInetAddress().toString().substring(1)+":"+sck.getPort();
    }
}
