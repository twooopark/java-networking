package test;

import java.io.*;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ClientSTR {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 5000;

    public static void main(String[] args) {
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(SERVER_IP, SERVER_PORT));

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            OutputStream os = socket.getOutputStream();


            String inStr, outStr = null;

            System.out.println("[client] /명령어 내용 입력");
//			inStr = br.readLine();
//          inStr = "/PRINT Most of the examples we've seen so far use unbuffered I/O. This means each read or write request is handled directly by the underlying OS. This can make a program much less efficient, since each such request often triggers disk access, network activity, or some other operation that is relatively expensive. o reduce this kind of overhead, the Java platform implements buffered I/O streams. Buffered input streams read data from a memory area known as a buffer; the native input API is called only when the buffer is empty. Similarly, buffered output streams write data to a buffer, and the native output API is called only when the buffer is full.A program can convert an unbuffered stream into a buffered stream using the wrapping idiom we've used several times now, where the unbuffered stream object is passed to the constructor for a buffered stream class. Here's how you might modify the constructor invocations in the CopyCharacters example to use buffered I/O";
            //명령어와 내용 분리
            inStr = "/CALC "+"123+525*2354";
            System.out.println(inStr.getBytes().length);

            String[] data = inStr.split(" ", 2);

            //명령어 별 처리
            String cmd = data[0].toUpperCase();
            if ("/PRINT".equals(cmd)) {
                data[0] = String.valueOf(Command.PRINT);
                //구분자 삽입
//              outStr = data[0] + "," + data[1];
                outStr = data[0] + "," + data[1].length() + "," + data[1];
            } else if ("/CALC".equals(cmd)) {
                outStr = String.valueOf(Command.REQUEST) + (data[1].length()) + data[1];
            } else if ("/QUIT".equals(cmd)) {
                data[0] = String.valueOf(Command.QUIT);
                outStr = data[0];
            } else {
                System.out.println("[client] command error");
            }

            if(outStr != null) {
                os.write(outStr.getBytes());   //송신
            }
/*
            //CSV 변환
            String csv = String.format("%s,%s,%s", bp.getHeader().getCmd(), bp.getHeader().getSize(), new String(bp.getData()));
            System.out.println(csv);

            //JSON 변환
            String json = String.format(
                    "{\"cmd\":\"%s\",\"size\":\"%s\",\"data\":\"%s\"}",
                    bp.getHeader().getCmd(), bp.getHeader().getSize(), new String(bp.getData()));
            System.out.println(json);
*/

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
