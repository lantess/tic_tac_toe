package com.s19215;


import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        try{
            GamesController gc = new GamesController(Integer.parseInt(args[0]),
                                                    Integer.parseInt(args[1]));
            gc.start();
        } catch (IOException e){
            System.out.println("Port jest już zajęty.");
        } catch (NumberFormatException e){
            System.out.println("Błędny numer portu.");
        } catch (IndexOutOfBoundsException e){
            System.out.println("Nie podano żadnego portu.");
        } catch (Exception e){
            System.out.println("Próba uruchomienia zakończona błędem.");
        }
    }
}
