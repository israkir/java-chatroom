import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class Server {
	private ArrayList<Channel> allChannels = new ArrayList<Channel>();
	private HashMap users = new HashMap();
	private ServerSocket ss;
	Channel defaultChannel;

	public class ClientHandler implements Runnable {
		BufferedReader in;
		PrintWriter out;
		Socket socket;

		public ClientHandler(Socket clientSocket) {
			try {
				socket = clientSocket;
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream());
				out.print("#" + defaultChannel.getName() + ":> ");
				out.flush();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		@Override
		public void run() {
			String message = null;
			String nickname = null;
			boolean login = true;

			try {
				while((message = in.readLine()) != null) {
					if (login) {
						nickname = message;
						notifyAllUsers(nickname);
						login = false;
					} else {
						System.out.println("Sending all: [" + message + "]...");
						sendAllInChannel(defaultChannel, message);
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
	}
	
	public static void main(String[] args) {
		//int port = Integer.parseInt(args[0]);
		int port = 80;
		try {
			new Server().listen(port);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void listen(int port) throws IOException {
		Socket clientSocket = null;
		PrintWriter out = null;
		BufferedReader in = null;
		BufferedReader stdin = null;

		try {
			ss = new ServerSocket(port);
			System.out.println("I am listening on [" + ss + "]...");
			defaultChannel = new Channel("default");
			allChannels.add(defaultChannel);

			while(true) {
				clientSocket = ss.accept();
				out = new PrintWriter(clientSocket.getOutputStream());
				defaultChannel.getOutputStreams().add(out);
				Thread t = new Thread(new ClientHandler(clientSocket));
				t.start();
				System.out.println("[" + clientSocket + "] connected.");
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}

	public void notifyAllUsers(String user) {
		Iterator channelIt = allChannels.iterator();
		Channel ch = null;
		PrintWriter out = null;

		while (channelIt.hasNext()) {
			ch = (Channel) channelIt.next();
			Iterator userIt = ch.getOutputStreams().iterator();
			while(userIt.hasNext()) {
				try {
					out = (PrintWriter) userIt.next();
					out.println("** User " + user + " joined #default channel");
					out.flush();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	public void sendAllInChannel(Channel ch, String message) {
		ArrayList channelOutputStreams = ch.getOutputStreams();
		Iterator it = channelOutputStreams.iterator();
		PrintWriter out = null;

		synchronized(channelOutputStreams) {
			while(it.hasNext()) {
				try {
					out = (PrintWriter) it.next();
					out.println(message);
					out.flush();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
	}

/*
	void removeConnection(Socket socket) {
		synchronized(outputStreams) {
			System.out.println("Removing connection: " + socket);
			outputStreams.remove(socket);
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
*/
}