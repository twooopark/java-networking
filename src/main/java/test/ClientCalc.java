package test;

import java.io.*;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
/*
1. [클라이언트] 헤더 송신
2. [서버] 헤더 수신
3. [클라이언트] 바디 송신
4. [서버] 바디 수신
*/

public class ClientCalc {

    public static void main(String[] args) {
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(StaticVal.IP, StaticVal.PORT));
            System.out.println("[client] connected");

            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();

            String fLength = null, inStr = null, outStr = null;

            //파일을 통해, 연산할 내용을 문자열로 입력받는다.
            String filePath = System.getProperty("user.dir")+"\\src\\main\\java\\test";
            File file = new File(filePath, "test.txt");

            if(file.exists()) {

                //Header, ByteBuffer
                ByteBuffer headByteBuf = ByteBuffer.allocate(StaticVal.HEAD_MAX_SIZE);

                //Java -> C : le
                //headByteBuf.order(ByteOrder.LITTLE_ENDIAN);

                //type : 2byte
                headByteBuf.put((byte)0);
                headByteBuf.put((byte)1);

                //precision : 2byte
                headByteBuf.put((byte)0);
                headByteBuf.put((byte)0);

                //데이터 길이, 문자열
                //Long -> String -> char -> byte
                fLength = String.valueOf(file.length());
                System.out.println(fLength);
                headByteBuf.put(new byte[StaticVal.LENGTH_MAX_SIZE - fLength.length()]); //padding

                //2byte char -> 1byte char
                for(int i=0; i<fLength.length(); i++)
                    headByteBuf.put((byte)((int)fLength.charAt(i)-48));

                //1. [클라이언트] 헤더 송신
                headByteBuf.flip();
                os.write(headByteBuf.array());
                os.flush();

                //buffer, file read > byte write
                ByteBuffer bodyByteBuf = ByteBuffer.allocate(StaticVal.BUFFERSIZE);
                FileInputStream fis = new FileInputStream(file);

                int readByteCount = -1;
                while (0 < (readByteCount = fis.read(bodyByteBuf.array()))) {
                    bodyByteBuf.flip();
                    //5. [클라이언트] 바디 송신
                    os.write(bodyByteBuf.array());
                }
            }

        } catch (ConnectException e) {
            System.out.println("[client] not connect");
        } catch (SocketTimeoutException e) {
            System.out.println("[client] read timeout");
        } catch (FileNotFoundException e){
            System.out.println("[client] file not found");
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
