package com.chat.client;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by dapan on 2015-11-14.
 */
public class ClientGUI {

    public static JTextArea messageArea;
    public JTextField messageField;
    public static JTextField userName;
    public static JTextField IPAdress;
    public JButton send;
    public JButton toggleConnection;
    public static JFrame window;
    private Socket socket;
    public static BufferedImage iconImg;
    public static BufferedImage iconImg_note;
    public static JScrollPane scrollPane;
    public static JCheckBox autoscroll;

    public Client client;

    public static void main(String[] args){
        ClientGUI clientGUI = new ClientGUI();
    }

    public ClientGUI(){

        try {
            iconImg = ImageIO.read(this.getClass().getResource("Icon.png"));
            iconImg_note = ImageIO.read(this.getClass().getResource("Icon_note.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        window = new JFrame("Danku's Chat Cafe");
        window.setSize(700, 600);
        window.setIconImage(iconImg);

        //window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setResizable(false);
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        window.setLocation((int) screensize.getWidth() / 2 - 250, 100);
        window.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                if (connected()) {
                    try {
                        client.Disconnect();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                System.exit(0);
            }
        });
        window.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                window.setIconImage(iconImg);
            }

            @Override
            public void windowLostFocus(WindowEvent e) {

            }
        });

        JPanel panel = new JPanel();
        window.add(panel);

        messageArea = new JTextArea();
        //messageArea.setPreferredSize(new Dimension(500,750));
        messageArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        //messageArea.setLocation(10, 10);
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);

        scrollPane = new JScrollPane(messageArea);
        scrollPane.setPreferredSize(new Dimension(500, 350));
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getViewport().setView(messageArea);
        scrollPane.setPreferredSize(new Dimension(650, 450));


        IPAdress = new JTextField("localhost");
        IPAdress.setLocation(520, 70);
        IPAdress.setPreferredSize(new Dimension(150, 25));
        JLabel IPLabel = new JLabel("IP Adress:");
        IPLabel.setSize(80, 25);
        IPLabel.setLocation(520, 40);

        toggleConnection = new JButton("Connect");
        toggleConnection.setSize(100, 20);
        toggleConnection.setLocation(550, 100);

        toggleConnection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!connected()) {
                    Initialize();
                    BindListeners();
                    toggleConnection.setText("Disconnect");
                    IPAdress.setEditable(false);
                } else if (connected()) {
                    try {
                        client.Disconnect();
                        toggleConnection.setText("Connect");
                        IPAdress.setEditable(true);
                        messageArea.setText("");
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        userName = new JTextField("Anon");
        userName.setLocation(520, 10);
        userName.setPreferredSize(new Dimension(120, 25));

        messageField = new JTextField();
        messageField.setLocation(10, 380);
//        messageField.setSize(500, 25);
        messageField.setPreferredSize(new Dimension(500, 25));

        send = new JButton("Send");
        send.setPreferredSize(new Dimension(70, 23));
        send.setLocation(520, 380);

        autoscroll = new JCheckBox("Autoscroll");
        autoscroll.setSelected(true);
        autoscroll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
            }
        });
        panel.add(IPLabel);
        panel.add(IPAdress);
        panel.add(new JLabel("Username: "));
        panel.add(userName);
        panel.add(toggleConnection);
        panel.add(new JLabel("v 1.0"));
        panel.add(scrollPane);
        panel.add(messageField);
        panel.add(send);
        panel.add(autoscroll);
        window.setVisible(true);
        panel.repaint();

    }
    private  void Initialize(){
        try{
            final int PORT = 444;
            socket = new Socket(IPAdress.getText(),PORT);
            System.out.println("You have connected to: " + socket.getLocalAddress().getHostName());

            client = new Client(socket);

            //First thing is add our Username to online list
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            out.println(userName.getText());
            out.flush();

            Thread T = new Thread(client);
            T.start();
        }
        catch (Exception e){
            System.out.println(e);
            JOptionPane.showMessageDialog(null, "Server not responding. Cannot connect.");
            System.exit(0);
        }
    }
    private void BindListeners(){
        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(connected()) {
                    if (!messageField.getText().equals("")) {
                        if(messageField.getText().toCharArray()[0]=='/'){
                            client.Send(messageField.getText());
                        }else {
                            client.Send(userName.getText() + ": " + messageField.getText());
                            messageArea.append(userName.getText() + ": " + messageField.getText() + "\n");
                        }

                        scrollDown();
                        messageArea.repaint();
                        messageField.setText("");
                        messageField.requestFocus();
                    }
                }
            }
        });
        messageField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (connected()) {
                    if (!messageField.getText().equals("")) {
                        if(messageField.getText().toCharArray()[0]=='/'){
                            client.Send(messageField.getText());
                        }else {
                            client.Send(userName.getText() + ": " + messageField.getText());
                            messageArea.append(userName.getText() + ": " + messageField.getText()+"\n");
                        }

                        scrollDown();
                        messageArea.repaint();
                        messageField.setText("");
                        messageField.requestFocus();
                    }
                }
            }
        });

    }

    public static void scrollDown(){
        if(ClientGUI.autoscroll.isSelected()){
            ClientGUI.scrollPane.getVerticalScrollBar().setValue(ClientGUI.scrollPane.getVerticalScrollBar().getMaximum());
        }
        ClientGUI.messageArea.repaint();
    }

    public boolean connected(){
        boolean r = false;
        if (socket!=null){
            r=true;
        }
        return r;
    }
}
