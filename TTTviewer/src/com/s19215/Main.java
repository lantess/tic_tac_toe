package com.s19215;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Main {

    public static void main(String[] args) {
        try{
            DatagramSocket sck = new DatagramSocket(Integer.parseInt(args[0]));
            sck.setSoTimeout(60000);
            byte[] data = new byte[65535];
            DatagramPacket pck = new DatagramPacket(data, data.length);
            while(true){
                sck.receive(pck);
                printField(new String(pck.getData()));
            }
        } catch (Exception e){
            System.out.println("Praca widza została zakończona.");
        }
    }

    private static void printField(String substring) {
        System.out.println("Gra "+substring.substring(9));
        System.out.println("=========");
        for(int i = 0; i < 9; i++){
            System.out.print(numerToXO(substring.charAt(i),i)+" ");
            if((i+1)%3==0)
                System.out.println();
        }
    }

    private static String numerToXO(char charAt,int i) {
        if(charAt=='1')
            return "X";
        else if(charAt=='2')
            return "O";
        else
            return ""+i;
    }
}
