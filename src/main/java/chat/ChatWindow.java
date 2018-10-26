package chat;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ChatWindow {
	private Socket socket;
	private PrintWriter printWriter;
	private BufferedReader bufferedReader;
	
	private Frame frame;
	private Panel pannel;
	private Button buttonSend;
	private TextField textField;
	private TextArea textArea;

	public ChatWindow( Socket socket ) {
		this.socket = socket;
		
		frame = new Frame( "채팅방" );
		pannel = new Panel();
		buttonSend = new Button( "Send" );
		textField = new TextField();
		textArea = new TextArea( 30, 80 );
	}

	public void show() throws IOException {
		
		// Button
		buttonSend.setBackground(Color.GRAY);
		buttonSend.setForeground(Color.WHITE);
		buttonSend.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent actionEvent ) {
				doMessage();
			}
		});

		// Textfield
		textField.setColumns(80);
		textField.addKeyListener( new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				char keyCode = e.getKeyChar();
				if (keyCode == KeyEvent.VK_ENTER) {
					doMessage();
				}
			}
		});

		// Pannel
		pannel.setBackground(Color.LIGHT_GRAY);
		pannel.add(textField);
		pannel.add(buttonSend);
		frame.add(BorderLayout.SOUTH, pannel);

		// TextArea
		textArea.setEditable(false);
		frame.add(BorderLayout.CENTER, textArea);

		// Frame
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				doQuit();
			}
		});
		frame.setVisible(true);
		frame.pack();
		
		bufferedReader = new BufferedReader( new InputStreamReader( socket.getInputStream(), StandardCharsets.UTF_8 ) );
		printWriter = new PrintWriter( new OutputStreamWriter( socket.getOutputStream(), StandardCharsets.UTF_8 ), true );
		new ChatClientReceiveThread().start();
	}
	
	private void doQuit() {
		try {
			printWriter.println( "quit" );
			
			if( socket != null && socket.isClosed() == false) {
				socket.close();
			}
			
			System.exit(0);
		} catch( IOException ex ) {
			ChatClientApp.log( "error:" + ex );
		}		
	}
	
	private void doMessage() {
		String message = textField.getText();
		
		// 빈 메세지
		if( "".equals( message ) ) {
			return;
		}
		
		if ( "/q".equals( message ) == true ) {
			doQuit();
			return;
		} 
		
		// 메시지 처리 (인코더)
		try {
			printWriter.println( "message " + new String( Base64.getEncoder().encode( message.getBytes( "UTF-8" ) ) ) );
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		textField.setText( "" );
		textField.requestFocus();		
	}
	
	private class ChatClientReceiveThread extends Thread {
		@Override
		public void run() {
			try {
				while( true ) {
					
					String line = bufferedReader.readLine();
					
					if( line == null ) {
						ChatClientApp.log( "Disconnection by Server" );
						break;
					}
					
					String[] tokens = line.split( " " );
					
					String message = null;
					
					if( "join".equals( tokens[ 0 ]) ) {
						message = tokens[1] + "님이 입장 하셨습니다." ;
					} else if( "quit".equals( tokens[ 0 ] ) ) {
						message = tokens[1] + "님이 퇴장 하셨습니다.";
					} else if( "message".equals( tokens[ 0 ] ) ) {
						byte[] data = Base64.getDecoder().decode( tokens[ 2 ] );
						message = tokens[1] + ":" + new String( data, 0, data.length, "utf-8" );
					} else {
						ChatClientApp.log( "unknown command:" + tokens[ 0 ] );
						continue;
					}
					
					textArea.append( message + "\n" );
				}
			} catch( SocketException ex ) {
				ChatClientApp.log( "Closed by Server" );
			} catch( IOException ex ) {
				ChatClientApp.log( "error:" + ex );
			}
		}
	}	
}