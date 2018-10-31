package test;

import java.io.*;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class Client {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 5000;

    public static void main(String[] args) {
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(SERVER_IP, SERVER_PORT));

            OutputStream os = socket.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);

            System.out.println("[client] /명령어 입력");
            BufferedReader bri = new BufferedReader(new InputStreamReader(System.in));
            String cmd = bri.readLine();

            String fileName = System.getProperty("user.dir")+"\\src\\main\\java\\test\\Google.html";
            BufferedReader br = new BufferedReader(new FileReader(fileName));

            StringBuffer sb = new StringBuffer();
            String inStr = null;

            while( (inStr = br.readLine())!=null ){
                sb.append(inStr);
            }

            BasicPacket bp = null;
            //명령어 별 처리
            cmd = cmd.toUpperCase();
            if ("/PRINT".equals(cmd)) {
                bp = new BasicPacket(Command.PRINT, sb.toString().length(), sb.toString().getBytes());
            } else if ("/QUIT".equals(cmd)) {
                bp = new BasicPacket(Command.QUIT);
            } else {
                System.out.println("[client] command error");
            }
            if (bp != null)
                oos.writeObject(bp);
        } catch (ConnectException e) {
            System.out.println("[client] not connect");
        } catch (SocketTimeoutException e) {
            System.out.println("[client] read timeout");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (socket != null && socket.isClosed() == false) {
                    socket.close();
                    System.out.println("[client] disconnection");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
