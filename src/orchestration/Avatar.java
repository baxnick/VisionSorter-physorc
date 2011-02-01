package orchestration;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import orchestration.path.Plannable;
import orchestration.path.PlannerShape;
import orchestration.path.RectShape;

import lcm.lcm.*;
import lcmtypes.cube_t;
import lejos.geom.Point;
import lejos.pc.comm.NXTInfo;
import physical.SimpleCallback;
import physical.GripperBot;
import physical.VisionQuery;

public class Avatar implements Runnable, Plannable {
	private LordSupreme parent;
	private TaskOverlord overlord;
	private GripperBot bot;
	private Task task;
	private String name;
	private Thread myThread;
	private Thread collisionThread;
	private CubeSubscriber cubeSubscriber = null;
	private long lastVision = 0;
	private final int acceptableReckoningTime = 20 * 1000;
	private final int updateFreq = 1 * 1000;
	
	private Point visionZone = new Point(500, 400);
	private boolean isActive = false;
	private boolean connectionUp = true;
	
	public Avatar(LordSupreme parent, GripperBot bot)
	{
		parent.reportForDuty(this);
		
		this.parent = parent;
		this.overlord = parent.getOverlord();
		this.bot = bot;
		bot.setVisionQuery(new BotVisionSource());
		bot.setErrorHandler(new OnConnectionError());
		this.name = bot.getConfig().getName();

		myThread = new Thread(this);
		myThread.start();
	}

	public boolean needsVision()
	{
		return lastVision < System.currentTimeMillis() - acceptableReckoningTime;
	}
	
	@Override
	public void run() {
		bot.recalibrate();
		
		cubeSubscriber = new CubeSubscriber();
		this.parent.lcm.subscribe("CUBE", cubeSubscriber);
		
		while (needsVision())
		{
			Thread.yield();
		}
		
		isActive = true;
		collisionThread = new Thread(new CollisionWatch());
		//collisionThread.start();
		
		while (connectionUp)
		{
			overlord.requestDuty(this);
			
			while (task == null) Thread.yield();
			task.assignBot(bot);
			
			System.out.println(getName() + " is taking task: " + task.toString());
			task.fulfil();
			task = null;
		}
		
		isActive = false;
		parent.removeFromDuty(this);
		bot.finished();
	}
	
	public void assignTask(Task assignment)
	{
		this.task = assignment;
	}
	
	public String getName()
	{
		return name;
	}
	
	private void dropConn()
	{
		connectionUp = false;
		if (cubeSubscriber != null) parent.lcm.unsubscribe("CUBE", cubeSubscriber);
		myThread.interrupt();
		collisionThread.interrupt();
		if (task != null) task.abort();
		parent.removeFromDuty(Avatar.this);
	}
	
	private class BotVisionSource implements VisionQuery
	{

		@Override
		public boolean needsVision()
		{
			return Avatar.this.needsVision();
		}

		@Override
		public Point visionPoint()
		{
			return visionZone;
		}
		
	}
	
	private class OnConnectionError implements SimpleCallback
	{
		@Override
		public void callback()
		{
			if (connectionUp == false) return;
			// TODO Auto-generated method stub
			System.err.println("Connection dropped, removing " + getName());
			dropConn();
		}
		
	}

	public Point location()
	{
		return bot.location();
	}
	
	public boolean isActive()
	{
		return isActive;
	}
	
	private class CubeSubscriber implements LCMSubscriber
	{
		private Lock messageLock = new ReentrantLock();
	   public void messageReceived(LCM lcm, String channel, LCMDataInputStream ins)
	   {
		   try
		{
			cube_t cube = new cube_t(ins);
			
			if (!getName().equals(cube.id)) return;
			
			long now = System.currentTimeMillis();
			if (now - lastVision > updateFreq && 
					!Avatar.this.bot.getNav().isMoving())
			{
				boolean locked = messageLock.tryLock();
				if (!locked) return;

				lastVision = now;
				
				System.out.println(getName() + " @ " + 
						cube.position[0] + ", " + cube.position[1] + ": " + 
						cube.orientation);
				
				Avatar.this.bot.getNav().setPose(
						(float)cube.position[0], 
						(float)cube.position[1],
						(float)cube.orientation);
				

				
				messageLock.unlock();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			dropConn();
		}
	   }
	}
	
	private class CollisionWatch implements Runnable
	{
		private float REALLY_CLOSE = 200.0f;
		
		public CollisionWatch()
		{
			REALLY_CLOSE = bot.safeDistance(0) * 1.2f;
		}
		
		@Override
		public void run()
		{
			while (connectionUp)
			{
				Point myLoc = location();
				
				for (Avatar otherBot : parent.avatars)
				{
					if (otherBot == Avatar.this) continue;
					if (otherBot.isActive() == false) continue;
					if (parent.priority(otherBot) < parent.priority(Avatar.this)) continue;
					
					if (otherBot.location().distance(myLoc) < REALLY_CLOSE)
					{
						if (task != null) task.halt();
						while (!task.isHalted()) Thread.yield();
						bot.getNav().stop();
						
						while (otherBot.location().distance(myLoc) < REALLY_CLOSE)
						{
							Thread.yield();
						}

						if (task != null) task.resume();
					}
				}
				
				Thread.yield();
			}
		}
	}

	@Override
	public String getPlanningName()
	{
		return getName();
	}

	@Override
	public PlannerShape getPlannerShape()
	{
		Point location = bot.location();
		double heading = bot.heading();
		
		return RectShape.easy(
				bot.getConfig().trackWidth, 
				bot.getConfig().gripDisplacement + bot.getConfig().rearDisplacement, 
				location, heading);
	}
}
