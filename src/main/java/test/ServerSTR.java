package test;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class ServerSTR {
    private static final int PORT = 5000;
    private static final int BUFFERSIZE = 256;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("[server] listening...");

        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("[server] connected from " + socket.getRemoteSocketAddress());
            long startTime = System.currentTimeMillis();
            try {
                InputStream is = socket.getInputStream();

                byte[] buffer = new byte[BUFFERSIZE];

                int readByteCount = is.read(buffer); // read() 반환값 : 스트림 데이터의 길이

                if (readByteCount > 0) {
                    String inStr = new String(buffer, 0, readByteCount);
                    System.out.println(inStr);
//                    String[] data = inStr.split(",", 2);//3);
//                    int cmd = Integer.parseInt(data[0]);
//
//                    if (cmd == Command.PRINT) {
//                        System.out.println("[server] PRINT\n" + data[1]);//2]);
//                    } else if (cmd == Command.QUIT) {
//                        System.out.println("[server] QUIT");
//                        break;
//                    } else {
//                        System.out.println("[server] command error");
//                    }
//
//                    //BUFFERSIZE 보다 내용이 긴 경우, 계속해서 읽는다.
//                    while (0 < (readByteCount = is.read(buffer))) {
//                        inStr = new String(buffer, 0, readByteCount);
//                        System.out.print(inStr);
//                    }
                }


            } catch (SocketException e) {
                System.out.println("[server] sudden closed by client");
            } catch (IOException e) {
                e.printStackTrace();
            }  finally {
                try {
                    if (socket != null && socket.isClosed() == false) {
                            socket.close();
                        System.out.println("[server] disconnection by client");
                    }
                    long endTime = System.currentTimeMillis();
                    System.out.println("수행시간: "+(endTime-startTime));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }
    }
}

