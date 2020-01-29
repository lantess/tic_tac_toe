package com.s19215;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
                while(isRunning)
                    for (String command : readDataFromClient()) {
                        if(command.equals("PLAY"))
                            startGame();
                        else if(command.equals("LOGOUT"))
                            killClient();
                        else if(command.equals("LIST"))
                            sendClientList();
                        else
                            sendDataToClient("ERROR");
                    }
            } catch (IOException e){
                System.out.println("Problem z połączeniem z klientem");
            }
    }

    private void startGame() throws IOException {
        ClientController rival = gc.startGame(this);
        if(rival==null){
            isWaiting = true;
            waitUntilGame();
        }
        else{
            Game game = new Game(this, rival);
            game.run();
            rival.endWaiting();
        }
    }

    private void sendClientList() {
        Collection<ClientController> l = gc.getClientsList();
        String data = "";
        l.stream()
            .map( n -> n.getClientinfo())
            .reduce(data, (a,b) -> a+b+"\n");
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

    public List<String> readDataFromClient() throws IOException {
        List<String> res = new ArrayList<>();
        BufferedReader in = new BufferedReader(new InputStreamReader(sck.getInputStream()));
        String s;
        while((s=in.readLine())!=null){
            res.add(s);
        }
        in.close();
        return res;
    }
    public void waitUntilGame(){
        while(isWaiting){

        }
    }
    public void sendDataToClient(String data) throws IOException{
        PrintWriter out = new PrintWriter(sck.getOutputStream());
        out.write(data);
        out.flush();
        out.close();
    }
    public void endWaiting(){
        isWaiting = false;
    }
    public String getClientinfo(){
        return "["+id+"]"+sck.getInetAddress()+":"+sck.getPort();
    }
}
