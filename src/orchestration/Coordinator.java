package orchestration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import orchestration.errand.ErrandOverlord;
import orchestration.path.BraindeadPlanner;
import orchestration.path.PathPlanner;

import lcm.lcm.LCM;

/**
 * The LordSupreme serves as the central co-ordination point of the various major components that make up the system.
 * 
 * The major components being: orchestration.Avatar orchesration.HotBotWatch orchestration.task.TaskOverlord
 * orchestration.path.PathPlanner
 * 
 * And in a more general capacity: lcm.lcm.LCM
 * 
 * @author baxnick
 * 
 */
public class Coordinator
{
	public List<Avatar> avatars = Collections.synchronizedList(new ArrayList<Avatar>(4));
	public PathPlanner planner;
	public ErrandOverlord overlord;
	public LCM lcm;
	private LiveBotFinder watcher;

	public Coordinator()
	{
		lcm = LCM.getSingleton();
		planner = new BraindeadPlanner(this);
		overlord = new ErrandOverlord(this);
		watcher = new LiveBotFinder(this);
	}

	public void start()
	{
		new Thread(watcher).start();
	}

	public ErrandOverlord getOverlord()
	{
		return overlord;
	}

	public synchronized void reportForDuty(Avatar avatar)
	{
		avatars.add(avatar);
	}

	public synchronized void removeFromDuty(Avatar avatar)
	{
		avatars.remove(avatar);
	}

	public synchronized boolean isActive(String name)
	{
		for (Avatar avatar : avatars)
		{
			if (avatar.getName().equals(name)) return true;
		}

		return false;
	}

	public int priority(Avatar avatar)
	{
		return avatars.indexOf(avatar);
	}
}
