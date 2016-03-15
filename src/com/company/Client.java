package com.company;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client{
    private Socket clientSocket;
    private int portNumber;
    private String serverAddress;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;


    Client(String serverAddress, int port) {
        this.serverAddress = serverAddress;
        this.portNumber = port;
    }

    public static void main(String[] args) {

        String msg;
        // create the Client object
        Client client = new Client("localhost", 4000);
        client.begin();
        Scanner sc = new Scanner(System.in);
        // loop to get message from the user
        while(true) {
            System.out.print("- ");
            // read message from user
            try {
                msg = sc.nextLine() + '\n';
            }catch (Exception e){
                System.out.println("You logged out!");
                client.sendMessageToServer("User disconnected \n");
                break;
            }
            //send it to server stream
            client.sendMessageToServer(msg);
        }
    }

    public void begin() {
        // try to connect to the server: open a socket, open an input and output stream to the socket, start a thread to keep an eye to the server stream
        try {
            clientSocket = new Socket(serverAddress, portNumber);
            inputStream = new ObjectInputStream(clientSocket.getInputStream());
            outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
        }catch (Exception e) {
            System.out.println("Can't connect to server:" + e);
            e.printStackTrace();
        }
        System.out.println("You are connected!");
        new SubscribeToServer().start();
    }

    void sendMessageToServer(String msg) {
        try {
            //write this message to the stream(output stream for the user, input stream for the server)
            outputStream.writeObject(msg);
        }catch(IOException e) {
            System.out.println("Can't write to server: " + e);
            e.printStackTrace();
        }
    }

    class SubscribeToServer extends Thread {

        public void run() {
            while(true) {
                try {
                    //Client : read a message sent by Server on the stream
                    String msg = (String) inputStream.readObject();
                    System.out.println(msg);
                    System.out.print("- ");
                }catch(Exception e) {
                    System.out.println("Server closed!");
                    break;
                }
            }
        }
    }
}
