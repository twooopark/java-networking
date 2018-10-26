package chat;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {

	private static final int PORT = 9090;
	
	public static void main(String[] args) {
		ServerSocket serverSocket = null;
		List<PrintWriter> listPrintWriters = new ArrayList<PrintWriter>();
		/*
			key: nickName, value: printWriter 로 하면 어떨까?
			1. 바꿀 필요가 있는가?
				1-1. 문제점은 없는가?
		 */

		try {
			
			//1. 서버 소켓 생성
			serverSocket = new ServerSocket();
			
			//1-1. set option SO_REUSEADDR ( 종료 후 빨리 바인딩 하기 위해서 )
			//bind (SocketAddress)를 사용하여 소켓을 바인딩하기 전에 SO_REUSEADDR을 활성화하면,
			//이전 연결이 시간 제한 상태에 있더라도 소켓을 바인딩 할 수 있습니다.
			serverSocket.setReuseAddress( true );
			
			//2. binding
			InetAddress inetAddress = InetAddress.getLocalHost();
			String hostAddress = inetAddress.getHostAddress();
			serverSocket.bind( new InetSocketAddress( hostAddress, PORT ) );
			log( "bind " + hostAddress + ":" + PORT );
			
			//3. 연결 요청 기다림 (accept)
			// 프로그램은 멈추고 외부의 소켓 접속 요청을 기다린다.
			// 소켓 접속 요청이 오면, 클라이언트와 통신을 할 서버 측 소켓을 만들고, 외부 소켓과 연결한 후 레퍼런스가 반환된다.
			// 실제로 접속이 이뤄지는 서버 측 소켓의 포트는 남아있는 포트 번호 중 임의로 정해진다.
			while( true ) {
				Socket socket = serverSocket.accept();
				
				Thread thread = new ChatServerProcessThread( socket, listPrintWriters );
				thread.start();
			}
		} catch( IOException ex ) {
			log( "error:" + ex );
		} finally {
			if( serverSocket != null && serverSocket.isClosed() == false ) {
				try {
					serverSocket.close();
				} catch( IOException ex ) {
					log( "error:" + ex );
				}
			}
		}
	}
	
	public static void log( String log ) {
		System.out.println( "[chat-server-"+Thread.currentThread().getName()+"] " + log );
	}
}
