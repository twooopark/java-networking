package test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
			socket.connect( new InetSocketAddress( SERVER_IP, SERVER_PORT ) );

			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();


			String str = "\n" +
					"프로토콜 \n" +
					"\t정의: 통신 규약\n" +
					"\t목적: 체계를 갖춰 정확한 정보의 통신이 이루어질 수 있도록 하기 위함\n" +
					"\n" +
					"포함되는 기능\n" +
					"\t1. 에러제어 : 에러 검출(CRC, checksum, parity check)과 정정 \n" +
					"\t2. 흐름제어 : 정보 흐름의 양을 제어( 송.수신 속도를 맞춤 ) \n" +
					"\t\t- 정지대기방식 : 수신확인 후 전송\n" +
					"\t\t- 윈도우 기반: 여러 패킷을 동시 전송 (버퍼의 여유 용량에 따라 윈도우 크기 지정)\n" +
					"\t3. 순서제어 : 패킷에 순서 번호를 부여, 순서 역전 방지\n" +
					"\t4. 동기화 : 송수신 시점을 맞춤(start,stop bit(비동기통신) / 클록 신호 (동기통신)) \n" +
					"\t5. 캡슐화 : 상위 계층에서 하위 계층으로 전달될 때, 각 계층 정보를 추가하는 것(각 계층 정보를 제외하고 캡슐화 한 것)\n" +
					"\t6. 주소지정 : 주소 체계의 통일\n" +
					"\t7. 단편화 : 조각으로 나눔(패킷이 길면, 버퍼의 낭비와 지연이 발생, 크기 제약 존재)\n" +
					"\n" +
					"이와 같은 여러 기능들을 어떻게 제어하고, 표기할 것인가에 대한 규약";

//			byte[] data = str.getBytes();
//
//			BasicPacket bp = new BasicPacket( Command.PRINT, data.length, data);
//
//			ObjectOutputStream oos = new ObjectOutputStream(os);
//
//			oos.writeObject(bp);
//
//			System.out.println(data.length);

			os.write( str.getBytes( "utf-8") );

			byte[] buffer = new byte[ 256 ];
			int readByteCount = is.read( buffer );

			if( readByteCount <= -1 ) {
				System.out.println( "[client] disconnection by server" );
				return;
			}
			String data2 = new String( buffer, 0, readByteCount);
			System.out.println( "[client] received:" + data2 );
			
		} catch( ConnectException e ) {
			System.out.println( "[client] not connect" );
	    } catch( SocketTimeoutException e ) {
			System.out.println( "[client] read timeout" );
		} catch( IOException e ) {
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
