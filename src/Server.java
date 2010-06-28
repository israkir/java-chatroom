import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;


public class Server {
	private ArrayList<Channel> channelList = new ArrayList<Channel>();
	private ServerSocket ss;
	Channel defaultChannel;

	public class ClientHandler implements Runnable {
		Socket clientSocket;
		User user;
		Channel userChannel;
		BufferedReader in;
		PrintWriter out;

		public ClientHandler(Socket s, Channel ch) {
			try {
				clientSocket = s;
				userChannel = ch;
				user = new User(clientSocket, defaultChannel.getName());
				in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				out = new PrintWriter(clientSocket.getOutputStream());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		@Override
		public void run() {
			SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
			Date date = new Date();
			String today = sdf.format(date);
			String message = null;
			String nickname = null;
			boolean login = true;

			try {
				while((message = in.readLine()) != null) {
					if (login) {
						nickname = message;
						user.setUsername(nickname);
						userChannel.addUser(user);
						notifyAllUsers(userChannel, user);
						//userChannel.listUsersInChannel();
						System.out.println(nickname + " login from " + 
								clientSocket.getLocalSocketAddress() + " @ " + today);
						login = false;
					} else {
						System.out.println("Sending all: [" + message + "]...");
						sendAllInChannel(userChannel, user, message);
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

		try {
			ss = new ServerSocket(port);
			System.out.println("Server is listening on: " +
					ss.getLocalSocketAddress());
			defaultChannel = new Channel("default");
			channelList.add(defaultChannel);

			while(true) {
				clientSocket = ss.accept();
				Thread t = new Thread(new ClientHandler(clientSocket, defaultChannel));
				t.start();
				//System.out.println("[" + clientSocket + "] connected.");
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}

	public void notifyAllUsers(Channel ch, User u) {
		Iterator channelIt = channelList.iterator();
		PrintWriter out = null;
		User usr = null;

		while (channelIt.hasNext()) {
			ch = (Channel) channelIt.next();
			Iterator userIt = ch.getAllUsers().iterator();
			while(userIt.hasNext()) {
				usr = (User) userIt.next();
				try {
					if (!usr.getUsername().equals(u.getUsername())) {
						out = usr.getUserOutputStream();
						out.println("** User " + u.getUsername() +
								" joined " + ch.getName() + " channel");
						out.flush();
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	public void sendAllInChannel(Channel ch, User u, String message) {
		ArrayList users = ch.getAllUsers();
		Iterator userIt = users.iterator();
		PrintWriter out = null;
		User usr = null;

		while(userIt.hasNext()) {
			usr = (User) userIt.next();
			try {
				if (!usr.getUsername().equals(u.getUsername())) {
					out = usr.getUserOutputStream();
					out.println(message);
					out.flush();
				}
			} catch (Exception e1) {
				e1.printStackTrace();
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