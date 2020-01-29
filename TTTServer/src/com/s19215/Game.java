package com.s19215;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class Game {
    private static final int EMPTY = 0,
                                X = 1,
                                Y = 2;
    private ClientController firstPlayer,
                            secondPlayer;
    private int[][] field;
    private boolean isRunning;

    public Game(ClientController clientController, ClientController rival) {
        firstPlayer = clientController;
        secondPlayer = rival;
        field = new int[3][3];
        isRunning = true;
    }

    public void run() throws IOException {
        ClientController player = Math.random()<0.5 ? firstPlayer : secondPlayer;
        while(isRunning){
            sendGameInfoOnUDP();
            sendInfoAboutTurns(player);
            String move = player.readDataFromClient();
            if(move.substring(0, 4).equals("MOVE")){
                int x = Integer.parseInt(""+move.charAt(4)),
                        y = Integer.parseInt(""+move.charAt(5));
                if(field[x][y]==Game.EMPTY){
                    field[x][y] = isPlayerFirstPlayer(player) ? Game.X : Game.Y;
                    int win = checkForVictory();
                    if(win!=Game.EMPTY){
                        player.sendDataToClient("VICTORY");
                        swapPlayer(player);
                        player.sendDataToClient("DEFEAT");
                        isRunning = false;
                    } else{
                        swapPlayer(player);
                    }
                }
            }
        }


    }
    private int checkForVictory(){
        if(checkPlayer(1))
            return Game.X;
        else if (checkPlayer(8))
            return  Game.Y;
        return Game.EMPTY;
    }

    private boolean checkPlayer(int vNumber) {
        for(int i = 0; i < 3; i++)
            if(field[i][0]*field[i][1]*field[i][2]==vNumber)
                return true;
        for(int i = 0; i < 3; i++)
            if(field[0][i]*field[1][i]*field[2][i]==vNumber)
                return true;
        if(field[0][0]*field[1][1]*field[2][2]==1)
            return true;
        else if(field[0][2]*field[1][1]*field[2][0]==1)
            return true;
        return false;
    }

    private void sendGameInfoOnUDP() throws  IOException{
        byte[] data = getViewerData().getBytes();
        DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName("255.255.255.255"),GamesController.viewerSocket.getLocalPort());
        GamesController.viewerSocket.send(packet);
    }

    private String getViewerData(){
        return "{"+firstPlayer.getClientinfo()+" -:- "+secondPlayer+"}"+fieldInfo();
    }

    private void sendInfoAboutTurns(ClientController player) throws  IOException{
        player.sendDataToClient("TURN"+fieldInfo());
        swapPlayer(player);
        player.sendDataToClient("WAIT"+fieldInfo());
        swapPlayer(player);
    }


    private void swapPlayer(ClientController player){
        player = isPlayerFirstPlayer(player) ? secondPlayer : firstPlayer;
    }

    private boolean isPlayerFirstPlayer(ClientController player){
        return player.equals(firstPlayer);
    }

    private String fieldInfo(){
        String res = "";
        for(int i = 0; i < 3; i++)
            for(int j = 0; j < 3; j++)
                res+=field[i][j];
        return res;
    }
}
