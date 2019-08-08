package com.example.TCPServer;

import javax.imageio.ImageIO;
import javax.xml.crypto.Data;
import java.awt.image.BufferedImage;
import java.net.*;
import java.io.*;
import java.util.Arrays;

public class TCPServer {

    public static void main(String[] args) throws Exception {

        //initialize socket and input stream
        Socket		 socket = null;
        ServerSocket server = null;
        DataInputStream in	 = null;
        OutputStream out = null;
        int length;
        byte[] data;

        try {
            server = new ServerSocket(8080);
            System.out.println("Server started");

            System.out.println("Waiting for a client ...");

            socket = server.accept();

            System.out.println("Client accepted");

            // takes input from the client socket
            in = new DataInputStream(socket.getInputStream());
            //writes on client socket
            out = new DataOutputStream(socket.getOutputStream());

            // Receiving data from client
            length = in.readInt();
            System.out.println("Length of Array: "+ length);

            data = new byte[length];

            if(length > 0 ) {
                in.readFully(data, 0, data.length);
            }

            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            BufferedImage bImage = ImageIO.read(bis);
            ImageIO.write(bImage, "jpg", new File("/home/erjseals/darknet/data/output.jpg"));
            System.out.println("image created!");

            Runtime rt = Runtime.getRuntime();
            Process pr = rt.exec("/home/erjseals/darknet/./darknet detect cfg/yolov3.cfg yolov3.weights data/output.jpg");

            in.close();
            out.close();
            socket.close();
        }catch (Exception e) {
            System.out.println(e);
        }
    }
}