package com.example.TCPServer;

import java.net.*;
import java.io.*;
public class TCPServer {
    public static void main(String[] args) throws Exception {

        while(true) {

            try {
                ServerSocket server = new ServerSocket(8888);
                Socket serverClient = server.accept();
                DataInputStream inStream = new DataInputStream(serverClient.getInputStream());
                DataOutputStream outStream = new DataOutputStream(serverClient.getOutputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                String clientMessage = "", serverMessage = "";
                clientMessage = inStream.readUTF();
                System.out.println("From Client: " + clientMessage);
                serverMessage = reader.readLine();
                outStream.writeUTF(serverMessage);
                outStream.flush();
                inStream.close();
                outStream.close();
                serverClient.close();
                server.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}