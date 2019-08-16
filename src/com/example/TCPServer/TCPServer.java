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
        Socket       socket2 = null;
        ServerSocket server = null;
        DataInputStream in	 = null;
        OutputStream out = null;

        //socket ip and port
        String phoneAddress = "192.168.108.118";
        int portNum = 8000;

        //general strings for file location
        String receivedImagePath = "/home/erjseals/darknet/data/output.jpg";
        String[] darknetArgArr = {"./darknet", "detect", "cfg/yolov3.cfg", "yolov3.weights", "data/output.jpg"};
        String darknetPath = "/home/erjseals/darknet/";
        String processedImagePath = "/home/erjseals/darknet/predictions.jpg";

        int length;
        byte[] data;
        while(true) {
            try {
                server = new ServerSocket(portNum);
                System.out.println("Server started");

                System.out.println("Waiting for a client ...");

                socket = server.accept();

                System.out.println("Client accepted");

                // takes input from the client socket
                in = new DataInputStream(socket.getInputStream());

                // Receiving data from client
                length = in.readInt();
                System.out.println("Length of Array: " + length);

                data = new byte[length];

                if (length > 0) {
                    in.readFully(data, 0, data.length);
                }

                ByteArrayInputStream bis = new ByteArrayInputStream(data);
                BufferedImage bImage = ImageIO.read(bis);
                ImageIO.write(bImage, "jpg", new File(receivedImagePath));
                System.out.println("Image created!");

                ProcessBuilder processBuilder = new ProcessBuilder(darknetArgArr[0], darknetArgArr[1], darknetArgArr[2], darknetArgArr[3], darknetArgArr[4]);
                processBuilder.directory(new File(darknetPath));

                System.out.println("Processing Image!");

                try {
                    Process process = processBuilder.start();
                    int errCode = process.waitFor();
                    System.out.println("Echo command executed, any errors? " + (errCode == 0 ? "No" : "Yes"));
                } catch (Exception e) {
                    System.out.println("in processBuilder.start()");
                    System.out.println(e);
                }

                in.close();
                socket.close();

                File tempFile = new File(processedImagePath);
                boolean exists = tempFile.exists();
                System.out.println("File exists: " + exists);

                //Now we try to send back!
                socket2 = new Socket(phoneAddress, portNum);
                if (socket.isConnected()) {
                    System.out.println("Socket is connected!");
                }

                DataOutputStream dos = new DataOutputStream(socket2.getOutputStream());

                //Need to get the image and extract a bytearray

                bImage = ImageIO.read(new File(processedImagePath));

                System.out.println("Test 1");

                ByteArrayOutputStream bos = new ByteArrayOutputStream();

                System.out.println("Test 2");

                ImageIO.write(bImage, "jpg", bos);

                System.out.println("Test 3");

                byte[] data2 = bos.toByteArray();

                System.out.println("Test 4");

                length = data2.length;

                System.out.println("Length of Array: " + length);

                dos.writeInt(length);
                dos.write(data2, 0, length);

                System.out.println("Test 5");

                socket.getOutputStream().close();
                dos.close();
                socket.close();



            } catch (Exception e) {
                System.out.println("general error!");
                System.out.println(e);
            }
        }
    }
}