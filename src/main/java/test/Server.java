package test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;


public class Server {
	private static final int PORT = 5000;

	public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("[server] listening...");

        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("[server] connected from " + socket.getRemoteSocketAddress());

            try {
                InputStream is = socket.getInputStream();
                OutputStream os = socket.getOutputStream();

                // 명령어와 사이즈에 따라 데이터 처리하자
                while (true) {
//                    //
//                    ObjectInputStream ois = new ObjectInputStream(is);
//                    BasicPacket inPacket = (BasicPacket)ois.readObject();
//
//                    byte[] buffer = new byte[inPacket.getHeader().getSize()];
//
//                    int readByteCount = inPacket.getHeader().getSize();
//
//
//
//                    //
                    byte[] buffer = new byte[256];
                    int readByteCount = is.read(buffer);

                    if (readByteCount <= -1) {
                        System.out.println("[server] disconnection by client");
                        break;
                    }

                    String data = new String(buffer, 0, readByteCount);
                    System.out.println("[server] received:" + data);

                    os.write(data.getBytes());
                }

            } catch (SocketException e) {
                System.out.println("[server] sudden closed by client");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (socket != null && socket.isClosed() == false) {
                        socket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}


