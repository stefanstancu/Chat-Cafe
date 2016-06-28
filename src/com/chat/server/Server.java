package com.chat.server;

import javax.swing.*;
import javax.swing.plaf.DimensionUIResource;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;

public class Server {

    public static ArrayList<Socket> ConnectionArray = new ArrayList<Socket>();
    public static ArrayList<String> Usernames = new ArrayList<String>();
    public static JTextArea textArea;

    public static void main (String[] args) throws IOException{

        JFrame window = new JFrame("Danku's Server");
        window.setSize(400, 500);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setVisible(true);
        JPanel panel = new JPanel();
        window.add(panel);
        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(380, 480));

        panel.add(scrollPane);
        window.getContentPane().repaint();

        try{

            final int port = 444;
            ServerSocket server = new ServerSocket(port);
            System.out.println("Server started on" + InetAddress.getLocalHost());
            textArea.append(Server.getTime() + " Server started on " + InetAddress.getLocalHost()+" \n");
            scrollPane.repaint();

            while (true){
                Socket socket = server.accept();
                ConnectionArray.add(socket);

                System.out.println("Client connected from: " + socket.getInetAddress());
                textArea.append(Server.getTime() + " Client connected from: " + socket.getInetAddress()+"\n");
                panel.repaint();
                AddUserName(socket);

                ServerReturn chat = new ServerReturn(socket);
                Thread T = new Thread(chat);
                T.start();
            }
        }
        catch (Exception e){
            System.out.println(e);
        }

    }
    public static void AddUserName(Socket socket) throws IOException{

        Scanner input = new Scanner(socket.getInputStream());
        String UserName = input.nextLine();

        for (int i = 0; i < Server.ConnectionArray.size(); i++) {

            Socket temp_sock = Server.ConnectionArray.get(i);
            PrintWriter out = new PrintWriter(temp_sock.getOutputStream());
            out.println(UserName+" has joined.");
            out.flush();
        }
    }

    public static String getTime(){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return ( sdf.format(cal.getTime()) );
    }


}
