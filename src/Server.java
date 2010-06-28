import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;


public class Server {
	private ArrayList<Channel> channelList = new ArrayList<Channel>();
	private ServerSocket ss;
	Channel defaultChannel;

	public class ClientHandler implements Runnable {
		Socket clientSocket;
		User user;
		Channel userChannel;
		BufferedReader in;
		boolean login = true;

		public ClientHandler(Socket s, Channel ch) {
			try {
				clientSocket = s;
				userChannel = ch;
				user = new User(clientSocket, defaultChannel.getName());
				in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
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

			try {
				nickname = in.readLine();
				user.setUsername(nickname);
				userChannel.addUser(user);
				notifyAllUsers(userChannel, user);
				System.out.println(nickname + " login from " +
								clientSocket.getLocalSocketAddress() + " @ " + today);
				notifyUserChannel(user);

				while((message = in.readLine()) != null) {
					if (message.contains(": /tell")) {
						commandTell(userChannel, message);
						notifyUserChannel(user);
					} else if (message.contains(": /ignore")) {
						commandIgnore(userChannel, user, message);
						notifyUserChannel(user);
					} else if (message.contains(": /unignore")) {
						commandUnignore(userChannel, user, message);
						notifyUserChannel(user);
					} else if (message.contains(": /listall")) {
						commandListAll(user);
						notifyUserChannel(user);
					} else if (message.contains(": /list")) {
						commandList(user,message);
						notifyUserChannel(user);
					} else {
						System.out.println("Sending all: [" + message + "]...");
						sendAllInChannel(userChannel, user, message);
						notifyUserChannel(user);
					}
					
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		public void commandTell(Channel ch, String m) {
			String[] separate = m.split(" ");
			String username = separate[2];
			String message = separate[0].substring(0,
									separate[0].length()-1) + " tells you: ";

			for(int i=3; i<separate.length; i++)
				message += separate[i] + " ";

			messagePrivate(ch, username, message);
		}

		public void commandIgnore(Channel ch, User u, String m) {
			String[] separate = m.split(" ");
			String ignoreUsername = separate[2];
			ch.getUser(u.getUsername()).addBlockedUser(ignoreUsername);
			notifyUser(user, ignoreUsername, 1);
		}

		public void commandUnignore(Channel ch, User u, String m) {
			String[] separate = m.split(" ");
			String unignoreUsername = separate[2];
			ch.getUser(u.getUsername()).removeBlockedUser(unignoreUsername);
			notifyUser(user, unignoreUsername, 2);
		}

		public void commandListAll(User u) {
			Iterator channelIt = channelList.iterator();
			PrintWriter out = null;
			Channel ch = null;

			while (channelIt.hasNext()) {
				ch = (Channel) channelIt.next();
				out = u.getUserOutputStream();
				out.println(ch.listUsersInChannel());
				out.flush();
			}
		}

		public void commandList(User u, String m) {
			String[] separate = m.split(" ");
			String channelName = separate[2];
			Iterator channelIt = channelList.iterator();
			PrintWriter out = null;
			Channel ch = null;

			while (channelIt.hasNext()) {
				ch = (Channel) channelIt.next();
				if (ch.getName().equals(channelName)) {
					out = u.getUserOutputStream();
					out.println(ch.listUsersInChannel());
					out.flush();
				} else {
					notifyUser(user, channelName, 3);
				}
			}
		}

		public void messagePrivate(Channel ch, String username, String message) {
			User u = null;
			PrintWriter out = null;

			if ((u = ch.getUser(username)) != null) {
				out = u.getUserOutputStream();
				out.println(message + "\n#" + u.getCurrentChannel() + ":> ");
				out.flush();
			} else {
				out = u.getUserOutputStream();
				out.println("Warning:" + username + " is not in channel!" +
									"\n#" + u.getCurrentChannel() + ":> ");
				out.flush();
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
			Set keys = ch.getAllUsers().keySet();
			for (Iterator i = keys.iterator(); i.hasNext();) {
				usr = ch.getUser((String) i.next());
				try {
					if (!usr.getUsername().equals(u.getUsername())) {
						out = usr.getUserOutputStream();
						out.println("** User " + u.getUsername() +
								" joined #" + ch.getName() + " channel\n" +
								"#" + usr.getCurrentChannel() + ":> ");
						out.flush();
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	public void sendAllInChannel(Channel ch, User u, String message) {
		HashMap<String, User> users = ch.getAllUsers();
		Set keys = ch.getAllUsers().keySet();
		PrintWriter out = null;
		User usr = null;

		for (Iterator i = keys.iterator(); i.hasNext(); ) {
			usr = ch.getUser((String) i.next());
			try {
				if (!usr.getUsername().equals(u.getUsername()) &&
						!usr.isBlocked(u.getUsername())) {
					out = usr.getUserOutputStream();
					out.println(message + "\n#" + usr.getCurrentChannel() + ":> ");
					out.flush();
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	public boolean isAvailable(Channel ch, String name) {
		Set keys = ch.getAllUsers().keySet();
		Iterator userIt = keys.iterator();
		User usr = null;

		while (userIt.hasNext()) {
			usr = (User) userIt.next();
			if (usr.getUsername().equals(name))
				return false;
		}
 		return true;
	}

	public void notifyUser(User u, String s, int i) {
		PrintWriter out = u.getUserOutputStream();
		switch(i) {
			case 0:
				out.println("Error:login");
				out.flush();
				break;
			case 1:
				out.println("** All messages from " + s + " will be discarded");
				out.flush();
				break;
			case 2:
				out.println("** All messages from " + s + " will be shown");
				out.flush();
				break;
			case 3:
				out.println("** Channel " + s + " does not exist");
				out.flush();
				break;
		}
	}

	public void notifyUserChannel(User u) {
		PrintWriter out = u.getUserOutputStream();
		try {
			out.println("#" + u.getCurrentChannel() + ":> ");
			out.flush();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

}