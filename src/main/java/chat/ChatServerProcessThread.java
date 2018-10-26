package chat;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ChatServerProcessThread extends Thread {
	private static final String PROTOCOL_DIVIDER = " ";
	
	private String nickname;
	private Socket socket;
	private List<PrintWriter> listPrintWriters;

	public ChatServerProcessThread( Socket socket, List<PrintWriter> listPrintWriters ) {
		this.socket = socket;
		this.listPrintWriters = listPrintWriters;
	}

	@Override
	public void run() {
		BufferedReader bufferedReader = null;
		PrintWriter printWriter = null;
		
		try {
			//스트림 얻기
			bufferedReader = new BufferedReader( new InputStreamReader( socket.getInputStream(), StandardCharsets.UTF_8 ) );
			printWriter = new PrintWriter( new OutputStreamWriter( socket.getOutputStream(), StandardCharsets.UTF_8 ), true );
			
			//리모트 호스트 정보 얻기
			InetSocketAddress inetSocketAddress = (InetSocketAddress)socket.getRemoteSocketAddress();
			String remoteHostAddress = inetSocketAddress.getAddress().getHostAddress();
			int remoteHostPort = inetSocketAddress.getPort();
			ChatServer.log( "연결됨 from " + remoteHostAddress + ":" + remoteHostPort );

			
			//요청처리
			while( true ) {
				String request = bufferedReader.readLine();
				if( request == null ) {
					ChatServer.log( "클라이언트로 부터 연결 끊김" );
					doQuit( printWriter );
					break;
				}
				// 디코더
				String[] tokens = request.split( PROTOCOL_DIVIDER );
				if( "join".equals( tokens[0] ) ) {
					doJoin( printWriter, tokens[1] );
				} else if( "message".equals( tokens[0] ) ){
					doMessage( tokens[1] );
				} else if( "quit".equals( tokens[0] ) ){
					doQuit( printWriter );
					break;
				} else {
					ChatServer.log( "에러: 알수 없는 요청명령(" + tokens[0] + ")" );
				}
				
			}
			
		} catch( IOException ex ) {
			ChatServer.log( "error:" + ex );
			// 클라이언트의 비정상 종료 ( 명시적으로 소켓을 닫지 않음 )
			doQuit( printWriter );
		} finally {
			try {
				//자원정리
				bufferedReader.close();
				printWriter.close();
				if( socket.isClosed() == false ) {
					socket.close();
				}
			} catch( IOException ex ) {
				ChatServer.log( "error:" + ex );
			}
		}
	}
	
	private void doQuit( PrintWriter printWriter ) {
		// PrintWriter 제거
		removePrintWriter( printWriter );
		
		//퇴장 메세지 브로드캐스팅
		String data = "quit " + nickname;
		broadcast( data );
	}
	
	private void doMessage( String message ) {
		String data = "message " + nickname + " " + message;
		broadcast( data );
	}
	
	private void doJoin( PrintWriter printWriter, String nickname ) {
		//1. 닉네임 저장
		this.nickname = nickname;


		//2. 메세지 브로드캐스팅
		String data = "join " + nickname;
		broadcast( data );

		//3. Writer Pool 에 저장
		addPrintWriter( printWriter );

		//4. ack
		printWriter.println( data );
		printWriter.flush();

		// 미해결 : addPrintWriter 후에, broadcast 하면, 4번을 생략할 수 있을텐데, 왜 안되는지 모르겠음.

	}
	// synchronized ( ) : 동기화
	// 공동의 자원(파일이나 메모리 블록)을 공유하는 경우,
	// 순서를 잘 맞추어 다른 쓰레드가 자원을 사용하고 있는 동안 한 쓰레드가 절대 자원을 변경할 수 없도록 한다.
	// listPrintWriters의 접근, 동작을 동기화한다.

	private void addPrintWriter( PrintWriter printWriter ) {
		synchronized( listPrintWriters ) {
			listPrintWriters.add( printWriter );
		}
	}

	private void removePrintWriter( PrintWriter printWriter ) {
		synchronized( listPrintWriters ) {
			listPrintWriters.remove( printWriter );
		}
	}

	// 동기화 블록 (listPrintWriters에 대한 동기)
	private void broadcast( String data ) {
		synchronized( listPrintWriters ) {
			int count = listPrintWriters.size();
			for( int i = 0; i < count; i++ ) {
				PrintWriter printWriter = listPrintWriters.get( i );
				printWriter.println( data );
				printWriter.flush();
			}
		}
	}
	// 동기화 메소드
//	synchronized private void broadcast( String data ) {
//			int count = listPrintWriters.size();
//			for( int i = 0; i < count; i++ ) {
//				PrintWriter printWriter = listPrintWriters.get( i );
//				printWriter.println( data );
//				printWriter.flush();
//			}
//	}

}
