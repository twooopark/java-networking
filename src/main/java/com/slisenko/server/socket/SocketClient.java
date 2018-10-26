package com.slisenko.server.socket;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class SocketClient {

    public static void main(String[] args) throws IOException {
        //ExecutorService pool = Executors.newFixedThreadPool(200);
        Socket socket = new Socket(InetAddress.getLocalHost(),45000);

        try {
            //InputStream in = socket.getInputStream();
            OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());
            BufferedReader readFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            PrintWriter writer = new PrintWriter(out);

            while(true) {
                // Send response
                String input = reader.readLine();
                if ("q".equals(input.toLowerCase())) {
                    socket.close();
                    break;
                }
                String request = input + ", servertime=" + new Date().toString();
                writer.println(request); // Blocking call
                writer.println("read from " + readFromServer); // Blocking call
                writer.flush();

                log""
                log("send to " + socket.getRemoteSocketAddress() + " > " + request);
            }
        } catch (IOException e) {
            log("error " + e.getMessage());
        }
//        finally {
//            try {
//                socket.close();
//            } catch (IOException e) {
//                log("error closing socket " + e.getMessage());
//            }
//        }

    }
    private static void log(String message) {
        System.out.println("[" + Thread.currentThread().getName() + "] " + message);
    }
}