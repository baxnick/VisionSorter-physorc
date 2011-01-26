package orchestration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lcm.lcm.LCM;

public class LordSupreme {
	public List<Avatar> avatars = Collections.synchronizedList(new ArrayList<Avatar>(4));
	public TaskOverlord overlord;
	public LCM lcm;
	private HotBotWatch watcher;
	
	public LordSupreme()
	{
		lcm = LCM.getSingleton();  
		overlord = new TaskOverlord(this);
		watcher = new HotBotWatch(this);
	}
	
	public void start()
	{
		new Thread(watcher).start();
	}
	
	public TaskOverlord getOverlord()
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
		for(Avatar avatar : avatars)
		{
			if (avatar.getName().equals(name))
				return true;
		}
		
		return false;
	}
	
	public int priority (Avatar avatar)
	{
		return avatars.indexOf(avatar);
	}
}
