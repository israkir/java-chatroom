import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;


public class User {
	Socket userSocket;
	String name;
	String currentChannel;
	ArrayList blockedUsers;
	PrintWriter out;

	User(Socket socket, String channel) throws IOException {
		this.userSocket = socket;
		this.currentChannel = channel;
		this.blockedUsers = new ArrayList();
		this.out = new PrintWriter(socket.getOutputStream());
	}

	public void setUsername(String name) {
		this.name = name;
	}

	public String getUsername() {
		return this.name;
	}

	public String getCurrentChannel() {
		return this.currentChannel;
	}

	public void setCurrentChannel(String channel) {
		this.currentChannel = channel;
	}

	public PrintWriter getUserOutputStream() {
		return this.out;
	}

	public void addBlockedUser(String username) {
		blockedUsers.add(username);
	}

	public void removeBlockedUser(String username) {
		blockedUsers.remove(username);
	}

	public boolean isBlocked(String username) {
		if (this.blockedUsers.contains(username))
			return true;
		return false;
	}

}
