import java.util.*;

public class Channel {
	private String name;
	private HashMap<String, User> users;
	//private ArrayList users;

	public Channel(String name) {
		this.name = name;
		this.users = new HashMap<String, User>();
		//this.users = new ArrayList();
	}

	public String getName() {
		return this.name;
	}

	public void addUser(User user) {
		users.put(user.getUsername(), user);
		//users.add(user);
	}

	public void removeUser(String nickname) { 
		//users.remove(nickname);
	}

	public HashMap<String, User> getAllUsers() {
		return this.users;
	}

	/*
	public void listUsersInChannel() {
		//Iterator it = users.`
		while(it.hasNext()) {
			User u = (User) it.next();
			System.out.println("** " + u.getUsername() + " in " + this.name);
		}
	}
	*/
	
	public User getUser(String username) {
		if (this.users.containsKey(username))
			return this.users.get(username);
		return null;
	}

}
