package com.gui;

import javax.swing.*;

public class GameWindow extends JFrame {

    public GameWindow(String s){
        super(s);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        add(new MenuPanel());
    }

}
