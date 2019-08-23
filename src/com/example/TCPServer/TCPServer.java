package com.example.TCPServer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

public class TCPServer {

    public static void main(String[] args) throws Exception {

        //initialize socket and input stream
        Socket socket = null;
        ServerSocket server = null;
        DataInputStream in = null;
        ByteArrayInputStream bis = null;
        BufferedImage bImage = null;
        DataOutputStream dos = null;
        ByteArrayOutputStream bos = null;

        //socket ip and port
        String phoneAddress = "10.1.10.44";
        int portNum = 8080;

        //general strings for file location
        String receivedImagePath = "/home/erjseals/darknet/data/output.jpg";
        String[] darknetArgArr = {"./darknet", "detect", "cfg/yolov3.cfg", "yolov3.weights", "data/output.jpg"};
        String darknetPath = "/home/erjseals/darknet/";
        String processedImagePath = "/home/erjseals/darknet/predictions.jpg";

        int length;
        byte[] data;
        while (true) {
            try {
                //Try to open the socket
                try {
                    server = new ServerSocket(portNum);


                    System.out.println("Server started");
                    System.out.println("Waiting for a client ...");

                    socket = server.accept();
                    System.out.println("Client accepted");
                    System.out.println("Client Address: " + socket.getRemoteSocketAddress().toString().replace("/", ""));
                } catch (Exception e) {
                    System.out.println("Error accepting Client: " + e);
                    System.out.println("Line number: " + e.getStackTrace()[0].getLineNumber());
                }

                //Socket is connected, try to get the array
                try {
                    // takes input from the client socket
                    in = new DataInputStream(socket.getInputStream());

                    // Receiving data from client
                    length = in.readInt();
                    System.out.println("Length of Array: " + length);

                    data = new byte[length];

                    if (length > 0) {
                        in.readFully(data, 0, data.length);
                    }

                    bis = new ByteArrayInputStream(data);
                    bImage = ImageIO.read(bis);
                    ImageIO.write(bImage, "jpg", new File(receivedImagePath));

                    System.out.println("Image created!");

                    in.close();
                    socket.close();

                } catch (Exception e) {
                    System.out.println("Error getting byteArray and creating file: " + e);
                    System.out.println("Line number: " + e.getStackTrace()[0].getLineNumber());
                }

                //try to call darknet for processing
                try {
                    ProcessBuilder processBuilder = new ProcessBuilder(darknetArgArr[0], darknetArgArr[1], darknetArgArr[2], darknetArgArr[3], darknetArgArr[4]);
                    processBuilder.directory(new File(darknetPath));

                    System.out.println("Processing Image!");
                    Process process = processBuilder.start();
                    int errCode = process.waitFor();
                    System.out.println("Echo command executed, any errors? " + (errCode == 0 ? "No" : "Yes"));
                } catch (Exception e) {
                    System.out.println("Error calling darknet to process: " + e);
                    System.out.println("Line number: " + e.getStackTrace()[0].getLineNumber());
                }

                try {
                    File tempFile = new File(processedImagePath);
                    boolean exists = tempFile.exists();
                    System.out.println("File exists: " + exists);
                } catch (Exception e) {
                    System.out.println("Error such that darknet did not create the image: " + e);
                    System.out.println("Line number: " + e.getStackTrace()[0].getLineNumber());
                }

                try {
                    //Now we try to send back!
                    socket = new Socket(phoneAddress, portNum);
                    if (socket.isConnected()) {
                        System.out.println("Socket is connected!");
                    }

                    dos = new DataOutputStream(socket.getOutputStream());

                    //Need to get the image and extract a bytearray

                    bImage = ImageIO.read(new File(processedImagePath));
                    bos = new ByteArrayOutputStream();
                    ImageIO.write(bImage, "jpg", bos);
                    byte[] data2 = bos.toByteArray();
                    length = data2.length;

                    System.out.println("Length of Array: " + length);

                    dos.writeInt(length);
                    dos.write(data2, 0, length);
                } catch (Exception e) {
                    System.out.println("Error in sending the new image back: " + e);
                    System.out.println("Line number: " + e.getStackTrace()[0].getLineNumber());
                }

                try {
                    socket.getOutputStream().close();
                    dos.close();
                    socket.close();
                    server.close();
                    in.close();
                    bis.close();
                    bos.close();
                } catch (Exception e) {
                    System.out.println("Error closing everything: " + e);
                    System.out.println("Line number: " + e.getStackTrace()[0].getLineNumber());
                }

            } catch (Exception e) {
                System.out.println("general error!" + e);
                System.out.println("Line number: " + e.getStackTrace()[0].getLineNumber());
            }
        }
    }
}