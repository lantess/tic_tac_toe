package com.s19215;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;

public class GamesController {
    public static DatagramSocket viewerSocket = null;
    private ServerSocket inSocket;
    private int clientId;
    private HashMap<Integer,ClientController> clientList;
    private ClientController waitingClient;
    private Thread connListener;

    public GamesController(int s) throws IOException {
        clientId = 0;
        inSocket = new ServerSocket(s);
        clientList = new HashMap<>();
        waitingClient = null;
        connListener = null;
        if(viewerSocket==null)
            viewerSocket = new DatagramSocket(s);
    }

    public void start(){
        runListenerThread();
    }

    private void runListenerThread(){
        connListener = new Thread(() -> {
            Socket s;
            try {
                while(true){
                    s = inSocket.accept();
                    synchronized (clientList) {
                        ClientController cc = new ClientController(this,s,clientId);
                        clientList.put(clientId,cc);
                        clientId++;
                        new Thread(cc).start();
                    }
                    s.setSoTimeout(600000);
                }
            } catch (IOException e){
                System.out.println("Socket został zamknięty.");
                throw new RuntimeException("Socket closed.");
            }
        });
        connListener.start();
    }

    public void deleteClient(int id){
        if(waitingClient!=null){
            if(waitingClient.equals(clientList.get(id)))
                waitingClient = null;
        }
        synchronized (clientList) {
            clientList.remove(id);
        }

    }

    public Collection<ClientController> getClientsList(){
        synchronized (clientList) {
            return clientList.values();
        }
    }

    public ClientController startGame(ClientController sck) {
            if (waitingClient == null) {
                waitingClient = sck;
                return null;
            } else {
                ClientController res = waitingClient;
                waitingClient = null;
                return res;
            }
    }
}
