import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.TimerTask;

public class Task extends TimerTask {
	private ArrayList<Channel> channelList = new ArrayList<Channel>();
	private int slots;
	SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");

	public Task(ArrayList<Channel> channelList, int slots) {
		this.channelList = channelList;
		this.slots = slots;
	}

	@Override
	public void run() {
		Date date = new Date();
		String now = sdf.format(date);
		Iterator channelIt = channelList.iterator();
		Channel ch = null;
		int usersOnline = 0;

		while (channelIt.hasNext()) {
			ch = (Channel) channelIt.next();
			Set keys = ch.getAllUsers().keySet();
			Iterator userIt = keys.iterator();
			while (userIt.hasNext()) {
				usersOnline++;
			}
		}
		System.out.println("** " + usersOnline + " online " +
				slots + " connection slots are in use @ " + now);

		while (channelIt.hasNext()) {
			ch = (Channel) channelIt.next();
			System.out.println(ch.listUsersInChannel());
		}
	}
}
