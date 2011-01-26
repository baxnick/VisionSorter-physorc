package orchestration;

import java.io.IOException;
import java.util.Date;

import lcm.lcm.*;
import lcmtypes.cube_t;
import lejos.geom.Point;
import lejos.pc.comm.NXTInfo;
import physical.SimpleCallback;
import physical.GripperBot;

public class Avatar implements Runnable {
	private LordSupreme parent;
	private TaskOverlord overlord;
	private GripperBot bot;
	private Task task;
	private String name;
	private Thread myThread;
	private Thread collisionThread;
	private CubeSubscriber cubeSubscriber = null;
	private long lastVision = 0;
	private final int acceptableReckoningTime = 10 * 1000;
	private final int updateFreq = 5 * 1000;
	
	private Point visionZone = new Point(600, 500);
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

	public boolean needsVision1()
	{
		return lastVision < System.currentTimeMillis() - acceptableReckoningTime;
	}
	
	@Override
	public void run() {
		bot.recalibrate();
		
		cubeSubscriber = new CubeSubscriber();
		this.parent.lcm.subscribe("CUBE", cubeSubscriber);
		
		while (needsVision1())
		{
			Thread.yield();
		}
		
		isActive = true;
		collisionThread = new Thread(new CollisionWatch());
		collisionThread.start();
		
		while (connectionUp)
		{
			task = overlord.requestDuty(this);
			task.assignBot(bot);
			
			System.out.println(getName() + " is taking task: " + task.toString());
			task.fulfil();
		}
		
		isActive = false;
		parent.removeFromDuty(this);
		bot.finished();
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
			return Avatar.this.needsVision1();
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
	public class CubeSubscriber implements LCMSubscriber
	{
	   public void messageReceived(LCM lcm, String channel, LCMDataInputStream ins)
	   {
		   try
		{
			cube_t cube = new cube_t(ins);
			
			if (!getName().equals(cube.id)) return;
			
			long now = System.currentTimeMillis();
			if (lastVision < now - updateFreq && 
					!Avatar.this.bot.getNav().isMoving())
			{
				Avatar.this.bot.getNav().setPose(
						(float)cube.position[0], 
						(float)cube.position[1],
						(float)cube.orientation);
				

				System.out.println(getName() + " @ " + 
						cube.position[0] + ", " + cube.position[1] + ": " + 
						cube.orientation);
				lastVision = now;
				
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
		private float KINDA_CLOSE = 400.0f;
		
		public CollisionWatch()
		{
			REALLY_CLOSE = bot.safeDistance(0) * 1.2f;
			KINDA_CLOSE = bot.safeDistance(0) * 2.0f;
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
					
					if (otherBot.location().distance(myLoc) < KINDA_CLOSE)
					{
						if (task != null) task.halt();
						while (!task.isHalted()) Thread.yield();
						try
						{
							bot.getNav().stop();
							
							do
							{
								Point otherLoc = otherBot.location();
								if (otherLoc.distance(myLoc) < REALLY_CLOSE)
								{
									bot.getNav().travel(-REALLY_CLOSE);
								}
								bot.getNav().rotateTo(bot.getNav().angleTo(otherLoc.x, otherLoc.y) + 90.0f);
								Thread.sleep(500);
							}
							while (otherBot.location().distance(myLoc) < KINDA_CLOSE);
						}
						catch (InterruptedException e)
						{
							return;
						}
						finally
						{
							if (task != null) task.resume();
						}
					}
				}
				
				Thread.yield();
			}
		}
		
		private class FreezeBehaviour implements Runnable
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				
			}
			
		}
		
		private class RunBehaviour implements Runnable
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				
			}
		}
	}
}
