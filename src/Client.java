import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Client implements Runnable {
	private Socket socket;
	PrintWriter out = null;
	BufferedReader in = null;
	BufferedReader stdin = null;
	String userInput;

	public Client(String host, int port) {
		String nickname = null;
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
					nickname = userInput;
					out.println(nickname);
					login = false;
				} else {
					out.println(userInput);
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

}