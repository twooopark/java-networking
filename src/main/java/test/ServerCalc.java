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

public class ServerCalc {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(StaticVal.IP, StaticVal.PORT));

        System.out.println("[server] listening...");

        while (true) {
            SocketChannel socketChannel = serverSocketChannel.accept();
            System.out.println("[server] connected from " + socketChannel.getRemoteAddress());
            long startTime = System.currentTimeMillis();
            try {
                /**************************** Header recv *******************************/
                //3. [서버] 헤더 수신
                ByteBuffer typeBuf = ByteBuffer.allocate(2);
                ByteBuffer precisionBuf = ByteBuffer.allocate(2);
                ByteBuffer lengthBuf = ByteBuffer.allocate(StaticVal.LENGTH_MAX_SIZE);
                ByteBuffer[] headerBuf = {typeBuf, precisionBuf, lengthBuf};

                if (socketChannel.read(headerBuf) > 0) {
                    //pointer 0으로 변경하여, 처음부터 읽을 수 있도록 함.
                    typeBuf.flip();
                    precisionBuf.flip();
                    lengthBuf.flip();

                    //1byte char -> 2byte char
                    StringBuffer sbType = new StringBuffer();
                    sbType.append((char)typeBuf.get());
                    sbType.append((char)typeBuf.get());

                    StringBuffer sbPrecision = new StringBuffer();
                    sbPrecision.append((char)precisionBuf.get());
                    sbPrecision.append((char)precisionBuf.get());

                    StringBuffer sbLength = new StringBuffer();
                    for (int i = 0; i < StaticVal.LENGTH_MAX_SIZE; i++)
                        sbLength.append((char)lengthBuf.get(i));
                    Long bodyLength = Long.parseLong(sbLength.toString());

                    /**************************** Body recv *******************************/
                    StringBuffer bodyData = new StringBuffer();

                    //4. [서버] 바디 수신
                    ByteBuffer data = ByteBuffer.allocate(StaticVal.BUFFERSIZE);

                    //버퍼 사이즈보다 작다면, 버퍼 사이즈 작게 재조정
                    if (StaticVal.BUFFERSIZE > bodyLength)
                        data = ByteBuffer.allocate(Integer.parseInt(sbLength.toString()));

                    int readByteCount = -1;

                    while ( (readByteCount = socketChannel.read(data)) > 0 ) {
                        System.out.println("readByteCount:" + readByteCount);
                        data.flip();

                        //1byte char -> 2byte char 형변환
                        byte[] cb = new byte[readByteCount];
                        data.get(cb);
                        for (int i = 0; i < readByteCount; i++)
                            bodyData.append((char) cb[i]);

                        //만약, 더 읽어야 한다면, data 버퍼를 비워준다.
                        //조건문이 없이, 항상 비워주면, socketChannel.read()에서 계속 같은 값 반환함. (원인 찾아야함)
                        if(StaticVal.BUFFERSIZE == readByteCount)
                            data.clear();
                    }

                    /**************************** Calc *******************************/
                    //후위표기 연산(값 저장)
                    Stack<String> valSt = new Stack();

                    //중위표기 -> 후위표기 변환(연산자 저장)
                    Stack<String> opSt = new Stack();
                    String valTemp = "";

                    //연산
                    for (int i = 0; i < bodyLength; i++) {
                        char token = bodyData.charAt(i);
                        //피연산자
                        if (!StaticVal.op.contains(token)) { //if(token < 48 || 57 < token)
                            valTemp += token;
                        }
                        //연산자
                        else{
                            //피연산자 푸시, valTemp 초기화
                            valSt.push(valTemp);//임시
                            valTemp = "";

                            //앞에 연산자가 있는 경우
                            if (!opSt.isEmpty()) {
                                //calculator : valSt에서 pop 한 값 2개, opSt로 연산
                                valSt.push(String.valueOf(calculator(valSt, opSt)));
                                opSt.push(String.valueOf(token));
                            }
                            //첫 연산자는 바로 push
                            else {
                                opSt.push(String.valueOf(token));
                            }
                        }
                    }
                    valSt.push(valTemp);//임시
                    String result = String.valueOf(calculator(valSt, opSt));
                    System.out.println("valTemp: "+ result);

                    /**************************** resp *******************************/
                    //response Header
                    typeBuf.clear();
                    lengthBuf.clear();

                    //type
                    typeBuf.put(StaticVal.RESPONSE);

                    //length
                    // int -> String -> char -> byte, ex) 12L -> "12" -> '1' '2' -> 0x31 0x32
                    String resultLength = String.valueOf(result.length());
                    for(int i=0; i<StaticVal.LENGTH_MAX_SIZE - resultLength.length(); i++)
                        lengthBuf.put((byte)0x30);
                    for(int i=0; i<resultLength.length(); i++)
                        lengthBuf.put((byte)(resultLength.charAt(i)));

                    //5.[서버] 응답 헤더 송신
                    typeBuf.flip();
                    precisionBuf.flip();
                    lengthBuf.flip();

                    socketChannel.write(headerBuf);

                    //6.[서버] 응답 바디 송신
                    ByteBuffer bodyBuf = ByteBuffer.allocate(result.length());
                    bodyBuf.put(result.getBytes());
                    bodyBuf.flip();

                    socketChannel.write(bodyBuf);
                }

            } catch (SocketException e) {
                System.out.println("[server] sudden closed by client");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                    if (socketChannel != null && socketChannel.isOpen() == true) {
                        //socketChannel.close();
                        System.out.println("[server] disconnection by client");
                    }
                    long endTime = System.currentTimeMillis();
                    System.out.println("수행시간: " + (endTime - startTime));
            }
        }
    }

    private static int calculator(Stack<String> valSt, Stack<String> opSt) {
        int t1 = Integer.parseInt(valSt.pop());
        int t2 = Integer.parseInt(valSt.pop());
        int temp = 0;
        switch (opSt.pop().charAt(0)) {
            case '+':
                temp = t2 + t1;
                break;
            case '-':
                temp = t2 - t1;
                break;
            case '*':
                temp = t2 * t1;
                break;
            case '/':
                if (t1 == 0) {
                    System.out.print("Cannot divide by'0'");
                    break;
                }
                temp = t2 / t1;
                break;
            default:
                break;
        }
        return temp;
    }
}

