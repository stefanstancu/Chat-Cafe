package com.chat.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by dapan on 2015-11-14.
 */
public class ServerReturn implements Runnable {


    Socket socket;
    private Scanner input;
    private PrintWriter output;
    String message = "";
    public static Socket clientWithRequest;
    public ServerReturn(Socket socket) {
        this.socket = socket;
    }

    public void run() {

        try {
            try {

                input = new Scanner(socket.getInputStream());
                output = new PrintWriter(socket.getOutputStream());

                while (true) {

                    if (!input.hasNext()) {
                        return;
                    }

                    message = input.nextLine();
                    System.out.println(socket.getInetAddress()+" :  " + message);
                    Server.textArea.append(Server.getTime()+"  "+socket.getInetAddress()+" :  " + message+"\n");

                    if(message.toCharArray()[0]=='/'){
                        if(message.equals("/users")){
                            clientWithRequest = socket;
                            System.out.println("Command Accepted");
                            //send command for receiving users from all clients
                            for (int i = 0; i < Server.ConnectionArray.size(); i++) {
                                Socket temp = Server.ConnectionArray.get(i);
                                    PrintWriter temp_out = new PrintWriter(temp.getOutputStream());
                                    temp_out.println("/user");
                                    temp_out.flush();
                            }
                        }
                        else if(message.equals("/spook")){
                            for (int i = 0; i < Server.ConnectionArray.size(); i++) {
                                Socket temp = Server.ConnectionArray.get(i);
                                PrintWriter temp_out = new PrintWriter(temp.getOutputStream());
                                temp_out.print("     _.--\"\"--._        \n" +
                                        "   .\"          \".     \n" +
                                        "  | .   `      ` |    \n" +
                                        "  \\(            )/   \n" +
                                        "   \\)__.    _._(/  \n" +
                                        "   //   >..<   \\\\  \n" +
                                        "   |__.' vv '.__/ \n" +
                                        "      l'''\"''l    \n" +
                                        "      \\_    _/  \n" +
                                        " _      )--(     _  \n" +
                                        "| '--.__)--(_.--' |  \n" +
                                        " \\ |`----''----'| / \n" +
                                        "  ||  `-'  '--' || \n" +
                                        "  || `--'  '--' || \n" +
                                        "  |l `--'--'--' |l  \n" +
                                        " |__|`--'  `--'|__| \n" +
                                        " |  |    )-(   |  | \n" +
                                        "  ||     )-(    \\|| \n" +
                                        "  || __  )_(  __ \\\\  \n" +
                                        "  ||'  `-   -'  \\ \\\\ \n" +
                                        "  ||\\_   `-'   _/ |_\\ \n" +
                                        " /_\\ _)J-._.-L(   /`-\\ \n" +
                                        "|`- I_)O /\\ O( `--l\\\\\\| \n" +
                                        "||||( `-'  `-') .-' ||| \n" +
                                        " \\\\\\ \\       / /   /// \n" +
                                        "    \\ \\     / / \n" +
                                        "     \\ \\   / / \n" +
                                        "     /  \\ /  \\ \n" +
                                        "     |_()I()._| \n" +
                                        "     \\   /\\   / \n" +
                                        "      | /  \\ | \n" +
                                        "      | |   \\ \\ \n" +
                                        "      | |    \\ \\ \n" +
                                        "      | |     \\ \\ \n" +
                                        "      | |-nabis\\ \\_ \n" +
                                        "      | |      /-._\\ \n" +
                                        "     |.-.\\    //.-._) \n" +
                                        "      \\\\\\\\   /// \n" +
                                        "       \\\\\\\\-''' ");
                                temp_out.flush();
                            }
                        }
                        else if(message.contains("/$")){
                            Server.Usernames.add(message.substring(2,message.length()));
                            System.out.println("I just added "+message);
                        }

                    }
                    else {
                        //send it out
                        for (int i = 0; i < Server.ConnectionArray.size(); i++) {
                            Socket temp = Server.ConnectionArray.get(i);
                            if (temp != socket) {
                                PrintWriter temp_out = new PrintWriter(temp.getOutputStream());
                                temp_out.println(message);
                                temp_out.flush();
                            }
                        }
                    }

                    if(Server.Usernames.size()==Server.ConnectionArray.size()){
                            System.out.println("printing the stuff");
                            PrintWriter temp_out = new PrintWriter(clientWithRequest.getOutputStream());
                            temp_out.println("*******************");
                            temp_out.println("There are " + Server.Usernames.size()+" users connected:");
                            for (int i = 0; i < Server.Usernames.size(); i++) {
                                temp_out.println(Server.Usernames.get(i));
                            }
                            temp_out.println("*******************");
                            temp_out.flush();
                        Server.textArea.append(Server.getTime()+"User list sent to "+clientWithRequest.getInetAddress());
                            clientWithRequest = null;
                            Server.Usernames.clear();
                            System.out.println("usernames cleared");

                        }
                }
            } finally {
                for (int i = 0; i < Server.ConnectionArray.size(); i++) {
                    if (Server.ConnectionArray.get(i) == socket) {
                        Server.ConnectionArray.remove(i);
                        break;
                    }
                }
                for (int i = 0; i < Server.ConnectionArray.size(); i++) {
                    Socket temp = Server.ConnectionArray.get(i);
                    PrintWriter temp_out = new PrintWriter(temp.getOutputStream());
                    temp_out.println(temp.getInetAddress() + " has disconnected!");
                    temp_out.flush();
                    //This was sent out to everyone, but we echo it to the console as well;
                    System.out.println(temp.getInetAddress()+ " has disconnected!");
                }
                socket.close();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
