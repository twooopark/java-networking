package test;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class Server {
    private static final int PORT = 5000;
    private static final int BUFFERSIZE = 256;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("[server] listening...");

        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("[server] connected from " + socket.getRemoteSocketAddress());
            try {
                InputStream is = socket.getInputStream();
                ObjectInputStream ois = new ObjectInputStream(is);

                BasicPacket bp = (BasicPacket)ois.readObject();

                switch (bp.getHeader().getCmd()){
                    case Command.PRINT :
                        System.out.println("[server] PRINT\n"+new String(bp.getData()));
                        break;
                    case Command.QUIT :
                        System.out.println("[server] QUIT");
                        break;
                    default:
                        System.out.println("[server] command error");
                            break;
                }

            } catch (SocketException e) {
                System.out.println("[server] sudden closed by client");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (socket != null && socket.isClosed() == false) {
                            socket.close();
                        System.out.println("[server] disconnection by client");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}


