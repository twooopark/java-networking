package test;

import java.io.*;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class Client {
    private static final String SERVER_IP = "172.19.250.129";
    private static final int SERVER_PORT = 5000;

    public static void main(String[] args) {
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(SERVER_IP, SERVER_PORT));

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            OutputStream os = socket.getOutputStream();

            String inStr, outStr = null;

            System.out.println("[client] /명령어 내용 입력");
			inStr = br.readLine();
//			inStr = "/PRINT Byte Stream부터 알아보자. Byte Stream은 데이터를 Byte 단위로 주고받는 것을 말한다. 대표적인 Byte Stream은 InputStream과 OutputStream이라고 배웠다. 그렇다면 InputStream과 OutputStream을 통과하는 단위는 당연히 Byte이다. 8bit의 이진 비트를 묶으면 Byte가 되는 바로 그 Byte이다. 원래 데이터는 모두 Byte이다. 알고 보면 그림도 Byte들로 이루어져 있고, 텍스트도 Byte로 이루어져 있다. 그리고 zip이나 jar같은 압축 파일도 일단은 Byte로 되어 있다. 이 Byte들이 적절하게 변환되면 의미 있는 데이터가 되는 것이다. Byte Stream의 경우에는 원시 Byte를 그대로 주고 받겠다는 의미를 담고있다.";

            //명령어와 내용 분리
            String[] data = inStr.split(" ", 2);

            //명령어 별 처리
            String cmd = data[0].toUpperCase();
            if ("/PRINT".equals(cmd)) {
                data[0] = String.valueOf(Command.PRINT);
                outStr = data[0] + "^" + data[1];
//              outStr = data[0] + "^" + data[1].length() + "^" + data[1];  //구분자 삽입
            } else if ("/QUIT".equals(cmd)) {
                data[0] = String.valueOf(Command.QUIT);
                outStr = data[0];
            } else {
                System.out.println("[client] command error");
            }

            if(outStr != null) {
                os.write(outStr.getBytes());   //송신
            }

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
