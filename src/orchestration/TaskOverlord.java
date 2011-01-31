package orchestration;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import orchestration.goal.Goal;
import orchestration.object.Ball;
import orchestration.object.BallColor;

import lcm.lcm.LCM;
import lcm.lcm.LCMDataInputStream;
import lcm.lcm.LCMSubscriber;
import lcmtypes.balls_t;
import lcmtypes.ball_t;
import lejos.geom.Point;

public class TaskOverlord {
	private ReadWriteLock rwLock = new ReentrantReadWriteLock();
	private Lock readLock = rwLock.readLock();
	private Lock writeLock = rwLock.writeLock();
	private Lock taskLock = new ReentrantLock();
	private List<Task> tasks = new Vector<Task>();
	private List<Ball> freeBalls = new Vector<Ball>();
	private List<Goal> goals = new Vector<Goal>();
	
	private LordSupreme parent;
	
	public TaskOverlord(LordSupreme parent)
	{
		this.parent = parent;
		
		parent.lcm.subscribe("BALL", new BallSubscriber());
	}
	
	public Task requestDuty(Avatar soldier)
	{
		System.out.println(soldier.getName() + " is waiting on a task.");
		
		while (freeBalls.isEmpty())
		{
			try
			{
				Thread.sleep(500);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
				return null;
			}
		}
		taskLock.lock();		
		System.out.print(tasks.size() + " active tasks. ");
		System.out.println(freeBalls.size() + " free balls. ");
		
		

		Point avatarLoc = soldier.location();
		
		Ball nearestBall = findNearestBall(avatarLoc);
		Goal nearestGoal = findBestGoal(avatarLoc, nearestBall);
		
		
		Task newTask = new Task(this, parent.planner, soldier, nearestBall, nearestGoal);
		takeBall(nearestBall);
		tasks.add(newTask);
		taskLock.unlock();
		
		return newTask;
	}
	
	public void takeBall(Ball ball)
	{
		writeLock.lock();
			freeBalls.remove(ball);
		writeLock.unlock();
	}
	
	public Ball findNearestBall(Point avatarLoc)
	{
		readLock.lock();
			if (freeBalls.size() == 0) return null; // DANGER
			
			Ball nearestBall = freeBalls.get(0);
			for (Ball ball : freeBalls)
			{
				if (
						ball.getLocation().distance(avatarLoc) < 
						nearestBall.getLocation().distance(avatarLoc))
				{
					nearestBall = ball;
				}
			}
		readLock.unlock();
		
		return nearestBall;
	}
	
	public Goal findBestGoal(Point avatarLoc, Ball ball)
	{
		readLock.lock();
			Point nearestBallLoc = ball.getLocation();
			List<Goal> suitableGoals = new Vector<Goal>();
			
			while (suitableGoals.size() == 0)
			{
				for (Goal goal : goals)
				{
					if (goal.accepts(ball)) suitableGoals.add(goal);
				}
				
				if (suitableGoals.size() == 0) Thread.yield();
			}
			
			if (suitableGoals.size() == 0) return null; // DANGER
			
			Goal nearestGoal = suitableGoals.get(0);
			
			for (Goal goal : suitableGoals)
			{
				if (
						goal.dropPoint(avatarLoc).distance(nearestBallLoc) < 
						nearestGoal.dropPoint(avatarLoc).distance(nearestBallLoc))
				{
					nearestGoal = goal;
				}
			}
		readLock.unlock();
		
		return nearestGoal;
	}
	
	public void announceGoal(Goal goal)
	{	writeLock.lock();
			goals.add(goal);
		writeLock.unlock();
	}
	
	public Goal getGoal(String id)
	{
		readLock.lock();
			for(Goal goal : goals)
			{
				if (goal.id().equals(id))
				{
					readLock.unlock();
					return goal;
				}
			}
		
		readLock.unlock();
		return null;
	}
	
	public void ballsUpdate(List<Ball> detectedBalls)
	{
		
		// Any ball detected and not active is a safe candidate for future tasks
		List<Ball> detectedAndFree = new Vector<Ball>();
		List<Ball> activeBalls = new Vector<Ball>();
		
		taskLock.lock();
		readLock.lock();
		for (Task task : tasks)
			activeBalls.add(task.getBall());
		
		for (Ball incoming : detectedBalls)
		{
			Point incomingLoc = incoming.getLocation();
			if (getBall(activeBalls, incomingLoc.x, incomingLoc.y) == null)
			{
				detectedAndFree.add(incoming);
			}
		}
		readLock.unlock();

		writeLock.lock();
		freeBalls.clear();
		freeBalls.addAll(detectedAndFree);
		writeLock.unlock();
		
		// Any ball not detected but active (without being gripped) is problematic. 
		// It's possible it was just obscured by the robot picking it up though, so
		// give it a very generous time limit to get it's act together
		
		List<Task> expiredTasks = new Vector<Task>();
		
		readLock.lock();
		for (Task task : tasks)
		{
			if (task.hasBall()) continue;
			
			Ball taskBall = task.getBall();
			
			boolean foundMatch = false;
			for (Ball incoming : detectedBalls)
			{
				if (areLocationsClose(taskBall.getLocation(), incoming.getLocation()))
				{
					foundMatch = true;
					task.unExpire();
					break;
				}
			}
			
			if (!foundMatch) expiredTasks.add(task);
		}
		readLock.unlock();
		
		// Need to process expirations from a separate collection, as the
		// invocation can cause it's removal from the task list..
		for(Task task : expiredTasks)
		{
			task.attemptExpire();
		}
		taskLock.unlock();
	}
	
	private static final float threshold = 10f;
	
	private boolean areLocationsClose(Point loc1, Point loc2)
	{
		double x_diff = Math.abs(loc1.x - loc2.x);
		double y_diff = Math.abs(loc1.y - loc2.y);
		
		if (Math.sqrt(x_diff * x_diff + y_diff * y_diff) <= threshold)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public Ball getBall(List<Ball> balls, float x, float y)
	{
		readLock.lock();
			Point loc = new Point(x, y);
			for (Ball ball : balls)
			{
				if (areLocationsClose(ball.getLocation(), loc))
				{
					readLock.unlock();
					return ball;
				}
			}
		readLock.unlock();
		
		return null;
	}

	public void abortTask(Task task)
	{
		writeLock.lock();
			if (tasks.contains(task))
			{
				tasks.remove(task);
			}
			
			System.out.println("Aborted task!: " + task);
		writeLock.unlock();
	}

	public void completeTask(Task task)
	{
		writeLock.lock();
			if (tasks.contains(task))
			{
				tasks.remove(task);
			}
			
		System.out.println("Completed task!: " + task);
		writeLock.unlock();
	}
	
	// Arranges balls so that oldest is first
	// Utterly useless at the moment now that I think of it....
	private class BallOrderingComparator implements Comparator<ball_t>
	{
		@Override
		public int compare(ball_t b1, ball_t b2)
		{
			if (b1.age < b2.age) return 1;
			else if (b1.age == b2.age) return 0;
			else return -1;
		}
		
	}
	
	public static final long updateRate = 1 * 1000;
	private long lastUpdate = 0;
	
	String firstSource = null;
	private class BallSubscriber implements LCMSubscriber
	{
		private Lock messageLock = new ReentrantLock();
		
	   public void messageReceived(LCM lcm, String channel, LCMDataInputStream ins)
	   {
		   balls_t detected = null;
		   try
		   {
			   detected = new balls_t(ins);
		   }
		   catch(IOException e)
		   {
			   e.printStackTrace();
			   return;
		   }

		   // Just writing code to handle one source for now. 
		   // I'll make it multi source compliant later.
		   if (firstSource == null)
			   {
			   firstSource = detected.info.source;
			   System.out.println("Using " + firstSource + " as sole ball source.");
			   }
		   else if (!detected.info.source.equals(firstSource))
		   {
			   return;
		   }
		   
		   // Only update every [updateRate] seconds
		   long now = System.currentTimeMillis();
		   if (now - lastUpdate > updateRate)
			   {
			   boolean locked = messageLock.tryLock();
			   if (!locked) return;
			   
			   lastUpdate = now;
			   
			   List<Ball> javinatedBalls = new Vector<Ball>();
			   for (ball_t ball : detected.balls)
			   {
				   Ball javaBall = new Ball(
						   new Point((float)ball.position[0], (float)ball.position[1]),
						   BallColor.values()[ball.colour]);
				   javinatedBalls.add(javaBall);
			   }
			   
			   System.out.println(javinatedBalls.size() + " balls detected.");
			   ballsUpdate(javinatedBalls);
			   messageLock.unlock();
		   }
	   }
	}
}
