package test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

                byte[] buffer = new byte[BUFFERSIZE];
                int readByteCount = is.read(buffer); // read() 반환값 : 스트림 데이터의 길이

                if (readByteCount > 0) {
                    String inStr = new String(buffer, 0, readByteCount);
                    String[] data = inStr.split("\\^", 2);//3); //data.length 생략
                    int cmd = Integer.parseInt(data[0]);

                    if (cmd == Command.PRINT) {
                        System.out.println("[server] PRINT:" + data[1]);//2]);
                    } else if (cmd == Command.QUIT) {
                        System.out.println("[server] QUIT");
                        break;
                    } else {
                        System.out.println("[server] command error");
                    }

                    //BUFFERSIZE 보다 내용이 긴 경우, 계속해서 읽는다.
                    while (0 < (readByteCount = is.read(buffer))) {
                        inStr = new String(buffer, 0, readByteCount);
                        System.out.println(inStr);
                    }
                }
            } catch (SocketException e) {
                System.out.println("[server] sudden closed by client");
            } catch (IOException e) {
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


