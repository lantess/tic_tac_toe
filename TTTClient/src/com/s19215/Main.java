package com.s19215;


import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main {

    public static void main(String[] args) {
        if(args.length<2)
            System.out.println("Niewystarczająca liczba argumentów.");
        else{
            try{
                InetAddress addr = InetAddress.getByName(args[0]);
                int port = Integer.parseInt(args[1]);
                Game game = new Game(addr,port);
                game.start();
            } catch (NumberFormatException e){
                System.out.println("Nieprawidłowy numer portu.");
            } catch (UnknownHostException e){
                System.out.println("Błędny adres serwera.");
            } catch (IOException e){
                System.out.println("Błąd podczas łączenia się z serwerem.");
            }
        }
    }
}
