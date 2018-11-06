package test;

import java.awt.*;
import java.io.*;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

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

            /**************************** request calc *******************************/
            if(file.exists()) {

                //Header, ByteBuffer
                ByteBuffer headByteBuf = ByteBuffer.allocate(StaticVal.HEAD_MAX_SIZE);

                //Java -> C : le
                //headByteBuf.order(ByteOrder.LITTLE_ENDIAN);

                //type : 2byte
                headByteBuf.put(StaticVal.REQUEST);
                headByteBuf.put(StaticVal.PREC[0]);

                //데이터 길이, 문자열
                //Long -> String -> char -> byte, ex) 12L -> "12" -> '1' '2' -> 0x31 0x32
                fLength = String.valueOf(file.length());
                for(int i=0; i<StaticVal.LENGTH_MAX_SIZE - fLength.length(); i++){
                    headByteBuf.put((byte)0x30);
                }
//                headByteBuf.put(new byte[StaticVal.LENGTH_MAX_SIZE - fLength.length()]); //padding

                //2byte char -> 1byte char
                for(int i=0; i<fLength.length(); i++)
                    headByteBuf.put((byte)(fLength.charAt(i)));//-48));

                //1. [클라이언트] 헤더 송신
                headByteBuf.flip();
                os.write(headByteBuf.array());
                os.flush();

                //buffer, file read > byte write
                ByteBuffer bodyByteBuf = ByteBuffer.allocate(StaticVal.BUFFERSIZE);
                FileInputStream fis = new FileInputStream(file);

                //2. [클라이언트] 바디 송신
                int readByteCount = -1;
                while (0 < (readByteCount = fis.read(bodyByteBuf.array()))) {
                    bodyByteBuf.flip();
                    os.write(bodyByteBuf.array());
                }

                /**************************** Header recv *******************************/
                //3. [클라이언트] 헤더 수신
                byte[] buffer = new byte[StaticVal.BUFFERSIZE];

                readByteCount = is.read(buffer);
                StringBuffer s = new StringBuffer(new String(buffer));

                String type = s.substring(0,2);
                System.out.println(type);
                if(type == StaticVal.RES){
                    String prec = s.substring(2,4);
                    System.out.println(prec);
                    String leng = s.substring(4,14);
                    System.out.println(leng);

                    Long length = Long.parseLong(leng);

                }
                else{
                    System.out.println("type error:"+type);
                }


//
//                //pointer 0으로 변경하여, 처음부터 읽을 수 있도록 함.
//                typeBuf.flip();
//                precisionBuf.flip();
//                lengthBuf.flip();
//
//                //1byte char -> 2byte char
//                //type
//                StringBuffer sbType = new StringBuffer();
//                sbType.append((char) typeBuf.get());
//                sbType.append((char) typeBuf.get());
//                //prec
//                StringBuffer sbPrecision = new StringBuffer();
//                sbPrecision.append((char) precisionBuf.get());
//                sbPrecision.append((char) precisionBuf.get());
//                //leng
//                StringBuffer sbLength = new StringBuffer();
//                for (int i = 0; i < StaticVal.LENGTH_MAX_SIZE; i++)
//                    sbLength.append((char) lengthBuf.get(i));
//                Long bodyLength = Long.parseLong(sbLength.toString());
//
//                /**************************** Body recv *******************************/
//                StringBuffer bodyData = new StringBuffer();
//
//                //4. [서버] 바디 수신
//                ByteBuffer data = ByteBuffer.allocate(StaticVal.BUFFERSIZE);
//
//                //버퍼 사이즈보다 작다면, 버퍼 사이즈 작게 재조정
//                if (StaticVal.BUFFERSIZE > bodyLength)
//                    data = ByteBuffer.allocate(Integer.parseInt(sbLength.toString()));
//
//                while ( (readByteCount = is.read(data.array())) > 0 ) {
//                    System.out.println("readByteCount:" + readByteCount);
//                    data.flip();
//
//                    //1byte char -> 2byte char 형변환
//                    byte[] cb = new byte[readByteCount];
//                    data.get(cb);
//                    for (int i = 0; i < readByteCount; i++)
//                        bodyData.append((char) cb[i]);
//
//                    //만약, 더 읽어야 한다면, data 버퍼를 비워준다.
//                    //조건문이 없이, 항상 비워주면, socketChannel.read()에서 계속 같은 값 반환함. (원인 찾아야함)
//                    if(StaticVal.BUFFERSIZE == readByteCount)
//                        data.clear();
//                }
//                System.out.print(bodyData);
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
                if (socket != null && socket.isClosed() == false) {
                    //socket.close();
                    System.out.println("[client] disconnection");
                }

        }
    }
}
