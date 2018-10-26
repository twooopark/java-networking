package chat;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ChatClientApp {
	private static final String SERVER_ADDRESS = "172.21.25.143";
	private static final int SERVER_PORT = 9090;

	public static void main(String[] args) {
		Scanner scanner = null;
		Socket socket = null;
		try {
			// 키보드 연결
			scanner = new Scanner( System.in );

			// socket 생성
			socket = new Socket();

			// 연결
			socket.connect( new InetSocketAddress( SERVER_ADDRESS, SERVER_PORT ) );

			// reader/ writer 생성
			BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( socket.getInputStream(), StandardCharsets.UTF_8 ) );
			PrintWriter printWriter = new PrintWriter( new OutputStreamWriter( socket.getOutputStream(), StandardCharsets.UTF_8 ), true );

			// join 프로토콜
			System.out.print( "ID>>" );
			String nickname = scanner.nextLine();
			printWriter.println( "join " + nickname );
			printWriter.flush();

//			bufferedReader.readLine();
			
			// ChatWindow 시작
			new ChatWindow( socket ).show();

		} catch (Exception ex) {
			log( "error:" + ex );
		} finally {
			// 자원정리
			if( scanner != null ) {
				scanner.close();
			}
		}		
	}
	
	public static void log( String log ) {
		System.out.println( "[chat-client-"+Thread.currentThread().getName()+"] " + log );
	}	

}
