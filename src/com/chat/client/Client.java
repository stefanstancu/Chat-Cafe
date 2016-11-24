package com.chat.client;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by Stefan Stancu on 2015-11-13.
 */
public class Client implements Runnable{

    Socket socket;
    Scanner input;
    Scanner send = new Scanner(System.in);
    PrintWriter out;

    public Client(Socket socket){
        this.socket=socket;
    }


    public void run(){

        try{
            try{
                input = new Scanner(socket.getInputStream());
                out = new PrintWriter(socket.getOutputStream());
                out.flush();

                while (true){
                    Receive();
                }
            }
            finally {
                socket.close();
            }
        }
        catch (Exception e){
            System.out.println(e);
        }

    }

    public void Receive(){

        if(input.hasNext()) {

            String message = input.nextLine();

            if(message.toCharArray()[0]=='/'){
                //check for commands
                if(message.equals("/user")){
                    out.println("/$"+ClientGUI.userName.getText());
                    out.flush();
                }
            }
            else{
                //append the message to the gui

                ClientGUI.messageArea.append(message+"\n");
                ClientGUI.messageArea.repaint();
                if(!ClientGUI.window.isFocused())
                playNotification();
                ClientGUI.scrollDown();
                if(!ClientGUI.window.isFocused()){
                    ClientGUI.window.setIconImage(ClientGUI.iconImg_note);
                }
            }
        }
    }

    public void Send (String string){
        out.println(string);
        out.flush();
    }

    public void playNotification(){
        try {
            Clip clip = AudioSystem.getClip();
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(
                    this.getClass().getResource("notification.wav"));
            clip.open(inputStream);
            clip.start();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void Disconnect() throws IOException{
        out.println(ClientGUI.userName.getText() + " has disconnected.");
        out.flush();
        socket.close();
        JOptionPane.showMessageDialog(null, "You have disconnected!");
    }
}
