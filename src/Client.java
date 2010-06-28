import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Client implements Runnable {
	private Socket socket;
	private String nickname;
	PrintWriter out;
	BufferedReader in;
	BufferedReader stdin;
	String userInput;
	String channel = "default";

	public Client(String host, int port) {
		boolean login = true;

		try {
			socket = new Socket(host, port);
			System.out.println("Connected to [" + socket + "]");
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			new Thread(this).start();

			System.out.print("LOGIN:> ");
			stdin = new BufferedReader(new InputStreamReader(System.in));

			while((userInput = stdin.readLine()) != null) {
				if (login) {
					setNickname(userInput);
					out.println(userInput);
					login = false;
				} else {
					out.println(nickname + ": " + userInput);
				}
			}

			out.close();
			in.close();
			stdin.close();

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public static void main(String[] args) {
		String host = "0.0.0.0";
		int port = 80;
		new Client(host, port);
	}

	@Override
	public void run() {
		String serverMessage = null;
		try {
			while((serverMessage = in.readLine()) != null) {
				System.out.println(serverMessage);
			}
		} catch (IOException ex) {
			Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

}