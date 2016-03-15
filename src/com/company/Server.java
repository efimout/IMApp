package com.company;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private List<Client> clients;
    public int clientNumber;
    private int portNumber;

    Server(){
    this.clients=new ArrayList<>();
    this.clientNumber=0;
    }

    public int getPort(){
        return this.portNumber;
    }

    public void setPort(int portNumber){
        this.portNumber=portNumber;
    }

    public static void main(String[] args) {

        Server server = new Server();
        server.setPort(4000);
        try {
            server.begin(server);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void begin(Server server) throws IOException {
        System.out.println("Server is running on port " + server.getPort() + ".");
       //create a Server Socket
        ServerSocket serverSocket=new ServerSocket(server.getPort());
        //run all the time and accept any connections
        //create a Socket for each connection, update the client list, start the client-thread
        while (true) {
            Socket clientSocket = serverSocket.accept();
            clientNumber++;
            Client client = new Client(clientSocket,clientNumber);
            clients.add(client);
            client.start();
        }
    }

    public void spreadTheMessage(String message,int user){

        System.out.print(message);

        for(int i=0; i<clients.size();i++) {
            Client client = clients.get(i);
            if (client.getClientId() != user) {
                if (!client.writeMessage(message)) {
                    // If a user is disconnected, remove it
                    clients.remove(i);
                    System.out.println("User" + client.getClientId() + " - removed from list.");
                }
            }
        }
    }

  class Client extends Thread{

      Socket socket;
      String clientMsg;
      ObjectInputStream inputStream;
      ObjectOutputStream outputStream;
      int id;


      Client(Socket socket, int clientId) {
          this.id=clientId;
          this.socket = socket;

          try{
              outputStream = new ObjectOutputStream(socket.getOutputStream());
              inputStream = new ObjectInputStream(socket.getInputStream());
              System.out.println("User"+id+ " just connected.");
          }catch (IOException e) {
              System.out.println("Connection failed for User"+id);
              e.printStackTrace();
          }
      }

     private int getClientId(){
         return this.id;
     }

      public void run() {
          while(true) {
              // Server : read a message from the stream
              try {
                  clientMsg = (String) inputStream.readObject();
                  spreadTheMessage("User" + id + ": " + clientMsg,id);
              }catch (Exception e) {
                  break;
              }
          }
      }

      private boolean writeMessage(String msg) {
          // write the message to the stream
          try {
              outputStream.writeObject(msg);
          }catch(IOException e) {
              return false;
          }
          return true;
      }
  }
}