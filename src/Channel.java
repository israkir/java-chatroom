
import java.util.ArrayList;
import java.util.Iterator;


public class Channel {
	private String name;
	private ArrayList users;

	public Channel(String name) {
		this.name = name;
		this.users = new ArrayList();
	}

	public String getName() {
		return this.name;
	}

	public void addUser(User user) {
		users.add(user);
	}

	public void removeUser(String nickname) {
		users.remove(nickname);
	}

	public ArrayList getAllUsers() {
		return this.users;
	}

	public void listUsersInChannel() {
		Iterator it = users.iterator();
		while(it.hasNext()) {
			User u = (User) it.next();
			System.out.println("** " + u.getUsername() + " in " + this.name);
		}
	}

}
