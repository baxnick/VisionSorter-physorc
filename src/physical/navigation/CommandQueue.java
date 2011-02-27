package physical.navigation;

import java.util.PriorityQueue;
import java.util.Vector;

import physical.navigation.commands.Command;

public class CommandQueue<T extends Command>
{
	private PriorityQueue<T> queue = new PriorityQueue<T>();
	
	public synchronized T nextCommand()
	{
		return queue.poll();
	}
	
	public synchronized void enqueue(T newItem)
	{
		if (newItem.isUnique())
		{
			Vector<T> existing = new Vector<T>();
			for (T existingItem : queue)
			{
				if (existingItem.getClass() == newItem.getClass()) 
				{
					existing.add(existingItem);
				}
			}
			
			queue.removeAll(existing);
		}
		
		queue.add(newItem);
	}
}
