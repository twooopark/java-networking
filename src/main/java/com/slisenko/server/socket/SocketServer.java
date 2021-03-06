package com.slisenko.server.socket;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketServer {

    public static void main(String[] args) throws IOException {
        //ExecutorService pool = Executors.newFixedThreadPool(200);
        ServerSocket serverSocket = new ServerSocket(45000);
        log("Server started at port 45000. Listening for client connections...");

        try {
            while (true) {
                // Blocking call, never null
                Socket socket = serverSocket.accept();
                handle(socket); // Handle in same thread
//              new Thread(() -> handle(socket)).start(); // Handle in always new thread
//              pool.submit(() -> handle(socket)); // Handle in thread pool
            }
        } finally {
//          pool.shutdown();
            serverSocket.close();
        }
    }

    private static void handle(Socket socket) {
        log("client connected: " + socket.getRemoteSocketAddress());
        try {
            InputStream in = socket.getInputStream();
            OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());

            PrintWriter writer = new PrintWriter(out);
            writer.println("connected!"); // Blocking call
            writer.flush();

            // Receive message from the client
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            while(true) {
                String clientRequest = reader.readLine(); // Blocking call

                log("receive from " + socket.getRemoteSocketAddress() + " > " + clientRequest);
                writer.println("[server] OK!"); // Blocking call
                writer.flush();

                // Send response
                String serverResponse = clientRequest + ", Thanks Client";
                writer.println(serverResponse); // Blocking call
                writer.flush();

                log("send to " + socket.getRemoteSocketAddress() + " > " + serverResponse);
            }
        } catch (IOException e) {
            log("error " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                log("error closing socket " + e.getMessage());
            }
        }
    }

    private static void decoder(BasicProtocol basicProtocol){

    }

    private static void log(String message) {
        System.out.println("[" + Thread.currentThread().getName() + "] " + message);
    }
}