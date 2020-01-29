package com.s19215;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class Game {
    private static final int EMPTY = 0,
                                X = 1,
                                O = 2;
    private int id;
    private ClientController[] player;
    private int[][] field;
    private boolean isRunning;

    public Game(ClientController clientController, ClientController rival) {
        player = new ClientController[]{clientController,rival};
        field = new int[3][3];
        isRunning = true;
    }

    public void run() throws IOException {
        id = (int)(Math.random()*2);
        while(isRunning){
            sendGameInfoOnUDP();
            sendInfoAboutTurns();
            String move = player[id].readDataFromClient();
            if(move.substring(0, 4).equals("MOVE")){
                int x = Integer.parseInt(""+move.charAt(4)),
                        y = Integer.parseInt(""+move.charAt(5));
                if(field[x][y]==Game.EMPTY){
                    field[x][y] = id == 0 ? Game.X : Game.O;
                    int win = checkForVictory();
                    if(win!=Game.EMPTY){
                        player[id].sendDataToClient(win!=-1 ? "VICTORY" : "DEFEAT");
                        swapPlayer();
                        player[id].sendDataToClient("DEFEAT");
                        isRunning = false;
                    } else{
                        swapPlayer();
                    }
                }
            }
        }


    }
    private int checkForVictory(){
        if(checkPlayer(1))
            return Game.X;
        else if (checkPlayer(8))
            return  Game.O;
        else if(!checkPlayer(0))
            return -1;
        return Game.EMPTY;
    }

    private boolean checkPlayer(int vNumber) {
        for(int i = 0; i < 3; i++)
            if(field[i][0]*field[i][1]*field[i][2]==vNumber)
                return true;
        for(int i = 0; i < 3; i++)
            if(field[0][i]*field[1][i]*field[2][i]==vNumber)
                return true;
        if(field[0][0]*field[1][1]*field[2][2]==vNumber)
            return true;
        else if(field[0][2]*field[1][1]*field[2][0]==vNumber)
            return true;
        return false;
    }

    private void sendGameInfoOnUDP() throws  IOException{
        byte[] data = getViewerData().getBytes();
        DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName("255.255.255.255"),GamesController.getPort());
        GamesController.viewerSocket.send(packet);
    }

    private String getViewerData(){
        return fieldInfo()+player[0].getClientinfo()+" i "+player[1].getClientinfo();
    }

    private void sendInfoAboutTurns() throws  IOException{
        player[id].sendDataToClient("TURN"+fieldInfo()+player[id==1?0:1].getClientinfo());
        swapPlayer();
        player[id].sendDataToClient("WAIT"+fieldInfo()+player[id==1?0:1].getClientinfo());
        swapPlayer();
    }


    private void swapPlayer(){
        id = id == 0 ? 1 : 0;
    }

    private String fieldInfo(){
        String res = "";
        for(int i = 0; i < 3; i++)
            for(int j = 0; j < 3; j++)
                res+=field[i][j];
        return res;
    }
}
