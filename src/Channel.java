
import java.util.ArrayList;
import java.util.Iterator;


public class Channel {
	private String name;
	private ArrayList users;
	private ArrayList outputStreams;

	public Channel(String name) {
		this.name = name;
		users = new ArrayList();
		outputStreams = new ArrayList();
	}

	public String getName() {
		return this.name;
	}

	public ArrayList getOutputStreams() {
		return this.outputStreams;
	}

	public void addUser(String nickname) {
		users.add(nickname);
	}

	public void removeUser(String nickname) {
		users.remove(nickname);
	}

	public void listUsersInChannel() {
		Iterator it = users.iterator();
		while(it.hasNext()) {
			System.out.println("** " + it.next() + " in " + this.name);
		}
	}

}
