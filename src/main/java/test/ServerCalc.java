package test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.ScatteringByteChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Stack;

/*
1. [클라이언트] 헤더 송신
2. [서버] 헤더 수신
3. [클라이언트] 바디 송신
4. [서버] 바디 수신
*/

public class ServerCalc {
    public static void main(String[] args) throws IOException {
//        ServerSocket serverSocket = new ServerSocket(StaticVal.PORT);
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(StaticVal.IP, StaticVal.PORT));

        System.out.println("[server] listening...");

        while (true) {
            //Socket socket = serverSocket.accept();
//            System.out.println("[server] connected from " + socket.getRemoteSocketAddress());
            SocketChannel socketChannel = serverSocketChannel.accept();
            System.out.println("[server] connected from " + socketChannel.getRemoteAddress());
            long startTime = System.currentTimeMillis();
            try {
//                InputStream is = socket.getInputStream();
//                OutputStream os = socket.getOutputStream();

                //2. [서버] 헤더 수신
                ByteBuffer type = ByteBuffer.allocate(2);
                ByteBuffer precision = ByteBuffer.allocate(2);
                ByteBuffer length = ByteBuffer.allocate(StaticVal.LENGTH_MAX_SIZE);
                ByteBuffer[] headerBuf = {type, precision, length};

                if (socketChannel.read(headerBuf) > 0) {
                    //pointer = 0
                    type.flip();
                    precision.flip();
                    length.flip();

                    //1byte char -> 2byte char
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < StaticVal.LENGTH_MAX_SIZE; i++)
                        sb.append((char)length.get(i));

//                    CalcPacket cp = new CalcPacket(new CalcHeader(
//                            type.getChar(),
//                            precision.getChar(),
//                            length.getLong(2)
////                            Long.parseLong(sb.toString())
//                    ));

                    Long bodyLength = Long.parseLong(sb.toString());
                    StringBuffer bodyData = new StringBuffer();

                    //4. [서버] 바디 수신
                    ByteBuffer data = ByteBuffer.allocate(StaticVal.BUFFERSIZE);

                    //버퍼 사이즈보다 작다면, 버퍼 사이즈 작게 재조정
                    if (StaticVal.BUFFERSIZE > bodyLength)
                        data = ByteBuffer.allocate(Integer.parseInt(sb.toString()));
//                    if (StaticVal.BUFFERSIZE > cp.getHeader().getLength())
//                        data = ByteBuffer.allocate((int) cp.getHeader().getLength());

                    int readByteCount = -1;
                    while ((readByteCount = socketChannel.read(data)) > 0) {
                        System.out.println("readByteCount:" + readByteCount);
                        //pointer = 0
                        data.flip();

                        //1byte char -> 2byte char 형변환
                        byte[] cb = new byte[readByteCount];
                        data.get(cb);
                        for (int i = 0; i < readByteCount; i++) {
//                            cp.append((char) cb[i]);
                            bodyData.append((char) cb[i]);
                        }
                        //pointer = 0
                        data.clear();
                    }

                    //후위표기 연산
                    Stack calcSt = new Stack();

                    //중위표기 -> 후위표기 변환
                    Stack tranSt = new Stack();
                    String rst = "";
                    //데이터 하나씩 읽어들인다.
                    for (int i = 0; i < bodyLength; i++) {
                        char x = bodyData.charAt(i);
//                    for (int i = 0; i < cp.getHeader().getLength(); i++) {
//                        char x = cp.getData().charAt(i);
                        //피연산자
                        if (!StaticVal.op.contains(x)) { //if(x < 48 || 57 < x)
                            rst += x;
                        }
                        //연산자
//                        else if( x == ')'){
//                            while((char)tranSt.peek() != '(')
//                                rst += tranSt.pop();
//                            tranSt.pop(); // '(' 제거
//                        }
                        else{
//                            rst += " ";

                            calcSt.push(rst);//임시
                            rst = "";

                            if(!tranSt.isEmpty()) {
//                                if( x != '(')
//                                    rst += tranSt.pop();
                                calcSt.push(tranSt.pop());//임시
                                tranSt.push(x);
                            }
                            else{
                                tranSt.push(x);
                            }
                        }
//                        else {
//                            st.push(rst);
//
//                            //rst += " ";
//                        }
                    }
                    calcSt.push(rst);//임시
                    calcSt.push(tranSt.pop());//임시
//                    rst += tranSt.pop();
//                    System.out.println("\n"+rst);

                    Calc(calcSt);

                }

            } catch (SocketException e) {
                System.out.println("[server] sudden closed by client");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (socketChannel != null && socketChannel.isOpen() == true) {
                        socketChannel.close();
                        System.out.println("[server] disconnection by client");
                    }
                    long endTime = System.currentTimeMillis();
                    System.out.println("수행시간: " + (endTime - startTime));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }
    }

    private static void Calc(Stack calcSt) {
        calcSt.pop();
    }
}

