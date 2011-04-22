package physical.navigation;

import java.util.PriorityQueue;
import java.util.Vector;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;

import physical.navigation.commands.Command;

public class CommandQueue<T extends Command>
{
	private PriorityQueue<T> queue = new PriorityQueue<T>();
	private ReentrantLock lock = new ReentrantLock();
	
	public synchronized T nextCommand()
	{
		return queue.poll();
	}
	
	public synchronized void enqueue(T newItem)
	{
		lock.lock();
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
		lock.unlock();
	}
	
	public synchronized void clear()
	{
		lock.lock();
		queue.clear();
		lock.unlock();
	}
}
