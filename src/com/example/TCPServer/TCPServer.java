package com.example.TCPServer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

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
            System.out.println("Image created!");

//            String[] argumentsForYOLO = new String[]{ "/home/erjseals/darknet/./darknet detect cfg/yolov3.cfg yolov3.weights data/output.jpg"};
            ProcessBuilder processBuilder = new ProcessBuilder("./darknet", "detect", "cfg/yolov3.cfg", "yolov3.weights", "data/output.jpg");
            processBuilder.directory(new File("/home/erjseals/darknet/"));

            System.out.println("Processing Image!");

            try {
                Process process = processBuilder.start();
                int errCode = process.waitFor();
                System.out.println("Echo command executed, any errors? " + (errCode == 0 ? "No" : "Yes"));
            }catch (Exception e){
                System.out.println("in processBuilder.start()");
                System.out.println(e);
            }
            in.close();
            out.close();
            socket.close();

            //Now we try to send back!
            socket = new Socket("192.168.52.207", 8080);
            if(socket.isConnected()){
                System.out.println("Socket is connected!");
            }
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            //Need to get the image and extract a bytearray
            bImage = ImageIO.read(new File("/home/erjseals/darknet/predictions.jpg"));
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(bImage, "jpg", bos);
            byte [] data2 = bos.toByteArray();

            out.write(data2, 0, data2.length);

            out.close();
            in.close();
            socket.close();

        }catch (Exception e) {
            System.out.println("general error!");
            System.out.println(e);
        }
    }
}